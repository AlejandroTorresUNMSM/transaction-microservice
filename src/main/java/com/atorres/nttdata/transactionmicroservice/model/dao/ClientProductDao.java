package com.atorres.nttdata.transactionmicroservice.model.dao;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
public class ClientProductDao {
    @Id
    private String id;
    private String client;
    private String product;
    private String category;
}
