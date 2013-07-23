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
