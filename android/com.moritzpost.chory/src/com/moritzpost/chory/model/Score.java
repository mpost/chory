package com.moritzpost.chory.model;

import java.util.ArrayList;

public class Score {

	private ArrayList<Track<?>> tracks;
	private int beats;
	private int cadence;

	public Score() {
		tracks = new ArrayList<Track<?>>();
	}

	public void addTrack(Track track) {
		tracks.add(track);
	}

	public ArrayList<Track<?>> getTracks() {
		return tracks;
	}

	public int getBeats() {
		return beats;
	}

	public void setBeats(int beats) {
		this.beats = beats;
	}

	public int getCadence() {
		return cadence;
	}

	public void setCadence(int cadence) {
		this.cadence = cadence;
	}

}
