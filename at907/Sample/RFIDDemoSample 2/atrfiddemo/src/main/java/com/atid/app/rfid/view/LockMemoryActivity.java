package com.atid.app.rfid.view;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atid.app.rfid.R;
import com.atid.app.rfid.dialog.CommonDialog;
import com.atid.app.rfid.view.base.AccessActivity;
import com.atid.lib.dev.rfid.exception.ATRfidReaderException;
import com.atid.lib.dev.rfid.param.EpcMatchParam;
import com.atid.lib.dev.rfid.param.LockParam;
import com.atid.lib.dev.rfid.type.ActionState;
import com.atid.lib.dev.rfid.type.BankType;
import com.atid.lib.dev.rfid.type.LockType;
import com.atid.lib.dev.rfid.type.ResultCode;
import com.atid.lib.dev.rfid.type.TagType;
import com.atid.lib.diagnostics.ATLog;

import java.util.Locale;

public class LockMemoryActivity extends AccessActivity implements OnClickListener {

    private static final String TAG = LockMemoryActivity.class.getSimpleName();

    private static final int OFFSET_ACCESS_PASSWORD = 2;

    private static final int TYPE_KILL_PASSWORD = 0;
    private static final int TYPE_ACCESS_PASSWORD = 1;
    private static final int TYPE_EPC = 2;
    private static final int TYPE_TID = 3;
    private static final int TYPE_USER = 4;

    // ------------------------------------------------------------------------
    // Member Variable
    // ------------------------------------------------------------------------

    private TextView txtKillPassword;
    private TextView txtAccessPassword;
    private TextView txtEpc;
    private TextView txtTid;
    private TextView txtUser;
    private Button btnAction;
    private Button btnSetPassword;
    private TextView txtTagType;

    private LockParam mLock;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public LockMemoryActivity() {
        super();

        mView = R.layout.activity_lock_memory;
        mLock = new LockParam();
    }

    // ------------------------------------------------------------------------
    // Activity Event Handler
    // ------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.kill_password:
                ATLog.i(TAG, "INFO. onClick(kill_password)");
                CommonDialog.showLockDialog(this, R.string.kill_password, TYPE_KILL_PASSWORD, mLock.getKillPassword(),
                        mLockListener);
                break;
            case R.id.access_password:
                ATLog.i(TAG, "INFO. onClick(access_password)");
                CommonDialog.showLockDialog(this, R.string.access_password, TYPE_ACCESS_PASSWORD,
                        mLock.getAccessPassword(), mLockListener);
                break;
            case R.id.epc:
                ATLog.i(TAG, "INFO. onClick(epc)");
                CommonDialog.showLockDialog(this, R.string.epc, TYPE_EPC, mLock.getEPC(), mLockListener);
                break;
            case R.id.tid:
                ATLog.i(TAG, "INFO. onClick(tid)");
                CommonDialog.showLockDialog(this, R.string.tid, TYPE_TID, mLock.getTID(), mLockListener);
                break;
            case R.id.user:
                ATLog.i(TAG, "INFO. onClick(user)");
                CommonDialog.showLockDialog(this, R.string.user, TYPE_USER, mLock.getUser(), mLockListener);
                break;
            case R.id.set_password:
                ATLog.i(TAG, "INFO. onClick(set_password)");
                CommonDialog.showHexPasswordDialog(this, R.string.set_password, "", mSetPasswordListener);
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
        String password = getPassword();
        EpcMatchParam epc = getEpc();
        TagType tagType = getTagType();

        if(tagType != TagType.Tag6C) {
            Toast.makeText(this, R.string.not_supported, Toast.LENGTH_LONG).show();
            enableWidgets(true);
            return;
        }

        clear();
        enableWidgets(false);

        LockParam param = new LockParam();
        param.setKillPassword(LockType.NoChange);
        param.setAccessPassword(LockType.NoChange);
        param.setEPC(LockType.Unlock);
        param.setTID(LockType.NoChange);
        param.setUser(LockType.NoChange);

        ResultCode result = mReader.lock6c(param, "12345678", null);

//        if ((res = mReader.lock6c(mLock, password, epc)) != ResultCode.NoError) {
//            ATLog.e(TAG, "ERROR. startAction() - Failed to lock 6C tag [%s]", res);
//            enableWidgets(true);
//            return;
//        }

