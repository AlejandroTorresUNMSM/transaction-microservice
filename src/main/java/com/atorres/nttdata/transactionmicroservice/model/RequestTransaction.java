package com.atorres.nttdata.transactionmicroservice.model;

import lombok.Data;

@Data
public class RequestTransaction {
    private String from;
    private String to;
    private Double amount;
    private String clientId;
}
