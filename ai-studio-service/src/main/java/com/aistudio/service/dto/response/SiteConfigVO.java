package com.aistudio.service.dto.response;

import lombok.Data;

@Data
public class SiteConfigVO {

    private String siteName;
    private String siteDescription;
    private String logoUrl;
    private String iconUrl;
    private String footerText;
    private String footerCopyright;
    private String footerRecord;
    private String footerLinks;
    private String contacts;
}
