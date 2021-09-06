package com.fms.service;

import com.fms.config.AppConfig;
import com.fms.service.TenantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.Collections;
import java.util.List;

public class TenantServiceTest {

    private static final String ABC = "abc";
    private static final List<String> TENANT_LIST = Collections.singletonList(ABC);
    private static final String FILE_DB_LOCATION = "/opt/java/file.jar";
    private static final boolean TENANT_VERIFICATION = true;

    AppConfig appConfig = new AppConfig(TENANT_LIST, FILE_DB_LOCATION, TENANT_VERIFICATION);

    TenantService tenantService = new TenantService(appConfig);

    @Test
    public void should_not_return_any_exception(){
        tenantService.checkIfTenantIsAllowed(ABC);
    }

    @Test
    public void should__return_runtime_exception(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            tenantService.checkIfTenantIsAllowed(ArgumentMatchers.anyString());
        });
    }

    @Test
    public void should_not_return_any_exception_when_tenant_verification_is_disabled(){
        tenantService  = new TenantService(
                new AppConfig(TENANT_LIST, FILE_DB_LOCATION, !TENANT_VERIFICATION)
        );

        tenantService.checkIfTenantIsAllowed(ArgumentMatchers.anyString());
    }

    @Test
    public void should_not_return_any_exception_when_tenant_verification_is_disabled_and_tenant_list_is_null(){
        tenantService  = new TenantService(
                new AppConfig(null, FILE_DB_LOCATION, !TENANT_VERIFICATION)
        );

        tenantService.checkIfTenantIsAllowed(ArgumentMatchers.anyString());
    }

    @Test
    public void should_not_return_any_exception_when_tenant_verification_is_disabled_and_tenant_list_is_empty(){
        tenantService  = new TenantService(
                new AppConfig(Collections.emptyList(), FILE_DB_LOCATION, !TENANT_VERIFICATION)
        );

        tenantService.checkIfTenantIsAllowed(ArgumentMatchers.anyString());
    }

}
