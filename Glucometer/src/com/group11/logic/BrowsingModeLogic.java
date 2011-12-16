package com.group11.logic;

import android.content.SharedPreferences;

import com.group11.hardware.CurrentStatus;
import com.group11.ui.DateArea;
import com.group11.ui.GlucometerActivity;
import com.group11.ui.ProgressBarArea;
import com.group11.ui.ResultArea;
import com.group11.ui.StatusArea;

/**
 * the logical controller in Browsing Mode, it judges what to do
 */

public class BrowsingModeLogic extends ModeLogic {
	public BrowsingModeLogic(StatusArea status, ResultArea result,
			ProgressBarArea progressBar, DateArea date) {
		super(status, result, progressBar, date);
	}

	@Override
	public void onShortClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLongClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDoubleClick() {
		// TODO Auto-generated method stub

	}
	//CurrentStatus currentStatus= new CurrentStatus(preferences);
}
