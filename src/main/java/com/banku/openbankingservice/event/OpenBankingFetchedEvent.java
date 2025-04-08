package com.banku.openbankingservice.event;

import com.banku.openbankingservice.aggregate.OpenBankingAggregate;
import lombok.Getter;

import java.util.List;

@Getter
public class OpenBankingFetchedEvent extends Event {
    private final String userId;
    private final List<OpenBankingAggregate.Account> accounts;
    private final List<OpenBankingAggregate.Transaction> transactions;

    public OpenBankingFetchedEvent(Object source, String aggregateId, String userId, 
                                 List<OpenBankingAggregate.Account> accounts,
                                 List<OpenBankingAggregate.Transaction> transactions) {
        super(source, aggregateId);
        this.userId = userId;
        this.accounts = accounts;
        this.transactions = transactions;
    }
} 