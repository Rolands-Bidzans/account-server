package com.eazybytes.accounts.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdersDto {
    private String orderId;

    @NotEmpty(message = "Name can not be a null or empty")
    private String name;

    @NotNull(message = "Quantity can not be a null or empty")
    private int qty;

    @NotNull(message = "Price can not be a null or empty")
    private double price;

    @NotEmpty(message = "Status can not be a null or empty")
    private String status; // pending, progress, completed

    @NotEmpty(message = "Account Number can not be a null or empty")
    private String accountNumber;
}