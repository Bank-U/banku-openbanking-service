package com.banku.openbankingservice.aggregate;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class OpenBankingAggregate {
    @Id
    private String id;
    private String userId;
    private String accessToken;
    private List<Account> accounts = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private LocalDateTime lastUpdated;

    public OpenBankingAggregate() {
        this.lastUpdated = LocalDateTime.now();
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