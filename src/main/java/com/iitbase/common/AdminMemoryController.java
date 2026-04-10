package com.iitbase.common;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/memory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMemoryController {
    // Internal memory cleanup endpoint
    @GetMapping("/cleanup")
    public String cleanupMemory() {
        System.gc(); // Suggests JVM to run garbage collection
        return "Memory cleanup triggered";
    }
}
