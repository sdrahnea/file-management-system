package com.fms.service;

import com.fms.config.AppConfig;
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

    public void checkIfTenantIsAllowed(final String tenant){
        if(appConfig.isTenantVerification() && !appConfig.getTenantList().contains(tenant)) {
            throw new RuntimeException("Tenant is not allowed!");
        }
    }

}
