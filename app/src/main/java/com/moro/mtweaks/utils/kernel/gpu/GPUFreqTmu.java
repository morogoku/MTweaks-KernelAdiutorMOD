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

    private static GPUFreqTmu sIOInstance;

    public static GPUFreqTmu getInstance() {
        if (sIOInstance == null) {
            sIOInstance = new GPUFreqTmu();
        }
        return sIOInstance;
    }

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

    private final List<String> mTmu = new ArrayList<>();
    private final List<String> mThrottling1 = new ArrayList<>();
    private final List<String> mThrottling2 = new ArrayList<>();
    private final List<String> mThrottling3 = new ArrayList<>();
    private final List<String> mThrottling4 = new ArrayList<>();
    private final List<String> mTripping = new ArrayList<>();


    {
        mTmu.add(TMU_S7);
        mTmu.add(TMU_S8);
        mTmu.add(TMU_S9);

        mThrottling1.add(THROTTLING1_S7);
        mThrottling1.add(THROTTLING1_S8);
        mThrottling1.add(THROTTLING1_S9);

        mThrottling2.add(THROTTLING2_S7);
        mThrottling2.add(THROTTLING2_S8);
        mThrottling2.add(THROTTLING2_S9);

        mThrottling3.add(THROTTLING3_S7);
        mThrottling3.add(THROTTLING3_S8);
        mThrottling3.add(THROTTLING3_S9);

        mThrottling4.add(THROTTLING4_S7);
        mThrottling4.add(THROTTLING4_S8);
        mThrottling4.add(THROTTLING4_S9);

        mTripping.add(TRIPPING_S7);
        mTripping.add(TRIPPING_S8);
        mTripping.add(TRIPPING_S9);
    }

    private String TMU;
    private String THROTTLING1;
    private String THROTTLING2;
    private String THROTTLING3;
    private String THROTTLING4;
    private String TRIPPING;

    private GPUFreqTmu() {
        for (String file : mTmu) {
            if (Utils.existFile(file)) {
                TMU = file;
                break;
            }
        }

        for (String file : mThrottling1) {
            if (Utils.existFile(file)) {
                THROTTLING1 = file;
                break;
            }
        }

        for (String file : mThrottling2) {
            if (Utils.existFile(file)) {
                THROTTLING2 = file;
                break;
            }
        }

        for (String file : mThrottling3) {
            if (Utils.existFile(file)) {
                THROTTLING3 = file;
                break;
            }
        }

        for (String file : mThrottling4) {
            if (Utils.existFile(file)) {
                THROTTLING4 = file;
                break;
            }
        }

        for (String file : mTripping) {
            if (Utils.existFile(file)) {
                TRIPPING = file;
                break;
            }
        }
    }

    public void enableTmu(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", TMU), TMU, context);
    }

    public boolean isTmuEnabled() {
        return Utils.readFile(TMU).equals("1");
    }

    public boolean hasTmu() {
        return TMU != null;
    }

    public void setThrottling1(String value, Context context) {
        run(Control.write(value, THROTTLING1), THROTTLING1, context);
    }

    public int getThrottling1() {
        return Utils.strToInt(Utils.readFile(THROTTLING1));
    }

    public boolean hasThrottling1() {
        return THROTTLING1 != null;
    }

    public void setThrottling2(String value, Context context) {
        run(Control.write(value, THROTTLING2), THROTTLING2, context);
    }

    public int getThrottling2() {
        return Utils.strToInt(Utils.readFile(THROTTLING2));
    }

    public boolean hasThrottling2() {
        return THROTTLING2 != null;
    }

    public void setThrottling3(String value, Context context) {
        run(Control.write(value, THROTTLING3), THROTTLING3, context);
    }

    public int getThrottling3() {
        return Utils.strToInt(Utils.readFile(THROTTLING3));
    }

    public boolean hasThrottling3() {
        return THROTTLING3 != null;
    }

    public void setThrottling4(String value, Context context) {
        run(Control.write(value, THROTTLING4), THROTTLING4, context);
    }

    public int getThrottling4() {
        return Utils.strToInt(Utils.readFile(THROTTLING4));
    }

    public boolean hasThrottling4() {
        return THROTTLING4 != null;
    }

    public void setTripping(String value, Context context) {
        run(Control.write(value, TRIPPING), TRIPPING, context);
    }

    public int getTripping() {
        return Utils.strToInt(Utils.readFile(TRIPPING));
    }

    public boolean hasTripping() {
        return TRIPPING != null;
    }

    public boolean supported() {
        return hasThrottling1() || hasThrottling2() || hasThrottling3() || hasThrottling4()
                || hasTripping();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.GPU, id, context);
    }
}
