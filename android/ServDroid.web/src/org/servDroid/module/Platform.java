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

package org.servDroid.module;

import org.servDroid.web.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

public class Platform extends AbstractModule {

	public final static String APP_VERSION_NAME = "appVersionName";
	public final static String APP_VERSION_CODE = "appVersionCode";
	public final static String HAS_TWO_PANES = "hasTwoPanes";

	@Override
	protected void configure() {
	}

	@Provides
	@Named(APP_VERSION_CODE)
	public String getAppVersionCode(Context context) {
		try {
			return String.valueOf(getPackageInfo(context).versionCode);
		} catch (PackageManager.NameNotFoundException e) {
			return "";
		}
	}

	@Provides
	@Named(HAS_TWO_PANES)
	public boolean haveTwoPanes(Context context) {
		return context.getResources().getBoolean(R.bool.has_two_panes);
	}

	@Provides
	@Named(APP_VERSION_NAME)
	public String getAppVersionName(Context context) {
		try {
			return getPackageInfo(context).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			return "";
		}
	}

	private PackageInfo getPackageInfo(Context context) throws NameNotFoundException {

		String packageName = context.getApplicationContext().getPackageName();
		PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName,
				PackageManager.GET_META_DATA);
		return packageInfo;
	}

}
