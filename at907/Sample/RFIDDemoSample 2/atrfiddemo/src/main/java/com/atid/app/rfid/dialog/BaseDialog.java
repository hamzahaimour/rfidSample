package com.atid.app.rfid.dialog;

import android.content.Context;
import android.widget.TextView;

public abstract class BaseDialog {

	protected TextView txtValue;
	
	public BaseDialog() {
		txtValue = null;
	}
	
	public BaseDialog(TextView view) {
		txtValue = view;
	}
	
	public void setEnabled(boolean enabled) {
		if (txtValue == null) {
			return;
		}
		txtValue.setEnabled(enabled);
	}
	
	public abstract void display();
	
	public void showDialog(Context context, int title) {
		showDialog(context, context.getResources().getString(title), null, null);
	}
	public void showDialog(Context context, int title, final IValueChangedListener changedListener) {
		showDialog(context, context.getResources().getString(title), changedListener, null);
	}
	public void showDialog(Context context, int title, final IValueChangedListener changedListener,
                           final ICancelListener cancelListener) {
		showDialog(context, context.getResources().getString(title), changedListener, cancelListener);
	}

	public void showDialog(Context context, String title) {
		showDialog(context, title, null, null);
	}
	public void showDialog(Context context, String title, final IValueChangedListener changedListener) {
		showDialog(context, title, changedListener, null);
	}
	
	public abstract void showDialog(Context context, String title, final IValueChangedListener changedListener,
                                    final ICancelListener cancelListener);

	// ------------------------------------------------------------------------
	// Declare Interface IValueChangedListener
	// ------------------------------------------------------------------------

	public interface IValueChangedListener {
		void onValueChanged(BaseDialog dialog);
	}

	// ------------------------------------------------------------------------
	// Declare Interface IValueChangedListener
	// ------------------------------------------------------------------------

	public interface ICancelListener {
		void onCanceled(BaseDialog dialog);
	}
}
