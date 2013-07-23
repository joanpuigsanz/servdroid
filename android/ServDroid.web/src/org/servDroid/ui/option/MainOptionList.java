package org.servDroid.ui.option;

import java.util.ArrayList;
import java.util.List;

import org.servDroid.helper.IStoreHelper;
import org.servDroid.web.R;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MainOptionList implements IMainOptionsList {

	private List<IMainOption> mMainOptions;

	private List<IMainOption> mLogOptions;

	@Inject
	private IStoreHelper mStoreHelper;

	@Inject
	public MainOptionList() {
		mMainOptions = new ArrayList<IMainOption>();
		mLogOptions = new ArrayList<IMainOption>();
		createOptions();
		createLogOptions();
	}

	private void createOptions() {
		mMainOptions.add(new BaseMainOption(ServDroidOptions.OPTION_ID_LOG,
				R.string.main_option_log, R.drawable.menu_log));
		mMainOptions.add(new BaseMainOption(ServDroidOptions.OPTION_ID_WEB,
				R.string.main_option_browse_localhost, R.drawable.menu_web));
		mMainOptions.add(new BaseMainOption(ServDroidOptions.OPTION_ID_SETTINGS,
				R.string.main_option_settings, R.drawable.menu_settings));
	}

	private void createLogOptions() {
		mLogOptions.add(new BaseMainOption(ServDroidOptions.OPTION_ID_DUMP_LOG,
				R.string.menu_save_log, R.drawable.icon));
		mLogOptions.add(new BaseMainOption(ServDroidOptions.OPTION_ID_DELETE_LOG,
				R.string.menu_delete_all, R.drawable.icon));
		mLogOptions.add(new BaseMainOption(ServDroidOptions.OPTION_ID_REFRESH_LOG,
				R.string.menu_refresh_log, R.drawable.icon));
	}

	@Override
	public List<IMainOption> getMainOptions() {
		return mMainOptions;
	}

	@Override
	public List<IMainOption> getLogOptions() {
		return mLogOptions;
	}

}
