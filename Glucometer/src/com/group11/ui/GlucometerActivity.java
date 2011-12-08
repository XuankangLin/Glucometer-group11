package com.group11.ui;

import java.util.Date;
import java.util.Random;

import com.group11.R;
import com.group11.base.ClickStyle;
import com.group11.hardware.Beeper;
import com.group11.util.ClickJudger;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import static com.group11.base.Interrupt.*;


public class GlucometerActivity extends Activity {

	ImageView realButtonImage;
	ImageView testStripImage;
	ImageView resetImage;
	ImageView usbImage;
	ImageView acImage;
	
	
	private ClickJudger judger = null;
	private Handler handler = new Handler(new GlucometerHandlerCallback());
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        
        realButtonImage = (ImageView) findViewById(R.id.buttonImage);
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

        testStripImage = (ImageView) findViewById(R.id.testStripImage);
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
        
        resetImage = (ImageView) findViewById(R.id.resetImage);
        resetImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Beeper.get().doDoubleBeep(GlucometerActivity.this);
				Toast.makeText(GlucometerActivity.this, "double beep", 1000).show();
			}
		});
        
        usbImage = (ImageView) findViewById(R.id.usbImage);
        usbImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Beeper.get().doLongBeep(GlucometerActivity.this);
				Toast.makeText(GlucometerActivity.this, "Long beep", 1000).show();
			}
		});
        
        acImage = (ImageView) findViewById(R.id.acImage);
        acImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Beeper.get().doShortBeep(GlucometerActivity.this);
				Toast.makeText(GlucometerActivity.this, "short beep", 1000).show();
			}
		});
        
        judger = new ClickJudger(handler);
    }
    
    private void doStripInserted() {
    	//TODO
    }
    
    private void doStripPulledOut() {
    	//TODO
    }
    
    private void doUSBConnected() {
    	//TODO
    }

	private void doUSBDisconnected() {
		//TODO
	}
	
	private void doStripValid() {
		//TODO
	}
	
	private void doStripInvalid() {
		//TODO
	}
	
	private void doBloodSufficient() {
		//TODO
	}
	
	private void doBloodInsufficient() {
		//TODO
	}
	
	private void doResultReady() {
		//TODO
	}
	
	private void doAcOn() {
		//TODO
	}
	
	private void doAcOff() {
		//TODO
	}
	
	private void doTimeTick() {
		//TODO
	}
	
	private void doButtonClicked(Message msg) {
		//TODO
		ClickStyle style = ClickStyle.get(msg.arg1);
		Log.i("CLICK_STYLE", style.toString());
		Toast.makeText(this, style.toString(), 50).show();
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
			
			return false;
		}
	}
}