package com.banku.openbankingservice.controller;

import com.banku.openbankingservice.service.PlaidService;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/openbanking")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class OpenBankingController {
    private final PlaidService plaidService;

    @PostMapping("/link-token")
    public ResponseEntity<LinkTokenResponse> createLinkToken(@RequestBody LinkTokenRequest request) {
        String linkToken = plaidService.createLinkToken(request.getUserId());
        return ResponseEntity.ok(new LinkTokenResponse(linkToken));
    }

    @PostMapping("/exchange-token")
    public ResponseEntity<Void> exchangePublicToken(@RequestBody ExchangeTokenRequest request) {
        plaidService.exchangePublicToken(request.getUserId(), request.getPublicToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/force-refresh")
    public ResponseEntity<Void> forceRefresh(@RequestBody ForceRefreshRequest request) {
        plaidService.forceRefresh(request.getUserId());
        return ResponseEntity.ok().build();
    }

    @Data
    public static class LinkTokenRequest {
        private String userId;
    }

    @Data
    public static class LinkTokenResponse {
        private final String linkToken;
    }

    @Data
    public static class ExchangeTokenRequest {
        private String userId;
        private String publicToken;
    }

    @Data
    public static class ForceRefreshRequest {
        private String userId;
    }
} 