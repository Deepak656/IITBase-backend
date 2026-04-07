package com.iitbase.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class MemoryMonitor {

    // Runs every 1 hour
    @Scheduled(fixedRate = 3600000)
    public void logMemory() {
        MemoryLogger.log("PERIODIC_1H");
    }
}