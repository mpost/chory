package com.moritzpost.chory;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Player {

	private static final float FPS = 60f;
	private static final long SLEEP_DURATION = Math.round(1f / FPS * 1000);

	private ExecutorService executor;
	private ArrayList<IPositionChangedListener> positionChangedListeners;

	private int tempo;
	private float pos;
	private long lastUpdateTime;
	private boolean playing;
	private boolean reseting;

	public Player() {
		executor = Executors.newSingleThreadExecutor();
		positionChangedListeners = new ArrayList<IPositionChangedListener>();
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	public void play() {
		playing = true;
		lastUpdateTime = System.currentTimeMillis();
		executor.submit(new Runnable() {

			@Override
			public void run() {
				while (playing) {
					long curTime = System.currentTimeMillis();
					long elapsed = curTime - lastUpdateTime;
					pos += tempo / 60f * elapsed / 1000f;
					lastUpdateTime = curTime;
					firePositionChanged();
					try {
						Thread.sleep(SLEEP_DURATION);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (reseting) {
					perfomReset();
				}
			}

		});
	}

	protected void firePositionChanged() {
		for (int i = 0; i < positionChangedListeners.size(); i++) {
			positionChangedListeners.get(i).positionChanged(pos);
		}
	}

	private void perfomReset() {
		pos = 0;
		firePositionChanged();
		reseting = false;
	}

	public void pause() {
		playing = false;
	}

	public void reset() {
		if (playing) {
			playing = false;
			reseting = true;
		} else {
			perfomReset();
		}
	}

	public boolean isPlaying() {
		return playing;
	}

	public void addPositionChangedListener(IPositionChangedListener positionChangedListener) {
		positionChangedListeners.add(positionChangedListener);
	}

	public void notifyPositionChangedListeners() {
		firePositionChanged();
	}

}
