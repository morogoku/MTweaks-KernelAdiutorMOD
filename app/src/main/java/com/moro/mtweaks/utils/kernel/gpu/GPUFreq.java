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
package com.moro.mtweaks.utils.kernel.gpu;

import android.content.Context;

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;
import com.moro.mtweaks.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 12.05.16.
 */
public class GPUFreq {

    private static GPUFreq sIOInstance;

    public static GPUFreq getInstance() {
        if (sIOInstance == null) {
            sIOInstance = new GPUFreq();
        }
        return sIOInstance;
    }

    public static final String BACKUP = "/data/.mtweaks/gpu_stock_voltage";

    private static final String MAX_S7_FREQ = "/sys/devices/14ac0000.mali/max_clock";
    private static final String MAX_S7_FREQ_STOCK = "/sys/devices/platform/gpusysfs/gpu_max_clock";
    private static final String MIN_S7_FREQ = "/sys/devices/14ac0000.mali/min_clock";
    private static final String MIN_S7_FREQ_STOCK = "/sys/devices/platform/gpusysfs/gpu_min_clock";
    private static final String CUR_S7_FREQ = "/sys/devices/14ac0000.mali/clock";
    private static final String AVAILABLE_S7_FREQS = "/sys/devices/14ac0000.mali/volt_table";
    private static final String AVAILABLE_S7_FREQS_STOCK = "/sys/devices/platform/gpusysfs/gpu_freq_table";
    private static final String AVAILABLE_S7_GOVERNORS = "/sys/devices/14ac0000.mali/dvfs_governor";
    private static final String TUNABLE_HIGHSPEED_S7_CLOCK = "/sys/devices/14ac0000.mali/highspeed_clock";
    private static final String TUNABLE_HIGHSPEED_S7_LOAD = "/sys/devices/14ac0000.mali/highspeed_load";
    private static final String TUNABLE_HIGHSPEED_S7_DELAY = "/sys/devices/14ac0000.mali/highspeed_delay";
    private static final String POWER_POLICY_S7 = "/sys/devices/14ac0000.mali/power_policy";

    private static final String MAX_S8_FREQ = "/sys/devices/platform/13900000.mali/max_clock";
    private static final String MAX_S8_FREQ_STOCK = "/sys/kernel/gpu/gpu_max_clock";
    private static final String MIN_S8_FREQ = "/sys/devices/platform/13900000.mali/min_clock";
    private static final String MIN_S8_FREQ_STOCK = "/sys/kernel/gpu/gpu_min_clock";
    private static final String CUR_S8_FREQ = "/sys/devices/platform/13900000.mali/clock";
    private static final String AVAILABLE_S8_FREQS = "/sys/devices/platform/13900000.mali/volt_table";
    private static final String AVAILABLE_S8_FREQS_STOCK = "/sys/kernel/gpu/gpu_freq_table";
    private static final String AVAILABLE_S8_GOVERNORS = "/sys/devices/platform/13900000.mali/dvfs_governor";
    private static final String TUNABLE_HIGHSPEED_S8_CLOCK = "/sys/devices/platform/13900000.mali/highspeed_clock";
    private static final String TUNABLE_HIGHSPEED_S8_LOAD = "/sys/devices/platform/13900000.mali/highspeed_load";
    private static final String TUNABLE_HIGHSPEED_S8_DELAY = "/sys/devices/platform/13900000.mali/highspeed_delay";
    private static final String POWER_POLICY_S8 = "/sys/devices/platform/13900000.mali/power_policy";


    private static final HashMap<String, Integer> sAvailableVolts = new HashMap<>();
    private static final HashMap<String, Integer> sCurrentFreqs = new HashMap<>();
    private static final HashMap<String, Integer> sMaxFreqs = new HashMap<>();
    private static final HashMap<String, Integer> sMinFreqs = new HashMap<>();
    private static final HashMap<String, Integer> sAvailableFreqs = new HashMap<>();
    private static final List<String> sScalingGovernors = new ArrayList<>();
    private static final HashMap<String, Integer> sTunableHighspeedClocks = new HashMap<>();
    private static final HashMap<String, Integer> sTunableHighspeedLoads = new HashMap<>();
    private static final HashMap<String, Integer> sTunableHighspeedDelays = new HashMap<>();
    private static final HashMap<String, Integer> sPowerPolicies = new HashMap<>();

