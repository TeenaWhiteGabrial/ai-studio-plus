package com.aistudio.service.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * YAML Frontmatter 解析工具
 */
@Slf4j
public class YamlFrontmatterParser {

    private static final Pattern FRONTMATTER_PATTERN = Pattern.compile(
            "^---\\s*\\n(.*?)\\n---\\s*\\n(.*)$",
            Pattern.DOTALL
    );

    private final Yaml yaml = new Yaml();

    /**
     * 解析包含 frontmatter 的 markdown 内容
     *
     * @param content markdown 文件内容
     * @return 解析结果
     */
    public ParseResult parse(String content) {
        ParseResult result = new ParseResult();
        result.setRawContent(content);

        if (content == null || content.isEmpty()) {
            result.setFrontmatter(new HashMap<>());
            result.setBody("");
            return result;
        }

        Matcher matcher = FRONTMATTER_PATTERN.matcher(content.trim());
        if (matcher.find()) {
            String frontmatterYaml = matcher.group(1);
            String body = matcher.group(2);

            try {
                Map<String, Object> frontmatter = yaml.load(frontmatterYaml);
                if (frontmatter == null) {
                    frontmatter = new HashMap<>();
                }
                result.setFrontmatter(frontmatter);
                result.setBody(body);
            } catch (Exception e) {
                log.warn("YAML frontmatter 解析失败，将内容作为纯文本处理", e);
                result.setFrontmatter(new HashMap<>());
                result.setBody(content);
            }
        } else {
            // 没有 frontmatter
            result.setFrontmatter(new HashMap<>());
            result.setBody(content);
        }

        return result;
    }

    /**
     * 从 frontmatter 中获取字符串值
     */
    public static String getString(Map<String, Object> frontmatter, String key, String defaultValue) {
        if (frontmatter == null || !frontmatter.containsKey(key)) {
            return defaultValue;
        }
        Object value = frontmatter.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * 解析结果
     */
    @Data
    public static class ParseResult {
        private Map<String, Object> frontmatter;
        private String body;
        private String rawContent;
    }
}
