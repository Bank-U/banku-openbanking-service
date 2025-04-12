package com.banku.openbankingservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public abstract class Event extends ApplicationEvent {
    private final String aggregateId;
    private final LocalDateTime occurredOn;

    protected Event(Object source, String aggregateId) {
        super(source);
        this.aggregateId = aggregateId;
        this.occurredOn = LocalDateTime.now();
    }

    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}