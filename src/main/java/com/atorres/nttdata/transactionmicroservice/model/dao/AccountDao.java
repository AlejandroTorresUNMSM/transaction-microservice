package com.atorres.nttdata.transactionmicroservice.model.dao;

import com.atorres.nttdata.transactionmicroservice.utils.AccountType;
import lombok.Data;

@Data
public class AccountDao {
    private String id;
    private AccountType type;
    private Double balance;
}
