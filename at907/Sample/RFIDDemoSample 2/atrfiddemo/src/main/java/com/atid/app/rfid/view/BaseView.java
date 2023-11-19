package com.atid.app.rfid.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.atid.app.rfid.R;
import com.atid.app.rfid.dialog.MessageBox;
import com.atid.app.rfid.dialog.WaitDialog;
import com.atid.lib.diagnostics.ATLog;



public abstract class BaseView extends Fragment {

	protected String TAG;


	// ------------------------------------------------------------------------
	// Member Variable
	// ------------------------------------------------------------------------

	protected View mView;
	protected long mId;
	protected boolean mIsEnabled;
	protected boolean mIsUseMask;
	private boolean mIsInitialize;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------

	public BaseView() {
		super();
		mIsEnabled = false;
		mIsInitialize = false;
		mIsUseMask = false;
	}

	// ------------------------------------------------------------------------
	// Overiable Event Methods
	// ------------------------------------------------------------------------

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mView = inflater.inflate(getInflateResId(), container, false);

		// Initialize Widget for Layout
		initView();

		// Enable Widgets...
		enableWidgets(false);

		loadProperties();

		ATLog.i(TAG,  "INFO. onCreateView()");
		return mView;
	}

	@Override
	public void onDestroyView() {

		// De-initialize Widget for Layout
		exitView();
		ATLog.i(TAG,  "INFO. onDestroyView()");
		super.onDestroyView();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*if (requestCode == RfidOptionActivity.ID) {
			switch (resultCode) {
			case Activity.RESULT_FIRST_USER:
				finishView();
				break;
			case Activity.RESULT_OK:
			case Activity.RESULT_CANCELED:
				checkMask();
				completeSetting(OPTION_RFID_UHF, data);
				if (mId == VIEW_INVENTORY) {
					if (getReader().getDeviceType() != DeviceType.ATD100) {
						try {
							getReader().setUseActionKey(true);
						} catch (ATException e) {
							ATLog.e(TAG, "ERROR. onActivityResult() - Failed to enable key action");
						}					
					}
				}
				enableWidgets(true);
				break;
			}
			ATLog.i(TAG, INFO, "INFO. onActivityResult() - RfidOptionActivity [%d]" , resultCode);
		} else if (requestCode == BarcodeOptionActivity.ID) {
			switch (resultCode) {
			case Activity.RESULT_FIRST_USER:
				finishView();
				break;
			case Activity.RESULT_OK:
			case Activity.RESULT_CANCELED:
				completeSetting(OPTION_BARCODE, data);
				if (getReader().getDeviceType() != DeviceType.ATD100) {
					try {
						getReader().setUseActionKey(true);
					} catch (ATException e) {
						ATLog.e(TAG, "ERROR. onActivityResult() - Failed to enable key action");
					}					
				}
				enableWidgets(true);
				break;
			}
			ATLog.i(TAG, INFO, "INFO. onActivityResult() - BarcodeOptionActivity [%d]" , resultCode);
		}*/
		super.onActivityResult(requestCode, resultCode, data);
	}

	// ------------------------------------------------------------------------
	// Abstract Methods
	// ------------------------------------------------------------------------

	// Get Inflate Resource Id
	protected abstract int getInflateResId();

	// Initialize View
	protected abstract void initView();

	// Exit View
	protected abstract void exitView();

	// Enabled Widgets
	protected void enableWidgets(boolean enabled) {
		/*if (getReader() != null)
			mIsEnabled = enabled && getReader().getAction() == ActionState.Stop;
		else 
			mIsEnabled = false;*/
	}

	// Exit View
	protected abstract void completeSetting(int type, Intent data);
		
	// Loading Reader Properties
	protected abstract boolean loadingProperties();

	// Loaded Reader Properteis
	protected abstract void loadedProperties(boolean isInitialize);

	// ------------------------------------------------------------------------
	// Abstract Event Methods
	// ------------------------------------------------------------------------
	/*public abstract void onReaderStateChanged(ATEAReader reader, ConnectState state, Object params);

	public abstract void onReaderActionChanged(ATEAReader reader, ResultCode code, ActionState action, Object params);

	public abstract void onReaderReadTag(ATEAReader reader, String tag, Object params);

	public abstract void onReaderAccessResult(ATEAReader reader, ResultCode code, ActionState action, String epc,
                                              String data, Object params);

	public abstract void onReaderReadBarcode(ATEAReader reader, BarcodeType type, String codeId, String barcode,
                                             Object params);

	public abstract void onReaderOperationModeChanged(ATEAReader reader, OperationMode mode, Object params);

	public abstract void onReaderPowerGainChanged(ATEAReader reader, int power, Object params);

	public abstract void onReaderBatteryState(ATEAReader reader, int batteryState, Object params);
	
	public abstract void onReaderKeyChanged(ATEAReader reader, KeyType type, KeyState state, Object params);
			*/
	
	// ------------------------------------------------------------------------
	// Heritable Methods
	// ------------------------------------------------------------------------

	/*protected ATEAReader getReader() {
		return ((IATEAReader) getActivity()).getReader();
	}

	public long getViewId() {
		return mId;
	}

	protected boolean isATS100() {
		return getReader().getDeviceType() == DeviceType.ATS100;
	}

	protected boolean isATD100() {
		return getReader().getDeviceType() == DeviceType.ATD100;
	}

	protected void finishView() {
		getReader().removeListener((IATEAReaderEventListener) getActivity());
		getActivity().setResult(Activity.RESULT_FIRST_USER);
		getActivity().finish();
		
		ATLog.i(TAG, INFO, "INFO. finishView()");
	}*/

	// ------------------------------------------------------------------------
	// Load Reader Properties
	// ------------------------------------------------------------------------

	protected void loadProperties() {
	//	WaitDialog.show(getActivity(), R.string.msg_initialize_view);

		Thread thread = new Thread(mLoadingProperties);
		thread.start();
	}

	private Runnable mLoadingProperties = new Runnable() {

		@Override
		public void run() {
			mIsInitialize = loadingProperties();

			getActivity().runOnUiThread(mLoadedProperties);
		}

	};

	private Runnable mLoadedProperties = new Runnable() {

		@Override
		public void run() {

			loadedProperties(mIsInitialize);
			WaitDialog.hide();
		}

	};

	protected void asyncWork(final int message, final int msgFail, final boolean controlWidget, final ActionWork work) {
		WaitDialog.show(getActivity(), message);
		//if (controlWidget)
			//enableWidgets(false);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				final boolean result = work.onWork();
				getActivity().runOnUiThread(new Runnable(){
					@Override
					public void run() {
						WaitDialog.hide();
						if (!result) {
							MessageBox.show(getActivity(), msgFail);
						}
						
						//if (controlWidget)
							//enableWidgets(true);
					}
				});
			}
		}).start();
	}
	
	protected interface ActionWork {
		boolean onWork();
	}
	
	protected void showMessage(final int message) {
		getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				MessageBox.show(getActivity(), message);
			}
		});
		
	}
	
	protected void showMessage(final String message) {
		getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				MessageBox.show(getActivity(), message);
			}
		});
		
	}

	protected void showErrorMessage(final String message) {
		getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				MessageBox.show(getActivity(), message, getString(R.string.title_error));
			}
		});
	}
	
	// ------------------------------------------------------------------------
	// Setting
	// ------------------------------------------------------------------------
	/*protected void showRfidSetting(int callingView) *//*{
		
		int position = GlobalData.ReaderManager.indexOf(getReader());
		Intent intent = new Intent(getActivity(), RfidOptionActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		intent.putExtra(Constants.SELECTED_READER, position);
		intent.putExtra(Constants.RFID_OPTION_CALLING_VIEW, callingView);

		intent.putExtra(Constants.RFID_BARCODE_OPTION_BATTERY_LOGGING, mIsBatteryLogging);

		try {
			
			if (getReader().getTransport().getConnectType() == ConnectType.USB) {
				DeviceItem item = null;
				try {
					item = GlobalData.DataManager.getUsbDeviceInfo();	
				} catch (NullPointerException e) {
					ATLog.e(TAG, e, "ERROR. showRfidSetting() - Failed to found usb device item");
					return;
				} catch (SQLiteException e1) {
					ATLog.e(TAG, e1, "ERROR. showRfidSetting() - Failed to found usb device item");
					return;
				}
				intent.putExtra(Constants.RFID_OPTION_TAG_DISPLAY_PC, (Boolean)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						item.getType(), item.getAddress(), GlobalData.KEY_RFID_TAG_DISPLAY_PC ));
				intent.putExtra(Constants.RFID_OPTION_TAG_DISPLAY_EPC_ASCII_STRING, (Boolean)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						item.getType(), item.getAddress(), GlobalData.KEY_RFID_TAG_DISPLAY_EPC_ASCII_STRING));

				intent.putExtra(Constants.RFID_BARCODE_OPTION_FILE_DATA_FORMAT, (Integer)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						item.getType(), item.getAddress(), GlobalData.KEY_RFID_BARCODE_OPTION_FILE_DATA_FORMAT ));
				intent.putExtra(Constants.RFID_BARCODE_OPTION_FILE_DATA_SEPERATOR, (Integer)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						item.getType(), item.getAddress(), GlobalData.KEY_RFID_BARCODE_OPTION_FILE_DATA_SEPERATOR_TYPE ));

				intent.putExtra(Constants.RFID_TAG_SPEED, (Boolean)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						item.getType(), item.getAddress(), GlobalData.KEY_TAG_SPEED));

				intent.putExtra(Constants.RFID_LIMITED_TAG_COUNT, 
						(Integer)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						item.getType(), item.getAddress(), GlobalData.KEY_LIMITED_TAG_COUNT));
				
			} else {
				intent.putExtra(Constants.RFID_OPTION_TAG_DISPLAY_PC, (Boolean)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						getReader().getDeviceType(), getReader().getAddress(), GlobalData.KEY_RFID_TAG_DISPLAY_PC));
				intent.putExtra(Constants.RFID_OPTION_TAG_DISPLAY_EPC_ASCII_STRING, (Boolean)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						getReader().getDeviceType(), getReader().getAddress(), GlobalData.KEY_RFID_TAG_DISPLAY_EPC_ASCII_STRING));

				intent.putExtra(Constants.RFID_BARCODE_OPTION_FILE_DATA_FORMAT, (Integer)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						getReader().getDeviceType(), getReader().getAddress(), GlobalData.KEY_RFID_BARCODE_OPTION_FILE_DATA_FORMAT));
				intent.putExtra(Constants.RFID_BARCODE_OPTION_FILE_DATA_SEPERATOR, (Integer)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						getReader().getDeviceType(), getReader().getAddress(), GlobalData.KEY_RFID_BARCODE_OPTION_FILE_DATA_SEPERATOR_TYPE));

				intent.putExtra(Constants.RFID_TAG_SPEED, (Boolean)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						getReader().getDeviceType(), getReader().getAddress(), GlobalData.KEY_TAG_SPEED));
				intent.putExtra(Constants.RFID_LIMITED_TAG_COUNT,
						(Integer)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						getReader().getDeviceType(), getReader().getAddress(), GlobalData.KEY_LIMITED_TAG_COUNT));
				
			}
			
		} catch (Exception e) {
			ATLog.e(TAG, e,"ERROR. showRfidSetting() - Failed to get global data");
			return;
		}
		
		startActivityForResult(intent, RfidOptionActivity.ID);
	}*/

	 /*protected void showBarcodeSetting(){
		int position = GlobalData.ReaderManager.indexOf(getReader());
		Intent intent = new Intent(getActivity(), BarcodeOptionActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		intent.putExtra(Constants.SELECTED_READER, position);

		intent.putExtra(Constants.RFID_BARCODE_OPTION_BATTERY_LOGGING, mIsBatteryLogging);

		try {
			if (getReader().getTransport().getConnectType() == ConnectType.USB) {
				DeviceItem item = null;
				try {
					item = GlobalData.DataManager.getUsbDeviceInfo();	
				} catch (NullPointerException e) {
					ATLog.e(TAG, e, "ERROR. showBarcodeSetting() - Failed to found usb device item");
					return;
				} catch (SQLiteException e1) {
					ATLog.e(TAG, e1, "ERROR. showBarcodeSetting() - Failed to found usb device item");
					return;
				}

				intent.putExtra(Constants.RFID_BARCODE_OPTION_FILE_DATA_FORMAT, (Integer)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						item.getType(), item.getAddress(), GlobalData.KEY_RFID_BARCODE_OPTION_FILE_DATA_FORMAT ));
				intent.putExtra(Constants.RFID_BARCODE_OPTION_FILE_DATA_SEPERATOR, (Integer)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						item.getType(), item.getAddress(), GlobalData.KEY_RFID_BARCODE_OPTION_FILE_DATA_SEPERATOR_TYPE ));

				intent.putExtra(Constants.BARCODE_RESTART_TIME, (Integer)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						item.getType(), item.getAddress(), GlobalData.KEY_RESTART_TIME));
				
			} else {
				intent.putExtra(Constants.RFID_BARCODE_OPTION_FILE_DATA_FORMAT, (Integer)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						getReader().getDeviceType(), getReader().getAddress(), GlobalData.KEY_RFID_BARCODE_OPTION_FILE_DATA_FORMAT));
				intent.putExtra(Constants.RFID_BARCODE_OPTION_FILE_DATA_SEPERATOR, (Integer)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						getReader().getDeviceType(), getReader().getAddress(), GlobalData.KEY_RFID_BARCODE_OPTION_FILE_DATA_SEPERATOR_TYPE));

				intent.putExtra(Constants.BARCODE_RESTART_TIME, (Integer)GlobalData.getDeviceConfig(getActivity().getApplicationContext(),
						getReader().getDeviceType(), getReader().getAddress(), GlobalData.KEY_RESTART_TIME));
			}
		} catch (Exception e) {
			ATLog.e(TAG,  e,"ERROR. showBarcodeSetting() - Failed to get global data");
			return;
		}
		
		startActivityForResult(intent, BarcodeOptionActivity.ID);
	}*/
	
	// ------------------------------------------------------------------------
	// Selection Mask Methods
	// ------------------------------------------------------------------------

	 /*protected boolean checkMask(){
//		SelectFlag selectFlag = SelectFlag.All;
		mIsUseMask = false;
//		try {
//			selectFlag = getReader().getRfidUhf().getSelectFlag();
//		} catch (ATException e) {
//			ATLog.e(TAG, e, "ERROR. checkMask() - Failed to load select flag");
//			return false;
//		}
		//if (selectFlag != SelectFlag.NotUsed) {
			for (int i = 0; i < MAX_MASK; i++) {
				try {
					mIsUseMask |= getReader().getRfidUhf().getSelectMask6cEnabled(i);
				} catch (ATException e) {
					ATLog.e(TAG, e, "ERROR. checkMask() - Failed to load select mask enabled [i]", i);
					return false;
				}
			}
		//}
		ATLog.i(TAG, INFO, "INFO. checkMask()");
		return true;
	}*/

	// ------------------------------------------------------------------------
	// Declare Interface ATEAReader
	// ------------------------------------------------------------------------

	public interface IATEAReader {

	//	ATEAReader getReader();
	}
}
