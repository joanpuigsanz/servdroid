/*
 * Copyright (C) 2010 Joan Puig Sanz
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
import org.servDroid.helper.IServiceHelper;
import org.servDroid.helper.IServiceHelper.ServerStatusListener;
import org.servDroid.helper.ServiceHelper;
import org.servDroid.server.service.ServerValues;
import org.servDroid.server.service.ServiceController;
import org.servDroid.server.service.params.ServerParams;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, ServerStatusListener {

	public static String TAG = "ServDroid_Service_test";

	private Button mStartStopButon, mGetLogButton;
	private TextView mTextViewStatus, mLogTextView;

	private IServiceHelper mServiceHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mStartStopButon = (Button) findViewById(R.id.button1);
		mGetLogButton = (Button) findViewById(R.id.button2);

		mTextViewStatus = (TextView) findViewById(R.id.textViewStatus);
		mLogTextView = (TextView) findViewById(R.id.textView2);

		mStartStopButon.setOnClickListener(this);
		mGetLogButton.setOnClickListener(this);

		mServiceHelper = new ServiceHelper(this);
		// We can change the periodicity of getting status change notifications
		//mServiceHelper.setStatusTimeRefresh(2500);

	}

	@Override
	protected void onResume() {
		// Register a listener to get notified when the status changes
		mServiceHelper.addServerStatusListener(this);
		// We also can run code when the service is binded:
		mServiceHelper.connect();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// Before disconnect, remove the status listener
		mServiceHelper.removeServerStatusListener(this);
		mServiceHelper.disconnect();
		super.onPause();
	}

	@Override
	public void onClick(View view) {

		if (view == mStartStopButon) {
			int status;
			try {
				status = mServiceHelper.getServiceController().getStatus();
				if (status == ServerValues.STATUS_RUNNING) {
					mServiceHelper.getServiceController().stopService();
				} else if (status == ServerValues.STATUS_STOPPED) {
					ServerParams params = new ServerParams("/sdcard/", "/sdcard/error", 30, true,
							8080, 10);
					mServiceHelper.startServer(params);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else if (view == mGetLogButton) {
			List<LogMessage> logList;
			try {
				logList = mServiceHelper.getServiceController().getLogList(100);

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

	private void updateStatusText(final String text){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mTextViewStatus.setText(text);
			}
		});
	}
	
	@Override
	public void onServerStatusChanged(ServiceController serviceController, int status) {
		switch (status) {
		case IServiceHelper.STATUS_CONNECTED:
			// Create a log line to append to the ServDroid logs
			LogMessage logLine = new LogMessage();
			logLine.setInfoEnd("Service linked succesfull from an external application");
			logLine.setTimeStamp(System.currentTimeMillis());
			try {
				// Send the line
				mServiceHelper.getServiceController().addLog(logLine);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			updateStatusText("Status: Connected");
			break;
		case IServiceHelper.STATUS_DISCONNECTED:
			updateStatusText("Status: Disconnected");
			break;
		case IServiceHelper.STATUS_STOPPED:
			updateStatusText("Status: Connected / Server stopped");
			break;
		case IServiceHelper.STATUS_RUNNING:
			updateStatusText("Status: Connected / Server running");
			break;
		default:
			break;
		}
	}
}