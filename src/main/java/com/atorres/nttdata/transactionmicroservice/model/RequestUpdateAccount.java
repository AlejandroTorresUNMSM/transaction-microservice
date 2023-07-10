package com.atorres.nttdata.transactionmicroservice.model;

import lombok.Data;

@Data
public class RequestUpdateAccount {
    private Double balance;
    private String accountId;
    private String clientId;
}
