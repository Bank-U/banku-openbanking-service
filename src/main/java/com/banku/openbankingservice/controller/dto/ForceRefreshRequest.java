package com.banku.openbankingservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request to force a refresh of the Plaid access token")
@AllArgsConstructor
public class ForceRefreshRequest {
    @Schema(description = "ID of the user to force the refresh for", example = "usr_123456789")
    private String userId;
}
