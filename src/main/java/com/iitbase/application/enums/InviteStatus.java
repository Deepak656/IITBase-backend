package com.iitbase.application.enums;

public enum InviteStatus {
    PENDING,    // sent, jobseeker hasn't acted
    ACCEPTED,   // jobseeker applied via invite
    DECLINED,   // jobseeker explicitly declined
    EXPIRED     // invite older than 30 days
}