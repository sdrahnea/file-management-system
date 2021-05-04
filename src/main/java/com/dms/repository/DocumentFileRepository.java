package com.dms.repository;

import com.dms.model.DocumentFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DocumentFileRepository extends CrudRepository<DocumentFile, Long> {

    //TODO: select should contains the environment
    @Query(name = "select * from document_file where document_id = ?1 limit 1", nativeQuery = true)
    List<DocumentFile> findByDocumentId(String documentId);

}
