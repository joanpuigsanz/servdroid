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

public class BaseMainOption implements IMainOption {

	private int mResTextName;
	private int mResImage;
	private int mId;

	public BaseMainOption(int id, int resTextName, int resImage) {
		mId = id;
		mResTextName = resTextName;
		mResImage = resImage;
	}

	@Override
	public int getName() {
		return mResTextName;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public int getResourceImage() {
		return mResImage;
	}

}
