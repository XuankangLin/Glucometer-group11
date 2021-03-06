package com.group11.base;

/**
 * defines all the interrupts 
 */
public enum Interrupt {
	//============Predefined hardware interrupts===================
	STRIP_INSERTED("When a test strip is inserted"),
	STRIP_PULLED_OUT("When test strip is pulled out"),
	USB_CONNECTED("When USB is connected"),
	USB_DISCONNECTED("When USB is disconnected"),

	//=====Testing Mode Specially=====
	STRIP_VALID("If the inserted test strip is valid"),
	STRIP_INVALID("If the inserted test strip is used or out of date"),
	BLOOD_SUFFICIENT("If blood sample fed is sufficient"),
	BLOOD_INSUFFICIENT("If blood sample fed is insufficient"),
	RESULT_READY("When the test result is ready for further processing"),
	RESULT_TIMEOUT("timeout"),
	RESULT_OUT_OF_RANGE("result is out of range in current unit"),

//	BUTTON_DOWN("When the button is pushed down"),
//	BUTTON_UP("When the button is released"),
	AC_ON("When AC is plugged in"),
	AC_OFF("When AC is pulled out"),
	TIME_TICK("Every second of the timer"),
	
	//===========NEWLY defined interrupts for implementing==========
	BUTTON_CLICKED("several button_down & button_up has happened"),
	BEEP_START("beeping starts"),
	BEEP_STOP("beeping stops"),
//	POWER_ON("power on now"),
//	POWER_OFF("power off now"),
	VOLUNTARY_ENDING("I want ending"),
	ERROR_ENDING("an error occurs");
	
	private String info;
	private Interrupt(String info) {
		this.info = info;
	}
	
	@Override
	public String toString() {
		return super.toString() + "  :  " + this.info;
	}
}
