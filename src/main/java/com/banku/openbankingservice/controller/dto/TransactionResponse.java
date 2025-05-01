package com.banku.openbankingservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Bank transaction information response")
public class TransactionResponse {
    @Schema(description = "Unique identifier of the transaction", example = "txn_123456789")
    private String transactionId;

    @Schema(description = "ID of the account this transaction belongs to", example = "acc_123456789")
    private String accountId;

    @Schema(description = "Amount of the transaction", example = "123.45")
    private BigDecimal amount;

    @Schema(description = "Currency of the transaction", example = "USD")
    private String currency;

    @Schema(description = "Date and time when the transaction occurred", example = "2024-03-15T10:30:00")
    private LocalDateTime date;

    @Schema(description = "Name of the merchant or description", example = "Walmart")
    private String name;

    @Schema(description = "Merchant name as it appears on the statement", example = "WALMART STORE #1234")
    private String merchantName;

    @Schema(description = "Primary category of the transaction", example = "SHOPPING")
    private String category;

    @Schema(description = "Detailed subcategory of the transaction", example = "RETAIL_STORE")
    private String subcategory;

    @Schema(description = "Whether the transaction is pending", example = "false")
    private boolean pending;

    @Schema(description = "Type of the transaction (debit/credit)", example = "debit")
    private String type;

    @Schema(description = "Location information of the transaction")
    private LocationInfo location;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Location information for the transaction")
    public static class LocationInfo {
        @Schema(description = "Address of the transaction location", example = "123 Main St")
        private String address;

        @Schema(description = "City where the transaction occurred", example = "New York")
        private String city;

        @Schema(description = "Region or state of the transaction", example = "NY")
        private String region;

        @Schema(description = "Postal code of the location", example = "10001")
        private String postalCode;

        @Schema(description = "Country where the transaction occurred", example = "US")
        private String country;

        @Schema(description = "Latitude of the transaction location", example = "40.7128")
        private Double latitude;

        @Schema(description = "Longitude of the transaction location", example = "-74.0060")
        private Double longitude;
    }
} 