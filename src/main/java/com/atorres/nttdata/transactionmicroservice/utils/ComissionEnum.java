package com.atorres.nttdata.transactionmicroservice.utils;

import java.math.BigDecimal;

public enum ComissionEnum {
	PERSONAL("personal",new BigDecimal("0.3")),
	VIP("vip",new BigDecimal("0.2")),
	MYPE("mype",new BigDecimal("0.1"));

	private final String key;
	private final BigDecimal value;

	ComissionEnum(String key, BigDecimal value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public BigDecimal getValue() {
		return value;
	}

	public static BigDecimal getValueByKey(String key) {
		for (ComissionEnum keyValue : ComissionEnum.values()) {
			if (keyValue.getKey().equals(key)) {
				return keyValue.getValue();
			}
		}
		return new BigDecimal("0.0");
	}
}
