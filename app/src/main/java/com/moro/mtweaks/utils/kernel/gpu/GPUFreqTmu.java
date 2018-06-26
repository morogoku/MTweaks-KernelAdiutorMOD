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

import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morogoku on 26.06.18.
 */
public class GPUFreqTmu {

    private static final String TMU_S7 = "/sys/devices/14ac0000.mali/tmu";
    private static final String THROTTLING1_S7 = "/sys/devices/14ac0000.mali/throttling1";
    private static final String THROTTLING2_S7 = "/sys/devices/14ac0000.mali/throttling2";
    private static final String THROTTLING3_S7 = "/sys/devices/14ac0000.mali/throttling3";
    private static final String THROTTLING4_S7 = "/sys/devices/14ac0000.mali/throttling4";
    private static final String TRIPPING_S7 = "/sys/devices/14ac0000.mali/tripping";

    private static final String TMU_S8 = "/sys/devices/platform/13900000.mali/tmu";
    private static final String THROTTLING1_S8 = "/sys/devices/platform/13900000.mali/throttling1";
    private static final String THROTTLING2_S8 = "/sys/devices/platform/13900000.mali/throttling2";
    private static final String THROTTLING3_S8 = "/sys/devices/platform/13900000.mali/throttling3";
    private static final String THROTTLING4_S8 = "/sys/devices/platform/13900000.mali/throttling4";
    private static final String TRIPPING_S8 = "/sys/devices/platform/13900000.mali/tripping";

    private static final String TMU_S9 = "/sys/devices/platform/17500000.mali/tmu";
    private static final String THROTTLING1_S9 = "/sys/devices/platform/17500000.mali/throttling1";
    private static final String THROTTLING2_S9 = "/sys/devices/platform/17500000.mali/throttling2";
    private static final String THROTTLING3_S9 = "/sys/devices/platform/17500000.mali/throttling3";
    private static final String THROTTLING4_S9 = "/sys/devices/platform/17500000.mali/throttling4";
    private static final String TRIPPING_S9 = "/sys/devices/platform/17500000.mali/tripping";

    private static final List<String> sTmu = new ArrayList<>();
    private static final List<String> sThrottling1 = new ArrayList<>();
    private static final List<String> sThrottling2 = new ArrayList<>();
    private static final List<String> sThrottling3 = new ArrayList<>();
    private static final List<String> sThrottling4 = new ArrayList<>();
    private static final List<String> sTripping = new ArrayList<>();


    static {
        sTmu.add(TMU_S7);
        sTmu.add(TMU_S8);
        sTmu.add(TMU_S9);

        sThrottling1.add(THROTTLING1_S7);
        sThrottling1.add(THROTTLING1_S8);
        sThrottling1.add(THROTTLING1_S9);

        sThrottling2.add(THROTTLING2_S7);
        sThrottling2.add(THROTTLING2_S8);
        sThrottling2.add(THROTTLING2_S9);

        sThrottling3.add(THROTTLING3_S7);
        sThrottling3.add(THROTTLING3_S8);
        sThrottling3.add(THROTTLING3_S9);

        sThrottling4.add(THROTTLING4_S7);
        sThrottling4.add(THROTTLING4_S8);
        sThrottling4.add(THROTTLING4_S9);

        sTripping.add(TRIPPING_S7);
        sTripping.add(TRIPPING_S8);
        sTripping.add(TRIPPING_S9);
        }

    private static String TMU;
    private static String THROTTLING1;
    private static String THROTTLING2;
    private static String THROTTLING3;
    private static String THROTTLING4;
    private static String TRIPPING;


    public static void enableTmu(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", TMU), TMU, context);
    }

    public static boolean isTmuEnabled() {
        return Utils.readFile(TMU).equals("1");
    }

    public static boolean hasTmu() {
        if (TMU == null) {
            for (String file : sTmu) {
                if (Utils.existFile(file)) {
                    TMU = file;
                    return true;
                }
            }
        }
        return TMU != null;
    }

    public static void setThrottling1(String value, Context context) {
        run(Control.write(value, THROTTLING1), THROTTLING1, context);
    }

    public static int getThrottling1() {
        return Utils.strToInt(Utils.readFile(THROTTLING1));
    }

    public static boolean hasThrottling1() {
        if (THROTTLING1 == null) {
            for (String file : sThrottling1) {
                if (Utils.existFile(file)) {
                    THROTTLING1 = file;
                    return true;
                }
            }
        }
        return THROTTLING1 != null;
    }

    public static void setThrottling2(String value, Context context) {
        run(Control.write(value, THROTTLING2), THROTTLING2, context);
    }

    public static int getThrottling2() {
        return Utils.strToInt(Utils.readFile(THROTTLING2));
    }

    public static boolean hasThrottling2() {
        if (THROTTLING2 == null) {
            for (String file : sThrottling2) {
                if (Utils.existFile(file)) {
                    THROTTLING2 = file;
                    return true;
                }
            }
        }
        return THROTTLING2 != null;
    }

    public static void setThrottling3(String value, Context context) {
        run(Control.write(value, THROTTLING3), THROTTLING3, context);
    }

    public static int getThrottling3() {
        return Utils.strToInt(Utils.readFile(THROTTLING3));
    }

    public static boolean hasThrottling3() {
        if (THROTTLING3 == null) {
            for (String file : sThrottling3) {
                if (Utils.existFile(file)) {
                    THROTTLING3 = file;
                    return true;
                }
            }
        }
        return THROTTLING3 != null;
    }

    public static void setThrottling4(String value, Context context) {
        run(Control.write(value, THROTTLING4), THROTTLING4, context);
    }

    public static int getThrottling4() {
        return Utils.strToInt(Utils.readFile(THROTTLING4));
    }

    public static boolean hasThrottling4() {
        if (THROTTLING4 == null) {
            for (String file : sThrottling4) {
                if (Utils.existFile(file)) {
                    THROTTLING4 = file;
                    return true;
                }
            }
        }
        return THROTTLING4 != null;
    }

    public static void setTripping(String value, Context context) {
        run(Control.write(value, TRIPPING), TRIPPING, context);
    }

    public static int getTripping() {
        return Utils.strToInt(Utils.readFile(TRIPPING));
    }

    public static boolean hasTripping() {
        if (TRIPPING == null) {
            for (String file : sTripping) {
                if (Utils.existFile(file)) {
                    TRIPPING = file;
                    return true;
                }
            }
        }
        return TRIPPING != null;
    }

    public static boolean supported() {
        return hasThrottling1() || hasThrottling2() || hasThrottling3() || hasThrottling4();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.GPU, id, context);
    }

}
