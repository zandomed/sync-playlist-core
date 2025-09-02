package com.zandome.syncplaylist.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client")
public class OAuth2Config {

    private Map<String, Registration> registration;
    private Map<String, Provider> provider;

    @Data
    public static class Registration {
        private String clientId;
        private String clientSecret;
        private String scope;
        private String redirectUri;
        private String authorizationGrantType;
        private String clientName;
        private String provider;
    }

    @Data
    public static class Provider {
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String userNameAttribute;
        private String jwkSetUri;
    }
}