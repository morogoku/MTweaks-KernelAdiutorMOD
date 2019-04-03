/*

Created by Grouxho on 19/03/2019.

*/
package com.grx.soundcontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moro.mtweaks.database.Settings;
import com.moro.mtweaks.utils.AppSettings;
import com.moro.mtweaks.utils.kernel.sound.MoroSound;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.R;

import java.util.HashMap;

import static android.view.View.GONE;

public class GrxVolumeManager extends RecyclerViewItem {

    private Context mContext;
    private boolean mMainSwitchEnabled = true;
    
    private final static int HEADPHONES_LINKED_MODE = 1;
    private final static int HEADPHONES_INDEPENDENT_MODE = 0;
    private final static int HEADPHONES_ASYMETRIC_LINKED_MODE = 2;
    private final static int HEADPHONES_UNKNOWN_MODE = -1;

    private int mAccentColor;


    /* headphones */
    
    private int mLinkMode  = HEADPHONES_LINKED_MODE;
    private ImageView mLinkedButton, mUnlinkedButton, mAsymetricLinkedButton;
    private GrxVolumeItemController mVolumeLeftController, mVolumeRightController;
    private int mWheelStep = 6;
    private int mReferenceLevel = 113;
    private int mReferencePosition=12;
    private int mMinhHeadPhonesVolumeLevel;
    private LinearLayout mHeadPhonesContainer;


    /* earpiece and speaker */

    private LinearLayout mEarpieceSpeakerContainer;
    GrxVolumeItemController mEarPieceController, mSpeakerController;
    private int mEarPieceRefVal, mEarPieceStep, mEarPieceRefPosition, mEarPieceMin;
    private int mSpeakerRefVal, mSpeakerStep, mSpeakerRefPosition, mSpeakerMin;

    private void testSettings(){
        boolean enabled = false;
        final Settings settings = new Settings(mContext);
        final HashMap<String, Boolean> mCategoryEnabled = new HashMap<>();
        for (Settings.SettingsItem item : settings.getAllSettings()) {
            if (!mCategoryEnabled.containsKey(item.getCategory())) {
                boolean categoryEnabled = AppSettings.getBoolean(
                        item.getCategory(), false, mContext);
                mCategoryEnabled.put(item.getCategory(), categoryEnabled);
                if (!enabled && categoryEnabled) {
                    enabled = true;
                }
            }
        }

        boolean a = enabled;
    }

    private void setAccentColor(){
        TypedValue typedValue = new TypedValue();
        TypedArray b = mContext.obtainStyledAttributes(typedValue.data, new int[] { android.R.attr.colorAccent });
        mAccentColor = b.getColor(0, 0);
        b.recycle();
    }    

    @Override
    public int getLayoutRes() {
        return R.layout.grx_volume_controls;
    }

