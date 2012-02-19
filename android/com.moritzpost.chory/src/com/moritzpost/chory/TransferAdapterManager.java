package com.moritzpost.chory;

import java.util.ArrayList;

import android.util.Log;

import com.moritzpost.chory.model.Track;

public class TransferAdapterManager implements ITrackValueChangedListener, IConnectionListener {

	private ArrayList<ITransferAdapater> adapters;
	private ArrayList<ITransferAdapater> activeAdapters;

	public TransferAdapterManager() {
		adapters = new ArrayList<ITransferAdapater>();
		activeAdapters = new ArrayList<ITransferAdapater>();
	}

	public ArrayList<ITransferAdapater> getAdapters() {
		return adapters;
	}

	public void addTransferAdapter(ITransferAdapater transferAdapter) {
		if (transferAdapter.isSupported()) {
			transferAdapter.getTransfer().setConnectionListener(this);
			adapters.add(transferAdapter);
			setActive(transferAdapter, true);
		}
	}

	public void removeTransferAdapter(ITransferAdapater transferAdapter) {
		adapters.remove(transferAdapter);
	}

	public void init() {
		for (int i = 0; i < adapters.size(); i++) {
			adapters.get(i).getTransfer().init();
		}
	}

	@Override
	public void valueChanged(Track<?> track, float value) {
		for (int i = 0; i < activeAdapters.size(); i++) {
			activeAdapters.get(i).valueChanged(track, value);
		}
	}

	public void connect() {
		for (int i = 0; i < adapters.size(); i++) {
			adapters.get(i).getTransfer().connect();
		}
	}

	public void disconnect() {
		for (int i = 0; i < adapters.size(); i++) {
			adapters.get(i).getTransfer().disconnect();
		}
	}

	public void destroy() {
		for (int i = 0; i < adapters.size(); i++) {
			adapters.get(i).getTransfer().destroy();
		}
	}

	public void setActive(ITransferAdapater transferAdapater, boolean active) {
		if (active) {
			if (!activeAdapters.contains(transferAdapater)) {
				activeAdapters.add(transferAdapater);
			}
		} else {
			activeAdapters.remove(transferAdapater);
		}
	}

	@Override
	public void connectionEstablished(ITransfer transfer) {
		showToast("Connection to transfer established");

	}

	@Override
	public void connectionAttemptFailed(ITransfer transfer) {
		showToast("Attempt to connect to transfer failed");
	}

	@Override
	public void connectionClosed(ITransfer transfer) {
		showToast("Connection to transfer closed");
	}

	public void showToast(String text) {
		Log.d(ChoryActivity.LOG_TAG, text);
	}

	public boolean isActive(ITransferAdapater transferAdapater) {
		return activeAdapters.contains(transferAdapater);
	}
}
