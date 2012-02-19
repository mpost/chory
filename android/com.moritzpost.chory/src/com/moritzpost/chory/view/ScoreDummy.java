package com.moritzpost.chory.view;

import com.moritzpost.chory.model.Score;
import com.moritzpost.chory.model.Track;

public class ScoreDummy {

	private static Score score;

	public static synchronized Score getScore() {
		if (score == null) {
			score = new Score();
			score.addTrack(new Track("Left Arm"));
			score.addTrack(new Track("Right Arm"));
		}
		return score;
	}
}
