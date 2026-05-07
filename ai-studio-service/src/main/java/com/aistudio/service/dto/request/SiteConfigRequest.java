package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SiteConfigRequest {

    @NotBlank(message = "网站标题不能为空")
    @Size(max = 100, message = "网站标题不能超过100个字符")
    @JsonAlias("siteName")
    private String siteName;

    @Size(max = 500, message = "网站描述不能超过500个字符")
    @JsonAlias("siteDescription")
    private String siteDescription;

    @Size(max = 500, message = "Logo地址不能超过500个字符")
    @JsonAlias("logoUrl")
    private String logoUrl;

    @Size(max = 500, message = "图标地址不能超过500个字符")
    @JsonAlias("iconUrl")
    private String iconUrl;

    @Size(max = 500, message = "底部说明不能超过500个字符")
    @JsonAlias("footerText")
    private String footerText;

    @Size(max = 300, message = "版权信息不能超过300个字符")
    @JsonAlias("footerCopyright")
    private String footerCopyright;

    @Size(max = 200, message = "备案信息不能超过200个字符")
    @JsonAlias("footerRecord")
    private String footerRecord;

    @JsonAlias("footerLinks")
    private String footerLinks;

    @Size(max = 500, message = "联系方式不能超过500个字符")
    private String contacts;
}
