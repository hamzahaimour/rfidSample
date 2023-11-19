package com.atid.app.rfid.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.atid.app.rfid.R;
import com.atid.app.rfid.filter.InputRangeFilter;
import com.atid.lib.dev.rfid.param.QValue;
import com.atid.lib.dev.rfid.param.RangeValue;
import com.atid.lib.dev.rfid.type.BankType;
import com.atid.lib.dev.rfid.type.InventorySession;
import com.atid.lib.dev.rfid.type.InventoryTarget;
import com.atid.lib.dev.rfid.type.LockType;
import com.atid.lib.dev.rfid.type.SelectFlagType;
import com.atid.lib.dev.rfid.type.TagType;
import com.atid.lib.diagnostics.ATLog;
import com.atid.lib.util.StringUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CommonDialog {

    private static final String TAG = CommonDialog.class.getSimpleName();

    private static final int MAX_POWER_GAIN_COUNT = 31; //21: 10~30dBm  26: 5~30dBm  31: 0~30dBm

    public static final int DIALOG_CANCELED = -1;

    public static void showPowerGainDialog(Context context, int title, final int value, RangeValue range,
                                           final IPowerGainDialogListener listener) {

        List<String> powerGains = new ArrayList<String>();
        for (int i = range.getMax(), count = 0; i >= range.getMin() && count < MAX_POWER_GAIN_COUNT; i -= 10, count++) {
            powerGains.add(String.format(Locale.US, "%.1f dBm", i / 10.0F));
        }
        String arrays[] = new String[powerGains.size()];
        final String list[] = powerGains.toArray(arrays);
        int selectedItem = (range.getMax() - value) / 10;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setSingleChoiceItems(list, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                int resValue = 0;
                String listItem = list[item].replaceAll(" dBm", "");
                try {
                    resValue = (int) (Double.parseDouble(listItem) * 10.0);
                } catch (Exception ex) {
                    resValue = value;
                }
                ATLog.i(TAG, "INFO. showPowerGainDialog().$SingleChoiceItems.onClick(%d)", resValue);
                if (listener != null)
                    listener.onSelected(resValue, dialog);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.show();

        ATLog.i(TAG, "INFO. showPowerGainDialog([%s], %d, [%s])", context.getResources().getString(title), value,
                range);
    }

    public static void showBankDialog(Context context, int title, BankType bank, final IBankDialogListener listener) {

        BankType[] values = BankType.values();
        final String[] banks = new String[values.length];
        int selectedItem = bank.getValue();
        for (int i = 0; i < values.length; i++) {
            banks[i] = values[i].toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.bank);
        builder.setSingleChoiceItems(banks, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                BankType bank = BankType.valueOf(item);
                ATLog.i(TAG, "INFO. showBankDialog().$SingleChoiceItem.onClick([%s])", bank);
                if (listener != null)
                    listener.onSelected(bank, dialog);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.show();

        ATLog.i(TAG, "INFO. showBankDialog([%s], [%s])", context.getResources().getString(title), bank);
    }

    public static void showLockDialog(Context context, int title, final int type, LockType lock,
                                      final ILockDialogListener listener) {

        LockType[] values = LockType.values();
        final String[] banks = new String[values.length];
        int selectedItem = lock.getCode();
        for (int i = 0; i < values.length; i++) {
            banks[i] = values[i].toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.bank);
        builder.setSingleChoiceItems(banks, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                LockType lock = LockType.valueOf(item);
                ATLog.i(TAG, "INFO. showLockDialog().$SingleChoiceItem.onClick([%s])", lock);
                if (listener != null)
                    listener.onSelected(type, lock, dialog);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.show();

        ATLog.i(TAG, "INFO. showLockDialog([%s], %d, [%s])", context.getResources().getString(title), type, lock);
    }

    public static void showSelectFlagDialog(Context context, int title, SelectFlagType flag,
                                            final ISelectFlagListener listener) {

        SelectFlagType[] values = SelectFlagType.values();
        final String[] flags = new String[values.length];
        int selectedItem = flag.getValue();
        for (int i = 0; i < values.length; i++) {
            flags[i] = values[i].toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.selection_flag);
        builder.setSingleChoiceItems(flags, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                SelectFlagType flag = SelectFlagType.valueOf(item);
                ATLog.i(TAG, "INFO. showSelectFlagDialog().$SingleChoiceItem.onClick([%s])", flag);
                if (listener != null)
                    listener.onSelected(flag, dialog);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.show();

        ATLog.i(TAG, "INFO. showSelectFlagDialog([%s], [%s])", context.getResources().getString(title), flag);
    }

    public static void showInventorySessionDialog(Context context, int title, InventorySession session,
                                                  final IInventorySessionListener listener) {

        InventorySession[] values = InventorySession.values();
        final String[] sessions = new String[values.length];
        int selectedItem = session.getValue();
        for (int i = 0; i < values.length; i++) {
            sessions[i] = values[i].toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //builder.setTitle(R.string.bank);
        builder.setTitle(title);
        builder.setSingleChoiceItems(sessions, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                InventorySession session = InventorySession.valueOf(item);
                ATLog.i(TAG, "INFO. showInventorySessionDialog().$SingleChoiceItem.onClick([%s])", session);
                if (listener != null)
                    listener.onSelected(session, dialog);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.show();

        ATLog.i(TAG, "INFO. showInventorySessionDialog([%s], [%s])", context.getResources().getString(title), session);
    }

    public static void showInventoryTargetDialog(Context context, int title, InventoryTarget target,
                                                 final IInventoryTargetListener listener) {

        InventoryTarget[] values = InventoryTarget.values();
        final String[] targets = new String[values.length];
        int selectedItem = target.getValue();
        for (int i = 0; i < values.length; i++) {
            targets[i] = values[i].toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //builder.setTitle(R.string.bank);
        builder.setTitle(title);
        builder.setSingleChoiceItems(targets, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                InventoryTarget target = InventoryTarget.valueOf(item);
                ATLog.i(TAG, "INFO. showInventoryTargetDialog().$SingleChoiceItem.onClick([%s])", target);
                if (listener != null)
                    listener.onSelected(target, dialog);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.show();

        ATLog.i(TAG, "INFO. showInventoryTargetDialog([%s], [%s])", context.getResources().getString(title), target);
    }
    public static void showQStartValueDialog(Context context, int title, QValue qValues,
                                             final IQValueListener listener) {

        final String[] targets = new String[16];
        final QValue Qs = qValues;
        final Context con = context;
        int selectedItem = Qs.getStartQ();
        for (int i = 0; i < targets.length; i++) {
            targets[i] = String.format("%d", i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setSingleChoiceItems(targets, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                int startQ = item;
                ATLog.i(TAG, "INFO. showQStartValueDialog().$SingleChoiceItem.onClick([%s])", startQ);

                if(startQ < Qs.getMinQ() || startQ > Qs.getMaxQ()) {
                    Toast.makeText(con, R.string.q_start_description, Toast.LENGTH_LONG).show();
                } else {
                    if (listener != null)
                        listener.onSelected(startQ, dialog);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.show();

        ATLog.i(TAG, "INFO. showQStartValueDialog([%s], [%s])", context.getResources().getString(title), qValues);
    }

    public static void showQMaxValueDialog(Context context, int title, QValue qValues,
                                           final IQValueListener listener) {

        final String[] targets = new String[16];
        final QValue Qs = qValues;
        final Context con = context;
        int selectedItem = Qs.getMaxQ();
        for (int i = 0; i < targets.length; i++) {
            targets[i] = String.format("%d", i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setSingleChoiceItems(targets, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                int maxQ = item;
                ATLog.i(TAG, "INFO. showQMaxValueDialog().$SingleChoiceItem.onClick([%s])", maxQ);

                if(maxQ < Qs.getStartQ() || maxQ < Qs.getMinQ()) {
                    Toast.makeText(con, R.string.q_max_description, Toast.LENGTH_LONG).show();
                } else {
                    if (listener != null)
                        listener.onSelected(maxQ, dialog);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.show();

        ATLog.i(TAG, "INFO. showQMaxValueDialog([%s], [%s])", context.getResources().getString(title), qValues);
    }

    public static void showQMinValueDialog(Context context, int title, QValue qValues,
                                           final IQValueListener listener) {

        final String[] targets = new String[16];
        final QValue Qs = qValues;
        final Context con = context;
        int selectedItem = Qs.getMinQ();
        for (int i = 0; i < targets.length; i++) {
            targets[i] = String.format("%d", i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setSingleChoiceItems(targets, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                int minQ = item;
                ATLog.i(TAG, "INFO. showQMaxValueDialog().$SingleChoiceItem.onClick([%s])", minQ);

                if(minQ > Qs.getStartQ() || minQ > Qs.getMaxQ()) {
                    Toast.makeText(con, R.string.q_min_description, Toast.LENGTH_LONG).show();
                } else {
                    if (listener != null)
                        listener.onSelected(minQ, dialog);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.show();

        ATLog.i(TAG, "INFO. showQMaxValueDialog([%s], [%s])", context.getResources().getString(title), qValues);
    }

    public static void showNumberDialog(Context context, int title, final int value, RangeValue range, String unit,
                                        final INumberDialogListener listener) {

        LinearLayout root = (LinearLayout) LinearLayout.inflate(context, R.layout.dialog_input_number_unit, null);
        final EditText edtValue = (EditText) root.findViewById(R.id.value);
        InputFilter filters[] = new InputFilter[] { new InputRangeFilter(range.getMin(), range.getMax()) };
        edtValue.setFilters(filters);
        edtValue.setText(String.format(Locale.US, "%d", value));
        edtValue.selectAll();
        TextView txtUnit = (TextView) root.findViewById(R.id.unit);
        txtUnit.setText(unit);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(root);
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int resValue = 0;
                try {
                    resValue = Integer.parseInt(edtValue.getText().toString());
                } catch (Exception ex) {
                    resValue = 0;
                }
                ATLog.i(TAG, "INFO. showNumberDialog()$PositiveButton.onClick(%d)", resValue);
                if (listener != null)
                    listener.onConfirm(resValue, dialog);
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        ATLog.i(TAG, "INFO. showNumberDialog([%s], %d, [%s], [%s])", context.getResources().getString(title), value,
                range, unit);
    }

    public static void showStringDialog(Context context, int title, String value,
                                        final IStringDialogListener listener) {

        LinearLayout root = (LinearLayout) LinearLayout.inflate(context, R.layout.dialog_input_string, null);
        final EditText edtValue = (EditText) root.findViewById(R.id.value);
        edtValue.setText(value);
        edtValue.selectAll();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(root);
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String resValue = edtValue.getText().toString();
                ATLog.i(TAG, "INFO. showStringDialog()$PositiveButton.onClick(%s)", resValue);
                if (listener != null)
                    listener.onConfirm(resValue, dialog);
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        ATLog.i(TAG, "INFO. showStringDialog([%s], [%s])", context.getResources().getString(title), value);
    }

    public static void showHexPasswordDialog(Context context, int title, String value,
                                             final IStringDialogListener listener) {

        LinearLayout root = (LinearLayout) LinearLayout.inflate(context, R.layout.dialog_hex_password, null);
        final EditText edtValue = (EditText) root.findViewById(R.id.password);
        edtValue.setText(StringUtil.padLeft(value, 8, '0'));
        edtValue.selectAll();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(root);
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String resValue = edtValue.getText().toString();
                if (resValue == "00000000")
                    resValue = "";
                ATLog.i(TAG, "INFO. showHexPasswordDialog()$PositiveButton.onClick(%s)", resValue);
                if (listener != null)
                    listener.onConfirm(resValue, dialog);
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        ATLog.i(TAG, "INFO. showHexPasswordDialog([%s], [%s])", context.getResources().getString(title), value);
    }

    public static void showPasswordDialog(Context context, int title, final IStringDialogListener listener) {

        LinearLayout root = (LinearLayout) LinearLayout.inflate(context, R.layout.dialog_password, null);
        final EditText edtValue = (EditText) root.findViewById(R.id.password);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(root);
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String resValue = edtValue.getText().toString();
                if (resValue == "00000000")
                    resValue = "";
                ATLog.i(TAG, "INFO. showPasswordDialog()$PositiveButton.onClick(%s)", resValue);
                if (listener != null)
                    listener.onConfirm(resValue, dialog);
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        ATLog.i(TAG, "INFO. showPasswordDialog([%s])", context.getResources().getString(title));
    }

    public static void showQuestionDialog(Context context, int title, int msg, final ICommonDialogListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setIcon(android.R.drawable.ic_menu_help);
        builder.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ATLog.i(TAG, "INFO. showQuestionDialog()$PositiveButton.onClick()");
                if (listener != null)
                    listener.onConfirm(dialog);
            }
        });
        builder.setNegativeButton(R.string.action_no, null);
        builder.setCancelable(true);
        builder.setOnCancelListener(null);
        builder.show();
        ATLog.i(TAG, "INFO. showQuestionDialog([%s], [%s])", context.getResources().getString(title),
                context.getResources().getString(msg));
    }

    public static void showArrayDialog(Context context, int title, final String[] list, final int selectedIndex,
                                       final IArrayDialogListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.bank);
        builder.setSingleChoiceItems(list, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                ATLog.i(TAG, "INFO. showArrayDialog().$SingleChoiceItem.onClick([%s])", item);
                if (listener != null)
                    listener.onSelected(item, dialog);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.show();

        ATLog.i(TAG, "INFO. showArrayDialog([%s], [%d], %d)", context.getResources().getString(title), list.length,
                selectedIndex);
    }

    public static void showTagTypeDialog(Context context, int title, final TagType value,
                                         final ITagTypeListener listener) {

        int checkedItem = value.getCode() - 0x30;
        int i=0;
        final String[] list = new String[TagType.values().length];
        for(TagType item : TagType.values()) {
            list[i++] = item.toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setSingleChoiceItems(list, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                char code = (char) (item + 0x30);
                TagType resValue = TagType.valueOf(code);



                if (listener != null)
                    listener.onSelected(resValue, dialog);
                //listener.onSelected(resValue, dialog);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setCancelable(true);
        builder.show();
    }

    public interface IPowerGainDialogListener {
        void onSelected(int value, DialogInterface dialog);
    }

    public interface IBankDialogListener {
        void onSelected(BankType bank, DialogInterface dialog);
    }

    public interface ILockDialogListener {
        void onSelected(int type, LockType lock, DialogInterface dialog);
    }

    public interface INumberDialogListener {
        void onConfirm(int value, DialogInterface dialog);
    }

    public interface IStringDialogListener {
        void onConfirm(String value, DialogInterface dialog);
    }

    public interface ICommonDialogListener {
        void onConfirm(DialogInterface dialg);
    }

    public interface ISelectFlagListener {
        void onSelected(SelectFlagType flag, DialogInterface dialog);
    }

    public interface IInventorySessionListener {
        void onSelected(InventorySession session, DialogInterface dialog);
    }

    public interface IInventoryTargetListener {
        void onSelected(InventoryTarget target, DialogInterface dialog);
    }

    public interface IQValueListener {
        void onSelected(int q, DialogInterface dialog);
    }

    public interface IArrayDialogListener {
        void onSelected(int index, DialogInterface dialog);
    }

    public interface ITagTypeListener {
        void onSelected(TagType value, DialogInterface dialog);
    }
}
