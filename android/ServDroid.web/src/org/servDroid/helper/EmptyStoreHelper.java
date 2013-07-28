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

package org.servDroid.helper;

import android.app.Activity;
import android.content.Intent;

import com.google.inject.Singleton;

@Singleton
public class EmptyStoreHelper implements IStoreHelper {

	@Override
	public String getLicenceKey() {
		return null;
	}

	@Override
	public boolean hasStoreInfo() {
		return false;
	}

	@Override
	public void initializeIap() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasDonate() {
		return false;
	}

	@Override
	public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
		return false;
	}

	@Override
	public void doDonationPurchase(Activity activity) {
	}

	@Override
	public void addOnPusrcahseInfoChangedListener(OnPurchaseInfoChangedListener listener) {
	}

	@Override
	public void removeOnPusrcahseInfoChangedListener(OnPurchaseInfoChangedListener listener) {
	}

}
