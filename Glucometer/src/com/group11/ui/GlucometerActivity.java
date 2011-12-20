package com.group11.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.group11.R;
import com.group11.base.BatteryLevel;
import com.group11.base.ClickStyle;
import com.group11.base.ErrorCode;
import com.group11.base.Interrupt;
import com.group11.base.Mode;
import com.group11.base.TestResult;
import com.group11.base.Unit;
import com.group11.hardware.Beeper;
import com.group11.hardware.CurrentStatus;
import com.group11.logic.BrowsingModeLogic;
import com.group11.logic.ModeLogic;
import com.group11.logic.SetupModeLogic;
import com.group11.logic.TestingModeLogic;
import com.group11.logic.UploadingModeLogic;
import com.group11.util.ClickJudger;
import com.group11.util.HistoryManager;
import com.group11.util.RandomGenerator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import static com.group11.base.Interrupt.*;

public class GlucometerActivity extends Activity {

	/**
	 * update the battery level every ** milliseconds
	 */
	private static final int BATTERY_UPDATE_PERIOD = 1000;
	/**
	 * charge one unit of battery every ** milliseconds if charging
	 */
	private static final int BATTERY_CHARGE_PERIOD = 2000;
	/**
	 * consume one unit of battery every ** milliseconds if not charging
	 */
	private static final int BATTERY_CONSUME_PERIOD = 4000;
	/**
	 * update the current time every ** milliseconds
	 */
	private static final int TIME_UPDATE_PERIOD = 1000;
	/**
	 * wait ** milliseconds to turn off
	 */
	private static final int ERROR_ENDING_TIME = 10000;

	private ImageView glucometerImage;
	private ImageView realButtonImage;
	private ImageView testStripImage;
	private ImageView resetImage;
	private ImageView usbImage;
	private ImageView acPlugImage;

	private StatusArea statusArea;
	private ResultArea resultArea;
	private ProgressBarArea progressBarArea;
	private DateArea dateArea;

	private SharedPreferences preferences;

	private Handler handler = new Handler(new GlucometerHandlerCallback());
	private ClickJudger judger = new ClickJudger(handler);

	private ModeLogic currentModeLogic = null;

	private TimerTask batteryUpdaterTask = null;
	private TimerTask timeUpdaterTask = null;
	private TimerTask chargeTask = null;
	private TimerTask consumeTask = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		preferences = getPreferences(MODE_PRIVATE);

		// ==========global pictures==========
		glucometerImage = (ImageView) findViewById(R.id.meterImage);
		realButtonImage = (ImageView) findViewById(R.id.buttonImage);
		testStripImage = (ImageView) findViewById(R.id.testStripImage);
		resetImage = (ImageView) findViewById(R.id.resetImage);
		usbImage = (ImageView) findViewById(R.id.usbImage);
		acPlugImage = (ImageView) findViewById(R.id.acPlugImage);

		// ==========screen's top panel==========
		statusArea = new StatusArea(
				(LinearLayout) findViewById(R.id.topPanel),
				(ImageView) findViewById(R.id.batteryImage),
				(ImageView) findViewById(R.id.acImage),
				(ImageView) findViewById(R.id.testingModeImage),
				(ImageView) findViewById(R.id.browsingModeImage),
				(ImageView) findViewById(R.id.uploadingModeImage),
				(ImageView) findViewById(R.id.errorModeImage));

		// ==========screen's result area==========
		resultArea = new ResultArea(
				(LinearLayout) findViewById(R.id.resultPanel),
				(ImageView) findViewById(R.id.firstNumberImage),
				(ImageView) findViewById(R.id.secondNumberImage),
				(ImageView) findViewById(R.id.pointImage),
				(ImageView) findViewById(R.id.thirdNumberImage),
				(ImageView) findViewById(R.id.unitImage));

		// ==========screen's progress bar==========
		progressBarArea = new ProgressBarArea(
				(LinearLayout) findViewById(R.id.progressBarPanel),
				(ImageView) findViewById(R.id.progressBarImage));

