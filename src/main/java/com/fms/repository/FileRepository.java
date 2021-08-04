package com.fms.repository;

import com.fms.model.FileEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileRepository extends CrudRepository<FileEntity, Long> {

    @Query(name = "select * from document_file where document_id = ?1 limit 1", nativeQuery = true)
    List<FileEntity> findByDocumentId(String documentId);

    @Query(name = "select * from document_file where document_id = ?1 AND tenant = ?1 limit 1", nativeQuery = true)
    List<FileEntity> findByDocumentIdAndTenant(String documentId, String tenant);

}
