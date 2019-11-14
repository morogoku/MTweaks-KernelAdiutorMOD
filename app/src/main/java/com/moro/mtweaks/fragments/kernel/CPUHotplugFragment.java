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

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.fragments.recyclerview.RecyclerViewFragment;
import com.moro.mtweaks.utils.kernel.cpu.CPUFreq;
import com.moro.mtweaks.utils.kernel.cpuhotplug.AiOHotplug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.AlucardHotplug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.AutoSmp;
import com.moro.mtweaks.utils.kernel.cpuhotplug.BluPlug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.CoreCtl;
import com.moro.mtweaks.utils.kernel.cpuhotplug.ClusterHotplug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.IntelliPlug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.LazyPlug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.MBHotplug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.MPDecision;
import com.moro.mtweaks.utils.kernel.cpuhotplug.MSMHotplug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.MakoHotplug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.SamsungPlug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.ThunderPlug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.ZenDecision;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.DescriptionView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SeekBarView;
import com.moro.mtweaks.views.recyclerview.SelectView;
import com.moro.mtweaks.views.recyclerview.SwitchView;
import com.moro.mtweaks.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 07.05.16.
 */
public class CPUHotplugFragment extends RecyclerViewFragment {

    private CPUFreq mCPUFreq;
    private IntelliPlug mIntelliPlug;
    private MSMHotplug mMSMHotplug;
    private MBHotplug mMBHotplug;
    private CoreCtl mCoreCtl;

    private List<SwitchView> mEnableViews = new ArrayList<>();

    @Override
    protected void init() {
        super.init();

        mCPUFreq = CPUFreq.getInstance(getActivity());
        mIntelliPlug = IntelliPlug.getInstance();
        mMSMHotplug = MSMHotplug.getInstance();
        mMBHotplug = MBHotplug.getInstance();
        mCoreCtl = CoreCtl.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mEnableViews.clear();

        if (SamsungPlug.supported()) {
            samsungPlugInit(items);
        }
        if (MPDecision.supported()) {
            mpdecisionInit(items);
        }
        if (mIntelliPlug.supported()) {
            intelliPlugInit(items);
        }
        if (LazyPlug.supported()) {
            lazyPlugInit(items);
        }
        if (BluPlug.supported()) {
            bluPlugInit(items);
        }
        if (mMSMHotplug.supported()) {
            msmHotplugInit(items);
        }
        if (MakoHotplug.supported()) {
            makoHotplugInit(items);
        }
        if (mMBHotplug.supported()) {
            mbHotplugInit(items);
        }
        if (AlucardHotplug.supported()) {
            alucardHotplugInit(items);
        }
        if (ThunderPlug.supported()) {
            thunderPlugInit(items);
        }
        if (ZenDecision.supported()) {
            zenDecisionInit(items);
        }
        if (AutoSmp.supported()) {
            autoSmpInit(items);
        }
        if (mCoreCtl.supported()) {
            coreCtlInit(items);
        }
        if (ClusterHotplug.supported()) {
            ClusterHotplugInit(items);
        }
        if (AiOHotplug.supported()) {
            aioHotplugInit(items);
        }

        for (SwitchView view : mEnableViews) {
            view.addOnSwitchListener((switchView, isChecked) -> {
                boolean enabled = false;
                for (SwitchView view1 : mEnableViews) {
                    if (!enabled && view1.isChecked()) {
                        enabled = true;
                        continue;
                    }
                    if (enabled && view1.isChecked()) {
                        Utils.toast(R.string.hotplug_warning, getActivity());
                        break;
                    }
                }
            });
        }
    }

    private void samsungPlugInit(List<RecyclerViewItem> items) {
        final CardView samsungPlug = new CardView(getActivity());
        samsungPlug.setTitle(getString(R.string.samsungPlug));

        SwitchView enable = new SwitchView();
        enable.setTitle(getString(R.string.samsungPlug));
        enable.setSummary(getString(R.string.samsungPlug_summary));
        enable.setChecked(SamsungPlug.isSamsungPlugEnabled());
        enable.addOnSwitchListener((switchView, isChecked)
                -> SamsungPlug.enableSamsungPlug(isChecked, getActivity()));

        samsungPlug.addItem(enable);
        mEnableViews.add(enable);
/*
		SeekBarView max = new SeekBarView();
        max.setTitle(getString(R.string.samsungPlug_max_cpu));
        max.setMax(8);
        max.setMin(1);
        max.setProgress(Utils.strToInt(SamsungPlug.getMaxOnlineCpu()) - 1);
        max.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                SamsungPlug.setMaxOnlineCpu((position + 1), getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        samsungPlug.addItem(max);

        SeekBarView min = new SeekBarView();
        min.setTitle(getString(R.string.samsungPlug_min_cpu));
        min.setMax(8);
        min.setMin(1);
        min.setProgress(Utils.strToInt(SamsungPlug.getMinOnlineCpu()) - 1);
        min.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                SamsungPlug.setMinOnlineCpu((position + 1), getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        samsungPlug.addItem(min);
*/
        if (samsungPlug.size() > 0) {
            items.add(samsungPlug);
        }

    }

    private void mpdecisionInit(List<RecyclerViewItem> items) {
        CardView mpdecision = new CardView(getActivity());
        mpdecision.setTitle(getString(R.string.mpdecision));

        SwitchView enable = new SwitchView();
        enable.setTitle(getString(R.string.mpdecision));
        enable.setSummary(getString(R.string.mpdecision_summary));
        enable.setChecked(MPDecision.isMpdecisionEnabled());
        enable.addOnSwitchListener((switchView, isChecked)
                -> MPDecision.enableMpdecision(isChecked, getActivity()));

        mpdecision.addItem(enable);
        mEnableViews.add(enable);

        if (mpdecision.size() > 0) {
            items.add(mpdecision);
        }
    }

    private void intelliPlugInit(List<RecyclerViewItem> items) {
        CardView intelliplug = new CardView(getActivity());
        intelliplug.setTitle(getString(R.string.intelliplug));
        intelliplug.setExpand(false);

        if (mIntelliPlug.hasIntelliPlugEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.intelliplug));
            enable.setSummary(getString(R.string.intelliplug_summary));
            enable.setChecked(mIntelliPlug.isIntelliPlugEnabled());
            enable.addOnSwitchListener((switchView, isChecked) -> {
                if (isChecked) {
                    mIntelliPlug.enableStateNotifier(true, getActivity());
                    mIntelliPlug.enableIntelliPlug(true, getActivity());
                } else {
                    mIntelliPlug.enableIntelliPlug(false, getActivity());
                    mIntelliPlug.enableStateNotifier(false, getActivity());
                }
            });

