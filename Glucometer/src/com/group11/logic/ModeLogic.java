package com.group11.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.group11.ui.DateArea;
import com.group11.ui.ProgressBarArea;
import com.group11.ui.ResultArea;
import com.group11.ui.StatusArea;

/**
 * a abstract class standardizing the interface of logical controllers
 */
public abstract class ModeLogic {

	protected final StatusArea statusArea;
	protected final ResultArea resultArea;
	protected final ProgressBarArea progressBarArea;
	protected final DateArea dateArea;

	protected final Context context;
	protected final SharedPreferences preferences;
	protected final Handler handler;

	public ModeLogic(StatusArea status, ResultArea result,
			ProgressBarArea progressBar, DateArea date, Context context,
			SharedPreferences preferences, Handler handler) {
		this.statusArea = status;
		this.resultArea = result;
		this.progressBarArea = progressBar;
		this.dateArea = date;
		this.context = context;
		this.preferences = preferences;
		this.handler = handler;
	}

	/**
	 * when short-clicked, what should be done?
	 */
	public abstract void onShortClick();

	/**
	 * when long-clicked, what should be done?
	 */
	public abstract void onLongClick();

	/**
	 * when double-clicked, what should be done?
	 */
	public abstract void onDoubleClick();
}
