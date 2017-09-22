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
package com.moro.mtweaks.utils.kernel.battery;

import android.content.Context;

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Prefs;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;

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
    private static final String S7_CAR_INPUT = CHARGE_S7 + "/car_input";
    private static final String S7_CAR_CHARGE = CHARGE_S7 + "/car_charge";
    private static final String S7_CHARGE_SOURCE = CHARGE_S7 + "/power_supply/battery/batt_charging_source";
    private static final String S7_STORE_MODE = "/sys/devices/battery/power_supply/battery/store_mode";
    private static final String S7_STORE_MODE_MAX = "/sys/module/sec_battery/parameters/store_mode_max";
    private static final String S7_STORE_MODE_MIN = "/sys/module/sec_battery/parameters/store_mode_min";

    private static Integer sCapacity;

    public static void saveS7StockValues(Context context) {
        if (hasS7HvInput()) {
            Prefs.saveString("bat_s7_hv_input", getS7HvInput(), context);
        } else {
            Prefs.remove("bat_s7_hv_input", context);
        }
        if (hasS7HvCharge()) {
            Prefs.saveString("bat_s7_hv_charge", getS7HvCharge(), context);
        } else {
            Prefs.remove("bat_s7_hv_charge", context);
        }
        if (hasS7AcInput()) {
            Prefs.saveString("bat_s7_ac_input", getS7AcInput(), context);
        } else {
            Prefs.remove("bat_s7_ac_input", context);
        }
        if (hasS7AcCharge()) {
            Prefs.saveString("bat_s7_ac_charge", getS7AcCharge(), context);
        } else {
            Prefs.remove("bat_s7_ac_charge", context);
        }
        if (hasS7AcInputScreen()) {
            Prefs.saveString("bat_s7_ac_input_screen", getS7AcInputScreen(), context);
        } else {
            Prefs.remove("bat_s7_ac_input_screen", context);
        }
        if (hasS7AcChargeScreen()) {
            Prefs.saveString("bat_s7_ac_charge_screen", getS7AcChargeScreen(), context);
        } else {
            Prefs.remove("bat_s7_ac_charge_screen", context);
        }
        if (hasS7UsbInput()) {
            Prefs.saveString("bat_s7_usb_input", getS7UsbInput(), context);
        } else {
            Prefs.remove("bat_s7_usb_input", context);
        }
        if (hasS7UsbCharge()) {
            Prefs.saveString("bat_s7_usb_charge", getS7UsbCharge(), context);
        } else {
            Prefs.remove("bat_s7_usb_charge", context);
        }
        if (hasS7WcInput()) {
            Prefs.saveString("bat_s7_wc_input", getS7WcInput(), context);
        } else {
            Prefs.remove("bat_s7_wc_input", context);
        }
        if (hasS7WcCharge()) {
            Prefs.saveString("bat_s7_wc_charge", getS7WcCharge(), context);
        } else {
            Prefs.remove("bat_s7_wc_charge", context);
        }
        if (hasS7CarInput()) {
            Prefs.saveString("bat_s7_car_input", getS7CarInput(), context);
        } else {
            Prefs.remove("bat_s7_car_input", context);
        }
        if (hasS7CarCharge()) {
            Prefs.saveString("bat_s7_car_charge", getS7CarCharge(), context);
        } else {
            Prefs.remove("bat_s7_car_charge", context);
        }

        Prefs.saveBoolean("s7_battery_saved", true, context);
    }

    public static boolean hasS7StoreMode(){
        return (Utils.existFile(S7_STORE_MODE) && Utils.existFile(S7_STORE_MODE_MAX)
                && Utils.existFile(S7_STORE_MODE_MIN));
    }

    public static boolean isS7StoreModeEnabled(){
        return Utils.readFile(S7_STORE_MODE).equals("1");
    }

    public static void enableS7StoreMode(boolean enable, Context context){
        run(Control.write(enable ? "1" : "0", S7_STORE_MODE), S7_STORE_MODE, context);
    }



    public static String getS7StoreModeMax(){
        return Utils.readFile(S7_STORE_MODE_MAX);
    }

    public static void setS7StoreModeMax(int value, Context context){
        run(Control.write(String.valueOf(value), S7_STORE_MODE_MAX), S7_STORE_MODE_MAX, context);
    }

    public static String getS7StoreModeMin(){
        return Utils.readFile(S7_STORE_MODE_MIN);
    }

    public static void setS7StoreModeMin(int value, Context context){
        run(Control.write(String.valueOf(value), S7_STORE_MODE_MIN), S7_STORE_MODE_MIN, context);
    }

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

    public static boolean hasS7HvInput() {
        return Utils.existFile(S7_HV_INPUT);
    }

    public static void setS7HvInput(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_HV_INPUT), S7_HV_INPUT, context);
    }

    public static String getS7HvInput() {
        return Utils.readFile(S7_HV_INPUT);
    }

    public static boolean hasS7HvCharge() {
        return Utils.existFile(S7_HV_CHARGE);
    }

    public static void setS7HvCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_HV_CHARGE), S7_HV_CHARGE, context);
    }

    public static String getS7HvCharge() {
        return Utils.readFile(S7_HV_CHARGE);
    }

    public static boolean hasS7AcInput() {
        return Utils.existFile(S7_AC_INPUT);
    }

    public static void setS7AcInput(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_AC_INPUT), S7_AC_INPUT, context);
    }

    public static String getS7AcInput() {
        return Utils.readFile(S7_AC_INPUT);
    }

    public static boolean hasS7AcCharge() {
        return Utils.existFile(S7_AC_CHARGE);
    }

    public static void setS7AcCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_AC_CHARGE), S7_AC_CHARGE, context);
    }

    public static String getS7AcCharge() {
        return Utils.readFile(S7_AC_CHARGE);
    }

    public static boolean hasS7AcInputScreen() {
        return Utils.existFile(S7_AC_INPUT_SCREEN);
    }

    public static void setS7AcInputScreen(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_AC_INPUT_SCREEN), S7_AC_INPUT_SCREEN, context);
    }

    public static String getS7AcInputScreen() {
        return Utils.readFile(S7_AC_INPUT_SCREEN);
    }

    public static boolean hasS7AcChargeScreen() {
        return Utils.existFile(S7_AC_CHARGE_SCREEN);
    }

    public static void setS7AcChargeScreen(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_AC_CHARGE_SCREEN), S7_AC_CHARGE_SCREEN, context);
    }

    public static String getS7AcChargeScreen() {
        return Utils.readFile(S7_AC_CHARGE_SCREEN);
    }

    public static boolean hasS7UsbInput() {
        return Utils.existFile(S7_USB_INPUT);
    }

    public static void setS7UsbInput(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_USB_INPUT), S7_USB_INPUT, context);
    }

    public static String getS7UsbInput() {
        return Utils.readFile(S7_USB_INPUT);
    }

    public static boolean hasS7UsbCharge() {
        return Utils.existFile(S7_USB_CHARGE);
    }

    public static void setS7UsbCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_USB_CHARGE), S7_USB_CHARGE, context);
    }

    public static String getS7UsbCharge() {
        return Utils.readFile(S7_USB_CHARGE);
    }

    public static boolean hasS7WcInput() {
        return Utils.existFile(S7_WC_INPUT);
    }

    public static void setS7WcInput(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_WC_INPUT), S7_WC_INPUT, context);
    }

    public static String getS7WcInput() {
        return Utils.readFile(S7_WC_INPUT);
    }

    public static boolean hasS7WcCharge() {
        return Utils.existFile(S7_WC_CHARGE);
    }

    public static void setS7WcCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_WC_CHARGE), S7_WC_CHARGE, context);
    }

    public static String getS7WcCharge() {
        return Utils.readFile(S7_WC_CHARGE);
    }

    public static boolean hasS7CarCharge() {
        return Utils.existFile(S7_CAR_CHARGE);
    }

    public static void setS7CarCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_CAR_CHARGE), S7_CAR_CHARGE, context);
    }

    public static String getS7CarCharge() {
        return Utils.readFile(S7_CAR_CHARGE);
    }

    public static boolean hasS7CarInput() {
        return Utils.existFile(S7_CAR_INPUT);
    }

    public static void setS7CarInput(int value, Context context) {
        run(Control.write(String.valueOf(value), S7_CAR_INPUT), S7_CAR_INPUT, context);
    }

    public static String getS7CarInput() {
        return Utils.readFile(S7_CAR_INPUT);
    }

    public static boolean hasS7ChargeSource() {
        return Utils.existFile(S7_CHARGE_SOURCE);
    }

    public static String getS7ChargeSource(Context context) {
        String value = Utils.readFile(S7_CHARGE_SOURCE);
        switch (value){
            case "0" :
                return context.getResources().getString(R.string.cs_unknown);
            case "1" :
                return context.getResources().getString(R.string.cs_battery);
            case "2" :
                return context.getResources().getString(R.string.cs_ups);
            case "3" :
                return context.getResources().getString(R.string.cs_main_ac);
            case "4" :
                return context.getResources().getString(R.string.cs_usb);
            case "5" :
                return context.getResources().getString(R.string.cs_usb_dedeicated);
            case "6" :
                return context.getResources().getString(R.string.cs_usb_charging);
            case "7" :
                return context.getResources().getString(R.string.cs_usb_accesory);
            case "8" :
                return context.getResources().getString(R.string.cs_battery_monitor);
            case "9" :
                return context.getResources().getString(R.string.cs_misc);
            case "10" :
                return context.getResources().getString(R.string.cs_wireless);
            case "11" :
                return context.getResources().getString(R.string.cs_hv_wireless);
            case "12" :
                return context.getResources().getString(R.string.cs_pma_wireless);
            case "13" :
                return context.getResources().getString(R.string.cs_car);
            case "14" :
                return context.getResources().getString(R.string.cs_uart_off);
            case "15" :
                return context.getResources().getString(R.string.cs_otg);
            case "16" :
                return context.getResources().getString(R.string.cs_lan);
            case "17" :
                return context.getResources().getString(R.string.cs_mhl_500);
            case "18" :
                return context.getResources().getString(R.string.cs_mhl_900);
            case "19" :
                return context.getResources().getString(R.string.cs_mhl_1500);
            case "20" :
                return context.getResources().getString(R.string.cs_mhl_usb);
            case "21" :
                return context.getResources().getString(R.string.cs_smart_otg);
            case "22" :
                return context.getResources().getString(R.string.cs_smart_notg);
            case "23" :
                return context.getResources().getString(R.string.cs_power_sharing);
            case "24" :
                return context.getResources().getString(R.string.cs_hv_mains);
            case "25" :
                return context.getResources().getString(R.string.cs_hv_mains_12);
            case "26" :
                return context.getResources().getString(R.string.cs_hv_prepare);
            case "27" :
                return context.getResources().getString(R.string.cs_hv_error);
            case "28" :
                return context.getResources().getString(R.string.cs_mhl_100);
            case "29" :
                return context.getResources().getString(R.string.cs_mhl_2000);
            case "30" :
                return context.getResources().getString(R.string.cs_hv_unknown);
            case "31" :
                return context.getResources().getString(R.string.cs_mdock);
            case "32" :
                return context.getResources().getString(R.string.cs_hmt_conected);
            case "33" :
                return context.getResources().getString(R.string.cs_hmt_charge);
            case "34" :
                return context.getResources().getString(R.string.cs_wireless_pack);
            case "35" :
                return context.getResources().getString(R.string.cs_wireless_pack_ta);
            case "36" :
                return context.getResources().getString(R.string.cs_wireless_stand);
            case "37" :
                return context.getResources().getString(R.string.cs_wireless_hv_stand);
            case "38" :
                return context.getResources().getString(R.string.cs_pdic);
            case "39" :
                return context.getResources().getString(R.string.cs_hv_mains);
            case "40" :
                return context.getResources().getString(R.string.cs_qc20);
            case "41" :
                return context.getResources().getString(R.string.cs_qc30);
        }
        return "Unknown source";
    }

    public static boolean hasChargeS7() {
        return Utils.existFile(CHARGE_S7);
    }

    public static boolean hasUnstableCharge() {
        return Utils.existFile(S7_UNSTABLE_CHARGE);
    }

    public static boolean isUnstableChargeEnabled() {
        return Utils.readFile(S7_UNSTABLE_CHARGE).equals("1");
    }

    public static void enableUnstableCharge(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", S7_UNSTABLE_CHARGE), S7_UNSTABLE_CHARGE, context);
    }
}
