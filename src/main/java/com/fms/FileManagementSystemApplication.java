package com.fms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by sdrahnea
 */
@EnableSwagger2
@EnableAutoConfiguration
@SpringBootApplication
@EnableJpaRepositories
@ComponentScan
public class FileManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileManagementSystemApplication.class, args);
    }
}
