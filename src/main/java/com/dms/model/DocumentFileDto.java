package com.dms.model;

import org.springframework.web.multipart.MultipartFile;

public class DocumentFileDto {

    private MultipartFile file;
    private String contentId;

    public DocumentFileDto(MultipartFile file, String contentId) {
        this.file = file;
        this.contentId = contentId;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
}
