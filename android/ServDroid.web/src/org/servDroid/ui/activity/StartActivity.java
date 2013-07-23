package org.servDroid.ui.activity;

import org.servDroid.helper.IPreferenceHelper;
import org.servDroid.module.Platform;
import org.servDroid.ui.util.DialogFactory;
import org.servDroid.util.FilesChecker;

import roboguice.activity.RoboActivity;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends RoboActivity {

	public static final String KEY_NEW_VERSION = "NEW_VERSION";

	@Inject
	@Named(Platform.HAS_TWO_PANES)
	protected boolean hasTwoPanes;

	@Inject
	@Named(Platform.APP_VERSION_NAME)
	private String version;

	@Inject
	private IPreferenceHelper mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent;
		if (hasTwoPanes) {
			intent = new Intent(this, MainActivityTwoPanes.class);
		} else {
			intent = new Intent(this, MainActivityOnePane.class);
		}

		String previousVersion = mPreferences.getPreviousVersion();
		// new version?
		if (previousVersion == null || previousVersion.length() < 4) {
			FilesChecker.checkLogPath(mPreferences.getLogPath(), null);
			FilesChecker.checkWwwPath(mPreferences.getWwwPath(), null);
			FilesChecker.checkErrorPath(mPreferences.getErrorPath(), null);
			intent.putExtra(KEY_NEW_VERSION, true);
		} else if (previousVersion != version) {
			// Do update process if needed
		}

		DialogFactory.showAboutDialog(this, previousVersion, null);

		mPreferences.setPreviousVersion(version);

		startActivity(intent);

	}

}
