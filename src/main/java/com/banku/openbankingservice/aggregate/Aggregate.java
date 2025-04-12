package com.banku.openbankingservice.aggregate;

public interface Aggregate {
    void apply(Object event);
    String getUserId();
    long getVersion();
} 