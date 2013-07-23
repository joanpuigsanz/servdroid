package org.servDroid.module;

import org.servDroid.helper.IPreferenceHelper;
import org.servDroid.helper.IServiceHelper;
import org.servDroid.helper.PreferenceHelper;
import org.servDroid.helper.ServiceHelper;


import com.google.inject.AbstractModule;

public class UtilsModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IPreferenceHelper.class).to(PreferenceHelper.class);
		bind(IServiceHelper.class).to(ServiceHelper.class);
	}

}
