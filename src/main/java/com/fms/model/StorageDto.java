package com.fms.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class StorageDto {

    private String tenant;
    private String fileId;
    private byte[] content;
    private MultipartFile multipartFile;

}
