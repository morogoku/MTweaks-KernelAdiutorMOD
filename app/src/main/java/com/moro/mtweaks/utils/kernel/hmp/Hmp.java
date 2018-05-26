package com.moro.mtweaks.utils.kernel.hmp;

import android.content.Context;

import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;

/**
 * Created by MoroGoku on 10/10/2017.
 */

public class Hmp {

    private static Hmp sInstance;

    public static Hmp getInstance() {
        if (sInstance == null) {
            sInstance = new Hmp();
        }
        return sInstance;
    }

    private static final String UP_THRESHOLD = "/sys/kernel/hmp/up_threshold";
    private static final String DOWN_THRESHOLD = "/sys/kernel/hmp/down_threshold";


    public void setHmpProfile(String value, Context context){
        String hmp[] = value.split(" ");
        int up = Utils.strToInt(hmp[0]);
        int down = Utils.strToInt(hmp[1]);
        setUpThreshold(up, context);
        setDownThreshold(down, context);
    }

    public String getUpThreshold(){
        return Utils.readFile(UP_THRESHOLD);
    }

    public void setUpThreshold(int value, Context context){
        run(Control.write(String.valueOf(value), UP_THRESHOLD), UP_THRESHOLD, context);
    }

    public boolean hasUpThreshold() {
        return Utils.existFile(UP_THRESHOLD);
    }

    public String getDownThreshold(){
        return Utils.readFile(DOWN_THRESHOLD);
    }

    public void setDownThreshold(int value, Context context){
        run(Control.write(String.valueOf(value), DOWN_THRESHOLD), DOWN_THRESHOLD, context);
    }

    public boolean hasDownThreshold() {
        return Utils.existFile(DOWN_THRESHOLD);
    }

    public boolean supported() {
        return hasUpThreshold() || hasDownThreshold();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.HMP, id, context);
    }
}
