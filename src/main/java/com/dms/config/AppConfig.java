package com.dms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {

    private final List<String> tenantList;
    private final String fileDbLocation;

    public AppConfig(@Value("#{'${tenant.list}'.split(',')}") List<String> tenantList,
                     @Value("${file.db.location}") String fileDbLocation) {
        this.tenantList = tenantList;
        this.fileDbLocation = fileDbLocation;
    }

    public List<String> getTenantList() {
        return tenantList;
    }

    public String getFileDbLocation() {
        return fileDbLocation;
    }
}
