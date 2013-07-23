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
