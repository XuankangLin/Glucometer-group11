package com.group11.hardware;

import java.util.Timer;
import java.util.TimerTask;

import com.group11.R;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Beeper only controls the beeping sound.
 * It's a singleton that is invoked by outside classes
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
	private void release() {
		if (player == null) {
			return;
		}
		
		try {
			player.stop();
		} catch (IllegalStateException e) {
			System.err.println(e);
		}
		player.release();
		player = null;
	}
	
	/**
	 * keep playing the beep sound for X milliseconds
	 * this is the only way in Beeper to start playing.
	 * @param context
	 */
	private void playXms(Context context, int milliseconds) {
		this.release();
		
		player = MediaPlayer.create(context, R.raw.beep);
		player.start();
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				release();
			}
		}, milliseconds);
	}
	
	/**
	 * a short-beep lasts 0.2 second
	 * also called remind-beep
	 * @param context
	 */
	public void doShortBeep(Context context) {
		this.playXms(context, SHORT_DURATION);
	}
	
	/**
	 * a long-beep lasts 0.5 seconds
	 * also called turn-off-beep
	 * @param context
	 */
	public void doLongBeep(Context context) {
		this.playXms(context, LONG_DURATION);
	}

	/**
	 * a double-beep is two consecutive short-beep with 0.2 second of interval
	 * also called warning beep
	 * @param context
	 */
	public void doDoubleBeep(final Context context) {
		this.doShortBeep(context);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				doShortBeep(context);
			}
		}, SHORT_DURATION + 200);
	}
	
	/**
	 * a short-long-beep is a short beep followed by a long beep 
	 * with interval less than 0.2 second
	 * also called error-beep
	 * @param context
	 */
	public void doShortLongBeep(final Context context) {
		this.doShortBeep(context);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				doLongBeep(context);
			}
		}, SHORT_DURATION + 150);
	}
}
