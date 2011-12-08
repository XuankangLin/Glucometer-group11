package com.group11.base;

public enum ErrorCode {
	ERROR1("blabla"),
	ERROR2("blablablabla");

	private String info;
	private ErrorCode(String info) {
		this.info = info;
	}
	
	@Override
	public String toString() {
		return this.info;
	}
}
