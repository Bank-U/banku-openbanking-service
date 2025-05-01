package com.banku.openbankingservice.service;

import com.banku.openbankingservice.aggregate.OpenBankingEventAggregate;
import com.banku.openbankingservice.event.OpenBankingEvent;
import com.plaid.client.request.PlaidApi;
import com.plaid.client.model.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaidService {
    private final OpenBankingEventService openBankingEventService;
    private final PlaidApi plaidApi;
    private final KafkaService kafkaService;

    public String createLinkToken(String userId, String language) {
        try {
            LinkTokenCreateRequest request = new LinkTokenCreateRequest();
            request.setClientName("Banku");
            request.setCountryCodes(List.of(CountryCode.ES));
            request.setLanguage(Optional.ofNullable(language).orElse("en"));
            request.setUser(new LinkTokenCreateRequestUser().clientUserId(userId));
            request.setProducts(List.of(Products.AUTH, Products.TRANSACTIONS));

            Response<LinkTokenCreateResponse> response = plaidApi.linkTokenCreate(request).execute();
            if (!response.isSuccessful()) {
                handlePlaidError(response);
            }
            
            // Save link created event
            openBankingEventService.saveLinkCreatedEvent(userId);
            
            return response.body().getLinkToken();
        } catch (Exception e) {
            log.error("Error creating link token", e);
            throw new RuntimeException("Failed to create link token", e);
        }
    }

    public void exchangePublicToken(String userId, String publicToken) {
        try {
            ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest();
            request.setPublicToken(publicToken);

            Response<ItemPublicTokenExchangeResponse> response = plaidApi.itemPublicTokenExchange(request).execute();
            if (!response.isSuccessful()) {
                handlePlaidError(response);
            }
            String accessToken = response.body().getAccessToken();
            
            // Save token exchanged event
            openBankingEventService.saveTokenExchangedEvent(userId, accessToken);
            Thread.sleep(5000); // Wait 5 seconds. TODO:Consider webhook instead.
            fetchAndProcessData(userId, accessToken);
        } catch (Exception e) {
            log.error("Error exchanging public token", e);
            throw new RuntimeException("Failed to exchange public token", e);
        }
    }

    public void forceRefresh(String userId) {
        log.info("Force refresh for user: {}", userId);
        String accessToken = openBankingEventService.findAggregatesByUserId(userId)
            .stream()
            .findFirst()
            .map(OpenBankingEventAggregate::getAccessToken)
            .orElseThrow(() -> new RuntimeException("No Plaid connection found for user"));

        fetchAndProcessData(userId, accessToken);
    }

    private void fetchAndProcessData(String userId, String accessToken) {
        try {
            // Fetch accounts
            AccountsGetRequest accountsRequest = new AccountsGetRequest();
            accountsRequest.setAccessToken(accessToken);
            Response<AccountsGetResponse> accountsResponse = plaidApi.accountsGet(accountsRequest).execute();
            if (!accountsResponse.isSuccessful()) {
                handlePlaidError(accountsResponse);
            }
            
            List<OpenBankingEvent.Account> accounts = accountsResponse.body().getAccounts().stream()
                .map(this::mapToAccount)
                .collect(Collectors.toList());

            // Fetch transactions
            TransactionsGetRequest transactionsRequest = new TransactionsGetRequest();
            transactionsRequest.setAccessToken(accessToken);
            transactionsRequest.setStartDate(LocalDate.now().minusDays(90));
            transactionsRequest.setEndDate(LocalDate.now());
            Response<TransactionsGetResponse> transactionsResponse = plaidApi.transactionsGet(transactionsRequest).execute();
            if (!transactionsResponse.isSuccessful()) {
                handlePlaidError(transactionsResponse);
            }
            
            List<OpenBankingEvent.Transaction> transactions = transactionsResponse.body().getTransactions().stream()
                .map(this::mapToTransaction)
                .collect(Collectors.toList());

            // Save data fetched event
            OpenBankingEvent event = openBankingEventService.saveDataFetchedEvent(userId, accounts, transactions);
            
            if (accounts.size() > 0 && transactions.size() > 0) {
                kafkaService.publishEvent(event);
            }
        } catch (Exception e) {
            log.error("Error refreshing data", e);
            throw new RuntimeException("Failed to refresh data", e);
        }
    }

    private void handlePlaidError(Response<?> response) throws Exception {
        try {
            String errorBody = response.errorBody().string();
            log.error("Plaid API error response: {}", errorBody);
            
            try {
                Gson gson = new Gson();
                PlaidError error = gson.fromJson(errorBody, PlaidError.class);
                throw new RuntimeException("Plaid API error: " + error.getErrorMessage());
            } catch (Exception e) {
                log.error("Failed to parse Plaid error as PlaidError object", e);
                throw new RuntimeException("Plaid API error: " + errorBody);
            }
        } catch (Exception e) {
            log.error("Failed to read error body", e);
            throw new RuntimeException("Failed to handle Plaid error: " + e.getMessage());
        }
    }

    private OpenBankingEvent.Account mapToAccount(AccountBase plaidAccount) {
        OpenBankingEvent.Account account = new OpenBankingEvent.Account();
        account.setAccountId(plaidAccount.getAccountId());
        account.setName(plaidAccount.getName());
        account.setType(plaidAccount.getType() != null ? plaidAccount.getType().toString() : null);
        account.setSubtype(plaidAccount.getSubtype() != null ? plaidAccount.getSubtype().toString() : null);
        account.setBalance(plaidAccount.getBalances().getCurrent() != null ? BigDecimal.valueOf(plaidAccount.getBalances().getCurrent()) : null);
        account.setCurrency(plaidAccount.getBalances().getIsoCurrencyCode());
        return account;
    }

    private OpenBankingEvent.Transaction mapToTransaction(Transaction plaidTransaction) {
        OpenBankingEvent.Transaction transaction = new OpenBankingEvent.Transaction();
        transaction.setTransactionId(plaidTransaction.getTransactionId());
        transaction.setAccountId(plaidTransaction.getAccountId());
        transaction.setAmount(plaidTransaction.getAmount() != null ? BigDecimal.valueOf(plaidTransaction.getAmount()) : null);
        transaction.setCurrency(plaidTransaction.getIsoCurrencyCode());
        transaction.setDate(plaidTransaction.getDate().atStartOfDay());
        transaction.setName(plaidTransaction.getName());
        transaction.setMerchantName(plaidTransaction.getMerchantName());
        transaction.setCategory(plaidTransaction.getCategory() != null ? plaidTransaction.getCategory().toString() : null);
        return transaction;
    }
} 