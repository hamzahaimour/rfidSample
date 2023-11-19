package com.atid.app.rfid.view.base;

import com.atid.app.rfid.R;
import com.atid.lib.dev.ATRfidManager;
import com.atid.lib.dev.ATRfidReader;
import com.atid.lib.dev.event.RfidReaderEventListener;
import com.atid.lib.dev.rfid.type.ActionState;
import com.atid.lib.dev.rfid.type.ConnectionState;
import com.atid.lib.dev.rfid.type.ResultCode;
import com.atid.lib.diagnostics.ATLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

public abstract class ReaderActivity extends Activity implements RfidReaderEventListener {

    // ------------------------------------------------------------------------
    // Member Variable
    // ------------------------------------------------------------------------

    private static final String TAG = ReaderActivity.class.getSimpleName();

    protected ATRfidReader mReader;
    protected int mView;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public ReaderActivity() {
        super();
        mReader = null;
        mView = 0;
    }

    // ------------------------------------------------------------------------
    // Activit Event Handler
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize RFID Reader
        if ((mReader = ATRfidManager.getInstance()) == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setTitle(R.string.module_error);
            builder.setMessage(R.string.fail_check_module);
            builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ReaderActivity.this.setResult(Activity.RESULT_CANCELED);
                    finish();
                }

            });
        }

        // Initialize Widgets
        initWidgets();

        // Disable All Widgets
        enableWidgets(false);

        ATLog.i(TAG, "INFO. onCreate()");
    }

    @Override
    protected void onDestroy() {

        // Deinitalize RFID reader Instance
        ATRfidManager.onDestroy();

        ATLog.i(TAG, "INFO. onDestroy()");

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mReader != null) {
            ATRfidManager.wakeUp();

            if (mReader.getState() == ConnectionState.Connected) {
                onInitReader(false);
            }
        }

        ATLog.i(TAG, "INFO. onStart()");
    }

    @Override
    protected void onStop() {

        ATRfidManager.sleep();

        ATLog.i(TAG, "INFO. onStop()");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mReader != null)
            mReader.setEventListener(this);

        ATLog.d(TAG, "INFO onResume()");
    }

    @Override
    protected void onPause() {

        if (mReader != null)
            mReader.removeEventListener(this);

        ATLog.i(TAG, "INFO. onPause()");
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ------------------------------------------------------------------------
    // Reader Event Handler
    // ------------------------------------------------------------------------

    @Override
    public void onReaderStateChanged(ATRfidReader reader, ConnectionState state) {
        switch (state) {
            case Connected:
                onInitReader(true);
                break;
            default:
                enableWidgets(false);
                break;
        }
        ATLog.i(TAG, "EVENT. onReaderStateChanged(%s)", state);
    }

    @Override
    public void onReaderActionChanged(ATRfidReader reader, ActionState action) {
        ATLog.i(TAG, "EVENT. onReaderActionchanged(%s)", action);
    }

    @Override
    public void onReaderReadTag(ATRfidReader reader, String tag, float rssi, float phase) {
        ATLog.i(TAG, "EVENT. onReaderReadTag([%s], %.2f, %.2f)", tag, rssi, phase);
    }

    @Override
    public void onReaderResult(ATRfidReader reader, ResultCode code, ActionState action, String epc, String data,
                               float rssi, float phase) {
        ATLog.i(TAG, "EVENT. onReaderResult(%s, %s, [%s], [%s], %.2f, %.2f", code, action, epc, data, rssi, phase);
    }

    // ------------------------------------------------------------------------
    // Override Widgets Control Methods
    // ------------------------------------------------------------------------

    // Initialize Activity Widgets
    protected abstract void initWidgets();

    // Eanble Activity Widgets
    protected abstract void enableWidgets(boolean enabled);

    // Initialize Reader
    protected abstract void initReader();

    // Activated Reader
    protected abstract void activateReader();

    // Begin Initialize Reader
    private void onInitReader(final boolean enabled) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                initReader();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activateReader();
                        if (enabled)
                            enableWidgets(true);
                    }

                });
            }

        }).start();
        ;
    }
}
