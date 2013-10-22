/*
 * Copyright (C) 2013 Joan Puig Sanz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.servDroid.ui.activity;

import org.servDroid.helper.IPreferenceHelper;
import org.servDroid.helper.IServiceHelper;
import org.servDroid.helper.IStoreHelper;
import org.servDroid.helper.IStoreHelper.OnPurchaseInfoChangedListener;
import org.servDroid.module.AppModule;
import org.servDroid.server.service.ServerValues;
import org.servDroid.ui.fragment.OptionsFragment.OnOptionClickListener;
import org.servDroid.ui.util.DialogFactory;
import org.servDroid.util.NetworkIp;
import org.servDroid.web.R;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.KeyEvent;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ServDroidBaseFragmentActivity extends RoboSherlockFragmentActivity implements
		OnOptionClickListener, OnPurchaseInfoChangedListener {

	public static final int MENU_ID_SHARE = 9875;
	public static final int MENU_ID_HELP = 9875 + 1;
	public static final int MENU_ID_DONATE = 9875 + 2;

	protected Menu appMenu;

	@Inject
	protected IStoreHelper storeHelper;

	@Inject
	protected IPreferenceHelper preferenceHelper;

	@Inject
	protected IServiceHelper serviceHelper;

	@Inject
	@Named(AppModule.HAS_TWO_PANES)
	protected boolean hasTwoPanes;

	private OnActivityKeyUp mOnKeyListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getBoolean(StartActivity.KEY_NEW_VERSION)) {
			DialogFactory.ShowDonateDialog(this, storeHelper);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		appMenu = menu;
		createMenuShare(appMenu);
		createMainMenus(appMenu);
		return true;
	}

	public void setOnKeyListener(OnActivityKeyUp onActivityKeyUp) {
		mOnKeyListener = onActivityKeyUp;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (storeHelper.hasStoreInfo()) {
			storeHelper.handleActivityResult(requestCode, resultCode, data);
		}
	}

	protected void createMenuShare(Menu menu) {
		MenuItem menuItemShare = menu.add(0, MENU_ID_SHARE, 0, R.string.main_menu_share);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			menuItemShare.setIcon(android.R.drawable.ic_menu_share);
		} else {
			menuItemShare.setIcon(R.drawable.ic_menu_share);

		}
		menuItemShare.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case MENU_ID_SHARE:
			share();
			break;
		case MENU_ID_HELP:
			DialogFactory.showHelpDialog(this, preferenceHelper, storeHelper);
			break;
		case MENU_ID_DONATE:
			DialogFactory.ShowDonateDialog(this, storeHelper);
		default:
			onOptionClick(item.getItemId());
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void createMainMenus(Menu menu) {
	}

	private void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");

		String subject;
		String text;
		String shareTitle;

		int serverStatus = -1;
		try {
			if (serviceHelper.isServiceConected()) {
				serverStatus = serviceHelper.getServiceController().getStatus();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		if (serverStatus == ServerValues.STATUS_RUNNING) {
			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			subject = getString(R.string.share_subject_server);
			text = getString(R.string.share_text_server) + " http://"
					+ NetworkIp.getWifiIp(wifiManager);
			if (preferenceHelper.getPort() != 80) {
				text = text + ":" + preferenceHelper.getPort();
			}
			shareTitle = getString(R.string.share_title_link);
		} else {
			subject = getString(R.string.share_subject_servdroid);
			text = getString(R.string.share_text_servdroid, getString(R.string.url_playstore));
			shareTitle = getString(R.string.share_title_servdroid);
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		this.startActivity(Intent.createChooser(intent, shareTitle));
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (mOnKeyListener != null) {
			if (mOnKeyListener.OnKeyUp(this, keyCode, event)) {
				return true;
			}
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			appMenu.performIdentifierAction(0, 1);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onOptionClick(int id) {
	}

	@Override
	public void onPusrcahseInfoChangedListener(IStoreHelper storeHelper) {
		if (!storeHelper.hasStoreInfo()) {
			return;
		}
	}

	public static interface OnActivityKeyUp {
		public boolean OnKeyUp(ServDroidBaseFragmentActivity activity, int keyCode, KeyEvent event);
	}

}
