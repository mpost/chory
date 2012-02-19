package com.moritzpost.chory;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;

import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;

public class SelectTransferActionProvider extends ActionProvider {

	static final int LIST_LENGTH = 3;

	Context mContext;

	private TransferAdapterManager transferManager;

	public SelectTransferActionProvider(Context context) {
		super(context);
	}

	@Override
	public View onCreateActionView() {
		return null;
	}

	@Override
	public boolean hasSubMenu() {
		return true;
	}

	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		subMenu.clear();
		ArrayList<ITransferAdapater> transferAdapters = transferManager.getAdapters();
		for (int i = 0; i < transferAdapters.size(); i++) {
			final ITransferAdapater transferAdapater = transferAdapters.get(i);
			String name = transferAdapater.getName();
			MenuItem menuItem = subMenu.add(name);
			menuItem.setCheckable(true);
			menuItem.setChecked(transferManager.isActive(transferAdapater));
			menuItem.setEnabled(transferAdapater.getTransfer().isConnected());
			menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if (transferManager.isActive(transferAdapater)) {
						transferManager.setActive(transferAdapater, false);
					} else {
						transferManager.setActive(transferAdapater, true);
					}
					return true;
				}
			});
		}
	}

	public void setTransferManager(TransferAdapterManager transferManager) {
		this.transferManager = transferManager;
	}
}