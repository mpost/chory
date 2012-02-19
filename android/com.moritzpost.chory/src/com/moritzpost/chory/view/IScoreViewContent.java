package com.moritzpost.chory.view;

import android.widget.LinearLayout;

import com.moritzpost.chory.model.Score;

public interface IScoreViewContent {

	void loadScore(Score score);

	int getWidth();

	int getHeight();

	void setAnchorTooltipView(LinearLayout anchorTooltipView);

	void setAbsolutOffsetX(int offsetX);

	void setAbsolutOffsetY(int offsetY);

	void playbackPositionChanged(float pos);
}
