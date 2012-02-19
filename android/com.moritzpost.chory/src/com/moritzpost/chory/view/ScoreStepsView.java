package com.moritzpost.chory.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class ScoreStepsView extends LinearLayout {

	private IScoreViewContent content;

	public ScoreStepsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(content.getWidth(), heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		int childCount = getChildCount();
		float step = getWidth() / (float) childCount;
		float distance = step / 2f;
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.layout((int) distance, child.getTop(), (int) (distance + child.getWidth()),
					child.getBottom());
			distance += step;
		}
	}

	public void setScoreViewConten(IScoreViewContent content) {
		this.content = content;
	}
}
