package com.banku.openbankingservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request to create a Plaid Link token")
public class LinkTokenRequest {
    @Schema(description = "ID of the user to create the Link token for")
    private String userId;
    
    @Schema(description = "Language code for the Link interface", example = "en")
    private String language;
}
