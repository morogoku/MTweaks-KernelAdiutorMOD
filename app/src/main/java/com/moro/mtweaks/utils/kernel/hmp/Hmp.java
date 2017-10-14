package com.moro.mtweaks.utils.kernel.hmp;

import android.content.Context;
import android.provider.Contacts;

import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;

/**
 * Created by MoroGoku on 10/10/2017.
 */

public class Hmp {

    private static final String UP_THRESHOLD = "/sys/kernel/hmp/up_threshold";
    private static final String DOWN_THRESHOLD = "/sys/kernel/hmp/down_threshold";


    public static void setHmpProfile(String value, Context context){
        String hmp[] = value.split(" ");
        int up = Utils.strToInt(hmp[0]);
        int down = Utils.strToInt(hmp[1]);
        setUpThreshold(up, context);
        setDownThreshold(down, context);
    }

    public static String getUpThreshold(){
        return Utils.readFile(UP_THRESHOLD);
    }

    public static void setUpThreshold(int value, Context context){
        run(Control.write(String.valueOf(value), UP_THRESHOLD), UP_THRESHOLD, context);
    }

    public static boolean hasUpThreshold() {
        return Utils.existFile(UP_THRESHOLD);
    }

    public static String getDownThreshold(){
        return Utils.readFile(DOWN_THRESHOLD);
    }

    public static void setDownThreshold(int value, Context context){
        run(Control.write(String.valueOf(value), DOWN_THRESHOLD), DOWN_THRESHOLD, context);
    }

    public static boolean hasDownThreshold() {
        return Utils.existFile(DOWN_THRESHOLD);
    }

    public static boolean supported() {
        return hasUpThreshold() || hasDownThreshold();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.HMP, id, context);
    }
}