		// ==========screen's date panel==========
		dateArea = new DateArea(
				(LinearLayout) findViewById(R.id.datePanel),
				(TextView) findViewById(R.id.monthText1),
				(TextView) findViewById(R.id.monthText2),
				(TextView) findViewById(R.id.dayText1),
				(TextView) findViewById(R.id.dayText2),
				(TextView) findViewById(R.id.yearText1),
				(TextView) findViewById(R.id.yearText2),
				(TextView) findViewById(R.id.hourText1),
				(TextView) findViewById(R.id.hourText2),
				(TextView) findViewById(R.id.colonText),
				(TextView) findViewById(R.id.minuteText1),
				(TextView) findViewById(R.id.minuteText2));

		this.setOnClickListeners();

		Beeper.get().attachHandler(this.handler);

		this.resetStatus();
		this.updateTestStripImage();
		this.updateUSBImage();
		this.updateACPlugImage();

		this.resetBatteryUpdaterTask();
		this.resetTimeUpdaterTask();
		this.resetConsumeTask();
	}

	/**
	 * reset the status stored in Memory our glucometer program could not
	 * recover from its last break down at present.
	 */
	private void resetStatus() {
		this.setScreenInvisible();

		CurrentStatus currentStatus = new CurrentStatus(preferences);
		currentStatus.syncCurrentTime();

		currentStatus.setErrorNow(false);
		currentStatus.setPowerOn(false);
		currentStatus.setCurrentMode(null);
		currentStatus.setRefreshingTime(true);
		currentStatus.setACPlugged(false);
		currentStatus.setUSBConnected(false);
		currentStatus.setStripInserted(false);
		currentStatus.setBloodFed(false);
		currentStatus.setLastModeComplete(true);

		currentStatus.commit();
	}
	
	/**
	 * clear charging's task
	 */
	private void clearChargeTask() {
		if (chargeTask != null) {
			chargeTask.cancel();
			chargeTask = null;
		}
	}
	
	/**
	 * periodically charge the battery
	 */
	private void resetChargeTask() {
		this.clearChargeTask();
		chargeTask = new TimerTask() {
			
			@Override
			public void run() {
				new CurrentStatus(preferences).batteryPlusPlus();
			}
		};
		new Timer().scheduleAtFixedRate(chargeTask, 0, BATTERY_CHARGE_PERIOD);
	}
	
	private void clearConsumeTask() {
		if (consumeTask != null) {
			consumeTask.cancel();
			consumeTask = null;
		}
	}
	
	private void resetConsumeTask() {
		this.clearConsumeTask();
		consumeTask = new TimerTask() {
			
			@Override
			public void run() {
				CurrentStatus status = new CurrentStatus(preferences);
				if (status.isPowerOn()) {
					status.batteryMinusMinus();
				}
			}
		};
		new Timer().scheduleAtFixedRate(consumeTask, 0, BATTERY_CONSUME_PERIOD);
	}
	
	/**
	 * clear the running time updater task if it exists
	 */
	private void clearTimeUpdaterTask() {
		if (timeUpdaterTask != null) {
			timeUpdaterTask.cancel();
			timeUpdaterTask = null;
		}		
	}

	/**
	 * periodically update the time according
	 */
	private void resetTimeUpdaterTask() {
		this.clearTimeUpdaterTask();
		timeUpdaterTask = new TimerTask() {

			@Override
			public void run() {
				CurrentStatus status = new CurrentStatus(preferences);
				status.nextSecond();
				if (status.isRefreshingTime()) {
					Message message = Message.obtain(handler,
							Interrupt.TIME_TICK.ordinal());
					message.sendToTarget();
				}
			}
		};
		new Timer().scheduleAtFixedRate(timeUpdaterTask, 0, TIME_UPDATE_PERIOD);
	}

	/**
	 * clear the battery updater task if it exists
	 */
	private void clearBatteryUpdaterTask() {
		if (batteryUpdaterTask != null) {
			batteryUpdaterTask.cancel();
			batteryUpdaterTask = null;
		}		
	}
	
	/**
	 * periodically update the battery level according to the status in
	 * "hardware"
	 */
	private void resetBatteryUpdaterTask() {
		this.clearBatteryUpdaterTask();
		batteryUpdaterTask = new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						CurrentStatus status = new CurrentStatus(preferences);
						BatteryLevel currentLevel = status.getBatteryLevel();
						statusArea.setBatteryLevel(currentLevel);
						statusArea.setBatteryBlinking(currentLevel == BatteryLevel.ZERO_PERCENT
										|| status.isACPlugged());
						if (currentLevel == BatteryLevel.ZERO_PERCENT
								&& status.getLastBatteryLevel() == BatteryLevel.TWENTY_FIVE_PERCENT) {
							Beeper.get().doWarningBeep(GlucometerActivity.this);
						}
						status.setBatteryLevel(status.getBattery());
						status.commit();
					}
				});
			}
		};
		new Timer().scheduleAtFixedRate(batteryUpdaterTask, 0,
				BATTERY_UPDATE_PERIOD);
	}

	/**
	 * set the click listeners of all the global pictures
	 */
	private void setOnClickListeners() {
		realButtonImage.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					realButtonImage.setImageResource(R.drawable.button_down);
					judger.buttonDown(new Date());
					return true;
				}
				case MotionEvent.ACTION_UP: {
					realButtonImage.setImageResource(R.drawable.button_up);
					judger.buttonUp(new Date());
					return true;
				}

				default:
					break;
				}
				return false;
			}
		});

		testStripImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CurrentStatus status = new CurrentStatus(preferences);
				if (status.isStripInserted()) {
					//=====strip inserted=====
					if (status.isBloodFed()) {
						//=====blood fed, should pull out=====
						Message message = Message.obtain(handler,
								Interrupt.STRIP_PULLED_OUT.ordinal());
						message.sendToTarget();

						status.setStripInserted(false);
						status.setBloodFed(false);
						status.commit();
					}
					else {
						//=====blood not fed, should feed blood=====
						Message message = Message.obtain(handler);
						message.what = status.isBloodSufficient() ? Interrupt.BLOOD_SUFFICIENT
								.ordinal() : Interrupt.BLOOD_INSUFFICIENT.ordinal();
						message.sendToTarget();
						
						status.setBloodFed(true);
						status.setStripInserted(true);
						status.commit();
					}					
				}
				else {
					//=====strip not inserted, should insert=====
					Message message = Message.obtain(handler,
							Interrupt.STRIP_INSERTED.ordinal());
					message.sendToTarget();
					
					status.setStripInserted(true);
					status.setBloodFed(false);
					status.commit();
				}
				updateTestStripImage();
			}
		});

		resetImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resetGlucometer();
				Toast.makeText(GlucometerActivity.this,
						"Reset Button clicked.", 200).show();
			}
		});

		usbImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CurrentStatus currentStatus = new CurrentStatus(preferences);
				if (currentStatus.isUSBConnected()) {
					Message message = Message.obtain(handler,
							USB_DISCONNECTED.ordinal());
					message.sendToTarget();
					currentStatus.setUSBConnected(false);
				}
				else {
					Message message = Message.obtain(handler,
							USB_CONNECTED.ordinal());
					message.sendToTarget();
					currentStatus.setUSBConnected(true);
				}
				currentStatus.commit();
				updateUSBImage();
			}
		});

		acPlugImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CurrentStatus status = new CurrentStatus(preferences);
				Message message = Message.obtain(handler);
				if (status.isACPlugged()) {
					message.what = Interrupt.AC_OFF.ordinal();
					status.setACPlugged(false);
				}
				else {
					message.what = Interrupt.AC_ON.ordinal();
					status.setACPlugged(true);
				}
				status.commit();
				message.sendToTarget();
				updateACPlugImage();
			}
		});
	}
	
	/**
	 * set the TestStrip Image according to INSERTED_OR_NOT BLOOD_FED_OR_NOT 
	 */
	private void updateTestStripImage() {
		CurrentStatus status = new CurrentStatus(preferences);
		if (status.isStripInserted()) {
			if (status.isBloodFed()) {
				testStripImage
						.setImageResource(status.isBloodSufficient() ? R.drawable.test_strip_with_sufficient
								: R.drawable.test_strip_with_insufficient);
			}
			else {
				testStripImage
						.setImageResource(status.isStripValid() ? R.drawable.valid_test_strip
								: R.drawable.invalid_test_strip);
			}
		}
		else {
			testStripImage
					.setImageResource(status.isStripValid() ? R.drawable.valid_test_strip_not_inserted
							: R.drawable.invalid_test_strip_not_inserted);
		}
	}
	
	/**
	 * set the USB's image according to USB_CONNECTED?
	 */
	private void updateUSBImage() {
		if (new CurrentStatus(preferences).isUSBConnected()) {
			usbImage.setImageResource(R.drawable.usb);
		}
		else {
			usbImage.setImageResource(R.drawable.usb_not_inserted);
		}
	}
	
	/**
	 * set the AC_PLUG's image according to AC_PLUGGED?
	 */
	private void updateACPlugImage() {
		if (new CurrentStatus(preferences).isACPlugged()) {
			acPlugImage.setImageResource(R.drawable.ac_plug_inserted);
		}
		else {
			acPlugImage.setImageResource(R.drawable.ac_plug);
		}
	}
	
	/**
	 * restore to the factory setting:
	 * test unit: L, 
	 * date: 2000/01/01, time: 00:00,
	 * battery level: current battery level)
	 * number of history: 0, clear the memory for history records.
	 */
	public void resetGlucometer() {
		CurrentStatus status = new CurrentStatus(preferences);
		status.setCurrentUnit(Unit.L);
		status.setCurrentTime(CurrentStatus.getDefaultTime());
		status.commit();
		
		new HistoryManager(this).deleteAllTestResults();
	}

	/**
	 * set all the screen parts's layout to be invisible
	 */
	public void setScreenInvisible() {
		statusArea.setVisible(false);
		resultArea.setVisible(false);
		progressBarArea.setVisible(false);
		dateArea.setVisible(false);
	}

	private void doStripInserted() {
		if (new CurrentStatus(preferences).isErrorNow()) {
			return;
		}

		if (currentModeLogic != null) {
			currentModeLogic.onStripInserted();
		}
		else {
			//===== enter Testing Mode =====
			this.enterTestingMode();
		}
	}

	private void doStripPulledOut() {
		if (new CurrentStatus(preferences).isErrorNow()) {
			return;
		}

		if (currentModeLogic != null) {
			currentModeLogic.onStripPulledOut();
		}
		else {
			//=====finish the last Testing Mode=====
			CurrentStatus status = new CurrentStatus(preferences);
			if (status.getCurrentMode() == Mode.TESTING) {
				status.setLastModeComplete(true);
				Log.i("NULLing~~~~~~~", "set null in doStripPulledOut(), finishing the last Testing Mode");
				status.setCurrentMode(null);
				status.commit();
			}
		}
	}

	private void doUSBConnected() {
		if (new CurrentStatus(preferences).isErrorNow()) {
			return;
		}

		if (currentModeLogic != null) {
			currentModeLogic.onUSBConnected();
		}
		else {
			//=====enter Uploading Mode=====
			this.enterUploadingMode();
		}
	}

	private void doUSBDisconnected() {
		if (new CurrentStatus(preferences).isErrorNow()) {
			return;
		}

		if (currentModeLogic != null) {
			currentModeLogic.onUSBDisconnected();
		}
		else {
			//=====finish the last Uploading Mode=====
			CurrentStatus status = new CurrentStatus(preferences);
			if (status.getCurrentMode() == Mode.UPLOADING) {
				status.setLastModeComplete(true);
				status.setCurrentMode(null);
				status.commit();
			}
		}
	}
	
	/**
	 * activate an error-beep, 
	 * display error code and error symbol for 10 seconds, 
	 * and then go through the Voluntary Ending procedure.
	 */
	private void doErrorEnding(Message msg) {
		Beeper.get().doErrorBeep(this);
		
		int errorCode = msg.arg1;
		statusArea.setVisible(true);
		statusArea.setErroring(true);
		resultArea.setVisible(true);
		resultArea.displayError(errorCode);
		progressBarArea.setVisible(false);
		dateArea.setVisible(true);
		
		CurrentStatus status = new CurrentStatus(preferences);
		status.setErrorNow(true);
		status.commit();
		Mode mode = status.getCurrentMode();
		if (mode == null) {
			Log.e("NULL...", "why here");
		}
		
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						doEnding();
					}
				});
			}
		}, ERROR_ENDING_TIME);
	}

	/**
	 * do some cleaning when voluntary ending
	 */
	private void doEnding() {
		setScreenInvisible();
		if (currentModeLogic != null) {
			currentModeLogic.clearTimerTasks();
		}
		currentModeLogic = null;
		
		CurrentStatus status = new CurrentStatus(preferences);
		status.setPowerOn(false);
		status.setRefreshingTime(true);
		status.setErrorNow(false);
		status.commit();
		
		if (status.getCurrentMode() == null) {
			// =====could also come to here: if INITIALIZATION_ERROR,
			// setCurrentMode(XX) hasn't work, it's still null=====
			return;
		}

		switch (status.getCurrentMode()) {
		case BROWSING:
		case SETUP:
			status.setLastModeComplete(true);
			status.setCurrentMode(null);
			break;
		case TESTING:
			if (!status.isStripInserted()) {
				status.setLastModeComplete(true);
				status.setCurrentMode(null);
			}
			else {
				status.setLastModeComplete(false);
			}
			break;
		case UPLOADING:
			if (!status.isUSBConnected()) {
				status.setLastModeComplete(true);
				status.setCurrentMode(null);
			}
			else {
				status.setLastModeComplete(false);
			}
			break;

		default:
			break;
		}			
		status.commit();
	}
	
	public void addTestingRecords() {
		//for testing
		HistoryManager historymanager = new HistoryManager(this);

		historymanager.addTestResult(new TestResult(222.1459972,
				Unit.L, new Date()));
		historymanager.addTestResult(new TestResult(999.1459972,
				Unit.L, new Date()));
		historymanager.addTestResult(new TestResult(666.1459972,
				Unit.L, new Date()));
	}

	private void doButtonClicked(Message msg) {
		if (new CurrentStatus(preferences).isErrorNow()) {
			return;
		}

		switch (ClickStyle.get(msg.arg1)) {
		case SHORT_CLICK: {
			if (currentModeLogic != null) {
				currentModeLogic.onShortClick();				
			}
			return;
		}
		case DOUBLE_CLICK: {
			if (currentModeLogic != null) {
				currentModeLogic.onDoubleClick();
			} else {
				//=====enter Setup Mode=====
				this.enterSetupMode();
			}
			return;
		}
		case LONG_CLICK: {
			if (currentModeLogic != null) {
				currentModeLogic.onLongClick();
			} else {
				//=======go into Browsing Mode=======
				this.enterBrowsingMode();
			}
			return;
		}
		
		default:
			return;
		}
	}
	
	private void enterTestingMode() {
		TestingModeLogic modeLogic = new TestingModeLogic(statusArea,
				resultArea, progressBarArea, dateArea, this, preferences,
				handler);
		currentModeLogic = modeLogic;

		this.enterXXMode(modeLogic);
	}
	
	private void enterBrowsingMode() {
		BrowsingModeLogic modeLogic = new BrowsingModeLogic(statusArea,
				resultArea, progressBarArea, dateArea, this,
				preferences, handler);
		currentModeLogic = modeLogic;

		this.enterXXMode(modeLogic);
	}
	
	private void enterUploadingMode() {
		UploadingModeLogic modeLogic = new UploadingModeLogic(statusArea,
				resultArea, progressBarArea, dateArea, this, preferences,
				handler);
		currentModeLogic = modeLogic;
		
		if (this.enterXXMode(modeLogic)) {
			modeLogic.onUSBConnected();			
		}
	}
	
	private void enterSetupMode() {
		SetupModeLogic modeLogic = new SetupModeLogic(statusArea, resultArea,
				progressBarArea, dateArea, this, preferences, handler);
		currentModeLogic = modeLogic;
		
		if (this.enterXXMode(modeLogic)) {
			modeLogic.initDisplay();			
		}
	}
	
	/**
	 * the procedure of entering a particular mode
	 * @param modeLogic
	 * @return whether successful
	 */
	private boolean enterXXMode(ModeLogic modeLogic) {
		CurrentStatus status = new CurrentStatus(preferences);
		status.setPowerOn(true);
		status.syncCurrentTime();
		status.commit();

		if (!modeLogic.initialize()) {
			//=====Initialization Error=====
			Message message = Message.obtain(handler, Interrupt.ERROR_ENDING.ordinal());
			message.arg1 = ErrorCode.INITIALIZATION_ERROR.getErrorCode();
			message.sendToTarget();
			return false;
		}
		
		if (!modeLogic.checkMeterStatus()) {
			//=====Meter Status Checking Error=====
			Message message = Message.obtain(handler, Interrupt.ERROR_ENDING.ordinal());
			message.arg1 = ErrorCode.METER_STATUS_ERROR.getErrorCode();
			message.sendToTarget();
			return false;
		}

		modeLogic.validateMode();
		return true;
	}

	/**
	 * change the beeper's image, since it's contained in the glucometer image
	 * 
	 * @param beeping
	 *            true: set beeping glucometer image false: set not beeping
	 *            glucometer image
	 */
	private void setBeeperImage(boolean beeping) {
		if (beeping) {
			this.glucometerImage
					.setImageResource(R.drawable.glucometer_beeping);
		} else {
			this.glucometerImage
					.setImageResource(R.drawable.glucometer_not_beeping);
		}
	}

	@Override
	public void onBackPressed() {
		this.clearBatteryUpdaterTask();
		this.clearTimeUpdaterTask();
		this.clearChargeTask();
		this.clearConsumeTask();
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_setting:
			this.doSetting();
			return true;

		case R.id.menu_about:
			this.doAbout();
			return true;

		case R.id.menu_exit:
			this.onBackPressed();
			return true;

		default:
			return false;
		}
	}

	/**
	 * display Setting info on a new dialog
	 */
	private void doSetting() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.setting_info, null);
		final CurrentStatus status = new CurrentStatus(preferences);

		{
			final TextView batteryTextView = (TextView) view
					.findViewById(R.id.batteryText);
			SeekBar batterySeekBar = (SeekBar) view
					.findViewById(R.id.batterySeekBar);
			batterySeekBar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
						}

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							if (fromUser) {
								status.setBatteryLevel(progress);
							}
							batteryTextView.setText(Integer.toString(progress)
									+ "%");
						}
					});
			batterySeekBar.setProgress(status.getBattery());
		} {
			CheckBox initializationBox = (CheckBox) view
					.findViewById(R.id.initializationErrorCheckbox);
			initializationBox
					.setChecked(status.isInitializationErrorNextTime());
			initializationBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							status.setInitializationErrorNextTime(isChecked);
						}
					});
		} {
			CheckBox validStripBox = (CheckBox) view
					.findViewById(R.id.stripValidCheckbox);
			validStripBox.setChecked(status.isStripValid());
			validStripBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					status.setStripValid(isChecked);
				}
			});
		} {
			CheckBox sufficientBloodBox = (CheckBox) view
					.findViewById(R.id.bloodSufficientCheckbox);
			sufficientBloodBox.setChecked(status.isBloodSufficient());
			sufficientBloodBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					status.setBloodSufficient(isChecked);
				}
			});
		} {
			CheckBox pcReadyBox = (CheckBox) view
					.findViewById(R.id.pcReadyCheckbox);
			pcReadyBox.setChecked(status.isPCReady());
			pcReadyBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					status.setPCReady(isChecked);
				}
			});
		} {
			CheckBox softwareReadyBox = (CheckBox) view
					.findViewById(R.id.softwareReadyCheckbox);
			softwareReadyBox.setChecked(status.isSoftwareReady());
			softwareReadyBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					status.setSoftwareReady(isChecked);
				}
			});
		} {
			CheckBox timeoutBox = (CheckBox) view
					.findViewById(R.id.testingTimeoutCheckbox);
			timeoutBox.setChecked(status.isResultTimeout());
			timeoutBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					status.setResultTimeout(isChecked);
				}
			});
		}

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Setting");
		builder.setView(view);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				status.commit();
				updateTestStripImage();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/**
	 * display About info on a new dialog
	 */
	private void doAbout() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("About");
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		builder.setView(inflater.inflate(R.layout.about_info, null));
		builder.setPositiveButton("Excellent",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/**
	 * the callback to deal with Messages in the Handler
	 */
	private class GlucometerHandlerCallback implements Callback {

		private boolean handleTestingMode(Message msg) {
			if (STRIP_INSERTED.ordinal() == msg.what) {
				doStripInserted();					
				return true;
			}
			if (STRIP_PULLED_OUT.ordinal() == msg.what) {
				doStripPulledOut();
				return true;
			}
			if (STRIP_VALID.ordinal() == msg.what) {
				if (new CurrentStatus(preferences).isErrorNow()) {
					return true;
				}
				if (currentModeLogic != null) {
					((TestingModeLogic) currentModeLogic).onStripValid();
				}
				return true;
			}
			if (STRIP_INVALID.ordinal() == msg.what) {
				if (new CurrentStatus(preferences).isErrorNow()) {
					return true;
				}
				if (currentModeLogic != null) {
					((TestingModeLogic) currentModeLogic).onStripInvalid();
				}
				return true;
			}	
			if (BLOOD_SUFFICIENT.ordinal() == msg.what) {
				if (new CurrentStatus(preferences).isErrorNow()) {
					return true;
				}
				if (currentModeLogic != null) {
					((TestingModeLogic) currentModeLogic).onBloodSufficient();
				}
				return true;
			}
			if (BLOOD_INSUFFICIENT.ordinal() == msg.what) {
				if (new CurrentStatus(preferences).isErrorNow()) {
					return true;
				}
				if (currentModeLogic != null) {
					((TestingModeLogic) currentModeLogic).onBloodInsufficient();
				}
				return true;
			}
			if (RESULT_READY.ordinal() == msg.what) {
				if (new CurrentStatus(preferences).isErrorNow()) {
					return true;
				}
				if (currentModeLogic != null) {
					CurrentStatus status = new CurrentStatus(preferences);
					Unit unit = status.getCurrentUnit();
					((TestingModeLogic) currentModeLogic)
							.onResultReady(new TestResult(RandomGenerator
									.getRandomResult(unit), unit, status
									.getCurrentTime()));
				}
				return true;
			}
			if (RESULT_TIMEOUT.ordinal() == msg.what) {
				if (new CurrentStatus(preferences).isErrorNow()) {
					return true;
				}
				if (currentModeLogic != null) {
					((TestingModeLogic) currentModeLogic).onResultTimeout();					
				}
				return true;				
			}

			//=====Message not handled=====
			return false;
		}
		
		private boolean handleUploadingMessage(Message msg) {
			if (USB_CONNECTED.ordinal() == msg.what) {
				doUSBConnected();
				return true;
			}
			if (USB_DISCONNECTED.ordinal() == msg.what) {
				doUSBDisconnected();
				return true;
			}
			
			//=====Message not handled=====
			return false;
		}
		
		private boolean handleGlobalMessage(Message msg) {
			if (AC_ON.ordinal() == msg.what) {
				statusArea.setACing(true);
				clearConsumeTask();
				resetChargeTask();
				return true;
			}
			if (AC_OFF.ordinal() == msg.what) {
				statusArea.setACing(false);
				clearChargeTask();
				resetConsumeTask();
				return true;
			}
			if (TIME_TICK.ordinal() == msg.what) {
				CurrentStatus status = new CurrentStatus(preferences);
				dateArea.setDateTime(status.getCurrentTime());
				return true;
			}
			if (BUTTON_CLICKED.ordinal() == msg.what) {
				doButtonClicked(msg);
				return true;
			}
			if (BEEP_START.ordinal() == msg.what) {
				setBeeperImage(true);
				return true;
			}
			if (BEEP_STOP.ordinal() == msg.what) {
				setBeeperImage(false);
				return true;
			}
			if (ERROR_ENDING.ordinal() == msg.what) {
				if (new CurrentStatus(preferences).isErrorNow()) {
					return true;
				}
				doErrorEnding(msg);
				return true;
			}		
			if (VOLUNTARY_ENDING.ordinal() == msg.what) {
				if (new CurrentStatus(preferences).isErrorNow()) {
					return true;
				}
				Beeper.get().doTurnOffBeep(GlucometerActivity.this);
				doEnding();
				return true;
			}
			
			//=====Message not handled=====
			return false;
		}
		
		@Override
		public boolean handleMessage(Message msg) {
			return this.handleGlobalMessage(msg) || this.handleTestingMode(msg)
					|| this.handleUploadingMessage(msg);
		}
	}

}
