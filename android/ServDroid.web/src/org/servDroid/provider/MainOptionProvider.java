package org.servDroid.provider;

import org.servDroid.ui.option.IMainOptionsList;
import org.servDroid.ui.option.MainOptionList;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class MainOptionProvider implements Provider<IMainOptionsList> {

	private @Inject Context context;
	
	@Override
	public IMainOptionsList get() {
		return new MainOptionList();
	}

}
