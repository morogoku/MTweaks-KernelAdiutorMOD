/*
 * Grouxho - www.espdroids.com - Dec 2018
 *
 * This class is based in the AnalogController.java class ( https://github.com/harjot-oberai/MusicDNA/blob/master/app/src/main/java/com/sdsmdg/harjot/MusicDNA/customviews/AnalogController.java )
 * created by harjot-oberai  ( https://github.com/harjot-oberai )
 *
 * The original class was written under CC BY-NC-SA license (https://creativecommons.org/licenses/by-nc-sa/2.0/ )
 *
 */
package com.grx.soundcontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class GrxVolumeControlView extends View {

    float midx, midy;
    Paint textPaint, circlePaint, circlePaint2, linePaint;
    String angle;
    float currdeg, deg = 3, downdeg;

    int progressColor, lineColor;

    onProgressChangedListener mListener;

    String label;

    boolean mIsEnabled=true;


    public interface onProgressChangedListener {
        void onProgressChanged(int progress, int increment);
    }

    private ProgressChangingListener mProgressChangingListener=null;

    public interface ProgressChangingListener{
        void onProgressChanging(int progress, int increment);
    }

    public void setOnProgressChangedListener(onProgressChangedListener listener) {
        mListener = listener;
    }

    public GrxVolumeControlView(Context context) {
        super(context);
        init();
    }

    public GrxVolumeControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GrxVolumeControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int mGrxAccentColor;

    void init() {

        TypedValue typedValue = new TypedValue();
        TypedArray b = getContext().obtainStyledAttributes(typedValue.data, new int[] { android.R.attr.colorAccent });
        int mGrxAccentColor = b.getColor(0, 0);
        b.recycle();

        textPaint = new Paint();
        textPaint.setColor(mGrxAccentColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(24);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        circlePaint = new Paint();
        circlePaint.setColor(mGrxAccentColor & 0x30ffffff);
        circlePaint.setStyle(Paint.Style.FILL);

        circlePaint2 = new Paint();
        circlePaint2.setColor(mGrxAccentColor);
        circlePaint2.setStyle(Paint.Style.FILL);

        linePaint = new Paint();
        linePaint.setColor(mGrxAccentColor);
        linePaint.setStrokeWidth(7);

        angle = "0.0";
        label = "";  // not used


        mArcPaint = new Paint();
        mArcPaint.setColor(mGrxAccentColor);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(3);
    }


    public RectF mGrxArch = new RectF();
    private Paint mArcPaint;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        midx = canvas.getWidth() / 2;
        midy = canvas.getHeight() / 2;

        int ang = 0;
        float x = 0, y = 0;
        int radius = (int) (Math.min(midx, midy) * ((float) 14.5 / 16));
        int archradius = (int) (Math.min(midx, midy) * ((float) 11 / 15));
        float deg2 = Math.max(3, deg);
        float deg3 = Math.min(deg, 21);


        for (int i = (int) (deg2); i < 22; i++) {
            float tmp = (float) i / 24;
            x = midx + (float) (radius * Math.sin(2 * Math.PI * (1.0 - tmp)));
            y = midy + (float) (radius * Math.cos(2 * Math.PI * (1.0 - tmp)));
            //         circlePaint.setColor(Color.parseColor("#111111"));
            canvas.drawCircle(x, y, ((float) radius / 15), circlePaint);
        }


        mGrxArch.set((int) (midx-archradius), (int) (midy-archradius), (int)(midx+archradius),(int)(midy+archradius));
        //   canvas.drawArc(mGrxArch,135f,270f,false,circlePaint);

        for (int i = 3; i <= deg3; i++) {  // little circle indicators
            float tmp = (float) i / 24;
            x = midx + (float) (radius * Math.sin(2 * Math.PI * (1.0 - tmp)));
            y = midy + (float) (radius * Math.cos(2 * Math.PI * (1.0 - tmp)));
            canvas.drawCircle(x, y, ((float) radius / 15), circlePaint2);
        }

        float tmp2 = deg / 24;
        float x1 = midx + (float) (radius * ((float) 2 / 5) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
        float y1 = midy + (float) (radius * ((float) 2 / 5) * Math.cos(2 * Math.PI * (1.0 - tmp2)));
        float x2 = midx + (float) (radius * ((float) 3 / 5) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
        float y2 = midy + (float) (radius * ((float) 3 / 5) * Math.cos(2 * Math.PI * (1.0 - tmp2)));


        // canvas.drawCircle(midx, midy, radius * ((float) 13 / 15), circlePaint);

        canvas.drawCircle(midx, midy, radius /4, circlePaint);
        canvas.drawCircle(midx, midy, radius /8, circlePaint2);
        //   canvas.drawText(label, midx, midy + (float) (radius * 1.2), textPaint);
        canvas.drawLine(x1, y1, x2, y2, linePaint);


        canvas.drawArc(mGrxArch, 135f, 270f, false, mArcPaint);
    }


    int mGrxCurrentStep = 3;

    int mGrxLastProgress = 3;


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(!mIsEnabled) return true;

        this.getParent().requestDisallowInterceptTouchEvent(true);

        if(mGrxLastProgress!=deg) {
            int dif = (int) deg - mGrxLastProgress;
            if(mProgressChangingListener!= null) mProgressChangingListener.onProgressChanging((int) deg, dif);
            mGrxLastProgress=(int) deg;
        }


        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            float dx = e.getX() - midx;
            float dy = e.getY() - midy;
            downdeg = (float) ((Math.atan2(dy, dx) * 180) / Math.PI);
            downdeg -= 90;
            if (downdeg < 0) {
                downdeg += 360;
            }
            downdeg = (float) Math.floor(downdeg / 15);
            //   this.getParent().requestDisallowInterceptTouchEvent(false);
            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = e.getX() - midx;
            float dy = e.getY() - midy;
            currdeg = (float) ((Math.atan2(dy, dx) * 180) / Math.PI);
            currdeg -= 90;
            if (currdeg < 0) {
                currdeg += 360;
            }
            currdeg = (float) Math.floor(currdeg / 15);

            if (currdeg == 0 && downdeg == 23) {
                deg++;
                if (deg > 21) {
                    deg = 21;
                }
                downdeg = currdeg;
            } else if (currdeg == 23 && downdeg == 0) {
                deg--;
                if (deg < 3) {
                    deg = 3;
                }
                downdeg = currdeg;
            } else {
                deg += (currdeg - downdeg);
                if (deg > 21) {
                    deg = 21;
                }
                if (deg < 3) {
                    deg = 3;
                }
                downdeg = currdeg;
            }


            if(deg<mMinProgress) deg=mMinProgress;
            if(deg>mMaxProgress) deg=mMaxProgress;


            angle = String.valueOf(String.valueOf(deg));

            //   this.getParent().requestDisallowInterceptTouchEvent(false);
            invalidate();
            return true;
        }else { /* GRX */
            if(e.getAction()==MotionEvent.ACTION_UP){  // to avoid big annnoying sound level we only call back when action is up and different step
                if(mGrxCurrentStep!=deg){
                    int dif =(int) deg-mGrxCurrentStep;
                    mGrxCurrentStep = (int) deg;
                    Log.d("Grxdeg ", String.valueOf(deg));
                    if(mListener!=null) mListener.onProgressChanged(mGrxCurrentStep, dif);
                    return true;
                }
            }
        }

        return true;
    }

    public int getProgress() {
        //return (int) (deg - 2); // ????? grx - this was not right, check further on.
        return (int) deg;
    }

    public void setProgress(int param) {

        // deg = param + 2;  ???
        deg = param;
        mGrxCurrentStep=(int) deg;
        mGrxLastProgress =(int) deg;
        invalidate();
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String txt) {
        label = txt;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }


    public void setOnChangingProgressListener( ProgressChangingListener listener){
        mProgressChangingListener=listener;
    }

    private int mMinProgress=3;  // fixed - grx To do : configurable number of positions.
    private int mMaxProgress=21; //

    public void setProgressRange(int minval, int nmaxval){
        mMinProgress=minval;
        mMaxProgress=nmaxval;
    }

    public void resetProgressRange(){
        mMinProgress=3;
        mMaxProgress=21;
        invalidate();
    }

    public int increaseProgress(int incr){
        deg+=incr;
        mGrxLastProgress=(int) deg;
        mGrxCurrentStep=(int) deg;
        invalidate();
        return (int) deg;
    }

    public void enableView(boolean enable){
        mIsEnabled = enable;
    }

}
