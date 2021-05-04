package com.dms.model;

import javax.persistence.*;
import java.util.Date;

/**
 * should store document name, or id
 */

@Entity
@Table(name = "document_file")
public class DocumentFile {

    private static final long serialVersionUID = -3009157732242241606L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "directory")
    private String directory;

    @Column(name = "path")
    private String path;

    @Column(name = "environment")
    private String environment;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    public DocumentFile(){
        if (getCreatedDate() == null){
            setCreatedDate(new Date());
        }
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String contentId) {
        this.documentId = contentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
