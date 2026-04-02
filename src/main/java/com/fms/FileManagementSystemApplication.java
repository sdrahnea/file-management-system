package com.fms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by sdrahnea
 */
@EnableAutoConfiguration
@SpringBootApplication
@EnableJpaRepositories
@ComponentScan
@EnableScheduling
public class FileManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileManagementSystemApplication.class, args);
    }
}
