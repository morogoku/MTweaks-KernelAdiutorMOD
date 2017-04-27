package com.moro.mtweaks.utils.kernel.cpuhotplug;

import android.content.Context;

import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;

/**
 * Created by Morogoku on 25/04/2017.
 */

public class SamsungPlug {

    private static final String HOTPLUG_SAMSUNG = "/sys/power/cpuhotplug";
    private static final String HOTPLUG_SAMSUNG_ENABLE = HOTPLUG_SAMSUNG + "/enabled";
    private static final String HOTPLUG_SAMSUNG_MAX_ONLINE_CPU = HOTPLUG_SAMSUNG + "/max_online_cpu";
    private static final String HOTPLUG_SAMSUNG_MIN_ONLINE_CPU = HOTPLUG_SAMSUNG + "/min_online_cpu";

    public static void enableSamsungPlug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_SAMSUNG_ENABLE), HOTPLUG_SAMSUNG_ENABLE, context);
    }

    public static boolean isSamsungPlugEnabled() {
        return Utils.readFile(HOTPLUG_SAMSUNG_ENABLE).equals("1");
    }

    public static String getMaxOnlineCpu() {
        String value = Utils.readFile(HOTPLUG_SAMSUNG_MAX_ONLINE_CPU);
        if (!value.isEmpty()) {
            return value.replace("max online cpu : ", "");
        }
        return null;
    }

    public static void setMaxOnlineCpu(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_SAMSUNG_MAX_ONLINE_CPU), HOTPLUG_SAMSUNG_MAX_ONLINE_CPU, context);
    }

    public static String getMinOnlineCpu() {
        String value = Utils.readFile(HOTPLUG_SAMSUNG_MIN_ONLINE_CPU);
        if (!value.isEmpty()) {
            return value.replace("min online cpu : ", "");
        }
        return null;
    }

    public static void setMinOnlineCpu(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_SAMSUNG_MIN_ONLINE_CPU), HOTPLUG_SAMSUNG_MIN_ONLINE_CPU, context);
    }

    public static boolean supported() {
        return Utils.existFile(HOTPLUG_SAMSUNG);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }
}
