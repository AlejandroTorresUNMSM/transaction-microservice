package com.atorres.nttdata.transactionmicroservice.utils;

import com.atorres.nttdata.transactionmicroservice.client.WebProductMicroservice;
import com.atorres.nttdata.transactionmicroservice.exception.CustomException;
import com.atorres.nttdata.transactionmicroservice.model.dao.TransactionDao;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@Component
@Log4j2
public class ComissionCalculator {
	@Autowired
	WebProductMicroservice productService;

	/**
	 * Metodo que calcula la comision
	 * @param idClient id cliente
	 * @param idAccount id cuenta
	 * @param amount monto
	 * @param listTrans lista transferencias del mes
	 * @return BigDecimal
	 */
	public Mono<BigDecimal> getComission(String idClient, String idAccount, BigDecimal amount,Flux<TransactionDao> listTrans) {
		return productService.getAllAccountClient(idClient)
						.filter(account -> account.getId().equals(idAccount))
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No existe la cuenta")))
						.single()
						.flatMap(account -> getLimitTransaction(listTrans,idAccount)
										.flatMap(value -> value ? Mono.just(new BigDecimal("0.0")) : Mono.just(getCommisionValue(account.getAccountCategory().toString())))
										.flatMap(value -> Mono.just(amount.multiply(value))));
	}

	/**
	 * Metodo que retorna el porcentaje de la comision
	 * @param tipo tipo
	 * @return BigDecimal
	 */
	static BigDecimal getCommisionValue(String tipo) {
		log.info("Le toca comision tipo: " + tipo);
		return ComissionEnum.getValueByKey(tipo);
	}

	/**
	 * Metodo que evalua el limite de transacciones por cuenta para cobrar una comision
	 * @param listTransaction lista de transacciones
	 * @return boolean
	 */
	static Mono<Boolean> getLimitTransaction(Flux<TransactionDao> listTransaction,String idAccount) {
		return listTransaction
						.filter(trans -> trans.getFrom().equals(idAccount))
						.count()
						.flatMap(cant -> {
							if(cant<=5){
								log.info("No excedio el limite de transacciones gratuitas cant:"+cant);
								return Mono.just(true);
							}else{
								log.info("Excedio el limite de transaccion gratuitas cant:"+cant);
								return Mono.just(false);
							}
						});
	}
}
