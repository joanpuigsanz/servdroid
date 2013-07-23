package org.servDroid.ui.activity;

import org.servDroid.ui.option.IMainOption;
import org.servDroid.ui.option.MainOptionList;
import org.servDroid.ui.option.ServDroidOptions;
import org.servDroid.web.R;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.google.inject.Inject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivityOnePane extends ServDroidBaseFragmentActivity {

	@Inject
	private MainOptionList mOptions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
	}

	@Override
	protected void createMainMenus(Menu menu) {
		if (hasTwoPanes || menu == null) {
			return;
		}
		SubMenu subMenuOptions = menu.addSubMenu(R.string.main_menu_options);

		for (int i = 0; i < mOptions.getMainOptions().size(); i++) {
			IMainOption option = mOptions.getMainOptions().get(i);
			subMenuOptions.add(0, option.getId(), 1, option.getName());
		}
		subMenuOptions.add(0, MENU_ID_HELP, 1, getString(R.string.menu_help));
		
		if (storeHelper != null && storeHelper.hasStoreInfo()) {
			subMenuOptions.add(0, MENU_ID_DONATE, 1, getString(R.string.donate));
		}

		MenuItem MenuOptions = subMenuOptions.getItem();
		MenuOptions.setIcon(R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark);
		MenuOptions.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	@Override
	public void onOptionClick(int id) {
		switch (id) {
		case ServDroidOptions.OPTION_ID_LOG:
			startGenericActivityWithFragment(LogActivity.class);
			break;
		case ServDroidOptions.OPTION_ID_SETTINGS:
			startGenericActivityWithFragment(SettingsActivity.class);
			break;
		case ServDroidOptions.OPTION_ID_WEB:
			startGenericActivityWithFragment(WebActivity.class);
			break;
		default:
			break;
		}
	}

	private void startGenericActivityWithFragment(Class<? extends Activity> clazz) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
	}
}
