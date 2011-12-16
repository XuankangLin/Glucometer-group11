package com.group11.ui;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.group11.R;
import com.group11.base.BatteryLevel;
import com.group11.base.ClickStyle;
import com.group11.base.Mode;
import com.group11.base.Unit;
import com.group11.hardware.Beeper;
import com.group11.hardware.CurrentStatus;
import com.group11.logic.ModeLogic;
import com.group11.util.ClickJudger;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.group11.base.Interrupt.*;


public class GlucometerActivity extends Activity {
	
	/**
	 * update the battery level every ** milliseconds
	 */
	private static final int BATTERY_UPDATE_PERIOD = 1000;

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
	private ClickJudger judger = new ClickJudger(handler);;
	
	private ModeLogic currentModeLogic = null;
	
	private TimerTask batteryUpdaterTask = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        
        preferences = getPreferences(MODE_PRIVATE);
        
        //==========global pictures==========
        glucometerImage = (ImageView) findViewById(R.id.meterImage);
        realButtonImage = (ImageView) findViewById(R.id.buttonImage);
        testStripImage = (ImageView) findViewById(R.id.testStripImage);
        resetImage = (ImageView) findViewById(R.id.resetImage);
        usbImage = (ImageView) findViewById(R.id.usbImage);
        acPlugImage = (ImageView) findViewById(R.id.acPlugImage);
        
        //==========screen's top panel==========
		statusArea = new StatusArea(
				(LinearLayout) findViewById(R.id.topPanel),
				this,
				(ImageView) findViewById(R.id.batteryImage),
				(ImageView) findViewById(R.id.acImage),
				(ImageView) findViewById(R.id.testingModeImage),
				(ImageView) findViewById(R.id.browsingModeImage),
				(ImageView) findViewById(R.id.uploadingModeImage),
				(ImageView) findViewById(R.id.errorModeImage));
		
        //==========screen's result area==========
		resultArea = new ResultArea(
				(LinearLayout) findViewById(R.id.resultPanel),
				(ImageView) findViewById(R.id.firstNumberImage),
				(ImageView) findViewById(R.id.secondNumberImage),
				(ImageView) findViewById(R.id.pointImage),
				(ImageView) findViewById(R.id.thirdNumberImage),
				(ImageView) findViewById(R.id.unitImage));
        
        //==========screen's progress bar==========
		progressBarArea = new ProgressBarArea(
				(LinearLayout) findViewById(R.id.progressBarPanel),
				(ImageView) findViewById(R.id.progressBarImage));
        
        //==========screen's date panel==========
		dateArea = new DateArea(
				(LinearLayout) findViewById(R.id.datePanel),
				this,
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
        
        
        //TODO testing code, to be deleted
        statusArea.setCurrentMode(Mode.BROWSING);
        statusArea.setErroring(true);
        statusArea.setACing(true);
        statusArea.setBatteryLevel(BatteryLevel.SEVENTY_FIVE_PERCENT);
        
        CurrentStatus currentStatus = new CurrentStatus(preferences);
        currentStatus.setBatteryLevel(13);
        this.resetBatteryUpdaterTask();

        resultArea.display(123.1459972, Unit.L);

        progressBarArea.setVisible(true);
        progressBarArea.setProgress(6);
        
        dateArea.setDateTime(new Date());
        dateArea.setColonBlinking(true);
    }
    
