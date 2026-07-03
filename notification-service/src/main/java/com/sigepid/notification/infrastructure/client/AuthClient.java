package com.sigepid.notification.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "auth-service", path = "/api/auth")
public interface AuthClient {

    @GetMapping("/profile")
    Map<String, Object> getProfile(@RequestHeader("X-User-Id") String userId);
}
