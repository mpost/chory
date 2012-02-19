package com.moritzpost.chory.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class UnscrollableHorizontalScrollView extends HorizontalScrollView {

	public UnscrollableHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}

}
