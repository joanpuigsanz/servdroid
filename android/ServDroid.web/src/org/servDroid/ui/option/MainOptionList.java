/*
 * Copyright (C) 2013 Joan Puig Sanz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.servDroid.ui.option;

import java.util.ArrayList;
import java.util.List;

import org.servDroid.helper.IStoreHelper;
import org.servDroid.web.R;

import com.google.inject.Inject;

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
