package com.atid.app.rfid.view;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atid.app.rfid.R;
import com.atid.app.rfid.dialog.CommonDialog;
import com.atid.app.rfid.view.base.ReadWriteMemoryActivity;
import com.atid.lib.dev.rfid.ATRfid900MAReader;
import com.atid.lib.dev.rfid.param.EpcMatchParam;
import com.atid.lib.dev.rfid.param.SelectionMask6b;
import com.atid.lib.dev.rfid.type.ActionState;
import com.atid.lib.dev.rfid.type.BankType;
import com.atid.lib.dev.rfid.type.MaskMatchingType;
import com.atid.lib.dev.rfid.type.ResultCode;
import com.atid.lib.dev.rfid.type.TagType;
import com.atid.lib.diagnostics.ATLog;

public class WriteMemoryActivity extends ReadWriteMemoryActivity {

    private static final String TAG = WriteMemoryActivity.class.getSimpleName();

    // ------------------------------------------------------------------------
    // Member Variable
    // ------------------------------------------------------------------------

    private TextView txtWriteValue;
    private Button btnAction;
    private TextView txtTagType;

    private String mWriteValue;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public WriteMemoryActivity() {
        super();

        mView = R.layout.activity_write_memory;
        mWriteValue = "";
    }

    // ------------------------------------------------------------------------
    // Activity Event Handler
    // ------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.write_value:
                ATLog.i(TAG, "INFO. onClick(write_value)");
                CommonDialog.showStringDialog(this, R.string.write_data, mWriteValue, mWriteValueListener);
                break;
        }
    }

    // ------------------------------------------------------------------------
    // Reader Control Methods
    // ------------------------------------------------------------------------

    // Start Action
    @Override
    protected void startAction() {

        ResultCode res;
        BankType bank = getBank();
        int offset = getOffset();
        String data = getWriteValue();
        String password = getPassword();
        EpcMatchParam epc = getEpc();
        TagType tagType = getTagType();

        clear();
        enableWidgets(false);

        if(tagType == TagType.Tag6C) {
            if ((res = mReader.writeMemory6c(bank, offset, data, password, epc)) != ResultCode.NoError) {
                ATLog.e(TAG, "ERROR. startAction() - Failed to write memory 6C tag [%s]", res);
                enableWidgets(true);
                return;
            }
        } else if(tagType == TagType.Tag6B) {

            ATRfid900MAReader MAReader = (ATRfid900MAReader)mReader;
            offset *= 2;
            String mask = getSelection();

            SelectionMask6b mask6b = new SelectionMask6b(0, mask, MaskMatchingType.Match);
            if ((res = MAReader.writeMemory6b(offset, data, mask6b)) != ResultCode.NoError) {
                ATLog.e(TAG, "ERROR. startAction() - Failed to write memory 6B tag [%s]", res);
                enableWidgets(true);
                return;
            }
        } else {
            Toast.makeText(this, R.string.not_supported, Toast.LENGTH_LONG).show();
            enableWidgets(true);
        }

        ATLog.i(TAG, "INFO. startAction()");
    }

    // ------------------------------------------------------------------------
    // Override Widgets Control Methods
    // ------------------------------------------------------------------------

    // Initialize Activity Widgets
    @Override
    protected void initWidgets() {
        super.initWidgets();

        // Initialize Write Data EditText
        txtWriteValue = (TextView) findViewById(R.id.write_value);
        txtWriteValue.setOnClickListener(this);

        // Initialize Action Button
        btnAction = (Button) findViewById(R.id.action);
        btnAction.setOnClickListener(this);

        //ATM2000:Restrict tag type only to 6C
        txtTagType = (TextView) findViewById(R.id.tag_type);
        txtTagType.setOnClickListener(null);

        setBank(BankType.EPC);
        setOffset(2);
    }

    // Eanble Activity Widgets
    @Override
    protected void enableWidgets(boolean enabled) {
        super.enableWidgets(enabled);

        if (mReader.getAction() == ActionState.Stop) {
            txtWriteValue.setEnabled(enabled);
            btnAction.setText(R.string.action_write);
        } else {
            txtWriteValue.setEnabled(false);
            btnAction.setText(R.string.action_stop);
        }
        btnAction.setEnabled(enabled);
    }

    private String getWriteValue() {
        return mWriteValue;
    }

    private void setWriteValue(String value) {
        mWriteValue = value;
        txtWriteValue.setText(mWriteValue);
    }

    // ------------------------------------------------------------------------
    // CommonDialog Result Listener
    // ------------------------------------------------------------------------

    private CommonDialog.IStringDialogListener mWriteValueListener = new CommonDialog.IStringDialogListener() {

        @Override
        public void onConfirm(String value, DialogInterface dialog) {
            setWriteValue(value);
            ATLog.i(TAG, "INFO. mWriteValueListener.$CommonDialog.IStringDialogListener.onConfirm([%s])", value);
        }
    };
    //export csv/txt
    protected  void onMoreOption(){ }
    protected void onSaveList(){ }
    protected void onSaveAsList(){ }
}
