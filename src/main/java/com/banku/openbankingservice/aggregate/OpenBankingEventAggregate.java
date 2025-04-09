package com.banku.openbankingservice.aggregate;

import com.banku.openbankingservice.event.OpenBankingEvent;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class OpenBankingEventAggregate implements Aggregate {
    private String id;
    private String userId;
    private String accessToken;
    private List<OpenBankingEvent.Account> accounts = new ArrayList<>();
    private List<OpenBankingEvent.Transaction> transactions = new ArrayList<>();
    private long version = 0;
    private LocalDateTime lastUpdated;

    @Override
    public void apply(Object event) {
        if (!(event instanceof OpenBankingEvent)) {
            return;
        }

        OpenBankingEvent openBankingEvent = (OpenBankingEvent) event;
        this.id = openBankingEvent.getAggregateId();
        this.userId = openBankingEvent.getUserId();
        this.version = openBankingEvent.getVersion();
        
        switch (openBankingEvent.getEventType()) {
            case LINK_CREATED:
                // No hay cambios para este evento
                break;
            case TOKEN_EXCHANGED:
                this.accessToken = openBankingEvent.getAccessToken();
                break;
            case DATA_FETCHED:
                if (openBankingEvent.getAccounts() != null && !openBankingEvent.getAccounts().isEmpty()) {
                    this.accounts = new ArrayList<>(openBankingEvent.getAccounts());
                }
                if (openBankingEvent.getTransactions() != null && !openBankingEvent.getTransactions().isEmpty()) {
                    this.transactions = new ArrayList<>(openBankingEvent.getTransactions());
                }
                break;
        }
        
        this.lastUpdated = LocalDateTime.now();
    }
} 