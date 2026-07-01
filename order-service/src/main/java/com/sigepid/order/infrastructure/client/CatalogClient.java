package com.sigepid.order.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "catalog-service")
public interface CatalogClient {

    @PutMapping("/api/catalog/products/reduce-stock")
    List<Map<String, Object>> reduceStock(@RequestBody List<Map<String, Object>> stockRequests);

    @PutMapping("/api/catalog/products/restore-stock")
    List<Map<String, Object>> restoreStock(@RequestBody List<Map<String, Object>> stockRequests);
}
