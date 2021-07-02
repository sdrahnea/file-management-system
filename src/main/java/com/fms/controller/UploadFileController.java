package com.fms.controller;

import com.fms.model.CreateFileResponseDto;
import com.fms.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * In this class are implemented all end-points related to file upload.
 */
@Slf4j
@RestController
@Api("Download File Controller API")
public class UploadFileController {

    private final FileService fileService;

    @Autowired
    public UploadFileController(FileService fileService){
        this.fileService = fileService;
    }

    @PostMapping("uploadNewFile/{tenant}")
    @ApiOperation("upload new file by tenant")
    public CreateFileResponseDto upload(@PathVariable String tenant,
                                        @RequestPart("file") MultipartFile multipartFile) {
        log.info("Receive request to upload file for tenant: {}", tenant );

        return fileService.upload(multipartFile, tenant);
    }

    @PostMapping("uploadMultipartFile/{fileId}/{tenant}")
    @ApiOperation("upload functionality")
    public String upload(@PathVariable String fileId,
                         @PathVariable String tenant,
                         @RequestPart("file") MultipartFile multipartFile) {
        log.info("Receive request to upload document id: {}", fileId);

        return fileService.upload(multipartFile, fileId, tenant);
    }

    @PostMapping("upload/{fileId}/{tenant}")
    @ApiOperation("upload functionality in case of byte array")
    public String upload(@PathVariable String fileId,
                         @PathVariable String tenant,
                         @RequestBody ByteArrayResource byteArrayResource) {
        log.info("Receive request to upload document id as byte array: {}", fileId);

        return fileService.upload(byteArrayResource, fileId, tenant);
    }

    @PostMapping("upload/{fileId}/{directory}/{tenant}")
    @ApiOperation("upload functionality in case of byte array and directory name")
    public String upload(@PathVariable String fileId,
                         @PathVariable String directory,
                         @PathVariable String tenant,
                         @RequestBody ByteArrayResource byteArrayResource) {
        log.info("Receive request to upload document id {} for directory: {}", fileId, directory);

        return fileService.upload(byteArrayResource, fileId, directory, tenant);
    }

}
