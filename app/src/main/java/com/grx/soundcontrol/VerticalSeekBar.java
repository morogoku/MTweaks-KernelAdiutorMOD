/*
 *    Grouxho (Grx)
 *
 *   This file is based  in the class VerticalSeekBar.java from Haruki Hasegawa  - https://github.com/h6ah4i/android-verticalseekbar
 *
 *   - Features added by Grx:
 *
 *           - Zero Offset
 *           - Progress line drawn from zero offset
 *           - Dividers
 *           - Show or hide Background track
 *
 *
 *
 *     To do so, some code from aosp sources has been taken from aosp frameworks
 *
 *
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 * This file contains AOSP code copied from /frameworks/base/core/java/android/widget/AbsSeekBar.java
 *
 *
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.grx.soundcontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ProgressBar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.view.ViewCompat;
import com.moro.mtweaks.R;


public class VerticalSeekBar extends AppCompatSeekBar {
    public static final int ROTATION_ANGLE_CW_90 = 90;
    public static final int ROTATION_ANGLE_CW_270 = 270;

    private boolean mIsDragging;
    private Drawable mThumb_;
    private Method mMethodSetProgressFromUser;
    private int mRotationAngle = ROTATION_ANGLE_CW_90;

    public VerticalSeekBar(Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle, 0);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        ViewCompat.setLayoutDirection(this, ViewCompat.LAYOUT_DIRECTION_LTR);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar, defStyleAttr, defStyleRes);
            final int rotationAngle = a.getInteger(R.styleable.VerticalSeekBar_seekBarRotation, 0);
            if (isValidRotationAngle(rotationAngle)) {
                mRotationAngle = rotationAngle;
            }

            grxSetUPAdditionalOptions(a); // grx

            a.recycle();
        }
    }

    @Override
    public void setThumb(Drawable thumb) {
        mThumb_ = thumb;
        super.setThumb(thumb);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (useViewRotation()) {
            return onTouchEventUseViewRotation(event);
        } else {
            return onTouchEventTraditionalRotation(event);
        }
    }

    private boolean onTouchEventTraditionalRotation(MotionEvent event) {

        if(!mIsEnabled) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                onStartTrackingTouch();
                trackTouchEvent(event);
                attemptClaimDrag(true);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if (mIsDragging) {
                    trackTouchEvent(event);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mIsDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    setPressed(false);
                } else {
                    // Touch up when we never crossed the touch slop threshold
                    // should
                    // be interpreted as a tap-seek to that location.
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    attemptClaimDrag(false);
                }
                // ProgressBar doesn't know to repaint the thumb drawable
                // in its inactive state when the touch stops (because the
                // value has not apparently changed)
                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (mIsDragging) {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                invalidate(); // see above explanation
                break;
        }
        return true;
    }

    private boolean onTouchEventUseViewRotation(MotionEvent event) {
        if(!mIsEnabled) return false;

        boolean handled = super.onTouchEvent(event);
        if (handled) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    attemptClaimDrag(true);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    attemptClaimDrag(false);
                    break;
            }
        }

        return handled;
    }

    private void trackTouchEvent(MotionEvent event) {
        final int paddingLeft = super.getPaddingLeft();
        final int paddingRight = super.getPaddingRight();
        final int height = getHeight();

        final int available = height - paddingLeft - paddingRight;
        int y = (int) event.getY();

        final float scale;
        float value = 0;

        switch (mRotationAngle) {
            case ROTATION_ANGLE_CW_90:
                value = y - paddingLeft;
                break;
            case ROTATION_ANGLE_CW_270:
                value = (height - paddingLeft) - y;
                break;
        }

        if (value < 0 || available == 0) {
            scale = 0.0f;
        } else if (value > available) {
            scale = 1.0f;
        } else {
            scale = value / (float) available;
        }

        final int max = getMax();
        final float progress = scale * max;

        _setProgressFromUser((int) progress, true);
    }

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     */
    private void attemptClaimDrag(boolean active) {
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(active);
        }
    }

    /**
     * This is called when the user has started touching this widget.
     */
    private void onStartTrackingTouch() {
        mIsDragging = true;
    }

    /**
     * This is called when the user either releases his touch or the touch is
     * canceled.
     */
    private void onStopTrackingTouch() {
        mIsDragging = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isEnabled()) {
            final boolean handled;
            int direction = 0;

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    direction = (mRotationAngle == ROTATION_ANGLE_CW_90) ? 1 : -1;
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    direction = (mRotationAngle == ROTATION_ANGLE_CW_270) ? 1 : -1;
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    // move view focus to previous/next view
                    return false;
                default:
                    handled = false;
                    break;
            }

            if (handled) {
                final int keyProgressIncrement = getKeyProgressIncrement();
                int progress = getProgress();

                progress += (direction * keyProgressIncrement);

                if (progress >= 0 && progress <= getMax()) {
                    _setProgressFromUser(progress, true);
                }

                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        if (!useViewRotation()) {
            refreshThumb();
        }
    }

    private synchronized void _setProgressFromUser(int progress, boolean fromUser) {
        if (mMethodSetProgressFromUser == null) {
            try {
                Method m;
                m = ProgressBar.class.getDeclaredMethod("setProgress", int.class, boolean.class);
                m.setAccessible(true);
                mMethodSetProgressFromUser = m;
            } catch (NoSuchMethodException e) {
            }
        }

        if (mMethodSetProgressFromUser != null) {
            try {
                mMethodSetProgressFromUser.invoke(this, progress, fromUser);
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        } else {
            super.setProgress(progress);
        }
        refreshThumb();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (useViewRotation()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(heightMeasureSpec, widthMeasureSpec);

            final ViewGroup.LayoutParams lp = getLayoutParams();

            if (isInEditMode() && (lp != null) && (lp.height >= 0)) {
                setMeasuredDimension(super.getMeasuredHeight(), lp.height);
            } else {
                setMeasuredDimension(super.getMeasuredHeight(), super.getMeasuredWidth());
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (useViewRotation()) {
            super.onSizeChanged(w, h, oldw, oldh);
        } else {
            super.onSizeChanged(h, w, oldh, oldw);
        }
    }


    /** grx **/

    Paint mGrxProgressPaint;

    public int mGrxZeroOffset;
    public int mGrxDividerStep;
    private int mGrxNumSteps;
    private int mGrxLeftMin=0;
    private float mGrxHalfDividerLength;
    private float mGrxHalfDividerThickness;
    private float mGrxHalfProgressThickness;
    private boolean mGrxShowBackgroundTrack = true;
    private float mGrxZeroOffsetCircleRadius;
    private int mGrxMin;
    Paint mGrxDividerPaint;


    private void grxSetUPAdditionalOptions(TypedArray a){

        mGrxZeroOffset = a.getInt(R.styleable.VerticalSeekBar_zeroOffset,0);
        mGrxDividerStep = a.getInt(R.styleable.VerticalSeekBar_dividerStep,0);
        mGrxHalfDividerLength = a.getDimension(R.styleable.VerticalSeekBar_dividerLength,0f)/2;
        mGrxHalfDividerThickness = a.getDimensionPixelSize(R.styleable.VerticalSeekBar_dividerThickness,0)/2;
        mGrxHalfProgressThickness = a.getDimensionPixelSize(R.styleable.VerticalSeekBar_progressThickness,0)/2;
        mGrxShowBackgroundTrack = a.getBoolean(R.styleable.VerticalSeekBar_showBackgroundTrack,true);
        mGrxZeroOffsetCircleRadius = a.getDimensionPixelSize(R.styleable.VerticalSeekBar_zeroOffsetCircleRadius,0);
        mGrxMin = a.getInt(R.styleable.VerticalSeekBar_min,0);

        mGrxNumSteps = getMax() - mGrxMin;
        mGrxProgressPaint = new Paint();
        mGrxProgressPaint.setStyle(Paint.Style.FILL);

        TypedValue typedValue = new TypedValue();
        TypedArray b = getContext().obtainStyledAttributes(typedValue.data, new int[] { android.R.attr.colorAccent });
        int color = b.getColor(0, 0);
        b.recycle();

        mGrxProgressPaint.setColor(color);

        Drawable currentprogressdrawable = getProgressDrawable();
        Drawable track = null;
        mGrxShowBackgroundTrack=false;//bbb
        if(currentprogressdrawable!= null && currentprogressdrawable instanceof LayerDrawable) {
            if (!mGrxShowBackgroundTrack){
                int indexbg = ((LayerDrawable) currentprogressdrawable).findIndexByLayerId(android.R.id.background);
                ((LayerDrawable) currentprogressdrawable).setDrawable(indexbg, new ColorDrawable(0));
            }

           int indexprogress = ((LayerDrawable) currentprogressdrawable).findIndexByLayerId(android.R.id.progress);
           ((LayerDrawable) currentprogressdrawable).setDrawable(indexprogress, new ColorDrawable(0));

        }

        mGrxDividerPaint = new Paint();
        mGrxDividerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mGrxDividerPaint.setColor(color);
        mGrxDividerPaint.setStrokeWidth(2.0f);

        mTickMark = new ShapeDrawable(new OvalShape());
        mTickMark.setIntrinsicHeight(10);
        mTickMark.setIntrinsicWidth(10);
        mTickMark.getPaint().setColor(color);
        mTickMark.setBounds(new Rect(-2, -2, 2, 2));

        mTickMarkSoft = new ShapeDrawable(new OvalShape());
        mTickMarkSoft.setIntrinsicHeight(10);
        mTickMarkSoft.setIntrinsicWidth(10);
        mTickMarkSoft.getPaint().setColor(color&0x10ffffff);
        mTickMarkSoft.setBounds(new Rect(-4, -4, 4, 4));
    }


    private boolean mGrxMeasuresSet=false;
    private float mGrxPixelsPerStep=0;
    private float mGrxLeftZeroOffset = 0f;
    private int mGrxCenterThickness =0;


    private void drawTickMarks(Canvas canvas) {
        if (mTickMark != null) {
            if (mGrxNumSteps> 1) {
                final int saveCount = canvas.save();
                canvas.translate(getPaddingLeft(), getHeight() / 2);
                for (int i = 0; i <= mGrxNumSteps; i++) {
                    if((i%mGrxDividerStep)==0) mTickMark.draw(canvas);
                    else mTickMarkSoft.draw(canvas); // hacer opción
                    canvas.translate(mGrxPixelsPerStep, 0);
                }
                canvas.restoreToCount(saveCount);
            }
        }
    }

    ShapeDrawable mTickMark, mTickMarkSoft;

    private void grxSetDimensions(Canvas canvas){

        if(canvas==null) return;

        mGrxPixelsPerStep = (getWidth() - getPaddingLeft() - getPaddingRight()) / (float) mGrxNumSteps;
        mGrxLeftZeroOffset = getPaddingRight() +  (int) ( (float) mGrxZeroOffset*mGrxPixelsPerStep );

        mGrxCenterThickness = canvas.getClipBounds().centerY();
        mGrxMeasuresSet = true;
    }

    public boolean mGrxIsInit = false;

    public void grxSetSeekBarProgress(String value) {
        if(value == null || value.isEmpty()) setProgress(mGrxZeroOffset);
        else setProgress(Integer.valueOf(value) + mGrxZeroOffset );
    }

    public void grxSetInitialized(boolean initialized){
        mGrxIsInit = initialized;
    }

    public void grxSetCurrentKernelValue(int band){

//        grxSetSeekBarProgress(ArizonaSound.getEqValue(band));

    }

   public int grxGetNormalizedProgress(){
        if(mGrxIsInit) return getProgress()-mGrxZeroOffset;
        else return 0;
    }

    public String grxGetNormalizedStringProgress(){
        if(mGrxIsInit) return String.valueOf(getProgress()-mGrxZeroOffset);
        else return null;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (!useViewRotation()) {
            switch (mRotationAngle) {
                case ROTATION_ANGLE_CW_90:
                    canvas.rotate(90);
                    canvas.translate(0, -super.getWidth());
                    break;
                case ROTATION_ANGLE_CW_270:
                    canvas.rotate(-90);
                    canvas.translate(-super.getHeight(), 0);
                    break;
            }
        }


        /* grx code */

        if (!mGrxMeasuresSet) grxSetDimensions(canvas);
        drawTickMarks(canvas);

        int totallines = mGrxNumSteps + 1;

        if(mGrxMeasuresSet) {
            int stepstodraw = getProgress() - mGrxZeroOffset;
            if(stepstodraw!=0)

                canvas.drawRect(mGrxLeftZeroOffset,
                        mGrxCenterThickness-mGrxHalfProgressThickness,
                        mGrxLeftZeroOffset + mGrxPixelsPerStep*stepstodraw,
                        mGrxCenterThickness+mGrxHalfProgressThickness,
                        mGrxProgressPaint);

            if(mGrxZeroOffsetCircleRadius!=0){
                canvas.drawCircle(mGrxLeftZeroOffset,mGrxCenterThickness,mGrxZeroOffsetCircleRadius,mGrxProgressPaint );
            }

            super.onDraw(canvas);
        }

        super.onDraw(canvas);
    }

    public int getRotationAngle() {
        return mRotationAngle;
    }

    public void setRotationAngle(int angle) {
        if (!isValidRotationAngle(angle)) {
            throw new IllegalArgumentException("Invalid angle specified :" + angle);
        }

        if (mRotationAngle == angle) {
            return;
        }

        mRotationAngle = angle;

        if (useViewRotation()) {
            VerticalSeekBarWrapper wrapper = getWrapper();
            if (wrapper != null) {
                wrapper.applyViewRotation();
            }
        } else {
            requestLayout();
        }
    }


    // refresh thumb position
    private void refreshThumb() {
        onSizeChanged(super.getWidth(), super.getHeight(), 0, 0);
    }

    /*package*/
    boolean useViewRotation() {
        return !isInEditMode();
    }

    private VerticalSeekBarWrapper getWrapper() {
        final ViewParent parent = getParent();

        if (parent instanceof VerticalSeekBarWrapper) {
            return (VerticalSeekBarWrapper) parent;
        } else {
            return null;
        }
    }

    private static boolean isValidRotationAngle(int angle) {
        return (angle == ROTATION_ANGLE_CW_90 || angle == ROTATION_ANGLE_CW_270);
    }

    public boolean mIsEnabled = true;

    public void grxSetEnabled(boolean enable){
        mIsEnabled = enable;
    }


}
