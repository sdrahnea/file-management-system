package service;

import com.fms.repository.FileRepository;
import com.fms.service.DownloadFileService;
import org.mockito.Mock;

public class DownloadFileServiceTest {

    @Mock
    FileRepository fileRepository;

    DownloadFileService downloadFileService = new DownloadFileService(fileRepository);

}
