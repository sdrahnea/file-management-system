package com.fms.config;

import com.fms.exception.ApiKeyUnauthorizedException;
import com.fms.service.ApiKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class ApiKeyAuthInterceptor implements HandlerInterceptor {

    private final ApiKeyService apiKeyService;

    @Autowired
    public ApiKeyAuthInterceptor(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestPath = request.getRequestURI();

        // Do not enforce API key for management endpoints used to create/list/revoke keys.
        if (requestPath != null && requestPath.startsWith("/admin/apikey")) {
            return true;
        }

        String resolvedTenant = apiKeyService.validateAndGetTenant(request.getHeader("X-API-Key"));
        if (resolvedTenant == null) {
            return true;
        }

        Object attr = request.getAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables");
        if (attr instanceof Map) {
            Map<String, String> vars = (Map<String, String>) attr;
            String tenantFromPath = vars.get("tenant");
            if (tenantFromPath != null && !tenantFromPath.equals(resolvedTenant)) {
                throw new ApiKeyUnauthorizedException();
            }
        }

        return true;
    }
}

