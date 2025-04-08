package com.banku.openbankingservice.config;

import com.plaid.client.ApiClient;
import com.plaid.client.request.PlaidApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class PlaidConfig {

    @Value("${plaid.client-id}")
    private String clientId;

    @Value("${plaid.secret}")
    private String secret;

    @Value("${plaid.env}")
    private String environment;

    @Bean
    public ApiClient plaidClient() {
        HashMap<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", clientId);
        apiKeys.put("secret", secret);
        ApiClient apiClient = new ApiClient(apiKeys);
        apiClient.setPlaidAdapter(environment.equals("sandbox") ? ApiClient.Sandbox : ApiClient.Production);
        return apiClient;
    }

    @Bean
    public PlaidApi plaidApi(ApiClient plaidClient) {
        return plaidClient.createService(PlaidApi.class);
    }
} 