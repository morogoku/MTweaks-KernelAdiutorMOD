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
package com.moro.mtweaks.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moro.mtweaks.R;
import com.moro.mtweaks.activities.tools.profile.ProfileActivity;
import com.moro.mtweaks.fragments.kernel.BatteryFragment;
import com.moro.mtweaks.fragments.kernel.BoefflaWakelockFragment;
import com.moro.mtweaks.fragments.kernel.BusCamFragment;
import com.moro.mtweaks.fragments.kernel.BusDispFragment;
import com.moro.mtweaks.fragments.kernel.BusIntFragment;
import com.moro.mtweaks.fragments.kernel.BusMifFragment;
import com.moro.mtweaks.fragments.kernel.CPUFragment;
import com.moro.mtweaks.fragments.kernel.CPUHotplugFragment;
import com.moro.mtweaks.fragments.kernel.CPUVoltageCl0Fragment;
import com.moro.mtweaks.fragments.kernel.CPUVoltageCl1Fragment;
import com.moro.mtweaks.fragments.kernel.EntropyFragment;
import com.moro.mtweaks.fragments.kernel.GPUFragment;
import com.moro.mtweaks.fragments.kernel.DvfsFragment;
import com.moro.mtweaks.fragments.kernel.HmpFragment;
import com.moro.mtweaks.fragments.kernel.IOFragment;
import com.moro.mtweaks.fragments.kernel.KSMFragment;
import com.moro.mtweaks.fragments.kernel.LEDFragment;
import com.moro.mtweaks.fragments.kernel.LMKFragment;
import com.moro.mtweaks.fragments.kernel.WakelockFragment;
import com.moro.mtweaks.fragments.kernel.MiscFragment;
import com.moro.mtweaks.fragments.kernel.ScreenFragment;
import com.moro.mtweaks.fragments.kernel.SoundFragment;
import com.moro.mtweaks.fragments.kernel.ThermalFragment;
import com.moro.mtweaks.fragments.kernel.VMFragment;
import com.moro.mtweaks.fragments.kernel.WakeFragment;
import com.moro.mtweaks.fragments.recyclerview.RecyclerViewFragment;
import com.moro.mtweaks.utils.AppSettings;

import java.util.HashMap;

/**
 * Created by willi on 03.05.16.
 */
public class ApplyOnBootFragment extends BaseFragment {

    private static final String PACKAGE = ApplyOnBootFragment.class.getCanonicalName();
    private static final String INTENT_CATEGORY = PACKAGE + ".INTENT.CATEGORY";

    public static final String CPU = "cpu_onboot";
    public static final String CPU_CL0_VOLTAGE = "cpucl0voltage_onboot";
    public static final String CPU_CL1_VOLTAGE = "cpucl1voltage_onboot";
    public static final String CPU_HOTPLUG = "cpuhotplug_onboot";
    public static final String BUS_MIF = "busMif_onboot";
    public static final String BUS_INT = "busInt_onboot";
    public static final String BUS_CAM = "busCam_onboot";
    public static final String BUS_DISP = "busDisp_onboot";
    public static final String HMP = "hmp_onboot";
    public static final String THERMAL = "thermal_onboot";
    public static final String GPU = "gpu_onboot";
    public static final String DVFS = "dvfs_onboot";
    public static final String SCREEN = "screen_onboot";
    public static final String WAKE = "wake_onboot";
    public static final String SOUND = "sound_onboot";
    public static final String BATTERY = "battery_onboot";
    public static final String LED = "led_onboot";
    public static final String IO = "io_onboot";
    public static final String KSM = "ksm_onboot";
    public static final String LMK = "lmk_onboot";
    public static final String WAKELOCK = "wakelock_onboot";
    public static final String BOEFFLA_WAKELOCK = "boeffla_wakelock_onboot";
    public static final String VM = "vm_onboot";
    public static final String ENTROPY = "entropy_onboot";
    public static final String MISC = "misc_onboot";

    private static final HashMap<Class, String> sAssignments = new HashMap<>();

    static {
        sAssignments.put(CPUFragment.class, CPU);
        sAssignments.put(CPUVoltageCl0Fragment.class, CPU_CL0_VOLTAGE);
        sAssignments.put(CPUVoltageCl1Fragment.class, CPU_CL1_VOLTAGE);
        sAssignments.put(CPUHotplugFragment.class, CPU_HOTPLUG);
        sAssignments.put(BusMifFragment.class, BUS_MIF);
        sAssignments.put(BusIntFragment.class, BUS_INT);
        sAssignments.put(BusCamFragment.class, BUS_CAM);
        sAssignments.put(BusDispFragment.class, BUS_DISP);
        sAssignments.put(HmpFragment.class, HMP);
        sAssignments.put(ThermalFragment.class, THERMAL);
        sAssignments.put(GPUFragment.class, GPU);
        sAssignments.put(DvfsFragment.class, DVFS);
        sAssignments.put(ScreenFragment.class, SCREEN);
        sAssignments.put(WakeFragment.class, WAKE);
        sAssignments.put(SoundFragment.class, SOUND);
        sAssignments.put(BatteryFragment.class, BATTERY);
        sAssignments.put(LEDFragment.class, LED);
        sAssignments.put(IOFragment.class, IO);
        sAssignments.put(KSMFragment.class, KSM);
        sAssignments.put(LMKFragment.class, LMK);
        sAssignments.put(WakelockFragment.class, WAKELOCK);
        sAssignments.put(BoefflaWakelockFragment.class, BOEFFLA_WAKELOCK);
        sAssignments.put(VMFragment.class, VM);
        sAssignments.put(EntropyFragment.class, ENTROPY);
        sAssignments.put(MiscFragment.class, MISC);
    }

    public static String getAssignment(Class fragment) {
        if (!sAssignments.containsKey(fragment)) {
            throw new RuntimeException("Assignment key does not exists: " + fragment.getSimpleName());
        }
        return sAssignments.get(fragment);
    }

    public static ApplyOnBootFragment newInstance(RecyclerViewFragment recyclerViewFragment) {
        Bundle args = new Bundle();
        args.putString(INTENT_CATEGORY, getAssignment(recyclerViewFragment.getClass()));
        ApplyOnBootFragment fragment = new ApplyOnBootFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() instanceof ProfileActivity) {
            View rootView = inflater.inflate(R.layout.fragment_description, container, false);

            TextView title = rootView.findViewById(R.id.title);
            TextView summary = rootView.findViewById(R.id.summary);

            title.setText(getString(R.string.apply_on_boot));
            summary.setText(getString(R.string.apply_on_boot_not_available));

            return rootView;
        } else {
            View rootView = inflater.inflate(R.layout.fragment_apply_on_boot, container, false);

            final String category = getArguments().getString(INTENT_CATEGORY);
            SwitchCompat switcher = rootView.findViewById(R.id.switcher);
            switcher.setChecked(AppSettings.getBoolean(category, false, getActivity()));
            switcher.setOnCheckedChangeListener((buttonView, isChecked) ->
                    AppSettings.saveBoolean(category, isChecked, getActivity()));
            return rootView;
        }
    }

}