    private View.OnClickListener mModeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!mMainSwitchEnabled) return;
            mLinkMode = HEADPHONES_UNKNOWN_MODE;
            mUnlinkedButton.clearColorFilter();
            mLinkedButton.clearColorFilter();
            mAsymetricLinkedButton.clearColorFilter();
            int id = view.getId();
            switch (id){
                case R.id.unlinked:
                    setLinkMode(HEADPHONES_INDEPENDENT_MODE);
                    saveSpHeadPhonesLinkMode(HEADPHONES_INDEPENDENT_MODE);
                    break;

                case R.id.linked:
                    setLinkMode(HEADPHONES_LINKED_MODE );
                    saveSpHeadPhonesLinkMode(HEADPHONES_LINKED_MODE);
                    break;

                case R.id.linked_a:
                    setLinkMode(HEADPHONES_ASYMETRIC_LINKED_MODE);
                    saveSpHeadPhonesLinkMode(HEADPHONES_ASYMETRIC_LINKED_MODE);
                    break;
            }
        }
    };

    @Override
    public void onCreateView(View view) {
        
        mContext = view.getContext();
        setAccentColor();
        testSettings();


        /* HeadPhones */
        
        mLinkMode = getHeadPhonesLinkMode();
        mVolumeLeftController = view.findViewById(R.id.headphone_left);
        mVolumeRightController = view.findViewById(R.id.headphone_right);

        mWheelStep = mVolumeLeftController.getStepValue();
        mReferenceLevel = mVolumeLeftController.getReferenceValue();
        mReferencePosition = mVolumeLeftController.getRefValPosition();
        mLinkedButton = view.findViewById(R.id.linked);
        mUnlinkedButton = view.findViewById(R.id.unlinked);
        mAsymetricLinkedButton = view.findViewById(R.id.linked_a);

        mLinkedButton.setOnClickListener(mModeListener);
        mUnlinkedButton.setOnClickListener(mModeListener);
        mAsymetricLinkedButton.setOnClickListener(mModeListener);

        setUpHeadPhonesVolumes();

        mVolumeLeftController.setListener((progress, refval, refvalposition, step, dif)
                -> processOnVolumeChange(true,progress,refval,refvalposition,step,dif));

        mVolumeLeftController.getVolumeControlView().setOnChangingProgressListener((progress, dif)
                -> processOnVolumeChanging(true, progress, dif));

        mVolumeRightController.setListener((progress, refval, refvalposition, step, dif)
                -> processOnVolumeChange(false,progress,refval,refvalposition,step, dif));

        mVolumeRightController.getVolumeControlView().setOnChangingProgressListener((progress, dif)
                -> processOnVolumeChanging(false, progress, dif));

        mVolumeRightController.setProgress(mVolumeRightController.getVolumeControlView().getProgress());
        mVolumeLeftController.setProgress(mVolumeLeftController.getVolumeControlView().getProgress());
        setLinkMode(mLinkMode);

        mHeadPhonesContainer = view.findViewById(R.id.headphonecontainer);


        /* earpiece and speaker */

        mEarpieceSpeakerContainer = view.findViewById(R.id.earpiecespeakercontainer);

        mEarPieceController = view.findViewById(R.id.earpiece);
        mSpeakerController = view.findViewById(R.id.speaker);

        if(!MoroSound.hasSpeaker()) {
            LinearLayout container = view.findViewById(R.id.speaker_container);
            container.setVisibility(GONE);
        }else {
            mEarPieceRefVal = mEarPieceController.getReferenceValue();
            mEarPieceStep = mEarPieceController.getStepValue();
            mEarPieceRefPosition = mEarPieceController.getRefValPosition();
            mEarPieceMin = mEarPieceRefVal - (mEarPieceRefPosition*mEarPieceStep);


            /* set up wheel and db text */

            String kernelspeakervalue = MoroSound.getEarpiece();
            int kernelvalue;
            if(kernelspeakervalue == null || kernelspeakervalue.isEmpty()) kernelvalue=20; // default value
            else kernelvalue = Integer.valueOf(kernelspeakervalue);

            int wheelprogress = (kernelvalue - mEarPieceMin) / mEarPieceStep;

            mEarPieceController.getVolumeControlView().setProgress( wheelprogress);
            int reg_val = mEarPieceController.getValue(wheelprogress);
            mEarPieceController.setText(getHeadPhoneDbs(reg_val) + " dB");

            mEarPieceController.setListener((progress, refval, refvalposition, step, dif) -> {
                int dbs = mEarPieceMin + progress*mEarPieceStep;
                MoroSound.setEarpiece(String.valueOf(dbs),mContext);
                int reg_val1 = mEarPieceController.getValue(progress);
                mEarPieceController.setText(getHeadPhoneDbs(reg_val1) + " dB");
            });

            mEarPieceController.getVolumeControlView().setOnChangingProgressListener((progress, dif) -> {
                int dbs = mEarPieceMin + progress*mEarPieceStep;
                MoroSound.setEarpiece(String.valueOf(dbs),mContext);

                int reg_val12 = mEarPieceController.getValue(progress);
                mEarPieceController.setText(getHeadPhoneDbs(reg_val12) + " dB");
            });
        }

        if(!MoroSound.hasEarpiece()){
            LinearLayout container = view.findViewById(R.id.earpiece_container);
            container.setVisibility(GONE);
        }else {

            mSpeakerRefVal = mSpeakerController.getReferenceValue();
            mSpeakerStep = mSpeakerController.getStepValue();
            mSpeakerRefPosition = mSpeakerController.getRefValPosition();
            mSpeakerMin = mSpeakerRefVal - (mSpeakerRefPosition*mSpeakerStep);


            /* set up wheel and db text */

            String kernelspeakervalue = MoroSound.getSpeaker();
            int kernelvalue;
            if(kernelspeakervalue == null || kernelspeakervalue.isEmpty()) kernelvalue=20; // default value
            else kernelvalue = Integer.valueOf(kernelspeakervalue);

            mSpeakerController.getVolumeControlView().setProgress( (kernelvalue - mSpeakerMin) / mSpeakerStep );
            mSpeakerController.setText(kernelspeakervalue + " dB");

            mSpeakerController.setListener((progress, refval, refvalposition, step, dif) -> {
                int dbs = mSpeakerMin + progress*mSpeakerStep;
                MoroSound.setSpeaker(String.valueOf(dbs),mContext);
                mSpeakerController.setText(String.valueOf(dbs) + " dB");
            });

            mSpeakerController.getVolumeControlView().setOnChangingProgressListener((progress, dif) -> {
                int dbs = mSpeakerMin + progress*mSpeakerStep;
                MoroSound.setSpeaker(String.valueOf(dbs),mContext);
                mSpeakerController.setText(String.valueOf(dbs) + " dB");
            });
        }

        setMainSwitchEnabled(MoroSound.isSoundSwEnabled());
    }

    private void saveSpHeadPhonesLinkMode(int mode){
        Context context = mContext;
        AppSettings.saveInt("HEADPHONES_LINKED_MODE",mode,context);
    }

    private void setLinkMode(int mode){
        mLinkMode = mode;

        mUnlinkedButton.setColorFilter(mAccentColor&0x80ffffff);
        mLinkedButton.setColorFilter(mAccentColor&0x80ffffff);
        mAsymetricLinkedButton.setColorFilter(mAccentColor&0x80ffffff);
        switch (mLinkMode){
            case HEADPHONES_INDEPENDENT_MODE: mUnlinkedButton.setColorFilter(mAccentColor);
                finishAsymLinedMode();
                break;
            case HEADPHONES_LINKED_MODE: mLinkedButton.setColorFilter(mAccentColor);
                finishAsymLinedMode();
                initLinkedVolumeMode();
                break;
            case HEADPHONES_ASYMETRIC_LINKED_MODE: mAsymetricLinkedButton.setColorFilter(mAccentColor);
                initAsymLinedMode();
                break;
        }
    }

    private void initLinkedVolumeMode(){

        int progress = Math.min(mVolumeLeftController.getVolumeControlView().getProgress(),
                mVolumeRightController.getVolumeControlView().getProgress());
        mVolumeRightController.getVolumeControlView().setProgress(progress);
        mVolumeLeftController.getVolumeControlView().setProgress(progress);

        int reg_val = mVolumeLeftController.getValue(progress);
        mVolumeRightController.setText(getHeadPhoneDbs(reg_val) + " dB");
        mVolumeLeftController.setText(getHeadPhoneDbs(reg_val) + " dB");

        MoroSound.setHeadphone(String.valueOf(mMinhHeadPhonesVolumeLevel + mWheelStep*progress),mContext);
    }

    private void finishAsymLinedMode(){
        mVolumeLeftController.getVolumeControlView().resetProgressRange();
        mVolumeRightController.getVolumeControlView().resetProgressRange();
    }

    private void initAsymLinedMode(){
        int progressLeft = mVolumeLeftController.getVolumeControlView().getProgress();
        int progressRight = mVolumeRightController.getVolumeControlView().getProgress();

        int max_decr = Math.min(progressLeft-3, progressRight-3);
        int max_incr = Math.min(21- progressLeft, 21-progressRight);

        mVolumeLeftController.getVolumeControlView().setProgressRange(progressLeft-max_decr, progressLeft+max_incr);
        mVolumeRightController.getVolumeControlView().setProgressRange(progressRight-max_decr, progressRight+max_incr);

        MoroSound.setHeadphoneL(String.valueOf(mMinhHeadPhonesVolumeLevel + mWheelStep*progressLeft), mContext);
        MoroSound.setHeadphoneR(String.valueOf(mMinhHeadPhonesVolumeLevel + mWheelStep*progressRight), mContext);
    }

    private void processOnVolumeChange(boolean isLeft, int progress, int refval, int refvalPosition, int step, int dif){

        switch (mLinkMode){
            case HEADPHONES_INDEPENDENT_MODE:
                 break;
            case HEADPHONES_LINKED_MODE:
                if(isLeft) {
                    int reg_val = mVolumeLeftController.getValue(progress);
                    mVolumeRightController.getVolumeControlView().setProgress(progress);
                    mVolumeRightController.setText(getHeadPhoneDbs(reg_val) + " dB");
                }else {
                    int reg_val = mVolumeLeftController.getValue(progress);
                    mVolumeLeftController.setText(getHeadPhoneDbs(reg_val) + " dB");
                    mVolumeLeftController.getVolumeControlView().setProgress(progress);
                }
                break;
            case HEADPHONES_ASYMETRIC_LINKED_MODE:
                break;
        }
    }

    private String getHeadPhoneDbs(int register_value) {
        // according to the doc of the chip -> dbs = -64.0 + (register_value) * 0.5 where register_value could go from 0 to 191

        float dbs = -64f +  ((float) register_value * 0.5f);
        return String.valueOf(dbs);
    }

    private void setUpHeadPhonesVolumes(){
        /* init wheels positions levels */

        mMinhHeadPhonesVolumeLevel = mReferenceLevel - (mReferencePosition*mWheelStep);

        String kernelLeftLevel = MoroSound.getHeadphoneL();
        int wheelprogress = getWheelProgressFromKernelReading(kernelLeftLevel);
        int reg_val = mVolumeLeftController.getValue(wheelprogress);
        mVolumeLeftController.setText(getHeadPhoneDbs(reg_val) + " dB");
        mVolumeLeftController.getVolumeControlView().setProgress(wheelprogress);

        if(mLinkMode == HEADPHONES_LINKED_MODE) {
            mVolumeRightController.setText(getHeadPhoneDbs(reg_val) + " dB");
            mVolumeRightController.getVolumeControlView().setProgress(wheelprogress);
        }else {
            wheelprogress = getWheelProgressFromKernelReading(MoroSound.getHeadphoneR());
            int regval = mVolumeRightController.getValue(wheelprogress);
            mVolumeRightController.setText(getHeadPhoneDbs(regval) + " dB");
            mVolumeRightController.getVolumeControlView().setProgress(wheelprogress);
        }
    }

    private void processOnVolumeChanging(boolean isleft, int progress, int dif){
        switch (mLinkMode){
            case HEADPHONES_INDEPENDENT_MODE: //independent
                if(isleft) {
                    int reg_val = mVolumeLeftController.getValue(progress);
                    String dbs = getHeadPhoneDbs(reg_val) + " dB";
                    mVolumeLeftController.setText(dbs);
                    MoroSound.setHeadphoneL(String.valueOf(progress*mWheelStep + mMinhHeadPhonesVolumeLevel), mContext);
                }
                else {
                    int reg_val = mVolumeRightController.getValue(progress);
                    String dbs = getHeadPhoneDbs(reg_val) + " dB";
                    mVolumeRightController.setText(dbs);
                    MoroSound.setHeadphoneR(String.valueOf(progress*mWheelStep + mMinhHeadPhonesVolumeLevel), mContext);
                }
                break;
            case HEADPHONES_LINKED_MODE: // linked
                int reg_val = mVolumeLeftController.getValue(progress);
                String dbs = getHeadPhoneDbs(reg_val) + " dB";
                mVolumeLeftController.setText(dbs);
                mVolumeRightController.setText(dbs);
                MoroSound.setHeadphone(String.valueOf(progress*mWheelStep + mMinhHeadPhonesVolumeLevel), mContext);
                if(isleft) {
                    mVolumeRightController.getVolumeControlView().setProgress(progress);
                }else {
                    mVolumeLeftController.getVolumeControlView().setProgress(progress);
                }
                break;
            case HEADPHONES_ASYMETRIC_LINKED_MODE: // linked asymetric
                if(isleft){
                    mVolumeLeftController.setText(getHeadPhoneDbs(mVolumeLeftController.getValue(progress)) + " dB");
                    String left = String.valueOf(mMinhHeadPhonesVolumeLevel + progress*mWheelStep);
                    int progr = mVolumeRightController.getVolumeControlView().increaseProgress(dif);
                    mVolumeRightController.setText(getHeadPhoneDbs(mVolumeRightController.getValue(progr)) + " dB");
                    String right = String.valueOf(mMinhHeadPhonesVolumeLevel + mVolumeRightController.getVolumeControlView().getProgress()*mWheelStep);
                    MoroSound.setHeadPhoneValues(left,right,mContext);
                }else {
                    mVolumeRightController.setText(getHeadPhoneDbs(mVolumeRightController.getValue(progress)) + " dB");
                    String right = String.valueOf(mMinhHeadPhonesVolumeLevel + progress*mWheelStep);
                    String left = String.valueOf(mMinhHeadPhonesVolumeLevel + mVolumeLeftController.getVolumeControlView().getProgress()*mWheelStep);
                    int progr = mVolumeLeftController.getVolumeControlView().increaseProgress(dif);
                    mVolumeLeftController.setText(getHeadPhoneDbs(mVolumeLeftController.getValue(progr)) + " dB");
                    MoroSound.setHeadPhoneValues(left,right,mContext);
                }
                break;
        }
    }
    
    private int getWheelProgressFromKernelReading(String kernel_value){
        int kv;
        if(kernel_value == null || kernel_value.isEmpty()) kv = mReferenceLevel;
        else kv = Integer.valueOf(kernel_value);
        if(kv == 0 ) kv = mReferenceLevel;

        int progress  = mReferencePosition + ( (kv - mReferenceLevel) ) / mWheelStep;
        return progress;
    }

    private int getHeadPhonesLinkMode(){
        // 0 -> independent, 1 - Linked, 2-> asymetric link

        return AppSettings.getInt("HEADPHONES_LINKED_MODE",HEADPHONES_LINKED_MODE, mContext);
    }
    
    public void setMainSwitchEnabled(boolean enabled){
        mMainSwitchEnabled = enabled;
        if(!enabled) {
            mLinkMode = HEADPHONES_LINKED_MODE; 
            setLinkMode(HEADPHONES_LINKED_MODE);
        }

        if(mVolumeLeftController != null) mVolumeLeftController.getVolumeControlView().enableView(enabled);
        if(mVolumeRightController != null) mVolumeRightController.getVolumeControlView().enableView(enabled);
        mHeadPhonesContainer.setAlpha(enabled ? 1.0f : 0.5f);
        mEarpieceSpeakerContainer.setAlpha(enabled ? 1.0f : 0.5f);
        if(mEarPieceController != null) mEarPieceController.getVolumeControlView().enableView(enabled);
        if(mSpeakerController != null) mSpeakerController.getVolumeControlView().enableView(enabled);

        setUpHeadPhonesVolumes();
    }
}
