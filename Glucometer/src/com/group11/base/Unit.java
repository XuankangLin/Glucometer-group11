package com.group11.base;

public enum Unit {
	L,
	DL;

	public static Unit get(int ordinal) {
		return Unit.values()[ordinal];
	}
}