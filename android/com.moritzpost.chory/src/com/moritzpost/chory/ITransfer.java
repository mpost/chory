package com.moritzpost.chory;

public interface ITransfer {

	void init();

	void connect();

	boolean isConnected();

	void disconnect();

	void destroy();

	void setConnectionListener(IConnectionListener listener);

}
