package com.banku.openbankingservice.event;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public abstract class Event  {
    private final String aggregateId;
    private final LocalDateTime occurredOn;

    protected Event(String aggregateId) {
        this.aggregateId = aggregateId;
        this.occurredOn = LocalDateTime.now();
    }

    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}