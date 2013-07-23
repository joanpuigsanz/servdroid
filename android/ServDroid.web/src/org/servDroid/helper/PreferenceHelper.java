package org.servDroid.helper;

import org.servDroid.server.service.params.ServerParams;
import org.servDroid.web.R;

import roboguice.inject.ContextSingleton;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.google.inject.Inject;

@ContextSingleton
public class PreferenceHelper implements IPreferenceHelper {

	private Context mContext;
	private SharedPreferences pref;
	private String mRootDirectory;

	@Inject
	public PreferenceHelper(Context context) {
		mContext = context;
		pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		mRootDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	private boolean getBoolean(int resKey, int resDefaultValue) {
		String prefKey = mContext.getString(resKey);
		boolean defaultValue = mContext.getResources().getBoolean(resDefaultValue);
		return pref.getBoolean(prefKey, defaultValue);
	}

	private int getInteger(int resKey, int resDefaultValue) {
		String prefKey = mContext.getString(resKey);
		int defaultValue = mContext.getResources().getInteger(resDefaultValue);
		String result = pref.getString(prefKey, null);
		if (result != null) {
			try {
				return Integer.parseInt(result);
			} catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}

	private String getString(int resKey, int resDefaultValue) {
		return getString(resKey, resDefaultValue, "");
	}

	private String getString(int resKey, int resDefaultValue, String prefixDefault) {
		String prefKey = mContext.getString(resKey);
		return pref.getString(prefKey, prefixDefault + mContext.getString(resDefaultValue));
	}

	@Override
	public String getErrorPath() {
		return getString(R.string.pref_error_path_key, R.string.default_error_path, mRootDirectory);
	}

	@Override
	public int getExpirationCacheTime() {
		return getInteger(R.string.pref_expiration_cache_key, R.integer.default_expiration_cache);
	}

	@Override
	public boolean getFileIndexingEnabled() {
		return getBoolean(R.string.pref_directory_indexing_key, R.bool.default_directory_indexing);
	}

	@Override
	public String getLogPath() {
		return getString(R.string.pref_log_path_key, R.string.default_log_path, mRootDirectory);
	}

	@Override
	public int getMaxClients() {
		return getInteger(R.string.pref_max_clients_key, R.integer.default_max_clients);
	}

	@Override
	public int getMaxLogEntries() {
		return getInteger(R.string.pref_log_entries_key, R.integer.default_log_entries);
	}

	@Override
	public int getPort() {
		return getInteger(R.string.pref_port_key, R.integer.default_port);
	}

	@Override
	public ServerParams getServerParameters() {
		ServerParams params = new ServerParams(getWwwPath(), getErrorPath(),
				getExpirationCacheTime(), getFileIndexingEnabled(), getPort(), getMaxClients());
		return params;
	}

	@Override
	public boolean getShowAds() {
		return getBoolean(R.string.pref_show_ads_key, R.bool.default_show_ads);
	}

	@Override
	public boolean getShowNotification() {
		return getBoolean(R.string.pref_show_notification_key, R.bool.default_show_notification);
	}

	@Override
	public String getPreviousVersion() {
		return getString(R.string.pref_version_key, R.string.default_version);
	}

	@Override
	public void setPreviousVersion(String version) {
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(mContext.getString(R.string.pref_version_key), version);
		editor.commit();
	}

	@Override
	public boolean getVibrate() {
		return getBoolean(R.string.pref_vibrate_key, R.bool.default_vibrate);
	}

	@Override
	public String getWwwPath() {
		return getString(R.string.pref_www_path_key, R.string.default_www_path, mRootDirectory);
	}

	@Override
	public boolean isAutostartBootEnabled() {
		return getBoolean(R.string.pref_autostart_boot_key, R.bool.default_autostart);
	}

	@Override
	public boolean isAutostartWifiEnabled() {
		return getBoolean(R.string.pref_autostart_wifi_key, R.bool.default_autostart);
	}

	@Override
	public boolean isAutostopWifiEnabled() {
		return getBoolean(R.string.pref_autostop_wifi_key, R.bool.default_autostart);
	}

	@Override
	public void restorePreferences() {
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

}
