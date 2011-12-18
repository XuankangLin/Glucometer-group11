package com.group11.logic;

import java.util.LinkedList;

import android.content.SharedPreferences;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.group11.base.ErrorCode;
import com.group11.base.Interrupt;
import com.group11.base.Mode;
import com.group11.base.TestResult;
import com.group11.base.Unit;
import com.group11.hardware.Beeper;
import com.group11.hardware.CurrentStatus;

import com.group11.ui.DateArea;
import com.group11.ui.ProgressBarArea;
import com.group11.ui.ResultArea;
import com.group11.ui.StatusArea;
import com.group11.util.Converter;
import com.group11.util.HistoryManager;

/**
 * the logical controller in Browsing Mode, it judges what to do
 */

public class BrowsingModeLogic extends ModeLogic {

	public BrowsingModeLogic(StatusArea status, ResultArea result,
			ProgressBarArea progressBar, DateArea date, Context context,
			SharedPreferences preferences, Handler handler) {
		super(status, result, progressBar, date, context, preferences, handler);
	}

	private HistoryManager historyManager = new HistoryManager(context);
	private LinkedList<TestResult> resultList = historyManager.getTestResults();
	private int position = resultList.size() - 1;

	/**
	 * Mode Validation in Browsing Mode:
	 * check whether there are any history test records
	 */
	@Override
	public void validateMode() {
		super.validateMode();

		if (resultList.size() > 0) {
			//=====the meter should give a reminding-beep,===== 
			//=====display symbol B, and enter the Browsing Mode.=====
			CurrentStatus currentStatus = new CurrentStatus(preferences);
			currentStatus.setPowerOn(true);
			currentStatus.setCurrentMode(Mode.BROWSING);
			currentStatus.setRefreshingTime(false);
			currentStatus.commit();

			Beeper.get().doShortBeep(context);
			
			statusArea.setVisible(true);
			resultArea.setVisible(true);
			progressBarArea.setVisible(false);
			dateArea.setVisible(true);

			statusArea.setCurrentMode(Mode.BROWSING);
			dateArea.setColonBlinking(false);
			
			this.displayTestResult(resultList.getLast());
		}
		else {
			//=====the meter shall blink symbol B and then go through Error Ending procedure=====
			statusArea.setVisible(true);
			statusArea.setCurrentMode(Mode.BROWSING);
			statusArea.cancelBlinking();
			statusArea.setModeBlinking(Mode.BROWSING);

			dateArea.setVisible(true);
			dateArea.setColonBlinking(true);
			
			Message message = Message.obtain(handler, Interrupt.ERROR.ordinal());
			message.arg1 = ErrorCode.NO_TEST_RESULTS.getErrorCode();
			message.sendToTarget();
		}
	}
	
	private void displayTestResult(TestResult testResult) {
		Unit unit = new CurrentStatus(preferences).getCurrentUnit();
		
		resultArea.displayResult(Converter.to(testResult.getValue(),
				testResult.getUnit(), unit), unit);
		dateArea.setDateTime(testResult.getTime());
	}
	
	@Override
	public void onShortClick() {
		if (position > 0) {
			position--;
			this.displayTestResult(resultList.get(position));
		} else {
			position = resultList.size() - 1;
			this.displayTestResult(resultList.get(position));
		}

		statusArea.setVisible(true);
		resultArea.setVisible(true);
		progressBarArea.setVisible(false);
		dateArea.setVisible(true);

		statusArea.setCurrentMode(Mode.BROWSING);
		statusArea.setErroring(false);
	}

	@Override
	public void onLongClick() {
		Message message = Message.obtain(handler, Interrupt.POWER_OFF.ordinal());
		message.sendToTarget();
	}

	@Override
	public void onDoubleClick() {
		if (position < resultList.size() - 1) {
			position++;
			this.displayTestResult(resultList.get(position));
		} else {
			position = 0;
			this.displayTestResult(resultList.get(position));
		}

		statusArea.setVisible(true);
		resultArea.setVisible(true);
		progressBarArea.setVisible(false);
		dateArea.setVisible(true);

		statusArea.setCurrentMode(Mode.BROWSING);
		statusArea.setErroring(false);
	}

}
