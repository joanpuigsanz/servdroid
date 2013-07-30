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

package org.servDroid.ui.activity;

import org.servDroid.ui.fragment.LogFragment;
import org.servDroid.ui.option.IMainOptionsList;
import org.servDroid.ui.option.ServDroidOptions;
import org.servDroid.web.R;

import roboguice.inject.InjectFragment;
import android.os.Bundle;
import android.view.KeyEvent;

import com.actionbarsherlock.view.Menu;
import com.google.inject.Inject;

public class LogActivity extends ServDroidBaseFragmentActivity {

	@Inject
	private IMainOptionsList mOptions;

	@InjectFragment(R.id.logFragment)
	private LogFragment mLogFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_activity);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void createMainMenus(Menu menu) {
		if (hasTwoPanes || menu == null) {
			return;
		}
		mLogFragment.addSpecificMenu(menu);
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			appMenu.performIdentifierAction(LogFragment.MENU_ID_LOG, 1);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public void onOptionClick(int id) {
		switch (id) {
		case ServDroidOptions.OPTION_ID_DUMP_LOG:
			mLogFragment.saveLog();
			break;
		case ServDroidOptions.OPTION_ID_DELETE_LOG:
			mLogFragment.deleteLog();
			break;
		case ServDroidOptions.OPTION_ID_REFRESH_LOG:
			mLogFragment.fillLogList();
			break;
		default:
			break;
		}
	}

}
