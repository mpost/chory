package com.moritzpost.chory.usb.accessory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.moritzpost.chory.ChoryActivity;

public class AccessoryPermissionBroadcastReceiver extends BroadcastReceiver {

	private final UsbAccessoryTransfer transfer;

	public AccessoryPermissionBroadcastReceiver(UsbAccessoryTransfer transfer) {
		this.transfer = transfer;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (UsbAccessoryTransfer.ACTION_USB_PERMISSION.equals(action)) {
			synchronized (transfer) {
				UsbAccessory accessory = (UsbAccessory) intent
						.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
					transfer.openAccessory(accessory);
				} else {
					transfer.connectionAttemptFailed();
					Log.d(ChoryActivity.LOG_TAG, "Permission denied for accessory " + accessory);
				}
				transfer.setPermissionRequestPending(false);
			}
		} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
			UsbAccessory accessory = (UsbAccessory) intent
					.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
			if (accessory != null && accessory.equals(transfer.getAccessory())) {
				transfer.disconnect();
			}
		}
	}
}
