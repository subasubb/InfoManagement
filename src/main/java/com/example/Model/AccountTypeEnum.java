package com.example.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountTypeEnum {

	ADMIN("ADMIN"),

	L1USER("L1USER"),

	L2USER("L2USER");

	private String value;

	AccountTypeEnum(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static AccountTypeEnum fromValue(String text) {
		for (AccountTypeEnum b : AccountTypeEnum.values()) {
			if (String.valueOf(b.value).equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}