        ATLog.i(TAG, "INFO. startAction()");
    }

    // ------------------------------------------------------------------------
    // Override Widgets Control Methods
    // ------------------------------------------------------------------------

    // Initialize Activity Widgets
    @Override
    protected void initWidgets() {
        super.initWidgets();

        // Initialize KillPassword Spinner
        txtKillPassword = (TextView) findViewById(R.id.kill_password);
        txtKillPassword.setOnClickListener(this);

        // Initialize AccessPassword Spinner
        txtAccessPassword = (TextView) findViewById(R.id.access_password);
        txtAccessPassword.setOnClickListener(this);

        // Initialize EPC Spinner
        txtEpc = (TextView) findViewById(R.id.epc);
        txtEpc.setOnClickListener(this);

        // Initialize TID Spinner
        txtTid = (TextView) findViewById(R.id.tid);
        txtTid.setOnClickListener(this);

        // Initialize User Spinner
        txtUser = (TextView) findViewById(R.id.user);
        txtUser.setOnClickListener(this);

        // Initialize Action Button
        btnAction = (Button) findViewById(R.id.action);
        btnAction.setOnClickListener(this);

        //ATM2000:Restrict tag type only to 6C
        txtTagType = (TextView) findViewById(R.id.tag_type);
        txtTagType.setOnClickListener(null);

        // Initialize Set Password Button
        btnSetPassword = (Button) findViewById(R.id.set_password);
        btnSetPassword.setOnClickListener(this);

        setLock(TYPE_KILL_PASSWORD, LockType.NoChange);
        setLock(TYPE_ACCESS_PASSWORD, LockType.NoChange);
        setLock(TYPE_EPC, LockType.NoChange);
        setLock(TYPE_TID, LockType.NoChange);
        setLock(TYPE_USER, LockType.NoChange);
    }

    // Eanble Activity Widgets
    @Override
    protected void enableWidgets(boolean enabled) {
        super.enableWidgets(enabled);

        if (mReader.getAction() == ActionState.Stop) {
            txtKillPassword.setEnabled(enabled);
            txtAccessPassword.setEnabled(enabled);
            txtEpc.setEnabled(enabled);
            txtTid.setEnabled(enabled);
            txtUser.setEnabled(enabled);
            btnAction.setText(R.string.action_lock);
            btnSetPassword.setEnabled(enabled);
        } else {
            txtKillPassword.setEnabled(false);
            txtAccessPassword.setEnabled(false);
            txtEpc.setEnabled(false);
            txtTid.setEnabled(false);
            txtUser.setEnabled(false);
            btnAction.setText(R.string.action_stop);
            btnSetPassword.setEnabled(false);
        }
        btnAction.setEnabled(enabled);
    }

    private void setLock(int type, LockType lock) {
        switch (type) {
            case TYPE_KILL_PASSWORD:
                mLock.setKillPassword(lock);
                txtKillPassword.setText(mLock.getKillPassword().toString());
                break;
            case TYPE_ACCESS_PASSWORD:
                mLock.setAccessPassword(lock);
                txtAccessPassword.setText(mLock.getAccessPassword().toString());
                break;
            case TYPE_EPC:
                mLock.setEPC(lock);
                txtEpc.setText(mLock.getEPC().toString());
                break;
            case TYPE_TID:
                mLock.setTID(lock);
                txtTid.setText(mLock.getTID().toString());
                break;
            case TYPE_USER:
                mLock.setUser(lock);
                txtUser.setText(mLock.getUser().toString());
                break;
        }
    }

    // ------------------------------------------------------------------------
    // CommonDialog Result Listener
    // ------------------------------------------------------------------------

    private CommonDialog.ILockDialogListener mLockListener = new CommonDialog.ILockDialogListener() {

        @Override
        public void onSelected(int type, LockType lock, DialogInterface dialog) {
            setLock(type, lock);
            ATLog.i(TAG, "INFO. mLockListener.$CommonDialog.ILockDialogListener.onSelected(%d, %s)", type, lock);
        }
    };

    private CommonDialog.IStringDialogListener mSetPasswordListener = new CommonDialog.IStringDialogListener() {

        @Override
        public void onConfirm(String value, DialogInterface dialog) {
            clear();
            ResultCode res;
            int time = getOperationTime();
            String password = getPassword();
            EpcMatchParam epc = getEpc();
            try {
                mReader.setOperationTime(time);
            } catch (ATRfidReaderException e) {
                ATLog.e(TAG, String.format(Locale.US,
                        "ERROR. setAccessPassword() - Failed to set operation time(%d)", time), e);
            }
            if ((res = mReader.writeMemory6c(BankType.Reserved, OFFSET_ACCESS_PASSWORD, value,
                    password, epc)) != ResultCode.NoError) {
                ATLog.e(TAG,
                        String.format(Locale.US,
                                "ERROR. setAccessPassword() - Failed to write memory {[%s], [%s]} - [%s]", value,
                                password, res));
                return;
            }
            ATLog.i(TAG, "INFO. mSetPasswordListener.$CommonDialog.IStringDialogListener.onConfirm([%s])", value);
        }
    };
    //export csv/txt
    protected  void onMoreOption(){ }
    protected void onSaveList(){ }
    protected void onSaveAsList(){ }
}