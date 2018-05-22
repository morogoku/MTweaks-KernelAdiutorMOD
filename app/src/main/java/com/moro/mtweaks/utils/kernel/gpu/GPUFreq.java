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

import java.util.ArrayList;
import java.util.Arrays;
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

    private static final String BACKUP = "/data/.mtweaks2/bk/gpu_stock_voltage";

    private static final String GENERIC_GOVERNORS = "performance powersave ondemand simple conservative";

    private static final String CUR_KGSL2D0_QCOM_FREQ = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/gpuclk";
    private static final String MAX_KGSL2D0_QCOM_FREQ = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/max_gpuclk";
    private static final String AVAILABLE_KGSL2D0_QCOM_FREQS = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/gpu_available_frequencies";
    private static final String SCALING_KGSL2D0_QCOM_GOVERNOR = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/pwrscale/trustzone/governor";

    private static final String KGSL3D0_GPUBUSY = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpubusy";
    private static final String CUR_KGSL3D0_FREQ = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpuclk";
    private static final String MAX_KGSL3D0_FREQ = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/max_gpuclk";
    private static final String AVAILABLE_KGSL3D0_FREQS = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpu_available_frequencies";
    private static final String SCALING_KGSL3D0_GOVERNOR = "/sys/class/kgsl/kgsl-3d0/pwrscale/trustzone/governor";

    private static final String MAX_S7_FREQ = "/sys/devices/14ac0000.mali/max_clock";
    private static final String MIN_S7_FREQ = "/sys/devices/14ac0000.mali/min_clock";
    private static final String CUR_S7_FREQ = "/sys/devices/14ac0000.mali/clock";
    private static final String AVAILABLE_S7_FREQS = "/sys/devices/14ac0000.mali/volt_table";
    private static final String AVAILABLE_S7_GOVERNORS = "/sys/devices/14ac0000.mali/dvfs_governor";
    private static final String TUNABLE_HIGHSPEED_CLOCK = "/sys/devices/14ac0000.mali/highspeed_clock";
    private static final String TUNABLE_HIGHSPEED_LOAD = "/sys/devices/14ac0000.mali/highspeed_load";
    private static final String TUNABLE_HIGHSPEED_DELAY = "/sys/devices/14ac0000.mali/highspeed_delay";
    private static final String POWER_POLICY = "/sys/devices/14ac0000.mali/power_policy";

    private static final String KGSL3D0_DEVFREQ_GPUBUSY = "/sys/class/kgsl/kgsl-3d0/gpubusy";
    private static final String CUR_KGSL3D0_DEVFREQ_FREQ = "/sys/class/kgsl/kgsl-3d0/gpuclk";
    private static final String MAX_KGSL3D0_DEVFREQ_FREQ = "/sys/class/kgsl/kgsl-3d0/max_gpuclk";
    private static final String MIN_KGSL3D0_DEVFREQ_FREQ = "/sys/class/kgsl/kgsl-3d0/devfreq/min_freq";
    private static final String AVAILABLE_KGSL3D0_DEVFREQ_FREQS = "/sys/class/kgsl/kgsl-3d0/gpu_available_frequencies";
    private static final String SCALING_KGSL3D0_DEVFREQ_GOVERNOR = "/sys/class/kgsl/kgsl-3d0/devfreq/governor";
    private static final String AVAILABLE_KGSL3D0_DEVFREQ_GOVERNORS = "/sys/class/kgsl/kgsl-3d0/devfreq/available_governors";

    private static final String CUR_OMAP_FREQ = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency";
    private static final String MAX_OMAP_FREQ = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency_limit";
    private static final String AVAILABLE_OMAP_FREQS = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency_list";
    private static final String SCALING_OMAP_GOVERNOR = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/governor";
    private static final String AVAILABLE_OMAP_GOVERNORS = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/governor_list";
    private static final String TUNABLES_OMAP = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/%s";

    private static final String CUR_TEGRA_FREQ = "/sys/kernel/tegra_gpu/gpu_rate";
    private static final String MAX_TEGRA_FREQ = "/sys/kernel/tegra_gpu/gpu_cap_rate";
    private static final String MIN_TEGRA_FREQ = "/sys/kernel/tegra_gpu/gpu_floor_rate";
    private static final String AVAILABLE_TEGRA_FREQS = "/sys/kernel/tegra_gpu/gpu_available_rates";

    private static final String CUR_POWERVR_FREQ = "/sys/devices/platform/dfrgx/devfreq/dfrgx/cur_freq";
    private static final String MAX_POWERVR_FREQ = "/sys/devices/platform/dfrgx/devfreq/dfrgx/max_freq";
    private static final String MIN_POWERVR_FREQ = "/sys/devices/platform/dfrgx/devfreq/dfrgx/min_freq";
    private static final String AVAILABLE_POWERVR_FREQS = "/sys/devices/platform/dfrgx/devfreq/dfrgx/available_frequencies";
    private static final String SCALING_POWERVR_GOVERNOR = "/sys/devices/platform/dfrgx/devfreq/dfrgx/governor";
    private static final String AVAILABLE_POWERVR_GOVERNORS = "/sys/devices/platform/dfrgx/devfreq/dfrgx/available_governors";

    private static final List<String> sGpuBusys = new ArrayList<>();
    private static final HashMap<String, Integer> sCurrentFreqs = new HashMap<>();
    private static final HashMap<String, Integer> sMaxFreqs = new HashMap<>();
    private static final HashMap<String, Integer> sMinFreqs = new HashMap<>();
    private static final HashMap<String, Integer> sAvailableFreqs = new HashMap<>();
    private static final List<String> sScalingGovernors = new ArrayList<>();
    private static final List<String> sAvailableGovernors = new ArrayList<>();
    private static final List<String> sTunables = new ArrayList<>();

    static {
        sGpuBusys.add(KGSL3D0_GPUBUSY);
        sGpuBusys.add(KGSL3D0_DEVFREQ_GPUBUSY);

        sCurrentFreqs.put(CUR_KGSL3D0_FREQ, 1000000);
        sCurrentFreqs.put(CUR_KGSL3D0_DEVFREQ_FREQ, 1000000);
        sCurrentFreqs.put(CUR_OMAP_FREQ, 1000000);
        sCurrentFreqs.put(CUR_TEGRA_FREQ, 1000000);
        sCurrentFreqs.put(CUR_POWERVR_FREQ, 1000);
        sCurrentFreqs.put(CUR_S7_FREQ, 1);

        sMaxFreqs.put(MAX_KGSL3D0_FREQ, 1000000);
        sMaxFreqs.put(MAX_KGSL3D0_DEVFREQ_FREQ, 1000000);
        sMaxFreqs.put(MAX_OMAP_FREQ, 1000000);
        sMaxFreqs.put(MAX_TEGRA_FREQ, 1000000);
        sMaxFreqs.put(MAX_POWERVR_FREQ, 1000);
        sMaxFreqs.put(MAX_S7_FREQ, 1);

        sMinFreqs.put(MIN_KGSL3D0_DEVFREQ_FREQ, 1000000);
        sMinFreqs.put(MIN_TEGRA_FREQ, 1000000);
        sMinFreqs.put(MIN_POWERVR_FREQ, 1000);
        sMinFreqs.put(MIN_S7_FREQ, 1);

        sAvailableFreqs.put(AVAILABLE_KGSL3D0_FREQS, 1000000);
        sAvailableFreqs.put(AVAILABLE_KGSL3D0_DEVFREQ_FREQS, 1000000);
        sAvailableFreqs.put(AVAILABLE_OMAP_FREQS, 1000000);
        sAvailableFreqs.put(AVAILABLE_TEGRA_FREQS, 1000000);
        sAvailableFreqs.put(AVAILABLE_POWERVR_FREQS, 1000);
        sAvailableFreqs.put(AVAILABLE_S7_FREQS, 1);

        sScalingGovernors.add(SCALING_KGSL3D0_GOVERNOR);
        sScalingGovernors.add(SCALING_KGSL3D0_DEVFREQ_GOVERNOR);
        sScalingGovernors.add(SCALING_OMAP_GOVERNOR);
        sScalingGovernors.add(SCALING_POWERVR_GOVERNOR);
        sScalingGovernors.add(AVAILABLE_S7_GOVERNORS);

        sAvailableGovernors.add(AVAILABLE_KGSL3D0_DEVFREQ_GOVERNORS);
        sAvailableGovernors.add(AVAILABLE_OMAP_GOVERNORS);
        sAvailableGovernors.add(AVAILABLE_POWERVR_GOVERNORS);

        sTunables.add(TUNABLES_OMAP);
    }

    private static String BUSY;
    private static String CUR_FREQ;
    private static Integer CUR_FREQ_OFFSET;
    private static List<Integer> AVAILABLE_FREQS;
    private static List<Integer> AVAILABLE_FREQS_SORT;
    private static String MAX_FREQ;
    private static Integer MAX_FREQ_OFFSET;
    private static String MIN_FREQ;
    private static Integer MIN_FREQ_OFFSET;
    private static String GOVERNOR;
    private static String[] AVAILABLE_GOVERNORS;
    private static Integer AVAILABLE_GOVERNORS_OFFSET;
    private static String TUNABLES;

    private static String SPLIT_NEW_LINE = "\\r?\\n";
    private static String SPLIT_LINE = " ";
    private static Integer VOLT_OFFSET = 1000;

    private static Integer[] AVAILABLE_2D_FREQS;

    public static String getTunables(String governor) {
        return Utils.strFormat(TUNABLES, governor);
    }

    public static boolean hasTunables(String governor) {
        if (TUNABLES != null) return true;
        for (String tunables : sTunables) {
            if (Utils.existFile(Utils.strFormat(tunables, governor))) {
                TUNABLES = tunables;
                return true;
            }
        }
        return false;
    }

    public static void set2dGovernor(String value, Context context) {
        run(Control.write(value, SCALING_KGSL2D0_QCOM_GOVERNOR), SCALING_KGSL2D0_QCOM_GOVERNOR, context);
    }

    public static String get2dGovernor() {
        return Utils.readFile(SCALING_KGSL2D0_QCOM_GOVERNOR);
    }

    public static List<String> get2dAvailableGovernors() {
        return Arrays.asList(GENERIC_GOVERNORS.split(" "));
    }

    public static boolean has2dGovernor() {
        return Utils.existFile(SCALING_KGSL2D0_QCOM_GOVERNOR);
    }

    public static void set2dMaxFreq(int value, Context context) {
        run(Control.write(String.valueOf(value), MAX_KGSL2D0_QCOM_FREQ), MAX_KGSL2D0_QCOM_FREQ, context);
    }

    public static int get2dMaxFreq() {
        return Utils.strToInt(Utils.readFile(MAX_KGSL2D0_QCOM_FREQ));
    }

    public static boolean has2dMaxFreq() {
        return Utils.existFile(MAX_KGSL2D0_QCOM_FREQ);
    }

    public static List<String> get2dAdjustedFreqs(Context context) {
        List<String> list = new ArrayList<>();
        for (int freq : get2dAvailableFreqs()) {
            list.add((freq / 1000000) + context.getString(R.string.mhz));
        }
        return list;
    }

    public static List<Integer> get2dAvailableFreqs() {
        if (AVAILABLE_2D_FREQS == null) {
            if (Utils.existFile(AVAILABLE_KGSL2D0_QCOM_FREQS)) {
                String[] freqs = Utils.readFile(AVAILABLE_KGSL2D0_QCOM_FREQS).split(" ");
                AVAILABLE_2D_FREQS = new Integer[freqs.length];
                for (int i = 0; i < freqs.length; i++) {
                    AVAILABLE_2D_FREQS[i] = Utils.strToInt(freqs[i]);
                }
            }
        }
        if (AVAILABLE_2D_FREQS == null) return null;
        List<Integer> list = Arrays.asList(AVAILABLE_2D_FREQS);
        Collections.sort(list);
        return list;
    }

    public static int get2dCurFreq() {
        return Utils.strToInt(Utils.readFile(CUR_KGSL2D0_QCOM_FREQ));
    }

    public static boolean has2dCurFreq() {
        return Utils.existFile(CUR_KGSL2D0_QCOM_FREQ);
    }

    public static void setS7Governor(String value, Context context) {
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

    public static void setGovernor(String value, Context context) {
        run(Control.write(value, GOVERNOR), GOVERNOR, context);
    }

    public static List<String> getAvailableGovernors() {
        if (AVAILABLE_GOVERNORS == null) {
            for (String file : sAvailableGovernors) {
                if (Utils.existFile(file)) {
                    AVAILABLE_GOVERNORS = Utils.readFile(file).split(" ");
                    break;
                }
            }
        }
        if (AVAILABLE_GOVERNORS == null) {
            AVAILABLE_GOVERNORS = GENERIC_GOVERNORS.split(" ");
        }
        return Arrays.asList(AVAILABLE_GOVERNORS);
    }

    public static List<String> getAvailableS7Governors() {
        String value = Utils.readFile(AVAILABLE_S7_GOVERNORS);
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

    public static String getS7Governor() {
        String value = Utils.readFile(AVAILABLE_S7_GOVERNORS);
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

    public static String getGovernor() {
        return Utils.readFile(GOVERNOR);
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
        if (getAvailableS7Freqs() != null) {
            for (int freq : getAvailableS7Freqs()) {
                list.add((freq / AVAILABLE_GOVERNORS_OFFSET) + context.getString(R.string.mhz));
            }
        }
        return list;
    }

    public static List<Integer> getAvailableS7Freqs() {
        if (AVAILABLE_FREQS == null) {
            for (String file : sAvailableFreqs.keySet()) {
                if (Utils.existFile(file)) {
                    String freqs[] = Utils.readFile(file).split("\\r?\\n");
                    AVAILABLE_FREQS = new ArrayList<>();
                    for (String freq : freqs) {
                        String[] freqLine = freq.split(" ");
                        AVAILABLE_FREQS.add(Utils.strToInt(freqLine[0].trim()));
                    }
                    AVAILABLE_GOVERNORS_OFFSET = sAvailableFreqs.get(file);
                    break;
                }
            }
        }
        if (AVAILABLE_FREQS == null) return null;
        return AVAILABLE_FREQS;
    }

    public static List<Integer> getAvailableS7FreqsSort() {
        if (AVAILABLE_FREQS_SORT == null) {
            for (String file : sAvailableFreqs.keySet()) {
                if (Utils.existFile(file)) {
                    String freqs[] = Utils.readFile(file).split("\\r?\\n");
                    AVAILABLE_FREQS_SORT = new ArrayList<>();
                    for (String freq : freqs) {
                        String[] freqLine = freq.split(" ");
                        AVAILABLE_FREQS_SORT.add(Utils.strToInt(freqLine[0].trim()));
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

    public static List<Integer> getAvailableFreqs() {
        if (AVAILABLE_FREQS == null) {
            for (String file : sAvailableFreqs.keySet()) {
                if (Utils.existFile(file)) {
                    String freqs[] = Utils.readFile(file).split(" ");
                    AVAILABLE_FREQS = new ArrayList<>();
                    for (String freq : freqs) {
                        if (!AVAILABLE_FREQS.contains(Utils.strToInt(freq))) {
                            AVAILABLE_FREQS.add(Utils.strToInt(freq));
                        }
                    }
                    AVAILABLE_GOVERNORS_OFFSET = sAvailableFreqs.get(file);
                    break;
                }
            }
        }
        if (AVAILABLE_FREQS == null) return null;
        Collections.sort(AVAILABLE_FREQS);
        return AVAILABLE_FREQS;
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

    public static int getBusy() {
        String value = Utils.readFile(BUSY);
        float arg1 = Utils.strToFloat(value.split(" ")[0]);
        float arg2 = Utils.strToFloat(value.split(" ")[1]);
        return arg2 == 0 ? 0 : Math.round(arg1 / arg2 * 100f);
    }

    public static boolean hasBusy() {
        if (BUSY == null) {
            for (String file : sGpuBusys) {
                if (Utils.existFile(file)) {
                    BUSY = file;
                    return true;
                }
            }
        }
        return BUSY != null;
    }

    public static int getHighspeedClock() {
        return Utils.strToInt(Utils.readFile(TUNABLE_HIGHSPEED_CLOCK));
    }

    public static void setHighspeedClock(String value, Context context) {
        run(Control.write(value, TUNABLE_HIGHSPEED_CLOCK), TUNABLE_HIGHSPEED_CLOCK, context);
    }

    public static boolean hasHighspeedClock() {
        return Utils.existFile(TUNABLE_HIGHSPEED_CLOCK);
    }

    public static int getHighspeedLoad() {
        return Utils.strToInt(Utils.readFile(TUNABLE_HIGHSPEED_LOAD));
    }

    public static void setHighspeedLoad(int value, Context context) {
        run(Control.write(String.valueOf(value), TUNABLE_HIGHSPEED_LOAD), TUNABLE_HIGHSPEED_LOAD, context);
    }

    public static boolean hasHighspeedLoad() {
        return Utils.existFile(TUNABLE_HIGHSPEED_LOAD);
    }

    public static int getHighspeedDelay() {
        return Utils.strToInt(Utils.readFile(TUNABLE_HIGHSPEED_DELAY));
    }

    public static void setHighspeedDelay(int value, Context context) {
        run(Control.write(String.valueOf(value), TUNABLE_HIGHSPEED_DELAY), TUNABLE_HIGHSPEED_DELAY, context);
    }

    public static boolean hasHighspeedDelay() {
        return Utils.existFile(TUNABLE_HIGHSPEED_DELAY);
    }

    public static void setVoltage(Integer freq, String voltage, Context context) {

        String volt = String.valueOf((int)(Utils.strToFloat(voltage) * VOLT_OFFSET));
        run(Control.write(freq + " " + volt, AVAILABLE_S7_FREQS), AVAILABLE_S7_FREQS + freq, context);
    }

    public static List<String> getStockVoltages() {
        String value = Utils.readFile(BACKUP);
        if (!value.isEmpty()) {
            String[] lines = value.split(SPLIT_NEW_LINE);
            List<String> voltages = new ArrayList<>();
            for (String line : lines) {
                String[] voltageLine = line.split(SPLIT_LINE);
                if (voltageLine.length > 1) {
                    voltages.add(String.valueOf(Utils.strToFloat(voltageLine[1].trim()) / VOLT_OFFSET));

                }
            }
            return voltages;
        }
        return null;
    }

    public static List<String> getVoltages() {
        String value = Utils.readFile(AVAILABLE_S7_FREQS);
        if (!value.isEmpty()) {
            String[] lines = value.split(SPLIT_NEW_LINE);
            List<String> voltages = new ArrayList<>();
            for (String line : lines) {
                String[] voltageLine = line.split(SPLIT_LINE);
                if (voltageLine.length > 1) {
                    voltages.add(String.valueOf(Utils.strToFloat(voltageLine[1].trim()) / VOLT_OFFSET));

                }
            }
            return voltages;
        }
        return null;
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
        return Utils.existFile(POWER_POLICY);
    }

    public static int getOffset () {
        return VOLT_OFFSET;
    }

    public static boolean hasBackup() {
        return Utils.existFile(BACKUP);
    }

    public static boolean supported() {
        return hasCurFreq()
                || (hasMaxFreq() && getAvailableS7Freqs() != null)
                || (hasMinFreq() && getAvailableS7Freqs() != null)
                || hasGovernor()
                || has2dCurFreq()
                || (has2dMaxFreq() && get2dAvailableFreqs() != null)
                || has2dGovernor();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.GPU, id, context);
    }

}
