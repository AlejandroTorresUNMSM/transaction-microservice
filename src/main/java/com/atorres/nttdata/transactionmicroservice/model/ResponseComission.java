package com.atorres.nttdata.transactionmicroservice.model;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class ResponseComission {
	private String productId;
	private BigDecimal comissionTotal;

	public ResponseComission(String productId,BigDecimal commisionTotal){
		this.productId = productId;
		this.comissionTotal = commisionTotal;
	}
}
