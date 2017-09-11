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
import com.moro.mtweaks.fragments.RecyclerViewFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.misc.Misc;
import com.moro.mtweaks.utils.kernel.misc.PowerSuspend;
import com.moro.mtweaks.utils.kernel.misc.Pwm;
import com.moro.mtweaks.utils.kernel.misc.Vibration;
import com.moro.mtweaks.utils.kernel.misc.Wakelocks;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.GenericSelectView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SeekBarView;
import com.moro.mtweaks.views.recyclerview.SelectView;
import com.moro.mtweaks.views.recyclerview.SwitchView;
import com.moro.mtweaks.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class MiscFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (Vibration.supported()) {
            vibrationInit(items);
        }
        if (Misc.hasLoggerEnable()) {
            loggerInit(items);
        }
        if (Misc.hasCrc()) {
            crcInit(items);
        }
        fsyncInit(items);
        if (Misc.hasGentleFairSleepers()) {
            gentlefairsleepersInit(items);
        }
        if (Misc.hasArchPower()) {
            archPowerInit(items);
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

    private void vibrationInit(List<RecyclerViewItem> items) {
        final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        final int min = Vibration.getMin();
        int max = Vibration.getMax();
        final float offset = (max - min) / 100f;

        SeekBarView vibration = new SeekBarView();
        vibration.setTitle(getString(R.string.vibration_strength));
        vibration.setUnit("%");
        vibration.setProgress(Math.round((Vibration.get() - min) / offset));
        vibration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Vibration.setVibration(Math.round(position * offset + min), getActivity());
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (vibrator != null) {
                            vibrator.vibrate(300);
                        }
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
        logger.setChecked(Misc.isLoggerEnabled());
        logger.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enableLogger(isChecked, getActivity());
            }
        });

        items.add(logger);
    }

    private void crcInit(List<RecyclerViewItem> items) {
        SwitchView crc = new SwitchView();
        crc.setTitle(getString(R.string.crc));
        crc.setSummary(getString(R.string.crc_summary));
        crc.setChecked(Misc.isCrcEnabled());
        crc.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enableCrc(isChecked, getActivity());
            }
        });

        items.add(crc);
    }

    private void fsyncInit(List<RecyclerViewItem> items) {
        if (Misc.hasFsync()) {
            SwitchView fsync = new SwitchView();
            fsync.setTitle(getString(R.string.fsync));
            fsync.setSummary(getString(R.string.fsync_summary));
            fsync.setChecked(Misc.isFsyncEnabled());
            fsync.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Misc.enableFsync(isChecked, getActivity());
                }
            });

            items.add(fsync);
        }

        if (Misc.hasDynamicFsync()) {
            SwitchView dynamicFsync = new SwitchView();
            dynamicFsync.setTitle(getString(R.string.dynamic_fsync));
            dynamicFsync.setSummary(getString(R.string.dynamic_fsync_summary));
            dynamicFsync.setChecked(Misc.isDynamicFsyncEnabled());
            dynamicFsync.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Misc.enableDynamicFsync(isChecked, getActivity());
                }
            });

            items.add(dynamicFsync);
        }
    }

    private void gentlefairsleepersInit(List<RecyclerViewItem> items) {
        SwitchView gentleFairSleepers = new SwitchView();
        gentleFairSleepers.setTitle(getString(R.string.gentlefairsleepers));
        gentleFairSleepers.setSummary(getString(R.string.gentlefairsleepers_summary));
        gentleFairSleepers.setChecked(Misc.isGentleFairSleepersEnabled());
        gentleFairSleepers.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enableGentleFairSleepers(isChecked, getActivity());
            }
        });

        items.add(gentleFairSleepers);
    }

    private void archPowerInit(List<RecyclerViewItem> items) {
        SwitchView archPower = new SwitchView();
        archPower.setTitle(getString(R.string.arch_power));
        archPower.setSummary(getString(R.string.arch_power_summary));
        archPower.setChecked(Misc.isArchPowerEnabled());
        archPower.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enableArchPower(isChecked, getActivity());
            }
        });

        items.add(archPower);
    }

    private void pwmInit(List<RecyclerViewItem> items) {
        CardView pwmCard = new CardView(getActivity());
        pwmCard.setTitle(getString(R.string.pwm));

        SwitchView enable = new SwitchView();
        enable.setTitle(getString(R.string.pwm));
        enable.setSummary(getString(R.string.pwm_summary));
        enable.setChecked(Pwm.isPwmEnabled());
        enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Pwm.enablePwm(isChecked, getActivity());
            }
        });

        pwmCard.addItem(enable);

        items.add(pwmCard);
    }

    private void powersuspendInit(List<RecyclerViewItem> items) {
        CardView ps = new CardView(getActivity());
        ps.setTitle(getString(R.string.power_suspend));

        if (PowerSuspend.hasMode()) {
            String v = PowerSuspend.getVersion();
            SelectView mode = new SelectView();
            mode.setTitle(getString(R.string.power_suspend_mode));
            mode.setSummary(getString(R.string.power_suspend_mode_summary));
            if (v.contains("1.5") || v.contains("1.8")) {
                mode.setItems(Arrays.asList(getResources().getStringArray(R.array.powersuspend_items)));
            } else {
                mode.setItems(Arrays.asList(getResources().getStringArray(R.array.powersuspend_items_lite)));
            }
            mode.setItem(PowerSuspend.getMode());
            mode.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    PowerSuspend.setMode(position, getActivity());
                }
            });

            ps.addItem(mode);
        }

        if (PowerSuspend.hasOldState()) {
            final SwitchView state = new SwitchView();
            state.setTitle(getString(R.string.power_suspend_state));
            state.setSummary(getString(R.string.power_suspend_state_summary));
            state.setChecked(PowerSuspend.isOldStateEnabled());
            state.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    PowerSuspend.enableOldState(isChecked, getActivity());
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int mode = PowerSuspend.getMode();
                            boolean st = PowerSuspend.isOldStateEnabled();
                            state.setChecked(st);
                            if (!st && mode != 1) Utils.toast(getString(R.string.power_suspend_state_toast),
                                    getActivity());
                        }
                    }, 200);
                }
            });

            ps.addItem(state);
        }

        if (PowerSuspend.hasNewState()) {
            SeekBarView state = new SeekBarView();
            state.setTitle(getString(R.string.power_suspend_state));
            state.setSummary(getString(R.string.power_suspend_state_summary));
            state.setMax(2);
            state.setProgress(PowerSuspend.getNewState());
            state.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    PowerSuspend.setNewState(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
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
            tcp.setItems(Misc.getTcpAvailableCongestions());
            tcp.setItem(Misc.getTcpCongestion());
            tcp.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    Misc.setTcpCongestion(item, getActivity());
                }
            });

            networkCard.addItem(tcp);
        } catch (Exception ignored) {
        }

        GenericSelectView hostname = new GenericSelectView();
        hostname.setSummary(getString(R.string.hostname));
        hostname.setValue(Misc.getHostname());
        hostname.setValueRaw(hostname.getValue());
        hostname.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                Misc.setHostname(value, getActivity());
            }
        });

        networkCard.addItem(hostname);

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
            switchView.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    wakelock.enable(isChecked, getActivity());
                }
            });

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
