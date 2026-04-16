package com.aistudio.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户导入结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserImportResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 用户数据
     */
    private UserImportData data;

    /**
     * 失败原因（成功时为空）
     */
    private String error;

    /**
     * 用户导入数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserImportData {
        private String username;
        private String realName;
        private String department;
        private String team;
        private String email;
    }
}
