package com.moro.mtweaks.utils.kernel.spectrum;

import android.content.Context;
import android.os.AsyncTask;

import com.moro.mtweaks.utils.AppSettings;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.RootUtils;

/**
 * Created by Morogoku on 28/07/2017.
 */

public class Spectrum {

    public static int getSuProfile(){
        return Utils.strToInt(RootUtils.runCommand("getprop persist.spectrum.profile"));
    }

    static int getProfile(Context context){
        return AppSettings.getInt("spectrum_profile", 0 , context);
    }

    // Method that interprets a profile and sets it
    public static void setProfile(int profile, Context context) {
        int numProfiles = 3;
        if (profile > numProfiles || profile < 0) profile = 0;

        setProp(profile, context);
    }

    // Method that sets system property
    private static void setProp(final int profile, Context context) {
        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                AppSettings.saveInt("spectrum_profile", profile, context);
                RootUtils.runCommand("setprop persist.spectrum.profile " + profile);
                return null;
            }
        }.execute();
    }

    public static boolean suSupported(){
        return RootUtils.runCommand("getprop spectrum.support").equals("1");
    }

    public static boolean supported(Context context){
        return AppSettings.getBoolean("spectrum_supported", false, context);
    }
}
