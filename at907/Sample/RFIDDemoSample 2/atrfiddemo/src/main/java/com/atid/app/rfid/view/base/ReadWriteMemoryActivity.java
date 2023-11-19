package com.atid.app.rfid.view.base;

import java.util.Locale;

import com.atid.app.rfid.R;
import com.atid.app.rfid.dialog.CommonDialog;
import com.atid.lib.dev.rfid.param.RangeValue;
import com.atid.lib.dev.rfid.type.ActionState;
import com.atid.lib.dev.rfid.type.BankType;
import com.atid.lib.dev.rfid.type.TagType;
import com.atid.lib.diagnostics.ATLog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public abstract class ReadWriteMemoryActivity extends AccessActivity {

    private static final String TAG = ReadWriteMemoryActivity.class.getSimpleName();

    protected static final int MAX_ADDRESS = 32;

    protected static final BankType DEFAULT_BANK = BankType.EPC;
    protected static final int DEFAULT_OFFSET = 2;
    protected static final int DEFAULT_LENGTH = 2;
    protected static final String WORD_UNIT = "WORD";

    // ------------------------------------------------------------------------
    // Member Variable
    // ------------------------------------------------------------------------

    private TextView txtBank;
    private TextView txtOffset;

    private BankType mBank;
    private int mOffset;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public ReadWriteMemoryActivity() {
        super();

        mBank = DEFAULT_BANK;
        mOffset = DEFAULT_OFFSET;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TagType tagType = getParentTagType();
        setTagType(tagType);
        switch(tagType) {
            case Tag6C:

                break;
            case Tag6B:
                setOffset(9);
                break;
            default:
                break;
        }
    }

    // ------------------------------------------------------------------------
    // Activity Event Handler
    // ------------------------------------------------------------------------
    @Override
    protected void startAction() {

    }



    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bank:
                ATLog.i(TAG, "INFO. onClick(bank)");
                CommonDialog.showBankDialog(this, R.string.bank, mBank, mBankListner);
                break;
            case R.id.offset:
                ATLog.i(TAG, "INFO. onClick(offset)");
                CommonDialog.showNumberDialog(this, R.string.offset, mOffset, new RangeValue(0, 255), WORD_UNIT,
                        mOffsetListener);
                break;
        }
    }

    // ------------------------------------------------------------------------
    // Override Widgets Control Methods
    // ------------------------------------------------------------------------

    // Initialize Activity Widgets
    @Override
    protected void initWidgets() {
        super.initWidgets();

        // Initialize Bank Spinner
        txtBank = (TextView) findViewById(R.id.bank);
        txtBank.setOnClickListener(this);

        // Initialize Offset Spinner
        txtOffset = (TextView) findViewById(R.id.offset);
        txtOffset.setOnClickListener(this);
    }

    @Override
    protected void enableWidgets(boolean enabled) {
        super.enableWidgets(enabled);
        if (mReader.getAction() == ActionState.Stop) {
            txtBank.setEnabled(enabled);
            txtOffset.setEnabled(enabled);
        } else {
            txtBank.setEnabled(false);
            txtOffset.setEnabled(false);
        }
    }

    // ------------------------------------------------------------------------
    // Override Widgets Access Methods
    // ------------------------------------------------------------------------

    protected BankType getBank() {
        return mBank;
    }

    protected void setBank(BankType bank) {
        mBank = bank;
        txtBank.setText(mBank.toString());
    }

    protected int getOffset() {
        return mOffset;
    }

    protected void setOffset(int offset) {
        mOffset = offset;
        txtOffset.setText(String.format(Locale.US, "%d WORD", mOffset));
    }

    // ------------------------------------------------------------------------
    // CommonDialog Result Listener
    // ------------------------------------------------------------------------

    private CommonDialog.IBankDialogListener mBankListner = new CommonDialog.IBankDialogListener() {

        @Override
        public void onSelected(BankType bank, DialogInterface dialog) {
            setBank(bank);
            ATLog.i(TAG, "INFO. mBankListner.$CommonDialog.IBankDialogListener.onSelected(%s)", bank);
        }
    };

    private CommonDialog.INumberDialogListener mOffsetListener = new CommonDialog.INumberDialogListener() {

        @Override
        public void onConfirm(int value, DialogInterface dialog) {
            setOffset(value);
            ATLog.i(TAG, "INFO. mOffsetListener.$CommonDialog.INumberDialogListener.onConfirm(%d)", value);
        }
    };
}
