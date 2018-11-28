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
package com.moro.mtweaks.fragments.kernel;

import android.content.Context;
import android.os.Vibrator;

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.fragments.recyclerview.RecyclerViewFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.misc.Misc;
import com.moro.mtweaks.utils.kernel.misc.PowerSuspend;
import com.moro.mtweaks.utils.kernel.misc.Pwm;
import com.moro.mtweaks.utils.kernel.misc.Selinux;
import com.moro.mtweaks.utils.kernel.misc.Vibration;
import com.moro.mtweaks.utils.kernel.misc.Wakelocks;
import com.moro.mtweaks.utils.root.RootUtils;
import com.moro.mtweaks.views.recyclerview.ButtonView;
import com.moro.mtweaks.views.recyclerview.ButtonView2;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.DescriptionView;
import com.moro.mtweaks.views.recyclerview.GenericSelectView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SeekBarView;
import com.moro.mtweaks.views.recyclerview.SelectView;
import com.moro.mtweaks.views.recyclerview.SwitchView;
import com.moro.mtweaks.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 29.06.16.
 */
public class MiscFragment extends RecyclerViewFragment {

    private Vibration mVibration;
    private Misc mMisc;

    @Override
    protected void init() {
        super.init();

        mVibration = Vibration.getInstance();
        mMisc = Misc.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (mVibration.supported()) {
            vibrationInit(items);
        }
        if (mMisc.hasLoggerEnable()) {
            loggerInit(items);
        }
        if (mMisc.hasCrc()) {
            crcInit(items);
        }
        fsyncInit(items);
        if (mMisc.hasGentleFairSleepers()) {
            gentlefairsleepersInit(items);
        }
        if (mMisc.hasArchPower()) {
            archPowerInit(items);
        }
        if (mMisc.hasMagiskBin()) {
            secureKernel(items);
        }
        if (Selinux.supported()){
            selinuxInit(items);
        }
        if (PowerSuspend.supported()) {
            powersuspendInit(items);
        }
        if (Pwm.supported()) {
            pwmInit(items);
        }
        networkInit(items);
        wakelockInit(items);
    }

    private void secureKernel(List<RecyclerViewItem> items){
        CardView skCard = new CardView(getActivity());
        skCard.setTitle(getString(R.string.secure_kernel_tit));

        SwitchView sk = new SwitchView();
        sk.setTitle(getString(R.string.secure_kernel_on));
        sk.setSummaryOn(getString(R.string.secure_kernel_on));
        sk.setSummaryOff(getString(R.string.secure_kernel_off));
        sk.setChecked(RootUtils.getProp("ro.secure").equals("1"));
        sk.addOnSwitchListener(((switchView, isChecked) ->
                mMisc.setProp("ro.secure", isChecked, getActivity())));
        skCard.addItem(sk);

        SwitchView adb = new SwitchView();
        adb.setTitle(getString(R.string.adb_secure_on));
        adb.setSummaryOn(getString(R.string.adb_secure_on));
        adb.setSummaryOff(getString(R.string.adb_secure_off));
        adb.setChecked(RootUtils.getProp("ro.adb.secure").equals("1"));
        adb.addOnSwitchListener(((switchView, isChecked) -> {
                mMisc.setProp("ro.adb.secure", isChecked, getActivity());
                mMisc.setProp("persist.service.adb.enable", isChecked, getActivity());
                Utils.toast(getString(R.string.restart_usb_toast), getActivity());
                RootUtils.runCommand("stop adbd");
                RootUtils.runCommand("start adbd");
        }));

        skCard.addItem(adb);

        SwitchView debug = new SwitchView();
        debug.setTitle(getString(R.string.debug_on));
        debug.setSummaryOn(getString(R.string.debug_on));
        debug.setSummaryOff(getString(R.string.debug_off));
        debug.setChecked(RootUtils.getProp("ro.debuggable").equals("1"));
        debug.addOnSwitchListener(((switchView, isChecked) -> {
                mMisc.setProp("ro.debuggable", isChecked, getActivity());
                mMisc.setProp("persist.service.debuggable", isChecked, getActivity());
                Utils.toast(getString(R.string.restart_usb_toast), getActivity());
                RootUtils.runCommand("stop adbd");
                RootUtils.runCommand("start adbd");
        }));
        skCard.addItem(debug);

        if (skCard.size() > 0) {
            items.add(skCard);
        }
    }

