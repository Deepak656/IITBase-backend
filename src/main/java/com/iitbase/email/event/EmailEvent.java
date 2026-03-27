package com.iitbase.email.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Base email event. Extend for each email type.
 * Published after DB commit — never before.
 */
@Getter
public abstract class EmailEvent extends ApplicationEvent {
    protected EmailEvent(Object source) {
        super(source);
    }
}