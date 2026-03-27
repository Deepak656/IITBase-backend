package com.iitbase.recruiter.enums;

public enum InviteStatus {
    PENDING,   // email sent, not yet accepted
    ACCEPTED,  // recipient signed up and joined
    EXPIRED,   // token TTL passed
    REVOKED    // admin manually revoked
}