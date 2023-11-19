package com.atid.app.rfid.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.atid.app.rfid.R;
import com.atid.app.rfid.types.FileDataFormatType;
import com.atid.lib.diagnostics.ATLog;

import java.util.ArrayList;

public class FileDataFormatDialog extends BaseDialog {

	private static final String TAG = FileDataFormatDialog.class.getSimpleName();
	//private static final int INFO = ATLog.L2;

	private FileDataFormatType mFileDataFormatType;

	private int mOldValue;

	public FileDataFormatDialog(TextView view) {
		super(view);
		mFileDataFormatType = new FileDataFormatType();
		mOldValue = 0;
	}

	public FileDataFormatDialog(TextView view, int value) {
		super(view);
		mFileDataFormatType = new FileDataFormatType(value);
		mOldValue = value;
	}

	public int getValue() {
		return mFileDataFormatType.getValue();
	}

	public void setValue(int value) {
		mFileDataFormatType.setValue(value);
		mOldValue = value;
	}

	public void resroteValue() {
		mFileDataFormatType.setValue(mOldValue);
	}

	@Override
	public void display() {
		if (txtValue != null)
			txtValue.setText(mFileDataFormatType.toString());
	}

	@Override
	public void showDialog(Context context, String title, final IValueChangedListener changedListener, final ICancelListener cancelListener) {

		ArrayList<String> nameList = new ArrayList<String>();
		for (int i = 0 ; i < FileDataFormatType.MAX_ITEM ; i++) {
			nameList.add(mFileDataFormatType.getItemName(i));
		}

		LinearLayout root = (LinearLayout) LinearLayout.inflate(context, R.layout.dialog_list_view, null);
		final ListView list = (ListView) root.findViewById(R.id.list);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_multiple_choice, nameList);
		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
		builder.setTitle(R.string.file_data_format);
		builder.setView(root);
		builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int value = 0;
				SparseBooleanArray checkedItems = list.getCheckedItemPositions();
				for ( int i = 0 ; i < adapter.getCount() ; i++) {
					if (checkedItems.get(i))
						value |= (1 << i );
				}
				mFileDataFormatType.setValue(value);
				display();

				if (changedListener != null) {
					changedListener.onValueChanged(FileDataFormatDialog.this);
				}
				ATLog.i(TAG, "INFO. showDialog().$PositiveButton.onClick()");
			}
		});

		builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (cancelListener != null) {
					cancelListener.onCanceled(null);
				}
				ATLog.i(TAG, "INFO. showDialog().$NegativeButton.onClick()()");
			}
		});

		builder.setCancelable(true);
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (cancelListener != null) {
					cancelListener.onCanceled(null);
				}
				ATLog.i(TAG,"INFO. showDialog().onCancel()");
			}
		});

		AlertDialog dialog = builder.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				for (int i = 0 ; i < adapter.getCount() ; i++) {
					list.setItemChecked(i, mFileDataFormatType.getUsed(i));
				}
				ATLog.i(TAG,  "INFO. showDialog().onShow()");
			}
		});

		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		ATLog.i(TAG,  "INFO. showDialog()");
	}
}
