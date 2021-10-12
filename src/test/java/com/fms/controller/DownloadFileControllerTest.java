package com.fms.controller;

import com.fms.config.AppConfig;
import com.fms.model.FileEntity;
import com.fms.repository.FileRepository;
import com.fms.service.DownloadFileService;
import com.fms.service.TenantService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

@Disabled
@ExtendWith(MockitoExtension.class)
class DownloadFileControllerTest {

    private static final String FILE_ID = "FILE_ID";
    private static final String TENANT_ID = "TENANT_ID";
    private static final List<String> TENANT_LIST = Collections.singletonList(TENANT_ID);
    private static final String FILE_DB_LOCATION = "/opt/java/file.jar";
    private static final boolean TENANT_VERIFICATION = true;

    @Mock
    FileRepository fileRepository;

    DownloadFileService downloadFileService = new DownloadFileService(fileRepository);

    AppConfig appConfig = new AppConfig(TENANT_LIST, FILE_DB_LOCATION, TENANT_VERIFICATION);

    TenantService tenantService = new TenantService(appConfig);

    DownloadFileController downloadFileController = new DownloadFileController(downloadFileService, tenantService);

    @Test
    void download() {

        FileEntity fileEntity = new FileEntity();
        List<FileEntity> list = Collections.singletonList(fileEntity);
        Mockito.when(fileRepository.findByFileId(ArgumentMatchers.anyString())).thenReturn(list);

        Mockito.when(downloadFileService.download(ArgumentMatchers.anyString()))
                .thenReturn(ArgumentMatchers.any());

        downloadFileController.download(FILE_ID);
    }

    @Test
    void downloadByTenant() {

        FileEntity fileEntity = new FileEntity();
        List<FileEntity> list = Collections.singletonList(fileEntity);
        Mockito.when(fileRepository.findByFileId(ArgumentMatchers.anyString())).thenReturn(list);

        downloadFileController.downloadByTenant(FILE_ID, TENANT_ID);
    }
}