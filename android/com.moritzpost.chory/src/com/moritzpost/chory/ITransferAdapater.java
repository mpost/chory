package com.moritzpost.chory;

public interface ITransferAdapater extends ITrackValueChangedListener {

	String getName();

	boolean isSupported();

	ITransfer getTransfer();
}
