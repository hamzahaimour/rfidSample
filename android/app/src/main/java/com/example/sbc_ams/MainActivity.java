package com.example.sbc_ams;

import android.app.Activity;
import android.app.Application;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.ScanUtil;
import com.atid.app.rfid.adapter.TagListAdapter;
import com.atid.lib.dev.ATRfidManager;
import com.atid.lib.dev.ATRfidReader;
import com.atid.lib.dev.event.RfidReaderEventListener;
import com.atid.lib.dev.rfid.ATRfid900MAReader;
import com.atid.lib.dev.rfid.exception.ATRfidReaderException;
import com.atid.lib.dev.rfid.param.RangeValue;
import com.atid.lib.dev.rfid.type.ActionState;
import com.atid.lib.dev.rfid.type.ConnectionState;
import com.atid.lib.dev.rfid.type.ResultCode;
import com.atid.lib.dev.rfid.type.TagType;
import com.atid.lib.diagnostics.ATLog;
import com.atid.lib.system.device.type.RfidModuleType;
import com.atid.lib.util.SysUtil;

import java.util.Locale;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity implements CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener, RfidReaderEventListener, View.OnClickListener {

    private static final String CHANNEL = "com.example.sbc_ams.dev/battery";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_LOG_LEVEL = "log_level";
    private static final String APP_NAME = "ATRfidDemo";
    private static final int UPDATE_TIME = 500;

    private Bundle savedInstanceState;
    private TagListAdapter adpTags;
    private boolean mIsReportRssi;
    private RangeValue mPowerRange;

    private ATRfid900MAReader mMAReader;
    private int mFileDataSeperatorType = 44;
    private long m_totalCount;
    private Thread mThread;
    private boolean mIsAliveThread;
    private boolean chkContinuousMode = true;
    private boolean chkReportRssi = false;
    private boolean chkDisplayPc = false;
    private ATRfidReader reader;
    private int m_timeFlag;
    private long m_timeSec;
    private long m_tagTpsCount;
    private String txtTotalCount;
    private static final boolean DEBUG_ENABLED = false;
    private String txtTagTpsCount;
    private String txtCount;
    private TagType mTagType;
    private int mPowerLevel;
    private int mOperationTime;
    private int batteryLevel;


    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            // This method is invoked on the main thread.
                            if (call.method.equals("getBatteryLevel")) {
                                batteryLevel = getBatteryLevel();

                                if (batteryLevel != -1) {
                                    result.success(batteryLevel);
                                } else {
                                    result.error("UNAVAILABLE", "Battery level not available.", null);
                                }
                            } else {
                                result.notImplemented();
                            }
                        });
    }

    private int getBatteryLevel() {
        int batteryLevel = -1;
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
        BatteryManager batteryManager = (BatteryManager)
        getSystemService(BATTERY_SERVICE);
        batteryLevel =
        batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
        Intent intent = new
        ContextWrapper(getApplicationContext()).registerReceiver(null,
        new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        batteryLevel = (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
        intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }

        // startAction();
        // startUpdateTagCount();
        return batteryLevel;
    }

    @Override
    public void onClick(View view) {
        ScanUtil.scan();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        // Initialize Widgets
        // initWidgets();

        // Setup always wake up
        SysUtil.wakeLock(this, APP_NAME);

        // WaitDialog.show(this, R.string.connect_module);

        if ((reader = ATRfidManager.getInstance()) == null) {
            // AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // builder.setIcon(android.R.drawable.ic_dialog_alert);
            // builder.setTitle(R.string.module_error);
            // builder.setMessage(R.string.fail_check_module);
            // builder.setPositiveButton(R.string.action_ok,
            // new DialogInterface.OnClickListener() {

            // @Override
            // public void onClick(DialogInterface dialog, int which) {
            // finish();
            // }

            // });
        }

        Log.i(TAG, "INFO. onCreate()");
    }

    @Override
    protected void onDestroy() {

        // Deinitalize RFID reader Instance
        ATRfidManager.onDestroy();

        // Wake Unlock
        SysUtil.wakeUnlock();

        // saveConfig();

        ATLog.d(TAG, "INFO. onDestroy");

        ATLog.shutdown();
        super.onDestroy();

        // getApplication().unregisterActivityLifecycleCallbacks(mCallbacks);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (reader != null) {
            ATRfidManager.wakeUp();
        }

        ATLog.i(TAG, "INFO. onStart()");
    }

    @Override
    protected void onStop() {

        ATRfidManager.sleep();

        ATLog.i(TAG, "INFO. onStop()");

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (reader != null)
            reader.setEventListener(this);

        ATLog.d(TAG, "INFO. onResume()");
    }

    @Override
    protected void onPause() {

        if (reader != null)
            reader.removeEventListener(this);

        ATLog.i(TAG, "INFO. onPause()");
        super.onPause();
    }

    @Override
    public void onReaderActionChanged(ATRfidReader reader, ActionState action) {
        ATLog.i(TAG, "EVENT. onReaderActionchanged(%s)", action);
    }

    // public TagListAdapter onReaderReadTag1(ATRfidReader reader, String tag,
    // float
    // rssi, float phase) {

    // synchronized (adpTags) {
    // adpTags.addItem(tag, rssi, phase);
    // }
    // m_totalCount++; // Total Tag Count
    // // playSuccess();
    // return adpTags;
    // // ATLog.i(TAG, "EVENT. onReaderReadTag([%s], %.2f, %.2f)", tag, rssi,
    // phase);
    // }

    @Override
    public void onReaderReadTag(ATRfidReader reader, String tag, float rssi, float phase) {

        synchronized (adpTags) {
            adpTags.addItem(tag, rssi, phase);
        }
        m_totalCount++; // Total Tag Count

        // ATLog.i(TAG, "EVENT. onReaderReadTag([%s], %.2f, %.2f)", tag, rssi,phase);
    }

    @Override
    public void onReaderResult(ATRfidReader reader, ResultCode code, ActionState action, String epc, String data,
            float rssi, float phase) {
        ATLog.i(TAG, "EVENT. onReaderResult(%s, %s, [%s], [%s], %.2f, %.2f", code,
                action, epc, data, rssi, phase);
    }

    private void saveConfig() {
        // SharedPreferences prefs = getSharedPreferences(RFID_DEMO, MODE_PRIVATE);
        // SharedPreferences.Editor editor = prefs.edit();
        // editor.putInt(KEY_LOG_LEVEL, GlobalInfo.getLogLevel());
        // editor.commit();
    }

    private void onInitReader() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                initReader();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activateReader();

                    }

                });
            }

        }).start();
        ;
    }

    protected void initReader() {
        // Get Power Range
        try {
            mPowerRange = reader.getPowerRange();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get power range");
        }
        ATLog.i(TAG, "INFO. initReader() - [Power Range : %d, %d]", mPowerRange.getMin(), mPowerRange.getMax());

        // Get Power Level
        try {
            mPowerLevel = reader.getPower();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get power level");
        }
        ATLog.i(TAG, "INFO. initReader() - [Power Level : %d]", mPowerLevel);
        // Get Operation Time
        try {
            mOperationTime = reader.getOperationTime();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get operation time");
        }
        ATLog.i(TAG, "INFO. initReader() - [Operation Time : %d]", mOperationTime);
    }

    protected void activateReader() {
        // Set Power Level
        setPowerLevel(mPowerLevel);

        // Set Operation Time
        setOperationTime(mOperationTime);

        // Set Tag Type
        setTagType(mTagType);

        chkDisplayPc = false;
        chkContinuousMode = true;
        chkReportRssi = false;
        adpTags.setDisplayPc(chkDisplayPc);
        adpTags.setVisibleRssi(chkReportRssi);

        ATLog.i(TAG, "INFO. activateReader()");
    }

    protected void setTagType(TagType type) {
        mTagType = type;
        if (reader.getModuleType() == RfidModuleType.I900MA)
            mTagType.toString();
        else if (reader.getModuleType() == RfidModuleType.ATX00S_1) {
            if (mTagType.equals(TagType.Tag6C) || mTagType.equals(TagType.Tag6B) || mTagType.equals(TagType.TagRail))
                mTagType.toString();
            else {
                // new ActionActivity.ShowToast(this, "Currently not supported !!");
                mTagType = TagType.Tag6C;
                TagType.Tag6C.toString();
            }
        }
    }

    protected int getPowerLevel() {
        return mPowerLevel;
    }

    protected void setPowerLevel(int power) {
        mPowerLevel = power;
        String.format(Locale.US, "%.1f dBm", mPowerLevel / 10.0);
    }

    protected int getOperationTime() {
        return mOperationTime;
    }

    protected void setOperationTime(int time) {
        mOperationTime = time;
    }

    @Override
    public void onReaderStateChanged(ATRfidReader reader, ConnectionState state) {

        switch (state) {
            case Connected:
                // WaitDialog.hide();
                // enableMenuButtons(true);
                String version = "";
                try {
                    version = reader.getFirmwareVersion();
                } catch (ATRfidReaderException e) {
                    ATLog.e(TAG, e, "ERROR. onReaderStateChanged(%s) - Failed to get firmware version", state);
                    version = "";
                    reader.disconnect();
                }
                // txtFirmwareVersion.setText(version);
                // imgLogo.setImageResource(R.drawable.ic_connected_logo);
                break;
            case Disconnected:
                // WaitDialog.hide();
                // enableMenuButtons(false);
                // imgLogo.setImageResource(R.drawable.ic_disconnected_logo);
                break;
            case Connecting:
                // enableMenuButtons(false);
                // imgLogo.setImageResource(R.drawable.ic_connecting_logo);
                break;
            default:
                break;
        }

        ATLog.i(TAG, "EVENT. onReaderStateChanged(%s)", state);
    }

    protected void startAction() { // 생략
        if(reader.getModuleType() == RfidModuleType.I900MA) { mMAReader = (ATRfid900MAReader)reader;
            if (chkContinuousMode) {
                // Multiple Reading
                if(mTagType == TagType.Tag6B) { mMAReader.inventory6bTag();
                } else if(mTagType == TagType.Tag6C) { mMAReader.inventory6cTag();
                } else if(mTagType == TagType.TagRail) { mMAReader.inventoryRailTag();
                } else if(mTagType == TagType.TagAny) { mMAReader.inventoryAnyTag();
                } } else {
                // Single Reading
                if(mTagType == TagType.Tag6B) { mMAReader.readEpc6bTag();
                } else if(mTagType == TagType.Tag6C) { mMAReader.readEpc6cTag();
                } else if(mTagType == TagType.TagRail) { mMAReader.readEpcRailTag();
                } else if(mTagType == TagType.TagAny) { mMAReader.readEpcAnyTag();
                } }
        } else {
            // Multiple Reading
            if (chkContinuousMode) { reader.inventory6cTag();
            } else {
// Single Reading
                reader.readEpc6cTag();
            } }
        ATLog.i(TAG, "INFO. startAction()");
    }

    private void stopUpdateTagCount() {
        if (mThread == null)
            return;

        mIsAliveThread = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            ATLog.e(TAG, "ERROR. stopUpdateTagCount() - Failed to join update list thread", e);
        }
        mThread = null;

        adpTags.notifyDataSetChanged();
        ATLog.i(TAG, "INFO. stopUpdateTagCount()");
    }

    private void startUpdateTagCount() {
        mThread = new Thread(mTimerThread);
        mThread.start();

        while (!mIsAliveThread) {
            SysUtil.sleep(5);
        }

        ATLog.i(TAG, "INFO. startUpdateTagCount()");
    }

    private Runnable mTimerThread = new Runnable() {

        @Override
        public void run() {
            mIsAliveThread = true;

            while (mIsAliveThread) {
                runOnUiThread(mUpdateList);
                SysUtil.sleep(UPDATE_TIME);
            }
        }

    };

    private Runnable mUpdateList = new Runnable() {

        @Override
        public void run() {
            synchronized (adpTags) {
                txtCount = String.format(Locale.US, "%d", adpTags.getCount());
                txtTotalCount = String.format(Locale.US, "%d", m_totalCount);

                m_timeFlag ^= 1;
                if (0 == m_timeFlag) {
                    m_tagTpsCount = m_totalCount / m_timeSec;
                    txtTagTpsCount = String.format(Locale.US, "%d", m_tagTpsCount);
                    if (DEBUG_ENABLED)
                        ATLog.d(TAG, "DEBUG. Tag [%d] , Total Tag [%d] , Tag per sec [%d]",
                                adpTags.getCount(), m_totalCount, m_tagTpsCount);
                    m_timeSec++;
                }
            }
        }
    };

}

class ActivityLifecycleManager implements Application.ActivityLifecycleCallbacks {

    private int mRefCount = 0;
    private String _tag = ActivityLifecycleManager.class.getSimpleName();

    @Override
    public void onActivityStarted(Activity activity) {
        ATLog.i(_tag, String.format(Locale.US, "INFO. %s started.",
                activity.getClass().getSimpleName()));
        mRefCount++;
        ATLog.i(_tag, "INFO. mRefCount : " + mRefCount);

        if (mRefCount == 1) {
            // Setup always wake up
            android.content.Context context = activity.getApplicationContext();
            SysUtil.wakeLock(activity.getApplicationContext(),
                    SysUtil.getAppName(context));
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ATLog.i(_tag, String.format(Locale.US, "INFO. %s stopped.",
                activity.getClass().getSimpleName()));
        mRefCount--;

        ATLog.i(_tag, "INFO. mRefCount : " + mRefCount);

        if (mRefCount == 0) {
            // release WakeLock.
            SysUtil.wakeUnlock();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

}
