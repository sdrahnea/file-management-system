package com.fms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * This class contains all configuration from application config file which is used in multiple class.
 * Small field's description:
 *  tenantList          all tenant (is this is the case)
 *  fileDbLocation      the path where the file will be saved
 *  tenantVerification  is required to do tenant verification or not
 */
@Configuration
public class AppConfig {

    private final List<String> tenantList;
    private final String fileDbLocation;
    private final boolean tenantVerification;

    public AppConfig(@Value("#{'${tenant.list}'.split(',')}") List<String> tenantList,
                     @Value("${file.db.location}") String fileDbLocation,
                     @Value("${tenant.verification:false}") boolean tenantVerification
                     ) {
        this.tenantList = tenantList;
        this.fileDbLocation = fileDbLocation;
        this.tenantVerification = tenantVerification;
    }

    public List<String> getTenantList() {
        return tenantList;
    }

    public String getFileDbLocation() {
        return fileDbLocation;
    }

    public boolean isTenantVerification() {
        return tenantVerification;
    }
}
