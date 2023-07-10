package com.atorres.nttdata.transactionmicroservice.utils;

import com.atorres.nttdata.transactionmicroservice.model.RequestRetiro;
import com.atorres.nttdata.transactionmicroservice.model.dao.TransactionDao;

import java.util.Date;

public class MapperTransaction {
    public TransactionDao retiroRequestToDao(RequestRetiro requestRetiro, Double balance){
        TransactionDao transactionDao = new TransactionDao();
        transactionDao.setBalance(balance);
        transactionDao.setFrom(requestRetiro.getFrom());
        transactionDao.setTo("CAJERO");
        transactionDao.setDate(new Date());
        return  transactionDao;
    }
}
