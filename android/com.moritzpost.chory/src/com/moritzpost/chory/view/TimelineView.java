package com.moritzpost.chory.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moritzpost.chory.R;
import com.moritzpost.chory.model.Score;
import com.moritzpost.chory.model.Track;
import com.moritzpost.chory.model.TrackEvent;
import com.moritzpost.chory.util.ScalableInt;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class TimelineView extends View implements IScoreViewContent, OnScaleGestureListener {

	private Score score;

	private float scale = 1;
	private ScaleGestureDetector scaleDetector;
	private GestureDetector gestureDetector;

	private Paint trackPaint;
	private Paint bgPaint;
	private Paint cadenceCounterPaint;
	private Paint curvePaint;
	private Paint anchorFillPaint;
	private Paint anchorStrokePaint;
	private Paint playbackPaint;

	private ArrayList<ScalableInt> scalableValues;
	private ScalableInt trackHeight;
	private ScalableInt beatsWidth;
	private ScalableInt sepWidth;
	private ScalableInt curveWidth;
	private ScalableInt anchorRadius;
	private ScalableInt anchorStrokeWidth;
	private ScalableInt playbackIndicatorWidth;

	private HashMap<TrackEvent<?>, Point> anchorMap;
	private TrackEvent activeTrackEvent;
	private Track<?> activeTrack;
	private double touchRadius;

	private LinearLayout anchorTooltipView;
	private TextView anchorTooltipText;

	private int absolutOffsetX;

	private int absolutOffsetY;

	private float playbackPos;

	public TimelineView(Context context) {
		super(context);
	}

	public TimelineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		scaleDetector = new ScaleGestureDetector(context, this);
		anchorMap = new HashMap<TrackEvent<?>, Point>();
		gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener());
		gestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {

			@Override
			public boolean onSingleTapConfirmed(MotionEvent event) {
				createTrackEvent(event);
				return true;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				deleteTrackEvent(e);
				return true;
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		createScalableValue();
		createPaints();
		touchRadius = getResources().getDimension(R.dimen.touch_radius);
		if (isInEditMode()) {
			score = ScoreDummy.getScore();
		}
	}

	private void deleteTrackEvent(MotionEvent e) {
		TrackEvent trackEvent = findTrackEvent(e.getX(), e.getY());
		if (trackEvent != null) {
			anchorMap.remove(trackEvent);
			Track<?> track = findTrack(trackEvent);
			track.remove(trackEvent);
			invalidate();
		}
	}

	private void createScalableValue() {
		scalableValues = new ArrayList<ScalableInt>();
		beatsWidth = createScalableValue(R.dimen.cadence_default_width);
		sepWidth = createScalableValue(R.dimen.timeline_default_separator_size);
		trackHeight = createScalableValue(R.dimen.track_default_height);
		curveWidth = createScalableValue(R.dimen.curve_width);
		anchorRadius = createScalableValue(R.dimen.anchor_radius);
		anchorStrokeWidth = createScalableValue(R.dimen.anchor_stroke_width);
		playbackIndicatorWidth = createScalableValue(R.dimen.playback_indicator_width);
	}

	private void createPaints() {
		Resources res = getResources();
		trackPaint = new Paint();
		trackPaint.setColor(res.getColor(R.color.score_track_background));
		bgPaint = new Paint();
		bgPaint.setColor(res.getColor(R.color.activity_background));
		cadenceCounterPaint = new Paint();
		cadenceCounterPaint.setColor(Color.WHITE);
		cadenceCounterPaint.setTextAlign(Align.CENTER);
		cadenceCounterPaint.setAntiAlias(true);
		curvePaint = new Paint();
		curvePaint.setAntiAlias(true);
		curvePaint.setStrokeJoin(Paint.Join.ROUND);
		curvePaint.setStrokeCap(Paint.Cap.ROUND);
		curvePaint.setStyle(Style.STROKE);
		playbackPaint = new Paint();
		playbackPaint.setColor(res.getColor(R.color.playback_indicator));
		anchorFillPaint = new Paint();
		anchorFillPaint.setStyle(Style.FILL);
		anchorFillPaint.setColor(res.getColor(R.color.holo_green_dark));
		anchorStrokePaint = new Paint();
		anchorStrokePaint.setStyle(Style.STROKE);
		anchorStrokePaint.setColor(res.getColor(R.color.anchor_stroke_paint));
		anchorStrokePaint.setAntiAlias(true);
	}

	private ScalableInt createScalableValue(int valueResource) {
		ScalableInt scalableInt = new ScalableInt(getResources().getDimensionPixelSize(
				valueResource));
		scalableValues.add(scalableInt);
		return scalableInt;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		performMeasure();
	}

	public void performMeasure() {
		int width = Math.round(score.getBeats() * beatsWidth.get());
		int height = score.getTracks().size() * trackHeight.get();
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		ArrayList<Track<?>> tracks = score.getTracks();
		drawBackground(canvas);
		drawCadence(canvas);
		drawTrackSeparators(canvas, tracks);
		drawCurve(canvas, tracks);
		drawAnchorPoints(canvas);
		drawPlaybackIndicator(canvas);
	}

	private void drawPlaybackIndicator(Canvas canvas) {
		float left = playbackPos * beatsWidth.get();
		float top = 0;
		float right = left + playbackIndicatorWidth.get();
		float bottom = getHeight();
		canvas.drawRect(left, top, right, bottom, playbackPaint);
	}

	private void drawAnchorPoints(Canvas canvas) {
		anchorStrokePaint.setStrokeWidth(anchorStrokeWidth.get());
		ArrayList<Track<?>> tracks = score.getTracks();
		for (int i = 0; i < tracks.size(); i++) {
			Track<?> track = tracks.get(i);
			anchorFillPaint.setColor(track.getColor());
			ArrayList<?> events = track.getEvents();
			for (int j = 0; j < events.size(); j++) {
				TrackEvent<?> trackEvent = (TrackEvent<?>) events.get(j);
				Point point = anchorMap.get(trackEvent);
				canvas.drawCircle(point.x, point.y, anchorRadius.get(), anchorFillPaint);
				canvas.drawCircle(point.x, point.y, anchorRadius.get(), anchorStrokePaint);
			}
		}
	}

	private void drawCurve(Canvas canvas, ArrayList<Track<?>> tracks) {
		for (int i = 0; i < tracks.size(); i++) {
			Track<?> track = tracks.get(i);
			ArrayList<?> events = track.getEvents();

			curvePaint.setColor(track.getColor());
			curvePaint.setStrokeWidth(curveWidth.get());

			float startX = 0;
			float startY = trackHeight.get() * i + trackHeight.get() - anchorRadius.get();
			float[] line = new float[4 + events.size() * 4];

			int k = 0;
			line[k++] = startX;
			line[k++] = startY;
			for (int j = 0; j < events.size(); j++) {
				TrackEvent<?> event = (TrackEvent<?>) events.get(j);
				Point point = anchorMap.get(event);
				line[k++] = point.x;
				line[k++] = point.y;
				line[k++] = point.x;
				line[k++] = point.y;
			}
			line[k++] = getWidth();
			line[k++] = startY;
			canvas.drawLines(line, curvePaint);
		}
	}

	private void drawBackground(Canvas canvas) {
		Rect bgRect = new Rect(0, 0, getWidth(), getHeight());
		canvas.drawRect(bgRect, trackPaint);
	}

	private void drawCadence(Canvas canvas) {
		int tracksSize = score.getTracks().size();
		for (int i = 1; i < score.getBeats(); i++) {
			if (i % score.getCadence() == 0) {
				int x = i * beatsWidth.get();
				Rect bgCadence = new Rect(x - sepWidth.get() / 2, 0, x + sepWidth.get() / 2,
						trackHeight.get() * tracksSize);
				canvas.drawRect(bgCadence, bgPaint);
			}
		}
	}

	private void drawTrackSeparators(Canvas canvas, ArrayList<Track<?>> tracks) {
		Rect trackSepRect = new Rect(0, 0, getWidth(), sepWidth.get());
		for (int i = 1; i < tracks.size(); i++) {
			trackSepRect.top = trackHeight.get() * i - sepWidth.get() / 2;
			trackSepRect.bottom = trackSepRect.top + sepWidth.get();
			canvas.drawRect(trackSepRect, bgPaint);
		}
	}

	@Override
	public void loadScore(Score score) {
		this.score = score;
		calculateAnchors(false);
	}

	private void calculateAnchors(boolean update) {
		ArrayList<Track<?>> tracks = score.getTracks();
		if (!update) {
			anchorMap.clear();
		}
		for (int i = 0; i < tracks.size(); i++) {
			Track<?> track = tracks.get(i);
			track.calculateAnchors(update, i);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		scaleDetector.onTouchEvent(event);
		gestureDetector.onTouchEvent(event);
		if (!scaleDetector.isInProgress()) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					activeTrackEvent = findTrackEvent(event.getX(), event.getY());
					if (activeTrackEvent != null) {
						activeTrack = findTrack(activeTrackEvent);
						showAnchorTooltip();
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (activeTrackEvent != null) {
						updateActiveTrackEvent(event);
						updateAnchorTooltip();
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					if (activeTrackEvent != null && activeTrack != null) {
						hideAnchorTooltip();
						activeTrackEvent = null;
						activeTrack = null;
					}
					break;
			}
		}

		return true;
	}

	private void showAnchorTooltip() {
		anchorTooltipView.setVisibility(View.VISIBLE);
		updateAnchorTooltip();
		Animation anim = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
		anchorTooltipView.startAnimation(anim);

	}

	private void updateAnchorTooltip() {
		activeTrack.updateAnchorTooltip(activeTrackEvent);
	}

	private void hideAnchorTooltip() {
		Animation anim = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
		anchorTooltipView.startAnimation(anim);
		anim.setDuration(500);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				anchorTooltipView.setVisibility(View.GONE);
			}
		});
	}

	private void createTrackEvent(MotionEvent event) {
		float beatTime = Math.round(event.getX() / beatsWidth.get());
		int i = getSelectedTrackIndex(event);
		Track<?> track = score.getTracks().get(i);
		if (track.canInsertStateAt(beatTime)) {
			int trackStartY = getTrackStartY(i);
			int trackEndY = getTrackEndY(trackStartY);
			float touchY = event.getY();
			if (touchY >= trackStartY && touchY <= trackEndY) {
				TrackEvent<?> trackEvent = track.createTrackEvent(trackStartY, trackEndY, touchY,
						beatTime);
				Point point = new Point((int) beatTime * beatsWidth.get(), (int) touchY);
				anchorMap.put(trackEvent, point);
				invalidate(trackStartY, trackEndY);
			}
		}
	}

	private int getSelectedTrackIndex(MotionEvent event) {
		return (int) Math.floor(event.getY() / trackHeight.get());
	}

	private Track<?> findTrack(TrackEvent<?> trackEvent) {
		ArrayList<Track<?>> tracks = score.getTracks();
		for (int i = 0; i < tracks.size(); i++) {
			Track<?> track = tracks.get(i);
			ArrayList<?> events = track.getEvents();
			for (int j = 0; j < events.size(); j++) {
				TrackEvent<?> curTrackEvent = (TrackEvent<?>) events.get(j);
				if (curTrackEvent.equals(trackEvent)) {
					return track;
				}
			}
		}
		return null;
	}

	private void updateActiveTrackEvent(MotionEvent event) {
		int i = getSelectedTrackIndex();
		int trackStartY = getTrackStartY(i);
		int trackEndY = getTrackEndY(trackStartY);
		float touchY = event.getY();
		if (touchY >= trackStartY && touchY <= trackEndY) {
			anchorMap.get(activeTrackEvent).y = Math.round(touchY);
			activeTrack.updateTrackEvent(activeTrackEvent, trackStartY, trackEndY, touchY);
		}
		invalidate(trackStartY, trackEndY);
	}

	private void invalidate(int trackStartY, int trackEndY) {
		invalidate(0, trackStartY - anchorRadius.get() * 2, getWidth(),
				trackEndY + anchorRadius.get());
	}

	private int getTrackEndY(int trackStartY) {
		return trackStartY + trackHeight.get() - anchorRadius.get() * 2;
	}

	private int getTrackStartY(int i) {
		return trackHeight.get() * i + anchorRadius.get();
	}

	private int getSelectedTrackIndex() {
		ArrayList<Track<?>> tracks = score.getTracks();
		int i = 0;
		for (; i < tracks.size(); i++) {
			if (tracks.get(i).equals(activeTrack)) {
				break;
			}
		}
		return i;
	}

	private TrackEvent<?> findTrackEvent(float x, float y) {
		Set<Entry<TrackEvent<?>, Point>> entries = anchorMap.entrySet();
		for (Entry<TrackEvent<?>, Point> entry : entries) {
			Point point = entry.getValue();
			if (distance(point.x, point.y, x, y) <= touchRadius) {
				return entry.getKey();
			}
		}
		return null;
	}

	private static double distance(float x1, float y1, float x2, float y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		scale *= detector.getScaleFactor();

		// int parentWidth = ((View) getParent().getParent()).getWidth();
		// float proportinalMinScale = parentWidth / (beatsWidth * (float) score.getSteps());
		// float min = Math.max(proportinalMinScale, 0.25f);
		scale = Math.max(0.2f, Math.min(scale, 5.0f));

		scaleValues();
		calculateAnchors(true);

		requestLayout();
		return true;
	}

	private void scaleValues() {
		for (int i = 0; i < scalableValues.size(); i++) {
			scalableValues.get(i).scale(scale);
		}
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		// nothing to do here
	}

	public boolean isConsumingTouchEvent() {
		return scaleDetector.isInProgress() || activeTrackEvent != null;
	}

	@Override
	public void setAnchorTooltipView(LinearLayout anchorTooltipView) {
		this.anchorTooltipView = anchorTooltipView;
		anchorTooltipText = (TextView) anchorTooltipView.findViewById(R.id.anchor_tooltip_text);
	}

	@Override
	public void setAbsolutOffsetX(int offsetX) {
		this.absolutOffsetX = offsetX;
	}

	@Override
	public void setAbsolutOffsetY(int offsetY) {
		this.absolutOffsetY = offsetY;
	}

	@Override
	public void playbackPositionChanged(float playbackPos) {
		this.playbackPos = playbackPos;
		postInvalidate();
	}

	public int getTrackHeight() {
		return trackHeight.get();
	}

	public int getAnchorRadius() {
		return anchorRadius.get();
	}

	public float getBeatsWidth() {
		return beatsWidth.get();
	}

	public HashMap<TrackEvent<?>, Point> getAnchorMap() {
		return anchorMap;
	}

	public TextView getAnchorTooltipText() {
		return anchorTooltipText;
	}

	public LinearLayout getAnchorTooltipView() {
		return anchorTooltipView;
	}

	public int getAbsolutOffsetX() {
		return absolutOffsetX;
	}

	public int getAbsolutOffsetY() {
		return absolutOffsetY;
	}

	public double getTouchRadius() {
		return touchRadius;
	}

}
