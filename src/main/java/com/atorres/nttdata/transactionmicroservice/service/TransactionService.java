package com.atorres.nttdata.transactionmicroservice.service;

import com.atorres.nttdata.transactionmicroservice.client.WebProductMicroservice;
import com.atorres.nttdata.transactionmicroservice.exception.CustomException;
import com.atorres.nttdata.transactionmicroservice.model.RequestTransaction;
import com.atorres.nttdata.transactionmicroservice.model.RequestTransactionAccount;
import com.atorres.nttdata.transactionmicroservice.model.ResponseAvgAmount;
import com.atorres.nttdata.transactionmicroservice.model.ResponseComission;
import com.atorres.nttdata.transactionmicroservice.model.dao.AccountDao;
import com.atorres.nttdata.transactionmicroservice.model.dao.TransactionDao;
import com.atorres.nttdata.transactionmicroservice.repository.TransaccionRepository;
import com.atorres.nttdata.transactionmicroservice.utils.ComissionCalculator;
import com.atorres.nttdata.transactionmicroservice.utils.MapperTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionService {
	@Autowired
	WebProductMicroservice productService;
	@Autowired
	MapperTransaction mapper;
	@Autowired
	TransaccionRepository transaccionRepository;
	@Autowired
	ComissionCalculator comissionCalculator;
	private BigDecimal comisionTransferencia;

	/**
	 * Metodo que simula un retiro por cajero
	 * @param request request
	 * @return Mono transactionDao
	 */
	public Mono<TransactionDao> retiroCuenta(RequestTransactionAccount request) {
		return productService.getAllAccountClient(request.getClientId())
						.filter(account -> account.getId().equals(request.getAccountId()))
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No existe cuenta para ese cliente")))
						.filter(accountDao -> accountDao.getBalance().compareTo(request.getAmount()) >= 0 && request.getAmount().compareTo(BigDecimal.ZERO) > 0)
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "Ingreso un monto invalido")))
						.single()
						.flatMap(account -> {
											BigDecimal balanceNuevo = account.getBalance().subtract(request.getAmount());
											return productService.updateAccount(mapper.toRequestUpdateAccount(balanceNuevo,request.getAccountId()))
															.flatMap(ac -> transaccionRepository.save(mapper.retiroRequestToDao(request, request.getAmount())));
										}
						);
	}

	/**
	 * Metodo que simula un deposito por cajero
	 * @param request request
	 * @return Mono transactionDao
	 */
	public Mono<TransactionDao> depositoCuenta(RequestTransactionAccount request) {
		return productService.getAllAccountClient(request.getClientId())
						.filter(account -> account.getId().equals(request.getAccountId()))
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No existe cuenta para ese cliente")))
						.filter(accountDao -> 0 < request.getAmount().doubleValue() && accountDao.getBalance().doubleValue() > 0)
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "Ingreso un monto invalido")))
						.single()
						.flatMap(account -> {
											BigDecimal balanceNuevo = account.getBalance().add(request.getAmount());
											account.setBalance(balanceNuevo);
											return productService.updateAccount(mapper.toRequestUpdateAccount(balanceNuevo, request.getAccountId()))
															.flatMap(ac -> transaccionRepository.save(mapper.depositoRequestToDao(request, request.getAmount())));
										}
						);
	}

	/**
	 * Metodo que simula una transferencia entre cuenta de un mismo cliente
	 * @param request request
	 * @return Mono transactionDao
	 */
	public Mono<TransactionDao> postTransferencia(RequestTransaction request) {
		return productService.getAllAccountClient(request.getClientId())
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No hay cuentas ligadas a este cliente")))
						.filter(account -> account.getId().equals(request.getTo()) || account.getId().equals(request.getFrom()))
						.collectList()
						.map(listAccount -> listAccount.stream().collect(Collectors.toMap(AccountDao::getId, cuenta -> cuenta)))
						.flatMap(mapAccount -> {
							AccountDao accountFrom = mapAccount.get(request.getFrom());
							AccountDao accountTo = mapAccount.get(request.getTo());
							return comissionCalculator.getComission(request.getClientId(), accountFrom.getId(), request.getAmount(), getAllTransactionByClientAnyMount(LocalDate.now().getMonthValue(), request.getClientId()))
											.map(value -> {
												comisionTransferencia = value;
												log.info("La comision asciende a: " + value);
												return modifyMapAccount(accountFrom,accountTo,value,request.getAmount());
											});
						})
						.map(mapAccount -> new ArrayList<>(mapAccount.values()))
						.flatMap(listAccount -> Flux.fromIterable(listAccount)
										.flatMap(account -> productService.updateAccount(mapper.toRequestUpdateAccount(account.getBalance(), account.getId())))
										.then(transaccionRepository.save(mapper.transRequestToTransDao(request, comisionTransferencia))));

	}

	/**
	 * Metodo para transferencia a terceros
	 * @param request request
	 * @return transactionDao
	 */
	public Mono<TransactionDao> getTransferenciaTerceros(RequestTransaction request){
		return productService.getAllAccountClient(request.getClientId())
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No hay cuentas ligadas a este cliente")))
						.filter(account -> account.getId().equals(request.getFrom()))
						.doOnNext(value -> log.info("cuenta encontrada "+ value.toString()))
						.single()
						.concatWith(productService.getAccount(request.getTo()))
						.collectList()
						.map(listAccount -> listAccount.stream().collect(Collectors.toMap(AccountDao::getId, cuenta -> cuenta)))
						.flatMap(mapAccount -> {
							AccountDao accountFrom = mapAccount.get(request.getFrom());
							AccountDao accountTo = mapAccount.get(request.getTo());
							return comissionCalculator.getComission(request.getClientId(), accountFrom.getId(), request.getAmount(), getAllTransactionByClientAnyMount(LocalDate.now().getMonthValue(), request.getClientId()))
											.map(value -> {
												comisionTransferencia = value;
												log.info("La comision asciende a: " + value);
												return modifyMapAccount(accountFrom,accountTo,value,request.getAmount());
											});
						})
						.map(mapAccount -> new ArrayList<>(mapAccount.values()))
						.flatMap(listAccount -> Flux.fromIterable(listAccount)
										.flatMap(account -> productService.updateAccount(mapper.toRequestUpdateAccount(account.getBalance(), account.getId())))
										.then(transaccionRepository.save(mapper.transRequestToTransDao(request, comisionTransferencia))));
	}

	/**
	 * Metodo para actualizar el Map de cuentas
	 * @param accountFrom cuenta salida
	 * @param accountTo cuenta destino
	 * @param comision comision
	 * @param amount monto
	 * @return map
	 */
	private Map<String,AccountDao> modifyMapAccount(AccountDao accountFrom,AccountDao accountTo,BigDecimal comision,BigDecimal amount){
		Map<String,AccountDao> mapAccount = new HashMap<>();
		accountFrom.setBalance(accountFrom.getBalance().subtract(amount).subtract(comision));
		accountTo.setBalance(accountTo.getBalance().add(amount));
		//Seteamos las cuentas actualizadas en el Map
		mapAccount.put(accountFrom.getId(), accountFrom);
		mapAccount.put(accountTo.getId(), accountTo);
		return mapAccount;
	}

	/**
	 * Metodo que obtiene todas las transacciones de un cliente
	 * @param clientId id del cliente
	 * @return Flux transactionDao
	 */
	public Flux<TransactionDao> getAllTransactionByClient(String clientId) {
		return transaccionRepository.findAll()
						.filter(trans -> trans.getClientId().equals(clientId));

	}

	/**
	 * Metodo que trae las transacciones de un mes en especifico de un cliente
	 * @param mounth   numero mes
	 * @param clientId id cliente
	 * @return Flux transactionDao
	 */
	public Flux<TransactionDao> getAllTransactionByClientAnyMount(int mounth, String clientId) {
		return transaccionRepository.findTransactionAnyMounth(2023, mounth)
						.filter(trans -> trans.getClientId().equals(clientId));
	}

	public Flux<TransactionDao> getCurrentMounthTrans(String clientId) {
		return transaccionRepository.findTransactionAnyMounth(2023, LocalDate.now().getMonthValue())
						.filter(trans -> trans.getClientId().equals(clientId));
	}

	/**
	 * Metodo que calcula la suma de las comision de un producto durante un mes
	 * @param clientId  id cliente
	 * @param productId id producto
	 * @return ResponseComision
	 */
	public Mono<ResponseComission> getComissionReport(String clientId, String productId) {
		return this.getAllTransactionByClientAnyMount(LocalDate.now().getMonthValue(), clientId)
						.filter(trans -> trans.getFrom().equals(productId))
						.collectList()
						.flatMap(transList -> Mono.just(transList.stream()))
						.flatMap(stream -> {
							BigDecimal totalComission = stream
											.map(trans -> Objects.requireNonNullElse(trans.getComission(), BigDecimal.ZERO))
											.reduce(BigDecimal.ZERO, BigDecimal::add);
							return Mono.just(new ResponseComission(clientId, totalComission));
						});
	}

	/**
	 * Metodo que calcula el promedio de transferencia por dia
	 * @param clientId id cliente
	 * @return ResponseAvgAmount
	 */
	public Mono<ResponseAvgAmount> getAvgAmount(String clientId) {
		return this.getAllTransactionByClientAnyMount(LocalDate.now().getMonthValue(), clientId)
						.map(TransactionDao::getBalance)
						.reduce(BigDecimal.ZERO, BigDecimal::add)
						.flatMap(totalmonto -> {
							int numeroDias = LocalDate.now().getDayOfMonth();
							log.info("Cantidad de dias: "+numeroDias);
							return Mono.just(totalmonto).map((totalMonto -> {
												log.info("Transfirio en total " + totalMonto);
												return totalMonto.divide(BigDecimal.valueOf(numeroDias), RoundingMode.HALF_UP);
											}))
											.map(mount -> new ResponseAvgAmount(clientId, mount));
						});
	}
}
