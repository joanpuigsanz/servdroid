package org.servDroid.provider;

import org.servDroid.helper.IPreferenceHelper;
import org.servDroid.helper.PreferenceHelper;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class PreferenceHelperProvider implements Provider<IPreferenceHelper> {

	private @Inject Context context;
	
	@Override
	public IPreferenceHelper get() {
		return new PreferenceHelper(context);
	}

}
