package com.atid.app.rfid.adapter;

import java.util.ArrayList;
import java.util.Locale;

import com.atid.app.rfid.R;
import com.atid.app.rfid.util.StringUtil;
import com.atid.lib.dev.rfid.param.SelectionMask6cItem;
import com.atid.lib.dev.rfid.param.SelectionMask6cList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SelectionMask6cAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<SelectionMask6cItem> mList;

    public SelectionMask6cAdapter(Context context) {
        super();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList = new ArrayList<SelectionMask6cItem>();
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    public void addAll(SelectionMask6cList masks) {
        mList.clear();
        mList.addAll(masks);
        notifyDataSetChanged();
    }

    public void add(SelectionMask6cItem item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    public void update(int position, SelectionMask6cItem item) {
        mList.set(position, item);
        notifyDataSetChanged();
    }

    public SelectionMask6cList getList() {
        SelectionMask6cList list = new SelectionMask6cList();
        list.addAll(mList);
        return list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public SelectionMask6cItem getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SelectionMaskViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_selection_mask_6c,
                    parent, false);
            holder = new SelectionMaskViewHolder(convertView);
        } else {
            holder = (SelectionMaskViewHolder) convertView.getTag();
        }
        holder.setItem(position, mList.get(position));
        return convertView;
    }

    // ------------------------------------------------------------------------
    // Class SelectionMaskViewHolder
    // ------------------------------------------------------------------------
    private class SelectionMaskViewHolder {

        private TextView txtTarget;
        private TextView txtAction;
        private TextView txtBank;
        private TextView txtOffset;
        private TextView txtLength;
        private TextView txtMask;

        public SelectionMaskViewHolder(View parent) {

            txtTarget = (TextView) parent.findViewById(R.id.target);
            txtAction = (TextView) parent.findViewById(R.id.action);
            txtBank = (TextView) parent.findViewById(R.id.bank);
            txtOffset = (TextView) parent.findViewById(R.id.offset);
            txtLength = (TextView) parent.findViewById(R.id.length);
            txtMask = (TextView) parent.findViewById(R.id.mask);
            parent.setTag(this);
        }

        public void setItem(int position, SelectionMask6cItem item) {

            txtTarget.setText(item.getTarget().toString());
            txtAction.setText(StringUtil.toString(item.getAction()));
            txtBank.setText(item.getBank().toString());
            txtOffset.setText(String.format(Locale.US, "%d bit", item.getPointer()));
            txtLength.setText(String.format(Locale.US, "%d bit", item.getLength()));
            txtMask.setText(item.getMask());
        }
    }
}
