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
package com.moro.mtweaks.utils.kernel.led;

import android.content.Context;

import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 13.08.16.
 */

public class Sec {

    private static final String LHC = "/sys/class/sec/led/led_highpower_current";
    private static final String LLC = "/sys/class/sec/led/led_lowpower_current";
    private static final String LNDO = "/sys/class/sec/led/led_notification_delay_on";
    private static final String LNDOFF = "/sys/class/sec/led/led_notification_delay_off";
    private static final String LP = "/sys/class/sec/led/led_pattern";

    private static final String LNRC1 = "/sys/class/sec/led/led_notification_ramp_control";
    private static final String LNRU1 = "/sys/class/sec/led/led_notification_ramp_up";
    private static final String LNRD1 = "/sys/class/sec/led/led_notification_ramp_down";

    private static final String LNRC2 = "/sys/class/sec/led/led_fade";
    private static final String LNRU2 = "/sys/class/sec/led/led_fade_time_up";
    private static final String LNRD2 = "/sys/class/sec/led/led_fade_time_down";

    private static final List<String> sLedFade = new ArrayList<>();
    private static final List<String> sLedFadeTimeUp = new ArrayList<>();
    private static final List<String> sLedFadeTimeDown = new ArrayList<>();

    static {
        sLedFade.add(LNRC1);
        sLedFade.add(LNRC2);

        sLedFadeTimeUp.add(LNRU1);
        sLedFadeTimeUp.add(LNRU2);

        sLedFadeTimeDown.add(LNRD1);
        sLedFadeTimeDown.add(LNRD2);
    }

    private static String LNRC;
    private static String LNRU;
    private static String LNRD;


    public static void testPattern(boolean test) {
        run(Control.write(test ? "3" : "0", LP), null, null);
    }

    public static boolean isTestingPattern() {
        return Utils.readFile(LP).equals("3");
    }

    public static boolean hasPattern() {
        return Utils.existFile(LP);
    }

    public static void setNotificationRampDown(int value, Context context) {
        run(Control.write(String.valueOf(value), LNRD), LNRD, context);
    }

    public static int getNotificationRampDown() {
        return Utils.strToInt(Utils.readFile(LNRD));
    }

    public static boolean hasNotificationRampDown() {
        if (LNRD == null) {
            for (String file : sLedFadeTimeDown) {
                if (Utils.existFile(file)) {
                    LNRD = file;
                    return true;
                }
            }
        }
        return LNRD != null;
    }

    public static void setNotificationRampUp(int value, Context context) {
        run(Control.write(String.valueOf(value), LNRU), LNRU, context);
    }

    public static int getNotificationRampUp() {
        return Utils.strToInt(Utils.readFile(LNRU));
    }

    public static boolean hasNotificationRampUp() {
        if (LNRU == null) {
            for (String file : sLedFadeTimeUp) {
                if (Utils.existFile(file)) {
                    LNRU = file;
                    return true;
                }
            }
        }
        return LNRU != null;
    }

    public static void enableNotificationRampControl(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", LNRC), LNRC, context);
    }

    public static boolean isNotificationRampControlEnabled() {
        return Utils.readFile(LNRC).equals("1");
    }

    public static boolean hasNotificationRampControl() {
        if (LNRC == null) {
            for (String file : sLedFade) {
                if (Utils.existFile(file)) {
                    LNRC = file;
                    return true;
                }
            }
        }
        return LNRC != null;
    }

    public static void setNotificationDelayOff(int value, Context context) {
        run(Control.write(String.valueOf(value), LNDOFF), LNDOFF, context);
    }

    public static int getNotificationDelayOff() {
        return Utils.strToInt(Utils.readFile(LNDOFF));
    }

    public static boolean hasNotificationDelayOff() {
        return Utils.existFile(LNDOFF);
    }

    public static void setNotificationDelayOn(int value, Context context) {
        run(Control.write(String.valueOf(value), LNDO), LNDO, context);
    }

    public static int getNotificationDelayOn() {
        return Utils.strToInt(Utils.readFile(LNDO));
    }

    public static boolean hasNotificationDelayOn() {
        return Utils.existFile(LNDO);
    }

    public static void setLowpowerCurrent(int value, Context context) {
        run(Control.write(String.valueOf(value), LLC), LLC, context);
    }

    public static int getLowpowerCurrent() {
        return Utils.strToInt(Utils.readFile(LLC));
    }

    public static boolean hasLowpowerCurrent() {
        return Utils.existFile(LLC);
    }

    public static void setHighpowerCurrent(int value, Context context) {
        run(Control.write(String.valueOf(value), LHC), LHC, context);
    }

    public static int getHighpowerCurrent() {
        return Utils.strToInt(Utils.readFile(LHC));
    }

    public static boolean hasHighpowerCurrent() {
        return Utils.existFile(LHC);
    }

    public static boolean supported() {
        return hasHighpowerCurrent() || hasLowpowerCurrent() || hasNotificationDelayOn()
                || hasNotificationDelayOff() || hasNotificationRampControl()
                || hasNotificationRampUp() || hasNotificationRampDown();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.LED, id, context);
    }

}
