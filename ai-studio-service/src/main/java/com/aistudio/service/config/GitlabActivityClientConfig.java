package com.aistudio.service.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GitlabActivityClientConfig {

    @Bean
    public RestTemplate gitlabActivityRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public RestTemplate aiModelRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
