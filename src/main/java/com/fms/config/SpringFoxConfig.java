package com.fms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

/**
 * This class contains the configuration for swagger.
 * Contains information related the production.
 *
 * WARN: at this moment the hard-coded values should be modified once a new release was done.
 */
@Configuration
public class SpringFoxConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    //TODO: get this values from the property file.
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "File Management System API",
                "Manage file contents (upload/download/storage).",
                "1.0.7",
                "Terms of service",
                null,
                "License of API", "API license URL", Collections.emptyList());
    }

}
