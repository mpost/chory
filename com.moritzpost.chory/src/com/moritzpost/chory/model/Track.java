package com.moritzpost.chory.model;

import java.util.ArrayList;

public class Track<T extends ITrackState> {

	protected ArrayList<TrackEvent<T>> events;
	private String title;
	private int color;

	public Track(String title) {
		this.title = title;
		events = new ArrayList<TrackEvent<T>>();
	}

	public Track(String title, int color) {
		this(title);
		this.color = color;
	}

	public TrackEvent<T> addState(T state, float time) {
		for (int i = 0; i < events.size(); i++) {
			TrackEvent<T> item = events.get(i);
			if (item.time > time) {
				return addStateAt(state, time, i);
			}
		}
		return addStateAt(state, time, events.size());
	}

	private TrackEvent<T> addStateAt(T state, float time, int index) {
		TrackEvent<T> newItem = new TrackEvent<T>();
		newItem.state = state;
		newItem.time = time;
		events.add(index, newItem);
		return newItem;
	}

	public ArrayList<TrackEvent<T>> getEvents() {
		return events;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public TrackEvent<?> findTrackEvent(float pos) {
		for (int i = 0; i < events.size(); i++) {
			TrackEvent<?> trackEvent = events.get(i);
			float eventTime = trackEvent.time;
			if (eventTime > pos) {
				return trackEvent;
			}
		}
		return null;
	}

	public boolean canInsertStateAt(float time) {
		for (int i = 0; i < events.size(); i++) {
			float eventTime = events.get(i).time;
			if (eventTime == time) {
				return false;
			} else if (eventTime > time) {
				return true;
			}
		}
		return true;
	}

	public void remove(TrackEvent<T> trackEvent) {
		events.remove(trackEvent);
	}

	public TrackEvent<T> createTrackEvent(int trackStartY, int trackEndY, float touchY,
			float beatTime) {
		return addState(createState(trackStartY, trackEndY, touchY), beatTime);
	}

	public void calculateAnchors(boolean update, int i) {
		// To be overridden by subclasses
	}

	public T createState(int trackStartY, int trackEndY, float touchY) {
		// To be overridden by subclasses

		return null;
	}

	public void updateTrackEvent(TrackEvent<T> trackEvent, int trackStartY, int trackEndY,
			float touchY) {
		// To be overridden by subclasses
	}

	public void updateAnchorTooltip(TrackEvent<T> trackEvent) {
		// To be overridden by subclasses
	}

	public float calculateTrackValue(float pos, TrackEvent<T> nextEvent, int nextEventPos) {
		// To be overridden by subclasses
		return 0;
	}

	public boolean isValueChanging(int nextEventPos) {
		return true;
	}
}
