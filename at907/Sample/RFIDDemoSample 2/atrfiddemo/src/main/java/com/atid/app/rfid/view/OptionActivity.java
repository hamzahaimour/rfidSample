package com.atid.app.rfid.view;

import java.util.Locale;

import com.atid.app.rfid.GlobalInfo;
import com.atid.app.rfid.R;
import com.atid.app.rfid.dialog.CommonDialog;
import com.atid.app.rfid.dialog.CommonDialog.IArrayDialogListener;
import com.atid.app.rfid.dialog.CommonDialog.IInventorySessionListener;
import com.atid.app.rfid.dialog.CommonDialog.IInventoryTargetListener;
import com.atid.app.rfid.dialog.CommonDialog.IQValueListener;
import com.atid.app.rfid.dialog.CommonDialog.IStringDialogListener;
import com.atid.app.rfid.dialog.WaitDialog;
import com.atid.app.rfid.view.base.ReaderActivity;
import com.atid.lib.dev.rfid.ATRfidATX00S1Reader;
import com.atid.lib.dev.rfid.exception.ATRfidReaderException;
import com.atid.lib.dev.rfid.param.QValue;
import com.atid.lib.dev.rfid.param.RangeValue;
import com.atid.lib.dev.rfid.type.GlobalBandType;
import com.atid.lib.dev.rfid.type.InventorySession;
import com.atid.lib.dev.rfid.type.InventoryTarget;
import com.atid.lib.diagnostics.ATLog;
import com.atid.lib.system.device.type.RfidModuleType;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class OptionActivity extends ReaderActivity implements OnClickListener {

    private static final String TAG = OptionActivity.class.getSimpleName();

    private static final int MAX_POWER_LEVEL = 300;
    private static final String ADMIN_PASSWORD = "1436";

    // ------------------------------------------------------------------------
    // Member Variable
    // ------------------------------------------------------------------------

    private TextView txtOperationTime;
    private TextView txtGlobalBand;
    private TextView txtInventoryTime;
    private TextView txtIdleTime;
    private TextView txtPower;
    private TextView txtLinkProfile;
    private Button btnFreqChannel;
    private TextView txtLogLevel;
    private Button btnSave;
    private Button btnDefaults;
    private TextView txtSession;
    private TextView txtTarget;
    private TextView txtQStart;
    private TextView txtQMax;
    private TextView txtQMin;

    private RangeValue mPowerRange;
    private GlobalBandType mGlobalBand;
    private int mOperationTime;
    private int mInventoryTime;
    private int mIdleTime;
    private int mPowerLevel;
    private int mMaxLinkProfile;
    private int mLinkProfileIndex;
    private String[] mFreqChanNames;
    private boolean[] mFreqChanUses;
    private InventorySession mInventorySession;
    private InventoryTarget mInventoryTarget;
    private QValue mQValue;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public OptionActivity() {
        super();

        mView = R.layout.activity_option;

        mPowerRange = null;
        mGlobalBand = GlobalBandType.Korea;
        mPowerLevel = MAX_POWER_LEVEL;
        mOperationTime = 0;
        mInventoryTime = 0;
        mIdleTime = 0;

        mFreqChanNames = null;
        mFreqChanUses = null;

        mInventorySession = InventorySession.S0;
        mInventoryTarget = InventoryTarget.AB;
        mQValue = new QValue();
    }

    // ------------------------------------------------------------------------
    // Activity Event Handler
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onStart() {
        WaitDialog.show(this, R.string.load_option);
        super.onStart();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.operation_time:
                ATLog.i(TAG, "INFO. onClick(operation_time)");
                CommonDialog.showNumberDialog(this, R.string.operation_time, mOperationTime, new RangeValue(0, 100000),
                        "ms", mOperationTimeListener);
                break;
            case R.id.inventory_time:
                ATLog.i(TAG, "INFO. onClick(inventory_time)");
                CommonDialog.showNumberDialog(this, R.string.inventory_time, mInventoryTime, new RangeValue(0, 100000),
                        "ms", mInventoryTimeListener);
                break;
            case R.id.idle_time:
                ATLog.i(TAG, "INFO. onClick(idle_time)");
                CommonDialog.showNumberDialog(this, R.string.idle_time, mIdleTime, new RangeValue(0, 100000), "ms",
                        mIdleTimeListener);
                break;
            case R.id.power_gain:
                ATLog.i(TAG, "INFO. onClick(power_gain)");
                CommonDialog.showPowerGainDialog(this, R.string.power_gain, mPowerLevel, mPowerRange, mPowerGainListener);
                break;
            case R.id.freq_channel:
                ATLog.i(TAG, "INFO. onClick(freq_channel)");
                showFreqChannelDialog();
                break;
            case R.id.log_level:
                ATLog.i(TAG, "INFO. onClick(log_level)");
                showLogLevelDialog();
                break;
            case R.id.link_profile:
                ATLog.i(TAG, "INFO. onClick(link_profile)");
                showLinkProfileDialog();
                break;
            case R.id.save:
                ATLog.i(TAG, "INFO. onClick(save)");
                saveOption();
                break;
            case R.id.option_default:
                ATLog.i(TAG, "INFO. onClick(option_default)");
                defaultOption();
                break;
            case R.id.inventory_session:
                ATLog.i(TAG, "INFO. onClick(inventory_session)");
                showSessionDialog();
                break;
            case R.id.inventory_target:
                ATLog.i(TAG, "INFO. onClick(inventory_target)");
                showTargetDialog();
                break;
            case R.id.q_start:
                ATLog.i(TAG, "INFO. onClick(q_start)");
                showQValueDialog(R.id.q_start);
                break;
            case R.id.q_max:
                ATLog.i(TAG, "INFO. onClick(q_max)");
                showQValueDialog(R.id.q_max);
                break;
            case R.id.q_min:
                ATLog.i(TAG, "INFO. onClick(q_min)");
                showQValueDialog(R.id.q_min);
                break;
        }
    }

    // ------------------------------------------------------------------------
    // Override Widgets Control Methods
    // ------------------------------------------------------------------------

    @Override
    protected void initWidgets() {

        // Initialize Operation Time AutoCompleteTextView
        txtOperationTime = (TextView) findViewById(R.id.operation_time);
        txtOperationTime.setOnClickListener(this);

        // Initialize Global Band TextView
        txtGlobalBand = (TextView) findViewById(R.id.global_band);

        // Initialize Inventory Time AutoCompleteTextView
        txtInventoryTime = (TextView) findViewById(R.id.inventory_time);
        txtInventoryTime.setOnClickListener(this);

        // Initialize Idle Time AutoCompleteTextView
        txtIdleTime = (TextView) findViewById(R.id.idle_time);
        txtIdleTime.setOnClickListener(this);

        // Initialize Power Gain
        txtPower = (TextView) findViewById(R.id.power_gain);
        txtPower.setOnClickListener(this);

        // Initialize Freq Channel
        btnFreqChannel = (Button) findViewById(R.id.freq_channel);
        btnFreqChannel.setOnClickListener(this);

        // Initialize Log Level
        txtLogLevel = (TextView) findViewById(R.id.log_level);
        txtLogLevel.setOnClickListener(this);
        setLogLevel();

        // Initialize Link Profile
        txtLinkProfile = (TextView) findViewById(R.id.link_profile);
        txtLinkProfile.setOnClickListener(this);
        setLinkProfile();

        // Initialize Save Button
        btnSave = (Button) findViewById(R.id.save);
        btnSave.setOnClickListener(this);

        // Initialize Default Button
        btnDefaults = (Button) findViewById(R.id.option_default);
        btnDefaults.setOnClickListener(this);

        // Initialize Session
        txtSession = (TextView) findViewById(R.id.inventory_session);
        txtSession.setOnClickListener(this);
        setInventorySession();

        // Initialize Target
        txtTarget = (TextView) findViewById(R.id.inventory_target);
        txtTarget.setOnClickListener(this);
        setInventoryTarget();

        // Initialize Q values
        txtQStart = (TextView) findViewById(R.id.q_start);
        txtQStart.setOnClickListener(this);


        txtQMax = (TextView) findViewById(R.id.q_max);
        txtQMax.setOnClickListener(this);

        txtQMin = (TextView) findViewById(R.id.q_min);
        txtQMin.setOnClickListener(this);
    }

    // Enable/Disable Widgets
    @Override
    protected void enableWidgets(boolean enabled) {

        txtOperationTime.setEnabled(enabled);
        txtGlobalBand.setEnabled(enabled);
        txtInventoryTime.setEnabled(enabled);
        txtIdleTime.setEnabled(enabled);
        txtPower.setEnabled(enabled);
        if(mReader.getModuleType() == RfidModuleType.I900MA) {
            txtLinkProfile.setEnabled(false);
            btnFreqChannel.setEnabled(false);
        } else {
            txtLinkProfile.setEnabled(enabled);
            btnFreqChannel.setEnabled(enabled);
        }
        btnSave.setEnabled(enabled);
        btnDefaults.setEnabled(enabled);
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

        // Get Global Band
        try {
            mGlobalBand = mReader.getGlobalBand();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get global band");
        }
        ATLog.i(TAG, "INFO. initReader() - [Global Band : %s]", mGlobalBand);

        // Get Operation Time
        try {
            mOperationTime = mReader.getOperationTime();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get operation time");
        }
        ATLog.i(TAG, "INFO. initReader() - [Operation Time : %d]", mOperationTime);

        // Get Inventory Time
        try {
            mInventoryTime = mReader.getInventoryTime();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get inventory time");
        }
        ATLog.i(TAG, "INFO. initReader() - [Inventory Time : %d]", mInventoryTime);

        // Get Idle Time
        try {
            mIdleTime = mReader.getIdleTime();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get idle time");
        }
        ATLog.i(TAG, "INFO. initReader() - [Idle Time : %d]", mIdleTime);

        if(mReader.getModuleType() == RfidModuleType.I900MA) {
            ATLog.i(TAG, "INFO. frequency methods are not supported(%s)", mReader.getModuleType());
            return;
        }
        // Get Freq Table
        int count = 0;
        int freq = 0;

        try {
            count = mReader.getFreqChannelCount();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get frequency channel count");
        }
        ATLog.i(TAG, "INFO. initReader() - [Frequency Channel Count : %d]", count);

        mFreqChanNames = new String[count];
        mFreqChanUses = new boolean[count];

        for (int i = 0; i < count; i++) {
            try {
                freq = mReader.getChannelFreq(i);
            } catch (ATRfidReaderException e) {
                ATLog.e(TAG, e, "ERROR. initReader() - Failed to get frequency channel name [%d]", i);
            }
            ATLog.i(TAG, "INFO. initReader() - [Frequency Channel name [%d] : %d]", i, count);
            mFreqChanNames[i] = getFreqString(freq);
            try {
                mFreqChanUses[i] = mReader.isUseFreqChannel(i);
            } catch (ATRfidReaderException e) {
                ATLog.e(TAG, e, "ERROR. initReader() - Failed to get channel frequency [%d]", i);
            }
            ATLog.i(TAG, "INFO. initReader() - [Channel Frequency [%d] : %d]", i, count);
        }

        if(mReader.getModuleType() == RfidModuleType.ATX00S_1) {
            ATRfidATX00S1Reader atx001siReader = (ATRfidATX00S1Reader)mReader;
            // Link Profile
            try {
                mMaxLinkProfile = atx001siReader.getLinkProfileCount();
            } catch (ATRfidReaderException e) {
                ATLog.e(TAG, e, "ERROR. initReader() - Failed to get link profile count");
            }
            ATLog.i(TAG, "INFO. initReader() - [Link Profile Count : %d]", mMaxLinkProfile);

            try {
                mLinkProfileIndex = atx001siReader.getActiveLinkProfile();
            } catch (ATRfidReaderException e) {
                ATLog.e(TAG, e, "ERROR. initReader() - Failed to get current link profile");
            }
            ATLog.i(TAG, "INFO. initReader() - [Link Profile : %d]", mLinkProfileIndex);

            try {
                mInventorySession = mReader.getInventorySession();
            } catch (ATRfidReaderException e) {
                ATLog.e(TAG, e, "ERROR. initReader() - Failed to get current inventory session");
            }
            ATLog.i(TAG, "INFO. initReader() - [Session : %s]", mInventorySession);

            try {
                mInventoryTarget = mReader.getInventoryTarget();
            } catch (ATRfidReaderException e) {
                ATLog.e(TAG, e, "ERROR. initReader() - Failed to get current inventory target");
            }
            ATLog.i(TAG, "INFO. initReader() - [Target : %s]", mInventoryTarget);

            try {
                ATLog.i(TAG, "INFO. Singulation Algorithm : " + atx001siReader.getCurrentSingulationAlgorithm());
            } catch (ATRfidReaderException e) {
                ATLog.e(TAG, e, "ERROR. initReader() - Failed to get Singulation algorithm.");
            }

            try {
                mQValue = atx001siReader.getQValue();
            } catch (ATRfidReaderException e) {
                ATLog.e(TAG, e, "ERROR. initReader() - Failed to get Q vlaue");
            }
            ATLog.i(TAG, "INFO. initReader() - [%s]", mQValue);

        }
    }

    private String getFreqString(int freq) {
        if (freq > 1000000000)
            return String.format(Locale.US, "%.3fGHz", (double) freq / 1000000000.0);
        else if (freq > 1000000)
            return String.format(Locale.US, "%.3fMHz", (double) freq / 1000000.0);
        else if (freq > 1000)
            return String.format(Locale.US, "%.3fKHz", (double) freq / 1000.0);
        return String.format(Locale.US, "%d", freq);
    }

    // Activated Reader
    @Override
    protected void activateReader() {

        // Set Power Level
        setPowerLevel(mPowerLevel);

        // Set Global Band
        txtGlobalBand.setText(mGlobalBand.toString());

        // Set Operation Time
        setOperationTime(mOperationTime);

        // Set Invenotry Time
        setInventoryTime(mInventoryTime);

        // Set Idle Time
        setIdleTime(mIdleTime);

        // Set Link Profile
        setLinkProfile();

        // Set Session
        setInventorySession();

        // Set Target
        setInventoryTarget();

        // Set Q values
        setQValues();

        WaitDialog.hide();
        enableWidgets(true);
    }

    // ------------------------------------------------------------------------
    // Override Widgets Access Methods
    // ------------------------------------------------------------------------

    private void setPowerLevel(int power) {
        mPowerLevel = power;
        txtPower.setText(String.format(Locale.US, "%.1f dBm", mPowerLevel / 10.0));
    }

    private void setOperationTime(int time) {
        mOperationTime = time;
        txtOperationTime.setText(String.format(Locale.US, "%d ms", mOperationTime));
    }

    private void setInventoryTime(int time) {
        mInventoryTime = time;
        txtInventoryTime.setText(String.format(Locale.US, "%d ms", mInventoryTime));
    }

    private void setIdleTime(int time) {
        mIdleTime = time;
        txtIdleTime.setText(String.format(Locale.US, "%d ms", mIdleTime));
    }

    private void setLogLevel() {
        txtLogLevel.setText(String.format(Locale.US, "Level %d", GlobalInfo.getLogLevel()));
    }

    private void setLinkProfile() {
        txtLinkProfile.setText(String.format(Locale.US, "Profile %d", mLinkProfileIndex));
    }

    private void setInventorySession() {
        txtSession.setText(mInventorySession.toString());
    }

    private void setInventoryTarget() {
        txtTarget.setText(mInventoryTarget.toString());
    }

    private void setQValues() {
        txtQStart.setText(String.format("%d", mQValue.getStartQ()));
        txtQMax.setText(String.format("%d", mQValue.getMaxQ()));
        txtQMin.setText(String.format("%d", mQValue.getMinQ()));
    }

    // ------------------------------------------------------------------------
    // CommonDialog Result Listener
    // ------------------------------------------------------------------------

    private CommonDialog.INumberDialogListener mOperationTimeListener = new CommonDialog.INumberDialogListener() {

        @Override
        public void onConfirm(int value, DialogInterface dialog) {
            setOperationTime(value);
            ATLog.i(TAG, "INFO. mOperatinTimeListener.$CommonDialog.INumberDialogListener.onConfirm(%d)", value);
        }
    };

    private CommonDialog.INumberDialogListener mInventoryTimeListener = new CommonDialog.INumberDialogListener() {

        @Override
        public void onConfirm(int value, DialogInterface dialog) {
            setInventoryTime(value);
            ATLog.i(TAG, "INFO. mInventoryTimeListener.$CommonDialog.INumberDialogListener.onConfirm(%d)", value);
        }
    };

    private CommonDialog.INumberDialogListener mIdleTimeListener = new CommonDialog.INumberDialogListener() {

        @Override
        public void onConfirm(int value, DialogInterface dialog) {
            setIdleTime(value);
            ATLog.i(TAG, "INFO. mIdleTimeListener.$CommonDialog.INumberDialogListener.onConfirm(%d)", value);
        }
    };

    private CommonDialog.IPowerGainDialogListener mPowerGainListener = new CommonDialog.IPowerGainDialogListener() {

        @Override
        public void onSelected(int value, DialogInterface dialog) {
            setPowerLevel(value);
            ATLog.i(TAG, "INFO. mPowerGainListener.$CommonDialog.IPowerGainDialogListener.onSelected(%d)", value);
        }
    };

    // ------------------------------------------------------------------------
    // Background Work Methods
    // ------------------------------------------------------------------------

    private void saveOption() {
        WaitDialog.show(this, "Save Properties...\r\nPlease Wait...");

        new Thread(new Runnable() {

            @Override
            public void run() {
                // Set Operation Time
                try {
                    mReader.setOperationTime(mOperationTime);
                } catch (ATRfidReaderException e) {
                    ATLog.e(TAG, e, "ERROR. saveOption() - Failed to set operation Time");

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            WaitDialog.hide();
                            enableWidgets(true);
                        }
                    });
                    return;
                }
                ATLog.i(TAG, "INFO. saveOption() - [Operation Time : %d]", mOperationTime);

                // Set Inventory Time
                try {
                    mReader.setInventoryTime(mInventoryTime);
                } catch (ATRfidReaderException e) {
                    ATLog.e(TAG, e, "ERROR. saveOption() - Failed to set inventory Time");

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            WaitDialog.hide();
                            enableWidgets(true);
                        }
                    });
                    return;
                }
                ATLog.i(TAG, "INFO. saveOption() - [Inventory Time : %d]", mInventoryTime);

                // Set Idle Time
                try {
                    mReader.setIdleTime(mIdleTime);
                } catch (ATRfidReaderException e) {
                    ATLog.e(TAG, e, "ERROR. saveOption() - Failed to set idle Time");

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            WaitDialog.hide();
                            enableWidgets(true);
                        }
                    });
                    return;
                }
                ATLog.i(TAG, "INFO. saveOption() - [Idle Time : %d]", mIdleTime);

                // Set Power Level
                try {
                    mReader.setPower(mPowerLevel);
                } catch (ATRfidReaderException e) {
                    ATLog.e(TAG, e, "ERROR. saveOption() - Failed to set power level");

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            WaitDialog.hide();
                            enableWidgets(true);
                        }
                    });
                    return;
                }
                ATLog.i(TAG, "INFO. saveOption() - [Power Level : %d]", mPowerLevel);

                if(mReader.getModuleType() == RfidModuleType.I900MA) {
                    ATLog.i(TAG, "INFO. frequency methods are not supported(%s)", mReader.getModuleType());
                    mReader.saveProperties();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            WaitDialog.hide();
                            enableWidgets(true);
                            finish();
                        }
                    });
                    return;
                }

                // Frequency Channel
                for (int i = 0; i < mFreqChanUses.length; i++) {
                    try {
                        mReader.setUseFreqChannel(i, mFreqChanUses[i]);
                    } catch (ATRfidReaderException e) {
                        ATLog.e(TAG, e, "ERROR. saveOption() - Failed to set use frequency channel [%d]");

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                WaitDialog.hide();
                                enableWidgets(true);
                            }
                        });
                        return;
                    }
                    ATLog.i(TAG, "INFO. saveOption() - [Freq Channel [%d] : %s]", i, mFreqChanUses[i]);
                }

                if(mReader.getModuleType() == RfidModuleType.ATX00S_1) {

                    ATRfidATX00S1Reader atx001siReader = (ATRfidATX00S1Reader)mReader;

                    // Set Active Link Profile
                    try {
                        atx001siReader.setActiveLinkProfile(mLinkProfileIndex);
                    } catch (ATRfidReaderException e) {
                        ATLog.e(TAG, e, "ERROR. saveOption() - Failed to set Active Link Profile");

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                WaitDialog.hide();
                                enableWidgets(true);
                            }
                        });
                        return;
                    }
                    ATLog.i(TAG, "INFO. saveOption() - [Active Link Profile : %d]", mLinkProfileIndex);

                    // Set Inventory Session
                    try {
                        mReader.setInventorySession(mInventorySession);
                    } catch (ATRfidReaderException e) {
                        ATLog.e(TAG, e, "ERROR. saveOption() - Failed to set Inventory Session");

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                WaitDialog.hide();
                                enableWidgets(true);
                            }
                        });
                        return;
                    }
                    ATLog.i(TAG, "INFO. saveOption() - [Inventory Session : %s]", mInventorySession);

                    // Set Inventory Target
                    try {
                        mReader.setInventoryTarget(mInventoryTarget);
                    } catch (ATRfidReaderException e) {
                        ATLog.e(TAG, e, "ERROR. saveOption() - Failed to set Inventory Target");

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                WaitDialog.hide();
                                enableWidgets(true);
                            }
                        });
                        return;
                    }
                    ATLog.i(TAG, "INFO. saveOption() - [Inventory Target : %s]", mInventoryTarget);

                    // Set Q value
                    try {
                        atx001siReader.setQValue(mQValue);
                    } catch (ATRfidReaderException e) {
                        ATLog.e(TAG, e, "ERROR. saveOption() - Failed to set Q value");

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                WaitDialog.hide();
                                enableWidgets(true);
                            }
                        });
                        return;
                    }
                    ATLog.i(TAG, "INFO. saveOption() - [%s]", mQValue);
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        WaitDialog.hide();
                        enableWidgets(true);
                        finish();
                    }
                });
            }
        }).start();
    }

    private void defaultOption() {
        WaitDialog.show(this, "Setting Default Properties...\r\nPlease Wait...");
        new Thread(new Runnable() {

            @Override
            public void run() {

                mReader.defaultProperties();
                if(mReader.getModuleType() == RfidModuleType.I900MA)
                    mReader.saveProperties();
                initReader();

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activateReader();
                        WaitDialog.hide();
                    }
                });
            }
        }).start();
    }

    private void showFreqChannelDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.freq_chan_title);
        builder.setMultiChoiceItems(mFreqChanNames, mFreqChanUses, onFreqChecked);
        builder.setPositiveButton(R.string.action_ok, null);
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.create().show();

        ATLog.i(TAG, "INFO. showFreqChanngelDialog()");
    }

    private void showLogLevelDialog() {
        CommonDialog.showPasswordDialog(this, R.string.admin_password, new IStringDialogListener() {

            @Override
            public void onConfirm(String value, DialogInterface dialog) {
                if (value.equals(ADMIN_PASSWORD)) {
                    String[] levels = new String[] { "Level 0", "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" };
                    CommonDialog.showArrayDialog(OptionActivity.this, R.string.log_level, levels,
                            GlobalInfo.getLogLevel(), new IArrayDialogListener() {

                                @Override
                                public void onSelected(int index, DialogInterface dialog) {
                                    GlobalInfo.setLogLevel(index);
                                    mReader.setLogLevel(GlobalInfo.getReaderLogLevel());
                                    setLogLevel();
                                }
                            });
                }
            }

        });
        ATLog.i(TAG, "INFO. showLogLevelDialog()");
    }

    private void showLinkProfileDialog() {
        String[] levels = new String[mMaxLinkProfile];
        for(int i=0 ; i<mMaxLinkProfile ; i++)
            levels[i] = String.format("Profile %d", i);

        CommonDialog.showArrayDialog(OptionActivity.this, R.string.link_profile, levels,
                mLinkProfileIndex, new IArrayDialogListener() {

                    @Override
                    public void onSelected(int index, DialogInterface dialog) {

                        mLinkProfileIndex = index;
                        setLinkProfile();
                    }
                });

        ATLog.i(TAG, "INFO. showLogLevelDialog()");
    }

    // Show Session Dialog
    private void showSessionDialog() {
        if (!txtSession.isEnabled())
            return;

        CommonDialog.showInventorySessionDialog(this, R.string.inventory_session, mInventorySession,
                new IInventorySessionListener() {

                    @Override
                    public void onSelected(InventorySession session, DialogInterface dialog) {
                        mInventorySession = session;
                        txtSession.setText(mInventorySession.toString());
                    }
                });
        ATLog.i(TAG, "INFO. showSessionDialog()");
    }

    // Show Target Dialog
    private void showTargetDialog() {
        if (!txtTarget.isEnabled())
            return;

        CommonDialog.showInventoryTargetDialog(this, R.string.session_flag, mInventoryTarget, new IInventoryTargetListener() {

            @Override
            public void onSelected(InventoryTarget target, DialogInterface dialog) {
                mInventoryTarget = target;
                txtTarget.setText(mInventoryTarget.toString());
            }
        });
        ATLog.i(TAG, "INFO. showSessionFlagDilog()");
    }

    // Show Q value Dialog
    private void showQValueDialog(int id) {

        switch(id) {
            case R.id.q_start:
                CommonDialog.showQStartValueDialog(this, R.string.q_start, mQValue, new IQValueListener () {

                    @Override
                    public void onSelected(int q, DialogInterface dialog) {
                        mQValue.setStratQ(q);
                        setQValues();
                    }
                });
                break;
            case R.id.q_max:
                CommonDialog.showQMaxValueDialog(this, R.string.q_max, mQValue, new IQValueListener () {

                    @Override
                    public void onSelected(int q, DialogInterface dialog) {
                        mQValue.setMaxQ(q);
                        setQValues();
                    }
                });
                break;
            case R.id.q_min:
                CommonDialog.showQMinValueDialog(this, R.string.q_min, mQValue, new IQValueListener () {

                    @Override
                    public void onSelected(int q, DialogInterface dialog) {
                        mQValue.setMinQ(q);
                        setQValues();
                    }
                });
                break;
        }

    }

    private OnMultiChoiceClickListener onFreqChecked = new OnMultiChoiceClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            mFreqChanUses[which] = isChecked;
        }

    };
}