/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.moro.mtweaks.utils.kernel.cpuhotplug;

import android.content.Context;

import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;

/**
 * Created by willi on 11.05.16.
 */
public class ThunderPlug {

    private static final String HOTPLUG_THUNDER_PLUG = "/sys/kernel/thunderplug";
    private static final String HOTPLUG_THUNDER_PLUG_ENABLE = HOTPLUG_THUNDER_PLUG + "/hotplug_enabled";
    private static final String HOTPLUG_THUNDER_PLUG_SUSPEND_CPUS = HOTPLUG_THUNDER_PLUG + "/suspend_cpus";
    private static final String HOTPLUG_THUNDER_PLUG_ENDURANCE_LEVEL = HOTPLUG_THUNDER_PLUG + "/endurance_level";
    private static final String HOTPLUG_THUNDER_PLUG_SAMPLING_RATE = HOTPLUG_THUNDER_PLUG + "/sampling_rate";
    private static final String HOTPLUG_THUNDER_PLUG_LOAD_THRESHOLD = HOTPLUG_THUNDER_PLUG + "/load_threshold";
    private static final String HOTPLUG_THUNDER_PLUG_TOUCH_BOOST = HOTPLUG_THUNDER_PLUG + "/touch_boost";
    private static final String HOTPLUG_THUNDER_PLUG_CPUS_BOOSTED = HOTPLUG_THUNDER_PLUG + "/cpus_boosted";
    private static final String HOTPLUG_THUNDER_PLUG_MAX_CORE_ONLINE = HOTPLUG_THUNDER_PLUG + "/max_core_online";
    private static final String HOTPLUG_THUNDER_PLUG_MIN_CORE_ONLINE = HOTPLUG_THUNDER_PLUG + "/min_core_online";
    private static final String HOTPLUG_THUNDER_PLUG_BOOST_LOCK_DURATION = HOTPLUG_THUNDER_PLUG + "/boost_lock_duration";
    private static final String HOTPLUG_THUNDER_PLUG_SUSPEND = HOTPLUG_THUNDER_PLUG + "/hotplug_suspend";
    private static final String HOTPLUG_THUNDER_PLUG_VERSION = HOTPLUG_THUNDER_PLUG + "/version";

    private static final String STATE_NOTIFIER = "/sys/module/state_notifier/parameters/enabled";

    public static void enableStateNotifier(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", STATE_NOTIFIER), STATE_NOTIFIER, context);
    }

