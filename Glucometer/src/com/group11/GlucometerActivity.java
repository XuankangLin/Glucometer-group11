package com.group11;

import java.util.Random;

import android.R.bool;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class GlucometerActivity extends Activity {
    /** Called when the activity is first created. */
	ImageView button;
	boolean buttonDown = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        button = (ImageView) findViewById(R.id.imageButton4);
        button.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				button.setImageResource(R.drawable.button_down);
				buttonDown = true;
				// TODO Auto-generated method stub
				return false;
			}
		});
        
        ImageButton imageButton = new ImageButton(this);
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				button.setImageResource(R.drawable.button_up);
				buttonDown = false;
//				if (buttonDown) {
//				}
//				else {
//					button.setImageResource(R.drawable.button_down);
//					buttonDown = true;
//				}
			}
		});
        
        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        final ImageView button = (ImageView) findViewById(R.id.imageButton1);
        
        imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Random random = new Random();
				if (random.nextBoolean()) {
					button.setImageResource(R.drawable.test_strip_invalid);					
				}
				else {
					button.setImageResource(R.drawable.test_strip_valid);
				}
			}
		});
    }
}