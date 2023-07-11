package com.atorres.nttdata.transactionmicroservice.model;

import lombok.Data;

@Data
public class RequestTransactionAccount {
    private String accountId;
    private String clientId;
    private Double amount;
}
