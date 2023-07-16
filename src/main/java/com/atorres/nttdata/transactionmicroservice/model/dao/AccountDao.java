package com.atorres.nttdata.transactionmicroservice.model.dao;

import com.atorres.nttdata.transactionmicroservice.utils.AccountType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDao {
    private String id;
    private AccountType type;
    private BigDecimal balance;
}
