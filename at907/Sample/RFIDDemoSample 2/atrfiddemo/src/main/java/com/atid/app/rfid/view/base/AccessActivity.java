package com.atid.app.rfid.view.base;

import com.atid.app.rfid.R;
import com.atid.app.rfid.dialog.CommonDialog;
import com.atid.lib.dev.ATRfidReader;
import com.atid.lib.dev.rfid.param.EpcMatchParam;
import com.atid.lib.dev.rfid.type.ActionState;
import com.atid.lib.dev.rfid.type.MaskMatchingType;
import com.atid.lib.dev.rfid.type.ResultCode;
import com.atid.lib.dev.rfid.type.TagType;
import com.atid.lib.diagnostics.ATLog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class AccessActivity extends ActionActivity {

    private static final String TAG = AccessActivity.class.getSimpleName();

    private static final int EPC_OFFSET = 4;
    private static final int NIBLE_LEN = 4;

    public static final String KEY_EPC = "epc";
    public static final String KEY_TAG_TYPE = "tag_type";

    protected static final char DEEFAULT_TAG_TYPE = '0';

    // ------------------------------------------------------------------------
    // Member Variable
    // ------------------------------------------------------------------------

    private TextView txtSelection;
    private ProgressBar progWait;
    private TextView txtMessage;
    private LinearLayout layoutBack;

    private TextView txtPassword;

    private String mPassword;

    private String mTag;
    private EpcMatchParam mEpc;
    private TagType mParentTagType;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public AccessActivity() {
        super();

        mPassword = "";
        mTag = "";
        mEpc = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mTag = intent.getStringExtra(KEY_EPC);
        if (mTag == null) {
            mTag = "";
            mEpc = null;
        } else {
            String epc = mTag.length() > EPC_OFFSET ? mTag.substring(EPC_OFFSET) : mTag;
            int len = epc.length() * NIBLE_LEN;
            mEpc = new EpcMatchParam(MaskMatchingType.Match, 0, len, epc);
        }
        setSelection(mTag);

        this.mParentTagType = TagType.valueOf(intent.getCharExtra(KEY_TAG_TYPE, DEEFAULT_TAG_TYPE));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.password:
                ATLog.i(TAG, "INFO. onClick(password)");
                CommonDialog.showHexPasswordDialog(this, R.string.password, mPassword, mPasswordListener);
                break;
        }
    }

    // ------------------------------------------------------------------------
    // Reader Event Handler
    // ------------------------------------------------------------------------

    @Override
    public void onReaderActionChanged(ATRfidReader reader, ActionState action) {

        enableWidgets(true);

        ATLog.i(TAG, "EVENT. onReaderActionchanged(%s)", action);
    }

    @Override
    public void onReaderResult(ATRfidReader reader, ResultCode code, ActionState action, String epc, String data,
                               float rssi, float phase) {

        resultMessage(code);
        if (mTag.equals("")) {
            setSelection(epc);
        } else {
            setSelection(mTag);
        }

        ATLog.i(TAG, "EVENT. onReaderResult(%s, %s, [%s], [%s], %.2f, %.2f", code, action, epc, data, rssi, phase);
    }

    // ------------------------------------------------------------------------
    // Override Widgets Control Methods
    // ------------------------------------------------------------------------

    @Override
    // Clear Widgets
    protected void clear() {
        txtSelection.setText(mTag);
        showMessage("");
    }

    // Initialize Activity Widgets
    @Override
    protected void initWidgets() {
        super.initWidgets();

        // Initialize Selection TextView
        txtSelection = (TextView) findViewById(R.id.selection);

        // Initialize Wait ProgressBar
        progWait = (ProgressBar) findViewById(R.id.progress_bar);

        // Initialize Message TextView
        txtMessage = (TextView) findViewById(R.id.message);

        // Initialize Background LinearLayout
        layoutBack = (LinearLayout) findViewById(R.id.background);

        // Initialize Password EditText
        txtPassword = (TextView) findViewById(R.id.password);
        txtPassword.setOnClickListener(this);
    }

    @Override
    protected void enableWidgets(boolean enabled) {
        super.enableWidgets(enabled);
        if (mReader.getAction() == ActionState.Stop) {
            txtPassword.setEnabled(enabled);
        } else {
            txtPassword.setEnabled(false);
        }
    }

    @Override
    protected boolean isMask() {
        return mEpc == null;
    }

    // Activated Reader
    @Override
    protected void activateReader() {
        super.activateReader();

        enableWidgets(true);

        ATLog.i(TAG, "INFO. activateReader()");
    }

    // ------------------------------------------------------------------------
    // Override Widgets Access Methods
    // ------------------------------------------------------------------------

    // Display Message With ProgressBar
    protected void waitMessage(String msg) {
        progWait.setVisibility(View.VISIBLE);
        txtMessage.setText(msg);
        layoutBack.setBackgroundResource(R.color.message_background);
    }

    // Display Message Without ProgressBar
    protected void showMessage(String msg) {
        progWait.setVisibility(View.GONE);
        txtMessage.setText(msg);
        layoutBack.setBackgroundResource(R.color.message_background);
    }

    // Display ResultCode Without ProgressBar
    protected void resultMessage(ResultCode code) {
        String msg;
        int resId;

        if (code == ResultCode.NoError) {
            msg = "Success";
            resId = R.color.blue;
        } else {
            msg = code.toString();
            resId = R.color.red;
        }
        progWait.setVisibility(View.GONE);
        txtMessage.setText(msg);
        layoutBack.setBackgroundResource(resId);
    }

    // Get Access Password
    protected String getPassword() {
        return mPassword;
    }

    // Set Access Password
    protected void setPassword(String password) {
        mPassword = password;
        txtPassword.setText(mPassword);
    }

    // Get EPC
    protected EpcMatchParam getEpc() {
        return mEpc;
    }

    // Set Selection
    protected void setSelection(String epc) {
        txtSelection.setText(epc);
    }

    // Get Selection (for ISO 18000-6B masking)
    protected String getSelection() {
        return (String)txtSelection.getText();
    }

    protected TagType getParentTagType() {
        return this.mParentTagType;
    }

    // ------------------------------------------------------------------------
    // CommonDialog Result Listener
    // ------------------------------------------------------------------------

    private CommonDialog.IStringDialogListener mPasswordListener = new CommonDialog.IStringDialogListener() {

        @Override
        public void onConfirm(String value, DialogInterface dialog) {
            setPassword(value);
            ATLog.i(TAG, "INFO. mPasswordListener.$CommonDialog.IStringDialogListener.onConfirm([%s])", value);
        }
    };
}
