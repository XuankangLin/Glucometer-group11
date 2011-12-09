package com.group11.ui;

import com.group11.base.Unit;

import android.widget.ImageView;

/**
 * a class containing 3 number images and 1 point image
 * for displaying testing results on the screen
 * @author Andriy
 *
 */
public class ResultArea {

	private final ImageView firstImage;
	private final ImageView secondImage;
	private final ImageView pointImage;
	private final ImageView thirdImage;
	
	public ResultArea(ImageView first, ImageView second, ImageView point, ImageView third) {
		this.firstImage = first;
		this.secondImage = second;
		this.pointImage = point;
		this.thirdImage = third;
	}
	
	public void display(double value, Unit unit) {
		if (unit == Unit.L) {
			this.displayL(value);
		}
		else if (unit == Unit.DL) {
			this.displayDL(value);
		}
	}
	
	private void displayL(double value) {
		//TODO fulfill this method
	}
	
	private void displayDL(double value) {
		//TODO fulfill this method
	}
}