    static {
        sAvailableVolts.put(AVAILABLE_S7_FREQS, 1000);
        sAvailableVolts.put(AVAILABLE_S8_FREQS, 1000);

        sCurrentFreqs.put(CUR_S7_FREQ, 1);
        sCurrentFreqs.put(CUR_S8_FREQ, 1);

        if (Utils.existFile(MAX_S7_FREQ) || Utils.existFile(MAX_S8_FREQ)) {
            sMaxFreqs.put(MAX_S7_FREQ, 1);
            sMaxFreqs.put(MAX_S8_FREQ, 1);
        } else {
            sMaxFreqs.put(MAX_S7_FREQ_STOCK, 1);
            sMaxFreqs.put(MAX_S8_FREQ_STOCK, 1);
        }

        if (Utils.existFile(MIN_S7_FREQ) || Utils.existFile(MIN_S8_FREQ)) {
            sMinFreqs.put(MIN_S7_FREQ, 1);
            sMinFreqs.put(MIN_S8_FREQ, 1);
        } else {
            sMinFreqs.put(MIN_S7_FREQ_STOCK, 1);
            sMinFreqs.put(MIN_S8_FREQ_STOCK, 1);
        }

        if (Utils.existFile(AVAILABLE_S7_FREQS) || Utils.existFile(AVAILABLE_S8_FREQS)) {
            sAvailableFreqs.put(AVAILABLE_S7_FREQS, 1);
            sAvailableFreqs.put(AVAILABLE_S8_FREQS, 1);
        } else {
            sAvailableFreqs.put(AVAILABLE_S7_FREQS_STOCK, 1);
            sAvailableFreqs.put(AVAILABLE_S8_FREQS_STOCK, 1);
        }

        sScalingGovernors.add(AVAILABLE_S7_GOVERNORS);
        sScalingGovernors.add(AVAILABLE_S8_GOVERNORS);

        sTunableHighspeedClocks.put(TUNABLE_HIGHSPEED_S7_CLOCK, 1);
        sTunableHighspeedClocks.put(TUNABLE_HIGHSPEED_S8_CLOCK, 1);

        sTunableHighspeedLoads.put(TUNABLE_HIGHSPEED_S7_LOAD, 1);
        sTunableHighspeedLoads.put(TUNABLE_HIGHSPEED_S8_LOAD, 1);

        sTunableHighspeedDelays.put(TUNABLE_HIGHSPEED_S7_DELAY, 1);
        sTunableHighspeedDelays.put(TUNABLE_HIGHSPEED_S8_DELAY, 1);

        sPowerPolicies.put(POWER_POLICY_S7, 1);
        sPowerPolicies.put(POWER_POLICY_S8, 1);
    }

    public static String AVAILABLE_VOLTS;
    private static int AVAILABLE_VOLTS_OFFSET;
    private static String CUR_FREQ;
    private static Integer CUR_FREQ_OFFSET;
    private static List<Integer> AVAILABLE_FREQS;
    private static List<Integer> AVAILABLE_FREQS_SORT;
    private static String MAX_FREQ;
    private static Integer MAX_FREQ_OFFSET;
    private static String MIN_FREQ;
    private static Integer MIN_FREQ_OFFSET;
    private static String GOVERNOR;
    private static Integer AVAILABLE_GOVERNORS_OFFSET;
    private static String TUNABLE_HIGHSPEED_CLOCK;
    private static int TUNABLE_HIGHSPEED_CLOCK_OFFSET;
    private static String TUNABLE_HIGHSPEED_LOAD;
    private static int TUNABLE_HIGHSPEED_LOAD_OFFSET;
    private static String TUNABLE_HIGHSPEED_DELAY;
    private static int TUNABLE_HIGHSPEED_DELAY_OFFSET;
    private static String POWER_POLICY;
    private static int POWER_POLICY_OFFSET;

    private static String SPLIT_NEW_LINE = "\\r?\\n";
    private static String SPLIT_LINE = " ";

