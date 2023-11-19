package com.atid.app.rfid.view.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atid.app.rfid.R;
import com.atid.app.rfid.dialog.CommonDialog;
import com.atid.app.rfid.util.SoundPlay;
import com.atid.app.rfid.view.SelectionMask6cActivity;
import com.atid.lib.dev.rfid.exception.ATRfidReaderException;
import com.atid.lib.dev.rfid.param.RangeValue;
import com.atid.lib.dev.rfid.type.ActionState;
import com.atid.lib.dev.rfid.type.ConnectionState;
import com.atid.lib.dev.rfid.type.ResultCode;
import com.atid.lib.dev.rfid.type.TagType;
import com.atid.lib.diagnostics.ATLog;
import com.atid.lib.system.device.type.RfidModuleType;

import java.lang.ref.WeakReference;
import java.util.Locale;

public abstract class ActionActivity extends ReaderActivity implements OnClickListener {

    private static final String TAG = ActionActivity.class.getSimpleName();

    private static final int MAX_POWER_LEVEL = 300;

    private static final int SELECTION_MASK_VIEW = 6;

    private static final long SKIP_KEY_EVENT_TIME = 1000;

    /** Key code constant: Left key. */
    public static final int AT907_LEFT_SCAN_KEY              = 133;
    /** Key code constant: Gun trigger key. */
    public static final int AT907_GUN_TRIGGER_KEY             = 134;
    /** Key code constant: Right key. */
    public static final int AT907_RIGHT_SCAN_KEY             = 135;



    // ------------------------------------------------------------------------
    // Member Variable
    // ------------------------------------------------------------------------

    private TextView txtPower;
    private TextView txtOperationTime;
    private TextView txtTagType;

    private Button btnClear;
    private Button btnMask;

    private RangeValue mPowerRange;

    private int mPowerLevel;
    private int mOperationTime;
    private TagType mTagType;
    private long mTick;
    private long mElapsedTick;

    private SoundPlay mSound;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public ActionActivity() {
        super();

        mPowerRange = null;
        mPowerLevel = MAX_POWER_LEVEL;
        mOperationTime = 0;
        mTagType = TagType.Tag6C;
        mTick = 0;
        mElapsedTick = 0;
    }
    /**
     * 1.Receive the KeyEvent of keys for customizable functions
     */
    private final KeyCodeReceiver keyCodeReceiver = new KeyCodeReceiver(this);

    private class KeyCodeReceiver extends BroadcastReceiver {
        private final WeakReference<ActionActivity> mWeakReference;

        KeyCodeReceiver(ActionActivity keyTestActivity) {
            mWeakReference = new WeakReference<>(keyTestActivity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {


            int keyCode = intent.getIntExtra("keyCode", 0);
            if (keyCode == 0) {
                keyCode = intent.getIntExtra("keycode", 0);
            }
            boolean isKeyDown = intent.getBooleanExtra("keydown", false);
            String displayTemplate = "[KeyCodeReceiver] receive keyCode:" + keyCode + ", %1$s";
            String displayTips;
            if (isKeyDown) {
               if( (keyCode == AT907_LEFT_SCAN_KEY || keyCode == AT907_GUN_TRIGGER_KEY ||keyCode == AT907_RIGHT_SCAN_KEY)/* && event.getRepeatCount() <= 0*/
                        && mReader.getAction() == ActionState.Stop && mReader.getState() == ConnectionState.Connected){
                   mElapsedTick = SystemClock.elapsedRealtime() - mTick;
                   if(mTick == 0 || mElapsedTick > SKIP_KEY_EVENT_TIME) {
                       startAction();
                       mTick = SystemClock.elapsedRealtime();
                   } else {
                       ATLog.i(TAG, "INFO. Skip key down event(elapsed:" + mElapsedTick + ")");
                   }
                }
                // press down
                displayTips = String.format(displayTemplate, "press down");
            } else {
                if( (keyCode == AT907_LEFT_SCAN_KEY || keyCode == AT907_GUN_TRIGGER_KEY ||keyCode == AT907_RIGHT_SCAN_KEY) /*&& event.getRepeatCount() <= 0*/
                        && mReader.getAction() != ActionState.Stop && mReader.getState() == ConnectionState.Connected){
                    stopAction();
                }
                // release up
                displayTips = String.format(displayTemplate, "release up");
            }
            displayTips = displayTips + "\n";
            Log.i(TAG, "[KeyCodeReceiver] receive " + displayTips);
        }
    }

    /**
     * Register the BroadcastReceiver for receive KeyEvent
     */
    private void registerKeyCodeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        filter.addAction("android.intent.action.FUN_KEY");
        registerReceiver(keyCodeReceiver, filter);
    }

