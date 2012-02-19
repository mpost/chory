package com.moritzpost.chory;

import android.os.Bundle;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.moritzpost.chory.model.Score;
import com.moritzpost.chory.model.ServoState;
import com.moritzpost.chory.model.ServoTrack;
import com.moritzpost.chory.usb.accessory.UsbAccessoryTransfer;
import com.moritzpost.chory.usb.accessory.UsbAccessoryTransferAdapter;
import com.moritzpost.chory.view.ScoreView;
import com.moritzpost.chory.view.SimulatorView;
import com.moritzpost.chory.view.TimelineView;

public class ChoryActivity extends SherlockActivity {

	public static final String LOG_TAG = "Chory";

	private TransferAdapterManager tam;
	private Player player;
	private MenuItem playMenuItem;

	private MenuItem selectTransfer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);

		ScoreView scoreView = (ScoreView) findViewById(R.id.score);
		Score score = createDemoScore((TimelineView) scoreView.findViewById(R.id.timeline));
		scoreView.loadScore(score);

		SimulatorView simulatorView = (SimulatorView) findViewById(R.id.simulator);
		simulatorView.loadScore(score);

		UsbAccessoryTransferAdapter adapter = new UsbAccessoryTransferAdapter(this,
				new UsbAccessoryTransfer(this));

		tam = new TransferAdapterManager();
		tam.addTransferAdapter(simulatorView);
		tam.addTransferAdapter(adapter);
		tam.init();

		ScoreProcessor scoreProcessor = new ScoreProcessor(score);
		scoreProcessor.addTrackValueChangedListener(tam);

		player = new Player();
		player.setTempo(120);
		player.addPositionChangedListener(scoreView);
		player.addPositionChangedListener(scoreProcessor);
		player.notifyPositionChangedListeners();
	}

	public Score createDemoScore(TimelineView timelineView) {
		int beats = 25;
		Score score = new Score();
		score.setBeats(beats);
		score.setCadence(4);

		int green = getResources().getColor(R.color.holo_green_light);
		int blue = getResources().getColor(R.color.holo_blue_light);
		int orange = getResources().getColor(R.color.holo_orange_light);

		// RgbLedTrack leftEyeTrack = new RgbLedTrack("Left Eye");
		// score.addTrack(leftEyeTrack);
		// RgbLedTrack rightEyeTrack = new RgbLedTrack("Right Eye");
		// score.addTrack(rightEyeTrack);

		ServoTrack headTrack = new ServoTrack(timelineView, (byte) 0, "Head", blue);
		addState(headTrack, 90, 0);
		addState(headTrack, 180, 2f);
		addState(headTrack, 90, 4f);
		addState(headTrack, 90, beats);
		score.addTrack(headTrack);

		ServoTrack leftArmTrack = new ServoTrack(timelineView, (byte) 1, "Left Arm", green);
		addState(leftArmTrack, 0, 0);
		addState(leftArmTrack, 0, 4);
		addState(leftArmTrack, 180, 6);
		addState(leftArmTrack, 0, 8);
		addState(leftArmTrack, 0, beats);
		score.addTrack(leftArmTrack);

		ServoTrack rightArmTrack = new ServoTrack(timelineView, (byte) 2, "Right Arm", green);
		rightArmTrack.setInverse(true);
		addState(rightArmTrack, 0, 0);
		addState(rightArmTrack, 0, 8);
		addState(rightArmTrack, 180, 10);
		addState(rightArmTrack, 0, 12);
		addState(rightArmTrack, 0, beats);
		score.addTrack(rightArmTrack);

		ServoTrack torsoTrack = new ServoTrack(timelineView, (byte) 3, "Torso", orange);
		addState(torsoTrack, 90, 0);
		addState(torsoTrack, 90, 12);
		addState(torsoTrack, 180, 14);
		addState(torsoTrack, 90, 16);
		addState(torsoTrack, 90, beats);
		score.addTrack(torsoTrack);

		return score;
	}

	public void addState(ServoTrack track, int angle, float time) {
		track.addState(new ServoState(angle), time);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.chory_menu, menu);
		playMenuItem = menu.findItem(R.id.play_menu_item);
		MenuItem rewindMenuItem = menu.findItem(R.id.rewind_menu_item);

		rewindMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				resetPlayer();
				return true;
			}
		});

		playMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (player.isPlaying()) {
					pausePlayer();
				} else {
					startPlayer();
				}
				return true;
			}

		});

		selectTransfer = menu.findItem(R.id.select_transfer_menu_item);
		SelectTransferActionProvider actionProvider = (SelectTransferActionProvider) selectTransfer
				.getActionProvider();
		actionProvider.setTransferManager(tam);
		return true;
	}

	public void startPlayer() {
		player.play();
		playMenuItem.setIcon(android.R.drawable.ic_media_pause);
	}

	public void pausePlayer() {
		player.pause();
		playMenuItem.setIcon(android.R.drawable.ic_media_play);
	}

	public void resetPlayer() {
		player.reset();
		playMenuItem.setIcon(android.R.drawable.ic_media_play);
	}

	@Override
	protected void onResume() {
		super.onResume();
		tam.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		tam.disconnect();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tam.destroy();
	}

}