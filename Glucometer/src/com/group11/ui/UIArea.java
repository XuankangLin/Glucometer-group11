package com.group11.ui;

import android.view.View;
import android.widget.LinearLayout;

public abstract class UIArea {

	/**
	 * the panel containing all the other UI objects
	 */
	protected final LinearLayout panel;
	
	public UIArea(LinearLayout panel) {
		this.panel = panel;
	}
	
	/**
	 * set the whole visibility according to @param visible
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			this.panel.setVisibility(View.VISIBLE);
		}
		else {
			this.panel.setVisibility(View.INVISIBLE);
		}
	}
}
