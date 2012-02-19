package com.moritzpost.chory.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.moritzpost.chory.IPositionChangedListener;
import com.moritzpost.chory.R;
import com.moritzpost.chory.model.Score;
import com.moritzpost.chory.model.Track;

public class ScoreView extends FrameLayout implements IPositionChangedListener {

	private Score score;
	private ScoreTrackHeaderView trackHeader;
	private IScoreViewContent scoreContent;
	private ScoreStepsView scoreSteps;
	private ScoreVerticalScrollView verticalScrollView;
	private ScoreHorizontalScrollView horizontalScrollView;

	public ScoreView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		trackHeader = (ScoreTrackHeaderView) findViewById(R.id.score_track_header);
		scoreSteps = (ScoreStepsView) findViewById(R.id.score_steps);
		horizontalScrollView = (ScoreHorizontalScrollView) findViewById(R.id.score_horizontal_scrollview);
		verticalScrollView = (ScoreVerticalScrollView) findViewById(R.id.score_vertical_scrollview);
		if (verticalScrollView.getChildCount() == 1) {
			View childView = verticalScrollView.getChildAt(0);
			if (childView instanceof IScoreViewContent) {
				scoreContent = (IScoreViewContent) childView;
			} else {
				throw new IllegalStateException("The child of the " + ScoreView.class
						+ " is not of type " + IScoreViewContent.class + ". Received "
						+ childView.getClass());
			}
		} else {
			throw new IllegalStateException("The " + ScoreView.class
					+ " has to have excatly one child of type " + IScoreViewContent.class);
		}
		scoreContent.setAnchorTooltipView((LinearLayout) findViewById(R.id.anchor_tooltip));
		scoreSteps.setScoreViewConten(scoreContent);
		trackHeader.setScoreViewConten(scoreContent);
		ScrollView trackScrollView = (ScrollView) findViewById(R.id.score_track_scroll_view);
		verticalScrollView.setSynchScrollView(trackScrollView);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		scoreContent.setAbsolutOffsetX(trackHeader.getWidth() - horizontalScrollView.getScrollX());
		scoreContent.setAbsolutOffsetY(scoreSteps.getHeight() - verticalScrollView.getScrollY());
	}

	private void updateTrackHeader() {
		ArrayList<Track<?>> tracks = score.getTracks();
		for (int i = 0; i < tracks.size(); i++) {
			TextView trackTitleText = (TextView) inflate(getContext(), R.layout.score_track_title,
					null);
			trackTitleText.setText(tracks.get(i).getTitle());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
			params.weight = 1.0f;
			trackHeader.addView(trackTitleText, params);
		}
	}

	private void updateCadenceSteps() {
		int count = (int) Math.ceil(score.getBeats() / (double) score.getCadence());
		for (int i = 1; i <= count; i++) {
			TextView stepText = (TextView) inflate(getContext(), R.layout.score_step_text, null);
			stepText.setText(String.valueOf(i));
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
			scoreSteps.addView(stepText, params);
		}
	}

	public void loadScore(Score score) {
		this.score = score;
		scoreContent.loadScore(score);
		updateTrackHeader();
		updateCadenceSteps();
	}

	@Override
	public void positionChanged(float pos) {
		scoreContent.playbackPositionChanged(pos);
	}

}
