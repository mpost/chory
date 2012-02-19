package com.moritzpost.chory.view;

import static android.view.ViewGroup.LayoutParams.*;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.moritzpost.chory.ITransfer;
import com.moritzpost.chory.ITransferAdapater;
import com.moritzpost.chory.R;
import com.moritzpost.chory.model.Score;
import com.moritzpost.chory.model.ServoTrack;
import com.moritzpost.chory.model.Track;

public class SimulatorView extends ScrollView implements ITransferAdapater {

	private LayoutInflater inflater;
	private int trackPaddingBottom;
	private LinearLayout parent;
	private NullTransfer transfer;
	private Handler handler;

	public SimulatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Resources res = getContext().getResources();
		parent = new LinearLayout(context);
		int paddingWidth = res.getDimensionPixelSize(R.dimen.track_text_padding);
		parent.setPadding(paddingWidth, paddingWidth, paddingWidth, paddingWidth);

		parent.setOrientation(LinearLayout.VERTICAL);

		addView(parent);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		trackPaddingBottom = res.getDimensionPixelSize(R.dimen.simulator_track_padding_bottom);
		transfer = new NullTransfer();
		handler = new Handler();
	}

	public void loadScore(Score score) {
		ArrayList<Track<?>> tracks = score.getTracks();
		for (int i = 0; i < tracks.size(); i++) {
			Track<?> track = tracks.get(i);
			TextView text = new TextView(getContext());
			text.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
			text.setText(track.getTitle());
			parent.addView(text, WRAP_CONTENT, WRAP_CONTENT);
			ProgressBar progressBar = (ProgressBar) inflater.inflate(R.layout.simulator_progress,
					null);
			progressBar.setMax((int) ServoTrack.MAX_ROTATION);
			progressBar.setTag(track);
			progressBar.setPadding(0, 0, 0, trackPaddingBottom);
			Drawable drawable = progressBar.getProgressDrawable();
			drawable.setColorFilter(new LightingColorFilter(0xFF000000, track.getColor()));
			parent.addView(progressBar, FILL_PARENT, WRAP_CONTENT);
		}
	}

	@Override
	public void valueChanged(final Track<?> track, final float value) {
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = parent.getChildAt(i);
			if (child.getTag() == track) {
				((ProgressBar) child).setProgress((int) value);
				return;
			}
		}
	}

	@Override
	public String getName() {
		return getContext().getString(R.string.simulator_transfer_title);
	}

	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public ITransfer getTransfer() {
		return transfer;
	}
}
