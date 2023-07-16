package com.atorres.nttdata.transactionmicroservice.utils;

import com.atorres.nttdata.transactionmicroservice.model.RequestTransaction;
import com.atorres.nttdata.transactionmicroservice.model.RequestTransactionAccount;
import com.atorres.nttdata.transactionmicroservice.model.RequestUpdateAccount;
import com.atorres.nttdata.transactionmicroservice.model.dao.TransactionDao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class MapperTransaction {
    public TransactionDao retiroRequestToDao(RequestTransactionAccount requestTransactionAccount, BigDecimal balance){
        TransactionDao transactionDao = new TransactionDao();
        transactionDao.setBalance(balance);
        transactionDao.setFrom(requestTransactionAccount.getAccountId());
        transactionDao.setTo("CAJERO");
        transactionDao.setCategory("RETIRO");
        transactionDao.setDate(new Date());
        transactionDao.setComission(new BigDecimal("0.5"));
        transactionDao.setClientId(requestTransactionAccount.getClientId());
        return  transactionDao;
    }
    public TransactionDao depositoRequestToDao(RequestTransactionAccount request, BigDecimal balance){
        TransactionDao transactionDao = new TransactionDao();
        transactionDao.setBalance(balance);
        transactionDao.setFrom("CAJERO");
        transactionDao.setTo(request.getAccountId());
        transactionDao.setCategory("DEPOSITO");
        transactionDao.setDate(new Date());
        transactionDao.setComission(new BigDecimal("10.5"));
        transactionDao.setClientId(request.getClientId());
        return  transactionDao;
    }
    public RequestUpdateAccount toRequestUpdateAccount(BigDecimal balance,String clientId,String from){
        RequestUpdateAccount request = new RequestUpdateAccount();
        request.setBalance(balance);
        request.setClientId(clientId);
        request.setAccountId(from);
        return  request;
    }
    public TransactionDao transRequestToTransDao(RequestTransaction request){
        TransactionDao trans = new TransactionDao();
        trans.setCategory("TRANSFERENCIA");
        trans.setFrom(request.getFrom());
        trans.setTo(request.getFrom());
        trans.setBalance(request.getAmount());
        trans.setDate(new Date());
        trans.setComission(new BigDecimal("20"));
        trans.setClientId(request.getClientId());
        return  trans;
    }

}
