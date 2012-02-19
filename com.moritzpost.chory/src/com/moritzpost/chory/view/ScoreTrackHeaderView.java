package com.moritzpost.chory.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class ScoreTrackHeaderView extends LinearLayout {

	private IScoreViewContent content;

	public ScoreTrackHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, content.getHeight());
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		int childCount = getChildCount();
		float step = getHeight() / (float) childCount;
		float distance = 0;
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			float offset = step / 2f - child.getHeight() / 2;
			int top = (int) (distance + offset);
			child.layout(child.getLeft(), top, child.getRight(), top + child.getHeight());
			distance += step;
		}
	}

	public void setScoreViewConten(IScoreViewContent content) {
		this.content = content;
	}
}
