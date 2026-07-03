package com.sigepid.order.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "notification-service", path = "/internal/notifications")
public interface NotificationClient {

    @PostMapping
    void sendNotification(@RequestBody Map<String, Object> request);
}
