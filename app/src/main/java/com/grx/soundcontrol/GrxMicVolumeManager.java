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

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;

import com.moro.mtweaks.R;
import com.moro.mtweaks.database.Settings;
import com.moro.mtweaks.utils.AppSettings;
import com.moro.mtweaks.utils.kernel.sound.MoroSound;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;

import java.util.HashMap;

import static android.view.View.GONE;

public class GrxMicVolumeManager extends RecyclerViewItem {

    private Context mContext;
    private boolean mMainSwitchEnabled = true;
    
    private int mAccentColor;

    private LinearLayout mSwitchContainer;
    private LinearLayout mMicUpDownContainer;
    private LinearLayout mMicHeadphoneContainer;

    GrxVolumeItemController mMicDownController, mMicUpController, mMicHeadphoneController;

    private int mMicDownRefVal, mMicDownStep, mMicDownRefPosition, mMicDownMin;
    private int mMicUpRefVal, mMicUpStep, mMicUpRefPosition, mMicUpMin;
    private int mMicHeadphoneRefVal, mMicHeadphoneStep, mMicHeadphoneRefPosition, mMicHeadphoneMin;

    private boolean mSwitchEnabled = false;
    private SwitchCompat mMicSwitch;
    private AppCompatTextView mMicSwitchSummary;

    private void testSettings() {
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

    private void setAccentColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray b = mContext.obtainStyledAttributes(typedValue.data, new int[] { android.R.attr.colorAccent });
        mAccentColor = b.getColor(0, 0);
        b.recycle();
    }    

    @Override
    public int getLayoutRes() {
        return R.layout.grx_mic_volume_controls;
    }



    @Override
    public void onCreateView(View view) {
        
        mContext = view.getContext();
        setAccentColor();
        testSettings();

        mSwitchContainer = view.findViewById(R.id.mic_sw_container);
        mMicUpDownContainer = view.findViewById(R.id.mic_up_down_container);
        mMicHeadphoneContainer = view.findViewById(R.id.mic_headphone_container);

        mMicDownController = view.findViewById(R.id.mic_down);
        mMicUpController = view.findViewById(R.id.mic_up);
        mMicHeadphoneController = view.findViewById(R.id.mic_headphone);


        // Mic Switch
        mSwitchContainer.setEnabled(true);
        mMicSwitch = mSwitchContainer.findViewById(R.id.switcher);
        mMicSwitchSummary = mSwitchContainer.findViewById(R.id.summary);
        mSwitchContainer.setOnClickListener(v -> {
            if (!mMainSwitchEnabled) return;
            mSwitchEnabled = !mSwitchEnabled;
            MoroSound.enableMicSw(mSwitchEnabled, mContext);
            updateMicSwitch();
        });
        mSwitchEnabled = MoroSound.isMicSwEnabled();

        // Mic Down
        if (!MoroSound.hasMicDownGain()) {
            LinearLayout container = view.findViewById(R.id.mic_down_container);
            container.setVisibility(GONE);
        } else {
            mMicDownRefVal = mMicDownController.getReferenceValue();
            mMicDownStep = mMicDownController.getStepValue();
            mMicDownRefPosition = mMicDownController.getRefValPosition();
            mMicDownMin = mMicDownRefVal - (mMicDownRefPosition * mMicDownStep);

            setUpHeadMicDownVolume();
        }

        // Mic Up
        if (!MoroSound.hasMicUpGain()) {
            LinearLayout container = view.findViewById(R.id.mic_up_container);
            container.setVisibility(GONE);
        } else {
            mMicUpRefVal = mMicUpController.getReferenceValue();
            mMicUpStep = mMicUpController.getStepValue();
            mMicUpRefPosition = mMicUpController.getRefValPosition();
            mMicUpMin = mMicUpRefVal - (mMicUpRefPosition * mMicUpStep);

            setUpHeadMicUpVolume();
        }

        // Mic Headphone
        if (!MoroSound.hasMicHpGain()) {
            mMicHeadphoneContainer.setVisibility(GONE);
        } else {
            mMicHeadphoneRefVal = mMicHeadphoneController.getReferenceValue();
            mMicHeadphoneStep = mMicHeadphoneController.getStepValue();
            mMicHeadphoneRefPosition = mMicHeadphoneController.getRefValPosition();
            mMicHeadphoneMin = mMicHeadphoneRefVal - (mMicHeadphoneRefPosition * mMicHeadphoneStep);

            setUpHeadMicHpVolume();
        }

        setMainSwitchEnabled(MoroSound.isSoundSwEnabled());
        updateMicSwitch();
    }

    private void setUpHeadMicUpVolume() {
        /* set up wheel and db text */
        String kernelMicUpValue = MoroSound.getMicUpGain();
        int kernelValue;
        if (kernelMicUpValue == null || kernelMicUpValue.isEmpty()) kernelValue = 128; // default value
        else kernelValue = Integer.valueOf(kernelMicUpValue);

        int wheelProgress = (kernelValue - mMicUpMin) / mMicUpStep;

        mMicUpController.getVolumeControlView().setProgress( wheelProgress);
        int reg_val = mMicUpController.getValue(wheelProgress);
        mMicUpController.setText(getHeadPhoneDbs(reg_val) + " dB");

        mMicUpController.setListener((progress, refval, refvalposition, step, dif) -> {
            int dbs = mMicUpMin + progress * mMicUpStep;
            MoroSound.setMicUpGain(String.valueOf(dbs), mContext);
            int reg_val1 = mMicUpController.getValue(progress);
            mMicUpController.setText(getHeadPhoneDbs(reg_val1) + " dB");
        });

        mMicUpController.getVolumeControlView().setOnChangingProgressListener((progress, dif) -> {
            int dbs = mMicUpMin + progress * mMicUpStep;
            MoroSound.setMicUpGain(String.valueOf(dbs), mContext);

            int reg_val12 = mMicUpController.getValue(progress);
            mMicUpController.setText(getHeadPhoneDbs(reg_val12) + " dB");
        });
    }

