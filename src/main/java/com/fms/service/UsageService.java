package com.fms.service;

import com.fms.model.UsageEvent;
import com.fms.repository.UsageEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UsageService {

    private final UsageEventRepository usageEventRepository;

    @Autowired
    public UsageService(UsageEventRepository usageEventRepository) {
        this.usageEventRepository = usageEventRepository;
    }

    public void recordUpload(String tenant, String fileId, long fileSizeBytes) {
        usageEventRepository.save(new UsageEvent(tenant, UsageEvent.EventType.UPLOAD, fileId, fileSizeBytes));
    }

    public void recordDownload(String tenant, String fileId, long fileSizeBytes) {
        usageEventRepository.save(new UsageEvent(tenant, UsageEvent.EventType.DOWNLOAD, fileId, fileSizeBytes));
    }

    public long countUploadsInLast24Hours(String tenant) {
        Date since = new Date(System.currentTimeMillis() - 24L * 60L * 60L * 1000L);
        Long count = usageEventRepository.countUploadsSince(tenant, since);
        return count == null ? 0L : count;
    }
}

