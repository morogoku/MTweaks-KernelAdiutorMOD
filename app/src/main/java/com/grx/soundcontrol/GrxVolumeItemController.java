/*
 *
 *    Grouxho - www.espdroids.com - Jan 2019
 *
 *    This class extends a LinearLayout and inflates inside it a grx_volume_item layout. This layout contains:
 *       - a textview to write the user volume value,
 *       - a GrxVolumeControlView to allow the user to change the specific volume the view is related to,
 *       - another textview with the label of the device or item controlled.
 *
 *    The following attrs were added to this class in order to configure the behavior of the volume item:
 *
 *       - app:label -> The text to appear in the textview under the wheel controller.
 *
 *       The wheel (GrxVolumeControlView) has been designed (for now) with 19 postions. We can calibrate the stock behavior through the following attrs:
 *
 *       - app:refVal  : reference real value. Using this refval we can determine the value of the sound level on each position of the wheel.
 *       - app:refValposition : The wheel position asigned to the refVal.
 *       - app:valStep : How much you want the real value to change per wheel position (remember that we only have for now 19 positions, so you will need to calculate for each volume control the best
 *       valStep value)
 *
 *       - app:wheelsize : Size, in dpi of the wheel.
 *
 *
 */
package com.grx.soundcontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moro.mtweaks.R;


public class GrxVolumeItemController extends LinearLayout implements GrxVolumeControlView.onProgressChangedListener{


    final public static int DEF_DEFREFVAL = 128;
    final public static int DEF_DEFVALSTEP = 3;
    final public static int DEF_DEFREFVALPOSITION = 10;

    public GrxVolumeControlView mVolumeView;
    private String mLabel="";
    TextView  mValueTextView;

    /* wheel configuration variables */

    private int mRefVal;
    private int mValStep;
    private int mRefValPosition;
    private int mWheelSize;

    public GrxVolumeItemController(Context context) {
        this(context, null, 0);
    }

    public GrxVolumeItemController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GrxVolumeItemController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr);
    }

    private void initView(AttributeSet attrs, int defStyleAttr){

        if (attrs != null) {

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.VolumeItemController, defStyleAttr, 0);

            String label  = a.getString(R.styleable.VolumeItemController_label);
            if(label!=null) mLabel = label;

            mRefValPosition=a.getInt(R.styleable.VolumeItemController_refValposition,DEF_DEFREFVALPOSITION);
            mRefVal=a.getInt(R.styleable.VolumeItemController_refVal,DEF_DEFREFVAL);
            mValStep=a.getInt(R.styleable.VolumeItemController_valStep,DEF_DEFVALSTEP);
            mWheelSize=a.getDimensionPixelSize(R.styleable.VolumeItemController_wheelsize,130);
            a.recycle();
        }
        inflate(getContext(),R.layout.grx_volume_item,this);
    }


    public void configureVolumeControl( int refval, int refvalposition, int valstep){
        mRefValPosition = refvalposition;
        mRefVal = refval;
        mValStep = valstep;
        invalidate();
    }

    public void setControlSize (int size) {

        mWheelSize = Math.round(size*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));

        mVolumeView = findViewById(R.id.volumeview);
        if(mVolumeView!=null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mWheelSize,mWheelSize);
            mVolumeView.setLayoutParams(params);
        }else Log.d("GrxVolumeItemControler", " cannot assing wheel size, wheel view is null" );
    }


    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();

        mVolumeView = findViewById(R.id.volumeview);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mWheelSize,mWheelSize);
        mVolumeView.setLayoutParams(params);

        TextView textView = findViewById(R.id.label);
        textView.setText(mLabel);
        mValueTextView=findViewById(R.id.value);

        mVolumeView.setOnProgressChangedListener(this);
    }


    VolumeItemListener mListener=null;

    public interface VolumeItemListener{
        void onProgressChanged(int progress, int refval, int refvalposition, int step, int dif);
    }

    public void setListener(VolumeItemListener listener){
        mListener=listener;
    }


    public void setText(String text){
        if(mValueTextView!=null) mValueTextView.setText(text);
    }

    public void setProgressText(int progress){
        int change = progress- mRefValPosition;
        int val = mRefVal + change*mValStep;
        Log.d("Grxdeg received", String.valueOf(val));
    }

    @Override
    public void onProgressChanged(int progress, int dif) {

        setProgressText(progress);
        if(mListener!=null) mListener.onProgressChanged(progress,mRefVal, mRefValPosition, mValStep, dif);
    }

    public void setProgress(int progress) {
        setProgressText(progress);
    }

    public GrxVolumeControlView getVolumeControlView(){
        return mVolumeView;
    }

    public int getValue(int progress){  // return real value for a given progress position
        return mRefVal + (progress - mRefValPosition) * mValStep;
    }

    public int getStepValue(){
        return mValStep;
    }

    public int getRefValPosition(){
        return mRefValPosition;
    }

    public int getReferenceValue(){
        return mRefVal;
    }


}

