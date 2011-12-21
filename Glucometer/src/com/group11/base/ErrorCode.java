package com.group11.base;

public enum ErrorCode {
	//====global errors=====
	UNDEFINED(000),
	INITIALIZATION_ERROR(001),
	METER_STATUS_ERROR(002),

	//=====Testing Mode=====
	INVALID_STRIP(101),
	INSUFFICIENT_BLOOD(102),
	TEST_TIMEOUT(103),
	RESULT_OUT_OF_RANGE(104),
	
	//=====Browsing Mode=====
	B_NO_TEST_RESULTS(201),
	
	//=====Uploading Mode=====
	U_NO_TEST_RESULTS(301),
	PC_NOT_READY(302),
	SOFTWARE_NOT_READY(303);
	
	
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
