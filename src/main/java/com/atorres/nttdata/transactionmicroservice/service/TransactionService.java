package com.atorres.nttdata.transactionmicroservice.service;

import com.atorres.nttdata.transactionmicroservice.client.WebClientMicroservice;
import com.atorres.nttdata.transactionmicroservice.client.WebProductMicroservice;
import com.atorres.nttdata.transactionmicroservice.exception.CustomException;
import com.atorres.nttdata.transactionmicroservice.model.RequestRetiro;
import com.atorres.nttdata.transactionmicroservice.model.RequestUpdateAccount;
import com.atorres.nttdata.transactionmicroservice.model.dao.TransactionDao;
import com.atorres.nttdata.transactionmicroservice.repository.TransaccionRepository;
import com.atorres.nttdata.transactionmicroservice.utils.MapperTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {

    @Autowired
    WebProductMicroservice productService;
    @Autowired
    WebClientMicroservice clientService;

    @Autowired
    MapperTransaction mapper;
    @Autowired
    TransaccionRepository transaccionRepository;

    public Mono<TransactionDao> retiroCuenta(RequestRetiro request) {
        return productService.getAllAccountClient(request.getClientId())
                .filter(account -> account.getId().equals(request.getFrom()))
                .filter(accountDao -> accountDao.getBalance() >= request.getBalance())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No existe la relacion client-producto")))
                .single()
                .flatMap(account -> {
                            Double balanceNuevo = account.getBalance() - request.getBalance();
                            account.setBalance(balanceNuevo);
                            RequestUpdateAccount requestUpdateAccount = new RequestUpdateAccount();
                            requestUpdateAccount.setBalance(balanceNuevo);
                            requestUpdateAccount.setClientId(request.getClientId());
                            requestUpdateAccount.setAccountId(request.getFrom());
                            return productService.updateAccount(requestUpdateAccount)
                                    .flatMap( ac ->{
                                        return transaccionRepository.save(mapper.retiroRequestToDao(request,balanceNuevo));
                                    });
                        }
                );
    }
}