    public static boolean hasThunderPlugVersion() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_VERSION);
    }

    public static String getThunderPlugVersion() {
        return Utils.readFile(HOTPLUG_THUNDER_PLUG_VERSION);
    }

    public static boolean hasThunderPlugMaxCoreOnline() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_MAX_CORE_ONLINE);
    }

    public static int getThunderPlugMaxCoreOnline() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_THUNDER_PLUG_MAX_CORE_ONLINE));
    }

    public static void setThunderPlugMaxCoreOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_THUNDER_PLUG_MAX_CORE_ONLINE),
                HOTPLUG_THUNDER_PLUG_MAX_CORE_ONLINE, context);
    }

    public static boolean hasThunderPlugMinCoreOnline() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_MIN_CORE_ONLINE);
    }

    public static int getThunderPlugMinCoreOnline() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_THUNDER_PLUG_MIN_CORE_ONLINE));
    }

    public static void setThunderPlugMinCoreOnline(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_THUNDER_PLUG_MIN_CORE_ONLINE),
                HOTPLUG_THUNDER_PLUG_MIN_CORE_ONLINE, context);
    }

    public static boolean hasThunderPlugCpusBoosted() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_CPUS_BOOSTED);
    }

    public static int getThunderPlugCpusBoosted() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_THUNDER_PLUG_CPUS_BOOSTED));
    }

    public static void setThunderPlugLoadCpusBoosted(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_THUNDER_PLUG_CPUS_BOOSTED),
                HOTPLUG_THUNDER_PLUG_CPUS_BOOSTED, context);
    }

    public static void enableThunderPlugTouchBoost(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_THUNDER_PLUG_TOUCH_BOOST),
                HOTPLUG_THUNDER_PLUG_TOUCH_BOOST, context);
    }

    public static boolean isThunderPlugTouchBoostEnabled() {
        return Utils.readFile(HOTPLUG_THUNDER_PLUG_TOUCH_BOOST).equals("1");
    }

    public static boolean hasThunderPlugTouchBoost() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_TOUCH_BOOST);
    }

    public static void setThunderPlugLoadThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_THUNDER_PLUG_LOAD_THRESHOLD),
                HOTPLUG_THUNDER_PLUG_LOAD_THRESHOLD, context);
    }

    public static int getThunderPlugLoadThreshold() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_THUNDER_PLUG_LOAD_THRESHOLD));
    }

    public static boolean hasThunderPlugLoadThreshold() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_LOAD_THRESHOLD);
    }

    public static void setThunderPlugSamplingRate(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_THUNDER_PLUG_SAMPLING_RATE),
                HOTPLUG_THUNDER_PLUG_SAMPLING_RATE, context);
    }

    public static int getThunderPlugSamplingRate() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_THUNDER_PLUG_SAMPLING_RATE));
    }

    public static boolean hasThunderPlugSamplingRate() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_SAMPLING_RATE);
    }

    public static void setThunderPlugBoostLockDuration(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_THUNDER_PLUG_BOOST_LOCK_DURATION),
                HOTPLUG_THUNDER_PLUG_BOOST_LOCK_DURATION, context);
    }

    public static int getThunderPlugBoostLockDuration() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_THUNDER_PLUG_BOOST_LOCK_DURATION));
    }

    public static boolean hasThunderPlugBoostLockDuration() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_BOOST_LOCK_DURATION);
    }

    public static void setThunderPlugEnduranceLevel(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_THUNDER_PLUG_ENDURANCE_LEVEL),
                HOTPLUG_THUNDER_PLUG_ENDURANCE_LEVEL, context);
    }

    public static int getThunderPlugEnduranceLevel() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_THUNDER_PLUG_ENDURANCE_LEVEL));
    }

    public static boolean hasThunderPlugEnduranceLevel() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_ENDURANCE_LEVEL);
    }

    public static void setThunderPlugSuspendCpus(int value, Context context) {
        run(Control.write(String.valueOf(value), HOTPLUG_THUNDER_PLUG_SUSPEND_CPUS),
                HOTPLUG_THUNDER_PLUG_SUSPEND_CPUS, context);
    }

    public static int getThunderPlugSuspendCpus() {
        return Utils.strToInt(Utils.readFile(HOTPLUG_THUNDER_PLUG_SUSPEND_CPUS));
    }

    public static boolean hasThunderPlugSuspendCpus() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_SUSPEND_CPUS);
    }
	
    public static void enableThunderPlugSuspend(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_THUNDER_PLUG_SUSPEND), HOTPLUG_THUNDER_PLUG_SUSPEND, context);
    }

    public static boolean isThunderPlugSuspendEnable() {
        return Utils.readFile(HOTPLUG_THUNDER_PLUG_SUSPEND).equals("1");
    }

    public static boolean hasThunderPlugSuspend() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_SUSPEND);
    }

    public static void enableThunderPlug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", HOTPLUG_THUNDER_PLUG_ENABLE), HOTPLUG_THUNDER_PLUG_ENABLE, context);
    }

    public static boolean isThunderPlugEnabled() {
        return Utils.readFile(HOTPLUG_THUNDER_PLUG_ENABLE).equals("1");
    }

    public static boolean hasThunderPlugEnable() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG_ENABLE);
    }

    public static boolean supported() {
        return Utils.existFile(HOTPLUG_THUNDER_PLUG);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
