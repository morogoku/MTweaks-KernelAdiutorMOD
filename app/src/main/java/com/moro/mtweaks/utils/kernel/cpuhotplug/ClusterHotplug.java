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
 *
 * Hotplug support added by @nalas XDA - ThundeRStormS Team | 2019-07-10
 */
package com.moro.mtweaks.utils.kernel.cpuhotplug;

import android.content.Context;

import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;

/**
 * Created by willi on 09.05.16.
 */
public class ClusterHotplug {

    private static final String CLUSTER_HOTPLUG = "/sys/module/cluster_hotplug/parameters";
    private static final String CLUSTER_HOTPLUG_ENABLED = CLUSTER_HOTPLUG + "/active";
    private static final String CLUSTER_HOTPLUG_LOW_POWER_MODE = CLUSTER_HOTPLUG + "/low_power_mode";
    private static final String CLUSTER_HOTPLUG_LOAD_THRESHOLD_DOWN = CLUSTER_HOTPLUG + "/load_threshlod_down";
    private static final String CLUSTER_HOTPLUG_LOAD_THRESHOLD_UP = CLUSTER_HOTPLUG + "/load_threshold_up";
    private static final String CLUSTER_HOTPLUG_VOTE_THRESHOLD_DOWN = CLUSTER_HOTPLUG + "/vote_threshold_down";
    private static final String CLUSTER_HOTPLUG_VOTE_THRESHOLD_UP = CLUSTER_HOTPLUG + "/vote_threshold_up";
	private static final String CLUSTER_HOTPLUG_SAMPLING_TIME = CLUSTER_HOTPLUG + "/sampling_time";


    public static void setClusterHotplugSamplingTime(int value, Context context) {
        run(Control.write(String.valueOf(value), CLUSTER_HOTPLUG_SAMPLING_TIME), CLUSTER_HOTPLUG_SAMPLING_TIME, context);
    }

    public static int getClusterHotplugSamplingTime() {
        return Utils.strToInt(Utils.readFile(CLUSTER_HOTPLUG_SAMPLING_TIME));
    }

    public static boolean hasClusterHotplugSamplingTime() {
        return Utils.existFile(CLUSTER_HOTPLUG_SAMPLING_TIME);
    }

    public static void setClusterHotplugLoadThresholdUp(int value, Context context) {
        run(Control.write(String.valueOf(value), CLUSTER_HOTPLUG_LOAD_THRESHOLD_UP),
                CLUSTER_HOTPLUG_LOAD_THRESHOLD_UP, context);
    }

    public static int getClusterHotplugLoadThresholdUp() {
        return Utils.strToInt(Utils.readFile(CLUSTER_HOTPLUG_LOAD_THRESHOLD_UP));
    }

    public static boolean hasClusterHotplugLoadThresholdUp() {
        return Utils.existFile(CLUSTER_HOTPLUG_LOAD_THRESHOLD_UP);
    }

    public static void setClusterHotplugLoadThresholdDown(int value, Context context) {
        run(Control.write(String.valueOf(value), CLUSTER_HOTPLUG_LOAD_THRESHOLD_DOWN),
                CLUSTER_HOTPLUG_LOAD_THRESHOLD_DOWN, context);
    }

    public static int getClusterHotplugLoadThresholdDown() {
        return Utils.strToInt(Utils.readFile(CLUSTER_HOTPLUG_LOAD_THRESHOLD_DOWN));
    }

    public static boolean hasClusterHotplugLoadThresholdDown() {
        return Utils.existFile(CLUSTER_HOTPLUG_LOAD_THRESHOLD_DOWN);
    }

    public static void setClusterHotplugVoteThresholdUp(int value, Context context) {
        run(Control.write(String.valueOf(value), CLUSTER_HOTPLUG_VOTE_THRESHOLD_UP),
                CLUSTER_HOTPLUG_VOTE_THRESHOLD_UP, context);
    }

    public static int getClusterHotplugVoteThresholdUp() {
        return Utils.strToInt(Utils.readFile(CLUSTER_HOTPLUG_VOTE_THRESHOLD_UP));
    }

    public static boolean hasClusterHotplugVoteThresholdUp() {
        return Utils.existFile(CLUSTER_HOTPLUG_VOTE_THRESHOLD_UP);
    }

    public static void setClusterHotplugVoteThresholdDown(int value, Context context) {
        run(Control.write(String.valueOf(value), CLUSTER_HOTPLUG_VOTE_THRESHOLD_DOWN),
                CLUSTER_HOTPLUG_VOTE_THRESHOLD_DOWN, context);
    }

    public static int getClusterHotplugVoteThresholdDown() {
        return Utils.strToInt(Utils.readFile(CLUSTER_HOTPLUG_VOTE_THRESHOLD_DOWN));
    }

    public static boolean hasClusterHotplugVoteThresholdDown() {
        return Utils.existFile(CLUSTER_HOTPLUG_VOTE_THRESHOLD_DOWN);
    }

    public static void enableClusterHotplugLowPowerMode(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CLUSTER_HOTPLUG_LOW_POWER_MODE), CLUSTER_HOTPLUG_LOW_POWER_MODE, context);
    }

    public static boolean isClusterHotplugLowPowerMode() {
        return Utils.readFile(CLUSTER_HOTPLUG_LOW_POWER_MODE).equals("1");
    }

    public static int getClusterHotplugLowPowerMode() {
        return Utils.strToInt(Utils.readFile(CLUSTER_HOTPLUG_LOW_POWER_MODE));
    }

    public static boolean hasClusterHotplugLowPowerMode() {
        return Utils.existFile(CLUSTER_HOTPLUG_LOW_POWER_MODE);
    }

    public static void enableClusterHotplug(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CLUSTER_HOTPLUG_ENABLED), CLUSTER_HOTPLUG_ENABLED, context);
    }

    public static boolean isClusterHotplugEnabled() {
        return Utils.readFile(CLUSTER_HOTPLUG_ENABLED).equals("1");
    }

    public static boolean hasClusterHotplugEnable() {
        return Utils.existFile(CLUSTER_HOTPLUG_ENABLED);
    }

    public static boolean supported() {
        return Utils.existFile(CLUSTER_HOTPLUG);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }

}
