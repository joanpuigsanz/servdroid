package org.servDroid.module;

import org.servDroid.helper.IStoreHelper;
import org.servDroid.helper.EmptyStoreHelper;

import com.google.inject.AbstractModule;

public class EmptyStoreModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IStoreHelper.class).to(EmptyStoreHelper.class);
	}

}
