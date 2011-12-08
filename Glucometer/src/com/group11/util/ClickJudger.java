package com.group11.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.group11.base.ClickStyle;
import com.group11.base.Interrupt;

import android.os.Handler;
import android.os.Message;

/**
 * it deals with the interrupts sent when the sole button is clicked
 * and decides what kind of button click it is.
 */
public class ClickJudger {

	private Handler handler;
	
	public ClickJudger(Handler handler) {
		this.handler = handler;
	}
	
	/**
	 * Short-click: Duration of button down and up is less than 0.5 second.
	 */
	private static final long SHORT_DURATION = 500;
	/**
	 * Long-click: Duration of button down and up is more than 2 seconds and less than 4 seconds.
	 */
	private static final long LONG_DURATION_MIN = 2000;
	/**
	 * Long-click: Duration of button down and up is more than 2 seconds and less than 4 seconds.
	 */
	private static final long LONG_DURATION_MAX = 4000;
	/**
	 * Double-click: Two consecutive short-clicks with interval less than 0.5 second.
	 */
	private static final long DOUBLE_INTERVAL = 500;
	
	/**
	 * last time's click's button_up, !=0 means
	 * last click should be considered whether it's a double-click
	 */
	private long lastShortUp = 0;
	/**
	 * this time's click's button_down
	 */
	private long thisDown;
	private TimerTask lastShortTask = null;
	
	private void resetLastShortTask() {
		lastShortTask = new TimerTask() {
			
			@Override
			public void run() {
				sendButtonClicked(ClickStyle.SHORT_CLICK);
			}
		};
	}
	
	public void buttonDown(Date time) {
		if (time == null) {
			throw new IllegalArgumentException("@param time should not be null");
		}
		
		thisDown = time.getTime();
		if (thisDown - lastShortUp < DOUBLE_INTERVAL) {
			/*
			 * if the interval between last click and this click < DOUBLE_INTERVAL
			 * the click may be a double-click, so let's postpone the TimerTask and see.
			 */
			lastShortTask.cancel();
			this.resetLastShortTask();
			new Timer().schedule(lastShortTask, SHORT_DURATION);
		}
	}
	
	public void buttonUp(Date time) {
		if (time == null) {
			throw new IllegalArgumentException("@param time should not be null");
		}
		
		long thisUp = time.getTime();
		//this click's duration
		long duration = thisUp - thisDown;
		//interval from last short click's button_up to this click's button_down
		long interval = thisDown - lastShortUp;

		if (duration > LONG_DURATION_MIN && duration < LONG_DURATION_MAX) {
			/*
			 * there is only one possibility -> long-click
			 * and if last click is a short click, it should have sent out the message.
			 */
			this.sendButtonClicked(ClickStyle.LONG_CLICK);
		}
		else if (duration < 500) {
			if (interval < 500) {
				/*
				 * it must be a double-click, so cancel the last Short-click message
				 * resend a double-click message!
				 */
				lastShortTask.cancel();
				lastShortTask = null;
				this.sendButtonClicked(ClickStyle.DOUBLE_CLICK);
				lastShortUp = 0;
			}
			else {
				/*
				 * it is a Short-click temporally, let's wait DOUBLE_INTERVAL milliseconds
				 * to see whether it may be a Double-click
				 */
				this.resetLastShortTask();
				new Timer().schedule(lastShortTask, DOUBLE_INTERVAL);
				lastShortUp = thisUp;
			}
		}
	}
	
	private void sendButtonClicked(ClickStyle style) {
		Message message = Message.obtain(handler,
				Interrupt.BUTTON_CLICKED.ordinal());	//msg.what
		message.arg1 = style.ordinal();						//msg.arg1
		message.sendToTarget();
	}
}
