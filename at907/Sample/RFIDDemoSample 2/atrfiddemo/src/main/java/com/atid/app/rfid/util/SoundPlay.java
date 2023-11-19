package com.atid.app.rfid.util;

import com.atid.app.rfid.R;
import com.atid.lib.diagnostics.ATLog;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class SoundPlay {
    private static final String TAG = SoundPlay.class.getSimpleName();

    private MediaPlayer mSucessMediaPlayer = null;
    private MediaPlayer mFailMediaPlayer = null;

    public SoundPlay(Context context) {
        mSucessMediaPlayer = MediaPlayer.create(context, R.raw.success);
        mSucessMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mFailMediaPlayer = MediaPlayer.create(context, R.raw.fail);
        mFailMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void playSuccess() {

        if(null != mSucessMediaPlayer) {
            //ATLog.d(TAG, "playSuccess()");

            mSucessMediaPlayer.seekTo(0);
            mSucessMediaPlayer.start();

        } else {
            ATLog.e(TAG, "Failed to play a success sound !!!");
        }
    }

    public void playFail() {

        if(null != mFailMediaPlayer) {
            //ATLog.d(TAG, "playFail()");

            mFailMediaPlayer.seekTo(0);
            mFailMediaPlayer.start();

        } else {
            ATLog.e(TAG, "Failed to play a fail sound !!!");
        }

    }

    public void close() {

        if(null != mSucessMediaPlayer) {
            if(mSucessMediaPlayer.isPlaying()){
                mSucessMediaPlayer.stop();
            }

            mSucessMediaPlayer.release();
            mSucessMediaPlayer = null;
        }

        if(null != mFailMediaPlayer) {
            if(mFailMediaPlayer.isPlaying()){
                mFailMediaPlayer.stop();
            }

            mFailMediaPlayer.release();
            mFailMediaPlayer = null;
        }
    }
}
