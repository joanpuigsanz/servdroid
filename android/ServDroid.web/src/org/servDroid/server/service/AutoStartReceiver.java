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

package org.servDroid.server.service;

import org.servDroid.db.LogMessage;
import org.servDroid.helper.IServiceHelper;
import org.servDroid.helper.PreferenceHelper;
import org.servDroid.util.Logger;

import roboguice.receiver.RoboBroadcastReceiver;

import com.google.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.RemoteException;

public class AutoStartReceiver extends RoboBroadcastReceiver {

	public static final String TAG = "AutoStartReceiver";

	@Inject
	private PreferenceHelper mPreferenceHelper;

	@Inject
	protected IServiceHelper serviceHelper;

	@Override
	public void handleReceive(Context context, Intent intent) {
		// AccessPreferences.setContext(context);
		Logger.d(TAG, " " + intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			if (!mPreferenceHelper.isAutostartBootEnabled()) {
				return;
			}
			serviceHelper.connect(new Runnable() {
				@Override
				public void run() {
					try {
						serviceHelper.startServer(mPreferenceHelper.getServerParameters());
						Logger.d(TAG, "Autostart ServDorid.web: System boot completed");
						LogMessage message = new LogMessage("", "",
								"System boot completed. Starting ServDroid.web", "",
								(new java.util.Date()).getTime());
						serviceHelper.getServiceController().addLog(message);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			});
		} else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
			if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI
					&& mPreferenceHelper.isAutostartWifiEnabled()) {
				serviceHelper.connect(new Runnable() {
					@Override
					public void run() {
						try {
							if (serviceHelper.getServiceController().getStatus() == ServerValues.STATUS_RUNNING) {
								serviceHelper.stopServer();
							}
							serviceHelper.startServer(mPreferenceHelper.getServerParameters());
							Logger.d(TAG, "Autostart ServDorid.web:  Wifi connect");
							LogMessage message = new LogMessage("", "",
									"Wifi connection stablished. Starting ServDroid.web", "",
									(new java.util.Date()).getTime());
							serviceHelper.getServiceController().addLog(message);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}
}
