package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleFolderRequest {

    @NotBlank(message = "文件夹名称不能为空")
    @Size(max = 100, message = "文件夹名称最多100字")
    @JsonAlias({"folderName", "folder_name"})
    private String folderName;

    @JsonAlias({"parentId", "parent_id"})
    private Long parentId;
}
