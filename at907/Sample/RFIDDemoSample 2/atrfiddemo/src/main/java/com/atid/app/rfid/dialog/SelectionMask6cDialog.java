package com.atid.app.rfid.dialog;

import java.util.Locale;

import com.atid.app.rfid.R;
import com.atid.app.rfid.adapter.SpinnerValueAdapter;
import com.atid.app.rfid.util.StringUtil;
import com.atid.lib.dev.rfid.param.SelectionMask6cItem;
import com.atid.lib.dev.rfid.type.BankType;
import com.atid.lib.dev.rfid.type.MaskActionType;
import com.atid.lib.dev.rfid.type.MaskTargetType;
import com.atid.lib.diagnostics.ATLog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

public class SelectionMask6cDialog {

    private static final String TAG = SelectionMask6cDialog.class.getSimpleName();

    private static final int NIBBLE_UNIT = 4;

    public static void show(Context context, int title, final ISelectionMask6cListener listener) {
        show(context, title, null, listener);
    }

    public static void show(Context context, int title, SelectionMask6cItem item, final ISelectionMask6cListener listener) {

        ScrollView root = (ScrollView) LinearLayout.inflate(context,
                R.layout.dialog_selection_mask_6c, null);
        // Target
        final Spinner spnTarget = (Spinner) root.findViewById(R.id.mask_target);
        final SpinnerValueAdapter adpTarget = new SpinnerValueAdapter(context, R.layout.item_spinner_dialog,
                R.layout.item_dialog_list);
        for (MaskTargetType itemTarget : MaskTargetType.values()) {
            adpTarget.addItem(itemTarget.getValue(), itemTarget.toString());
        }
        spnTarget.setAdapter(adpTarget);
        spnTarget.setSelection(adpTarget.indexOf(MaskTargetType.SL.getValue()));
        // Action
        final Spinner spnAction = (Spinner) root.findViewById(R.id.mask_action);
        final SpinnerValueAdapter adpAction = new SpinnerValueAdapter(context, R.layout.item_spinner_dialog,
                R.layout.item_dialog_list);
        for (MaskActionType itemAction : MaskActionType.values()) {
            adpAction.addItem(itemAction.getValue(), StringUtil.toString(itemAction));
        }
        spnAction.setAdapter(adpAction);
        spnAction.setSelection(adpAction.indexOf(MaskActionType.Assert_Deassert.getValue()));
        // Bank
        final Spinner spnBank = (Spinner) root.findViewById(R.id.mask_bank);
        final SpinnerValueAdapter adpBank = new SpinnerValueAdapter(context, R.layout.item_spinner_dialog,
                R.layout.item_dialog_list);
        for (BankType itemBank : BankType.values()) {
            adpBank.addItem(itemBank.getValue(), itemBank.toString());
        }
        spnBank.setAdapter(adpBank);
        // Offset
        final EditText edtOffset = (EditText) root.findViewById(R.id.mask_offset);
        edtOffset.setText("16");
        // Mask
        final EditText edtMask = (EditText) root.findViewById(R.id.mask_value);
        // Length
        final EditText edtLength = (EditText) root.findViewById(R.id.mask_length);

        edtMask.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (edtMask.isFocusable()) {
                    int length = s.length() * NIBBLE_UNIT;
                    edtLength.setText(String.format(Locale.US, "%d", length));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        if (item != null) {
            spnTarget.setSelection(adpTarget.indexOf(item.getTarget().getValue()));
            spnAction.setSelection(adpAction.indexOf(item.getAction().getValue()));
            spnBank.setSelection(adpBank.indexOf(item.getBank().getValue()));
            edtOffset.setText(String.format(Locale.US, "%d", item.getPointer()));
            edtMask.setText(item.getMask());
            edtLength.setText(String.format(Locale.US, "%d", item.getLength()));
        } else {
            spnTarget.setSelection(adpTarget.indexOf(MaskTargetType.SL.getValue()));
            spnAction.setSelection(adpAction.indexOf(MaskActionType.Assert_Deassert.getValue()));
            spnBank.setSelection(adpBank.indexOf(BankType.EPC.getValue()));
            edtOffset.setText(String.format(Locale.US, "%d", 16));
            edtMask.setText("");
            edtLength.setText(String.format(Locale.US, "%d", 0));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(root);
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SelectionMask6cItem item = new SelectionMask6cItem();

                item.setTarget(MaskTargetType.valueOf(adpTarget.getValue(spnTarget.getSelectedItemPosition())));
                item.setAction(MaskActionType.valueOf(adpAction.getValue(spnAction.getSelectedItemPosition())));
                item.setBank(BankType.valueOf(adpBank.getValue(spnBank.getSelectedItemPosition())));
                item.setPointer(Integer.parseInt(edtOffset.getText().toString()));
                item.setMask(edtMask.getText().toString());
                item.setLength(Integer.parseInt(edtLength.getText().toString()));

                ATLog.i(TAG, "INFO. show()$PositiveButton.onClick() - [%s]", item);
                if (listener != null)
                    listener.onConfirm(item, dialog);
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.show();

        ATLog.i(TAG, "INFO. show(%d, [%s])", title, item == null ? "NULL" : item);
    }

    public interface ISelectionMask6cListener {
        void onConfirm(SelectionMask6cItem item, DialogInterface dialog);
    }
}
