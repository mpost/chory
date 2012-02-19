package com.moritzpost.chory.model;

import java.util.HashMap;

import android.graphics.Point;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.moritzpost.chory.view.TimelineView;

public class ServoTrack extends Track<ServoState> {

	public static final float MAX_ROTATION = 180.0f;
	public static final float MIN_ROTATION = 0.0f;
	private final TimelineView tv;
	private final byte id;
	private boolean inverse;

	public ServoTrack(TimelineView timelineView, byte id, String title, int color) {
		super(title, color);
		this.id = id;
		this.tv = timelineView;
	}

	@Override
	public void calculateAnchors(boolean update, int pos) {
		HashMap<TrackEvent<?>, Point> anchorMap = tv.getAnchorMap();
		int trackHeight = tv.getTrackHeight();
		int anchorRadius = tv.getAnchorRadius();
		float beatsWidth = tv.getBeatsWidth();

		float startY = trackHeight * pos + trackHeight - anchorRadius;

		for (int j = 0; j < events.size(); j++) {
			TrackEvent<ServoState> event = events.get(j);
			ServoState state = event.state;
			float x = event.time * beatsWidth;
			float angle = state.getAngle();
			float y = startY - (trackHeight - anchorRadius * 2) / MAX_ROTATION * angle;
			if (update) {
				Point point = anchorMap.get(event);
				point.x = Math.round(x);
				point.y = Math.round(y);
			} else {
				anchorMap.put(event, new Point(Math.round(x), Math.round(y)));
			}
		}
	}

	@Override
	public ServoState createState(int trackStartY, int trackEndY, float touchY) {
		return new ServoState(Math.round(calculateAngle(trackStartY, trackEndY, touchY)));
	}

	private float calculateAngle(int trackStartY, int trackEndY, float touchY) {
		float relativeTouchY = touchY - trackStartY;
		int trackHeightWithoutPadding = trackEndY - trackStartY;
		return Math.abs(relativeTouchY / trackHeightWithoutPadding * MAX_ROTATION - MAX_ROTATION);
	}

	@Override
	public void updateTrackEvent(TrackEvent<ServoState> event, int trackStartY, int trackEndY,
			float touchY) {
		ServoState servoAction = event.state;
		float angle = calculateAngle(trackStartY, trackEndY, touchY);
		servoAction.setAngle(angle);
	}

	@Override
	public void updateAnchorTooltip(TrackEvent<ServoState> trackEvent) {
		String angleStr = String.valueOf(Math.round(trackEvent.state.getAngle()));
		tv.getAnchorTooltipText().setText(angleStr);
		Point point = tv.getAnchorMap().get(trackEvent);
		LinearLayout anchorTooltipView = tv.getAnchorTooltipView();
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) anchorTooltipView
				.getLayoutParams();

		params.leftMargin = tv.getAbsolutOffsetX() + point.x - anchorTooltipView.getWidth() / 2;
		params.topMargin = tv.getAbsolutOffsetY() + point.y - (int) (tv.getTouchRadius() * 1.5f)
				- anchorTooltipView.getHeight();
	}

	@Override
	public float calculateTrackValue(float pos, TrackEvent<ServoState> nextEvent, int nextEventPos) {
		float prevAngle = 0;
		float prevEventTime = 0;
		if (nextEventPos > 0) {
			TrackEvent<ServoState> prevEvent = events.get(nextEventPos - 1);
			prevEventTime = prevEvent.time;
			prevAngle = prevEvent.state.getAngle();
		}
		float nextAngle = nextEvent.state.getAngle();
		float gradient = nextAngle - prevAngle;
		return (pos - prevEventTime) / (nextEvent.time - prevEventTime) * gradient + prevAngle;
	}

	@Override
	public boolean isValueChanging(int nextEventPos) {
		if (nextEventPos <= 1) {
			return true;
		}
		TrackEvent<ServoState> nextEvent = events.get(nextEventPos);
		TrackEvent<ServoState> prevEvent = events.get(nextEventPos - 1);
		return nextEvent.state.getAngle() != prevEvent.state.getAngle();
	}

	public byte getId() {
		return id;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	public boolean isInverse() {
		return inverse;
	}
}
