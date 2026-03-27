package com.iitbase.email.event;

import lombok.Getter;

@Getter
public class RecruiterWelcomeEvent extends EmailEvent {
    private final String email;
    private final String name;
    private final String companyName;

    public RecruiterWelcomeEvent(Object source, String email,
                                 String name, String companyName) {
        super(source);
        this.email       = email;
        this.name        = name;
        this.companyName = companyName;
    }
}