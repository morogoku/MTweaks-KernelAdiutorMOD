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
package com.moro.mtweaks.utils.kernel.vm;

import android.content.Context;

import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class VM {

    private static final String PATH = "/proc/sys/vm";
    private static List<String> list;
    private static final List<String> COMMON_VM = Arrays.asList("swappiness", "dirty_ratio", "dirty_background_ratio",
            "dirty_expire_centisecs", "dirty_writeback_centisecs", "min_free_kbytes", "oom_kill_allocating_task",
            "overcommit_ratio", "vfs_cache_pressure", "laptop_mode", "extra_free_kbytes");
    private static final List<String> REMOVED_VM = Arrays.asList("swappiness");
    private static final List<String> ALL_VM = getAllSupportedVm();


    private static List<String> getAllSupportedVm() {
        List<String> listVm = new ArrayList<>();
        listVm.add("swappiness");

        File f = new File(PATH);
        if (f.exists()){
            File[] ficheros = f.listFiles();
            for (File fichero : ficheros) {
                boolean blocked = false;
                for (String vm : REMOVED_VM) {
                    if (fichero.getName().contentEquals(vm)) {
                        blocked = true;
                        break;
                    }
                }
                if (!blocked) listVm.add(fichero.getName());
            }
        }
        return listVm;
    }

    public static void setValue(String value, int position, Context context, boolean completeList) {
        if (completeList) list = ALL_VM;
        else list = COMMON_VM;

        run(Control.write(value, PATH + "/" + list.get(position)), PATH + "/" +
                list.get(position), context);
    }

    public static String getValue(int position, boolean completeList) {
        if (completeList) list = ALL_VM;
        else list = COMMON_VM;

        return Utils.readFile(PATH + "/" + list.get(position));
    }

    public static String getName(int position, boolean completeList) {
        if (completeList) list = ALL_VM;
        else list = COMMON_VM;

        return list.get(position);
    }

    public static boolean exists(int position, boolean completeList) {
        if (completeList) list = ALL_VM;
        else list = COMMON_VM;

        return Utils.existFile(PATH + "/" + list.get(position));
    }

    public static int size(boolean completeList) {
        if (completeList) list = ALL_VM;
        else list = COMMON_VM;

        return list.size();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.VM, id, context);
    }

}
