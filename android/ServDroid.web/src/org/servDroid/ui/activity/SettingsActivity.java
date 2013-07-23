package org.servDroid.ui.activity;

import org.servDroid.helper.IPreferenceHelper;
import org.servDroid.helper.IStoreHelper;
import org.servDroid.helper.PreferenceUiHelper;
import org.servDroid.module.Platform;
import org.servDroid.web.R;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockPreferenceActivity;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import android.content.Intent;
import android.os.Bundle;

public class SettingsActivity extends RoboSherlockPreferenceActivity {

	@Inject
	private IPreferenceHelper mPrefereneceHelper;

	@Inject
	IStoreHelper mStoreHelper;

	@Inject
	@Named(Platform.APP_VERSION_NAME)
	protected String mVersion;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		addPreferencesFromResource(R.xml.preferences);

		Runnable invalidateUi = new Runnable() {
			@Override
			public void run() {
				// This is a hack to refresh the UI when the preferences are
				// reseted
				Intent intent = getIntent();
				overridePendingTransition(0, 0);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finish();
				overridePendingTransition(0, 0);
				startActivity(intent);
			}
		};
		new PreferenceUiHelper(getPreferenceScreen(), this, mPrefereneceHelper, mVersion,
				invalidateUi, mStoreHelper);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
