package com.moro.kerneladiutor.utils.kernel.dvfs;

import android.content.Context;

import com.moro.kerneladiutor.fragments.ApplyOnBootFragment;
import com.moro.kerneladiutor.utils.Utils;
import com.moro.kerneladiutor.utils.root.Control;

/**
 * Created by Morogoku on 11/04/2017.
 */

public class Dvfs {

    private static final String DECISION_MODE = "/sys/devices/system/cpu/cpufreq/mp-cpufreq/cpu_dvfs_mode_control";
    private static final String THERMAL_CONTROL = "/sys/power/little_thermal_temp";

    public static void setDecisionMode(String value, Context context) {
        switch (value){
            case "Battery" :
                run(Control.write("0", DECISION_MODE), DECISION_MODE, context);
                break;
            case "Balance" :
                run(Control.write("1", DECISION_MODE), DECISION_MODE, context);
                break;
            case "Performance" :
                run(Control.write("2", DECISION_MODE), DECISION_MODE, context);
                break;
        }
    }

    public static String getDecisionMode() {
        if (Utils.readFile(DECISION_MODE) != null) {
            String value = Utils.readFile(DECISION_MODE);
            switch (value) {
                case "0":
                    return "Battery";
                case "1":
                    return "Balance";
                case "2":
                    return "Performance";
            }
        }
        return null;
    }

    public static void setThermalControl (String value, Context context){
        run(Control.write(String.valueOf(value), THERMAL_CONTROL), THERMAL_CONTROL, context);
    }

    public static String getThermalControl() {
        return Utils.readFile(THERMAL_CONTROL);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.DVFS, id, context);
    }


    public static boolean supported() {
        return Utils.existFile(DECISION_MODE) && Utils.existFile(THERMAL_CONTROL);
    }
}