    private void setUpHeadMicDownVolume() {
        /* set up wheel and db text */
        String kernelMicDownValue = MoroSound.getMicDownGain();
        int kernelValue;
        if (kernelMicDownValue == null || kernelMicDownValue.isEmpty()) kernelValue = 128; // default value
        else kernelValue = Integer.valueOf(kernelMicDownValue);

        int wheelprogress = (kernelValue - mMicDownMin) / mMicDownStep;

        mMicDownController.getVolumeControlView().setProgress( wheelprogress);
        int reg_val = mMicDownController.getValue(wheelprogress);
        mMicDownController.setText(getHeadPhoneDbs(reg_val) + " dB");

        mMicDownController.setListener((progress, refval, refvalposition, step, dif) -> {
            int dbs = mMicDownMin + progress * mMicDownStep;
            MoroSound.setMicDownGain(String.valueOf(dbs), mContext);
            int reg_val1 = mMicDownController.getValue(progress);
            mMicDownController.setText(getHeadPhoneDbs(reg_val1) + " dB");
        });

        mMicDownController.getVolumeControlView().setOnChangingProgressListener((progress, dif) -> {
            int dbs = mMicDownMin + progress * mMicDownStep;
            MoroSound.setMicDownGain(String.valueOf(dbs), mContext);

            int reg_val12 = mMicDownController.getValue(progress);
            mMicDownController.setText(getHeadPhoneDbs(reg_val12) + " dB");
        });
    }

    private void setUpHeadMicHpVolume() {
        /* set up wheel and db text */

        String kernelMicHpValue = MoroSound.getMicHpGain();
        int kernelValue;
        if (kernelMicHpValue == null || kernelMicHpValue.isEmpty()) kernelValue = 128; // default value
        else kernelValue = Integer.valueOf(kernelMicHpValue);

        int wheelProgress = (kernelValue - mMicHeadphoneMin) / mMicHeadphoneStep;

        mMicHeadphoneController.getVolumeControlView().setProgress( wheelProgress);
        int reg_val = mMicHeadphoneController.getValue(wheelProgress);
        mMicHeadphoneController.setText(getHeadPhoneDbs(reg_val) + " dB");

        mMicHeadphoneController.setListener((progress, refval, refvalposition, step, dif) -> {
            int dbs = mMicHeadphoneMin + progress * mMicHeadphoneStep;
            MoroSound.setMicHpGain(String.valueOf(dbs), mContext);
            int reg_val1 = mMicHeadphoneController.getValue(progress);
            mMicHeadphoneController.setText(getHeadPhoneDbs(reg_val1) + " dB");
        });

        mMicHeadphoneController.getVolumeControlView().setOnChangingProgressListener((progress, dif) -> {
            int dbs = mMicHeadphoneMin + progress * mMicHeadphoneStep;
            MoroSound.setMicHpGain(String.valueOf(dbs) ,mContext);

            int reg_val12 = mMicHeadphoneController.getValue(progress);
            mMicHeadphoneController.setText(getHeadPhoneDbs(reg_val12) + " dB");
        });
    }

    private void updateMicSwitch() {
        boolean enabled = mSwitchEnabled & mMainSwitchEnabled;
        mMicSwitch.setChecked(mSwitchEnabled);
        mMicSwitchSummary.setText(enabled ? R.string.enabled : R.string.disabled);
        mMicUpDownContainer.setAlpha(enabled ? 1.0f : 0.5f);
        mMicHeadphoneContainer.setAlpha(enabled ? 1.0f : 0.5f);
        if (mMicDownController != null) mMicDownController.getVolumeControlView().enableView(enabled);
        if (mMicUpController != null) mMicUpController.getVolumeControlView().enableView(enabled);
        if (mMicHeadphoneController != null) mMicHeadphoneController.getVolumeControlView().enableView(enabled);
    }

    private String getHeadPhoneDbs(int register_value) {
        // according to the doc of the chip -> dbs = -64.0 + (register_value) * 0.5 where register_value could go from 0 to 191

        float dbs = -64f +  ((float) register_value * 0.5f);
        return String.valueOf(dbs);
    }

    public void setMainSwitchEnabled(boolean enabled) {
        mMainSwitchEnabled = enabled;
        boolean state = mSwitchEnabled & mMainSwitchEnabled;
        mSwitchContainer.setAlpha(mMainSwitchEnabled ? 1.0f : 0.5f);
        mMicUpDownContainer.setAlpha(state ? 1.0f : 0.5f);
        mMicHeadphoneContainer.setAlpha(state ? 1.0f : 0.5f);
        if (mMicDownController != null) mMicDownController.getVolumeControlView().enableView(state);
        if (mMicUpController != null) mMicUpController.getVolumeControlView().enableView(state);
        if (mMicHeadphoneController != null) mMicHeadphoneController.getVolumeControlView().enableView(state);
    }

    public void resetValues() {
        setUpHeadMicDownVolume();
        setUpHeadMicUpVolume();
        setUpHeadMicHpVolume();
        mSwitchEnabled = false;
        updateMicSwitch();
    }
}
