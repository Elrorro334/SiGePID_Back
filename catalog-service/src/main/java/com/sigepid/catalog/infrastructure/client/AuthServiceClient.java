package com.sigepid.catalog.infrastructure.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    public AuthServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserPreferencesResponse getUserPreferences(Long userId) {
        return restTemplate.getForObject(
                "http://auth-service/api/auth/users/{userId}/preferences",
                UserPreferencesResponse.class,
                userId
        );
    }
}
