package com.fms.controller;

import com.fms.service.DownloadFileService;
import com.fms.service.TenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * In this class are implemented all end-points related to file download.
 */
@Slf4j
@RestController
@Api("Download File Controller API")
public class DownloadFileController {

    private final DownloadFileService downloadFileService;
    private final TenantService tenantService;

    @Autowired
    public DownloadFileController(DownloadFileService downloadFileService, TenantService tenantService){
        this.downloadFileService = downloadFileService;
        this.tenantService = tenantService;
    }

    @GetMapping(value = "download/{fileId}")
    @ApiOperation("download functionality")
    @ResponseBody
    public byte[] download(@PathVariable String fileId) {
        log.info("Receive request to download document id: {}", fileId);

        return downloadFileService.download(fileId);
    }

    @GetMapping(value = "download/{fileId}/{tenant}")
    @ApiOperation("download functionality by tenant")
    @ResponseBody
    public byte[] downloadByTenant(@PathVariable String fileId, @PathVariable String tenant) {
        log.info("Receive request to download document by (id, tenant): {},  {}", fileId, tenant);

        tenantService.checkIfTenantIsAllowed(tenant);

        return downloadFileService.download(fileId);
    }

}
