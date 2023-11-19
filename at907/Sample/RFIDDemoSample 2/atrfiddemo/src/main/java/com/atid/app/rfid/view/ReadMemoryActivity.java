package com.atid.app.rfid.view;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atid.app.rfid.R;
import com.atid.app.rfid.adapter.MemoryListAdapter;
import com.atid.app.rfid.dialog.CommonDialog;
import com.atid.app.rfid.view.base.ReadWriteMemoryActivity;
import com.atid.lib.dev.ATRfidReader;
import com.atid.lib.dev.rfid.ATRfid900MAReader;
import com.atid.lib.dev.rfid.param.EpcMatchParam;
import com.atid.lib.dev.rfid.param.RangeValue;
import com.atid.lib.dev.rfid.param.SelectionMask6b;
import com.atid.lib.dev.rfid.type.ActionState;
import com.atid.lib.dev.rfid.type.BankType;
import com.atid.lib.dev.rfid.type.MaskMatchingType;
import com.atid.lib.dev.rfid.type.ResultCode;
import com.atid.lib.dev.rfid.type.TagType;
import com.atid.lib.diagnostics.ATLog;

import java.util.Locale;

public class ReadMemoryActivity extends ReadWriteMemoryActivity {

    private static final String TAG = ReadMemoryActivity.class.getSimpleName();

    // ------------------------------------------------------------------------
    // Member Variable
    // ------------------------------------------------------------------------

    private ListView lstReadValue;
    private TextView txtLength;
    private Button btnAction;
    private TextView txtTagType;

    private MemoryListAdapter adpReadValue;

    private int mLength;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public ReadMemoryActivity() {
        super();

        mView = R.layout.activity_read_memory;
        mLength = DEFAULT_LENGTH;
    }

    // ------------------------------------------------------------------------
    // Activity Event Handler
    // ------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.length:
                ATLog.i(TAG, "INFO. onClick(length)");
                CommonDialog.showNumberDialog(this, R.string.length, mLength, new RangeValue(1, 255), WORD_UNIT,
                        mLengthListener);
                break;
        }
    }

    // ------------------------------------------------------------------------
    // Reader Event Handler
    // ------------------------------------------------------------------------

    @Override
    public void onReaderResult(ATRfidReader reader, ResultCode code, ActionState action, String epc, String data,
                               float rssi, float phase) {
        super.onReaderResult(reader, code, action, epc, data, rssi, phase);

        if (code != ResultCode.NoError) {
            adpReadValue.clear();
        } else {
            int offset = getOffset();
            adpReadValue.setOffset(offset);
            adpReadValue.setValue(data);
        }

        ATLog.i(TAG, "EVENT. onReaderResult(%s, %s, [%s], [%s], %.2f, %.2f", code, action, epc, data, rssi, phase);
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
        int length = getLength();
        String password = getPassword();
        EpcMatchParam epc = getEpc();
        TagType tagType = getTagType();

        clear();
        enableWidgets(false);

        if(tagType == TagType.Tag6C) { // word unit
            if ((res = mReader.readMemory6c(bank, offset, length, password, epc)) != ResultCode.NoError) {
                ATLog.e(TAG, "ERROR. startAction() - Failed to read memory 6C tag [%s]", res);
                enableWidgets(true);
                return;
            }
        } else if(tagType == TagType.Tag6B){ // byte unit

            ATRfid900MAReader MAReader = (ATRfid900MAReader)mReader;
            offset *= 2;
            length *= 2;
            String mask = getSelection();

            SelectionMask6b mask6b = new SelectionMask6b(0, mask, MaskMatchingType.Match);
            if ((res = MAReader.readMemory6b(offset, length, mask6b)) != ResultCode.NoError) {
                ATLog.e(TAG, "ERROR. startAction() - Failed to read memory 6B tag [%s]", res);
                enableWidgets(true);
                return;
            }
        } else {
            Toast.makeText(this, R.string.not_supported, Toast.LENGTH_SHORT).show();
            enableWidgets(true);
        }

        ATLog.i(TAG, "INFO. startAction()");
    }

    // ------------------------------------------------------------------------
    // Override Widgets Control Methods
    // ------------------------------------------------------------------------

    // Clear Widgets
    @Override
    protected void clear() {
        super.clear();

        adpReadValue.clear();
    }

    // Initialize Activity Widgets
    @Override
    protected void initWidgets() {
        super.initWidgets();

        // Initialize Read Value
        lstReadValue = (ListView) findViewById(R.id.read_value);
        adpReadValue = new MemoryListAdapter(this);
        lstReadValue.setAdapter(adpReadValue);

        // Initialize Length
        txtLength = (TextView) findViewById(R.id.length);
        txtLength.setOnClickListener(this);

        // Initialize Action Button
        btnAction = (Button) findViewById(R.id.action);
        btnAction.setOnClickListener(this);

        //ATM2000:Restrict tag type only to 6C
        txtTagType = (TextView) findViewById(R.id.tag_type);
        txtTagType.setOnClickListener(null);

        setBank(BankType.EPC);
        setOffset(2);
        setLength(2);
    }

    // Eanble Activity Widgets
    @Override
    protected void enableWidgets(boolean enabled) {
        super.enableWidgets(enabled);

        if (mReader.getAction() == ActionState.Stop) {
            lstReadValue.setEnabled(enabled);
            txtLength.setEnabled(enabled);
            btnAction.setText(R.string.action_read);
        } else {
            lstReadValue.setEnabled(false);
            txtLength.setEnabled(false);
            btnAction.setText(R.string.action_stop);
        }
        btnAction.setEnabled(enabled);
    }

    // Get Length
    private int getLength() {
        return mLength;
    }

    private void setLength(int length) {
        mLength = length;
        txtLength.setText(String.format(Locale.US, "%d WORD", mLength));
    }

    // ------------------------------------------------------------------------
    // CommonDialog Result Listener
    // ------------------------------------------------------------------------

    private CommonDialog.INumberDialogListener mLengthListener = new CommonDialog.INumberDialogListener() {

        @Override
        public void onConfirm(int value, DialogInterface dialog) {
            setLength(value);
            ATLog.i(TAG, "INFO. mLengthListener.$CommonDialog.INumberDialogListener.onConfirm(%d)", value);
        }

    };
    //export csv/txt
    protected  void onMoreOption(){ }
    protected void onSaveList(){ }
    protected void onSaveAsList(){ }
}
