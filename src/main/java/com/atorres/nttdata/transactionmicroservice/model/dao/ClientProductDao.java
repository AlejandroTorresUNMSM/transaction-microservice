package com.atorres.nttdata.transactionmicroservice.model.dao;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class ClientProductDao {
    @Id
    private String id;
    private String client;
    private String product;
    private String category;
    private String subcategory;
}