    /**
     * Unregister the BroadcastReceiver for receive KeyEvent
     */
    private void unregisterKeyCodeReceiver() {
        unregisterReceiver(keyCodeReceiver);
    }
    // ------------------------------------------------------------------------
    // Activity Event Handler
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSound = new SoundPlay(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Clear Activity
        clear();
    }
    @Override
    protected void onResume() {
        registerKeyCodeReceiver();
        super.onResume();
    }
    @Override
    protected void onPause() {
        unregisterKeyCodeReceiver();
        super.onPause();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        enableWidgets(true);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_SOFT_RIGHT || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT
                || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT
                || keyCode == KeyEvent.KEYCODE_F7 || keyCode == KeyEvent.KEYCODE_F8
                || keyCode == 298 || keyCode == 299 || keyCode == 300 || keyCode == 301
                || keyCode == AT907_LEFT_SCAN_KEY || keyCode == AT907_GUN_TRIGGER_KEY ||keyCode == AT907_RIGHT_SCAN_KEY) && event.getRepeatCount() <= 0
                && mReader.getAction() == ActionState.Stop && mReader.getState() == ConnectionState.Connected) {

           // ATLog.i(TAG, "INFO. onKeyDown(%d, %d)", keyCode, event.getAction());

            mElapsedTick = SystemClock.elapsedRealtime() - mTick;
            if(mTick == 0 || mElapsedTick > SKIP_KEY_EVENT_TIME) {
                startAction();
                mTick = SystemClock.elapsedRealtime();
            } else {
                ATLog.e(TAG, "INFO. Skip key down event(elapsed:" + mElapsedTick + ")");
                return super.onKeyDown(keyCode, event);
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_SOFT_RIGHT || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT
                || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT
                || keyCode == KeyEvent.KEYCODE_F7 || keyCode == KeyEvent.KEYCODE_F8
                || keyCode == 298 || keyCode == 299 || keyCode == 300 || keyCode == 301
                ||keyCode == AT907_LEFT_SCAN_KEY || keyCode == AT907_GUN_TRIGGER_KEY ||keyCode == AT907_RIGHT_SCAN_KEY) && event.getRepeatCount() <= 0
                && mReader.getAction() != ActionState.Stop && mReader.getState() == ConnectionState.Connected) {

            //ATLog.i(TAG, "INFO. onKeyUp(%d, %d)", keyCode, event.getAction());

            stopAction();

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.power_gain:
                ATLog.i(TAG, "INFO. onClick(power_gain)");
                CommonDialog.showPowerGainDialog(this, R.string.power_gain, getPowerLevel(), mPowerRange,
                        mPowerGainListener);
                break;
            case R.id.operation_time:
                ATLog.i(TAG, "INFO. onClick(operation_time)");
                CommonDialog.showNumberDialog(this, R.string.operation_time, mOperationTime, new RangeValue(0, 100000),
                        "ms", mOperationTimeListener);
                break;
            case R.id.clear:
                ATLog.i(TAG, "INFO. onClick(clear)");
                clear();
                break;
            case R.id.mask:
                ATLog.i(TAG, "INFO. onClick(mask)");
                Intent intent = null;
                enableWidgets(false);
                intent = new Intent(this, SelectionMask6cActivity.class);
                startActivityForResult(intent, SELECTION_MASK_VIEW);
                break;
            case R.id.action:
                ATLog.i(TAG, "INFO. onClick(action)");
                enableWidgets(false);
                if (mReader.getAction() == ActionState.Stop) {
                    startAction();
                } else {
                    stopAction();
                }
                break;
            case R.id.tag_type:
                ATLog.i(TAG, "INFO. onClick(tag_type)");
                CommonDialog.showTagTypeDialog(this, R.string.tag_type, mTagType, mTagTypeListener);
                break;
            case R.id.action_save_list:
                ATLog.i(TAG, "INFO. onClick(action_save_list)");
                onSaveList();
                break;
            case R.id.action_save_as_list:
                ATLog.i(TAG, "INFO. onClick(action_save_as_list)");
                onSaveAsList();
                break;
            case R.id.more_option:
                ATLog.i(TAG, "INFO. onClick(more_option)");
                onMoreOption();
                break;
        }
    }

    /**
     * export csv/txt toast message
     */
    public class ShowToast {
        public ShowToast(Context context, String info) {
            Toast toast = Toast.makeText(context, Html.fromHtml("<font color='#F73910' ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 4, 240);
            toast.show();
        }
    }

    // ------------------------------------------------------------------------
    // Override Widgets Control Methods
    // ------------------------------------------------------------------------

    // Clear Widgets
    protected abstract void clear();
    //export csv/txt
    protected abstract void onSaveList();
    protected abstract void onSaveAsList();
    protected abstract void onMoreOption();

    // Initialize Activity Widgets
    @Override
    protected void initWidgets() {

        // Initialize Power Gain
        txtPower = (TextView) findViewById(R.id.power_gain);
        txtPower.setOnClickListener(this);

        // Initialize Operation Time
        txtOperationTime = (TextView) findViewById(R.id.operation_time);
        txtOperationTime.setOnClickListener(this);

        // Initialize Clear
        btnClear = (Button) findViewById(R.id.clear);
        btnClear.setOnClickListener(this);

        // Initialize Mask
        btnMask = (Button) findViewById(R.id.mask);
        btnMask.setOnClickListener(this);

        // Initialize TagType
        txtTagType = (TextView) findViewById(R.id.tag_type);
        txtTagType.setOnClickListener(this);


    }

    // Eanble Activity Widgets
    @Override
    protected void enableWidgets(boolean enabled) {

        if (mReader.getAction() == ActionState.Stop) {
            txtPower.setEnabled(enabled);
            txtOperationTime.setEnabled(enabled);
            btnClear.setEnabled(enabled);
            if(mReader.getModuleType() == RfidModuleType.I900MA) {
                txtTagType.setEnabled(enabled);
            }
            if(mReader.getModuleType() == RfidModuleType.ATX00S_1) {
                //txtTagType.setEnabled(false); //MI
				txtTagType.setEnabled(true); //ATM2K
                btnMask.setEnabled(enabled && isMask());
            }
        } else {
            txtPower.setEnabled(false);
            txtOperationTime.setEnabled(false);
            btnClear.setEnabled(false);
            if(mReader.getModuleType() == RfidModuleType.I900MA) {
                txtTagType.setEnabled(false);
            }
            if(mReader.getModuleType() == RfidModuleType.ATX00S_1) {
                txtTagType.setEnabled(false);
                btnMask.setEnabled(false);
            }
        }
    }

    protected boolean isMask() {
        return true;
    }

    // Initialize Reader
    @Override
    protected void initReader() {
        // Get Power Range
        try {
            mPowerRange = mReader.getPowerRange();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get power range");
        }
        ATLog.i(TAG, "INFO. initReader() - [Power Range : %d, %d]", mPowerRange.getMin(), mPowerRange.getMax());

        // Get Power Level
        try {
            mPowerLevel = mReader.getPower();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get power level");
        }
        ATLog.i(TAG, "INFO. initReader() - [Power Level : %d]", mPowerLevel);

        // Get Operation Time
        try {
            mOperationTime = mReader.getOperationTime();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get operation time");
        }
        ATLog.i(TAG, "INFO. initReader() - [Operation Time : %d]", mOperationTime);

        try {
            mIsReportRssi = mReader.getReportRssi();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get report RSSI");
        }
        ATLog.i(TAG, "INFO. initReader() - [Report RSSI : %s]", mIsReportRssi);

        ATLog.i(TAG, "INFO initReader()");
    }

    // Activated Reader
    @Override
    protected void activateReader() {

        // Set Power Level
        setPowerLevel(mPowerLevel);

        // Set Operation Time
        setOperationTime(mOperationTime);

        // Set Tag Type
        setTagType(mTagType);
    }

    // ------------------------------------------------------------------------
    // Override Widgets Access Methods
    // ------------------------------------------------------------------------

    protected int getPowerLevel() {
        return mPowerLevel;
    }

    protected void setPowerLevel(int power) {
        mPowerLevel = power;
        txtPower.setText(String.format(Locale.US, "%.1f dBm", mPowerLevel / 10.0));
    }

    protected int getOperationTime() {
        return mOperationTime;
    }

    protected void setOperationTime(int time) {
        mOperationTime = time;
        txtOperationTime.setText(String.format(Locale.US, "%d ms", mOperationTime));
    }

    protected TagType getTagType() {
        return mTagType;
    }

    protected void setTagType(TagType type) {
        mTagType = type;
        if(mReader.getModuleType() == RfidModuleType.I900MA)
            txtTagType.setText(mTagType.toString());
        else if(mReader.getModuleType() == RfidModuleType.ATX00S_1 ){
            if(mTagType.equals(TagType.Tag6C) || mTagType.equals(TagType.Tag6B) || mTagType.equals(TagType.TagRail))
                txtTagType.setText(mTagType.toString());
            else {
                new ActionActivity.ShowToast(this, "Currently not supported !!");
                mTagType = TagType.Tag6C;
                txtTagType.setText(TagType.Tag6C.toString());
            }
        }
    }

    // Start Action
    protected abstract void startAction();

    // Stop Action
    protected void stopAction() {

        if(mReader.getAction() == ActionState.Stop) {
            ATLog.e(TAG, "ActionState is not busy.");
            return;
        }
        ResultCode res;

        enableWidgets(false);

        if ((res = mReader.stop()) != ResultCode.NoError) {
            ATLog.e(TAG, "ERROR. stopAction() - Failed to stop operation [%s]", res);
            enableWidgets(true);
            return;
        }

        ATLog.i(TAG, "INFO. stopAction()");
    }

    // ------------------------------------------------------------------------
    // Intenal Widgets Control Methods
    // ------------------------------------------------------------------------

    protected void playSuccess() {
        mSound.playSuccess();
    }

    protected void playFail() {
        mSound.playFail();
    }

    // ------------------------------------------------------------------------
    // CommonDialog Result Listener
    // ------------------------------------------------------------------------

    private CommonDialog.IPowerGainDialogListener mPowerGainListener = new CommonDialog.IPowerGainDialogListener() {

        @Override
        public void onSelected(int value, DialogInterface dialog) {
            try {
                mReader.setPower(value);
            } catch (ATRfidReaderException e) {
                ATLog.e(TAG, e,
                        "ERROR. mPowerGainListener.$CommonDialog.IPowerGainDialogListener.onSelected(%d) - Failed to set power gain",
                        value);
                return;
            }
            setPowerLevel(value);
            ATLog.i(TAG, "INFO. mPowerGainListener.$CommonDialog.IPowerGainDialogListener.onSelected(%d)", value);
        }
    };

    private CommonDialog.INumberDialogListener mOperationTimeListener = new CommonDialog.INumberDialogListener() {

        @Override
        public void onConfirm(int value, DialogInterface dialog) {
            try {
                mReader.setOperationTime(value);
            } catch (ATRfidReaderException e) {
                ATLog.i(TAG, e,
                        "ERROR. mOperationTimeListener.$CommonDialog.INumberDialogListener.onConfirm(%d) - Failed to set operation time",
                        value);
                return;
            }
            setOperationTime(value);
            ATLog.i(TAG, "INFO. mOperationTimeListener.$CommonDialog.INumberDialogListener.onConfirm(%d)", value);
        }
    };

    private CommonDialog.ITagTypeListener mTagTypeListener = new CommonDialog.ITagTypeListener() {

        @Override
        public void onSelected(TagType value, DialogInterface dialog) {
            setTagType(value);
        }
    };
}
