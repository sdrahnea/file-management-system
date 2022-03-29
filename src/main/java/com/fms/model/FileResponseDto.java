package com.fms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileResponseDto {

    private String fileId;
    private String directory;
    private String path;
    private String tenant;
    private ErrorDTO errorDto;

}
