package com.iitbase.email.event;

import lombok.Getter;

@Getter
public class JobseekerWelcomeEvent extends EmailEvent {
    private final String email;
    private final String name;

    public JobseekerWelcomeEvent(Object source, String email, String name) {
        super(source);
        this.email = email;
        this.name  = name;
    }
}