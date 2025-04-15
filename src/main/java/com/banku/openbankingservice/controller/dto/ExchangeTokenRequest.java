package com.banku.openbankingservice.controller.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request to exchange a public token for an access token")
public class ExchangeTokenRequest {
    @Schema(description = "ID of the user to exchange the token for")
    private String userId;

    @Schema(description = "Public token to exchange", example = "public_token_123456789")
    private String publicToken;
}
