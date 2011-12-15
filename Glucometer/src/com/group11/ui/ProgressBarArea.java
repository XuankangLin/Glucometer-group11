package com.group11.ui;

import com.group11.R;

import android.widget.ImageView;
import android.widget.LinearLayout;

public class ProgressBarArea extends UIArea {

	private final ImageView progressBarImage;
	
	public ProgressBarArea(LinearLayout panel, ImageView progressBar) {
		super(panel);

		this.progressBarImage = progressBar;
	}

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
}
