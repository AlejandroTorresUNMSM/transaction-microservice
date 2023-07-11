package com.atorres.nttdata.transactionmicroservice.controller;

import com.atorres.nttdata.transactionmicroservice.model.RequestTransaction;
import com.atorres.nttdata.transactionmicroservice.model.RequestTransactionAccount;
import com.atorres.nttdata.transactionmicroservice.model.dao.TransactionDao;
import com.atorres.nttdata.transactionmicroservice.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/transaction")
@Slf4j
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    @PostMapping("/account/retiro")
    public Mono<TransactionDao> retiroCuenta(@RequestBody RequestTransactionAccount request){
        return transactionService.retiroCuenta(request);
    }

    @PostMapping("/account/deposito")
    public Mono<TransactionDao> depositoCuenta(@RequestBody RequestTransactionAccount request){
        return transactionService.depositoCuenta(request);
    }

    @PostMapping("")
    public Mono<TransactionDao> transferencia(@RequestBody RequestTransaction request){
        return transactionService.postTransferencia(request);
    }

    @GetMapping("/all/{clientId}")
    public Flux<TransactionDao> allTransaction(@PathVariable String clientId){
        return transactionService.getAllTransactionByClient(clientId);
    }

}
