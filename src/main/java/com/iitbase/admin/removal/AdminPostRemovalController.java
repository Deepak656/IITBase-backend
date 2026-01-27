package com.iitbase.admin.removal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/postRemoval")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostRemovalController {
    private final AdminPostRemovalService adminPostRemovalService;
}
