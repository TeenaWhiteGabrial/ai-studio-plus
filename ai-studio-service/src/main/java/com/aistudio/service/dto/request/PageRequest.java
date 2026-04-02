package com.aistudio.service.dto.request;

import lombok.Data;

/**
 * 通用分页请求
 */
@Data
public class PageRequest {

    private Integer page = 1;
    private Integer size = 10;

    public long getOffset() {
        return (long) (page - 1) * size;
    }
}
