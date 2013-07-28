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

import org.servDroid.web.R;

import com.actionbarsherlock.view.Menu;

import roboguice.inject.InjectFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class WebActivity extends ServDroidBaseFragmentActivity {

	@InjectFragment(R.id.webFragment)
	private Fragment mWebFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_activity);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void createMainMenus(Menu menu) {
		super.createMainMenus(menu);
	}
}
