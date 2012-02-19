package com.moritzpost.chory;

import java.util.ArrayList;
import java.util.HashMap;

import com.moritzpost.chory.model.Score;
import com.moritzpost.chory.model.Track;
import com.moritzpost.chory.model.TrackEvent;

public class ScoreProcessor implements IPositionChangedListener {

	private final Score score;
	private ArrayList<ITrackValueChangedListener> trackValueChangedListeners;
	private HashMap<Track<?>, Float> valueRecorder;

	public ScoreProcessor(Score score) {
		this.score = score;
		trackValueChangedListeners = new ArrayList<ITrackValueChangedListener>();
		valueRecorder = new HashMap<Track<?>, Float>();
		ArrayList<Track<?>> tracks = score.getTracks();
		for (int i = 0; i < tracks.size(); i++) {
			Track<?> track = tracks.get(i);
			valueRecorder.put(track, Float.MIN_VALUE);
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void positionChanged(float pos) {
		ArrayList<Track<?>> tracks = score.getTracks();
		for (int i = 0; i < tracks.size(); i++) {
			Track<?> track = tracks.get(i);
			TrackEvent nextEvent = null;
			ArrayList<?> events = track.getEvents();
			int j = 0;
			for (; j < events.size(); j++) {
				TrackEvent<?> curTrackEvent = (TrackEvent<?>) events.get(j);
				float eventTime = curTrackEvent.time;
				if (eventTime > pos) {
					nextEvent = curTrackEvent;
					break;
				}
			}
			if (nextEvent != null) {
				if (track.isValueChanging(j)) {
					float value = track.calculateTrackValue(pos, nextEvent, j);
					fireTrackValueChanged(track, value);
				}
			}
		}
	}

	private void fireTrackValueChanged(Track<?> track, float value) {
		for (int i = 0; i < trackValueChangedListeners.size(); i++) {
			trackValueChangedListeners.get(i).valueChanged(track, value);
		}
	}

	public void addTrackValueChangedListener(ITrackValueChangedListener listener) {
		trackValueChangedListeners.add(listener);
	}
}
