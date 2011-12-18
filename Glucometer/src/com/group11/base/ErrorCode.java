package com.group11.base;

public enum ErrorCode {
	//====global errors=====
	UNDEFINED(000),
	INITIALIZATION_ERROR(001),
	METER_STATUS_ERROR(002),

	//=====Testing Mode=====
	INVALID_STRIP(101),
	INSUFFICIENT_BLOOD(102),
	
	//=====Browsing Mode=====
	NO_TEST_RESULTS(201);
	
	//=====Uploading Mode=====
	
	//=====Setup Mode=====

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
