package com.atorres.nttdata.transactionmicroservice.model;

import lombok.Data;

@Data
public class RequestRetiro {
    private String from;
    private String clientId;
    private Double balance;
}
