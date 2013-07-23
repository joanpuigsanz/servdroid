package org.servDroid.ui.activity;

import org.servDroid.ui.fragment.LogFragment;
import org.servDroid.ui.option.IMainOptionsList;
import org.servDroid.ui.option.ServDroidOptions;
import org.servDroid.web.R;

import roboguice.inject.InjectFragment;
import android.os.Bundle;

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
		mLogFragment.addLogMenu(menu);
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
