package com.atorres.nttdata.transactionmicroservice.model.dao;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
public class CreditDao {
    @Id
    private String id;
    private Double balance;
    private Double debt;
}
