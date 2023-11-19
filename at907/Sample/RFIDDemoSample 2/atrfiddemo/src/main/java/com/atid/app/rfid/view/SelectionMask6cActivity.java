package com.atid.app.rfid.view;

import com.atid.app.rfid.R;
import com.atid.app.rfid.adapter.SelectionMask6cAdapter;
import com.atid.app.rfid.dialog.CommonDialog;
import com.atid.app.rfid.dialog.CommonDialog.ICommonDialogListener;
import com.atid.app.rfid.dialog.CommonDialog.IInventorySessionListener;
import com.atid.app.rfid.dialog.CommonDialog.IInventoryTargetListener;
import com.atid.app.rfid.dialog.CommonDialog.ISelectFlagListener;
import com.atid.app.rfid.dialog.SelectionMask6cDialog;
import com.atid.app.rfid.dialog.SelectionMask6cDialog.ISelectionMask6cListener;
import com.atid.app.rfid.dialog.WaitDialog;
import com.atid.app.rfid.view.base.ReaderActivity;
import com.atid.lib.dev.rfid.exception.ATRfidReaderException;
import com.atid.lib.dev.rfid.param.SelectionMask6cItem;
import com.atid.lib.dev.rfid.param.SelectionMask6cList;
import com.atid.lib.dev.rfid.type.InventorySession;
import com.atid.lib.dev.rfid.type.InventoryTarget;
import com.atid.lib.dev.rfid.type.SelectFlagType;
import com.atid.lib.diagnostics.ATLog;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class SelectionMask6cActivity extends ReaderActivity
        implements OnClickListener, OnItemLongClickListener, OnCheckedChangeListener, OnItemClickListener {

    private static final String TAG = SelectionMask6cActivity.class.getSimpleName();

    private static final int MAX_SELECTION_MASK_COUNT = 8;

    private CheckBox chkUseSelectMask;
    private ListView lstMasks;
    private Button btnMaskAdd;
    private Button btnMaskEdit;
    private Button btnMaskRemove;
    private Button btnMaskClear;
    private TextView txtSelectFlag;
    private TextView txtSession;
    private TextView txtSessionFlag;
    private Button btnSave;
    private Button btnCancel;

    private SelectionMask6cAdapter adpMasks;

    private boolean mUseSelectMask;
    private SelectionMask6cList mMasks;
    private SelectFlagType mSelect;
    private InventorySession mSession;
    private InventoryTarget mTarget;

    private boolean mEnabled;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public SelectionMask6cActivity() {
        super();

        mView = R.layout.activity_selection_mask_6c;

        mUseSelectMask = false;
        mMasks = null;
        mSelect = SelectFlagType.All;
        mSession = InventorySession.S0;
        mTarget = InventoryTarget.AB;

        mEnabled = false;
    }

    // ------------------------------------------------------------------------
    // Activity Event Handler
    // ------------------------------------------------------------------------

    @Override
    protected void onStart() {
        WaitDialog.show(this, R.string.wait_select_mask);
        super.onStart();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.use_selection_mask:
                if (mUseSelectMask == isChecked)
                    return;
                mUseSelectMask = isChecked;
                enableWidgets(mUseSelectMask);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mask_add:
                showAddMaskDialog();
                break;
            case R.id.mask_edit:
                showEditMaskDialog();
                break;
            case R.id.mask_remove:
                showRemoveMaskDialog();
                break;
            case R.id.mask_clear:
                showClearMaskDialog();
                break;
            case R.id.selection_flag:
                showSelectFlagDialog();
                break;
            case R.id.session:
                showSessionDialog();
                break;
            case R.id.session_flag:
                showSessionFlagDialog();
                break;
            case R.id.save:
                saveSelectionMask();
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() != R.id.mask_list)
            return;
        btnMaskEdit.setEnabled(mEnabled && lstMasks.getCheckedItemPosition() >= 0);
        btnMaskRemove.setEnabled(mEnabled && lstMasks.getCheckedItemPosition() >= 0);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() != R.id.mask_list)
            return true;

        showRemoveMaskDialog();

        return true;
    }

    // ------------------------------------------------------------------------
    // Override Widgets Control Methods
    // ------------------------------------------------------------------------

    // Initialize Activity Widgets
    @Override
    protected void initWidgets() {
        chkUseSelectMask = (CheckBox) findViewById(R.id.use_selection_mask);
        chkUseSelectMask.setOnCheckedChangeListener(this);

        lstMasks = (ListView) findViewById(R.id.mask_list);
        adpMasks = new SelectionMask6cAdapter(this);
        lstMasks.setAdapter(adpMasks);
        lstMasks.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lstMasks.setOnItemClickListener(this);
        lstMasks.setOnItemLongClickListener(this);

        btnMaskAdd = (Button) findViewById(R.id.mask_add);
        btnMaskAdd.setOnClickListener(this);

        btnMaskEdit = (Button) findViewById(R.id.mask_edit);
        btnMaskEdit.setOnClickListener(this);

        btnMaskRemove = (Button) findViewById(R.id.mask_remove);
        btnMaskRemove.setOnClickListener(this);

        btnMaskClear = (Button) findViewById(R.id.mask_clear);
        btnMaskClear.setOnClickListener(this);

        txtSelectFlag = (TextView) findViewById(R.id.selection_flag);
        txtSelectFlag.setOnClickListener(this);

        txtSession = (TextView) findViewById(R.id.session);
        txtSession.setOnClickListener(this);

        txtSessionFlag = (TextView) findViewById(R.id.session_flag);
        txtSessionFlag.setOnClickListener(this);

        btnSave = (Button) findViewById(R.id.save);
        btnSave.setOnClickListener(this);
        btnSave.setEnabled(true);

        btnCancel = (Button) findViewById(R.id.cancel);
        btnCancel.setOnClickListener(this);
        btnCancel.setEnabled(true);

        ATLog.i(TAG, "INFO. initWidgets()");
    }

    // Eanble Activity Widgets
    @Override
    protected void enableWidgets(boolean enabled) {
        mEnabled = enabled;
        lstMasks.setEnabled(mEnabled);
        btnMaskAdd.setEnabled(mEnabled && adpMasks.getCount() < MAX_SELECTION_MASK_COUNT);
        btnMaskEdit.setEnabled(mEnabled && lstMasks.getCheckedItemPosition() >= 0);
        btnMaskRemove.setEnabled(mEnabled && lstMasks.getCheckedItemPosition() >= 0);
        btnMaskClear.setEnabled(mEnabled && adpMasks.getCount() > 0);

        ATLog.i(TAG, "INFO. enableWidgets(%s)", enabled);
    }

    // Initialize Reader
    @Override
    protected void initReader() {
        // Get Use Selection Mask
        try {
            mUseSelectMask = mReader.getUseSelectionMask();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get use selection mask");
        }
        // Get Mask Item
        try {
            mMasks = mReader.getSelectionMask6cList();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get selection mask list");
        }
        // Get Select Flag
        try {
            mSelect = mReader.getSelectFlag();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get select flag");
        }
        // Get Inventory Session
        try {
            mSession = mReader.getInventorySession();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get inventory session");
        }
        // Get Inventory Target
        try {
            mTarget = mReader.getInventoryTarget();
        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, e, "ERROR. initReader() - Failed to get inventory target");
        }

        ATLog.i(TAG, "INFO initReader()");
    }

    // Activated Reader
    @Override
    protected void activateReader() {

        chkUseSelectMask.setChecked(mUseSelectMask);
        enableWidgets(mUseSelectMask);
        lstMasks.setEnabled(mUseSelectMask);

        if(mMasks != null)
            adpMasks.addAll(mMasks);

        if(mSelect != null)
            txtSelectFlag.setText(mSelect.toString());

        if(mSession != null)
            txtSession.setText(mSession.toString());

        if(mTarget != null)
            txtSessionFlag.setText(mTarget.toString());

        chkUseSelectMask.setEnabled(true);
        WaitDialog.hide();

        ATLog.i(TAG, "INFO. activateReader()");
    }

    // Show Add Selection Mask Item Dialog
    private void showAddMaskDialog() {
        SelectionMask6cDialog.show(this, R.string.add_mask_title, new ISelectionMask6cListener() {

            @Override
            public void onConfirm(SelectionMask6cItem item, DialogInterface dialog) {
                adpMasks.add(item);
                mMasks = adpMasks.getList();
                btnMaskAdd.setEnabled(mEnabled && adpMasks.getCount() < MAX_SELECTION_MASK_COUNT);
                btnMaskEdit.setEnabled(mEnabled && lstMasks.getCheckedItemPosition() >= 0);
                btnMaskRemove.setEnabled(mEnabled && lstMasks.getCheckedItemPosition() >= 0);
                btnMaskClear.setEnabled(mEnabled && adpMasks.getCount() > 0);
                ATLog.i(TAG, "INFO. showAddMaskDialog().onConfirm([%s])", item);
            }

        });
        ATLog.i(TAG, "INFO. showAddMaskDialog()");
    }

    // Show Edit Selection Mask Item Dialog
    private void showEditMaskDialog() {
        final int position = lstMasks.getCheckedItemPosition();
        if (position < 0)
            return;
        SelectionMask6cItem item = adpMasks.getItem(position);
        SelectionMask6cDialog.show(this, R.string.edit_mask_title, item, new ISelectionMask6cListener() {

            @Override
            public void onConfirm(SelectionMask6cItem item, DialogInterface dialog) {
                adpMasks.update(position, item);
                mMasks = adpMasks.getList();
                btnMaskAdd.setEnabled(mEnabled && adpMasks.getCount() < MAX_SELECTION_MASK_COUNT);
                btnMaskEdit.setEnabled(mEnabled && lstMasks.getCheckedItemPosition() >= 0);
                btnMaskRemove.setEnabled(mEnabled && lstMasks.getCheckedItemPosition() >= 0);
                btnMaskClear.setEnabled(mEnabled && adpMasks.getCount() > 0);
                ATLog.i(TAG, "INFO. showEditMaskDialog().onConfirm([%s])", item);
            }

        });
        ATLog.i(TAG, "INFO. showEditMaskDialog()");
    }

    // Show Remove Selection Mask Item Dialog
    private void showRemoveMaskDialog() {
        final int position = lstMasks.getCheckedItemPosition();
        if (position < 0)
            return;

        CommonDialog.showQuestionDialog(this, R.string.remove_mask_title, R.string.remove_mask_msg,
                new ICommonDialogListener() {

                    @Override
                    public void onConfirm(DialogInterface dialg) {
                        lstMasks.setItemChecked(position, false);
                        adpMasks.remove(position);
                        mMasks = adpMasks.getList();
                        btnMaskAdd.setEnabled(mEnabled && adpMasks.getCount() < MAX_SELECTION_MASK_COUNT);
                        btnMaskEdit.setEnabled(mEnabled && lstMasks.getCheckedItemPosition() >= 0);
                        btnMaskRemove.setEnabled(mEnabled && lstMasks.getCheckedItemPosition() >= 0);
                        btnMaskClear.setEnabled(mEnabled && adpMasks.getCount() > 0);
                        ATLog.i(TAG, "INFO. showRemoveMaskDialog().onConfirm()");
                    }

                });

        ATLog.i(TAG, "INFO. showRemoveMaskDialog()");
    }

    // Show Clear Selection Mask Item Dialog
    private void showClearMaskDialog() {
        if (adpMasks.getCount() <= 0)
            return;

        CommonDialog.showQuestionDialog(this, R.string.clear_mask_title, R.string.clear_mask_msg,
                new ICommonDialogListener() {

                    @Override
                    public void onConfirm(DialogInterface dialg) {
                        adpMasks.clear();
                        mMasks = adpMasks.getList();
                        btnMaskAdd.setEnabled(mEnabled && adpMasks.getCount() < MAX_SELECTION_MASK_COUNT);
                        btnMaskEdit.setEnabled(mEnabled && lstMasks.getCheckedItemPosition() >= 0);
                        btnMaskRemove.setEnabled(mEnabled && lstMasks.getCheckedItemPosition() >= 0);
                        btnMaskClear.setEnabled(mEnabled && adpMasks.getCount() > 0);
                        ATLog.i(TAG, "INFO. showClearMaskDialog().onConfirm()");
                    }

                });

        ATLog.i(TAG, "INFO. showClearMaskDialog()");
    }

    // Show Select Flag Dialog
    private void showSelectFlagDialog() {
        if (!txtSelectFlag.isEnabled())
            return;

        CommonDialog.showSelectFlagDialog(this, R.string.selection_flag, mSelect, new ISelectFlagListener() {

            @Override
            public void onSelected(SelectFlagType flag, DialogInterface dialog) {
                mSelect = flag;
                txtSelectFlag.setText(mSelect.toString());
            }
        });
        ATLog.i(TAG, "INFO. showSelectFlagDialog()");
    }

    // Show Session Dialog
    private void showSessionDialog() {
        if (!txtSession.isEnabled())
            return;

        CommonDialog.showInventorySessionDialog(this, R.string.inventory_session, mSession,
                new IInventorySessionListener() {

                    @Override
                    public void onSelected(InventorySession session, DialogInterface dialog) {
                        mSession = session;
                        txtSession.setText(mSession.toString());
                    }
                });
        ATLog.i(TAG, "INFO. showSessionDialog()");
    }

    // Show Session Flag Dialog
    private void showSessionFlagDialog() {
        if (!txtSessionFlag.isEnabled())
            return;

        CommonDialog.showInventoryTargetDialog(this, R.string.session_flag, mTarget, new IInventoryTargetListener() {

            @Override
            public void onSelected(InventoryTarget target, DialogInterface dialog) {
                mTarget = target;
                txtSessionFlag.setText(mTarget.toString());
            }
        });
        ATLog.i(TAG, "INFO. showSessionFlagDilog()");
    }

    // Save Selection Mask
    private void saveSelectionMask() {
        WaitDialog.show(this, "Saving Selection Mask\r\nPlease Wait...");

        new Thread(new Runnable() {

            @Override
            public void run() {
                // Set Use Selection Mask
                try {
                    mReader.setUseSelectionMask(mUseSelectMask);
                } catch (ATRfidReaderException e) {
                    ATLog.e(TAG, e, "ERROR. saveSelectionMask().run() - Failed to set use selection mask [%s]",
                            mUseSelectMask);
                }
                if (mUseSelectMask) {
                    try {
                        mReader.setSelectionMask6cList(mMasks);
                    } catch (ATRfidReaderException e) {
                        ATLog.e(TAG, e, "ERROR. saveSelectionMask() - Failed to set selection mask [%s]", mMasks);
                    }

                    try {
                        mReader.setSelectFlag(mSelect);
                    } catch (ATRfidReaderException e) {
                        ATLog.e(TAG, e, "ERROR. saveSelectionMask().run() - Failed to set selection flag [%s]", mSelect);
                    }

                    try {
                        mReader.setInventorySession(mSession);
                    } catch (ATRfidReaderException e) {
                        ATLog.e(TAG, e, "ERROR. saveSelectionMask().run() - Failed to set inventory session [%s]", mSession);
                    }

                    try {
                        mReader.setInventoryTarget(mTarget);
                    } catch (ATRfidReaderException e) {
                        ATLog.e(TAG, e, "ERROR. saveSelectionMask().run() - Failed to set inventory target [%s]", mTarget);
                    }
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        WaitDialog.hide();
                        finish();
                    }});
            }
        }).start();
        ATLog.i(TAG, "INFO. saveSelectionMask()");
    }
}
