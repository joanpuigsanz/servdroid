package org.servDroid.module;

import org.servDroid.db.LogAdapter;
import org.servDroid.ui.option.IMainOptionsList;
import org.servDroid.ui.option.MainOptionList;

import com.google.inject.AbstractModule;

public class UiModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IMainOptionsList.class).to(MainOptionList.class);
		bind(LogAdapter.class);
	}

}
