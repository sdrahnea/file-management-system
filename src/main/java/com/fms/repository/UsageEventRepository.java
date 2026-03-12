package com.fms.repository;

import com.fms.model.UsageEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsageEventRepository extends CrudRepository<UsageEvent, Long> {

    @Query(value = "SELECT * FROM usage_event WHERE tenant = ?1", nativeQuery = true)
    List<UsageEvent> findByTenant(String tenant);

    @Query(value = "SELECT * FROM usage_event WHERE tenant = ?1 AND event_type = 'UPLOAD'", nativeQuery = true)
    List<UsageEvent> findUploadsByTenant(String tenant);

    @Query(value = "SELECT COALESCE(SUM(file_size_bytes), 0) FROM usage_event WHERE tenant = ?1 AND event_type = 'UPLOAD'", nativeQuery = true)
    Long sumStorageByTenant(String tenant);

    @Query(value = "SELECT COUNT(*) FROM usage_event WHERE tenant = ?1 AND event_type = 'UPLOAD' AND event_date >= ?2", nativeQuery = true)
    Long countUploadsSince(String tenant, java.util.Date since);
}

