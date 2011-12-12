package com.group11.base;

public enum Mode {
	TESTING,
	BROWSING,
	UPLOADING,
	SETUP;
	
	public static Mode get(int ordinal) {
		return values()[ordinal];
	}
}
