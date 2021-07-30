package com.fms.model;

import javax.persistence.*;
import java.util.Date;

/**
 * should store file name, or id
 */

@Entity
@Table(name = "file_entity")
public class FileEntity {

    private static final long serialVersionUID = -3009157732242241606L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "file_id")
    private String fileId;

    @Column(name = "directory")
    private String directory;

    @Column(name = "path")
    private String path;

    @Column(name = "tenant")
    private String tenant;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    public FileEntity(){
        if (getCreatedDate() == null){
            setCreatedDate(new Date());
        }
    }

    public FileEntity(String fileId, String directory, String path, String tenant) {
        this.fileId = fileId;
        this.directory = directory;
        this.path = path;
        this.tenant = tenant;
        if (getCreatedDate() == null){
            setCreatedDate(new Date());
        }
    }

    public FileEntity(String fileId, String path, String tenant) {
        this.fileId = fileId;
        this.path = path;
        this.tenant = tenant;
        if (getCreatedDate() == null){
            setCreatedDate(new Date());
        }
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String contentId) {
        this.fileId = contentId;
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

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
