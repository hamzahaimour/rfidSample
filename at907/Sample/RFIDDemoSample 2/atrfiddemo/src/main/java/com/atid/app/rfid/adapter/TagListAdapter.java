package com.atid.app.rfid.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.atid.app.rfid.R;
import com.atid.lib.diagnostics.ATLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TagListAdapter extends BaseAdapter {

    private static final String TAG = TagListAdapter.class.getSimpleName();
    private static final boolean DEBUG_ENABLED = false;

    public static final int PC_LEN = 4;
    private static final int UPDATE_TIME = 500;

    // ------------------------------------------------------------------------
    // Member Variable
    // ------------------------------------------------------------------------

    private LayoutInflater mInflater;

    private ArrayList<TagListItem> mList;
    private ArrayList<TagListItem> mAddList;
    private HashMap<String, TagListItem> mMap;

    private boolean mIsDisplayPc;
    private boolean mIsVisibleRssi;

    private Timer mUpdater;
    private Handler mHandler;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public TagListAdapter(Context context) {
        super();

        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mList = new ArrayList<TagListItem>();
        mAddList = new ArrayList<TagListItem>();
        mMap = new HashMap<String, TagListItem>();

        mIsDisplayPc = true;
        mIsVisibleRssi = false;

        mHandler = new Handler();
        mUpdater = null;
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public void clear() {
        mList.clear();
        mMap.clear();
        notifyDataSetChanged();
    }

    public boolean isDisplayPc() {
        return mIsDisplayPc;
    }

    public void setDisplayPc(boolean enabled) {
        mIsDisplayPc = enabled;
        notifyDataSetChanged();
    }

    public boolean isVisibleRssi() {
        return mIsVisibleRssi;
    }

    public void setVisibleRssi(boolean visibled) {
        mIsVisibleRssi = visibled;
        notifyDataSetChanged();
    }

    public void addItem(String tag, float rssi, float phase) {
        TagListItem item = null;

        if ((item = mMap.get(tag)) == null) {
            item = new TagListItem(tag, rssi, phase);
            mMap.put(tag, item);
            synchronized (mAddList) {
                mAddList.add(item);
            }
        } else {
            item.updateTag(rssi, phase);
        }

        if(DEBUG_ENABLED)
            ATLog.i(TAG, "INFO. addItem([%s], %.1f, %.1f", tag, rssi, phase);
    }

    public void startUpdate() {
        mAddList.clear();
        mUpdater = new Timer();
        mUpdater.schedule(new TimerTask() {

            @Override
            public void run() {
                mHandler.post(mDoUpdate);
            }

        }, 0, UPDATE_TIME);
        ATLog.i(TAG, "INFO. startUpdate()");
    }

    public void stopUpdate() {
        if (mUpdater == null)
            return;

        mUpdater.cancel();
        mUpdater = null;
        mList.addAll(mAddList);
        mAddList.clear();
        notifyDataSetChanged();
        ATLog.i(TAG, "INFO. stopUpdate()");
    }

    public boolean isUpdate() {
        return mUpdater != null;
    }

    public void update() {
        synchronized (mAddList) {
            mList.addAll(mAddList);
            mAddList.clear();
            notifyDataSetChanged();
        }
    }

    private Runnable mDoUpdate = new Runnable() {

        @Override
        public void run() {
            update();
        }

    };

    public int getTotalCount() {
        return mList.size() + mAddList.size();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public String getItem(int position) {
        return mList.get(position).getTag();
    }
    // export to txt/csv : adding additional APIs
    public float getRssi(int position){return mList.get(position).getRssi();}
    public float getPhase(int position){return mList.get(position).getPhase();}
    public int getItemCount(int position){return mList.get(position).getCount();}

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TagListViewHolder holder;

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_tag_list, parent,
                    false);
            holder = new TagListViewHolder(convertView);
        } else {
            holder = (TagListViewHolder) convertView.getTag();
        }
        holder.setItem(mList.get(position), mIsDisplayPc, mIsVisibleRssi);

        return convertView;
    }

    // ------------------------------------------------------------------------
    // Internal Class TagListItem
    // ------------------------------------------------------------------------

    private class TagListItem {

        private String mTag;
        private float mRssi;
        private float mPhase;
        private int mCount;

        public TagListItem(String tag, float rssi, float phase) {
            mTag = tag;
            mRssi = rssi;
            mPhase = phase;
            mCount = 1;
        }

        public String getTag() {
            return mTag;
        }

        public float getRssi() {
            return mRssi;
        }

        public float getPhase() {
            return mPhase;
        }

        public int getCount() {
            return mCount;
        }

        public void updateTag(float rssi, float phase) {
            mRssi = rssi;
            mPhase = phase;
            mCount++;
        }
    }

    // ------------------------------------------------------------------------
    // Internal Class TagListViewHolder
    // ------------------------------------------------------------------------

    private class TagListViewHolder {

        private TextView txtTag;
        private TextView txtRssi;
        private TextView txtPhase;
        private TextView txtCount;
        private boolean isVisibleRssi;

        public TagListViewHolder(View parent) {
            txtTag = (TextView) parent.findViewById(R.id.tag_value);
            txtRssi = (TextView) parent.findViewById(R.id.rssi_value);
            txtPhase = (TextView) parent.findViewById(R.id.phase_value);
            txtCount = (TextView) parent.findViewById(R.id.tag_count);
            parent.setTag(this);
            isVisibleRssi = false;
        }

        public void setItem(TagListItem item, boolean displayPc,
                            boolean visibleRssi) {
            if (displayPc) {
                txtTag.setText(item.getTag());
            } else {
                txtTag.setText(item.getTag().substring(PC_LEN));
            }

            if( visibleRssi != isVisibleRssi) {
                txtRssi.setVisibility(visibleRssi ? View.VISIBLE : View.GONE);
                txtPhase.setVisibility(visibleRssi ? View.VISIBLE : View.GONE);
                isVisibleRssi = visibleRssi;
            }

            if (visibleRssi) {
                txtRssi.setText(String.format(Locale.US, "%.1f dB", item.getRssi()));
                String sPhase = String.format(Locale.US, "%.1f", item.getPhase());
                String sSymbol = String.format(Locale.US, " \u00B0");
                txtPhase.setText(sPhase + sSymbol);
            }
            txtCount.setText(String.format(Locale.US, "%d", item.getCount()));
        }
    }
}
