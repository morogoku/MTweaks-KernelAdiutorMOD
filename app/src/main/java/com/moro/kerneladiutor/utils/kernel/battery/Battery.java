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
package com.moro.kerneladiutor.utils.kernel.battery;

import android.content.Context;

import com.moro.kerneladiutor.fragments.ApplyOnBootFragment;
import com.moro.kerneladiutor.utils.Utils;
import com.moro.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by willi on 26.06.16.
 */
public class Battery {

    private static final String FORCE_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
    private static final String BLX = "/sys/devices/virtual/misc/batterylifeextender/charging_limit";

    private static final String CHARGE_RATE = "/sys/kernel/thundercharge_control";
    private static final String CHARGE_RATE_ENABLE = CHARGE_RATE + "/enabled";
    private static final String CUSTOM_CURRENT = CHARGE_RATE + "/custom_current";

    private static final String CHARGE_S7 = "/sys/devices/battery";
    private static final String S7_UNSTABLE_CHARGE = CHARGE_S7 + "/unstable_power_detection";
    private static final String S7_HV_INPUT = CHARGE_S7 + "/hv_input";
    private static final String S7_HV_CHARGE = CHARGE_S7 + "/hv_charge";
    private static final String S7_AC_INPUT = CHARGE_S7 + "/ac_input";
    private static final String S7_AC_CHARGE = CHARGE_S7 + "/ac_charge";
    private static final String S7_AC_INPUT_SCREEN = CHARGE_S7 + "/so_limit_input";
    private static final String S7_AC_CHARGE_SCREEN = CHARGE_S7 + "/so_limit_charge";
    private static final String S7_USB_INPUT = CHARGE_S7 + "/sdp_input";
    private static final String S7_USB_CHARGE = CHARGE_S7 + "/sdp_charge";
    private static final String S7_WC_INPUT = CHARGE_S7 + "/wc_input";
    private static final String S7_WC_CHARGE = CHARGE_S7 + "/wc_charge";

    private static Integer sCapacity;

    public static void setChargingCurrent(int value, Context context) {
        run(Control.write(String.valueOf(value), CUSTOM_CURRENT), CUSTOM_CURRENT, context);
    }

    public static int getChargingCurrent() {
        return Utils.strToInt(Utils.readFile(CUSTOM_CURRENT));
    }

    public static boolean hasChargingCurrent() {
        return Utils.existFile(CUSTOM_CURRENT);
    }

    public static void enableChargeRate(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CHARGE_RATE_ENABLE), CHARGE_RATE_ENABLE, context);
    }

    public static boolean isChargeRateEnabled() {
        return Utils.readFile(CHARGE_RATE_ENABLE).equals("1");
    }

    public static boolean hasChargeRateEnable() {
        return Utils.existFile(CHARGE_RATE_ENABLE);
    }

    public static void setBlx(int value, Context context) {
        run(Control.write(String.valueOf(value == 0 ? 101 : value - 1), BLX), BLX, context);
    }

    public static int getBlx() {
        int value = Utils.strToInt(Utils.readFile(BLX));
        return value > 100 ? 0 : value + 1;
    }

    public static boolean hasBlx() {
        return Utils.existFile(BLX);
    }

    public static void enableForceFastCharge(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", FORCE_FAST_CHARGE), FORCE_FAST_CHARGE, context);
    }

    public static boolean isForceFastChargeEnabled() {
        return Utils.readFile(FORCE_FAST_CHARGE).equals("1");
    }

    public static boolean hasForceFastCharge() {
        return Utils.existFile(FORCE_FAST_CHARGE);
    }

    public static int getCapacity(Context context) {
        if (sCapacity == null) {
            try {
                Class<?> powerProfile = Class.forName("com.android.internal.os.PowerProfile");
                Constructor constructor = powerProfile.getDeclaredConstructor(Context.class);
                Object powerProInstance = constructor.newInstance(context);
                Method batteryCap = powerProfile.getMethod("getBatteryCapacity");
                sCapacity = Math.round((long) (double) batteryCap.invoke(powerProInstance));
            } catch (Exception e) {
                e.printStackTrace();
                sCapacity = 0;
            }
        }
        return sCapacity;
    }

    public static boolean hasCapacity(Context context) {
        return getCapacity(context) != 0;
    }

    public static boolean supported(Context context) {
        return hasCapacity(context);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.BATTERY, id, context);
    }

/* Init S7 Battery */

    public static void setS7HvInput(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_HV_INPUT), S7_HV_INPUT, context);
    }

    public static String getS7HvInput() {
        return Utils.readFile(S7_HV_INPUT);
    }

    public static void setS7HvCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_HV_CHARGE), S7_HV_CHARGE, context);
    }

    public static String getS7HvCharge() {
        return Utils.readFile(S7_HV_CHARGE);
    }

    public static void setS7AcInput(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_AC_INPUT), S7_AC_INPUT, context);
    }

    public static String getS7AcInput() {
        return Utils.readFile(S7_AC_INPUT);
    }

    public static void setS7AcCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_AC_CHARGE), S7_AC_CHARGE, context);
    }

    public static String getS7AcCharge() {
        return Utils.readFile(S7_AC_CHARGE);
    }

    public static void setS7AcInputScreen(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_AC_INPUT_SCREEN), S7_AC_INPUT_SCREEN, context);
    }

    public static String getS7AcInputScreen() {
        return Utils.readFile(S7_AC_INPUT_SCREEN);
    }

    public static void setS7AcChargeScreen(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_AC_CHARGE_SCREEN), S7_AC_CHARGE_SCREEN, context);
    }

    public static String getS7AcChargeScreen() {
        return Utils.readFile(S7_AC_CHARGE_SCREEN);
    }

    public static void setS7UsbInput(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_USB_INPUT), S7_USB_INPUT, context);
    }

    public static String getS7UsbInput() {
        return Utils.readFile(S7_USB_INPUT);
    }

    public static void setS7UsbCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_USB_CHARGE), S7_USB_CHARGE, context);
    }

    public static String getS7UsbCharge() {
        return Utils.readFile(S7_USB_CHARGE);
    }

    public static void setS7WcInput(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_WC_INPUT), S7_WC_INPUT, context);
    }

    public static String getS7WcInput() {
        return Utils.readFile(S7_WC_INPUT);
    }

    public static void setS7WcCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_WC_CHARGE), S7_WC_CHARGE, context);
    }

    public static String getS7WcCharge() {
        return Utils.readFile(S7_WC_CHARGE);
    }

    public static boolean hasChargeS7() {
        return Utils.existFile(CHARGE_S7);
    }

    public static boolean isUnstableChargeEnabled() {
        return Utils.readFile(S7_UNSTABLE_CHARGE).equals("1");
    }

    public static void enableUnstableCharge(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", S7_UNSTABLE_CHARGE), S7_UNSTABLE_CHARGE, context);
    }
}
