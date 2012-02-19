package com.moritzpost.chory;

import com.moritzpost.chory.model.Track;

public interface ITrackValueChangedListener {

	void valueChanged(Track<?> track, float value);

}
