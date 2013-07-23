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
