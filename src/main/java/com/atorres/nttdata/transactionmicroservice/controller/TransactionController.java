package com.atorres.nttdata.transactionmicroservice.controller;

import com.atorres.nttdata.transactionmicroservice.model.RequestTransaction;
import com.atorres.nttdata.transactionmicroservice.model.RequestTransactionAccount;
import com.atorres.nttdata.transactionmicroservice.model.ResponseAvgAmount;
import com.atorres.nttdata.transactionmicroservice.model.ResponseComission;
import com.atorres.nttdata.transactionmicroservice.model.dao.TransactionDao;
import com.atorres.nttdata.transactionmicroservice.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transaction")
@Slf4j
public class TransactionController {
  /**.
   * Servicio transacciones
   */
  @Autowired
  TransactionService transactionService;

  /**.
   * Metodo para hacer retiro desde un cajero
   * @param request request
   * @return transactionDao
   */
  @PostMapping(value = "/account/retiro", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Mono<TransactionDao> retiroCuenta(@RequestBody RequestTransactionAccount request) {
    return transactionService.retiroCuenta(request)
            .doOnSuccess(v -> log.info("Retiro de cajero exitoso"));
  }

  /**.
   * Metodo para hacer deposito desde un cajero
   * @param request request
   * @return TransactionDao
   */
  @PostMapping(value = "/account/deposito", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Mono<TransactionDao> depositoCuenta(@RequestBody RequestTransactionAccount request) {
    return transactionService.depositoCuenta(request)
            .doOnSuccess(v -> log.info("Deposito de cajero exitoso"));
  }

  /**.
   * Metodo para hacer transferencia entre mis cuentas
   * @param request request
   * @return TransactionDao
   */
  @PostMapping(value = "/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Mono<TransactionDao> transferencia(@RequestBody RequestTransaction request) {
    return transactionService.postTransferencia(request)
            .doOnSuccess(v -> log.info("Transferencia entre tus cuentas exitosa"));
  }

    /**
     * Metodo para hacer transferencias a tercerso
     * @param request request
     * @return transaction
     */
  @PostMapping(value = "/terceros", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Mono<TransactionDao> transferenciaTerceros(@RequestBody RequestTransaction request) {
    return transactionService.getTransferenciaTerceros(request)
            .doOnSuccess(v -> log.info("Transferencia a terceros exitosa"));
  }

  /**.
   * Metodo que trae todas las transferencias de un cliente
   * @param clientId id del cliente
   * @return Flux de TransactionDao
   */
  @GetMapping(value = "/all/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<TransactionDao> allTransaction(@PathVariable String clientId) {
    return transactionService.getAllTransactionByClient(clientId)
            .doOnNext(v -> log.info("Transferencia encontrada: " + v.getId()));
  }

  /**.
   * Metodo que trae todas las transferencia de este mes del cliente
   * @param clientId id del cliente
   * @return Flux de TransactionDao
   */
  @GetMapping(value = "/thismounth/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<TransactionDao> allTransactionthisMounth(@PathVariable String clientId) {
    return transactionService.getAllTransactionByClientAnyMount(LocalDate.now().getMonthValue(), clientId)
            .doOnNext(v -> log.info("Transferencia de este mes: " + v.getId()));
  }

  /**.
   * Metodo que trae todas las transferencia de cualquier mes del cliente
   * @param mounth   numero del mes
   * @param clientId id del cliente
   * @return Flux de TransactionDao
   */
  @GetMapping(value = "/anymounth/{clientId}/{mounth}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<TransactionDao> allTransactionAnyMounth(
          @PathVariable String clientId,
          @PathVariable int mounth) {
    return transactionService.getAllTransactionByClientAnyMount(mounth, clientId)
            .doOnNext(v -> log.info("Transferencia del mes {} : {}", mounth, v.getId()));
  }

  /**.
   * Metodo que retorna toda la comision cobrada a un producto durante el mes actual
   * @param clientId  id del cliento
   * @param productId id del producto
   * @return ResponseComission
   */
  @GetMapping(value = "/comission/{clientId}/{productId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Mono<ResponseComission> getComissionProduct(
          @PathVariable String clientId,
          @PathVariable String productId) {
    return transactionService.getComissionReport(clientId, productId)
            .doOnSuccess(v -> log.info("Comision del mes asciende a: {}", v.getComissionTotal()));
  }

  /**.
   * Metodo que calcula el promedio de montos transferidos por dia para todos los producto del cliente
   * @param clientId id cliente
   * @return ResponseAvgAmount
   */
  @GetMapping(value = "/avg/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Mono<ResponseAvgAmount> getAvgAmount(
          @PathVariable String clientId) {
    return transactionService.getAvgAmount(clientId)
            .doOnSuccess(v -> log.info("Transfirio en promedio " + v.getAvgAmount() + " por dia"));
  }
}
