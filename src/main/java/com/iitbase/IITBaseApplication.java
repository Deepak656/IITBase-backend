package com.iitbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class IITBaseApplication {
    public static void main(String[] args) {
        // Prevent PDFBox from loading AWT/font rendering subsystem
        System.setProperty("java.awt.headless", "true");
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        System.setProperty("org.apache.pdfbox.rendering.UsePureJavaCMYKConversion", "true");

        SpringApplication.run(IITBaseApplication.class, args);
    }
}