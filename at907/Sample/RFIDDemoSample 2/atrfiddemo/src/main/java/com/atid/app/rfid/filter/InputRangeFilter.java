package com.atid.app.rfid.filter;

import android.text.InputFilter;
import android.text.Spanned;

public class InputRangeFilter implements InputFilter {

    private int mMin;
    private int mMax;

    public InputRangeFilter(int min, int max) {
        mMin = min;
        mMax = max;
    }

    public InputRangeFilter(String min, String max) {
        try { mMin = Integer.parseInt(min); }
        catch (Exception ex) { mMin = 0;}
        try { mMax = Integer.parseInt(max); }
        catch (Exception ex) { mMax = 0;}
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        try {
            String input = "";
            input += dest.subSequence(0, dstart);
            input += source.subSequence(start, end);
            input += dest.subSequence(dend, dest.length());
            int value = Integer.parseInt(input);
            if (isRange(mMin, mMax, value)) {
                return null;
            }
        }
        catch (Exception ex) {

        }
        return "";
    }

    private boolean isRange(int min, int max, int value) {
        return max > min ? value >= min && value <= max : value >= max && value <= min;
    }
}
