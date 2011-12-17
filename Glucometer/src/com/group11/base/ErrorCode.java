package com.group11.base;

public enum ErrorCode {
	INITIALIZATION_ERROR(001);

	private int errorCode;
	private ErrorCode(int code) {
		this.errorCode = code;
	}
	
	public int getErrorCode() {
		return this.errorCode;
	}
	
	@Override
	public String toString() {
		return this.errorCode + "";
	}
}
