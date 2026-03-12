package com.fms.service;

import com.fms.config.AppConfig;
import com.fms.exception.TenantIsNotAllowedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains implementation related to tenant methods
 */
@Service
public class TenantService {

    private final AppConfig appConfig;

    @Autowired
    public TenantService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void checkIfTenantIsAllowed(final String tenant) {
        if (appConfig.isTenantVerification() && !appConfig.getTenantList().contains(tenant)) {
            throw new TenantIsNotAllowedException("Tenant '" + tenant + "' is not in the allowed list.");
        }
    }
}
