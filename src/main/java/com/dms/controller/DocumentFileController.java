package com.dms.controller;

import com.dms.service.DocumentFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Upload / download contents
 */
@Slf4j
@RestController
@Api("Document File Controller API")
public class DocumentFileController {

    private final DocumentFileService documentFileService;

    @Autowired
    public DocumentFileController(DocumentFileService documentFileService) {
        this.documentFileService = documentFileService;
    }

    @PostMapping("uploadMultipartFile/{documentId}/{tenant}")
    @ApiOperation("upload functionality")
    public String upload(@PathVariable String documentId,
                         @PathVariable String tenant,
                         @RequestPart("file") MultipartFile multipartFile) {
        log.info("Receive request to upload document id: {}", documentId);

        return documentFileService.upload(multipartFile, documentId, tenant);
    }

    @PostMapping("upload/{documentId}/{tenant}")
    @ApiOperation("upload functionality in case of byte array")
    public String upload(@PathVariable String documentId,
                         @PathVariable String tenant,
                         @RequestBody ByteArrayResource byteArrayResource) {
        log.info("Receive request to upload document id as byte array: {}", documentId);

        return documentFileService.upload(byteArrayResource, documentId, tenant);
    }

    @PostMapping("upload/{documentId}/{directory}/{tenant}")
    @ApiOperation("upload functionality in case of byte array and directory name")
    public String upload(@PathVariable String documentId,
                         @PathVariable String directory,
                         @PathVariable String tenant,
                         @RequestBody ByteArrayResource byteArrayResource) {
        log.info("Receive request to upload document id {} for directory: {}", documentId, directory);

        return documentFileService.upload(byteArrayResource, documentId, directory, tenant);
    }

    @GetMapping(value = "download/{documentId}")
    @ApiOperation("download functionality")
    public @ResponseBody byte[] download(@PathVariable String documentId) {
        log.info("Receive request to download document id: {}", documentId);

        return documentFileService.download(documentId);
    }

}
