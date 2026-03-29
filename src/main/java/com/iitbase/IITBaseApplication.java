package com.iitbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class IITBaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(IITBaseApplication.class, args);
    }
}