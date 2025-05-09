package net.mohamed.springmultitenant.dto;

import lombok.Data;

@Data
public class InvoiceDto {

    private double amount;
    private String currency;
    private String status;
}
