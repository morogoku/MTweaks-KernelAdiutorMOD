package com.grx.soundcontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.moro.mtweaks.R;
import com.moro.mtweaks.utils.AppSettings;
import com.moro.mtweaks.utils.kernel.sound.MoroSound;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class GrxEqualizerManager extends RecyclerViewItem
        implements GrxEqualizerBandController.EqBandValueChange {

    private boolean mMainSwitchEnabled=false;

    private int mAccentColor;

    private LinearLayout mSwitchContainer;
    private SwitchCompat mEqSwitch;
    private AppCompatTextView mEqSwitchSummary;

    private LinearLayout mBandsContainer, mProfilesContainer;

    private ImageView mButtonSaveEqProfile;
    private AppCompatTextView mButtonCurrentProfile;

    private int mNumOfMoroProfiles;
    private int mSelectedEqProfile = -1, mOldSelectedEqProfile = -1;
    private String mCurrentProfile;

    private ArrayList<ProfileInfo> mEquProfiles = new ArrayList<>();
    
    private Context mContext;

    private static final int NUMBANDS = 5;
    private boolean mSwitchEnabled=false;


    @Override
    public int getLayoutRes() {
        return R.layout.grx_equalizer;
    }

    @Override
    public void onCreateView(View view) {
        mContext = view.getContext();
        setAccentColor();
        initEqProfilesList();


        mSwitchContainer = view.findViewById(R.id.switchcontainer);
        mSwitchContainer.setEnabled(true);
        mEqSwitch = mSwitchContainer.findViewById(R.id.switcher);
        mEqSwitchSummary = mSwitchContainer.findViewById(R.id.summary);
        mSwitchContainer.setOnClickListener(v -> {
            if(!mMainSwitchEnabled) {
                return;
            }
            mSwitchEnabled=!mSwitchEnabled;
            MoroSound.enableEqSw(mSwitchEnabled, mContext);
            updateEqSwitch();

        });
        mSwitchEnabled = MoroSound.isEqSwEnabled();

        mBandsContainer = view.findViewById(R.id.bandscontainer);
        mProfilesContainer = view.findViewById(R.id.profilescontainer);
        setCallBacks();

        mButtonCurrentProfile = view.findViewById(R.id.button_eq_profile);
        mButtonCurrentProfile.setAllCaps(false);
        mButtonCurrentProfile.setTextColor(mAccentColor);

        mButtonCurrentProfile.setOnClickListener(v -> showProfileSelectionDialog());

        mButtonSaveEqProfile = view.findViewById(R.id.button_eq_save);
        mButtonSaveEqProfile.setColorFilter(mAccentColor);
        mButtonSaveEqProfile.setOnClickListener(v -> showSaveProfileDialog());

        if(mSelectedEqProfile == -1) {
            mButtonCurrentProfile.setText(R.string.eq_profile_custom);
            mButtonSaveEqProfile.setVisibility(VISIBLE);
        }
        else {
            mButtonCurrentProfile.setText(mCurrentProfile);
            mButtonSaveEqProfile.setVisibility(INVISIBLE);
        }
        setMainSwitchEnabled(MoroSound.isSoundSwEnabled());
        updateEqSwitch();
    }

    private void setAccentColor(){
        TypedValue typedValue = new TypedValue();
        TypedArray b = mContext.obtainStyledAttributes(typedValue.data, new int[] { android.R.attr.colorAccent });
        mAccentColor = b.getColor(0, 0);
        b.recycle();
    }

    private void fireStateToSeekBars(){
        for(int i = 0; i < NUMBANDS; i++) {
            View view = mBandsContainer.findViewWithTag(String.valueOf(i));
            if(view!=null){
                ((GrxEqualizerBandController)view).mVerticalSeekBar.grxSetEnabled(mSwitchEnabled & mMainSwitchEnabled); //bbb

            }
        }
    }

    private void updateEqSwitch(){
        boolean alpha = mSwitchEnabled & mMainSwitchEnabled;
        mEqSwitch.setChecked(mSwitchEnabled);
        mSwitchContainer.setAlpha(mMainSwitchEnabled ? 1.0f : 0.5f);
          mEqSwitchSummary.setText( alpha ? R.string.enabled : R.string.disabled   );
         mProfilesContainer.setAlpha(alpha ? 1.0f : 0.5f);
        mBandsContainer.setAlpha(alpha ? 1.0f: 0.5f);
    }

    @Override
    public void EqValueChanged(int id, String value){
        String currentprofilestring = MoroSound.getCurrentProfileString();
        for(int i = 0 ; i < mEquProfiles.size(); i++) {
            if(mEquProfiles.get(i).getProfileValue().equals(currentprofilestring)) {
                mSelectedEqProfile = i;
                mOldSelectedEqProfile = i;
                mCurrentProfile=mEquProfiles.get(i).getProfileName();
                checkSelectedProfile();
                return;
            }
        }

        if(mSelectedEqProfile == -1) return;
        mSelectedEqProfile = -1;
        checkSelectedProfile();
    }

    private void setCallBacks(){
        int childs = mBandsContainer.getChildCount();
        for(int i = 0; i < childs; i++){
            View view = mBandsContainer.getChildAt(i);
            if(view != null && view instanceof GrxEqualizerBandController) {
                ((GrxEqualizerBandController) view).setCallBack(this);
            }
        }
    }

    private boolean mDismissControl = false;
    private boolean mDeleteProfileControl = false;


    private void showProfileSelectionDialog(){

        mOldSelectedEqProfile=mSelectedEqProfile;
        mDismissControl=false;
        mDeleteProfileControl=false;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.arizona_eqprofile_tit);
        builder.setSingleChoiceItems(getProfileNamesArray(), mSelectedEqProfile, (dialogInterface, i) -> {
            mSelectedEqProfile=i;
            if(mSelectedEqProfile<mNumOfMoroProfiles)
                ((AlertDialog)dialogInterface).getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(INVISIBLE);
            else
                 ((AlertDialog)dialogInterface).getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(VISIBLE);
        });
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> mDismissControl=true);
        builder.setNeutralButton(R.string.eq_profile_delete, (dialogInterface, i) -> {
            mDeleteProfileControl=true;
            mDismissControl=true;
        });
        builder.setOnDismissListener(dialog -> {
            if(mDismissControl) {
                if(!mDeleteProfileControl) {
                    if(mSelectedEqProfile!=mOldSelectedEqProfile) {
                        checkSelectedProfile();
                        fireSelectedProfile();
                    }
                }
                else delteSelectedProfile();
            }
            else mSelectedEqProfile=mOldSelectedEqProfile;
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            if(mSelectedEqProfile<mNumOfMoroProfiles)
                ((AlertDialog)dialogInterface).getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(INVISIBLE);
            else
                ((AlertDialog)dialogInterface).getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(VISIBLE);
        });
        alertDialog.show();
    }

    private void fireSelectedProfile(){
        for(int i = 0; i < NUMBANDS; i++) {
            View view = mBandsContainer.findViewWithTag(String.valueOf(i));
            if(view != null){
                String value = mEquProfiles.get(mSelectedEqProfile).getBandValue(i);
                MoroSound.setEqValues(value, i, mContext);
                ((GrxEqualizerBandController)view).mVerticalSeekBar.grxSetSeekBarProgress(value);
            }
        }
    }

    private void delteSelectedProfile(){
        String removedprofilename = mEquProfiles.get(mSelectedEqProfile).getProfileName();
        mEquProfiles.remove(mSelectedEqProfile);
        saveCustomProfiles();
        if(mOldSelectedEqProfile == mSelectedEqProfile) {  // we are deleting current profile
            mSelectedEqProfile = -1;
            mOldSelectedEqProfile = -1;
        }else {
            mSelectedEqProfile=mOldSelectedEqProfile;
        }
        checkSelectedProfile();
        Toast.makeText(mContext,removedprofilename + " - " + mContext.getString(R.string.eq_profile_deleted),Toast.LENGTH_SHORT).show();
    }

    private void checkSelectedProfile(){
        if(mSelectedEqProfile == -1) {
            mButtonSaveEqProfile.setVisibility(VISIBLE);
            mCurrentProfile = "custom";
            mButtonCurrentProfile.setText(R.string.eq_profile_custom);
        }else {
            mCurrentProfile = mEquProfiles.get(mSelectedEqProfile).getProfileName();
            mButtonCurrentProfile.setText(mCurrentProfile);
            mButtonSaveEqProfile.setVisibility(INVISIBLE);
        }
        AppSettings.saveString("arizona_eq_current_profile",mCurrentProfile,mContext);

    }

    private String[] getProfileNamesArray(){
        int elements = mEquProfiles.size();
        String[] array = new String[elements];
        for(int i=0; i < mEquProfiles.size(); i++) array[i] = mEquProfiles.get(i).getProfileName();
        return array;
    }

    private void showSaveProfileDialog(){
        final EditText editText = new EditText(mContext);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMarginStart(50);
        layoutParams.setMarginEnd(50);
        editText.setLayoutParams(layoutParams);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.eq_profile_save);
        builder.setMessage(R.string.eq_profile_name);

        builder.setView(editText);

        builder.setPositiveButton(android.R.string.ok, (dialogInterface, i)
                -> saveCurrentProfile(editText.getText().toString()));
        builder.create().show();
    }

    private void saveCurrentProfile(String profilename){
        if(profilename == null || profilename.isEmpty()) {
            Toast.makeText(mContext, R.string.eq_profile_invalid_name, Toast.LENGTH_SHORT).show();
        }else {
            String value = "";
            List<String> eqvalues = MoroSound.getEqValues();
            for(int i = 0; i < eqvalues.size(); i++) {
                value += eqvalues.get(i);
                if(i < eqvalues.size() -1) value += ",";
            }

            mEquProfiles.add(new ProfileInfo(profilename, value));
            saveCustomProfiles();
            mButtonCurrentProfile.setText(profilename);
            mCurrentProfile = profilename;
            mSelectedEqProfile = mEquProfiles.size() -1;
            mOldSelectedEqProfile = mSelectedEqProfile;
            mButtonSaveEqProfile.setVisibility(INVISIBLE);
            Toast.makeText(mContext, R.string.eq_profile_saved, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCustomProfiles() {
        String profiles = "";
        if(mEquProfiles.size() > mNumOfMoroProfiles) {
            for(int i = mNumOfMoroProfiles; i < mEquProfiles.size(); i++)
                profiles += mEquProfiles.get(i).getStringValue();
        }
        AppSettings.saveString("moro_eq_custom_profiles", profiles, mContext);
    }

    private void initEqProfilesList(){

        mEquProfiles.clear();

        mCurrentProfile = AppSettings.getString("arizona_eq_current_profile", "custom", mContext );

        String[] moroProfilesTitles = mContext.getResources().getStringArray(R.array.moro_eq_profiles_names);
        String[] moroProfilesValues = mContext.getResources().getStringArray(R.array.moro_eq_profiles_values);

        if (moroProfilesTitles==null || moroProfilesTitles.length == 0) mNumOfMoroProfiles = 0;
        else {
            mNumOfMoroProfiles = moroProfilesTitles.length;
            for(int i = 0 ; i < moroProfilesTitles.length; i++) {
                mEquProfiles.add(new ProfileInfo(moroProfilesTitles[i], moroProfilesValues[i]));
            }
        }

        String customprofiles = AppSettings.getString("moro_eq_custom_profiles", "", mContext );
        if(customprofiles!=null && !customprofiles.isEmpty()) {
            String[] profiles = customprofiles.split(Pattern.quote("|"));
            if(profiles!=null && profiles.length>0){
                for(int i = 0 ; i < profiles.length; i++) {
                    String[] profile = profiles[i].split(";");
                    if(profile!=null && profile.length == 2) {
                            mEquProfiles.add(new ProfileInfo(profile[0],profile[1]));
                    }
                }
            }
        }

        if(mCurrentProfile!=null && !mCurrentProfile.isEmpty() && !mCurrentProfile.equals("custom")) { // let´s look for profile element in list

            for(int i = 0; i<mEquProfiles.size();i++) {
                String profilename = mEquProfiles.get(i).getProfileName();
                if(mCurrentProfile.equals(profilename)) {
                    mSelectedEqProfile = i;
                    mOldSelectedEqProfile = i;
                    mCurrentProfile = profilename;
                    break;
                }
            }

        }else {
            mSelectedEqProfile = -1;
            mOldSelectedEqProfile = -1;
        }
    }

    public void setMainSwitchEnabled(boolean enabled){
        mMainSwitchEnabled = enabled;
        updateEqSwitch();
    }

    public class ProfileInfo{

        String profileName;

        String[] bandValues;

        String stringValue;

        String profileValue;

        ProfileInfo(String name, String values){
            profileName = name;
            profileValue=values;
            bandValues = values.split(",");
            stringValue = profileName+ ";" + values + "|";
        }

        String getProfileName() {
            return profileName;
        }

        String getBandValue(int band) {
            return bandValues[band];
        }

        String getStringValue() { return stringValue;}

        String getProfileValue() {return profileValue;}
    }
}
