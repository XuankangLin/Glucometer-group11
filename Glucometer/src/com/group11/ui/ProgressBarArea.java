package com.group11.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.group11.R;
import com.group11.base.Interrupt;
import com.group11.hardware.CurrentStatus;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ProgressBarArea extends UIArea {

	private final ImageView progressBarImage;
	
	public ProgressBarArea(LinearLayout panel, ImageView progressBar) {
		super(panel);

		this.progressBarImage = progressBar;
	}
	
	private TimerTask progressTask = null;
	private int progress = 0;

	/**
	 * set the image according to @param progress
	 * @param progress is from 0~9 
	 */
	public void setProgress(int progress) {
		progress %= 10;
		switch (progress) {
		case 0:
			this.progressBarImage.setImageResource(R.drawable.progress_bar_0);
			return;
		case 1:
			this.progressBarImage.setImageResource(R.drawable.progress_bar_1);
			return;
		case 2:
			this.progressBarImage.setImageResource(R.drawable.progress_bar_2);
			return;
		case 3:
			this.progressBarImage.setImageResource(R.drawable.progress_bar_3);
			return;
		case 4:
			this.progressBarImage.setImageResource(R.drawable.progress_bar_4);
			return;
		case 5:
			this.progressBarImage.setImageResource(R.drawable.progress_bar_5);
			return;
		case 6:
			this.progressBarImage.setImageResource(R.drawable.progress_bar_6);
			return;
		case 7:
			this.progressBarImage.setImageResource(R.drawable.progress_bar_7);
			return;
		case 8:
			this.progressBarImage.setImageResource(R.drawable.progress_bar_8);
			return;
		case 9:
			this.progressBarImage.setImageResource(R.drawable.progress_bar_9);
			return;

		default:
			break;
		}
	}
	
	public void cancelProgress() {
		if (progressTask != null) {
			progressTask.cancel();
			progressTask = null;
			progress = 0;
		}
	}
	
	public void startProgress(final Handler handler, final SharedPreferences preference) {
		this.cancelProgress();
		this.progress = 0;
		progressTask = new TimerTask() {
			
			@Override
			public void run() {
				if (progress > 9) {
					Message message = Message.obtain(handler);
					CurrentStatus status = new CurrentStatus(preference);
					if (status.isResultTimeout()) {
						message.what = Interrupt.RESULT_TIMEOUT.ordinal();
					}
					else if (status.isResultOutOfRange()) {
						message.what = Interrupt.RESULT_OUT_OF_RANGE.ordinal();
					}
					else {
						message.what = Interrupt.RESULT_READY.ordinal();
					}
					message.sendToTarget();
					cancel();
				}
				else {
					progressBarImage.post(new Runnable() {
						
						@Override
						public void run() {
							setProgress(progress);
							progress++;
						}
					});
				}
			}
		};
		new Timer().scheduleAtFixedRate(progressTask, 0, 1000);
	}
}
