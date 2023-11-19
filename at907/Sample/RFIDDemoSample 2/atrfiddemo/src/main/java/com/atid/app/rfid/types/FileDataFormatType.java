package com.atid.app.rfid.types;


public class FileDataFormatType {
	private static final String TAG = FileDataFormatType.class.getSimpleName();

	public static final int INDEX = 0;
	public static final int DATA = 1;
	public static final int RSSI = 2;
	public static final int COUNT = 3;
	public static final int MAX_ITEM = 4;

	private final String[] mNames;
	private boolean[] mIsEnabled;

	public FileDataFormatType() {
		mNames = new String[] {"INDEX", "DATA", "RSSI","COUNT"};
		mIsEnabled = new boolean[] {false, false, false, false};
	}

	public FileDataFormatType(int value) {
		mNames = new String[] {"INDEX", "DATA", "RSSI","COUNT"};
		mIsEnabled = new boolean[] {false, false, false, false};
		setValue(value);
	}

	public int getCount() {
		return MAX_ITEM;
	}

	public int getUsedCount() {
		int value = 0;
		for (int i = 0 ; i < MAX_ITEM ; i++) {
			if (mIsEnabled[i])
				value++;
		}
		return value;
	}

	public int getValue() {
		int value = 0;
		for (int i = 0 ; i < MAX_ITEM ; i++) {
			if (mIsEnabled[i])
				value |= 1 << i;
		}
		return value;
	}

	public void setValue(int value) {
		for (int i = 0 ; i < MAX_ITEM ; i++) {
			if ( (value >> i & 1 ) == 1)
				mIsEnabled[i] = true;
			else
				mIsEnabled[i] = false;
		}
	}

	public boolean getUsed(int value) {
		return mIsEnabled[value];
	}

	public void setUsed(int pos, boolean value) {
		mIsEnabled[pos] = value;
	}

	public String getItemName(int value) {
		return mNames[value];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0 ; i < MAX_ITEM ; i++) {
			if (mIsEnabled[i]) {
				if (builder.length() > 0 ) builder.append(", ");
				builder.append(mNames[i]);
			}
		}

		return builder.toString();
	}


}