    private void selinuxInit(List<RecyclerViewItem> items){
        CardView sl = new CardView(getActivity());
        sl.setTitle(getString(R.string.selinux));

        final SelectView mode = new SelectView();
        mode.setTitle(getString(R.string.selinux));
        mode.setSummary(getString(R.string.selinux_summary));
        mode.setItems(Arrays.asList(getResources().getStringArray(R.array.selinux_states)));
        mode.setItem(Selinux.getStringEnforceMode());
        mode.setOnItemSelected((selectView, position, item) -> {
            Selinux.setEnforceMode(position, getActivity());
            getHandler().postDelayed(() -> {
                mode.setItem(Selinux.getStringEnforceMode());
                if (position != Selinux.getEnforceMode())
                    Utils.toast(getString(R.string.selinux_no_kernel_toast), getActivity());
            }, 500);
        });

        sl.addItem(mode);

        if (sl.size() > 0) {
            items.add(sl);
        }
    }

    private void vibrationInit(List<RecyclerViewItem> items) {
        final Vibrator vibrator = (Vibrator) Objects.requireNonNull(getActivity())
                .getSystemService(Context.VIBRATOR_SERVICE);

        final int min = mVibration.getMin();
        int max = mVibration.getMax();
        final float offset = (max - min) / 100f;

        SeekBarView vibration = new SeekBarView();
        vibration.setTitle(getString(R.string.vibration_strength));
        vibration.setUnit("%");
        vibration.setProgress(Math.round((mVibration.get() - min) / offset));
        vibration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mVibration.setVibration(Math.round(position * offset + min), getActivity());
                getHandler().postDelayed(() -> {
                    if (vibrator != null) {
                        vibrator.vibrate(300);
                    }
                }, 250);
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(vibration);
    }

    private void loggerInit(List<RecyclerViewItem> items) {
        SwitchView logger = new SwitchView();
        logger.setSummary(getString(R.string.android_logger));
        logger.setChecked(mMisc.isLoggerEnabled());
        logger.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enableLogger(isChecked, getActivity()));

