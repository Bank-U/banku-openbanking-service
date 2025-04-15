package com.banku.openbankingservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Bank account information response")
public class AccountResponse {
    @Schema(description = "Unique identifier of the account", example = "acc_123456789")
    private String accountId;

    @Schema(description = "Name of the account", example = "Main Checking Account")
    private String name;

    @Schema(description = "Type of the account", example = "CHECKING")
    private String type;

    @Schema(description = "Current balance of the account", example = "1234.56")
    private BigDecimal balance;

    @Schema(description = "Currency of the account", example = "USD")
    private String currency;

    @Schema(description = "Name of the bank institution", example = "Bank of America")
    private String institutionName;

    @Schema(description = "Last 4 digits of the account number", example = "1234")
    private String mask;

    @Schema(description = "Official name of the account at the institution", example = "360 Checking Account")
    private String officialName;

    @Schema(description = "Subtype of the account for more specific categorization", example = "360_CHECKING")
    private String subtype;
} 