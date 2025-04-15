package com.banku.openbankingservice.controller;

import com.banku.openbankingservice.event.OpenBankingEvent;
import com.banku.openbankingservice.service.OpenBankingEventService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/openbanking/financial")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Financial", description = "Financial APIs for account and transaction information")
@SecurityRequirement(name = "bearerAuth")
public class FinancialController {
    
    private final OpenBankingEventService openBankingEventService;
    
    @GetMapping("/transactions")
    public ResponseEntity<List<OpenBankingEvent.Transaction>> getTransactions() {
        try {
            List<OpenBankingEvent.Transaction> transactions = openBankingEventService.getTransactions();
            if (transactions == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Error al obtener transacciones", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/accounts")
    public ResponseEntity<List<OpenBankingEvent.Account>> getAccounts() {
        try {
            List<OpenBankingEvent.Account> accounts = openBankingEventService.getAccounts();
            if (accounts == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            log.error("Error al obtener cuentas", e);
            return ResponseEntity.status(500).build();
        }
    }
}