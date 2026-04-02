package com.aistudio.service.gitlab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.net.URI;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitLabClientImpl implements GitLabClient {

    private final GitLabProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean testConnection() {
        try {
            String url = buildApiUrl("/projects/" + encodePath(properties.getRepository()));
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(buildHeaders()),
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("GitLab 连接测试失败", e);
            return false;
        }
    }

    @Override
    public List<GitLabTreeItem> getRepositoryTree(String path, boolean recursive) {
        List<GitLabTreeItem> allItems = new ArrayList<>();
        int page = 1;
        int perPage = 100;

        try {
            while (true) {
                String baseUrl = buildApiUrl("/projects/" + properties.getRepository() + "/repository/tree");
                StringBuilder urlBuilder = new StringBuilder(baseUrl);
                urlBuilder.append("?ref=").append(properties.getBranch());
                if (path != null && !path.isEmpty()) {
                    urlBuilder.append("&path=").append(UriUtils.encodeQueryParam(path, StandardCharsets.UTF_8));
                }
                if (recursive) {
                    urlBuilder.append("&recursive=true");
                }
                urlBuilder.append("&per_page=").append(perPage);
                urlBuilder.append("&page=").append(page);

                ResponseEntity<String> response = restTemplate.exchange(
                        urlBuilder.toString(),
                        HttpMethod.GET,
                        new HttpEntity<>(buildHeaders()),
                        String.class
                );

                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                    break;
                }

                JsonNode root = objectMapper.readTree(response.getBody());
                if (!root.isArray() || root.size() == 0) {
                    break;
                }

                for (JsonNode node : root) {
                    GitLabTreeItem item = new GitLabTreeItem();
                    item.setId(node.get("id").asText());
                    item.setName(node.get("name").asText());
                    item.setPath(node.get("path").asText());
                    item.setType(node.get("type").asText());
                    item.setMode(node.has("mode") ? node.get("mode").asText() : null);
                    allItems.add(item);
                }

                // 如果返回数量小于 perPage，说明已是最后一页
                if (root.size() < perPage) {
                    break;
                }
                page++;
            }
        } catch (Exception e) {
            log.error("获取 GitLab 仓库文件树失败", e);
            return Collections.emptyList();
        }

        return allItems;
    }

    @Override
    public GitLabFile getFile(String filePath) {
        try {
            // 使用 URI.create 避免 RestTemplate 对已编码的 %2F 进行二次编码
            String encodedFilePath = UriUtils.encodePath(filePath, StandardCharsets.UTF_8).replace("/", "%2F");
            String urlStr = buildApiUrl("/projects/" + properties.getRepository()
                    + "/repository/files/" + encodedFilePath + "?ref=" + properties.getBranch());
            URI uri = URI.create(urlStr);

            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(buildHeaders()),
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return null;
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            GitLabFile file = new GitLabFile();
            file.setFilePath(root.get("file_path").asText());
            file.setContent(root.get("content").asText());
            file.setCommitId(root.has("commit_id") ? root.get("commit_id").asText() : null);
            file.setLastCommitId(root.has("last_commit_id") ? root.get("last_commit_id").asText() : null);
            file.setSize(root.has("size") ? root.get("size").asLong() : 0);

            // Base64 解码内容
            String base64Content = root.get("content").asText();
            byte[] decoded = Base64.getDecoder().decode(base64Content.replace("\n", ""));
            file.setContentPlain(new String(decoded, StandardCharsets.UTF_8));

            return file;
        } catch (Exception e) {
            log.error("获取 GitLab 文件失败: {}", filePath, e);
            return null;
        }
    }

    @Override
    public String getRawContent(String filePath) {
        try {
            String encodedFilePath = UriUtils.encodePath(filePath, StandardCharsets.UTF_8).replace("/", "%2F");
            String urlStr = buildApiUrl("/projects/" + properties.getRepository()
                    + "/repository/files/" + encodedFilePath + "/raw?ref=" + properties.getBranch());
            URI uri = URI.create(urlStr);

            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(buildHeaders()),
                    String.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            return null;
        } catch (Exception e) {
            log.error("获取 GitLab Raw 内容失败: {}", filePath, e);
            return null;
        }
    }

    @Override
    public byte[] getRawContentBytes(String filePath) {
        try {
            String encodedFilePath = UriUtils.encodePath(filePath, StandardCharsets.UTF_8).replace("/", "%2F");
            String urlStr = buildApiUrl("/projects/" + properties.getRepository()
                    + "/repository/files/" + encodedFilePath + "/raw?ref=" + properties.getBranch());
            URI uri = URI.create(urlStr);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(buildHeaders()),
                    byte[].class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            return null;
        } catch (Exception e) {
            log.error("获取 GitLab Raw 内容(二进制)失败: {}", filePath, e);
            return null;
        }
    }

    @Override
    public GitLabRepository getRepository() {
        try {
            String url = buildApiUrl("/projects/" + encodePath(properties.getRepository()));
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(buildHeaders()),
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return null;
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            GitLabRepository repo = new GitLabRepository();
            repo.setId(root.get("id").asLong());
            repo.setName(root.get("name").asText());
            repo.setPath(root.get("path").asText());
            repo.setWebUrl(root.get("web_url").asText());
            repo.setDefaultBranch(root.get("default_branch").asText());
            repo.setPrivate(root.get("visibility").asText().equals("private"));
            return repo;
        } catch (Exception e) {
            log.error("获取 GitLab 仓库信息失败", e);
            return null;
        }
    }

    private String buildApiUrl(String path) {
        String baseUrl = properties.getUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/api/v4" + path;
    }

    private String encodePath(String path) {
        return UriUtils.encodePath(path, StandardCharsets.UTF_8).replace("/", "%2F");
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("PRIVATE-TOKEN", properties.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
