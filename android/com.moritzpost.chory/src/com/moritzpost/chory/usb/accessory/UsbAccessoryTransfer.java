package com.moritzpost.chory.usb.accessory;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.moritzpost.chory.ChoryActivity;
import com.moritzpost.chory.IConnectionListener;
import com.moritzpost.chory.ITransfer;

public class UsbAccessoryTransfer implements ITransfer {

	static final String ACTION_USB_PERMISSION = "com.moritzpost.chory.action.USB_PERMISSION";

	private final Activity activity;
	private IConnectionListener listener;

	private UsbManager usbManager;
	private UsbAccessory usbAccessory;

	private boolean permissionRequestPending;
	private AccessoryPermissionBroadcastReceiver permissionReceiver;
	private PendingIntent permissionIntent;

	private ParcelFileDescriptor accessoryFileDescriptor;
	private InputStream inputStream;
	private OutputStream outputStream;

	public UsbAccessoryTransfer(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void init() {
		usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
		permissionIntent = PendingIntent.getBroadcast(activity, 0,
				new Intent(ACTION_USB_PERMISSION), 0);
	}

	@Override
	public void connect() {
		registerPermissionReceiver();

		if (isConnected()) {
			listener.connectionEstablished(this);
			return;
		}

		UsbAccessory[] accessories = usbManager.getAccessoryList();
		UsbAccessory accessory = null;
		if (accessories != null) {
			accessory = accessories[0];
			if (usbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (permissionReceiver) {
					if (!permissionRequestPending) {
						usbManager.requestPermission(accessory, permissionIntent);
						permissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(ChoryActivity.LOG_TAG, "The accessory is null");
		}
	}

	public void registerPermissionReceiver() {
		if (permissionReceiver == null) {
			permissionReceiver = new AccessoryPermissionBroadcastReceiver(this);
			IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
			filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
			activity.registerReceiver(permissionReceiver, filter);
		}
	}

	@Override
	public void disconnect() {
		try {
			if (accessoryFileDescriptor != null) {
				accessoryFileDescriptor.close();
			}
		} catch (IOException e) {
			Log.d(ChoryActivity.LOG_TAG, "Could not close accessory FileDesriptor");
		} finally {
			accessoryFileDescriptor = null;
			usbAccessory = null;
			inputStream = null;
			outputStream = null;
			listener.connectionClosed(this);
		}
	}

	@Override
	public void destroy() {
		activity.unregisterReceiver(permissionReceiver);
		permissionReceiver = null;
	}

	void openAccessory(UsbAccessory accessory) {
		accessoryFileDescriptor = usbManager.openAccessory(accessory);
		if (accessoryFileDescriptor != null) {
			usbAccessory = accessory;
			FileDescriptor fd = accessoryFileDescriptor.getFileDescriptor();
			inputStream = new FileInputStream(fd);
			outputStream = new FileOutputStream(fd);
			listener.connectionEstablished(this);
		}
	}

	@Override
	public boolean isConnected() {
		return inputStream != null && outputStream != null;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	void setPermissionRequestPending(boolean permissionRequestPending) {
		this.permissionRequestPending = permissionRequestPending;
	}

	UsbAccessory getAccessory() {
		return usbAccessory;
	}

	void connectionAttemptFailed() {
		listener.connectionAttemptFailed(this);
	}

	@Override
	public void setConnectionListener(IConnectionListener listener) {
		this.listener = listener;
	}

}
