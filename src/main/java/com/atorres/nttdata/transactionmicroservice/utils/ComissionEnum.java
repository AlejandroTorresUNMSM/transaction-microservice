package com.atorres.nttdata.transactionmicroservice.utils;

public enum ComissionEnum {
	PERSONAL("personal",0.3),
	VIP("vip",0.2),
	MYPE("vip",0.1);

	private final String key;
	private final double value;

	ComissionEnum(String key, double value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public double getValue() {
		return value;
	}

	public static double getValueByKey(String key) {
		for (ComissionEnum keyValue : ComissionEnum.values()) {
			if (keyValue.getKey().equals(key)) {
				return keyValue.getValue();
			}
		}
		return 0.0;
	}
}
