package com.moritzpost.chory.model;

public class RgbLedTrack extends Track {

	public RgbLedTrack(String title) {
		super(title);
	}

	private static final float MAX_BRIGHTNESS = 255.0f;
	private static final float MIN_BRIGHTNESS = 0.0f;

	public float getMaxBrightness() {
		return MAX_BRIGHTNESS;
	}

	public float getMinBrightness() {
		return MIN_BRIGHTNESS;
	}
}
