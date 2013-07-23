package org.servDroid.helper;

import org.servDroid.server.service.params.ServerParams;

public interface IPreferenceHelper {

	public String getErrorPath();

	public int getExpirationCacheTime();

	public boolean getFileIndexingEnabled();

	public String getLogPath();

	public int getMaxClients();

	public int getMaxLogEntries();

	public int getPort();

	public ServerParams getServerParameters();

	public boolean getShowAds();

	public boolean getShowNotification();

	public String getPreviousVersion();

	public void setPreviousVersion(String version);

	public boolean getVibrate();

	public String getWwwPath();

	public boolean isAutostartBootEnabled();

	public boolean isAutostartWifiEnabled();

	public boolean isAutostopWifiEnabled();

	public void restorePreferences();

}
