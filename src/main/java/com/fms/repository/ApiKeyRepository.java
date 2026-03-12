package com.fms.repository;

import com.fms.model.ApiKeyEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApiKeyRepository extends CrudRepository<ApiKeyEntity, Long> {

    @Query(value = "SELECT * FROM api_key WHERE key_value = ?1 AND active = TRUE LIMIT 1", nativeQuery = true)
    Optional<ApiKeyEntity> findActiveByKeyValue(String keyValue);

    @Query(value = "SELECT * FROM api_key WHERE tenant = ?1 AND active = TRUE", nativeQuery = true)
    java.util.List<ApiKeyEntity> findActiveByTenant(String tenant);
}