        items.add(logger);
    }

    private void crcInit(List<RecyclerViewItem> items) {
        SwitchView crc = new SwitchView();
        crc.setTitle(getString(R.string.crc));
        crc.setSummary(getString(R.string.crc_summary));
        crc.setChecked(mMisc.isCrcEnabled());
        crc.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enableCrc(isChecked, getActivity()));

        items.add(crc);
    }

    private void fsyncInit(List<RecyclerViewItem> items) {
        if (mMisc.hasFsync()) {
            SwitchView fsync = new SwitchView();
            fsync.setTitle(getString(R.string.fsync));
            fsync.setSummary(getString(R.string.fsync_summary));
            fsync.setChecked(mMisc.isFsyncEnabled());
            fsync.addOnSwitchListener((switchView, isChecked)
                    -> mMisc.enableFsync(isChecked, getActivity()));

            items.add(fsync);
        }

        if (mMisc.hasDynamicFsync()) {
            SwitchView dynamicFsync = new SwitchView();
            dynamicFsync.setTitle(getString(R.string.dynamic_fsync));
            dynamicFsync.setSummary(getString(R.string.dynamic_fsync_summary));
            dynamicFsync.setChecked(mMisc.isDynamicFsyncEnabled());
            dynamicFsync.addOnSwitchListener((switchView, isChecked)
                    -> mMisc.enableDynamicFsync(isChecked, getActivity()));

            items.add(dynamicFsync);
        }
    }

    private void gentlefairsleepersInit(List<RecyclerViewItem> items) {
        SwitchView gentleFairSleepers = new SwitchView();
        gentleFairSleepers.setTitle(getString(R.string.gentlefairsleepers));
        gentleFairSleepers.setSummary(getString(R.string.gentlefairsleepers_summary));
        gentleFairSleepers.setChecked(mMisc.isGentleFairSleepersEnabled());
        gentleFairSleepers.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enableGentleFairSleepers(isChecked, getActivity()));

        items.add(gentleFairSleepers);
    }

    private void archPowerInit(List<RecyclerViewItem> items) {
        SwitchView archPower = new SwitchView();
        archPower.setTitle(getString(R.string.arch_power));
        archPower.setSummary(getString(R.string.arch_power_summary));
        archPower.setChecked(mMisc.isArchPowerEnabled());
        archPower.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enableArchPower(isChecked, getActivity()));

        items.add(archPower);
    }

    private void pwmInit(List<RecyclerViewItem> items) {
        CardView pwmCard = new CardView(getActivity());
        pwmCard.setTitle(getString(R.string.pwm));

        SwitchView enable = new SwitchView();
        enable.setTitle(getString(R.string.pwm));
        enable.setSummary(getString(R.string.pwm_summary));
        enable.setChecked(Pwm.isPwmEnabled());
        enable.addOnSwitchListener((switchView, isChecked) -> Pwm.enablePwm(isChecked, getActivity()));

        pwmCard.addItem(enable);

        items.add(pwmCard);
    }
    private void powersuspendInit(List<RecyclerViewItem> items) {
        String v = PowerSuspend.getVersion().replace("version: ", " v");
        CardView ps = new CardView(getActivity());
        ps.setTitle(getString(R.string.power_suspend) + v);

        if (PowerSuspend.hasMode()) {
            SelectView mode = new SelectView();
            mode.setTitle(getString(R.string.power_suspend_mode));
            mode.setSummary(getString(R.string.power_suspend_mode_summary));
            if (v.contains("1.5") || v.contains("1.8")) {
                mode.setItems(Arrays.asList(getResources().getStringArray(R.array.powersuspend_items)));
            } else {
                mode.setItems(Arrays.asList(getResources().getStringArray(R.array.powersuspend_items_lite)));
            }
            mode.setItem(PowerSuspend.getMode());
            mode.setOnItemSelected((selectView, position, item)
                    -> PowerSuspend.setMode(position, getActivity()));

            ps.addItem(mode);
        }

        if (PowerSuspend.hasState()) {
            final SwitchView state = new SwitchView();
            state.setTitle(getString(R.string.power_suspend_state));
            state.setSummary(getString(R.string.power_suspend_state_summary));
            state.setChecked(PowerSuspend.isStateEnabled());
            state.addOnSwitchListener((switchView, isChecked) -> {
                PowerSuspend.enableState(isChecked, getActivity());
                getHandler().postDelayed(() -> {
                    state.setChecked(PowerSuspend.isStateEnabled());
                    if (isChecked != PowerSuspend.isStateEnabled())
                        Utils.toast(getString(R.string.power_suspend_state_toast), getActivity());
                }, 500);
            });

            ps.addItem(state);
        }

        if (ps.size() > 0) {
            items.add(ps);
        }
    }

    private void networkInit(List<RecyclerViewItem> items) {
        CardView networkCard = new CardView(getActivity());
        networkCard.setTitle(getString(R.string.network));

        try {
            SelectView tcp = new SelectView();
            tcp.setTitle(getString(R.string.tcp));
            tcp.setSummary(getString(R.string.tcp_summary));
            tcp.setItems(mMisc.getTcpAvailableCongestions());
            tcp.setItem(mMisc.getTcpCongestion());
            tcp.setOnItemSelected((selectView, position, item)
                    -> mMisc.setTcpCongestion(item, getActivity()));

            networkCard.addItem(tcp);
        } catch (Exception ignored) {
        }

        GenericSelectView hostname = new GenericSelectView();
        hostname.setSummary(getString(R.string.hostname));
        hostname.setValue(mMisc.getHostname());
        hostname.setValueRaw(hostname.getValue());
        hostname.setOnGenericValueListener((genericSelectView, value)
                -> mMisc.setHostname(value, getActivity()));

        networkCard.addItem(hostname);

        if (Misc.hasWireguard()) {
            DescriptionView wireguard = new DescriptionView();
            wireguard.setTitle(getString(R.string.wireguard_title));
            wireguard.setSummary(getString(R.string.version) + ": " + Misc.getWireguard());

            networkCard.addItem(wireguard);
        }

        items.add(networkCard);
    }

    private void wakelockInit(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> wakelocks = new ArrayList<>();

        for (final Wakelocks.Wakelock wakelock : Wakelocks.getWakelocks()) {
            if (!wakelock.exists()) continue;

            String description = wakelock.getDescription(getActivity());

            SwitchView switchView = new SwitchView();
            if (description == null) {
                switchView.setSummary(wakelock.getTitle(getActivity()));
            } else {
                switchView.setTitle(wakelock.getTitle(getActivity()));
                switchView.setSummary(description);
            }
            switchView.setChecked(wakelock.isEnabled());
            switchView.addOnSwitchListener((switchView1, isChecked)
                    -> wakelock.enable(isChecked, getActivity()));

            wakelocks.add(switchView);
        }

        if (Wakelocks.hasWlanrxDivider()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 17; i++) {
                list.add((100 / i) + "%");
            }
            list.add("0%");

            SeekBarView wlanrxDivider = new SeekBarView();
            wlanrxDivider.setTitle(getString(R.string.wlan_rx_wakelock_divider));
            wlanrxDivider.setItems(list);
            wlanrxDivider.setProgress(Wakelocks.getWlanrxDivider());
            wlanrxDivider.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Wakelocks.setWlanrxDivider(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            wakelocks.add(wlanrxDivider);
        }

        if (Wakelocks.hasWlanctrlDivider()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 17; i++) {
                list.add((100 / i) + "%");
            }
            list.add("0%");

            SeekBarView wlanctrlDivider = new SeekBarView();
            wlanctrlDivider.setTitle(getString(R.string.wlan_ctrl_wakelock_divider));
            wlanctrlDivider.setItems(list);
            wlanctrlDivider.setProgress(Wakelocks.getWlanctrlDivider());
            wlanctrlDivider.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Wakelocks.setWlanctrlDivider(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            wakelocks.add(wlanctrlDivider);
        }

        if (Wakelocks.hasMsmHsicDivider()) {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < 17; i++) {
                list.add((100 / i) + "%");
            }
            list.add("0%");

            SeekBarView msmHsicDivider = new SeekBarView();
            msmHsicDivider.setTitle(getString(R.string.msm_hsic_wakelock_divider));
            msmHsicDivider.setItems(list);
            msmHsicDivider.setProgress(Wakelocks.getMsmHsicDivider());
            msmHsicDivider.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Wakelocks.setMsmHsicDivider(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            wakelocks.add(msmHsicDivider);
        }

        if (Wakelocks.hasBCMDHDDivider()) {
            SeekBarView bcmdhdDivider = new SeekBarView();
            bcmdhdDivider.setTitle(getString(R.string.bcmdhd_wakelock_divider));
            bcmdhdDivider.setMax(9);
            bcmdhdDivider.setMin(1);
            bcmdhdDivider.setProgress(Wakelocks.getBCMDHDDivider() - 1);
            bcmdhdDivider.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Wakelocks.setBCMDHDDivider(position + 1, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            wakelocks.add(bcmdhdDivider);
        }

        if (wakelocks.size() > 0) {
            TitleView wakelockTitle = new TitleView();
            wakelockTitle.setText(getString(R.string.wakelock));

            items.add(wakelockTitle);
            items.addAll(wakelocks);
        }
    }

}
