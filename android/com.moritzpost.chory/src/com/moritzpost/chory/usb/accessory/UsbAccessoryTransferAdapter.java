package com.moritzpost.chory.usb.accessory;

import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

import com.moritzpost.chory.ChoryActivity;
import com.moritzpost.chory.ITransfer;
import com.moritzpost.chory.ITransferAdapater;
import com.moritzpost.chory.R;
import com.moritzpost.chory.model.ServoTrack;
import com.moritzpost.chory.model.Track;

public class UsbAccessoryTransferAdapter implements ITransferAdapater {

	private static final byte COMPONENT_SERVO = 0;

	private final UsbAccessoryTransfer transfer;

	private byte[] buffer;

	private final Context context;

	public UsbAccessoryTransferAdapter(Context context, UsbAccessoryTransfer transfer) {
		this.context = context;
		this.transfer = transfer;
		buffer = new byte[3];
	}

	@Override
	public void valueChanged(Track<?> track, float value) {
		if (transfer.isConnected()) {
			if (track instanceof ServoTrack) {
				ServoTrack servoTrack = (ServoTrack) track;
				if (servoTrack.isInverse()) {
					value = (value - 180) * -1;
				}
				sendCommand(COMPONENT_SERVO, servoTrack.getId(), (byte) Math.round(value));
			}
		}
	}

	public void sendCommand(byte component, byte target, byte value) {
		buffer[0] = component;
		buffer[1] = target;
		buffer[2] = value;
		OutputStream outputStream = transfer.getOutputStream();
		if (outputStream != null && buffer[1] != -1) {
			try {
				outputStream.write(buffer);
			} catch (IOException e) {
				Log.e(ChoryActivity.LOG_TAG, "Writing to UsbAccessory failed", e);
			}
		}
	}

	@Override
	public boolean isSupported() {
		return Integer.valueOf(android.os.Build.VERSION.SDK) > 11;
	}

	@Override
	public String getName() {
		return context.getString(R.string.usb_accessory_transfer_title);
	}

	@Override
	public ITransfer getTransfer() {
		return transfer;
	}
}
