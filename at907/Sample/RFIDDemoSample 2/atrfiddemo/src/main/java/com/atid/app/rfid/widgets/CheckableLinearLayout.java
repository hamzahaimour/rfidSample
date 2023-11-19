package com.atid.app.rfid.widgets;

import com.atid.lib.diagnostics.ATLog;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private static final String TAG = CheckableLinearLayout.class.getSimpleName();

    private static final String NS = "http://schemas.adnroid.com/apk/res/com.aitd.app.rfid.checkable";
    private static final String ATTR = "checkable";

    private int mCheckableId;
    private Checkable mCheckable;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCheckableId = attrs.getAttributeResourceValue(NS, ATTR, 0);
        mCheckable = null;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mCheckable == null)
            mCheckable = (Checkable) findViewById(mCheckableId);
        if (mCheckable == null) {
            ATLog.e(TAG, "ERROR. setChecked(%s) - Failed to invalid checkable object", checked);
            return ;
        }
        mCheckable.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        if (mCheckable == null)
            mCheckable = (Checkable) findViewById(mCheckableId);
        if (mCheckable == null) {
            ATLog.e(TAG, "ERROR. isChecked() - Failed to invalid checkable object");
            return false;
        }
        return mCheckable.isChecked();
    }

    @Override
    public void toggle() {
        if (mCheckable == null)
            mCheckable = (Checkable) findViewById(mCheckableId);
        if (mCheckable == null) {
            ATLog.e(TAG, "ERROR. toggle() - Failed to invalid checkable object");
            return ;
        }
        mCheckable.toggle();
    }

}
