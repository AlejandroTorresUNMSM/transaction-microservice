package com.atorres.nttdata.transactionmicroservice.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestUpdateAccount {
    private BigDecimal balance;
    private String accountId;
    private String clientId;
}
