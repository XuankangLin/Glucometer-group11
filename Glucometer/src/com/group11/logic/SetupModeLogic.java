package com.group11.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.group11.ui.DateArea;
import com.group11.ui.ProgressBarArea;
import com.group11.ui.ResultArea;
import com.group11.ui.StatusArea;

/**
 * the logical controller in Setup Mode, it judges what to do
 */
public class SetupModeLogic extends ModeLogic {

	public SetupModeLogic(StatusArea status, ResultArea result,
			ProgressBarArea progressBar, DateArea date, Context context,
			SharedPreferences preferences, Handler handler) {
		super(status, result, progressBar, date, context, preferences, handler);
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

}
