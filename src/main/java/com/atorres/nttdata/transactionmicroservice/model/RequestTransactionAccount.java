package com.atorres.nttdata.transactionmicroservice.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestTransactionAccount {
    private String accountId;
    private String clientId;
    private BigDecimal amount;
}
