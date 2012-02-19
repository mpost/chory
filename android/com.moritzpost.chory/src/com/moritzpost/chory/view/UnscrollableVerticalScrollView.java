package com.moritzpost.chory.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class UnscrollableVerticalScrollView extends ScrollView {

	public UnscrollableVerticalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}

}
