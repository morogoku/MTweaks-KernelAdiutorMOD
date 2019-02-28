package com.moro.mtweaks.utils.kernel.spectrum;

import android.os.AsyncTask;

import com.moro.mtweaks.utils.root.RootUtils;

/**
 * Created by Morogoku on 28/07/2017.
 */

public class Spectrum {

    public static String getProfile(){
        return RootUtils.runCommand("getprop persist.spectrum.profile");
    }

    // Method that interprets a profile and sets it
    public static void setProfile(int profile) {
        int numProfiles = 3;
        if (profile > numProfiles || profile < 0) {
            setProp(0);
        } else {
            setProp(profile);
        }
    }

    // Method that sets system property
    private static void setProp(final int profile) {
        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                RootUtils.runCommand("setprop persist.spectrum.profile " + profile);
                return null;
            }
        }.execute();
    }

    public static boolean supported() {
        return RootUtils.getProp("spectrum.support").equals("1");
    }
}
