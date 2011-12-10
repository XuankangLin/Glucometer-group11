package com.group11.ui;

import com.group11.R;
import com.group11.base.Unit;
import com.group11.util.Converter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * a class containing 3 number images and 1 point image plus the unit image
 * for displaying testing results on the screen
 */
public class ResultArea {

	private final ImageView firstImage;
	private final ImageView secondImage;
	private final ImageView pointImage;
	private final ImageView thirdImage;
	private final ImageView unitImage;
	private final LinearLayout resultPanel;
	
	public ResultArea(ImageView first, ImageView second, ImageView point,
			ImageView third, ImageView unit, LinearLayout panel) {
		this.firstImage = first;
		this.secondImage = second;
		this.pointImage = point;
		this.thirdImage = third;
		this.unitImage = unit;
		this.resultPanel = panel;
	}

	/**
	 * if @param visible, the whole layout is visible
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			this.resultPanel.setVisibility(View.VISIBLE);
		}
		else {
			this.resultPanel.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * display a certain @param value in certain @param unit 
	 */
	public void display(double value, Unit unit) {
		if (unit == Unit.L) {
			this.displayL(value);
		}
		else if (unit == Unit.DL) {
			this.displayDL(value);
		}
	}
	
	private void displayL(double value) {
		this.unitImage.setImageResource(R.drawable.l);
		this.pointImage.setVisibility(View.VISIBLE);

		int[] numbers = Converter.toLNumbers(value);
		this.firstImage.setImageResource(getImageIDByValue(numbers[0]));
		this.secondImage.setImageResource(getImageIDByValue(numbers[1]));
		this.thirdImage.setImageResource(getImageIDByValue(numbers[2]));
	}
	
	private void displayDL(double value) {
		this.unitImage.setImageResource(R.drawable.dl);
		this.pointImage.setVisibility(View.INVISIBLE);

		int[] numbers = Converter.toDLNumbers(value);
		this.firstImage.setImageResource(getImageIDByValue(numbers[0]));
		this.secondImage.setImageResource(getImageIDByValue(numbers[1]));
		this.thirdImage.setImageResource(getImageIDByValue(numbers[2]));
	}
	
	private int getImageIDByValue(int value) {
		value %= 10;
		switch (value) {
		case 0:
			return R.drawable.number0;
		case 1:
			return R.drawable.number1;
		case 2:
			return R.drawable.number2;
		case 3:
			return R.drawable.number3;
		case 4:
			return R.drawable.number4;
		case 5:
			return R.drawable.number5;
		case 6:
			return R.drawable.number6;
		case 7:
			return R.drawable.number7;
		case 8:
			return R.drawable.number8;
		case 9:
			return R.drawable.number9;
		}
		return -1;
	}
}
