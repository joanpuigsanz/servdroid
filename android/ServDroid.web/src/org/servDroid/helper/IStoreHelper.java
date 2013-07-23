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
