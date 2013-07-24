package org.servDroid.ui.fragment;

import org.servDroid.db.LogAdapter;
import org.servDroid.helper.IPreferenceHelper;
import org.servDroid.helper.IServiceHelper;
import org.servDroid.helper.IServiceHelper.ServerStatusListener;
import org.servDroid.helper.ServiceHelper;
import org.servDroid.server.service.ServerValues;
import org.servDroid.server.service.ServiceController;
import org.servDroid.util.Logger;
import org.servDroid.util.NetworkIp;
import org.servDroid.web.R;

import roboguice.inject.InjectView;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;

public class StartStopFragment extends RoboSherlockFragment implements OnCheckedChangeListener,
		ServerStatusListener {

	@InjectView(R.id.toggleButtonStartStop)
	private ToggleButton mStartStopButton;

	@InjectView(R.id.textViewUrl)
	private TextView mTextViewUrl;

	@Inject
	private LogAdapter mLogAdapter;

	@Inject
	private Context mContex;

	@Inject
	private IServiceHelper serviceHelper;

	@Inject
	private IPreferenceHelper mPreferenceHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.start_stop_fragment, container, false);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mStartStopButton.setOnCheckedChangeListener(this);

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			startServer();
		} else {
			stopService();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		serviceHelper.connect(new Runnable() {
			@Override
			public void run() {
				try {
					serviceHelper.addServerStatusListener(StartStopFragment.this);
					if (serviceHelper.getServiceController().getStatus() == ServerValues.STATUS_RUNNING) {
						mStartStopButton.setChecked(true);
						setUrlText(true);
					} else {
						mStartStopButton.setChecked(false);
						setUrlText(false);
					}
				} catch (RemoteException e) {
					Logger.e("Error resuming the connection to the service", e);
					setErrorConnectingService();
				}
			}
		});
		serviceHelper.addServerStatusListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		serviceHelper.removeServerStatusListener(this);
		serviceHelper.disconnect();
	}

	private void startServer() {
		try {
			serviceHelper.startServer(mPreferenceHelper.getServerParameters());
			Thread.sleep(500);
			if (serviceHelper.getServiceController().getStatus() == ServerValues.STATUS_RUNNING) {
				setUrlText(true);
			} else {
				setUrlText(false);
			}
		} catch (RemoteException e) {
			Logger.e("Error starting the server", e);
			setErrorConnectingService();
		} catch (InterruptedException e) {
			Logger.e("Warning starting the server", e);
		}
	}

	private void setUrlText(boolean running) {
		if (running) {
			WifiManager wifiManager = (WifiManager) mContex.getSystemService(Context.WIFI_SERVICE);
			int port;
			try {
				// We make sure that this is the port in use
				port = serviceHelper.getServiceController().getCurrentParams().getPort();
			} catch (RemoteException e) {
				port = mPreferenceHelper.getPort();
				Logger.e("Error getting the port in use", e);
			}
			mTextViewUrl.setText(getText(R.string.server_url) + " "
					+ NetworkIp.getWifiIp(wifiManager) + ":" + port);
		} else {
			mTextViewUrl.setText(R.string.text_stopped);
		}
	}

	private void setErrorConnectingService() {
		mStartStopButton.setChecked(false);
		mTextViewUrl.setText(R.string.error_connecting_service);
	}

	private void stopService() {
		try {
			serviceHelper.stopServer();
			setUrlText(false);
			Thread.sleep(500);
		} catch (RemoteException e) {
			Logger.e("Error stoping the server", e);
			setErrorConnectingService();
		} catch (InterruptedException e) {
			Logger.e("Warning stoping the server", e);
		}
	}

	@Override
	public void onServerStatusChanged(ServiceController serviceController, final int status) {
		if (getActivity() == null)
			return;

		switch (status) {
		case ServiceHelper.STATUS_RUNNING:
		case ServiceHelper.STATUS_STOPPED:
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mStartStopButton.setChecked(status == ServiceHelper.STATUS_RUNNING);
					setUrlText(status == ServiceHelper.STATUS_RUNNING);
				}
			});
			break;
		case ServiceHelper.STATUS_DISCONNECTED:
			serviceHelper.connect();
			break;
		default:
			break;
		}

	}

}
