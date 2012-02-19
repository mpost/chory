package com.moritzpost.chory.view;

import com.moritzpost.chory.IConnectionListener;
import com.moritzpost.chory.ITransfer;

public class NullTransfer implements ITransfer {

	private IConnectionListener listener;

	@Override
	public void init() {
		// nothing to do here
	}

	@Override
	public void connect() {
		listener.connectionEstablished(this);
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public void disconnect() {
		listener.connectionClosed(this);
	}

	@Override
	public void destroy() {
		// nothing to do here
	}

	@Override
	public void setConnectionListener(IConnectionListener listener) {
		this.listener = listener;
	}

}