            intelliplug.addItem(enable);
            mEnableViews.add(enable);
        }

        if (mIntelliPlug.hasIntelliPlugProfile()) {
            SelectView profile = new SelectView();
            profile.setTitle(getString(R.string.profile));
            profile.setSummary(getString(R.string.cpu_hotplug_profile_summary));
            profile.setItems(mIntelliPlug.getIntelliPlugProfileMenu(getActivity()));
            profile.setItem(mIntelliPlug.getIntelliPlugProfile());
            profile.setOnItemSelected((selectView, position, item)
                    -> mIntelliPlug.setIntelliPlugProfile(position, getActivity()));

            intelliplug.addItem(profile);
        }

        if (mIntelliPlug.hasIntelliPlugEco()) {
            SwitchView eco = new SwitchView();
            eco.setTitle(getString(R.string.eco_mode));
            eco.setSummary(getString(R.string.eco_mode_summary));
            eco.setChecked(mIntelliPlug.isIntelliPlugEcoEnabled());
            eco.addOnSwitchListener((switchView, isChecked)
                    -> mIntelliPlug.enableIntelliPlugEco(isChecked, getActivity()));

            intelliplug.addItem(eco);
        }

        if (mIntelliPlug.hasIntelliPlugTouchBoost()) {
            SwitchView touchBoost = new SwitchView();
            touchBoost.setTitle(getString(R.string.touch_boost));
            touchBoost.setSummary(getString(R.string.touch_boost_summary));
            touchBoost.setChecked(mIntelliPlug.isIntelliPlugTouchBoostEnabled());
            touchBoost.addOnSwitchListener((switchView, isChecked)
                    -> mIntelliPlug.enableIntelliPlugTouchBoost(isChecked, getActivity()));

            intelliplug.addItem(touchBoost);
        }

        if (mIntelliPlug.hasIntelliPlugHysteresis()) {
            SeekBarView hysteresis = new SeekBarView();
            hysteresis.setTitle(getString(R.string.hysteresis));
            hysteresis.setSummary(getString(R.string.hysteresis_summary));
            hysteresis.setMax(17);
            hysteresis.setProgress(mIntelliPlug.getIntelliPlugHysteresis());
            hysteresis.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIntelliPlug.setIntelliPlugHysteresis(position, getActivity());
                }
            });

            intelliplug.addItem(hysteresis);
        }

        if (mIntelliPlug.hasIntelliPlugThresold()) {
            SeekBarView threshold = new SeekBarView();
            threshold.setTitle(getString(R.string.cpu_threshold));
            threshold.setSummary(getString(R.string.cpu_threshold_summary));
            threshold.setMax(1000);
            threshold.setProgress(mIntelliPlug.getIntelliPlugThresold());
            threshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIntelliPlug.setIntelliPlugThresold(position, getActivity());
                }
            });

            intelliplug.addItem(threshold);
        }

        if (mIntelliPlug.hasIntelliPlugScreenOffMax() && mCPUFreq.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            list.addAll(mCPUFreq.getAdjustedFreq(getActivity()));

            SelectView maxScreenOffFreq = new SelectView();
            maxScreenOffFreq.setTitle(getString(R.string.cpu_max_screen_off_freq));
            maxScreenOffFreq.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            maxScreenOffFreq.setItems(list);
            maxScreenOffFreq.setItem(mIntelliPlug.getIntelliPlugScreenOffMax());
            maxScreenOffFreq.setOnItemSelected((selectView, position, item)
                    -> mIntelliPlug.setIntelliPlugScreenOffMax(position, getActivity()));

            intelliplug.addItem(maxScreenOffFreq);
        }

        if (mIntelliPlug.hasIntelliPlugDebug()) {
            SwitchView debug = new SwitchView();
            debug.setTitle(getString(R.string.debug_mask));
            debug.setSummary(getString(R.string.debug_mask_summary));
            debug.setChecked(mIntelliPlug.isIntelliPlugDebugEnabled());
            debug.addOnSwitchListener((switchView, isChecked)
                    -> mIntelliPlug.enableIntelliPlugDebug(isChecked, getActivity()));

            intelliplug.addItem(debug);
        }

        if (mIntelliPlug.hasIntelliPlugSuspend()) {
            SwitchView suspend = new SwitchView();
            suspend.setTitle(getString(R.string.suspend));
            suspend.setSummary(getString(R.string.suspend_summary));
            suspend.setChecked(mIntelliPlug.isIntelliPlugSuspendEnabled());
            suspend.addOnSwitchListener((switchView, isChecked)
                    -> mIntelliPlug.enableIntelliPlugSuspend(isChecked, getActivity()));

            intelliplug.addItem(suspend);
        }

        if (mIntelliPlug.hasIntelliPlugCpusBoosted()) {
            SeekBarView cpusBoosted = new SeekBarView();
            cpusBoosted.setTitle(getString(R.string.cpus_boosted));
            cpusBoosted.setSummary(getString(R.string.cpus_boosted_summary));
            cpusBoosted.setMax(mCPUFreq.getCpuCount());
            cpusBoosted.setMin(1);
            cpusBoosted.setProgress(mIntelliPlug.getIntelliPlugCpusBoosted() - 1);
            cpusBoosted.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIntelliPlug.setIntelliPlugCpusBoosted(position + 1, getActivity());
                }
            });

            intelliplug.addItem(cpusBoosted);
        }

        if (mIntelliPlug.hasIntelliPlugMinCpusOnline()) {
            SeekBarView minCpusOnline = new SeekBarView();
            minCpusOnline.setTitle(getString(R.string.min_cpu_online));
            minCpusOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minCpusOnline.setMax(mCPUFreq.getCpuCount());
            minCpusOnline.setMin(1);
            minCpusOnline.setProgress(mIntelliPlug.getIntelliPlugMinCpusOnline() - 1);
            minCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIntelliPlug.setIntelliPlugMinCpusOnline(position + 1, getActivity());
                }
            });

            intelliplug.addItem(minCpusOnline);
        }

        if (mIntelliPlug.hasIntelliPlugMaxCpusOnline()) {
            SeekBarView maxCpusOnline = new SeekBarView();
            maxCpusOnline.setTitle(getString(R.string.max_cpu_online));
            maxCpusOnline.setSummary(getString(R.string.max_cpu_online_summary));
            maxCpusOnline.setMax(mCPUFreq.getCpuCount());
            maxCpusOnline.setMin(1);
            maxCpusOnline.setProgress(mIntelliPlug.getIntelliPlugMaxCpusOnline() - 1);
            maxCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIntelliPlug.setIntelliPlugMaxCpusOnline(position + 1, getActivity());
                }
            });

            intelliplug.addItem(maxCpusOnline);
        }

        if (mIntelliPlug.hasIntelliPlugMaxCpusOnlineSusp()) {
            SeekBarView maxCpusOnlineSusp = new SeekBarView();
            maxCpusOnlineSusp.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCpusOnlineSusp.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCpusOnlineSusp.setMax(mCPUFreq.getCpuCount());
            maxCpusOnlineSusp.setMin(1);
            maxCpusOnlineSusp.setProgress(mIntelliPlug.getIntelliPlugMaxCpusOnlineSusp() - 1);
            maxCpusOnlineSusp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIntelliPlug.setIntelliPlugMaxCpusOnlineSusp(position + 1, getActivity());
                }
            });

            intelliplug.addItem(maxCpusOnlineSusp);
        }

        if (mIntelliPlug.hasIntelliPlugSuspendDeferTime()) {
            SeekBarView suspendDeferTime = new SeekBarView();
            suspendDeferTime.setTitle(getString(R.string.suspend_defer_time));
            suspendDeferTime.setUnit(getString(R.string.ms));
            suspendDeferTime.setMax(5000);
            suspendDeferTime.setOffset(10);
            suspendDeferTime.setProgress(mIntelliPlug.getIntelliPlugSuspendDeferTime() / 10);
            suspendDeferTime.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIntelliPlug.setIntelliPlugSuspendDeferTime(position * 10, getActivity());
                }
            });

            intelliplug.addItem(suspendDeferTime);
        }

        if (mIntelliPlug.hasIntelliPlugDeferSampling()) {
            SeekBarView deferSampling = new SeekBarView();
            deferSampling.setTitle(getString(R.string.defer_sampling));
            deferSampling.setUnit(getString(R.string.ms));
            deferSampling.setMax(1000);
            deferSampling.setProgress(mIntelliPlug.getIntelliPlugDeferSampling());
            deferSampling.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIntelliPlug.setIntelliPlugDeferSampling(position, getActivity());
                }
            });

            intelliplug.addItem(deferSampling);
        }

        if (mIntelliPlug.hasIntelliPlugBoostLockDuration()) {
            SeekBarView boostLockDuration = new SeekBarView();
            boostLockDuration.setTitle(getString(R.string.boost_lock_duration));
            boostLockDuration.setSummary(getString(R.string.boost_lock_duration_summary));
            boostLockDuration.setUnit(getString(R.string.ms));
            boostLockDuration.setMax(5000);
            boostLockDuration.setMin(1);
            boostLockDuration.setProgress(mIntelliPlug.getIntelliPlugBoostLockDuration() - 1);
            boostLockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIntelliPlug.setIntelliPlugBoostLockDuration(position + 1, getActivity());
                }
            });

            intelliplug.addItem(boostLockDuration);
        }

        if (mIntelliPlug.hasIntelliPlugDownLockDuration()) {
            SeekBarView downLockDuration = new SeekBarView();
            downLockDuration.setTitle(getString(R.string.down_lock_duration));
            downLockDuration.setSummary(getString(R.string.down_lock_duration_summary));
            downLockDuration.setUnit(getString(R.string.ms));
            downLockDuration.setMax(5000);
            downLockDuration.setMin(1);
            downLockDuration.setProgress(mIntelliPlug.getIntelliPlugDownLockDuration() - 1);
            downLockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIntelliPlug.setIntelliPlugDownLockDuration(position + 1, getActivity());
                }
            });

            intelliplug.addItem(downLockDuration);
        }

        if (mIntelliPlug.hasIntelliPlugFShift()) {
            SeekBarView fShift = new SeekBarView();
            fShift.setTitle(getString(R.string.fshift));
            fShift.setMax(8);
            fShift.setProgress(mIntelliPlug.getIntelliPlugFShift());
            fShift.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mIntelliPlug.setIntelliPlugFShift(position, getActivity());
                }
            });

            intelliplug.addItem(fShift);
        }

        if (intelliplug.size() > 0) {
            items.add(intelliplug);
        }
    }

    private void lazyPlugInit(List<RecyclerViewItem> items) {
        CardView lazyplug = new CardView(getActivity());
        lazyplug.setTitle(getString(R.string.lazyplug));
        lazyplug.setExpand(false);

        if (LazyPlug.hasEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.lazyplug));
            enable.setSummary(getString(R.string.lazyplug_summary));
            enable.setChecked(LazyPlug.isEnabled());
            enable.addOnSwitchListener((switchView, isChecked) -> {
                if (isChecked) {
                    LazyPlug.enableStateNotifier(true, getActivity());
                    LazyPlug.enable(true, getActivity());
                } else {
                    LazyPlug.enable(false, getActivity());
                    LazyPlug.enableStateNotifier(false, getActivity());
                }
            });

            lazyplug.addItem(enable);
            mEnableViews.add(enable);
        }

        if (LazyPlug.hasProfile()) {
            SelectView profile = new SelectView();
            profile.setTitle(getString(R.string.profile));
            profile.setSummary(getString(R.string.cpu_hotplug_profile_summary));
            profile.setItems(LazyPlug.getProfileMenu(getActivity()));
            profile.setItem(LazyPlug.getProfile());
            profile.setOnItemSelected((selectView, position, item)
                    -> LazyPlug.setProfile(position, getActivity()));

            lazyplug.addItem(profile);
        }

        if (LazyPlug.hasTouchBoost()) {
            SwitchView touchBoost = new SwitchView();
            touchBoost.setTitle(getString(R.string.touch_boost));
            touchBoost.setSummary(getString(R.string.touch_boost_summary));
            touchBoost.setChecked(LazyPlug.isTouchBoostEnabled());
            touchBoost.addOnSwitchListener((switchView, isChecked)
                    -> LazyPlug.enableTouchBoost(isChecked, getActivity()));

            lazyplug.addItem(touchBoost);
        }

        if (LazyPlug.hasHysteresis()) {
            SeekBarView hysteresis = new SeekBarView();
            hysteresis.setTitle(getString(R.string.hysteresis));
            hysteresis.setSummary(getString(R.string.hysteresis_summary));
            hysteresis.setMax(17);
            hysteresis.setProgress(LazyPlug.getHysteresis());
            hysteresis.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    LazyPlug.setHysteresis(position, getActivity());
                }
            });

            lazyplug.addItem(hysteresis);
        }

        if (LazyPlug.hasThreshold()) {
            SeekBarView threshold = new SeekBarView();
            threshold.setTitle(getString(R.string.cpu_threshold));
            threshold.setSummary(getString(R.string.cpu_threshold_summary));
            threshold.setMax(1250);
            threshold.setProgress(LazyPlug.getThreshold());
            threshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    LazyPlug.setThreshold(position, getActivity());
                }
            });

            lazyplug.addItem(threshold);
        }

        if (LazyPlug.hasPossibleCores()) {
            SeekBarView possibleCores = new SeekBarView();
            possibleCores.setTitle(getString(R.string.max_cpu_online));
            possibleCores.setSummary(getString(R.string.possible_cpu_cores_summary));
            possibleCores.setMax(mCPUFreq.getCpuCount());
            possibleCores.setMin(1);
            possibleCores.setProgress(LazyPlug.getPossibleCores() - 1);
            possibleCores.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    LazyPlug.setPossibleCores(position + 1, getActivity());
                }
            });

            lazyplug.addItem(possibleCores);
        }

        if (lazyplug.size() > 0) {
            items.add(lazyplug);
        }
    }

    private void bluPlugInit(List<RecyclerViewItem> items) {
        CardView bluplug = new CardView(getActivity());
        bluplug.setTitle(getString(R.string.blu_plug));
        bluplug.setExpand(false);

        if (BluPlug.hasBluPlugEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.blu_plug));
            enable.setSummary(getString(R.string.blu_plug_summary));
            enable.setChecked(BluPlug.isBluPlugEnabled());
             enable.addOnSwitchListener((switchView, isChecked) -> {
                if (isChecked) {
                    BluPlug.enableStateNotifier(true, getActivity());
                    BluPlug.enableBluPlug(true, getActivity());
                } else {
                    BluPlug.enableBluPlug(false, getActivity());
                    BluPlug.enableStateNotifier(false, getActivity());
                }
            });

            bluplug.addItem(enable);
            mEnableViews.add(enable);
        }

        if (BluPlug.hasBluPlugPowersaverMode()) {
            SwitchView powersaverMode = new SwitchView();
            powersaverMode.setTitle(getString(R.string.powersaver_mode));
            powersaverMode.setSummary(getString(R.string.powersaver_mode_summary));
            powersaverMode.setChecked(BluPlug.isBluPlugPowersaverModeEnabled());
            powersaverMode.addOnSwitchListener((switchView, isChecked)
                    -> BluPlug.enableBluPlugPowersaverMode(isChecked, getActivity()));

            bluplug.addItem(powersaverMode);
        }

        if (BluPlug.hasBluPlugMinOnline()) {
            SeekBarView minOnline = new SeekBarView();
            minOnline.setTitle(getString(R.string.min_cpu_online));
            minOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minOnline.setMax(mCPUFreq.getCpuCount());
            minOnline.setMin(1);
            minOnline.setProgress(BluPlug.getBluPlugMinOnline() - 1);
            minOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugMinOnline(position + 1, getActivity());
                }
            });

            bluplug.addItem(minOnline);
        }

        if (BluPlug.hasBluPlugMaxOnline()) {
            SeekBarView maxOnline = new SeekBarView();
            maxOnline.setTitle(getString(R.string.max_cpu_online));
            maxOnline.setSummary(getString(R.string.max_cpu_online_summary));
            maxOnline.setMax(mCPUFreq.getCpuCount());
            maxOnline.setMin(1);
            maxOnline.setProgress(BluPlug.getBluPlugMaxOnline() - 1);
            maxOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugMaxOnline(position + 1, getActivity());
                }
            });

            bluplug.addItem(maxOnline);
        }

        if (BluPlug.hasBluPlugMaxCoresScreenOff()) {
            SeekBarView maxCoresScreenOff = new SeekBarView();
            maxCoresScreenOff.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCoresScreenOff.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCoresScreenOff.setMax(mCPUFreq.getCpuCount());
            maxCoresScreenOff.setMin(1);
            maxCoresScreenOff.setProgress(BluPlug.getBluPlugMaxCoresScreenOff() - 1);
            maxCoresScreenOff.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugMaxCoresScreenOff(position + 1, getActivity());
                }
            });

            bluplug.addItem(maxCoresScreenOff);
        }

        if (BluPlug.hasBluPlugMaxFreqScreenOff() && mCPUFreq.getFreqs() != null) {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.disabled));
            list.addAll(mCPUFreq.getAdjustedFreq(getActivity()));

            SeekBarView maxFreqScreenOff = new SeekBarView();
            maxFreqScreenOff.setTitle(getString(R.string.cpu_max_screen_off_freq));
            maxFreqScreenOff.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            maxFreqScreenOff.setItems(list);
            maxFreqScreenOff.setProgress(BluPlug.getBluPlugMaxFreqScreenOff());
            maxFreqScreenOff.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugMaxFreqScreenOff(position, getActivity());
                }
            });

            bluplug.addItem(maxFreqScreenOff);
        }

        if (BluPlug.hasBluPlugUpThreshold()) {
            SeekBarView upThreshold = new SeekBarView();
            upThreshold.setTitle(getString(R.string.up_threshold));
            upThreshold.setSummary(getString(R.string.up_threshold_summary));
            upThreshold.setUnit("%");
            upThreshold.setMax(100);
            upThreshold.setProgress(BluPlug.getBluPlugUpThreshold());
            upThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugUpThreshold(position, getActivity());
                }
            });

            bluplug.addItem(upThreshold);
        }
		
        if (BluPlug.hasBluPlugPlugThreshold()) {
            SeekBarView PlugThreshold = new SeekBarView();
            PlugThreshold.setTitle(getString(R.string.plug_threshold));
            PlugThreshold.setSummary(getString(R.string.plug_threshold_summary));
            PlugThreshold.setUnit("%");
            PlugThreshold.setMax(100);
            PlugThreshold.setProgress(BluPlug.getBluPlugPlugThreshold());
            PlugThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugPlugThreshold(position, getActivity());
                }
            });

            bluplug.addItem(PlugThreshold);
        }

        if (BluPlug.hasBluPlugUpTimerCnt()) {
            List<String> list = new ArrayList<>();
            for (float i = 0; i < 21; i++) {
                list.add(String.valueOf(i * 0.5f));
            }

            SeekBarView upTimerCnt = new SeekBarView();
            upTimerCnt.setTitle(getString(R.string.up_timer_cnt));
            upTimerCnt.setSummary(getString(R.string.up_timer_cnt_summary));
            upTimerCnt.setItems(list);
            upTimerCnt.setProgress(BluPlug.getBluPlugUpTimerCnt());
            upTimerCnt.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugUpTimerCnt(position, getActivity());
                }
            });

            bluplug.addItem(upTimerCnt);
        }

        if (BluPlug.hasBluPlugDownTimerCnt()) {
            List<String> list = new ArrayList<>();
            for (float i = 0; i < 21; i++) {
                list.add(String.valueOf(i * 0.5f));
            }

            SeekBarView downTimerCnt = new SeekBarView();
            downTimerCnt.setTitle(getString(R.string.down_timer_cnt));
            downTimerCnt.setSummary(getString(R.string.down_timer_cnt_summary));
            downTimerCnt.setItems(list);
            downTimerCnt.setProgress(BluPlug.getBluPlugDownTimerCnt());
            downTimerCnt.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    BluPlug.setBluPlugDownTimerCnt(position, getActivity());
                }
            });

            bluplug.addItem(downTimerCnt);
        }

        if (bluplug.size() > 0) {
            items.add(bluplug);
        }
    }

    private void msmHotplugInit(List<RecyclerViewItem> items) {
        CardView msmHotplug = new CardView(getActivity());
        msmHotplug.setTitle(getString(R.string.msm_hotplug));
        msmHotplug.setExpand(false);

        if (mMSMHotplug.hasMsmHotplugEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.msm_hotplug));
            enable.setSummary(getString(R.string.msm_hotplug_summary));
            enable.setChecked(mMSMHotplug.isMsmHotplugEnabled());
            enable.addOnSwitchListener((switchView, isChecked)
                    -> mMSMHotplug.enableMsmHotplug(isChecked, getActivity()));

            msmHotplug.addItem(enable);
            mEnableViews.add(enable);
        }

        if (mMSMHotplug.hasMsmHotplugDebugMask()) {
            SwitchView debugMask = new SwitchView();
            debugMask.setTitle(getString(R.string.debug_mask));
            debugMask.setSummary(getString(R.string.debug_mask_summary));
            debugMask.setChecked(mMSMHotplug.isMsmHotplugDebugMaskEnabled());
            debugMask.addOnSwitchListener((switchView, isChecked)
                    -> mMSMHotplug.enableMsmHotplugDebugMask(isChecked, getActivity()));

            msmHotplug.addItem(debugMask);
        }

        if (mMSMHotplug.hasMsmHotplugMinCpusOnline()) {
            SeekBarView minCpusOnline = new SeekBarView();
            minCpusOnline.setTitle(getString(R.string.min_cpu_online));
            minCpusOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minCpusOnline.setMax(mCPUFreq.getCpuCount());
            minCpusOnline.setMin(1);
            minCpusOnline.setProgress(mMSMHotplug.getMsmHotplugMinCpusOnline() - 1);
            minCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugMinCpusOnline(position + 1, getActivity());
                }
            });

            msmHotplug.addItem(minCpusOnline);
        }

        if (mMSMHotplug.hasMsmHotplugMaxCpusOnline()) {
            SeekBarView maxCpusOnline = new SeekBarView();
            maxCpusOnline.setTitle(getString(R.string.max_cpu_online));
            maxCpusOnline.setSummary(getString(R.string.max_cpu_online_summary));
            maxCpusOnline.setMax(mCPUFreq.getCpuCount());
            maxCpusOnline.setMin(1);
            maxCpusOnline.setProgress(mMSMHotplug.getMsmHotplugMaxCpusOnline() - 1);
            maxCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugMaxCpusOnline(position + 1, getActivity());
                }
            });

            msmHotplug.addItem(maxCpusOnline);
        }

        if (mMSMHotplug.hasMsmHotplugCpusBoosted()) {
            SeekBarView cpusBoosted = new SeekBarView();
            cpusBoosted.setTitle(getString(R.string.cpus_boosted));
            cpusBoosted.setSummary(getString(R.string.cpus_boosted_summary));
            cpusBoosted.setMax(mCPUFreq.getCpuCount());
            cpusBoosted.setMin(1);
            cpusBoosted.setProgress(mMSMHotplug.getMsmHotplugCpusBoosted());
            cpusBoosted.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugCpusBoosted(position, getActivity());
                }
            });

            msmHotplug.addItem(cpusBoosted);
        }

        if (mMSMHotplug.hasMsmHotplugMaxCpusOnlineSusp()) {
            SeekBarView maxCpusOnlineSusp = new SeekBarView();
            maxCpusOnlineSusp.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCpusOnlineSusp.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCpusOnlineSusp.setMax(mCPUFreq.getCpuCount());
            maxCpusOnlineSusp.setMin(1);
            maxCpusOnlineSusp.setProgress(mMSMHotplug.getMsmHotplugMaxCpusOnlineSusp() - 1);
            maxCpusOnlineSusp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugMaxCpusOnlineSusp(position + 1, getActivity());
                }
            });

            msmHotplug.addItem(maxCpusOnlineSusp);
        }

        if (mMSMHotplug.hasMsmHotplugBoostLockDuration()) {
            SeekBarView boostLockDuration = new SeekBarView();
            boostLockDuration.setTitle(getString(R.string.boost_lock_duration));
            boostLockDuration.setSummary(getString(R.string.boost_lock_duration_summary));
            boostLockDuration.setMax(5000);
            boostLockDuration.setMin(1);
            boostLockDuration.setProgress(mMSMHotplug.getMsmHotplugBoostLockDuration() - 1);
            boostLockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugBoostLockDuration(position + 1, getActivity());
                }
            });

            msmHotplug.addItem(boostLockDuration);
        }

        if (mMSMHotplug.hasMsmHotplugDownLockDuration()) {
            SeekBarView downLockDuration = new SeekBarView();
            downLockDuration.setTitle(getString(R.string.down_lock_duration));
            downLockDuration.setSummary(getString(R.string.down_lock_duration_summary));
            downLockDuration.setMax(5000);
            downLockDuration.setMin(1);
            downLockDuration.setProgress(mMSMHotplug.getMsmHotplugDownLockDuration() - 1);
            downLockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugDownLockDuration(position + 1, getActivity());
                }
            });

            msmHotplug.addItem(downLockDuration);
        }

        if (mMSMHotplug.hasMsmHotplugHistorySize()) {
            SeekBarView historySize = new SeekBarView();
            historySize.setTitle(getString(R.string.history_size));
            historySize.setSummary(getString(R.string.history_size_summary));
            historySize.setMax(60);
            historySize.setMin(1);
            historySize.setProgress(mMSMHotplug.getMsmHotplugHistorySize() - 1);
            historySize.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugHistorySize(position + 1, getActivity());
                }
            });

            msmHotplug.addItem(historySize);
        }

        if (mMSMHotplug.hasMsmHotplugUpdateRate()) {
            SeekBarView updateRate = new SeekBarView();
            updateRate.setTitle(getString(R.string.update_rate));
            updateRate.setSummary(getString(R.string.update_rate_summary));
            updateRate.setMax(60);
            updateRate.setProgress(mMSMHotplug.getMsmHotplugUpdateRate());
            updateRate.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugUpdateRate(position, getActivity());
                }
            });

            msmHotplug.addItem(updateRate);
        }

        if (mMSMHotplug.hasMsmHotplugFastLaneLoad()) {
            SeekBarView fastLaneLoad = new SeekBarView();
            fastLaneLoad.setTitle(getString(R.string.fast_lane_load));
            fastLaneLoad.setSummary(getString(R.string.fast_lane_load_summary));
            fastLaneLoad.setMax(400);
            fastLaneLoad.setProgress(mMSMHotplug.getMsmHotplugFastLaneLoad());
            fastLaneLoad.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugFastLaneLoad(position, getActivity());
                }
            });

            msmHotplug.addItem(fastLaneLoad);
        }

        if (mMSMHotplug.hasMsmHotplugFastLaneMinFreq() && mCPUFreq.getFreqs() != null) {
            SelectView fastLaneMinFreq = new SelectView();
            fastLaneMinFreq.setTitle(getString(R.string.fast_lane_min_freq));
            fastLaneMinFreq.setSummary(getString(R.string.fast_lane_min_freq_summary));
            fastLaneMinFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            fastLaneMinFreq.setItem((mMSMHotplug.getMsmHotplugFastLaneMinFreq() / 1000) + getString(R.string.mhz));
            fastLaneMinFreq.setOnItemSelected((selectView, position, item)
                    -> mMSMHotplug.setMsmHotplugFastLaneMinFreq(
                    mCPUFreq.getFreqs().get(position), getActivity()));

            msmHotplug.addItem(fastLaneMinFreq);
        }

        if (mMSMHotplug.hasMsmHotplugOfflineLoad()) {
            SeekBarView offlineLoad = new SeekBarView();
            offlineLoad.setTitle(getString(R.string.offline_load));
            offlineLoad.setSummary(getString(R.string.offline_load_summary));
            offlineLoad.setProgress(mMSMHotplug.getMsmHotplugOfflineLoad());
            offlineLoad.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugOfflineLoad(position, getActivity());
                }
            });

            msmHotplug.addItem(offlineLoad);
        }

        if (mMSMHotplug.hasMsmHotplugIoIsBusy()) {
            SwitchView ioIsBusy = new SwitchView();
            ioIsBusy.setTitle(getString(R.string.io_is_busy));
            ioIsBusy.setSummary(getString(R.string.io_is_busy_summary));
            ioIsBusy.setChecked(mMSMHotplug.isMsmHotplugIoIsBusyEnabled());
            ioIsBusy.addOnSwitchListener((switchView, isChecked)
                    -> mMSMHotplug.enableMsmHotplugIoIsBusy(isChecked, getActivity()));

            msmHotplug.addItem(ioIsBusy);
        }

        if (mMSMHotplug.hasMsmHotplugSuspendMaxCpus()) {
            SeekBarView suspendMaxCpus = new SeekBarView();
            suspendMaxCpus.setTitle(getString(R.string.max_cpu_online_screen_off));
            suspendMaxCpus.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            suspendMaxCpus.setMax(mCPUFreq.getCpuCount());
            suspendMaxCpus.setMin(1);
            suspendMaxCpus.setProgress(mMSMHotplug.getMsmHotplugSuspendMaxCpus());
            suspendMaxCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugSuspendMaxCpus(position, getActivity());
                }
            });

            msmHotplug.addItem(suspendMaxCpus);
        }

        if (mMSMHotplug.hasMsmHotplugSuspendFreq() && mCPUFreq.getFreqs() != null) {
            SelectView suspendFreq = new SelectView();
            suspendFreq.setTitle(getString(R.string.cpu_max_screen_off_freq));
            suspendFreq.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            suspendFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            suspendFreq.setItem((mMSMHotplug.getMsmHotplugSuspendFreq() / 1000) + getString(R.string.mhz));
            suspendFreq.setOnItemSelected((selectView, position, item)
                    -> mMSMHotplug.setMsmHotplugSuspendFreq(mCPUFreq.getFreqs().get(position), getActivity()));

            msmHotplug.addItem(suspendFreq);
        }

        if (mMSMHotplug.hasMsmHotplugSuspendDeferTime()) {
            SeekBarView suspendDeferTime = new SeekBarView();
            suspendDeferTime.setTitle(getString(R.string.suspend_defer_time));
            suspendDeferTime.setUnit(getString(R.string.ms));
            suspendDeferTime.setMax(5000);
            suspendDeferTime.setOffset(10);
            suspendDeferTime.setProgress(mMSMHotplug.getMsmHotplugSuspendDeferTime() / 10);
            suspendDeferTime.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMSMHotplug.setMsmHotplugSuspendDeferTime(position * 10, getActivity());
                }
            });

            msmHotplug.addItem(suspendDeferTime);
        }

        if (msmHotplug.size() > 0) {
            items.add(msmHotplug);
        }
    }

    private void makoHotplugInit(List<RecyclerViewItem> items) {
        CardView makoHotplug = new CardView(getActivity());
        makoHotplug.setTitle(getString(R.string.mako_hotplug));
        makoHotplug.setExpand(false);

        if (MakoHotplug.hasMakoHotplugEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.mako_hotplug));
            enable.setSummary(getString(R.string.mako_hotplug_summary));
            enable.setChecked(MakoHotplug.isMakoHotplugEnabled());
            enable.addOnSwitchListener((switchView, isChecked)
                    -> MakoHotplug.enableMakoHotplug(isChecked, getActivity()));

            makoHotplug.addItem(enable);
            mEnableViews.add(enable);
        }

        if (MakoHotplug.hasMakoHotplugCoresOnTouch()) {
            SeekBarView coresOnTouch = new SeekBarView();
            coresOnTouch.setTitle(getString(R.string.cpus_on_touch));
            coresOnTouch.setSummary(getString(R.string.cpus_on_touch_summary));
            coresOnTouch.setMax(mCPUFreq.getCpuCount());
            coresOnTouch.setMin(1);
            coresOnTouch.setProgress(MakoHotplug.getMakoHotplugCoresOnTouch() - 1);
            coresOnTouch.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MakoHotplug.setMakoHotplugCoresOnTouch(position + 1, getActivity());
                }
            });

            makoHotplug.addItem(coresOnTouch);
        }

        if (MakoHotplug.hasMakoHotplugCpuFreqUnplugLimit() && mCPUFreq.getFreqs() != null) {
            SelectView cpufreqUnplugLimit = new SelectView();
            cpufreqUnplugLimit.setSummary(getString(R.string.cpu_freq_unplug_limit));
            cpufreqUnplugLimit.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            cpufreqUnplugLimit.setItem((MakoHotplug.getMakoHotplugCpuFreqUnplugLimit() / 1000)
                    + getString(R.string.mhz));
            cpufreqUnplugLimit.setOnItemSelected((selectView, position, item)
                    -> MakoHotplug.setMakoHotplugCpuFreqUnplugLimit(
                    mCPUFreq.getFreqs().get(position), getActivity()));

            makoHotplug.addItem(cpufreqUnplugLimit);
        }

        if (MakoHotplug.hasMakoHotplugFirstLevel()) {
            SeekBarView firstLevel = new SeekBarView();
            firstLevel.setTitle(getString(R.string.first_level));
            firstLevel.setSummary(getString(R.string.first_level_summary));
            firstLevel.setUnit("%");
            firstLevel.setProgress(MakoHotplug.getMakoHotplugFirstLevel());
            firstLevel.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MakoHotplug.setMakoHotplugFirstLevel(position, getActivity());
                }
            });

            makoHotplug.addItem(firstLevel);
        }

        if (MakoHotplug.hasMakoHotplugHighLoadCounter()) {
            SeekBarView highLoadCounter = new SeekBarView();
            highLoadCounter.setTitle(getString(R.string.high_load_counter));
            highLoadCounter.setProgress(MakoHotplug.getMakoHotplugHighLoadCounter());
            highLoadCounter.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MakoHotplug.setMakoHotplugHighLoadCounter(position, getActivity());
                }
            });

            makoHotplug.addItem(highLoadCounter);
        }

        if (MakoHotplug.hasMakoHotplugLoadThreshold()) {
            SeekBarView loadThreshold = new SeekBarView();
            loadThreshold.setTitle(getString(R.string.load_threshold));
            loadThreshold.setSummary(getString(R.string.load_threshold_summary));
            loadThreshold.setUnit("%");
            loadThreshold.setProgress(MakoHotplug.getMakoHotplugLoadThreshold());
            loadThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MakoHotplug.setMakoHotplugLoadThreshold(position, getActivity());
                }
            });

            makoHotplug.addItem(loadThreshold);
        }

        if (MakoHotplug.hasMakoHotplugMaxLoadCounter()) {
            SeekBarView maxLoadCounter = new SeekBarView();
            maxLoadCounter.setTitle(getString(R.string.max_load_counter));
            maxLoadCounter.setProgress(MakoHotplug.getMakoHotplugMaxLoadCounter());
            maxLoadCounter.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MakoHotplug.setMakoHotplugMaxLoadCounter(position, getActivity());
                }
            });

            makoHotplug.addItem(maxLoadCounter);
        }

        if (MakoHotplug.hasMakoHotplugMinTimeCpuOnline()) {
            SeekBarView minTimeCpuOnline = new SeekBarView();
            minTimeCpuOnline.setTitle(getString(R.string.min_time_cpu_online));
            minTimeCpuOnline.setProgress(MakoHotplug.getMakoHotplugMinTimeCpuOnline());
            minTimeCpuOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MakoHotplug.setMakoHotplugMinTimeCpuOnline(position, getActivity());
                }
            });

            makoHotplug.addItem(minTimeCpuOnline);
        }

        if (MakoHotplug.hasMakoHotplugMinCoresOnline()) {
            SeekBarView minCoresOnline = new SeekBarView();
            minCoresOnline.setTitle(getString(R.string.min_cpu_online));
            minCoresOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minCoresOnline.setMax(mCPUFreq.getCpuCount());
            minCoresOnline.setMin(1);
            minCoresOnline.setProgress(MakoHotplug.getMakoHotplugMinCoresOnline() - 1);
            minCoresOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MakoHotplug.setMakoHotplugMinCoresOnline(position + 1, getActivity());
                }
            });

            makoHotplug.addItem(minCoresOnline);
        }

        if (MakoHotplug.hasMakoHotplugTimer()) {
            SeekBarView timer = new SeekBarView();
            timer.setTitle(getString(R.string.timer));
            timer.setProgress(MakoHotplug.getMakoHotplugTimer());
            timer.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    MakoHotplug.setMakoHotplugTimer(position, getActivity());
                }
            });

            makoHotplug.addItem(timer);
        }

        if (MakoHotplug.hasMakoHotplugSuspendFreq() && mCPUFreq.getFreqs() != null) {
            SelectView suspendFreq = new SelectView();
            suspendFreq.setTitle(getString(R.string.cpu_max_screen_off_freq));
            suspendFreq.setSummary(getString(R.string.cpu_max_screen_off_freq_summary));
            suspendFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            suspendFreq.setItem((MakoHotplug.getMakoHotplugSuspendFreq() / 1000) + getString(R.string.mhz));
            suspendFreq.setOnItemSelected((selectView, position, item)
                    -> MakoHotplug.setMakoHotplugSuspendFreq(mCPUFreq.getFreqs().get(position), getActivity()));

            makoHotplug.addItem(suspendFreq);
        }

        if (makoHotplug.size() > 0) {
            items.add(makoHotplug);
        }
    }

    private void ClusterHotplugInit(List<RecyclerViewItem> items) {
        CardView clusterHotplug = new CardView(getActivity());
        clusterHotplug.setTitle(getString(R.string.cluster_hotplug));
        clusterHotplug.setExpand(false);

        if (ClusterHotplug.hasClusterHotplugEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.cluster_hotplug));
            enable.setSummary(getString(R.string.cluster_hotplug_summary));
            enable.setChecked(ClusterHotplug.isClusterHotplugEnabled());
            enable.addOnSwitchListener((switchView, isChecked)
                    -> ClusterHotplug.enableClusterHotplug(isChecked, getActivity()));

            clusterHotplug.addItem(enable);
            mEnableViews.add(enable);
        }

        if (ClusterHotplug.hasClusterHotplugLowPowerMode()) {
            SwitchView lowPowerMode = new SwitchView();
            lowPowerMode.setTitle(getString(R.string.low_power_mode));
            lowPowerMode.setSummary(getString(R.string.low_power_mode_summary));
            lowPowerMode.setChecked(ClusterHotplug.isClusterHotplugLowPowerMode());
            lowPowerMode.addOnSwitchListener((switchView, isChecked)
                    -> ClusterHotplug.enableClusterHotplugLowPowerMode(isChecked, getActivity()));

            clusterHotplug.addItem(lowPowerMode);
        }

        if (ClusterHotplug.hasClusterHotplugLoadThresholdDown()) {
            SeekBarView loadThresholdDown = new SeekBarView();
            loadThresholdDown.setTitle(getString(R.string.cl_load_threshold_down));
            loadThresholdDown.setSummary(getString(R.string.cl_load_threshold_down_summary));
            loadThresholdDown.setUnit("%");
            loadThresholdDown.setProgress(ClusterHotplug.getClusterHotplugLoadThresholdDown());
            loadThresholdDown.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ClusterHotplug.setClusterHotplugLoadThresholdDown(position, getActivity());
                }
            });

            clusterHotplug.addItem(loadThresholdDown);
        }

        if (ClusterHotplug.hasClusterHotplugLoadThresholdUp()) {
            SeekBarView loadThresholdUp = new SeekBarView();
            loadThresholdUp.setTitle(getString(R.string.cl_load_threshold_up));
            loadThresholdUp.setSummary(getString(R.string.cl_load_threshold_up_summary));
            loadThresholdUp.setUnit("%");
            loadThresholdUp.setProgress(ClusterHotplug.getClusterHotplugLoadThresholdUp());
            loadThresholdUp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ClusterHotplug.setClusterHotplugLoadThresholdUp(position, getActivity());
                }
            });

            clusterHotplug.addItem(loadThresholdUp);
        }

        if (ClusterHotplug.hasClusterHotplugVoteThresholdDown()) {
            SeekBarView voteThresholdDown = new SeekBarView();
            voteThresholdDown.setTitle(getString(R.string.cl_vote_threshold_down));
            voteThresholdDown.setSummary(getString(R.string.cl_vote_threshold_down_summary));
            voteThresholdDown.setUnit("%");
            voteThresholdDown.setProgress(ClusterHotplug.getClusterHotplugVoteThresholdDown());
            voteThresholdDown.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ClusterHotplug.setClusterHotplugVoteThresholdDown(position, getActivity());
                }
            });

            clusterHotplug.addItem(voteThresholdDown);
        }

        if (ClusterHotplug.hasClusterHotplugVoteThresholdUp()) {
            SeekBarView voteThresholdUp = new SeekBarView();
            voteThresholdUp.setTitle(getString(R.string.cl_vote_threshold_up));
            voteThresholdUp.setSummary(getString(R.string.cl_vote_threshold_up_summary));
            voteThresholdUp.setUnit("%");
            voteThresholdUp.setProgress(ClusterHotplug.getClusterHotplugVoteThresholdUp());
            voteThresholdUp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ClusterHotplug.setClusterHotplugVoteThresholdUp(position, getActivity());
                }
            });

            clusterHotplug.addItem(voteThresholdUp);
        }

        if (ClusterHotplug.hasClusterHotplugSamplingTime()) {
            SeekBarView SamplingTime = new SeekBarView();
            SamplingTime.setTitle(getString(R.string.cl_sampling_time));
            SamplingTime.setProgress(ClusterHotplug.getClusterHotplugSamplingTime());
			SamplingTime.setMax(500);
            SamplingTime.setMin(1);
            SamplingTime.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ClusterHotplug.setClusterHotplugSamplingTime(position, getActivity());
                }
            });

            clusterHotplug.addItem(SamplingTime);
        }

        if (clusterHotplug.size() > 0) {
            items.add(clusterHotplug);
        }
    }

    private void mbHotplugInit(List<RecyclerViewItem> items) {
        CardView mbHotplug = new CardView(getActivity());
        mbHotplug.setTitle(mMBHotplug.getMBName(getActivity()));
        mbHotplug.setExpand(false);

        if (mMBHotplug.hasMBGHotplugEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(mMBHotplug.getMBName(getActivity()));
            enable.setSummary(getString(R.string.mb_hotplug_summary));
            enable.setChecked(mMBHotplug.isMBHotplugEnabled());
            enable.addOnSwitchListener((switchView, isChecked)
                    -> mMBHotplug.enableMBHotplug(isChecked, getActivity()));

            mbHotplug.addItem(enable);
            mEnableViews.add(enable);
        }

        if (mMBHotplug.hasMBHotplugScroffSingleCore()) {
            SwitchView scroffSingleCore = new SwitchView();
            scroffSingleCore.setTitle(getString(R.string.screen_off_single_cpu));
            scroffSingleCore.setSummary(getString(R.string.screen_off_single_cpu_summary));
            scroffSingleCore.setChecked(mMBHotplug.isMBHotplugScroffSingleCoreEnabled());
            scroffSingleCore.addOnSwitchListener((switchView, isChecked)
                    -> mMBHotplug.enableMBHotplugScroffSingleCore(isChecked, getActivity()));

            mbHotplug.addItem(scroffSingleCore);
        }

        if (mMBHotplug.hasMBHotplugMinCpus()) {
            SeekBarView minCpus = new SeekBarView();
            minCpus.setTitle(getString(R.string.min_cpu_online));
            minCpus.setSummary(getString(R.string.min_cpu_online_summary));
            minCpus.setMax(mCPUFreq.getCpuCount());
            minCpus.setMin(1);
            minCpus.setProgress(mMBHotplug.getMBHotplugMinCpus() - 1);
            minCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugMinCpus(position + 1, getActivity());
                }
            });

            mbHotplug.addItem(minCpus);
        }

        if (mMBHotplug.hasMBHotplugMaxCpus()) {
            SeekBarView maxCpus = new SeekBarView();
            maxCpus.setTitle(getString(R.string.max_cpu_online));
            maxCpus.setSummary(getString(R.string.max_cpu_online_summary));
            maxCpus.setMax(mCPUFreq.getCpuCount());
            maxCpus.setMin(1);
            maxCpus.setProgress(mMBHotplug.getMBHotplugMaxCpus() - 1);
            maxCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugMaxCpus(position + 1, getActivity());
                }
            });

            mbHotplug.addItem(maxCpus);
        }

        if (mMBHotplug.hasMBHotplugMaxCpusOnlineSusp()) {
            SeekBarView maxCpusOnlineSusp = new SeekBarView();
            maxCpusOnlineSusp.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCpusOnlineSusp.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCpusOnlineSusp.setMax(mCPUFreq.getCpuCount());
            maxCpusOnlineSusp.setMin(1);
            maxCpusOnlineSusp.setProgress(mMBHotplug.getMBHotplugMaxCpusOnlineSusp() - 1);
            maxCpusOnlineSusp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugMaxCpusOnlineSusp(position + 1, getActivity());
                }
            });

            mbHotplug.addItem(maxCpusOnlineSusp);
        }

        if (mMBHotplug.hasMBHotplugIdleFreq() && mCPUFreq.getFreqs() != null) {
            SelectView idleFreq = new SelectView();
            idleFreq.setTitle(getString(R.string.idle_frequency));
            idleFreq.setSummary(getString(R.string.idle_frequency_summary));
            idleFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            idleFreq.setItem((mMBHotplug.getMBHotplugIdleFreq() / 1000) + getString(R.string.mhz));
            idleFreq.setOnItemSelected((selectView, position, item)
                    -> mMBHotplug.setMBHotplugIdleFreq(
                    mCPUFreq.getFreqs().get(position), getActivity()));

            mbHotplug.addItem(idleFreq);
        }

        if (mMBHotplug.hasMBHotplugBoostEnable()) {
            SwitchView boost = new SwitchView();
            boost.setTitle(getString(R.string.touch_boost));
            boost.setSummary(getString(R.string.touch_boost_summary));
            boost.setChecked(mMBHotplug.isMBHotplugBoostEnabled());
            boost.addOnSwitchListener((switchView, isChecked)
                    -> mMBHotplug.enableMBHotplugBoost(isChecked, getActivity()));

            mbHotplug.addItem(boost);
        }

        if (mMBHotplug.hasMBHotplugBoostTime()) {
            SeekBarView boostTime = new SeekBarView();
            boostTime.setTitle(getString(R.string.touch_boost_time));
            boostTime.setSummary(getString(R.string.touch_boost_time_summary));
            boostTime.setUnit(getString(R.string.ms));
            boostTime.setMax(5000);
            boostTime.setOffset(100);
            boostTime.setProgress(mMBHotplug.getMBHotplugBoostTime() / 100);
            boostTime.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugBoostTime(position * 100, getActivity());
                }
            });

            mbHotplug.addItem(boostTime);
        }

        if (mMBHotplug.hasMBHotplugCpusBoosted()) {
            SeekBarView cpusBoosted = new SeekBarView();
            cpusBoosted.setTitle(getString(R.string.cpus_boosted));
            cpusBoosted.setSummary(getString(R.string.cpus_boosted_summary));
            cpusBoosted.setMax(mCPUFreq.getCpuCount());
            cpusBoosted.setMin(1);
            cpusBoosted.setProgress(mMBHotplug.getMBHotplugCpusBoosted());
            cpusBoosted.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugCpusBoosted(position, getActivity());
                }
            });

            mbHotplug.addItem(cpusBoosted);
        }

        if (mMBHotplug.hasMBHotplugBoostFreqs() && mCPUFreq.getFreqs() != null) {
            List<Integer> freqs = mMBHotplug.getMBHotplugBoostFreqs();

            for (int i = 0; i < freqs.size(); i++) {
                SelectView boostFreq = new SelectView();
                boostFreq.setSummary(getString(R.string.boost_frequency_core, i));
                boostFreq.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
                boostFreq.setItem((freqs.get(i) / 1000) + getString(R.string.mhz));
                final int pos = i;
                boostFreq.setOnItemSelected((selectView, position, item)
                        -> mMBHotplug.setMBHotplugBoostFreqs(
                        pos, mCPUFreq.getFreqs().get(position), getActivity()));

                mbHotplug.addItem(boostFreq);
            }
        }

        if (mMBHotplug.hasMBHotplugStartDelay()) {
            SeekBarView startDelay = new SeekBarView();
            startDelay.setTitle(getString(R.string.start_delay));
            startDelay.setSummary(getString(R.string.start_delay_summary));
            startDelay.setUnit(getString(R.string.ms));
            startDelay.setMax(5000);
            startDelay.setOffset(1000);
            startDelay.setProgress(mMBHotplug.getMBHotplugStartDelay() / 1000);
            startDelay.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugStartDelay(position * 1000, getActivity());
                }
            });

            mbHotplug.addItem(startDelay);
        }

        if (mMBHotplug.hasMBHotplugDelay()) {
            SeekBarView delay = new SeekBarView();
            delay.setTitle(getString(R.string.delay));
            delay.setSummary(getString(R.string.delay_summary));
            delay.setUnit(getString(R.string.ms));
            delay.setMax(200);
            delay.setProgress(mMBHotplug.getMBHotplugDelay());
            delay.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugDelay(position, getActivity());
                }
            });

            mbHotplug.addItem(delay);
        }

        if (mMBHotplug.hasMBHotplugPause()) {
            SeekBarView pause = new SeekBarView();
            pause.setTitle(getString(R.string.pause));
            pause.setSummary(getString(R.string.pause_summary));
            pause.setUnit(getString(R.string.ms));
            pause.setMax(5000);
            pause.setOffset(1000);
            pause.setProgress(mMBHotplug.getMBHotplugPause() / 1000);
            pause.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMBHotplug.setMBHotplugPause(position * 1000, getActivity());
                }
            });

            mbHotplug.addItem(pause);
        }

        if (mbHotplug.size() > 0) {
            items.add(mbHotplug);
        }
    }

    private void alucardHotplugInit(List<RecyclerViewItem> items) {
        CardView alucardHotplug = new CardView(getActivity());
        alucardHotplug.setTitle(getString(R.string.alucard_hotplug));
        alucardHotplug.setExpand(false);

        if (AlucardHotplug.hasAlucardHotplugEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.alucard_hotplug));
            enable.setSummary(getString(R.string.alucard_hotplug_summary));
            enable.setChecked(AlucardHotplug.isAlucardHotplugEnable());
			enable.addOnSwitchListener((switchView, isChecked)
			           -> AlucardHotplug.enableAlucardHotplug(isChecked, getActivity()));

            alucardHotplug.addItem(enable);
            mEnableViews.add(enable);
        }

        if (AlucardHotplug.hasAlucardHotplugHpIoIsBusy()) {
            SwitchView ioIsBusy = new SwitchView();
            ioIsBusy.setTitle(getString(R.string.io_is_busy));
            ioIsBusy.setSummary(getString(R.string.io_is_busy_summary));
            ioIsBusy.setChecked(AlucardHotplug.isAlucardHotplugHpIoIsBusyEnable());
            ioIsBusy.addOnSwitchListener((switchView, isChecked)
                    -> AlucardHotplug.enableAlucardHotplugHpIoIsBusy(isChecked, getActivity()));

            alucardHotplug.addItem(ioIsBusy);
        }

        if (AlucardHotplug.hasAlucardHotplugSamplingRate()) {
            SeekBarView samplingRate = new SeekBarView();
            samplingRate.setTitle(getString(R.string.sampling_rate));
            samplingRate.setUnit("%");
            samplingRate.setMin(1);
            samplingRate.setProgress(AlucardHotplug.getAlucardHotplugSamplingRate() - 1);
            samplingRate.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugSamplingRate(position + 1, getActivity());
                }
            });

            alucardHotplug.addItem(samplingRate);
        }

        if (AlucardHotplug.hasAlucardHotplugSuspend()) {
            SwitchView suspend = new SwitchView();
            suspend.setTitle(getString(R.string.suspend));
            suspend.setSummary(getString(R.string.suspend_summary));
            suspend.setChecked(AlucardHotplug.isAlucardHotplugSuspendEnabled());
            suspend.addOnSwitchListener((switchView, isChecked)
                    -> AlucardHotplug.enableAlucardHotplugSuspend(isChecked, getActivity()));

            alucardHotplug.addItem(suspend);
        }

        if (AlucardHotplug.hasAlucardHotplugMinCpusOnline()) {
            SeekBarView minCpusOnline = new SeekBarView();
            minCpusOnline.setTitle(getString(R.string.min_cpu_online));
            minCpusOnline.setSummary(getString(R.string.min_cpu_online_summary));
            minCpusOnline.setMax(mCPUFreq.getCpuCount());
            minCpusOnline.setMin(1);
            minCpusOnline.setProgress(AlucardHotplug.getAlucardHotplugMinCpusOnline() - 1);
            minCpusOnline.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugMinCpusOnline(position + 1, getActivity());
                }
            });

            alucardHotplug.addItem(minCpusOnline);
        }

        if (AlucardHotplug.hasAlucardHotplugMaxCoresLimit()) {
            SeekBarView maxCoresLimit = new SeekBarView();
            maxCoresLimit.setTitle(getString(R.string.max_cpu_online));
            maxCoresLimit.setSummary(getString(R.string.max_cpu_online_summary));
            maxCoresLimit.setMax(mCPUFreq.getCpuCount());
            maxCoresLimit.setMin(1);
            maxCoresLimit.setProgress(AlucardHotplug.getAlucardHotplugMaxCoresLimit() - 1);
            maxCoresLimit.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugMaxCoresLimit(position + 1, getActivity());
                }
            });

            alucardHotplug.addItem(maxCoresLimit);
        }

        if (AlucardHotplug.hasAlucardHotplugMaxCoresLimitSleep()) {
            SeekBarView maxCoresLimitSleep = new SeekBarView();
            maxCoresLimitSleep.setTitle(getString(R.string.max_cpu_online_screen_off));
            maxCoresLimitSleep.setSummary(getString(R.string.max_cpu_online_screen_off_summary));
            maxCoresLimitSleep.setMax(mCPUFreq.getCpuCount());
            maxCoresLimitSleep.setMin(1);
            maxCoresLimitSleep.setProgress(AlucardHotplug.getAlucardHotplugMaxCoresLimitSleep() - 1);
            maxCoresLimitSleep.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugMaxCoresLimitSleep(position + 1, getActivity());
                }
            });

            alucardHotplug.addItem(maxCoresLimitSleep);
        }

        if (AlucardHotplug.hasAlucardHotplugCpuDownRate()) {
            SeekBarView cpuDownRate = new SeekBarView();
            cpuDownRate.setTitle(getString(R.string.cpu_down_rate));
            cpuDownRate.setUnit("%");
            cpuDownRate.setMin(1);
            cpuDownRate.setProgress(AlucardHotplug.getAlucardHotplugCpuDownRate() - 1);
            cpuDownRate.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugCpuDownRate(position + 1, getActivity());
                }
            });

            alucardHotplug.addItem(cpuDownRate);
        }

        if (AlucardHotplug.hasAlucardHotplugCpuUpRate()) {
            SeekBarView cpuUpRate = new SeekBarView();
            cpuUpRate.setTitle(getString(R.string.cpu_up_rate));
            cpuUpRate.setUnit("%");
            cpuUpRate.setMin(1);
            cpuUpRate.setProgress(AlucardHotplug.getAlucardHotplugCpuUpRate() - 1);
            cpuUpRate.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugCpuUpRate(position + 1, getActivity());
                }
            });

            alucardHotplug.addItem(cpuUpRate);
        }

		// added by @nalas XDA | 2019-07-12 | support for all Alucard features
		if (AlucardHotplug.hasAlucardHotplugFreq_1_1() && mCPUFreq.getFreqs() != null) {
            SelectView freq_1_1 = new SelectView();
            freq_1_1.setTitle("Freq_1_1");
            freq_1_1.setSummary(getString(R.string.freq_1_1));
            freq_1_1.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            freq_1_1.setItem((AlucardHotplug.getAlucardHotplugFreq_1_1() / 1000)
                    + getString(R.string.mhz));
            freq_1_1.setOnItemSelected((selectView, position, item)
                    -> AlucardHotplug.setAlucardHotplugFreq_1_1(
                    mCPUFreq.getFreqs().get(position), getActivity()));

            alucardHotplug.addItem(freq_1_1);
        }

		if (AlucardHotplug.hasAlucardHotplugFreq_2_0() && mCPUFreq.getFreqs() != null) {
            SelectView freq_2_0 = new SelectView();
            freq_2_0.setTitle("Freq_2_0");
            freq_2_0.setSummary(getString(R.string.freq_2_0));
            freq_2_0.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            freq_2_0.setItem((AlucardHotplug.getAlucardHotplugFreq_2_0() / 1000)
                    + getString(R.string.mhz));
            freq_2_0.setOnItemSelected((selectView, position, item)
                    -> AlucardHotplug.setAlucardHotplugFreq_1_1(
                    mCPUFreq.getFreqs().get(position), getActivity()));

            alucardHotplug.addItem(freq_2_0);
        }

		if (AlucardHotplug.hasAlucardHotplugFreq_2_1() && mCPUFreq.getFreqs() != null) {
            SelectView freq_2_1 = new SelectView();
            freq_2_1.setTitle("Freq_2_1");
            freq_2_1.setSummary(getString(R.string.freq_2_1));
            freq_2_1.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            freq_2_1.setItem((AlucardHotplug.getAlucardHotplugFreq_2_1() / 1000)
                    + getString(R.string.mhz));
            freq_2_1.setOnItemSelected((selectView, position, item)
                    -> AlucardHotplug.setAlucardHotplugFreq_1_1(
                    mCPUFreq.getFreqs().get(position), getActivity()));

            alucardHotplug.addItem(freq_2_1);
        }

		if (AlucardHotplug.hasAlucardHotplugFreq_3_0() && mCPUFreq.getFreqs() != null) {
            SelectView freq_3_0 = new SelectView();
            freq_3_0.setTitle("Freq_3_0");
            freq_3_0.setSummary(getString(R.string.freq_3_0));
            freq_3_0.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            freq_3_0.setItem((AlucardHotplug.getAlucardHotplugFreq_3_0() / 1000)
                    + getString(R.string.mhz));
            freq_3_0.setOnItemSelected((selectView, position, item)
                    -> AlucardHotplug.setAlucardHotplugFreq_1_1(
                    mCPUFreq.getFreqs().get(position), getActivity()));

            alucardHotplug.addItem(freq_3_0);
        }

		if (AlucardHotplug.hasAlucardHotplugFreq_3_1() && mCPUFreq.getFreqs() != null) {
            SelectView freq_3_1 = new SelectView();
            freq_3_1.setTitle("Freq_3_1");
            freq_3_1.setSummary(getString(R.string.freq_3_1));
            freq_3_1.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            freq_3_1.setItem((AlucardHotplug.getAlucardHotplugFreq_3_1() / 1000)
                    + getString(R.string.mhz));
            freq_3_1.setOnItemSelected((selectView, position, item)
                    -> AlucardHotplug.setAlucardHotplugFreq_1_1(
                    mCPUFreq.getFreqs().get(position), getActivity()));

            alucardHotplug.addItem(freq_3_1);
        }

		if (AlucardHotplug.hasAlucardHotplugFreq_4_0() && mCPUFreq.getFreqs() != null) {
            SelectView freq_4_0 = new SelectView();
            freq_4_0.setTitle("Freq_4_0");
            freq_4_0.setSummary(getString(R.string.freq_4_0));
            freq_4_0.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
            freq_4_0.setItem((AlucardHotplug.getAlucardHotplugFreq_4_0() / 1000)
                    + getString(R.string.mhz));
            freq_4_0.setOnItemSelected((selectView, position, item)
                    -> AlucardHotplug.setAlucardHotplugFreq_1_1(
                    mCPUFreq.getFreqs().get(position), getActivity()));

            alucardHotplug.addItem(freq_4_0);
        }

        if (AlucardHotplug.hasAlucardHotplugLoad_1_1()) {
            SeekBarView load_1_1 = new SeekBarView();
            load_1_1.setTitle("Load_1_1");
            load_1_1.setSummary(getString(R.string.load_1_1));
            load_1_1.setUnit("%");
            load_1_1.setMin(0);
            load_1_1.setMax(100);
            load_1_1.setProgress(AlucardHotplug.getAlucardHotplugLoad_1_1());
            load_1_1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugLoad_1_1(position, getActivity());
                }
            });

            alucardHotplug.addItem(load_1_1);
        }

        if (AlucardHotplug.hasAlucardHotplugLoad_2_0()) {
            SeekBarView load_2_0 = new SeekBarView();
            load_2_0.setTitle("Load_2_0");
            load_2_0.setSummary(getString(R.string.load_2_0));
            load_2_0.setUnit("%");
            load_2_0.setMin(0);
            load_2_0.setMax(100);
            load_2_0.setProgress(AlucardHotplug.getAlucardHotplugLoad_2_0());
            load_2_0.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugLoad_2_0(position, getActivity());
                }
            });

            alucardHotplug.addItem(load_2_0);
        }

        if (AlucardHotplug.hasAlucardHotplugLoad_2_1()) {
            SeekBarView load_2_1 = new SeekBarView();
            load_2_1.setTitle("Load_2_1");
            load_2_1.setSummary(getString(R.string.load_2_1));
            load_2_1.setUnit("%");
            load_2_1.setMin(0);
            load_2_1.setMax(100);
            load_2_1.setProgress(AlucardHotplug.getAlucardHotplugLoad_2_1());
            load_2_1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugLoad_2_1(position, getActivity());
                }
            });

            alucardHotplug.addItem(load_2_1);
        }

        if (AlucardHotplug.hasAlucardHotplugLoad_3_0()) {
            SeekBarView load_3_0 = new SeekBarView();
            load_3_0.setTitle("Load_3_0");
            load_3_0.setSummary(getString(R.string.load_3_0));
            load_3_0.setUnit("%");
            load_3_0.setMin(0);
            load_3_0.setMax(100);
            load_3_0.setProgress(AlucardHotplug.getAlucardHotplugLoad_3_0());
            load_3_0.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugLoad_3_0(position, getActivity());
                }
            });

            alucardHotplug.addItem(load_3_0);
        }

        if (AlucardHotplug.hasAlucardHotplugLoad_3_1()) {
            SeekBarView load_3_1 = new SeekBarView();
            load_3_1.setTitle("Load_3_1");
            load_3_1.setSummary(getString(R.string.load_3_1));
            load_3_1.setUnit("%");
            load_3_1.setMin(0);
            load_3_1.setMax(100);
            load_3_1.setProgress(AlucardHotplug.getAlucardHotplugLoad_3_1());
            load_3_1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugLoad_3_1(position, getActivity());
                }
            });

            alucardHotplug.addItem(load_3_1);
        }

        if (AlucardHotplug.hasAlucardHotplugLoad_4_0()) {
            SeekBarView load_4_0 = new SeekBarView();
            load_4_0.setTitle("Load_4_0");
            load_4_0.setSummary(getString(R.string.load_4_0));
            load_4_0.setUnit("%");
            load_4_0.setMin(0);
            load_4_0.setMax(100);
            load_4_0.setProgress(AlucardHotplug.getAlucardHotplugLoad_4_0());
            load_4_0.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugLoad_4_0(position, getActivity());
                }
            });

            alucardHotplug.addItem(load_4_0);
        }

        if (AlucardHotplug.hasAlucardHotplugRate_1_1()) {
            SeekBarView rate_1_1 = new SeekBarView();
            rate_1_1.setTitle("Rate_1_1");
            rate_1_1.setSummary(getString(R.string.rate_1_1));
            rate_1_1.setUnit("");
            rate_1_1.setMin(0);
            rate_1_1.setMax(60);
            rate_1_1.setProgress(AlucardHotplug.getAlucardHotplugRate_1_1());
            rate_1_1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugRate_1_1(position, getActivity());
                }
            });

            alucardHotplug.addItem(rate_1_1);
        }

        if (AlucardHotplug.hasAlucardHotplugRate_2_0()) {
            SeekBarView rate_2_0 = new SeekBarView();
            rate_2_0.setTitle("Rate_2_0");
            rate_2_0.setSummary(getString(R.string.rate_2_0));
            rate_2_0.setUnit("");
            rate_2_0.setMin(0);
            rate_2_0.setMax(10);
            rate_2_0.setProgress(AlucardHotplug.getAlucardHotplugRate_2_0());
            rate_2_0.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugRate_2_0(position, getActivity());
                }
            });

            alucardHotplug.addItem(rate_2_0);
        }

        if (AlucardHotplug.hasAlucardHotplugRate_2_1()) {
            SeekBarView rate_2_1 = new SeekBarView();
            rate_2_1.setTitle("Rate_2_1");
            rate_2_1.setSummary(getString(R.string.rate_2_1));
            rate_2_1.setUnit("");
            rate_2_1.setMin(0);
            rate_2_1.setMax(10);
            rate_2_1.setProgress(AlucardHotplug.getAlucardHotplugRate_2_1());
            rate_2_1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugRate_2_1(position, getActivity());
                }
            });

            alucardHotplug.addItem(rate_2_1);
        }

        if (AlucardHotplug.hasAlucardHotplugRate_3_0()) {
            SeekBarView rate_3_0 = new SeekBarView();
            rate_3_0.setTitle("Rate_3_0");
            rate_3_0.setSummary(getString(R.string.rate_3_0));
            rate_3_0.setUnit("");
            rate_3_0.setMin(0);
            rate_3_0.setMax(10);
            rate_3_0.setProgress(AlucardHotplug.getAlucardHotplugRate_3_0());
            rate_3_0.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugRate_3_0(position, getActivity());
                }
            });

            alucardHotplug.addItem(rate_3_0);
        }

        if (AlucardHotplug.hasAlucardHotplugRate_3_1()) {
            SeekBarView rate_3_1 = new SeekBarView();
            rate_3_1.setTitle("Rate_3_1");
            rate_3_1.setSummary(getString(R.string.rate_3_1));
            rate_3_1.setUnit("");
            rate_3_1.setMin(0);
            rate_3_1.setMax(10);
            rate_3_1.setProgress(AlucardHotplug.getAlucardHotplugRate_3_1());
            rate_3_1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugRate_3_1(position, getActivity());
                }
            });

            alucardHotplug.addItem(rate_3_1);
        }

        if (AlucardHotplug.hasAlucardHotplugRate_4_0()) {
            SeekBarView rate_4_0 = new SeekBarView();
            rate_4_0.setTitle("Rate_4_0");
            rate_4_0.setSummary(getString(R.string.rate_4_0));
            rate_4_0.setUnit("");
            rate_4_0.setMin(0);
            rate_4_0.setMax(10);
            rate_4_0.setProgress(AlucardHotplug.getAlucardHotplugRate_4_0());
            rate_4_0.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugRate_4_0(position, getActivity());
                }
            });

            alucardHotplug.addItem(rate_4_0);
        }

        if (AlucardHotplug.hasAlucardHotplugRQ_1_1()) {
            SeekBarView RQ_1_1 = new SeekBarView();
            RQ_1_1.setTitle("Run Queue_1_1");
            RQ_1_1.setSummary(getString(R.string.rq_1_1));
            RQ_1_1.setUnit("mS");
            RQ_1_1.setMin(0);
            RQ_1_1.setMax(2000);
            RQ_1_1.setProgress(AlucardHotplug.getAlucardHotplugRQ_1_1());
            RQ_1_1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugRate_1_1(position, getActivity());
                }
            });

            alucardHotplug.addItem(RQ_1_1);
        }

        if (AlucardHotplug.hasAlucardHotplugRQ_2_0()) {
            SeekBarView RQ_2_0 = new SeekBarView();
            RQ_2_0.setTitle("Run Queue_2_0");
            RQ_2_0.setSummary(getString(R.string.rq_2_0));
            RQ_2_0.setUnit("mS");
            RQ_2_0.setMin(0);
            RQ_2_0.setMax(2000);
            RQ_2_0.setProgress(AlucardHotplug.getAlucardHotplugRQ_2_0());
            RQ_2_0.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugRQ_2_0(position, getActivity());
                }
            });

            alucardHotplug.addItem(RQ_2_0);
        }

        if (AlucardHotplug.hasAlucardHotplugRQ_2_1()) {
            SeekBarView RQ_2_1 = new SeekBarView();
            RQ_2_1.setTitle("Run Queue_2_1");
            RQ_2_1.setSummary(getString(R.string.rq_2_1));
            RQ_2_1.setUnit("mS");
            RQ_2_1.setMin(0);
            RQ_2_1.setMax(2000);
            RQ_2_1.setProgress(AlucardHotplug.getAlucardHotplugRQ_2_1());
            RQ_2_1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugRQ_2_1(position, getActivity());
                }
            });

            alucardHotplug.addItem(RQ_2_1);
        }

        if (AlucardHotplug.hasAlucardHotplugRQ_3_0()) {
            SeekBarView RQ_3_0 = new SeekBarView();
            RQ_3_0.setTitle("Run Queue_3_0");
            RQ_3_0.setSummary(getString(R.string.rq_3_0));
            RQ_3_0.setUnit("mS");
            RQ_3_0.setMin(0);
            RQ_3_0.setMax(2000);
            RQ_3_0.setProgress(AlucardHotplug.getAlucardHotplugRQ_3_0());
            RQ_3_0.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugRQ_3_0(position, getActivity());
                }
            });

            alucardHotplug.addItem(RQ_3_0);
        }

        if (AlucardHotplug.hasAlucardHotplugRQ_3_1()) {
            SeekBarView RQ_3_1 = new SeekBarView();
            RQ_3_1.setTitle("Run Queue_3_1");
            RQ_3_1.setSummary(getString(R.string.rq_3_1));
            RQ_3_1.setUnit("mS");
            RQ_3_1.setMin(0);
            RQ_3_1.setMax(2000);
            RQ_3_1.setProgress(AlucardHotplug.getAlucardHotplugRQ_3_1());
            RQ_3_1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugRQ_3_1(position, getActivity());
                }
            });

            alucardHotplug.addItem(RQ_3_1);
        }

        if (AlucardHotplug.hasAlucardHotplugRQ_4_0()) {
            SeekBarView RQ_4_0 = new SeekBarView();
            RQ_4_0.setTitle("Run Queue_4_0");
            RQ_4_0.setSummary(getString(R.string.rq_4_0));
            RQ_4_0.setUnit("mS");
            RQ_4_0.setMin(0);
            RQ_4_0.setMax(2000);
            RQ_4_0.setProgress(AlucardHotplug.getAlucardHotplugRQ_4_0());
            RQ_4_0.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AlucardHotplug.setAlucardHotplugRQ_4_0(position, getActivity());
                }
            });

            alucardHotplug.addItem(RQ_4_0);
        }

        if (alucardHotplug.size() > 0) {
            items.add(alucardHotplug);
        }
    }

    private void thunderPlugInit(List<RecyclerViewItem> items) {
        CardView thunderPlug = new CardView(getActivity());
        if (ThunderPlug.hasThunderPlugVersion()){
            thunderPlug.setTitle(ThunderPlug.getThunderPlugVersion());
        }else {
            thunderPlug.setTitle(getString(R.string.thunderplug));
        }
        thunderPlug.setExpand(false);

        if (ThunderPlug.hasThunderPlugEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.thunderplug));
            enable.setSummary(getString(R.string.thunderplug_summary));
            enable.setChecked(ThunderPlug.isThunderPlugEnabled());
            enable.addOnSwitchListener((switchView, isChecked) -> {
                if (isChecked) {
                    ThunderPlug.enableStateNotifier(true, getActivity());
                    ThunderPlug.enableThunderPlug(true, getActivity());
                } else {
                    ThunderPlug.enableThunderPlug(false, getActivity());
                    ThunderPlug.enableStateNotifier(false, getActivity());
                }
            });

            thunderPlug.addItem(enable);
            mEnableViews.add(enable);
        }

        if (ThunderPlug.hasThunderPlugSuspendCpus()) {
            SeekBarView suspendCpus = new SeekBarView();
            suspendCpus.setTitle(getString(R.string.min_cpu_online_screen_off));
            suspendCpus.setSummary(getString(R.string.min_cpu_online_screen_off_summary));
            suspendCpus.setMax(mCPUFreq.getCpuCount());
            suspendCpus.setMin(1);
            suspendCpus.setProgress(ThunderPlug.getThunderPlugSuspendCpus() - 1);
            suspendCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ThunderPlug.setThunderPlugSuspendCpus(position + 1, getActivity());
                }
            });

            thunderPlug.addItem(suspendCpus);
        }

        if (ThunderPlug.hasThunderPlugEnduranceLevel()) {
            SelectView enduranceLevel = new SelectView();
            enduranceLevel.setTitle(getString(R.string.endurance_level));
            enduranceLevel.setSummary(getString(R.string.endurance_level_summary));
            enduranceLevel.setItems(Arrays.asList(getResources().getStringArray(R.array.endurance_level_items)));
            enduranceLevel.setItem(ThunderPlug.getThunderPlugEnduranceLevel());
            enduranceLevel.setOnItemSelected((selectView, position, item)
                    -> ThunderPlug.setThunderPlugEnduranceLevel(position, getActivity()));

            thunderPlug.addItem(enduranceLevel);
        }

        if (ThunderPlug.hasThunderPlugSamplingRate()) {
            SeekBarView samplingRate = new SeekBarView();
            samplingRate.setTitle(getString(R.string.sampling_rate));
            samplingRate.setMax(800); // was 500
            samplingRate.setMin(10);
            samplingRate.setOffset(10);
            samplingRate.setProgress(ThunderPlug.getThunderPlugSamplingRate() / 10 - 1);
            samplingRate.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ThunderPlug.setThunderPlugSamplingRate((position + 1) * 10, getActivity());
                }
            });

            thunderPlug.addItem(samplingRate);
        }

        if (ThunderPlug.hasThunderPlugLoadThreshold()) {
            SeekBarView loadThreadshold = new SeekBarView();
            loadThreadshold.setTitle(getString(R.string.load_threshold));
            loadThreadshold.setSummary(getString(R.string.load_threshold_summary));
            loadThreadshold.setMin(11);
            loadThreadshold.setProgress(ThunderPlug.getThunderPlugLoadThreshold() - 11);
            loadThreadshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ThunderPlug.setThunderPlugLoadThreshold(position + 11, getActivity());
                }
            });

            thunderPlug.addItem(loadThreadshold);
        }

        if (ThunderPlug.hasThunderPlugTouchBoost()) {
            SwitchView touchBoost = new SwitchView();
            touchBoost.setTitle(getString(R.string.touch_boost));
            touchBoost.setSummary(getString(R.string.touch_boost_summary));
            touchBoost.setChecked(ThunderPlug.isThunderPlugTouchBoostEnabled());
            touchBoost.addOnSwitchListener((switchView, isChecked)
                    -> ThunderPlug.enableThunderPlugTouchBoost(isChecked, getActivity()));

            thunderPlug.addItem(touchBoost);
        }
		
        if (ThunderPlug.hasThunderPlugSuspend()) {
            SwitchView h_suspend = new SwitchView();
            h_suspend.setTitle(getString(R.string.thunder_plug_suspend));
            h_suspend.setSummary(getString(R.string.thunder_plug_suspend_summary));
            h_suspend.setChecked(ThunderPlug.isThunderPlugSuspendEnable());
            h_suspend.addOnSwitchListener((switchView, isChecked)
                    -> ThunderPlug.enableThunderPlugSuspend(isChecked, getActivity()));

            thunderPlug.addItem(h_suspend);
        }

        if (ThunderPlug.hasThunderPlugCpusBoosted()) {
            SeekBarView cpusBoosted = new SeekBarView();
            cpusBoosted.setTitle(getString(R.string.cpus_boosted));
            cpusBoosted.setSummary(getString(R.string.cpus_boosted_summary));
            cpusBoosted.setMax(8);
            cpusBoosted.setMin(1);
            cpusBoosted.setProgress(ThunderPlug.getThunderPlugCpusBoosted() - 1);
            cpusBoosted.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ThunderPlug.setThunderPlugLoadCpusBoosted(position + 1, getActivity());
                }
            });

            thunderPlug.addItem(cpusBoosted);
        }

        if (ThunderPlug.hasThunderPlugBoostLockDuration()) {
            SeekBarView lockDuration = new SeekBarView();
            lockDuration.setTitle(getString(R.string.lock_duration));
            lockDuration.setSummary(getString(R.string.lock_duration_summary));
            lockDuration.setUnit(getString(R.string.ms));
            lockDuration.setMax(5000);
            lockDuration.setOffset(50);
			lockDuration.setMin(100);
            lockDuration.setProgress(ThunderPlug.getThunderPlugBoostLockDuration() / 1000);
            lockDuration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ThunderPlug.setThunderPlugBoostLockDuration(position * 1000, getActivity());
                }
            });

            thunderPlug.addItem(lockDuration);
        }

        if (ThunderPlug.hasThunderPlugMaxCoreOnline()) {
            SeekBarView maxCoreOnLine = new SeekBarView();
            maxCoreOnLine.setTitle(getString(R.string.cpus_max_core_online));
            maxCoreOnLine.setSummary(getString(R.string.cpus_max_core_online_summary));
            maxCoreOnLine.setMax(8);
            maxCoreOnLine.setMin(1);
            maxCoreOnLine.setProgress(ThunderPlug.getThunderPlugMaxCoreOnline() - 1);
            maxCoreOnLine.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ThunderPlug.setThunderPlugMaxCoreOnline(position + 1, getActivity());
                }
            });

            thunderPlug.addItem(maxCoreOnLine);
        }
		
        if (ThunderPlug.hasThunderPlugMinCoreOnline()) {
            SeekBarView minCoreOnLine = new SeekBarView();
            minCoreOnLine.setTitle(getString(R.string.cpus_min_core_online));
            minCoreOnLine.setSummary(getString(R.string.cpus_min_core_online_summary));
            minCoreOnLine.setMax(8);
            minCoreOnLine.setMin(1);
            minCoreOnLine.setProgress(ThunderPlug.getThunderPlugMinCoreOnline() - 1);
            minCoreOnLine.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ThunderPlug.setThunderPlugMinCoreOnline(position + 1, getActivity());
                }
            });

            thunderPlug.addItem(minCoreOnLine);
        }

        if (thunderPlug.size() > 0) {
            items.add(thunderPlug);
        }
    }

    private void zenDecisionInit(List<RecyclerViewItem> items) {
        CardView zenDecision = new CardView(getActivity());
        zenDecision.setTitle(getString(R.string.zen_decision));
        zenDecision.setExpand(false);

        if (ZenDecision.hasZenDecisionEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.zen_decision));
            enable.setSummary(getString(R.string.zen_decision_summary));
            enable.setChecked(ZenDecision.isZenDecisionEnabled());
            enable.addOnSwitchListener((switchView, isChecked)
                    -> ZenDecision.enableZenDecision(isChecked, getActivity()));

            zenDecision.addItem(enable);
            mEnableViews.add(enable);
        }

        if (ZenDecision.hasZenDecisionWakeWaitTime()) {
            SeekBarView wakeWaitTime = new SeekBarView();
            wakeWaitTime.setTitle(getString(R.string.wake_wait_time));
            wakeWaitTime.setSummary(getString(R.string.wake_wait_time_summary));
            wakeWaitTime.setUnit(getString(R.string.ms));
            wakeWaitTime.setMax(6000);
            wakeWaitTime.setOffset(1000);
            wakeWaitTime.setProgress(ZenDecision.getZenDecisionWakeWaitTime() / 1000);
            wakeWaitTime.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ZenDecision.setZenDecisionWakeWaitTime(position * 1000, getActivity());
                }
            });

            zenDecision.addItem(wakeWaitTime);
        }

        if (ZenDecision.hasZenDecisionBatThresholdIgnore()) {
            SeekBarView batThresholdIgnore = new SeekBarView();
            batThresholdIgnore.setTitle(getString(R.string.bat_threshold_ignore));
            batThresholdIgnore.setSummary(getString(R.string.bat_threshold_ignore_summary));
            batThresholdIgnore.setUnit("%");
            batThresholdIgnore.setMin(1);
            batThresholdIgnore.setProgress(ZenDecision.getZenDecisionBatThresholdIgnore());
            batThresholdIgnore.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ZenDecision.setZenDecisionBatThresholdIgnore(position, getActivity());
                }
            });

            zenDecision.addItem(batThresholdIgnore);
        }

        if (zenDecision.size() > 0) {
            items.add(zenDecision);
        }
    }

    private void autoSmpInit(List<RecyclerViewItem> items) {
        CardView autoSmp = new CardView(getActivity());
        autoSmp.setTitle(getString(R.string.autosmp));
        autoSmp.setExpand(false);

        if (AutoSmp.hasAutoSmpEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.autosmp));
            enable.setSummary(getString(R.string.autosmp_summary));
            enable.setChecked(AutoSmp.isAutoSmpEnabled());
            enable.addOnSwitchListener((switchView, isChecked)
                    -> AutoSmp.enableAutoSmp(isChecked, getActivity()));

            autoSmp.addItem(enable);
            mEnableViews.add(enable);
        }

        if (AutoSmp.hasAutoSmpCpufreqDown()) {
            SeekBarView cpuFreqDown = new SeekBarView();
            cpuFreqDown.setTitle(getString(R.string.downrate_limits));
            cpuFreqDown.setUnit("%");
            cpuFreqDown.setProgress(AutoSmp.getAutoSmpCpufreqDown());
            cpuFreqDown.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpCpufreqDown(position, getActivity());
                }
            });

            autoSmp.addItem(cpuFreqDown);
        }

        if (AutoSmp.hasAutoSmpCpufreqUp()) {
            SeekBarView cpuFreqUp = new SeekBarView();
            cpuFreqUp.setTitle(getString(R.string.uprate_limits));
            cpuFreqUp.setUnit("%");
            cpuFreqUp.setProgress(AutoSmp.getAutoSmpCpufreqUp());
            cpuFreqUp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpCpufreqUp(position, getActivity());
                }
            });

            autoSmp.addItem(cpuFreqUp);
        }

        if (AutoSmp.hasAutoSmpCpufreqDown_bc()) {
            SeekBarView cpuFreqDown_bc = new SeekBarView();
            cpuFreqDown_bc.setTitle(getString(R.string.downrate_limits_bc));
            cpuFreqDown_bc.setUnit("%");
            cpuFreqDown_bc.setProgress(AutoSmp.getAutoSmpCpufreqDown_bc());
            cpuFreqDown_bc.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpCpufreqDown_bc(position, getActivity());
                }
            });

            autoSmp.addItem(cpuFreqDown_bc);
        }

        if (AutoSmp.hasAutoSmpCpufreqDown_lc()) {
            SeekBarView cpuFreqDown_lc = new SeekBarView();
            cpuFreqDown_lc.setTitle(getString(R.string.downrate_limits_lc));
            cpuFreqDown_lc.setUnit("%");
            cpuFreqDown_lc.setProgress(AutoSmp.getAutoSmpCpufreqDown_lc());
            cpuFreqDown_lc.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpCpufreqDown_lc(position, getActivity());
                }
            });

            autoSmp.addItem(cpuFreqDown_lc);
        }

        if (AutoSmp.hasAutoSmpCpufreqUp_bc()) {
            SeekBarView cpuFreqUp_bc = new SeekBarView();
            cpuFreqUp_bc.setTitle(getString(R.string.uprate_limits_bc));
            cpuFreqUp_bc.setUnit("%");
            cpuFreqUp_bc.setProgress(AutoSmp.getAutoSmpCpufreqUp_bc());
            cpuFreqUp_bc.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpCpufreqUp_bc(position, getActivity());
                }
            });

            autoSmp.addItem(cpuFreqUp_bc);
        }

        if (AutoSmp.hasAutoSmpCpufreqUp_lc()) {
            SeekBarView cpuFreqUp_lc = new SeekBarView();
            cpuFreqUp_lc.setTitle(getString(R.string.uprate_limits_lc));
            cpuFreqUp_lc.setUnit("%");
            cpuFreqUp_lc.setProgress(AutoSmp.getAutoSmpCpufreqUp_lc());
            cpuFreqUp_lc.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpCpufreqUp_lc(position, getActivity());
                }
            });

            autoSmp.addItem(cpuFreqUp_lc);
        }

        if (AutoSmp.hasAutoSmpCycleDown()) {
            SeekBarView cycleDown = new SeekBarView();
            cycleDown.setTitle(getString(R.string.cycle_down));
            cycleDown.setSummary(getString(R.string.cycle_down_summary));
            cycleDown.setMax(mCPUFreq.getCpuCount());
            cycleDown.setProgress(AutoSmp.getAutoSmpCycleDown());
            cycleDown.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpCycleDown(position, getActivity());
                }
            });

            autoSmp.addItem(cycleDown);
        }

        if (AutoSmp.hasAutoSmpCycleUp()) {
            SeekBarView cycleUp = new SeekBarView();
            cycleUp.setTitle(getString(R.string.cycle_up));
            cycleUp.setSummary(getString(R.string.cycle_up_summary));
            cycleUp.setMax(mCPUFreq.getCpuCount());
            cycleUp.setProgress(AutoSmp.getAutoSmpCycleUp());
            cycleUp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpCycleUp(position, getActivity());
                }
            });

            autoSmp.addItem(cycleUp);
        }

        if (AutoSmp.hasAutoSmpDelay()) {
            SeekBarView delay = new SeekBarView();
            delay.setTitle(getString(R.string.delay));
            delay.setSummary(getString(R.string.delay_summary));
            delay.setUnit(getString(R.string.ms));
            delay.setMax(500);
            delay.setProgress(AutoSmp.getAutoSmpDelay());
            delay.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpDelay(position, getActivity());
                }
            });

            autoSmp.addItem(delay);
        }

        if (AutoSmp.hasAutoSmpMaxCpus()) {
            SeekBarView maxCpus = new SeekBarView();
            maxCpus.setTitle(getString(R.string.max_cpu_online));
            maxCpus.setSummary(getString(R.string.max_cpu_online_summary));
            maxCpus.setMax(mCPUFreq.getCpuCount());
            maxCpus.setMin(1);
            maxCpus.setProgress(AutoSmp.getAutoSmpMaxCpus() - 1);
            maxCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpMaxCpus(position + 1, getActivity());
                }
            });

            autoSmp.addItem(maxCpus);
        }

        if (AutoSmp.hasAutoSmpMinCpus()) {
            SeekBarView minCpus = new SeekBarView();
            minCpus.setTitle(getString(R.string.min_cpu_online));
            minCpus.setSummary(getString(R.string.min_cpu_online_summary));
            minCpus.setMax(mCPUFreq.getCpuCount());
            minCpus.setMin(1);
            minCpus.setProgress(AutoSmp.getAutoSmpMinCpus() - 1);
            minCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpMinCpus(position + 1, getActivity());
                }
            });

            autoSmp.addItem(minCpus);
        }

        if (AutoSmp.hasAutoSmpMaxCpus_bc()) {
            SeekBarView maxCpus_bc = new SeekBarView();
            maxCpus_bc.setTitle(getString(R.string.max_cpu_online_bc));
            maxCpus_bc.setSummary(getString(R.string.max_cpu_online_bc_summary));
            maxCpus_bc.setMax(mCPUFreq.getCpuCount());
            maxCpus_bc.setMin(1);
            maxCpus_bc.setProgress(AutoSmp.getAutoSmpMaxCpus_bc() - 1);
            maxCpus_bc.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpMaxCpus_bc(position + 1, getActivity());
                }
            });

            autoSmp.addItem(maxCpus_bc);
        }

        if (AutoSmp.hasAutoSmpMaxCpus_lc()) {
            SeekBarView maxCpus_lc = new SeekBarView();
            maxCpus_lc.setTitle(getString(R.string.max_cpu_online_lc));
            maxCpus_lc.setSummary(getString(R.string.max_cpu_online_lc_summary));
            maxCpus_lc.setMax(mCPUFreq.getCpuCount());
            maxCpus_lc.setMin(1);
            maxCpus_lc.setProgress(AutoSmp.getAutoSmpMaxCpus_lc() - 1);
            maxCpus_lc.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpMaxCpus_lc(position + 1, getActivity());
                }
            });

            autoSmp.addItem(maxCpus_lc);
        }

        if (AutoSmp.hasAutoSmpMinCpus_bc()) {
            SeekBarView minCpus_bc = new SeekBarView();
            minCpus_bc.setTitle(getString(R.string.min_cpu_online_bc));
            minCpus_bc.setSummary(getString(R.string.min_cpu_online_bc_summary));
            minCpus_bc.setMax(mCPUFreq.getCpuCount());
            minCpus_bc.setMin(1);
            minCpus_bc.setProgress(AutoSmp.getAutoSmpMinCpus_bc() - 1);
            minCpus_bc.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpMinCpus_bc(position + 1, getActivity());
                }
            });

            autoSmp.addItem(minCpus_bc);
        }

        if (AutoSmp.hasAutoSmpMinCpus_lc()) {
            SeekBarView minCpus_lc = new SeekBarView();
            minCpus_lc.setTitle(getString(R.string.min_cpu_online_lc));
            minCpus_lc.setSummary(getString(R.string.min_cpu_online_lc_summary));
            minCpus_lc.setMax(mCPUFreq.getCpuCount());
            minCpus_lc.setMin(1);
            minCpus_lc.setProgress(AutoSmp.getAutoSmpMinCpus_lc() - 1);
            minCpus_lc.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AutoSmp.setAutoSmpMinCpus_lc(position + 1, getActivity());
                }
            });

            autoSmp.addItem(minCpus_lc);
        }

        if (AutoSmp.hasAutoSmpScroffSingleCore()) {
            SwitchView scroffSingleCore = new SwitchView();
            scroffSingleCore.setTitle(getString(R.string.screen_off_single_cpu));
            scroffSingleCore.setSummary(getString(R.string.screen_off_single_cpu_summary));
            scroffSingleCore.setChecked(AutoSmp.isAutoSmpScroffSingleCoreEnabled());
            scroffSingleCore.addOnSwitchListener((switchView, isChecked)
                    -> AutoSmp.enableAutoSmpScroffSingleCoreActive(isChecked, getActivity()));

            autoSmp.addItem(scroffSingleCore);
        }

        if (autoSmp.size() > 0) {
            items.add(autoSmp);
        }
    }

    private void coreCtlInit(List<RecyclerViewItem> items) {
        CardView coreCtl = new CardView(getActivity());
        coreCtl.setTitle(getString(mCoreCtl.hasEnable() ? R.string.hcube : R.string.core_control));
        coreCtl.setExpand(false);

        if (mCoreCtl.hasMinCpus(mCPUFreq.getBigCpu())) {
            SeekBarView minCpus = new SeekBarView();
            minCpus.setTitle(getString(R.string.min_cpus_big));
            minCpus.setSummary(getString(R.string.min_cpus_big_summary));
            minCpus.setMax(mCPUFreq.getBigCpuRange().size());
            minCpus.setProgress(mCoreCtl.getMinCpus(mCPUFreq.getBigCpu()));
            minCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCoreCtl.setMinCpus(position, mCPUFreq.getBigCpu(), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            coreCtl.addItem(minCpus);
        }

        if (mCoreCtl.hasBusyDownThreshold()) {
            SeekBarView busyDownThreshold = new SeekBarView();
            busyDownThreshold.setTitle(getString(R.string.busy_down_threshold));
            busyDownThreshold.setSummary(getString(R.string.busy_down_threshold_summary));
            busyDownThreshold.setProgress(mCoreCtl.getBusyDownThreshold());
            busyDownThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCoreCtl.setBusyDownThreshold(position, getActivity());
                }
            });

            coreCtl.addItem(busyDownThreshold);
        }

        if (mCoreCtl.hasBusyUpThreshold()) {
            SeekBarView busyUpThreshold = new SeekBarView();
            busyUpThreshold.setTitle(getString(R.string.busy_up_threshold));
            busyUpThreshold.setSummary(getString(R.string.busy_up_threshold_summary));
            busyUpThreshold.setProgress(mCoreCtl.getBusyUpThreshold());
            busyUpThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCoreCtl.setBusyUpThreshold(position, getActivity());
                }
            });

            coreCtl.addItem(busyUpThreshold);
        }

        if (mCoreCtl.hasOfflineDelayMs()) {
            SeekBarView offlineDelayMs = new SeekBarView();
            offlineDelayMs.setTitle(getString(R.string.offline_delay));
            offlineDelayMs.setSummary(getString(R.string.offline_delay_summary));
            offlineDelayMs.setUnit(getString(R.string.ms));
            offlineDelayMs.setMax(5000);
            offlineDelayMs.setOffset(100);
            offlineDelayMs.setProgress(mCoreCtl.getOfflineDelayMs() / 100);
            offlineDelayMs.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCoreCtl.setOfflineDelayMs(position * 100, getActivity());
                }
            });

            coreCtl.addItem(offlineDelayMs);
        }

        if (mCoreCtl.hasOnlineDelayMs()) {
            SeekBarView onlineDelayMs = new SeekBarView();
            onlineDelayMs.setTitle(getString(R.string.online_delay));
            onlineDelayMs.setSummary(getString(R.string.online_delay_summary));
            onlineDelayMs.setUnit(getString(R.string.ms));
            onlineDelayMs.setMax(5000);
            onlineDelayMs.setOffset(100);
            onlineDelayMs.setProgress(mCoreCtl.getOnlineDelayMs() / 100);
            onlineDelayMs.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mCoreCtl.setOnlineDelayMs(position * 100, getActivity());
                }
            });

            coreCtl.addItem(onlineDelayMs);
        }

        if (coreCtl.size() > 0) {

            if (mCoreCtl.hasEnable()) {
                SwitchView enable = new SwitchView();
                enable.setTitle(getString(R.string.hcube));
                enable.setSummary(getString(R.string.hcube_summary));
                enable.setChecked(mCoreCtl.isEnabled());
                enable.addOnSwitchListener((switchView, isChecked)
                        -> mCoreCtl.enable(isChecked, getActivity()));

                items.add(enable);
                mEnableViews.add(enable);
            } else {
                DescriptionView description = new DescriptionView();
                description.setTitle(getString(R.string.core_control));
                description.setSummary(getString(R.string.core_control_summary));
                items.add(description);
            }

            items.add(coreCtl);
        }
    }

    private void aioHotplugInit(List<RecyclerViewItem> items) {
        CardView aioHotplug = new CardView(getActivity());
        aioHotplug.setTitle(getString(R.string.aio_hotplug));
        aioHotplug.setExpand(false);

        if (AiOHotplug.hasToggle()) {
            SwitchView toggle = new SwitchView();
            toggle.setTitle(getString(R.string.aio_hotplug));
            toggle.setSummary(getString(R.string.aio_hotplug_summary));
            toggle.setChecked(AiOHotplug.isEnabled());
            toggle.addOnSwitchListener((switchView, isChecked)
                    -> AiOHotplug.enable(isChecked, getActivity()));

            aioHotplug.addItem(toggle);
            mEnableViews.add(toggle);
        }

        if (AiOHotplug.hasCores()) {
            SeekBarView maxCpus = new SeekBarView();
            maxCpus.setTitle(getString(R.string.max_cpu_online));
            maxCpus.setSummary(getString(R.string.max_cpu_online_summary));
            maxCpus.setMax(mCPUFreq.getCpuCount());
            maxCpus.setMin(1);
            maxCpus.setProgress(AiOHotplug.getCores() - 1);
            maxCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AiOHotplug.setCores(position + 1, getActivity());
                }
            });

            aioHotplug.addItem(maxCpus);
        }

        if (mCPUFreq.isBigLITTLE() && AiOHotplug.hasBigCores()) {
            List<String> list = new ArrayList<>();
            list.add("Disable");
            for (int i = 1; i <= mCPUFreq.getBigCpuRange().size(); i++) {
                list.add(String.valueOf(i));
            }

            SeekBarView bigMaxCpus = new SeekBarView();
            bigMaxCpus.setTitle(getString(R.string.max_cpu_online_big));
            bigMaxCpus.setSummary(getString(R.string.max_cpu_online_big_summary));
            bigMaxCpus.setItems(list);
            bigMaxCpus.setProgress(AiOHotplug.getBigCores());
            bigMaxCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AiOHotplug.setBigCores(position, getActivity());
                }
            });

            aioHotplug.addItem(bigMaxCpus);
        }

        if (mCPUFreq.isBigLITTLE() && AiOHotplug.hasLITTLECores()) {
            List<String> list = new ArrayList<>();
            list.add("Disable");
            for (int i = 1; i <= mCPUFreq.getLITTLECpuRange().size(); i++) {
                list.add(String.valueOf(i));
            }

            SeekBarView LITTLEMaxCpus = new SeekBarView();
            LITTLEMaxCpus.setTitle(getString(R.string.max_cpu_online_little));
            LITTLEMaxCpus.setSummary(getString(R.string.max_cpu_online_little_summary));
            LITTLEMaxCpus.setItems(list);
            LITTLEMaxCpus.setProgress(AiOHotplug.getLITTLECores());
            LITTLEMaxCpus.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AiOHotplug.setLITTLECores(position, getActivity());
                }
            });

            aioHotplug.addItem(LITTLEMaxCpus);
        }

        if (aioHotplug.size() > 0) {
            items.add(aioHotplug);
        }
    }
}
