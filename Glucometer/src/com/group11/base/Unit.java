package com.group11.base;

public enum Unit {
	L,
	DL;

	public static Unit get(int ordinal) {
		for (Unit unit : Unit.values()) {
			if (unit.ordinal() == ordinal) {
				return unit;
			}
		}
		return null;
	}
}