package com.fms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class CreateFileResponseDTO {

    private String uuid;
    private String tenant;
    private String path;
    private Instant createdAt;

}
