package com.iitbase.admin.staff;

public enum StaffInviteStatus {
    PENDING,    // sent, not yet accepted
    ACCEPTED,   // recipient accepted and now has ADMIN role
    EXPIRED,    // TTL passed
    REVOKED     // manually cancelled by an admin
}