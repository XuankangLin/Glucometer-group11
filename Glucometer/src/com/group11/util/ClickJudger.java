package com.group11.util;

import java.util.Date;

import android.os.Handler;

/**
 * it deals with the interrupts sent when the sole button is clicked
 * and decides what kind of button click it is.
 */
public class ClickJudger {

	private Handler handler;
	
	public ClickJudger(Handler handler) {
		this.handler = handler;
	}
	
	public void buttonDown(Date time) {
		//TODO
	}
	
	public void buttonUp(Date time) {
		//TODO
	}
	
	public static enum ClickStyle {
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
	}
}
