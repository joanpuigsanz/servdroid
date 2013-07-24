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
package test.service;

import java.util.Iterator;
import java.util.List;

import org.servDroid.db.LogMessage;
import org.servDroid.server.service.ServerValues;
import org.servDroid.server.service.ServiceController;
import org.servDroid.server.service.params.ServerParams;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
 * It is recommended to use the MainActivity example to do the binding process. This class is not in the manifest.
 */
public class MainActivityOld extends Activity implements OnClickListener {

	public static String TAG = "ServDroid_Service_test";

	private Button mStartStopButon, mGetLogButton;
	private TextView mVersionTextView, mLogTextView;

	// This is the controller and the connection for the service
	private ServiceController mServDroidService;
	private ServDroidConnection mServiceConnection;

	// /////////////////////////////////////////////////////////

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mStartStopButon = (Button) findViewById(R.id.button1);
		mGetLogButton = (Button) findViewById(R.id.button2);

		mVersionTextView = (TextView) findViewById(R.id.textViewStatus);
		mLogTextView = (TextView) findViewById(R.id.textView2);

		mStartStopButon.setOnClickListener(this);
		mGetLogButton.setOnClickListener(this);

		// /////////////////////

		// Establish a connection with the process
		bindServDroid();

	}

	private void bindServDroid() {
		// Request bind to the service
		mServiceConnection = new ServDroidConnection();
		Intent intent = new Intent("org.servDroid.server.service.ServiceController");
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	// This private class will let us know when the service is ready
	class ServDroidConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName name, IBinder service) {
			mServDroidService = ServiceController.Stub.asInterface(service);

			String text = null;
			try {
				if (mServiceConnection == null) {
					text = "Error connecting to the service";
				} else {
					text = " Connected to ServDroid v" + mServDroidService.getVersion();
				}
			} catch (RemoteException e1) {
				e1.printStackTrace();
				text = "Error connecting to the service";
			}
			mVersionTextView.setText(text);

			// Create a log line to append to the ServDroid logs
			LogMessage logLine = new LogMessage();
			logLine.setInfoEnd("Service linked succesfull from an external application");
			logLine.setTimeStamp(System.currentTimeMillis());

			try {
				// Send the line
				mServDroidService.addLog(logLine);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

		public void onServiceDisconnected(ComponentName name) {
			mServDroidService = null;
			Log.i(TAG, "disconnected");
		}
	}

	@Override
	public void onClick(View view) {
		if (mServDroidService == null) {
			Toast.makeText(this, "Service not found", Toast.LENGTH_SHORT).show();
			return;
		}

		if (view == mStartStopButon) {
			int status;
			try {
				status = mServDroidService.getStatus();
				if (status == ServerValues.STATUS_RUNNING) {
					mServDroidService.stopService();
				} else if (status == ServerValues.STATUS_STOPPED) {
					ServerParams params = new ServerParams("/sdcard/", "/sdcard/error", 30, true,
							8080, 10);
					mServDroidService.startService(params);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else if (view == mGetLogButton) {
			List<LogMessage> logList;
			try {
				logList = mServDroidService.getLogList(100);

			} catch (RemoteException e) {
				e.printStackTrace();
				mLogTextView.setText("Error getting the log");
				return;
			}
			if (logList == null) {
				return;
			}
			Iterator<LogMessage> it = logList.iterator();
			String log = "";
			while (it.hasNext()) {
				LogMessage logLine = (LogMessage) it.next();
				log = log + logLine.toString() + "\n";
			}
			mLogTextView.setText(log);
		}

	}
}