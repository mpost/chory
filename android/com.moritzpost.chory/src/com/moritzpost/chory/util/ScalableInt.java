package com.moritzpost.chory.util;

public class ScalableInt {

	private final int baseValue;
	private int curValue;

	public ScalableInt(int baseValue) {
		this.baseValue = baseValue;
		this.curValue = baseValue;
	}

	public void scale(float factor) {
		curValue = Math.round(baseValue * factor);
	}

	public int get() {
		return curValue;
	}

}
