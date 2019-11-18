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

/**
 * Created by willi on 07.05.16.
 */
public class Hotplug {

    public static void disableAllHotplugs(Context context) {
        AlucardHotplug.enableAlucardHotplug(false, context);
        AutoSmp.enableAutoSmp(false, context);
        BluPlug.enableBluPlug(false, context);
        ClusterHotplug.enableClusterHotplug(false, context);
        CoreCtl.getInstance().enable(false, context);
        IntelliPlug.getInstance().enableIntelliPlug(false, context);
        LazyPlug.enable(false, context);
        MakoHotplug.enableMakoHotplug(false, context);
        MBHotplug.getInstance().enableMBHotplug(false, context);
        MPDecision.enableMpdecision(false, context);
        MSMHotplug.getInstance().enableMsmHotplug(false, context);
        SamsungPlug.enableSamsungPlug(false, context);
        ThunderPlug.enableThunderPlug(false, context);
        ZenDecision.enableZenDecision(false, context);
    }

    public static boolean supported() {
        return MPDecision.supported() || IntelliPlug.getInstance().supported() || LazyPlug.supported()
                || BluPlug.supported() || MSMHotplug.getInstance().supported() || MakoHotplug.supported()
                || MBHotplug.getInstance().supported() || AlucardHotplug.supported() || ThunderPlug.supported()
                || ZenDecision.supported() || AutoSmp.supported() || CoreCtl.getInstance().supported()
                || AiOHotplug.supported() || SamsungPlug.supported() || ClusterHotplug.supported();
    }

}
