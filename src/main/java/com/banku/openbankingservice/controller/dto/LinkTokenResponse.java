package com.banku.openbankingservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(description = "Response containing a Plaid Link token")
@AllArgsConstructor
public class LinkTokenResponse {
    @Schema(description = "Plaid Link token", example = "link_token_123456789")
    private String linkToken;

}
