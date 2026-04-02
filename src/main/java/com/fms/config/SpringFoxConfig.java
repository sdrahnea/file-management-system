package com.fms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration exposed by springdoc.
 */
@Configuration
public class SpringFoxConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(new Info()
                .title("File Management System API")
                .description("Manage file contents (upload/download/storage).")
                .version("1.0.7"));
    }

}
