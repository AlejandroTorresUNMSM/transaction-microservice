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

    /**
     * Metodo para hacer retiro desde un cajero
     * @param request request
     * @return transactionDao
     */
    @PostMapping("/account/retiro")
    public Mono<TransactionDao> retiroCuenta(@RequestBody RequestTransactionAccount request){
        return transactionService.retiroCuenta(request)
                .doOnSuccess(v -> log.info("Retiro de cajero exitoso"));
    }

    /**
     * Metodo para hacer deposito desde un cajero
     * @param request request
     * @return TransactionDao
     */
    @PostMapping("/account/deposito")
    public Mono<TransactionDao> depositoCuenta(@RequestBody RequestTransactionAccount request){
        return transactionService.depositoCuenta(request)
                .doOnSuccess(v -> log.info("Deposito de cajero exitoso"));
    }

    /**
     * Metodo para hacer transferencia entre mis cuentas
     * @param request request
     * @return TransactionDao
     */
    @PostMapping("")
    public Mono<TransactionDao> transferencia(@RequestBody RequestTransaction request){
        return transactionService.postTransferencia(request)
                .doOnSuccess(v -> log.info("Transferencia entre tus cuentas exitosa"));
    }

    /**
     * Metodo que trae todas las transferencias de un cliente
     * @param clientId id del cliente
     * @return Flux de TransactionDao
     */
    @GetMapping("/all/{clientId}")
    public Flux<TransactionDao> allTransaction(@PathVariable String clientId){
        return transactionService.getAllTransactionByClient(clientId)
                .doOnNext(v-> log.info("Transferencia encontrada"+v.getId()));
    }

    /**
     * Metodo que trae todas las transferencia de este mes del cliente
     * @param clientId id del cliente
     * @return Flux de TransactionDao
     */
    @GetMapping("/all/thismounth/{clientId}")
    public Flux<TransactionDao> allTransactionthisMounth(@PathVariable String clientId){
        return transactionService.getAllTransactionByClientThisMount(clientId)
                .doOnNext(v-> log.info("Transferencia de este mes: "+v.getId()));
    }


}
