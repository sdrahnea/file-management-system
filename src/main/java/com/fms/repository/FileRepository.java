package com.fms.repository;

import com.fms.model.FileEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface FileRepository extends CrudRepository<FileEntity, Long> {

    @Query(value = "SELECT * FROM file_entity WHERE file_id = ?1 LIMIT 1", nativeQuery = true)
    List<FileEntity> findByFileId(String fileId);

    @Query(value = "SELECT * FROM file_entity WHERE file_id = ?1 AND tenant = ?2 LIMIT 1", nativeQuery = true)
    List<FileEntity> findByFileIdAndTenant(String fileId, String tenant);

    @Query(value = "SELECT * FROM file_entity WHERE created_date <= ?1", nativeQuery = true)
    List<FileEntity> getFileForDeleting(Date cutoffDate);

    @Query(value = "SELECT * FROM file_entity WHERE tenant = ?1 AND created_date <= ?2", nativeQuery = true)
    List<FileEntity> findFilesOlderThan(String tenant, Date cutoffDate);

    @Query(value = "SELECT COALESCE(SUM(file_size_bytes), 0) FROM file_entity WHERE tenant = ?1", nativeQuery = true)
    Long sumStorageByTenant(String tenant);
}
