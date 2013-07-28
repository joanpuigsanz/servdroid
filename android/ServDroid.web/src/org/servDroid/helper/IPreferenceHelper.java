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
