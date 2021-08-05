package com.fms.service;

import com.fms.config.AppConfig;

/**
 * This class contains implementation related to tenant methods
 */
public class TenantService {

    private final AppConfig appConfig;

    public TenantService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void checkIfTenantIsAllowed(final String tenant){
        if(!appConfig.getTenantList().contains(tenant)) {
            new RuntimeException("Tenant is not allowed!");
        }
    }

}
