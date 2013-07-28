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

public interface IStoreHelper {

	public String getLicenceKey();

	public boolean hasStoreInfo();

	public void initializeIap();

	public boolean hasDonate();

	public void doDonationPurchase(Activity activity);

	public boolean handleActivityResult(int requestCode, int resultCode, Intent data);

	public void addOnPusrcahseInfoChangedListener(OnPurchaseInfoChangedListener listener);

	public void removeOnPusrcahseInfoChangedListener(OnPurchaseInfoChangedListener listener);

	public interface OnPurchaseInfoChangedListener {
		public void onPusrcahseInfoChangedListener(IStoreHelper storeHelper);
	}

}
