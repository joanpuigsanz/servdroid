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
