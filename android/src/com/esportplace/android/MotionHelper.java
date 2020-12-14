package com.esportplace.android;

import android.view.View;
import android.view.MotionEvent;

public class MotionHelper implements View.OnTouchListener {

    SweepListener mSweepListener;
    float mStartX;
    float mStartY;
    float mEndX;
    float mEndY;

    public static final int SWEEP_LEN = 10;

    public static interface SweepListener {
        public void onSweep(View v);
    }

    public MotionHelper(SweepListener l) {
        mSweepListener = l;
    }

    public boolean onTouch(View v, MotionEvent e) {
        int action = e.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {
            mStartX = e.getX();
            mStartY = e.getY();
            Tracer.log("DOWN " + mStartX + " " + mStartY);
            return false;
        }
        else if (action == MotionEvent.ACTION_UP) {
            mEndX = e.getX();
            mEndY = e.getY();
            Tracer.log("UP " + mEndX + " " + mEndY);
            if (mEndX - mStartX > SWEEP_LEN) {
                if (mSweepListener != null)
                    mSweepListener.onSweep(v);
                return true;
            }
            return false;
        }
/*
        if (e.getAction() == MotionEvent.ACTION_MOVE)
        {
            long now = System.currentTimeMillis();
            if (now - mLastTime < mThrottle)
                return true;
            mLastTime = now;
            if (mSweepListener != null)
                mSweepListener.onSweep(v);
            return true;
        }
*/
        return true;
    }

}


