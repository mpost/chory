package com.moritzpost.chory.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.moritzpost.chory.R;

public class ScoreHorizontalScrollView extends HorizontalScrollView {

	private TimelineView timelineView;
	private HorizontalScrollView scoreStepsScrollView;

	public ScoreHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		timelineView = (TimelineView) findViewById(R.id.timeline);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		scoreStepsScrollView = (HorizontalScrollView) ((ViewGroup) getParent())
				.findViewById(R.id.score_steps_scroll_view);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		scoreStepsScrollView.scrollTo(l, t);
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

}
