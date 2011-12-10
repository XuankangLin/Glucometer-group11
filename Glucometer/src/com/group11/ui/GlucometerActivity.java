package com.group11.ui;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.group11.R;
import com.group11.base.ClickStyle;
import com.group11.base.TestResult;
import com.group11.hardware.Beeper;
import com.group11.util.ClickJudger;
import com.group11.util.HistoryManager;
import com.group11.util.TimeFormatter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
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

	private ImageView glucometerImage;
	private ImageView realButtonImage;
	private ImageView testStripImage;
	private ImageView resetImage;
	private ImageView usbImage;
	private ImageView acPlugImage;

	private ImageView batteryImage;
	private ImageView acImage;
	private ImageView testingModeImage;
	private ImageView browsingModeImage;
	private ImageView uploadingModeImage;
	private ImageView errorModeImage;
	private LinearLayout topPanel;

	private ResultArea resultArea;
	private LinearLayout resultPanel;

	private ImageView progressBarImage;

	private TextView dateText;
	private TextView timeText;
	private LinearLayout datePanel;
	
	
	private Handler handler = new Handler(new GlucometerHandlerCallback());
	private ClickJudger judger = new ClickJudger(handler);;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        
        //==========global pictures==========
        glucometerImage = (ImageView) findViewById(R.id.meterImage);
        realButtonImage = (ImageView) findViewById(R.id.buttonImage);
        testStripImage = (ImageView) findViewById(R.id.testStripImage);
        resetImage = (ImageView) findViewById(R.id.resetImage);
        usbImage = (ImageView) findViewById(R.id.usbImage);
        acPlugImage = (ImageView) findViewById(R.id.acPlugImage);
        
        //==========screen's top panel==========
        batteryImage = (ImageView) findViewById(R.id.batteryImage);
        acImage = (ImageView) findViewById(R.id.acImage);
        testingModeImage = (ImageView) findViewById(R.id.testingModeImage);
        browsingModeImage = (ImageView) findViewById(R.id.browsingModeImage);
        uploadingModeImage = (ImageView) findViewById(R.id.uploadingModeImage);
        errorModeImage = (ImageView) findViewById(R.id.errorModeImage);
        topPanel = (LinearLayout) findViewById(R.id.topPanel);
        
        //==========screen's result area==========
		resultArea = new ResultArea(
				(ImageView) findViewById(R.id.firstNumberImage),
				(ImageView) findViewById(R.id.secondNumberImage),
				(ImageView) findViewById(R.id.pointImage),
				(ImageView) findViewById(R.id.thirdNumberImage));
        resultPanel = (LinearLayout) findViewById(R.id.resultPanel);
        
        //==========screen's progress bar==========
        progressBarImage = (ImageView) findViewById(R.id.progressBarImage);
        
        //==========screen's date panel==========
        dateText = (TextView) findViewById(R.id.dateText);
        timeText = (TextView) findViewById(R.id.timeText);
        datePanel = (LinearLayout) findViewById(R.id.datePanel);
        
        //TODO testing code, to be deleted
        batteryImage.setVisibility(View.VISIBLE);
        acImage.setVisibility(View.VISIBLE);
        testingModeImage.setVisibility(View.VISIBLE);
        browsingModeImage.setVisibility(View.VISIBLE);
        uploadingModeImage.setVisibility(View.VISIBLE);
        errorModeImage.setVisibility(View.VISIBLE);
        dateText.setText(TimeFormatter.formatDate(new Date()));
        timeText.setText(TimeFormatter.formatTime(new Date()));
        progressBarImage.setVisibility(View.INVISIBLE);
        
        this.setOnClickListeners();
        
        Beeper.get().attachHandler(this.handler);
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
     * set the screen parts to be visible or not
     * @param visible
     */
    private void setScreenVisible(boolean visible) {
    	if (visible) {
			topPanel.setVisibility(View.VISIBLE);
			resultPanel.setVisibility(View.VISIBLE);
			progressBarImage.setVisibility(View.VISIBLE);
			datePanel.setVisibility(View.VISIBLE);
		}
    	else {
			topPanel.setVisibility(View.INVISIBLE);
			resultPanel.setVisibility(View.INVISIBLE);
			progressBarImage.setVisibility(View.INVISIBLE);
			datePanel.setVisibility(View.INVISIBLE);
		}
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
		//TODO testing code, to be deleted
		ClickStyle style = ClickStyle.get(msg.arg1);
		Log.i("CLICK_STYLE", style.toString());
		Toast.makeText(this, style.toString(), 50).show();
		
		if (style == ClickStyle.DOUBLE_CLICK) {
			Random random = new Random();
			TestResult result = new TestResult(random.nextDouble(), random.nextInt(2), random.nextLong());
			HistoryManager historyManager = new HistoryManager();
			historyManager.addTestResult(this, result);
		}
		else if (style == ClickStyle.SHORT_CLICK) {
			HistoryManager historyManager = new HistoryManager();
			List<TestResult> list = historyManager.getTestResults(this);
			Log.i("TEST_RESULT", "size: " + list.size());
			for (int i = 0; i < list.size(); i++) {
				Log.i("TEST_RESULT", i + " : " + list.get(i).toString());				
			}
		}
		else {
			HistoryManager historyManager = new HistoryManager();
			historyManager.deleteAllTestResults(this);
			
			setScreenVisible(new Random().nextBoolean());
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
			
			return false;
		}
	}
    
}