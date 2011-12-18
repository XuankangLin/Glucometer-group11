package com.group11.hardware;

import java.util.Timer;
import java.util.TimerTask;

import com.group11.R;
import com.group11.base.Interrupt;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

/**
 * Beeper only controls the beeping sound.
 * It's a singleton that is invoked by outside classes
 * 
 * Just use the 4 doXXBeep() methods
 * 
 * @author Andriy_Lin
 */
public class Beeper {

	private static Beeper instance = null;
	public static Beeper get() {
		if (instance == null) {
			instance = new Beeper();
		}
		return instance;
	}
	
	private Beeper() {	}
	
	private Handler handler = null;
	public void attachHandler(Handler handler) {
		this.handler = handler;
	}
	
	/**
	 * the duration of a short beep in milliseconds
	 */
	private static final int SHORT_DURATION = 200;
	/**
	 * the duration of a long beep in milliseconds
	 */
	private static final int LONG_DURATION = 500;
	
	
	private MediaPlayer player = null;
	
	/**
	 * called every time when it wants to play something new or finishes playing 
	 */
	private synchronized void release() {
		if (player == null) {
			return;
		}
		
		try {
			player.stop();
		} catch (IllegalStateException e) {
			System.err.println(e);
		}
		this.sendBeepStartStop(false);
		player.release();
		player = null;
	}
	
	/**
	 * keep playing the beep sound for X milliseconds
	 * this is the only way in Beeper to start playing.
	 */
	private synchronized void playXms(Context context, int milliseconds) {
		this.release();
		
		player = MediaPlayer.create(context, R.raw.beep);
		player.start();
		this.sendBeepStartStop(true);
		
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				release();
			}
		}, milliseconds);
	}
	
	private void doShortBeep(Context context) {
		this.playXms(context, SHORT_DURATION);
	}
	
	/**
	 * a short-beep lasts 0.2 seconds
	 * also called remind-beep
	 * @param context  passing in the Activity instance is OK
	 */
	public void doRemindBeep(Context context) {
		this.doShortBeep(context);
	}
	
	public void doLongBeep(Context context) {
		this.playXms(context, LONG_DURATION);
	}
	
	/**
	 * a long-beep lasts 0.5 seconds
	 * also called turn-off-beep
	 * @param context  passing in the Activity instance is OK
	 */
	public void doTurnOffBeep(Context context) {
		this.doLongBeep(context);
	}

	public void doDoubleBeep(final Context context) {
		this.doShortBeep(context);
		
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				doShortBeep(context);
			}
		}, SHORT_DURATION + 200);
	}
	
	/**
	 * a double-beep is two consecutive short-beep with 0.2 seconds of interval
	 * also called warning beep
	 * @param context  passing in the Activity instance is OK
	 */
	public void doWarningBeep(Context context) {
		this.doDoubleBeep(context);
	}
	
	private void doShortLongBeep(final Context context) {
		this.doShortBeep(context);
		
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				doLongBeep(context);
			}
		}, SHORT_DURATION + 150);
		
		sendBeepStartStop(true);
	}
	
	/**
	 * a short-long-beep is a short beep followed by a long beep 
	 * with interval less than 0.2 seconds
	 * also called error-beep
	 * @param context  passing in the Activity instance is OK
	 */
	public void doErrorBeep(Context context) {
		this.doShortLongBeep(context);
	}
	
	private synchronized void sendBeepStartStop(boolean start) {
		Message message = Message.obtain(handler, 
				start ? Interrupt.BEEP_START.ordinal() : Interrupt.BEEP_STOP.ordinal());
		message.sendToTarget();
	}
}
