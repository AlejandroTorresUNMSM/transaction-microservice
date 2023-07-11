package com.atorres.nttdata.transactionmicroservice.service;

import com.atorres.nttdata.transactionmicroservice.client.WebClientMicroservice;
import com.atorres.nttdata.transactionmicroservice.client.WebProductMicroservice;
import com.atorres.nttdata.transactionmicroservice.exception.CustomException;
import com.atorres.nttdata.transactionmicroservice.model.RequestTransactionAccount;
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

    public Mono<TransactionDao> retiroCuenta(RequestTransactionAccount request) {
        return productService.getAllAccountClient(request.getClientId())
                .filter(account -> account.getId().equals(request.getAccountId()))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No existe cuenta para ese cliente")))
                .filter(accountDao -> accountDao.getBalance() >= request.getAmount() && request.getAmount()>0)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "Ingreso un monto invalido")))
                .single()
                .flatMap(account -> {
                            Double balanceNuevo = account.getBalance() - request.getAmount();
                            return productService.updateAccount(mapper.toRequestUpdateAccount(balanceNuevo,request.getClientId(), request.getAccountId()))
                                    .flatMap( ac ->transaccionRepository.save(mapper.retiroRequestToDao(request,request.getAmount())));
                        }
                );
    }

    public Mono<TransactionDao> depositoCuenta(RequestTransactionAccount request){
        return productService.getAllAccountClient(request.getClientId())
                .filter(account -> account.getId().equals(request.getAccountId()))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No existe cuenta para ese cliente")))
                .filter(accountDao -> 0 < request.getAmount() && accountDao.getBalance()>0)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "Ingreso un monto invalido")))
                .single()
                .flatMap(account -> {
                            Double balanceNuevo = account.getBalance() + request.getAmount();
                            account.setBalance(balanceNuevo);
                            return productService.updateAccount(mapper.toRequestUpdateAccount(balanceNuevo,request.getClientId(), request.getAccountId()))
                                    .flatMap( ac ->{
                                        return transaccionRepository.save(mapper.depositoRequestToDao(request,request.getAmount()));
                                    });
                        }
                );
    }
}
