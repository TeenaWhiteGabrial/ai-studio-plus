package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GitlabWebhookRequest {

    @JsonAlias("object_kind")
    private String object_kind;

    @JsonAlias("event_name")
    private String event_name;

    private Map<String, Object> project;
    private Map<String, Object> user;

    @JsonAlias("user_name")
    private String user_name;

    @JsonAlias("user_email")
    private String user_email;

    private String ref;

    @JsonAlias("checkout_sha")
    private String checkout_sha;

    private List<Map<String, Object>> commits;

    @JsonAlias("total_commits_count")
    private Integer total_commits_count;

    @JsonAlias("object_attributes")
    private Map<String, Object> object_attributes;
}
