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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.servDroid.db.LogHelper;
import org.servDroid.db.LogMessage;
import org.servDroid.helper.IPreferenceHelper;
import org.servDroid.module.AppModule;
import org.servDroid.server.HttpRequestHandler;
import org.servDroid.server.service.params.ServerParams;
import org.servDroid.ui.activity.StartActivity;
import org.servDroid.util.Logger;
import org.servDroid.util.shell.ShellCommands;
import org.servDroid.web.R;

import roboguice.service.RoboService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ServerService extends RoboService implements ServerValues {

	private static final String TAG = "ServerService";

	private static final int START_NOTIFICATION_ID = 1;

	private static final int VIBRATE_IDENTIFIER = 0x102;
	private static final int SERVER_STARTED_IDENTIFIER = 0x102 + 1;
	private static final int SERVER_STOPED_IDENTIFIER = 0x102 + 2;

	/**
	 * This is the default port opened when the user ask for opening a port
	 * under 1024. <br>
	 * The system will try to use iptables like this:<br>
	 * iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port
	 * DEFAULT_PORT_ON_ROOT
	 */
	public static final int DEFAULT_PORT_ON_ROOT = 65535 - 50;

	@Inject
	private LogHelper mLogAdapter;

	@Inject
	@Named(AppModule.APP_VERSION_NAME)
	private String mVersion;

	@Inject
	private IPreferenceHelper mPreferenceHelper;

	// This field is can only be setted if the server is started.
	private ServerParams mParams;
	private int mCurrentPort;
	private String mLogPort;
	private ServerSocket mServerSocket;

	private static MainServerThread mServerThread;

	private volatile boolean mVibrate;

	private NotificationManager mNotificationManager;

	private BroadcastReceiver wifiStateChangedReceiver;

	@SuppressLint("HandlerLeak")
	final Handler mServiceHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VIBRATE_IDENTIFIER:
				((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300);
				break;
			case SERVER_STARTED_IDENTIFIER:
				showRunningNotification();
				break;
			case SERVER_STOPED_IDENTIFIER:
				clearRunningNotification();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {

		if (getServerStatus() != STATUS_RUNNING) {
			clearRunningNotification();
		}

		return new ServiceController.Stub() {
			@Override
			public boolean startService(ServerParams params) throws RemoteException {
				if (null == params) {
					return false;
				}
				mParams = params;
				return startServer();
			}

			@Override
			public boolean restartService(ServerParams params) throws RemoteException {
				if (null == params) {
					return false;
				}
				if (getStatus() == STATUS_RUNNING) {
					stopServer();
				}
				return startServer();
			}

			@Override
			public boolean stopService() throws RemoteException {
				return stopServer();
			}

			@Override
			public int getStatus() throws RemoteException {
				return getServerStatus();
			}

			@Override
			public void setVibrate(boolean vibrate) throws RemoteException {
				mVibrate = vibrate;
			}

			@Override
			public String getVersion() throws RemoteException {
				return mVersion;
			}

			@Override
			public long addLog(LogMessage msg) throws RemoteException {
				return ServerService.this.addLog(msg);
			}

			@Override
			public List<LogMessage> getLogList(int n) throws RemoteException {
				return mLogAdapter.fetchLogList(n);
			}

			@Override
			public ServerParams getCurrentParams() throws RemoteException {
				return mParams;
			}

			@Override
			public int getDefaultPortOnRoot() throws RemoteException {
				return DEFAULT_PORT_ON_ROOT;
			}

		};
	}

	private int getServerStatus() {
		if (null == mServerThread) {
			return STATUS_STOPPED;
		} else if (mServerThread.isAlive()) {
			return STATUS_RUNNING;
		} else {
			return STATUS_STOPPED;
		}
	}

	/**
	 * This function displays the notifications
	 */
	private void showRunningNotification() {
		if (null == mNotificationManager) {
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}
		if (!mPreferenceHelper.getShowNotification()) {
			return;
		}

		Context context = getApplicationContext();

		Intent notificationIntent = new Intent(context, StartActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, START_NOTIFICATION_ID,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Resources res = context.getResources();
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.icon)
				.setOngoing(true)
				.setAutoCancel(false)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(res.getString(R.string.app_name))
				.setContentText(res.getString(R.string.text_running));
		Notification n = builder.build();

		nm.notify(START_NOTIFICATION_ID, n);

	}

	/**
	 * Clear all notifications
	 */
	private void clearRunningNotification() {
		if (null == mNotificationManager) {
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}

		try {
			mNotificationManager.cancel(START_NOTIFICATION_ID);
		} catch (Exception e) {

		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		wifiStateChangedReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
						WifiManager.WIFI_STATE_UNKNOWN);

				switch (extraWifiState) {
				case WifiManager.WIFI_STATE_DISABLED:
					// Logger.d(TAG, "WIFI STATE DISABLED");
					break;
				case WifiManager.WIFI_STATE_DISABLING:
					if (mPreferenceHelper.isAutostopWifiEnabled()
							&& getServerStatus() == STATUS_RUNNING) {
						addLog("", "", "", "Wifi connection down... Stopping server");
						stopServer();
					}
					break;
				case WifiManager.WIFI_STATE_ENABLED:
					// Logger.d(TAG, "WIFI STATE ENABLED");
					break;
				case WifiManager.WIFI_STATE_ENABLING:
					// Logger.d(TAG, "WIFI STATE ENABLING");
					break;
				case WifiManager.WIFI_STATE_UNKNOWN:
					// Logger.d(TAG, "WIFI STATE UNKNOWN");
					break;
				}

			}
		};

		registerReceiver(wifiStateChangedReceiver, new IntentFilter(
				WifiManager.WIFI_STATE_CHANGED_ACTION));

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Logger.d(TAG, "  Destroing ServDroid Service");
		stopServer();
		if (getServerStatus() != STATUS_RUNNING) {
			clearRunningNotification();
		}
		super.onDestroy();
	}

	private boolean startServer() {
		if (null == mServerThread || !mServerThread.isAlive()) {
			mServerThread = new MainServerThread();
			mServerThread.start();
			return true;
		}
		return false;

	}

	private boolean stopServer() {

		clearRunningNotification();
		if (mCurrentPort < 1024) {
			ShellCommands.closeNatPorts();
		}
		if (null == mServerThread) {
			addLog("", "", "", "ERROR stopping ServDroid.web server ");
			return false;
		}
		if (mServerThread.isAlive()) {
			mServerThread.stopThread();
			mServerThread = null;
			addLog("", "", "", "ServDroid.web server stoped ");
			return true;
		}
		addLog("", "", "", "ERROR stopping ServDroid.web server");
		mServerThread = null;
		return false;
	}

	public void addLog(String ip, String path, String infoBeginning, String infoEnd) {
		if (mLogAdapter == null) {
			return;
		}
		mLogAdapter.addLog(ip, path, infoBeginning, infoEnd);
	}

	public void addLog(String ip, String path) {
		if (mLogAdapter == null) {
			return;
		}
		mLogAdapter.addLog(ip, path);
	}

	public long addLog(LogMessage msg) {
		if (mLogAdapter == null) {
			return -1;
		}
		return mLogAdapter.addLog(msg);
	}

	// ///////////////////////////////////////
	// ///////////////////////////////////////
	// ///////////////////////////////////////

	/**
	 * Private class for the server thread
	 */
	private class MainServerThread extends Thread {

		private volatile boolean mRun;
		private WifiLock mWl;

		public MainServerThread() {
			mRun = true;
		}

		public synchronized void stopThread() {
			if (null != mWl && mWl.isHeld()) {
				mWl.release();
			}
			if (mRun == false) {
				return;
			}
			mRun = false;
			if (mServerSocket == null) {
				return;
			}
			try {
				mServerSocket.close();
			} catch (IOException e) {
				Logger.e(TAG, "Error stoping server thread: ", e);
				e.printStackTrace();
			}

		}

		public void run() {

			try {
				if (mWl == null || !mWl.isHeld()) {
					WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					mWl = manager.createWifiLock(WifiManager.WIFI_MODE_FULL, "servdroid_wifilock");
					mWl.setReferenceCounted(false);
					mWl.acquire();
				}
			} catch (Exception e) {
			}

			mCurrentPort = mParams.getPort();
			mLogPort = "" + mCurrentPort;

			try {
				if (mParams.getPort() < 1024) {
					if (!ShellCommands.isDeviceRooted()
							|| !ShellCommands.openNatPort(mParams.getPort(), DEFAULT_PORT_ON_ROOT)) {
						mLogPort = "" + DEFAULT_PORT_ON_ROOT;
						addLog("", "", "", "ERROR opening port " + mParams.getPort());
						Logger.d(TAG, "ERROR opening port " + mParams.getPort());
						mCurrentPort = 8080;
						mLogPort = "" + mCurrentPort;
					} else {
						mCurrentPort = DEFAULT_PORT_ON_ROOT;
						mLogPort = mLogPort + " / " + DEFAULT_PORT_ON_ROOT;
					}

				}
				mServerSocket = new ServerSocket(mCurrentPort, mParams.getMaxClients());
				Message m = new Message();

				m.what = ServerService.SERVER_STARTED_IDENTIFIER;
				mServiceHandler.sendMessage(m);
				addLog("", "", "",
						"ServDroid.web server running on port: " + mLogPort + " | WWW path: "
								+ mParams.getWwwPath() + " | Error path: " + mParams.getErrorPath()
								+ " | Max clients: " + mParams.getMaxClients()
								+ " | File indexing: " + mParams.isFileIndexing());
				Logger.d(TAG, "ServDroid.web server running on port " + mLogPort);
			} catch (IOException e) {
				if (mRun) {
					Logger.e(TAG, "Error accepting connections: ", e);
				}
				addLog("", "", "", "ERROR starting server ServDroid.web on port " + mLogPort);
				Message m = new Message();
				m.what = ServerService.SERVER_STOPED_IDENTIFIER;
				mServiceHandler.sendMessage(m);
				// Toast.makeText(ServerService.this,
				// R.string.error_starting_process,
				// Toast.LENGTH_LONG).show();

				return;
			}

			while (mRun) {
				Socket socket;
				try {
					socket = mServerSocket.accept();
				} catch (IOException e1) {
					if (mRun) {
						addLog("", "", "",
								"Warning! One connection has been droped! " + mParams.getPort());
					}
					return;
				}
				// Logger.d(TAG, "New connection accepted " +
				// socket.getInetAddress()
				// + ":" + socket.getPort());

				try {
					HttpRequestHandler request = new HttpRequestHandler(socket, mLogAdapter,
							mParams, mVersion);
					Thread thread = new Thread(request);

					thread.start();

				} catch (Exception e) {
					// Logger.e(TAG, "ERROR handing request: " + e.getMessage());
					return;
				}
				if (mVibrate) {
					Message m = new Message();
					m.what = ServerService.VIBRATE_IDENTIFIER;
					mServiceHandler.sendMessage(m);
				}

			}
		}
	}
}
