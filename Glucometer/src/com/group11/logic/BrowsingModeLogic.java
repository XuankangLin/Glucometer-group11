package com.group11.logic;

import java.util.LinkedList;

import android.content.SharedPreferences;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.group11.base.Interrupt;
import com.group11.base.Mode;
import com.group11.base.TestResult;
import com.group11.base.Unit;

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

	@Override
	public boolean validateMode() {
		// TODO Auto-generated method stub
		return super.validateMode();
	}

	@Override
	public void onShortClick() {
		if (position > 0) {
			// !!!for testing suppose DL for setup!!!
			position--;

			resultArea.display(Converter.to(
					resultList.get(position).getValue(),
					resultList.get(position).getUnit(), Unit.DL), Unit.DL);

			dateArea.setDateTime(resultList.get(position).getTime());
		} else {
			position = resultList.size() - 1;

			resultArea.display(Converter.to(
					resultList.get(position).getValue(),
					resultList.get(position).getUnit(), Unit.DL), Unit.DL);

			dateArea.setDateTime(resultList.get(position).getTime());
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
			// !!!for testing suppose DL for setup!!!
			position++;

			resultArea.display(Converter.to(
					resultList.get(position).getValue(),
					resultList.get(position).getUnit(), Unit.DL), Unit.DL);

			dateArea.setDateTime(resultList.get(position).getTime());
		} else {
			position = 0;

			resultArea.display(Converter.to(
					resultList.get(position).getValue(),
					resultList.get(position).getUnit(), Unit.DL), Unit.DL);

			dateArea.setDateTime(resultList.get(position).getTime());
		}
		statusArea.setVisible(true);
		resultArea.setVisible(true);
		progressBarArea.setVisible(false);
		dateArea.setVisible(true);

		statusArea.setCurrentMode(Mode.BROWSING);
		statusArea.setErroring(false);
	}
	
	public LinkedList<TestResult> getTestResults() {
		return this.resultList;
	}
}
