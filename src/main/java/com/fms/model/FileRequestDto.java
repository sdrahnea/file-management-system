package com.fms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileRequestDto {

    private String fileId;
    private String tenant;

}