    public static void setGovernor(String value, Context context) {
        switch (value){
            case "Default" :
                run(Control.write("0", GOVERNOR), GOVERNOR, context);
                break;
            case "Interactive" :
                run(Control.write("1", GOVERNOR), GOVERNOR, context);
                break;
            case "Static" :
                run(Control.write("2", GOVERNOR), GOVERNOR, context);
                break;
            case "Booster" :
                run(Control.write("3", GOVERNOR), GOVERNOR, context);
                break;
        }
    }

    public static List<String> getAvailableGovernors() {
        String value = Utils.readFile(GOVERNOR);
        if (!value.isEmpty()) {
            String[] lines = value.split("\\r?\\n");
            List<String> governors = new ArrayList<>();
            for (String line : lines) {
                if (line.startsWith("[Current Governor]")){
                    break;
                }
                governors.add(line);
        }
            return governors;
        }
        return null;
    }

    public static String getGovernor() {
        String value = Utils.readFile(GOVERNOR);
        if (!value.isEmpty()) {
            String[] lines = value.split("\\r?\\n");
            String governor = "";
            for (String line : lines) {
                if (line.startsWith("[Current Governor]")){
                    governor = line.replace("[Current Governor] ", "");
                }
            }
            return governor;
        }
        return null;
    }

    public static boolean hasGovernor() {
        if (GOVERNOR == null) {
            for (String file : sScalingGovernors) {
                if (Utils.existFile(file)) {
                    GOVERNOR = file;
                    return true;
                }
            }
        }
        return GOVERNOR != null;
    }

