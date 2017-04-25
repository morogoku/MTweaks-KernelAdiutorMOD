package com.moro.mtweaks.utils.kernel.cpuhotplug;

import android.content.Context;

import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;

/**
 * Created by Morogoku on 25/04/2017.
 */

public class SamsungPlug {

    private static final String HOTPLUG_SAMSUNG_PLUG = "/sys/power/cpuhotplug";
    private static final String HOTPLUG_SAMSUNG_PLUG_ENABLE = HOTPLUG_SAMSUNG_PLUG + "/enabled";
    private static final String HOTPLUG_SAMSUNG_PLUG_MAX_ONLINE_CPU = HOTPLUG_SAMSUNG_PLUG + "/max_online_cpu";
    private static final String HOTPLUG_SAMSUNG_PLUG_MIN_ONLINE_CPU = HOTPLUG_SAMSUNG_PLUG + "/min_online_cpu";



    public static void enableSamsungPlug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_SAMSUNG_PLUG_ENABLE), HOTPLUG_SAMSUNG_PLUG_ENABLE, context);
    }

    public static boolean isSamsungPlugEnabled() {
        return Utils.readFile(HOTPLUG_SAMSUNG_PLUG_ENABLE).equals("1");
    }

    public static boolean hasSamsungPlugEnable() {
        return Utils.existFile(HOTPLUG_SAMSUNG_PLUG_ENABLE);
    }

    public static boolean supported() {
        return Utils.existFile(HOTPLUG_SAMSUNG_PLUG);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }
}
