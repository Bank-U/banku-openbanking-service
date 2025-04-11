package com.banku.openbankingservice.event;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "openbanking_events")
public class OpenBankingEvent {
    
    @Id
    private String id;
    private String aggregateId;
    private String userId;
    private EventType eventType;
    private long timestamp;
    private long version = 1L;
    
    private String accessToken;
    
    private List<Account> accounts = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    
    public enum EventType {
        LINK_CREATED,
        TOKEN_EXCHANGED,
        DATA_FETCHED
    }
    
    @Data
    public static class Account {
        private String accountId;
        private String name;
        private String type;
        private String subtype;
        private BigDecimal balance;
        private String currency;
    }

    @Data
    public static class Transaction {
        private String transactionId;
        private String accountId;
        private BigDecimal amount;
        private String currency;
        private LocalDateTime date;
        private String name;
        private String merchantName;
        private String category;
    }
} 