package com.atorres.nttdata.transactionmicroservice.model.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("transaction")
public class TransactionDao {
    @Id
    private String id;
    private String from;
    private String to;
    private String category;
    private Double balance;
    private Double comission;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Lima")
    private Date date;
    private String clientId;
}
