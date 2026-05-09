import crypto from 'node:crypto';
import fs from 'node:fs/promises';
import https from 'node:https';
import path from 'node:path';

const registry = 'docker.m.daocloud.io';
const repo = 'library/node';
const tag = process.argv[3] || '22-bookworm-slim';
const refName = `${registry}/${repo}:${tag}`;
const fixedIps = {
  'docker.m.daocloud.io': '139.224.10.160',
  'm.daocloud.io': '139.224.10.160',
  'image-mirror.r2.daocloud.vip': '104.21.24.221',
};
const outDir = process.argv[2] || '/private/tmp/node-22-bookworm-slim-oci';

const agent = new https.Agent({
  lookup(hostname, options, callback) {
    const fixedIp = fixedIps[hostname];
    if (!fixedIp) {
      callback(new Error(`no fixed IP for ${hostname}`));
      return;
    }
    if (typeof options === 'function') {
      options(null, fixedIp, 4);
      return;
    }
    if (options?.all) {
      callback(null, [{ address: fixedIp, family: 4 }]);
      return;
    }
    callback(null, fixedIp, 4);
  },
});

function requestBuffer(url, headers = {}, redirectCount = 0) {
  return new Promise((resolve, reject) => {
    const request = https.get(url, { agent, headers, timeout: 60_000 }, (response) => {
      if ([301, 302, 303, 307, 308].includes(response.statusCode) && response.headers.location) {
        response.resume();
        if (redirectCount > 5) {
          reject(new Error(`too many redirects: ${url}`));
          return;
        }
        resolve(requestBuffer(response.headers.location, headers, redirectCount + 1));
        return;
      }
      const chunks = [];
      response.on('data', (chunk) => chunks.push(chunk));
      response.on('end', () => {
        resolve({
          body: Buffer.concat(chunks),
          headers: response.headers,
          status: response.statusCode,
        });
      });
    });
    request.on('timeout', () => request.destroy(new Error(`timeout: ${url}`)));
    request.on('error', reject);
  });
}

async function getToken(wwwAuthenticate) {
  const realm = wwwAuthenticate.match(/realm="([^"]+)"/)?.[1];
  const service = wwwAuthenticate.match(/service="([^"]+)"/)?.[1];
  const scope = wwwAuthenticate.match(/scope="([^"]+)"/)?.[1];
  const url = `${realm}?service=${encodeURIComponent(service)}&scope=${encodeURIComponent(scope)}`;
  const response = await requestBuffer(url);
  if (response.status !== 200) {
    throw new Error(`token failed: ${response.status} ${response.body.toString()}`);
  }
  return JSON.parse(response.body.toString()).token;
}

let token;

async function getManifest(ref, accept) {
  const url = `https://${registry}/v2/${repo}/manifests/${ref}`;
  let response = await requestBuffer(url, { Accept: accept });
  if (response.status === 401) {
    token = await getToken(response.headers['www-authenticate']);
    response = await requestBuffer(url, { Accept: accept, Authorization: `Bearer ${token}` });
  }
  if (response.status < 200 || response.status >= 300) {
    throw new Error(`manifest failed: ${response.status} ${response.body.toString().slice(0, 500)}`);
  }
  return response;
}

async function getBlob(digest) {
  const url = `https://${registry}/v2/${repo}/blobs/${digest}`;
  const response = await requestBuffer(url, { Authorization: `Bearer ${token}` });
  if (response.status < 200 || response.status >= 300) {
    throw new Error(`blob failed ${digest}: ${response.status} ${response.body.toString().slice(0, 500)}`);
  }
  return response.body;
}

function sha256(buffer) {
  return `sha256:${crypto.createHash('sha256').update(buffer).digest('hex')}`;
}

async function writeBlob(digest, buffer) {
  const actual = sha256(buffer);
  if (actual !== digest) {
    throw new Error(`digest mismatch: expected ${digest}, got ${actual}`);
  }
  await fs.writeFile(path.join(outDir, 'blobs', 'sha256', digest.replace('sha256:', '')), buffer);
}

await fs.rm(outDir, { force: true, recursive: true });
await fs.mkdir(path.join(outDir, 'blobs', 'sha256'), { recursive: true });

const indexResponse = await getManifest(
  tag,
  [
    'application/vnd.oci.image.index.v1+json',
    'application/vnd.docker.distribution.manifest.list.v2+json',
    'application/vnd.oci.image.manifest.v1+json',
    'application/vnd.docker.distribution.manifest.v2+json',
  ].join(', '),
);
const index = JSON.parse(indexResponse.body.toString());
const descriptor = index.manifests
  ? index.manifests.find((item) => item.platform?.os === 'linux' && item.platform?.architecture === 'amd64')
  : {
      digest: indexResponse.headers['docker-content-digest'] || sha256(indexResponse.body),
      mediaType: index.mediaType,
      size: indexResponse.body.length,
    };

if (!descriptor) {
  throw new Error('linux/amd64 manifest not found');
}

const manifestResponse = index.manifests
  ? await getManifest(
      descriptor.digest,
      'application/vnd.oci.image.manifest.v1+json, application/vnd.docker.distribution.manifest.v2+json',
    )
  : indexResponse;
const manifestDigest = manifestResponse.headers['docker-content-digest'] || sha256(manifestResponse.body);
const manifest = JSON.parse(manifestResponse.body.toString());

await writeBlob(manifestDigest, manifestResponse.body);
await writeBlob(manifest.config.digest, await getBlob(manifest.config.digest));

let totalBytes = 0;
for (const layer of manifest.layers) {
  const buffer = await getBlob(layer.digest);
  await writeBlob(layer.digest, buffer);
  totalBytes += buffer.length;
  console.log(`layer ${layer.digest.slice(0, 20)} ${(buffer.length / 1024 / 1024).toFixed(1)}MB`);
}

await fs.writeFile(path.join(outDir, 'oci-layout'), JSON.stringify({ imageLayoutVersion: '1.0.0' }));
await fs.writeFile(
  path.join(outDir, 'index.json'),
  JSON.stringify(
    {
      manifests: [
        {
          annotations: { 'org.opencontainers.image.ref.name': refName },
          digest: manifestDigest,
          mediaType: manifest.mediaType,
          size: manifestResponse.body.length,
        },
      ],
      schemaVersion: 2,
    },
    null,
    2,
  ),
);

console.log(`OCI_DIR=${outDir}`);
console.log(`IMAGE=${refName}`);
console.log(`DOWNLOADED_MB=${(totalBytes / 1024 / 1024).toFixed(1)}`);
