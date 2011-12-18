package com.group11.ui;

import java.util.Timer;
import java.util.TimerTask;

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
public class ResultArea extends UIArea {

	private final ImageView firstImage;
	private final ImageView secondImage;
	private final ImageView pointImage;
	private final ImageView thirdImage;
	private final ImageView unitImage;
	
	private TimerTask unitBlinkingTask = null;
	
	public ResultArea(LinearLayout panel, ImageView first, ImageView second, ImageView point,
			ImageView third, ImageView unit) {
		super(panel);

		this.firstImage = first;
		this.secondImage = second;
		this.pointImage = point;
		this.thirdImage = third;
		this.unitImage = unit;
	}
	
	/**
	 * display a certain @param value in certain @param unit 
	 */
	public void displayResult(double value, Unit unit) {
		if (unit == Unit.L) {
			this.displayL(value);
		}
		else if (unit == Unit.DL) {
			this.displayDL(value);
		}
		this.unitImage.setVisibility(View.VISIBLE);
		this.firstImage.setVisibility(View.VISIBLE);
		this.secondImage.setVisibility(View.VISIBLE);
		this.thirdImage.setVisibility(View.VISIBLE);
	}
	
	/**
	 * display an error code
	 */
	public void displayError(int errorCode) {
		this.pointImage.setVisibility(View.INVISIBLE);
		this.unitImage.setVisibility(View.INVISIBLE);
		this.firstImage.setVisibility(View.VISIBLE);
		this.secondImage.setVisibility(View.VISIBLE);
		this.thirdImage.setVisibility(View.VISIBLE);
		
		int[] numbers = Converter.toErrorCodeNumbers(errorCode);
		this.firstImage.setImageResource(Converter.toImageIDByValue(numbers[0]));
		this.secondImage.setImageResource(Converter.toImageIDByValue(numbers[1]));
		this.thirdImage.setImageResource(Converter.toImageIDByValue(numbers[2]));
	}
	
	private void displayL(double value) {
		this.unitImage.setImageResource(R.drawable.l);
		this.pointImage.setVisibility(View.VISIBLE);

		int[] numbers = Converter.toLNumbers(value);
		this.firstImage.setImageResource(Converter.toImageIDByValue(numbers[0]));
		this.secondImage.setImageResource(Converter.toImageIDByValue(numbers[1]));
		this.thirdImage.setImageResource(Converter.toImageIDByValue(numbers[2]));
	}
	
	private void displayDL(double value) {
		this.unitImage.setImageResource(R.drawable.dl);
		this.pointImage.setVisibility(View.INVISIBLE);

		int[] numbers = Converter.toDLNumbers(value);
		this.firstImage.setImageResource(Converter.toImageIDByValue(numbers[0]));
		this.secondImage.setImageResource(Converter.toImageIDByValue(numbers[1]));
		this.thirdImage.setImageResource(Converter.toImageIDByValue(numbers[2]));
	}
	
	
	private void clearUnitBlinkingTask() {
		if (unitBlinkingTask != null) {
			unitBlinkingTask.cancel();
			unitBlinkingTask = null;
		}
	}
	
	private void initUnitBlinkingTask() {
		unitBlinkingTask = new TimerTask() {
			
			@Override
			public void run() {
				unitImage.post(new Runnable() {
					
					@Override
					public void run() {
						if (unitImage.getVisibility() == View.VISIBLE) {
							unitImage.setVisibility(View.INVISIBLE);
						}
						else {
							unitImage.setVisibility(View.VISIBLE);
						}
					}
				});
			}
		};
	}
	
	public void setUnitBlinking(boolean blinking) {
		this.clearUnitBlinkingTask();
		if (blinking) {
			this.initUnitBlinkingTask();
			new Timer().scheduleAtFixedRate(unitBlinkingTask, 0, 1000);
		}
	}

	/**
	 * only unit image is visible
	 */
	public void setOnlyDisplayUnit() {
		this.firstImage.setVisibility(View.INVISIBLE);
		this.secondImage.setVisibility(View.INVISIBLE);
		this.pointImage.setVisibility(View.INVISIBLE);
		this.thirdImage.setVisibility(View.INVISIBLE);
		this.unitImage.setVisibility(View.VISIBLE);
	}

	public void setUnit(Unit unit) {
		if (unit == Unit.L) {
			this.unitImage.setImageResource(R.drawable.l);
		}
		else if (unit == Unit.DL) {
			this.unitImage.setImageResource(R.drawable.dl);
		}
	}
}