    /**
     * periodically update the battery level according to the status in "hardware"
     */
    private void resetBatteryUpdaterTask() {
    	if (batteryUpdaterTask != null) {
			batteryUpdaterTask.cancel();
			batteryUpdaterTask = null;
		}
    	
    	batteryUpdaterTask = new TimerTask() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						BatteryLevel level = new CurrentStatus(preferences).getBatteryLevel();
						statusArea.setBatteryLevel(level);
						statusArea.setBatteryBlinking(level == BatteryLevel.ZERO_PERCENT);
					}
				});
			}
		};
		new Timer().schedule(batteryUpdaterTask, 0, BATTERY_UPDATE_PERIOD);
    }
    
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
				Random random = new Random();
				if (random.nextBoolean()) {
					testStripImage.setImageResource(R.drawable.test_strip_invalid);					
				}
				else {
					testStripImage.setImageResource(R.drawable.test_strip_valid);
				}
				Beeper.get().doShortLongBeep(GlucometerActivity.this);
				Toast.makeText(GlucometerActivity.this, "short long beep", 1000).show();				
			}
		});
        
        resetImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Beeper.get().doDoubleBeep(GlucometerActivity.this);
				Toast.makeText(GlucometerActivity.this, "double beep", 1000).show();
			}
		});
        
        usbImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Beeper.get().doLongBeep(GlucometerActivity.this);
				Toast.makeText(GlucometerActivity.this, "Long beep", 1000).show();
			}
		});

        acPlugImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Beeper.get().doShortBeep(GlucometerActivity.this);
				Toast.makeText(GlucometerActivity.this, "short beep", 1000).show();
			}
		});
    }
    
    /**
     * set the screen parts's layout to be visible or not
     * @param visible
     */
    private void setScreenVisible(boolean visible) {
		statusArea.setVisible(visible);
		resultArea.setVisible(visible);
		progressBarArea.setVisible(visible);
		dateArea.setVisible(visible);
    }
    
    private void doStripInserted() {
    	//TODO fulfill this method
    }
    
    private void doStripPulledOut() {
    	//TODO fulfill this method
    }
    
    private void doUSBConnected() {
    	//TODO fulfill this method
    }

	private void doUSBDisconnected() {
		//TODO fulfill this method
	}
	
	private void doStripValid() {
		//TODO fulfill this method
	}
	
	private void doStripInvalid() {
		//TODO fulfill this method
	}
	
	private void doBloodSufficient() {
		//TODO fulfill this method
	}
	
	private void doBloodInsufficient() {
		//TODO fulfill this method
	}
	
	private void doResultReady() {
		//TODO fulfill this method
	}
	
	private void doAcOn() {
		//TODO fulfill this method
	}
	
	private void doAcOff() {
		//TODO fulfill this method
	}
	
	private void doTimeTick() {
		//TODO fulfill this method
	}
	
	private void doButtonClicked(Message msg) {
		/*
		 * provided that this.currentModeLogic
		 * is NOT null when it's power on
		 */
		CurrentStatus currentStatus = new CurrentStatus(preferences);
		switch (ClickStyle.get(msg.arg1)) {
		case SHORT_CLICK: {
			if (currentStatus.isPowerOn()) {
				currentModeLogic.onShortClick();				
			}
			return;
		}
		case DOUBLE_CLICK: {
			if (currentStatus.isPowerOn()) {
				currentModeLogic.onDoubleClick();				
			}
			else {
				//TODO go into Setup Mode
			}
			return;
		}
		case LONG_CLICK: {
			if (currentStatus.isPowerOn()) {
				currentModeLogic.onLongClick();				
			}
			else {
				//TODO go into Browsing Mode
			}
			return;
		}

		default:
			break;
		}
	}
	
	/**
	 * change the beeper's image, since it's contained in the glucometer image
	 * @param beeping true: set beeping glucometer image
	 * 								false: set not beeping glucometer image
	 */
	private void setBeeperImage(boolean beeping) {
		if (beeping) {
			this.glucometerImage.setImageResource(R.drawable.glucometer_beeping);
		}
		else {
			this.glucometerImage.setImageResource(R.drawable.glucometer_not_beeping);
		}
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
			this.finish();
			return true;

		default:
			return false;
		}
	}
	
	/**
	 * display Setting info on a new dialog
	 */
	private void doSetting() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Setting");
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.setting_info, null);
		//TODO fulfill the view's objects' listener
		final CurrentStatus status = new CurrentStatus(preferences);

		builder.setView(view);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
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
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		builder.setView(inflater.inflate(R.layout.about_info, null));
		builder.setPositiveButton("Excellent", new DialogInterface.OnClickListener() {
			
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
		
		@Override
		public boolean handleMessage(Message msg) {
			if (STRIP_INSERTED.ordinal() == msg.what) {
				doStripInserted();
				return true;
			}
			if (STRIP_PULLED_OUT.ordinal() == msg.what) {
				doStripPulledOut();
				return true;
			}
			
			if (USB_CONNECTED.ordinal() == msg.what) {
				doUSBConnected();
				return true;
			}
			if (USB_DISCONNECTED.ordinal() == msg.what) {
				doUSBDisconnected();
				return true;
			}
			
			if (STRIP_VALID.ordinal() == msg.what) {
				doStripValid();
				return true;
			}
			if (STRIP_INVALID.ordinal() == msg.what) {
				doStripInvalid();
				return true;
			}
			
			if (BLOOD_SUFFICIENT.ordinal() == msg.what) {
				doBloodSufficient();
				return true;
			}
			if (BLOOD_INSUFFICIENT.ordinal() == msg.what) {
				doBloodInsufficient();
				return true;
			}
			
			if (RESULT_READY.ordinal() == msg.what) {
				doResultReady();
				return true;
			}
			
			if (AC_ON.ordinal() == msg.what) {
				doAcOn();
				return true;
			}
			if (AC_OFF.ordinal() == msg.what) {
				doAcOff();
				return true;
			}
			
			if (TIME_TICK.ordinal() == msg.what) {
				doTimeTick();
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
			
			if (POWER_ON.ordinal() == msg.what) {
				setScreenVisible(true);
				return true;
			}
			if (POWER_OFF.ordinal() == msg.what) {
				setScreenVisible(false);
				currentModeLogic = null;
				return true;
			}
			
			return false;
		}
	}
    
}