package com.fms.controller;

import com.fms.service.DownloadFileService;
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

    @Autowired
    public DownloadFileController(DownloadFileService downloadFileService){
        this.downloadFileService = downloadFileService;
    }

    @GetMapping(value = "download/{fileId}")
    @ApiOperation("download functionality")
    public @ResponseBody
    byte[] download(@PathVariable String fileId) {
        log.info("Receive request to download document id: {}", fileId);

        return downloadFileService.download(fileId);
    }

}