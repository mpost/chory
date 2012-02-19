package com.moritzpost.chory.model;

public class ServoState implements ITrackState {

	private float angle;

	public ServoState(int angle) {
		this.angle = angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public float getAngle() {
		return angle;
	}

}
