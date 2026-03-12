package com.fms.controller;

import com.fms.model.ApiKeyEntity;
import com.fms.service.ApiKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/apikey")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @Autowired
    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping("/{tenant}")
    public Map<String, String> generate(@PathVariable String tenant) {
        String key = apiKeyService.generateKey(tenant);
        Map<String, String> response = new HashMap<String, String>();
        response.put("tenant", tenant);
        response.put("apiKey", key);
        return response;
    }

    @GetMapping("/{tenant}")
    public List<ApiKeyEntity> listActive(@PathVariable String tenant) {
        return apiKeyService.listActiveKeys(tenant);
    }

    @DeleteMapping("/{tenant}")
    public Map<String, String> revokeAll(@PathVariable String tenant) {
        apiKeyService.revokeAllKeys(tenant);
        Map<String, String> response = new HashMap<String, String>();
        response.put("tenant", tenant);
        response.put("status", "revoked");
        return response;
    }
}

