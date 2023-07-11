package com.atorres.nttdata.transactionmicroservice.utils;

import com.atorres.nttdata.transactionmicroservice.model.RequestTransactionAccount;
import com.atorres.nttdata.transactionmicroservice.model.RequestUpdateAccount;
import com.atorres.nttdata.transactionmicroservice.model.dao.TransactionDao;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MapperTransaction {
    public TransactionDao retiroRequestToDao(RequestTransactionAccount requestTransactionAccount, Double balance){
        TransactionDao transactionDao = new TransactionDao();
        transactionDao.setBalance(balance);
        transactionDao.setFrom(requestTransactionAccount.getAccountId());
        transactionDao.setTo("CAJERO");
        transactionDao.setCategory("RETIRO");
        transactionDao.setDate(new Date());
        return  transactionDao;
    }
    public TransactionDao depositoRequestToDao(RequestTransactionAccount request, Double balance){
        TransactionDao transactionDao = new TransactionDao();
        transactionDao.setBalance(balance);
        transactionDao.setFrom("CAJERO");
        transactionDao.setTo(request.getAccountId());
        transactionDao.setCategory("DEPOSITO");
        transactionDao.setDate(new Date());
        return  transactionDao;
    }
    public RequestUpdateAccount toRequestUpdateAccount(Double balance,String clientId,String from){
        RequestUpdateAccount request = new RequestUpdateAccount();
        request.setBalance(balance);
        request.setClientId(clientId);
        request.setAccountId(from);
        return  request;

    }
}
