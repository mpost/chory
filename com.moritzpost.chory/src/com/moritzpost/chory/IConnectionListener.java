package com.moritzpost.chory;

public interface IConnectionListener {

	void connectionEstablished(ITransfer transfer);

	void connectionAttemptFailed(ITransfer transfer);

	void connectionClosed(ITransfer transfer);
}
