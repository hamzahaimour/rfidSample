package com.atid.app.rfid.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atid.app.rfid.R;
import com.atid.app.rfid.types.FileDataSeperatorType;
import com.atid.lib.diagnostics.ATLog;


public class SaveFileFormatDialog implements View.OnClickListener {
	private static final String TAG = SaveFileFormatDialog.class.getSimpleName();
	//private static final int INFO = ATLog.L2;

	private int mFileDataFormat;
	private FileDataSeperatorType mSeperatorType;

	private TextView txtFileDataFormat;
	private TextView txtFileDataSeperatorType;

	private FileDataFormatDialog dlgFileDataFormat;
	private EnumListDialog dlgFileDataSeperator;

	private Context mContext;

	public SaveFileFormatDialog() {
		mFileDataFormat = 15;   // changed 7 (0,1,2,3) -> 15 (0,1,2,3,4)
		mSeperatorType = FileDataSeperatorType.Comma;
	}

	public SaveFileFormatDialog(int fileDataFormat , int seperator) {
		mFileDataFormat = fileDataFormat;
		mSeperatorType = FileDataSeperatorType.valueOf(seperator);
	}

	public int getFileDataFormat() {
		return mFileDataFormat;
	}

	public void setFileDataFormat(int value) {
		mFileDataFormat = value;
	}

	public FileDataSeperatorType getSeperatorType() {
		return mSeperatorType;
	}

	public void setSeperatorType(FileDataSeperatorType value) {
		mSeperatorType = value;
	}


	public void showDialog(Context context, final IValueChangedListener changedListener, final ICancelListener cancelListener) {
		mContext = context;

		LinearLayout root = (LinearLayout) LinearLayout.inflate(context, R.layout.dialog_save_file_format, null);

	/*	txtFileDataFormat = (TextView) root.findViewById(R.id.file_data_format);
		txtFileDataFormat.setOnClickListener(this);*/

		txtFileDataSeperatorType = (TextView) root.findViewById(R.id.file_data_separator_type);
		txtFileDataSeperatorType.setOnClickListener(this);

		dlgFileDataFormat = new FileDataFormatDialog(txtFileDataFormat, mFileDataFormat);
		dlgFileDataSeperator = new EnumListDialog(txtFileDataSeperatorType, FileDataSeperatorType.values());
		dlgFileDataSeperator.setValue(mSeperatorType);

		AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
		builder.setTitle(R.string.save_file_format);
		builder.setView(root);
		builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mFileDataFormat = dlgFileDataFormat.getValue();
				mSeperatorType = (FileDataSeperatorType) dlgFileDataSeperator.getValue();

				if (changedListener != null) {
					changedListener.onValueChanged(SaveFileFormatDialog.this);
				}

				ATLog.i(TAG,  "INFO. showDialog().$PositiveButton.onClick()");
			}
		});

		builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (cancelListener != null) {
					cancelListener.onCanceled(null);
				}
				ATLog.i(TAG,  "INFO. showDialog().$NegativeButton.onClick()");
			}
		});

		builder.setCancelable(true);
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (cancelListener != null) {
					cancelListener.onCanceled(null);
				}
				ATLog.i(TAG,  "INFO. showDialog().onCancel()");
			}
		});

		AlertDialog dialog = builder.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				dlgFileDataFormat.display();
				dlgFileDataSeperator.display();
			}
		});

		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		ATLog.i(TAG,  "INFO. showDialog()");
	}

	@Override
	public void onClick(View v) {

		switch(v.getId()) {
			/*case R.id.file_data_format:
				dlgFileDataFormat.showDialog(mContext, R.string.file_data_format);
				break;*/

			case R.id.file_data_separator_type:
				dlgFileDataSeperator.showDialog(mContext, R.string.file_data_separator_type);
				break;
		}

	}

	// ------------------------------------------------------------------------
	// Declare Interface IValueChangedListener
	// ------------------------------------------------------------------------
	public interface IValueChangedListener {
		void onValueChanged(SaveFileFormatDialog dialog);
	}

	public interface ICancelListener {
		void onCanceled(SaveFileFormatDialog dialog);
	}
}
