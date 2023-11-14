package com.example.recorder.widget.audiodialog;

import android.os.Handler;

public class AudioRecoderUtils {

    private long startTime;
    private long endTime;
    private int SPACE = 100;// Interval sampling time

    private OnAudioStatusUpdateListener audioStatusUpdateListener;

    public interface OnAudioStatusUpdateListener {
        public void onUpdate(double db);
    }

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    private final Handler mHandler = new Handler();

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private void updateMicStatus() {
        double ratio = (10.0 * Math.random()) + 2;
        double db = 0;// decibel
        if (ratio > 1) {
            db = 100 * Math.log10(ratio);
            if (null != audioStatusUpdateListener) {
                audioStatusUpdateListener.onUpdate(db);
            }
        }
        mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);

    }

    public void startRecord() {
        updateMicStatus();
    }

    public void stopRecord() {
        mHandler.removeCallbacks(mUpdateMicStatusTimer);
    }

}
