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

package org.servDroid.web;

import java.util.ArrayList;
import java.util.List;

import org.servDroid.module.EmptyStoreModule;
import org.servDroid.module.AppModule;
import org.servDroid.module.UiModule;
import org.servDroid.module.UtilsModule;

import roboguice.RoboGuice;
import android.app.Application;

import com.google.inject.Module;
import com.google.inject.Stage;

public class ServDroidApplication extends Application {

	/**
	 * Get the modules that will run in the application. Override this method to
	 * personalize the modules to load
	 * 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	protected Module[] getModules() throws InstantiationException, IllegalAccessException {
		List<Module> modules = new ArrayList<Module>();
		if (!addStoreClass(modules)) {
			modules.add(selectModule(EmptyStoreModule.class));
		}
		modules.add(RoboGuice.newDefaultRoboModule(this));
		modules.add(selectModule(AppModule.class));
		modules.add(selectModule(UiModule.class));
		modules.add(selectModule(UtilsModule.class));

		Module[] result = (Module[]) modules.toArray(new Module[modules.size()]);
		return result;
	}

	/*
	 * This method is used to load the modules used in the store version. If
	 * this is not the store version, just ignore it
	 */
	private boolean addStoreClass(List<Module> modules) {
		Class<?> clazz;
		try {
			clazz = Class.forName("org.servDroid.module.StoreModule");
			Object clazzInstance = clazz.newInstance();
			modules.add((Module) clazzInstance);
			return true;
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return false;
	}

	@Override
	public void onCreate() {
		try {

			RoboGuice.setBaseApplicationInjector(this, Stage.PRODUCTION, getModules());
		} catch (Throwable t) {
			throw new RuntimeException("FATAL!! Could not instantiate application modules", t);
		}
		super.onCreate();
	}

	private Module selectModule(Class<? extends Module> module) throws InstantiationException,
			IllegalAccessException {
		return module.newInstance();
	}
}
