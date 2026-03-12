package com.fms.service;

import com.fms.config.AppConfig;
import com.fms.exception.RateLimitExceededException;
import com.fms.exception.StorageQuotaExceededException;
import com.fms.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuotaService {

    private final AppConfig appConfig;
    private final FileRepository fileRepository;
    private final UsageService usageService;

    @Autowired
    public QuotaService(AppConfig appConfig,
                        FileRepository fileRepository,
                        UsageService usageService) {
        this.appConfig = appConfig;
        this.fileRepository = fileRepository;
        this.usageService = usageService;
    }

    public void assertUploadAllowed(String tenant, long incomingBytes) {
        assertStorageQuota(tenant, incomingBytes);
        assertRateLimit(tenant);
    }

    private void assertStorageQuota(String tenant, long incomingBytes) {
        Long used = fileRepository.sumStorageByTenant(tenant);
        long usedBytes = used == null ? 0L : used;
        long limitBytes = appConfig.getTenantStorageQuotaBytes();

        if (usedBytes + incomingBytes > limitBytes) {
            throw new StorageQuotaExceededException(tenant, limitBytes);
        }
    }

    private void assertRateLimit(String tenant) {
        long uploadsIn24h = usageService.countUploadsInLast24Hours(tenant);
        long limitPerDay = appConfig.getTenantUploadLimitPerDay();

        if (uploadsIn24h >= limitPerDay) {
            throw new RateLimitExceededException(tenant, limitPerDay);
        }
    }
}

