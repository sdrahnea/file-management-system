package com.fms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class CreateFileRequestDTO {

    private String tenant;
    private MultipartFile multipartFile;

}
