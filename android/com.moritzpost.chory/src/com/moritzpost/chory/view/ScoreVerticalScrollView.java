package com.moritzpost.chory.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.moritzpost.chory.R;

public class ScoreVerticalScrollView extends ScrollView {

	private TimelineView timelineView;
	private ScrollView synchScrollView;

	public ScoreVerticalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		timelineView = (TimelineView) findViewById(R.id.timeline);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		synchScrollView.scrollTo(l, t);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (timelineView.isConsumingTouchEvent()) {
			return false;
		}
		boolean intercept = true;
		try {
			intercept = super.onInterceptTouchEvent(ev);
		} catch (Exception e) {
			// can happen sporadically
		}
		return intercept;
	}

	public void setSynchScrollView(ScrollView synchScrollView) {
		this.synchScrollView = synchScrollView;
	}

}
