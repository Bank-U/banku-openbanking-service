package com.banku.openbankingservice.controller;

import com.banku.openbankingservice.service.PlaidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.banku.openbankingservice.controller.dto.ExchangeTokenRequest;
import com.banku.openbankingservice.controller.dto.ForceRefreshRequest;
import com.banku.openbankingservice.controller.dto.LinkTokenRequest;
import com.banku.openbankingservice.controller.dto.LinkTokenResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/openbanking")
@RequiredArgsConstructor
@Tag(name = "Open Banking", description = "Open Banking APIs for account and transaction information")
@SecurityRequirement(name = "bearerAuth")
public class OpenBankingController {
    private final PlaidService plaidService;

    @PostMapping("/link-token")
    public ResponseEntity<LinkTokenResponse> createLinkToken(@RequestBody LinkTokenRequest request) {
        String linkToken = plaidService.createLinkToken(request.getUserId(), request.getLanguage());
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
} 