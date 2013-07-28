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

package org.servDroid.helper;

import java.util.ArrayList;
import java.util.List;

import org.servDroid.server.service.ServiceController;
import org.servDroid.server.service.params.ServerParams;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ServiceHelper implements ServiceConnection, IServiceHelper {

	private static final String TAG = "ServiceHelper";

	private ServiceController mServiceController;
	private boolean mServiceBinded;
	private Context mContext;

	private List<Runnable> mRunnablesOnConnect;
	private volatile List<ServerStatusListener> mServerStatusListener;
	private Object mLock;

	private RefreshThread mRefreshThread;
	private int mRefreshTime = DEFAULT_STATUS_REFRESH_TIME;

	public ServiceHelper(Context context) {
		mServiceBinded = false;
		mContext = context;
		mRunnablesOnConnect = new ArrayList<Runnable>();
		mServerStatusListener = new ArrayList<ServerStatusListener>();
		mLock = new Object();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mServiceController = ServiceController.Stub.asInterface(service);
		Log.i(TAG, "Connected to ServDroid.web Service");
		mServiceBinded = true;

		runRunnablesOnConnect();
		runServerEventListener(STATUS_CONNECTED);
		startThreadNotification();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mServiceBinded = false;
		Log.i(TAG, "disconnected from ServDroid.web Service");
		runServerEventListener(STATUS_DISCONNECTED);

	}

	@Override
	public void connect() {
		if (isServiceConected()) {
			runRunnablesOnConnect();
			return;
		}
		Intent serviceIntent = new Intent("org.servDroid.server.service.ServiceController");
		mContext.startService(serviceIntent);
		mContext.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void disconnect() {
		if (isServiceConected()) {
			mContext.unbindService(this);
			mServiceBinded = false;
		}
	}

	@Override
	public boolean isServiceConected() {
		return mServiceBinded && mServiceController != null;
	}

	@Override
	public boolean startServer(ServerParams params) throws RemoteException {
		if (mServiceController == null || !isServiceConected()) {
			connect();
		}
		return mServiceController.startService(params);
	}

	@Override
	public void stopServer() throws RemoteException {
		if (mServiceController == null || !isServiceConected()) {
			connect(new Runnable() {
				@Override
				public void run() {
					try {
						mServiceController.stopService();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			});
			return;
		}
		mServiceController.stopService();
	}

	@Override
	public void addRunnableOnConnect(Runnable runable) {
		synchronized (mLock) {
			if (!mRunnablesOnConnect.contains(runable)) {
				mRunnablesOnConnect.add(runable);
			}
		}
	}

	private void startThreadNotification() {
		synchronized (mLock) {
			if (mRefreshThread != null && mRefreshThread.isAlive() && !mRefreshThread.isEnded()) {
				return;
			}
			mRefreshThread = new RefreshThread();
			mRefreshThread.start();
		}
	}

	private void runRunnablesOnConnect() {
		synchronized (mLock) {
			for (int i = 0; i < mRunnablesOnConnect.size(); i++) {
				mRunnablesOnConnect.get(i).run();
			}
			mRunnablesOnConnect.clear();
		}
	}

	private int lastStatus = STATUS_UNKNOWN;

	private void runServerEventListener(int status) {
		if (lastStatus == status)
			return;
		synchronized (mLock) {
			lastStatus = status;
			for (int i = 0; i < mServerStatusListener.size(); i++) {
				mServerStatusListener.get(i).onServerStatusChanged(mServiceController, status);
			}
		}
	}

	@Override
	public void connect(Runnable runOnConnect) {
		addRunnableOnConnect(runOnConnect);
		connect();
	}

	@Override
	public ServiceController getServiceController() {
		return mServiceController;
	}

	@Override
	public void addServerStatusListener(ServerStatusListener serverEventListener) {
		synchronized (mLock) {
			if (!mServerStatusListener.contains(serverEventListener)) {
				mServerStatusListener.add(serverEventListener);
				startThreadNotification();
			}
		}
	}

	@Override
	public void removeServerStatusListener(ServerStatusListener serverEventListener) {
		synchronized (mLock) {
			mServerStatusListener.remove(serverEventListener);
		}
	}

	private class RefreshThread extends Thread {

		public boolean isEnded() {
			synchronized (mLock) {
				return mServerStatusListener.isEmpty() || !mServiceBinded;
			}
		}

		@Override
		public void run() {
			int status = STATUS_UNKNOWN;
			while (!isEnded()) {
				try {
					sleep(mRefreshTime);
					try {
						status = mServiceController.getStatus();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					runServerEventListener(status);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void setStatusTimeRefresh(int time) {
		mRefreshTime = time;
	}
}
