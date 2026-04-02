package com.fms.controller;

import com.fms.model.CreateFileResponseDTO;
import com.fms.service.TenantService;
import com.fms.service.UploadFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Upload File Controller API")
public class UploadFileController {

    private final UploadFileService uploadFileService;
    private final TenantService tenantService;

    @Autowired
    public UploadFileController(UploadFileService uploadFileService,
                                TenantService tenantService) {
        this.uploadFileService = uploadFileService;
        this.tenantService = tenantService;
    }

    @PostMapping("uploadNewFile/{tenant}")
    @Operation(summary = "upload new file by tenant")
    public CreateFileResponseDTO upload(@PathVariable String tenant,
                                        @RequestPart("file") MultipartFile multipartFile) {
        log.info("Receive request to upload file for tenant: {}", tenant);

        tenantService.checkIfTenantIsAllowed(tenant);

        return uploadFileService.upload(multipartFile, tenant);
    }

    @PostMapping("uploadByTenantAndFileId/{tenant}/{fileId}")
    @Operation(summary = "upload new file by tenant and file id values")
    public CreateFileResponseDTO uploadFileByTenantAndFileId(@PathVariable String tenant,
                                                             @PathVariable String fileId,
                                                             @RequestPart("file") MultipartFile multipartFile) {
        log.info("Receive request to upload file for tenant: {}, file id: {}", tenant, fileId);
        tenantService.checkIfTenantIsAllowed(tenant);
        return uploadFileService.uploadFileByTenantAndId(multipartFile, tenant, fileId);
    }

    @PostMapping("uploadNewFile/{fileId}/{tenant}")
    @Operation(summary = "upload new file by file id and tenant values")
    public String uploadNewFile(@PathVariable String fileId,
                                @PathVariable String tenant,
                                @RequestPart("file") MultipartFile multipartFile) {
        log.info("Receive request to upload file for tenant: {}, file id: {}", tenant, fileId);
        tenantService.checkIfTenantIsAllowed(tenant);
        return uploadFileService.uploadNewFile(multipartFile, tenant, fileId);
    }

    @PostMapping("uploadMultipartFile/{fileId}/{tenant}")
    @Operation(summary = "upload functionality")
    public String upload(@PathVariable String fileId,
                         @PathVariable String tenant,
                         @RequestPart("file") MultipartFile multipartFile) {
        log.info("Receive request to upload document id: {}", fileId);

        tenantService.checkIfTenantIsAllowed(tenant);

        return uploadFileService.upload(multipartFile, fileId, tenant);
    }

    @PostMapping("upload/{fileId}/{tenant}")
    @Operation(summary = "upload functionality in case of byte array")
    public String upload(@PathVariable String fileId,
                         @PathVariable String tenant,
                         @RequestBody ByteArrayResource byteArrayResource) {
        log.info("Receive request to upload document id as byte array: {}", fileId);

        tenantService.checkIfTenantIsAllowed(tenant);

        return uploadFileService.upload(byteArrayResource, fileId, tenant);
    }

    @PostMapping("upload/{fileId}/{directory}/{tenant}")
    @Operation(summary = "upload functionality in case of byte array and directory name")
    public String upload(@PathVariable String fileId,
                         @PathVariable String directory,
                         @PathVariable String tenant,
                         @RequestBody ByteArrayResource byteArrayResource) {
        log.info("Receive request to upload document id {} for directory: {}", fileId, directory);

        tenantService.checkIfTenantIsAllowed(tenant);

        return uploadFileService.upload(byteArrayResource, fileId, directory, tenant);
    }

}
