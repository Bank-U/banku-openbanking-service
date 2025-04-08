package com.banku.openbankingservice.service;

import com.banku.openbankingservice.aggregate.OpenBankingAggregate;
import com.banku.openbankingservice.repository.OpenBankingAggregateRepository;
import com.plaid.client.request.PlaidApi;
import com.plaid.client.model.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaidService {
    private final OpenBankingAggregateRepository aggregateRepository;
    private final PlaidApi plaidApi;
    private final KafkaService kafkaService;

    public String createLinkToken(String userId) {
        try {
            LinkTokenCreateRequest request = new LinkTokenCreateRequest();
            request.setClientName("Banku");
            request.setCountryCodes(List.of(CountryCode.ES));
            request.setLanguage("en");
            request.setUser(new LinkTokenCreateRequestUser().clientUserId(userId));
            request.setProducts(List.of(Products.AUTH, Products.TRANSACTIONS));

            Response<LinkTokenCreateResponse> response = plaidApi.linkTokenCreate(request).execute();
            if (!response.isSuccessful()) {
                handlePlaidError(response);
            }
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

            OpenBankingAggregate aggregate = new OpenBankingAggregate();
            aggregate.setUserId(userId);
            aggregate.setAccessToken(accessToken);
            aggregate.setLastUpdated(LocalDateTime.now());

            aggregateRepository.save(aggregate);
        } catch (Exception e) {
            log.error("Error exchanging public token", e);
            throw new RuntimeException("Failed to exchange public token", e);
        }
    }

    public void forceRefresh(String userId) {
        OpenBankingAggregate aggregate = aggregateRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("No Plaid connection found for user"));

        try {
            // Fetch accounts
            AccountsGetRequest accountsRequest = new AccountsGetRequest();
            accountsRequest.setAccessToken(aggregate.getAccessToken());
            Response<AccountsGetResponse> accountsResponse = plaidApi.accountsGet(accountsRequest).execute();
            if (!accountsResponse.isSuccessful()) {
                handlePlaidError(accountsResponse);
            }

            // Fetch transactions
            TransactionsGetRequest transactionsRequest = new TransactionsGetRequest();
            transactionsRequest.setAccessToken(aggregate.getAccessToken());
            transactionsRequest.setStartDate(LocalDate.now().minusDays(90));
            transactionsRequest.setEndDate(LocalDate.now());
            Response<TransactionsGetResponse> transactionsResponse = plaidApi.transactionsGet(transactionsRequest).execute();
            if (!transactionsResponse.isSuccessful()) {
                handlePlaidError(transactionsResponse);
            }

            // Update aggregate
            aggregate.setAccounts(accountsResponse.body().getAccounts().stream()
                .map(this::mapToAccount)
                .collect(Collectors.toList()));

            aggregate.setTransactions(transactionsResponse.body().getTransactions().stream()
                .map(this::mapToTransaction)
                .collect(Collectors.toList()));

            aggregate.setLastUpdated(LocalDateTime.now());
            aggregateRepository.save(aggregate);

            if (aggregate.getTransactions().size() > 0 && aggregate.getAccounts().size() > 0) {
                kafkaService.sendMessage("banku.openbanking", aggregate.getUserId(), aggregate);
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

    private OpenBankingAggregate.Account mapToAccount(AccountBase plaidAccount) {
        OpenBankingAggregate.Account account = new OpenBankingAggregate.Account();
        account.setAccountId(plaidAccount.getAccountId());
        account.setName(plaidAccount.getName());
        account.setType(plaidAccount.getType() != null ? plaidAccount.getType().toString() : null);
        account.setSubtype(plaidAccount.getSubtype() != null ? plaidAccount.getSubtype().toString() : null);
        account.setBalance(plaidAccount.getBalances().getCurrent() != null ? BigDecimal.valueOf(plaidAccount.getBalances().getCurrent()) : null);
        account.setCurrency(plaidAccount.getBalances().getIsoCurrencyCode());
        return account;
    }

    private OpenBankingAggregate.Transaction mapToTransaction(Transaction plaidTransaction) {
        OpenBankingAggregate.Transaction transaction = new OpenBankingAggregate.Transaction();
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