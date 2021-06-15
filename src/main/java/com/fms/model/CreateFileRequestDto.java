package com.fms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class CreateFileRequestDto {

    private String tenant;
    private MultipartFile multipartFile;

}