    public static void setMinFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), MIN_FREQ), MIN_FREQ, context);
    }

    public static int getMinFreqOffset() {
        return MIN_FREQ_OFFSET;
    }

    public static int getMinFreq() {
        return Utils.strToInt(Utils.readFile(MIN_FREQ));
    }

    public static boolean hasMinFreq() {
        if (MIN_FREQ == null) {
            for (String file : sMinFreqs.keySet()) {
                if (Utils.existFile(file)) {
                    MIN_FREQ = file;
                    MIN_FREQ_OFFSET = sMinFreqs.get(file);
                    return true;
                }
            }
        }
        return MIN_FREQ != null;
    }

    public static void setMaxFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), MAX_FREQ), MAX_FREQ, context);
    }

    public static int getMaxFreqOffset() {
        return MAX_FREQ_OFFSET;
    }

    public static int getMaxFreq() {
        return Utils.strToInt(Utils.readFile(MAX_FREQ));
    }

    public static boolean hasMaxFreq() {
        if (MAX_FREQ == null) {
            for (String file : sMaxFreqs.keySet()) {
                if (Utils.existFile(file)) {
                    MAX_FREQ = file;
                    MAX_FREQ_OFFSET = sMaxFreqs.get(file);
                    return true;
                }
            }
        }
        return MAX_FREQ != null;
    }

    public static List<String> getAdjustedFreqs(Context context) {
        List<String> list = new ArrayList<>();
        if (getAvailableFreqs() != null) {
            for (int freq : getAvailableFreqs()) {
                list.add((freq / AVAILABLE_GOVERNORS_OFFSET) + context.getString(R.string.mhz));
            }
        }
        return list;
    }

    public static List<Integer> getAvailableFreqs() {
        if (AVAILABLE_FREQS == null) {
            for (String file : sAvailableFreqs.keySet()) {
                if (Utils.existFile(file)) {
                    if ((file.equals(AVAILABLE_S7_FREQS)) || (file.equals(AVAILABLE_S8_FREQS))) {
                        String freqs[] = Utils.readFile(file).split("\\r?\\n");
                        AVAILABLE_FREQS = new ArrayList<>();
                        for (String freq : freqs) {
                            String[] freqLine = freq.split(" ");
                            AVAILABLE_FREQS.add(Utils.strToInt(freqLine[0].trim()));
                        }
                    } else if ((file.equals(AVAILABLE_S7_FREQS_STOCK)) || (file.equals(AVAILABLE_S8_FREQS_STOCK))){
                        String freqs[] = Utils.readFile(file).split(" ");
                        AVAILABLE_FREQS = new ArrayList<>();
                        for (String freq : freqs) {
                            AVAILABLE_FREQS.add(Utils.strToInt(freq.trim()));
                        }
                    }
                    AVAILABLE_GOVERNORS_OFFSET = sAvailableFreqs.get(file);
                    break;
                }
            }
        }
        if (AVAILABLE_FREQS == null) return null;
        return AVAILABLE_FREQS;
    }

    public static List<Integer> getAvailableFreqsSort() {
        if (AVAILABLE_FREQS_SORT == null) {
            for (String file : sAvailableFreqs.keySet()) {
                if (Utils.existFile(file)) {
                    if ((file.equals(AVAILABLE_S7_FREQS)) || (file.equals(AVAILABLE_S8_FREQS))) {
                        String freqs[] = Utils.readFile(file).split("\\r?\\n");
                        AVAILABLE_FREQS_SORT = new ArrayList<>();
                        for (String freq : freqs) {
                            String[] freqLine = freq.split(" ");
                            AVAILABLE_FREQS_SORT.add(Utils.strToInt(freqLine[0].trim()));
                        }
                    } else if ((file.equals(AVAILABLE_S7_FREQS_STOCK)) || (file.equals(AVAILABLE_S8_FREQS_STOCK))){
                        String freqs[] = Utils.readFile(file).split(" ");
                        AVAILABLE_FREQS_SORT = new ArrayList<>();
                        for (String freq : freqs) {
                            AVAILABLE_FREQS_SORT.add(Utils.strToInt(freq.trim()));
                        }
                    }
                    AVAILABLE_GOVERNORS_OFFSET = sAvailableFreqs.get(file);
                    break;
                }
            }
        }
        if (AVAILABLE_FREQS_SORT == null) return null;
        Collections.sort(AVAILABLE_FREQS_SORT);
        return AVAILABLE_FREQS_SORT;
    }

    public static int getCurFreqOffset() {
        return CUR_FREQ_OFFSET;
    }

    public static int getCurFreq() {
        return Utils.strToInt(Utils.readFile(CUR_FREQ));
    }

    public static boolean hasCurFreq() {
        if (CUR_FREQ == null) {
            for (String file : sCurrentFreqs.keySet()) {
                if (Utils.existFile(file)) {
                    CUR_FREQ = file;
                    CUR_FREQ_OFFSET = sCurrentFreqs.get(file);
                    return true;
                }
            }
        }
        return CUR_FREQ != null;
    }

    public static int getHighspeedClock() {
        return Utils.strToInt(Utils.readFile(TUNABLE_HIGHSPEED_CLOCK));
    }

    public static void setHighspeedClock(String value, Context context) {
        run(Control.write(value, TUNABLE_HIGHSPEED_CLOCK), TUNABLE_HIGHSPEED_CLOCK, context);
    }

    public static boolean hasHighspeedClock() {
        if (TUNABLE_HIGHSPEED_CLOCK == null) {
            for (String file : sTunableHighspeedClocks.keySet()) {
                if (Utils.existFile(file)) {
                    TUNABLE_HIGHSPEED_CLOCK = file;
                    TUNABLE_HIGHSPEED_CLOCK_OFFSET = sTunableHighspeedClocks.get(file);
                    return true;
                }
            }
        }
        return TUNABLE_HIGHSPEED_CLOCK != null;
    }

    public static int getHighspeedLoad() {
        return Utils.strToInt(Utils.readFile(TUNABLE_HIGHSPEED_LOAD));
    }

    public static void setHighspeedLoad(int value, Context context) {
        run(Control.write(String.valueOf(value), TUNABLE_HIGHSPEED_LOAD), TUNABLE_HIGHSPEED_LOAD, context);
    }

    public static boolean hasHighspeedLoad() {
        if (TUNABLE_HIGHSPEED_LOAD == null) {
            for (String file : sTunableHighspeedLoads.keySet()) {
                if (Utils.existFile(file)) {
                    TUNABLE_HIGHSPEED_LOAD = file;
                    TUNABLE_HIGHSPEED_LOAD_OFFSET = sTunableHighspeedLoads.get(file);
                    return true;
                }
            }
        }
        return TUNABLE_HIGHSPEED_LOAD != null;
    }

    public static int getHighspeedDelay() {
        return Utils.strToInt(Utils.readFile(TUNABLE_HIGHSPEED_DELAY));
    }

    public static void setHighspeedDelay(int value, Context context) {
        run(Control.write(String.valueOf(value), TUNABLE_HIGHSPEED_DELAY), TUNABLE_HIGHSPEED_DELAY, context);
    }

    public static boolean hasHighspeedDelay() {
        if (TUNABLE_HIGHSPEED_DELAY == null) {
            for (String file : sTunableHighspeedDelays.keySet()) {
                if (Utils.existFile(file)) {
                    TUNABLE_HIGHSPEED_DELAY = file;
                    TUNABLE_HIGHSPEED_DELAY_OFFSET = sTunableHighspeedDelays.get(file);
                    return true;
                }
            }
        }
        return TUNABLE_HIGHSPEED_DELAY != null;
    }

    public static void setVoltage(Integer freq, String voltage, Context context) {
        String volt = String.valueOf((int)(Utils.strToFloat(voltage) * AVAILABLE_VOLTS_OFFSET));
        run(Control.write(freq + " " + volt, AVAILABLE_VOLTS), AVAILABLE_VOLTS + freq, context);
    }

    public static List<String> getStockVoltages() {
        String value = Utils.readFile(BACKUP);
        if (!value.isEmpty()) {
            String[] lines = value.split(SPLIT_NEW_LINE);
            List<String> voltages = new ArrayList<>();
            for (String line : lines) {
                String[] voltageLine = line.split(SPLIT_LINE);
                if (voltageLine.length > 1) {
                    voltages.add(String.valueOf(Utils.strToFloat(voltageLine[1].trim()) / AVAILABLE_VOLTS_OFFSET));

                }
            }
            return voltages;
        }
        return null;
    }

    public static List<String> getVoltages() {
        String value = Utils.readFile(AVAILABLE_VOLTS);
        if (!value.isEmpty()) {
            String[] lines = value.split(SPLIT_NEW_LINE);
            List<String> voltages = new ArrayList<>();
            for (String line : lines) {
                String[] voltageLine = line.split(SPLIT_LINE);
                if (voltageLine.length > 1) {
                    voltages.add(String.valueOf(Utils.strToFloat(voltageLine[1].trim()) / AVAILABLE_VOLTS_OFFSET));

                }
            }
            return voltages;
        }
        return null;
    }

    public static boolean hasVoltage() {
        if (AVAILABLE_VOLTS == null) {
            for (String file : sAvailableVolts.keySet()) {
                if (Utils.existFile(file)) {
                    AVAILABLE_VOLTS = file;
                    AVAILABLE_VOLTS_OFFSET = sAvailableVolts.get(file);
                    return true;
                }
            }
        }
        return AVAILABLE_VOLTS != null;
    }

    public static void setPowerPolicy(String value, Context context) {
        run(Control.write(value, POWER_POLICY), POWER_POLICY, context);
    }

    public static String getPowerPolicy() {
        String[] policies = Utils.readFile(POWER_POLICY).split(" ");
        for (String policy : policies) {
            if (policy.startsWith("[") && policy.endsWith("]")) {
                return policy.replace("[", "").replace("]", "");
            }
        }
        return "";
    }

    public static List<String> getPowerPolicies() {
        String[] policies = Utils.readFile(POWER_POLICY).split(" ");
        List<String> list = new ArrayList<>();
        for (String policy : policies) {
            list.add(policy.replace("[", "").replace("]", ""));
        }
        return list;
    }

    public static boolean hasPowerPolicy() {
        if (POWER_POLICY == null) {
            for (String file : sPowerPolicies.keySet()) {
                if (Utils.existFile(file)) {
                    POWER_POLICY = file;
                    POWER_POLICY_OFFSET = sPowerPolicies.get(file);
                    return true;
                }
            }
        }
        return POWER_POLICY != null;
    }

    public static int getVoltageOffset () {
        return AVAILABLE_VOLTS_OFFSET;
    }

    public static boolean supported() {
        return hasCurFreq() || hasVoltage()
                || (hasMaxFreq() && getAvailableFreqs() != null)
                || (hasMinFreq() && getAvailableFreqs() != null)
                || hasGovernor()
                || hasHighspeedClock() || hasHighspeedLoad() || hasHighspeedDelay()
                || hasPowerPolicy();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.GPU, id, context);
    }

}
