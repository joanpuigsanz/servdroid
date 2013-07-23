package org.servDroid.ui.fragment;

import org.servDroid.helper.IPreferenceHelper;
import org.servDroid.helper.IStoreHelper;
import org.servDroid.helper.PreferenceUiHelper;
import org.servDroid.module.Platform;
import org.servDroid.web.R;

import roboguice.RoboGuice;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment {

	@Inject
	private IPreferenceHelper mPrefereneceHelper;

	@Inject
	private IStoreHelper mStoreHelper;

	@Inject
	@Named(Platform.APP_VERSION_NAME)
	protected String mVersion;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);

		addPreferencesFromResource(R.xml.preferences);

		Runnable invalidateUi = new Runnable() {
			@Override
			public void run() {
				// This is a hack to refresh the UI when the preferences are
				// reseted
				FragmentManager fManager = getFragmentManager();
				fManager.beginTransaction().remove(SettingsFragment.this).commit();
				fManager.beginTransaction()
						.add(R.id.fillableFrameLayout, new SettingsFragment(),
								SettingsFragment.class.getSimpleName()).commit();
			}
		};
		new PreferenceUiHelper(getPreferenceScreen(), getActivity(), mPrefereneceHelper, mVersion,
				invalidateUi, mStoreHelper);
	}

}
