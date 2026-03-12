package com.fms.service;

import com.fms.config.AppConfig;
import com.fms.exception.ApiKeyUnauthorizedException;
import com.fms.model.ApiKeyEntity;
import com.fms.repository.ApiKeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages API key creation, validation, and revocation per tenant.
 */
@Slf4j
@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final AppConfig appConfig;

    @Autowired
    public ApiKeyService(ApiKeyRepository apiKeyRepository, AppConfig appConfig) {
        this.apiKeyRepository = apiKeyRepository;
        this.appConfig = appConfig;
    }

    /**
     * Validate the given key and return the associated tenant.
     * Throws ApiKeyUnauthorizedException if the key is invalid or inactive.
     */
    public String validateAndGetTenant(String apiKey) {
        if (!appConfig.isApiKeyVerification()) {
            return null;
        }

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ApiKeyUnauthorizedException();
        }

        Optional<ApiKeyEntity> entity = apiKeyRepository.findActiveByKeyValue(apiKey.trim());
        if (!entity.isPresent()) {
            throw new ApiKeyUnauthorizedException();
        }

        return entity.get().getTenant();
    }

    /**
     * Generate and persist a new API key for the given tenant.
     * Returns the generated key value (shown only once).
     */
    public String generateKey(String tenant) {
        String keyValue = "fms-" + UUID.randomUUID().toString().replace("-", "");
        ApiKeyEntity entity = new ApiKeyEntity(keyValue, tenant);
        apiKeyRepository.save(entity);
        log.info("Generated new API key for tenant '{}'", tenant);
        return keyValue;
    }

    /**
     * Revoke (deactivate) all active keys for the given tenant.
     */
    public void revokeAllKeys(String tenant) {
        List<ApiKeyEntity> keys = apiKeyRepository.findActiveByTenant(tenant);
        for (ApiKeyEntity key : keys) {
            key.setActive(false);
            apiKeyRepository.save(key);
        }
        log.info("Revoked {} API key(s) for tenant '{}'", keys.size(), tenant);
    }

    /**
     * List all active keys for a tenant.
     */
    public List<ApiKeyEntity> listActiveKeys(String tenant) {
        return apiKeyRepository.findActiveByTenant(tenant);
    }
}
