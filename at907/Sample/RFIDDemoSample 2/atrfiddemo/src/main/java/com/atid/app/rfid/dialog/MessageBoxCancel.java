package com.atid.app.rfid.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atid.app.rfid.R;
import com.atid.lib.diagnostics.ATLog;
import com.atid.lib.util.StringUtil;

import java.util.Locale;

public class MessageBoxCancel {

	private static final String TAG = MessageBoxCancel.class.getSimpleName();
	//private static final int INFO = ATLog.L2;

	private static AlertDialog mDialog = null;
	
	public static void show(Context context, int msgId, int titleId, int iconId, int okTextId,
                            DialogInterface.OnClickListener okListener, int cancelTextId,
                            final DialogInterface.OnCancelListener cancelListener) {

		if (msgId == 0) {
			ATLog.e(TAG, "ERROR. show() - Invalid message");
			return;
		}

		LinearLayout root = (LinearLayout) LinearLayout.inflate(context, R.layout.dialog_message_box, null);
		final TextView text = (TextView) root.findViewById(R.id.value);

		AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
		builder.setView(root);

		if (iconId != 0) {
			builder.setIcon(iconId);
		}
		if (titleId != 0) {
			builder.setTitle(titleId);
		}

		//builder.setMessage(msgId);
		text.setText(msgId);
		if (okTextId == 0) {
			okTextId = R.string.action_ok;
		}
		builder.setPositiveButton(okTextId, okListener);
		if (cancelTextId != 0) {
			builder.setNegativeButton(cancelTextId, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (cancelListener != null) {
						cancelListener.onCancel(dialog);
					}
				}
			});
			builder.setCancelable(true);
			if (cancelListener != null) {
				builder.setOnCancelListener(cancelListener);
			}
		} else {
			builder.setCancelable(false);
		}

		mDialog = builder.create();
		mDialog.show();
		
		ATLog.i(TAG,  "INFO. show([%s]%s)", context.getResources().getString(msgId),
				titleId == 0 ? "" : String.format(Locale.US, ", [%s]", context.getResources().getString(titleId)));
	}

	public static void show(Context context, int msgId, int titleId, int iconId, int okTextId,
                            DialogInterface.OnClickListener okListener, DialogInterface.OnCancelListener cancelListener) {
		show(context, msgId, titleId, iconId, okTextId, okListener, R.string.action_cancel, cancelListener);
	}

	public static void show(Context context, int msgId, int titleId, int iconId, int okTextId,
                            DialogInterface.OnClickListener okListener, int cancelTextId) {
		show(context, msgId, titleId, iconId, okTextId, okListener, cancelTextId, null);
	}

	public static void show(Context context, int msgId, int titleId, int iconId,
                            DialogInterface.OnClickListener okListener, DialogInterface.OnCancelListener cancelListener) {
		show(context, msgId, titleId, iconId, R.string.action_ok, okListener, R.string.action_cancel, cancelListener);
	}

	public static void show(Context context, int msgId, int titleId, int iconId, int okTextId,
                            DialogInterface.OnClickListener okListener) {
		show(context, msgId, titleId, iconId, okTextId, okListener, 0, null);
	}

	public static void show(Context context, int msgId, int titleId, int iconId,
                            DialogInterface.OnClickListener okListener, int cancelTextId) {
		show(context, msgId, titleId, iconId, R.string.action_ok, okListener, cancelTextId, null);
	}

	public static void show(Context context, int msgId, int titleId, int iconId,
                            DialogInterface.OnClickListener okListener) {
		show(context, msgId, titleId, iconId, R.string.action_ok, okListener, 0, null);
	}

	public static void show(Context context, int msgId, int titleId, int iconId) {
		show(context, msgId, titleId, iconId, R.string.action_ok, null, 0, null);
	}

	public static void show(Context context, int msgId, int titleId) {
		show(context, msgId, titleId, 0, R.string.action_ok, null, 0, null);
	}

	public static void show(Context context, int msgId) {
		show(context, msgId, 0, 0, R.string.action_ok, null, 0, null);
	}

	public static void show(Context context, String msg, int titleId, int iconId, int okTextId,
                            DialogInterface.OnClickListener okListener, int cancelTextId,
                            final DialogInterface.OnCancelListener cancelListener) {

		if (StringUtil.isNullOrEmpty(msg)) {
			ATLog.e(TAG, "ERROR. show() - Invalid message");
			return;
		}

		LinearLayout root = (LinearLayout) LinearLayout.inflate(context, R.layout.dialog_message_box, null);
		final TextView text = (TextView) root.findViewById(R.id.value);

		AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
		builder.setView(root);

		if (iconId != 0) {
			builder.setIcon(iconId);
		}
		if (titleId != 0) {
			builder.setTitle(titleId);
		}

		//builder.setMessage(msg);
		text.setText(msg);
		if (okTextId == 0) {
			okTextId = R.string.action_ok;
		}
		builder.setPositiveButton(okTextId, okListener);
		if (cancelTextId != 0) {
			builder.setNegativeButton(cancelTextId, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (cancelListener != null) {
						cancelListener.onCancel(dialog);
					}
				}
			});
			builder.setCancelable(true);
			if (cancelListener != null) {
				builder.setOnCancelListener(cancelListener);
			}
		} else {
			builder.setCancelable(false);
		}

		mDialog = builder.create();
		mDialog.show();

		ATLog.i(TAG,  "INFO. show([%s]%s)", msg,
				titleId == 0 ? "" : String.format(Locale.US, ", [%s]", context.getResources().getString(titleId)));
	}

	public static void show(Context context, String msg, int titleId, int iconId, int okTextId,
                            DialogInterface.OnClickListener okListener, DialogInterface.OnCancelListener cancelListener) {
		show(context, msg, titleId, iconId, okTextId, okListener, R.string.action_cancel, cancelListener);
	}

	public static void show(Context context, String msg, int titleId, int iconId, int okTextId,
                            DialogInterface.OnClickListener okListener, int cancelTextId) {
		show(context, msg, titleId, iconId, okTextId, okListener, cancelTextId, null);
	}

	public static void show(Context context, String msg, int titleId, int iconId,
                            DialogInterface.OnClickListener okListener, DialogInterface.OnCancelListener cancelListener) {
		show(context, msg, titleId, iconId, R.string.action_ok, okListener, R.string.action_cancel, cancelListener);
	}

	public static void show(Context context, String msg, int titleId, int iconId, int okTextId,
                            DialogInterface.OnClickListener okListener) {
		show(context, msg, titleId, iconId, okTextId, okListener, 0, null);
	}

	public static void show(Context context, String msg, int titleId, int iconId,
                            DialogInterface.OnClickListener okListener, int cancelTextId) {
		show(context, msg, titleId, iconId, R.string.action_ok, okListener, cancelTextId, null);
	}

	public static void show(Context context, String msg, int titleId, int iconId,
                            DialogInterface.OnClickListener okListener) {
		show(context, msg, titleId, iconId, R.string.action_ok, okListener, 0, null);
	}

	public static void show(Context context, String msg, int titleId, int iconId) {
		show(context, msg, titleId, iconId, R.string.action_ok, null, 0, null);
	}

	public static void show(Context context, String msg, int titleId) {
		show(context, msg, titleId, 0, R.string.action_ok, null, 0, null);
	}

	public static void show(Context context, String msg, String title, Drawable icon, String okText,
                            DialogInterface.OnClickListener okListener, String cancelText,
                            final DialogInterface.OnCancelListener cancelListener) {

		if (StringUtil.isNullOrEmpty(msg)) {
			ATLog.e(TAG, "ERROR. show() - Invalid message");
			return;
		}

		LinearLayout root = (LinearLayout) LinearLayout.inflate(context, R.layout.dialog_message_box, null);
		final TextView text = (TextView) root.findViewById(R.id.value);

		AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
		builder.setView(root);
		if (icon != null)
			builder.setIcon(icon);
		if (!StringUtil.isNullOrEmpty(title))
			builder.setTitle(title);

		//builder.setMessage(msg);
		text.setText(msg);
		if (StringUtil.isNullOrEmpty(okText))
			okText = context.getResources().getString(R.string.action_ok);
		builder.setPositiveButton(okText, okListener);
		if (!StringUtil.isNullOrEmpty(cancelText)) {
			builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (cancelListener != null) {
						cancelListener.onCancel(dialog);
					}
				}
			});
			builder.setCancelable(true);
			if (cancelListener != null) {
				builder.setOnCancelListener(cancelListener);
			}
		} else {
			builder.setCancelable(false);
		}

		mDialog = builder.create();
		mDialog.show();

		ATLog.i(TAG,  "INFO. show([%s]%s)", msg,
				StringUtil.isNullOrEmpty(title) ? "" : String.format(Locale.US, ", [%s]", title));
	}

	public static void show(Context context, String msg, String title, Drawable icon, String okText,
                            DialogInterface.OnClickListener okListener, final DialogInterface.OnCancelListener cancelListener) {
		show(context, msg, title, icon, okText, okListener, context.getResources().getString(R.string.action_cancel),
				cancelListener);
	}

	public static void show(Context context, String msg, String title, Drawable icon, String okText,
                            DialogInterface.OnClickListener okListener, String cancelText) {
		show(context, msg, title, icon, okText, okListener, cancelText, null);
	}

	public static void show(Context context, String msg, String title, Drawable icon,
                            DialogInterface.OnClickListener okListener, DialogInterface.OnCancelListener cancelListener) {
		show(context, msg, title, icon, context.getResources().getString(R.string.action_ok), okListener,
				context.getResources().getString(R.string.action_cancel), cancelListener);
	}

	public static void show(Context context, String msg, String title, Drawable icon, String okText,
                            DialogInterface.OnClickListener okListener) {
		show(context, msg, title, icon, okText, okListener, "", null);
	}

	public static void show(Context context, String msg, String title, Drawable icon,
                            DialogInterface.OnClickListener okListener, String cancelTextId) {
		show(context, msg, title, icon, context.getResources().getString(R.string.action_ok), okListener, cancelTextId, null);
	}

	public static void show(Context context, String msg, String title, Drawable icon,
                            DialogInterface.OnClickListener okListener) {
		show(context, msg, title, icon, context.getResources().getString(R.string.action_ok), okListener, "", null);
	}

	public static void show(Context context, String msg, String title, Drawable icon) {
		show(context, msg, title, icon, context.getResources().getString(R.string.action_ok), null, "", null);
	}

	public static void show(Context context, String msg, String title) {
		show(context, msg, title, null, context.getResources().getString(R.string.action_ok), null, "", null);
	}

	public static void show(Context context, String msg) {
		show(context, msg, "", null, context.getResources().getString(R.string.action_ok), null, "", null);
	}
	
	public static void hide() {
		if (null == mDialog) {
			return;
		}
		
		mDialog.dismiss();
		mDialog = null;
		ATLog.i(TAG,  "hide()");
	}
}
