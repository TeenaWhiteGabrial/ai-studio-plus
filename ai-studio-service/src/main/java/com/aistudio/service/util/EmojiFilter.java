package com.aistudio.service.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;

import java.io.IOException;

/**
 * Emoji字符过滤工具
 * 将无法存储到MySQL utf8字符集的4字节字符替换为空字符
 */
public class EmojiFilter {

    /**
     * 过滤字符串中的emoji等4字节字符
     */
    public static String filter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (isEmoji(c)) {
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static boolean isEmoji(char c) {
        return Character.isSurrogate(c);
    }

    /**
     * Jackson序列化器，用于在序列化时自动过滤emoji
     */
    public static class EmojiSerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value != null) {
                gen.writeString(filter(value));
            }
        }
    }
}
