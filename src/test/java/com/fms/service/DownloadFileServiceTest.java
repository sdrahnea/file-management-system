package com.fms.service;

import com.fms.model.FileEntity;
import com.fms.repository.FileRepository;
import com.fms.service.DownloadFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class DownloadFileServiceTest {

    @Mock
    FileRepository fileRepository;

    @InjectMocks
    DownloadFileService downloadFileService;

    @Test
    public void test(){
        FileEntity fileEntity = new FileEntity();
        List<FileEntity> list = Collections.singletonList(fileEntity);
        Mockito.when(fileRepository.findByFileId(ArgumentMatchers.anyString())).thenReturn(list);

        downloadFileService.download(ArgumentMatchers.anyString());
    }

}
