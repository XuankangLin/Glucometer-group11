package com.group11.base;

public enum ClickStyle {
	SHORT_CLICK("Duration of button down and up is less than 0.5 second."),
	LONG_CLICK("Duration of button down and up is more than 2 seconds and les than 4 seconds."),
	DOUBLE_CLICK("Two consecutive short-clicks with interval less than 0.5 second.");
	
	private String info;
	private ClickStyle(String info) {
		this.info = info;
	}
	
	@Override
	public String toString() {
		return super.toString() + "  :  " + this.info;
	}
	
	public static ClickStyle get(int ordinal) {
		return ClickStyle.values()[ordinal];
	}
}