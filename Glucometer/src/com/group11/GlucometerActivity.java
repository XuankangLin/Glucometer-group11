package com.group11;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class GlucometerActivity extends Activity {
    /** Called when the activity is first created. */
	ImageView realButtonImage;
	boolean buttonDown = false;
	
	ImageView testStripImage;
	ImageView resetImage;
	ImageView usbImage;
	ImageView acImage;
	
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
				realButtonImage.setImageResource(R.drawable.button_down);
				buttonDown = true;
				return false;
			}
		});
        realButtonImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				realButtonImage.setImageResource(R.drawable.button_up);
				buttonDown = false;
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
			}
		});
        
        resetImage = (ImageView) findViewById(R.id.resetImage);
        resetImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(GlucometerActivity.this, "RESET BUTTON CLICKED", 1000).show();
			}
		});
        
        usbImage = (ImageView) findViewById(R.id.usbImage);
        usbImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(GlucometerActivity.this, "USB BUTTON CLICKED", 1000).show();
			}
		});
        
        acImage = (ImageView) findViewById(R.id.acImage);
        acImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(GlucometerActivity.this, "AC BUTTON CLICKED", 1000).show();
			}
		});
    }
}