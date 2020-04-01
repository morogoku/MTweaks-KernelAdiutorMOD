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
import com.moro.mtweaks.fragments.BaseFragment;
import com.moro.mtweaks.fragments.recyclerview.RecyclerViewFragment;
import com.moro.mtweaks.utils.AppSettings;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.gpu.AdrenoIdler;
import com.moro.mtweaks.utils.kernel.gpu.AdrenoBoost;
import com.moro.mtweaks.utils.kernel.gpu.GPUFreq;
import com.moro.mtweaks.utils.kernel.gpu.GPUFreqExynos;
import com.moro.mtweaks.utils.kernel.gpu.GPUFreqTmu;
import com.moro.mtweaks.utils.kernel.gpu.SimpleGPU;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.DescriptionView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SeekBarView;
import com.moro.mtweaks.views.recyclerview.SelectView;
import com.moro.mtweaks.views.recyclerview.SwitchView;
import com.moro.mtweaks.views.recyclerview.TitleView;
import com.moro.mtweaks.views.recyclerview.XYGraphView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 12.05.16.
 */
public class GPUFragment extends RecyclerViewFragment {

    private GPUFreq mGPUFreq;
    private GPUFreqExynos mGPUFreqExynos;
    private GPUFreqTmu mGPUFreqTmu;

    private XYGraphView m2dCurFreq;
    private XYGraphView mCurFreq;
    private XYGraphView mUtilization;
    private List<SeekBarView> mVoltages = new ArrayList<>();
    private SeekBarView mSeekbarProf = new SeekBarView();

    private PathReaderFragment mGPUGovernorTunableFragment;

    private static float mVoltMinValue = -100000f;
    private static float mVoltMaxValue = 25000f;
    private static int mVoltStep = 6250;
    public static int mDefZeroPosition = (Math.round(mVoltMaxValue - mVoltMinValue) / mVoltStep) - (Math.round(mVoltMaxValue) / mVoltStep);

    @Override
    protected BaseFragment getForegroundFragment() {
        return mGPUGovernorTunableFragment = new PathReaderFragment();
    }

    @Override
    protected void init() {
        super.init();

        mGPUFreq = GPUFreq.getInstance();
        mGPUFreqExynos = GPUFreqExynos.getInstance();
        mGPUFreqTmu = GPUFreqTmu.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mVoltages.clear();

        freqInit(items);
        if (mGPUFreqExynos.hasPowerPolicy()){
            powerPolicyInit(items);
        }
        if (mGPUFreqExynos.hasGovernor()){
            governorInit(items);
        }
        if (mGPUFreqTmu.supported()){
            throttlingInit(items);
        }
        if (mGPUFreqExynos.hasVoltage()){
            voltageInit(items);
        }
        if (SimpleGPU.supported()) {
            simpleGpuInit(items);
        }
        if (AdrenoIdler.supported()) {
            adrenoIdlerInit(items);
        }
        if (AdrenoBoost.hasAdrenoBoost()) {
            adrenoboostInit(items);
        }
    }

    private void throttlingInit(List<RecyclerViewItem> items) {
        CardView thrott = new CardView(getActivity());
        thrott.setTitle(getString(R.string.gpu_throttling));

        DescriptionView desc = new DescriptionView();
        desc.setSummary(getString(R.string.gpu_throttling_summary));
        thrott.addItem(desc);

        if(mGPUFreqTmu.hasTmu()) {
            SwitchView tmu = new SwitchView();
            tmu.setTitle(getString(R.string.gpu_tmu));
            tmu.setSummary(getString(R.string.gpu_tmu_summary));
            tmu.setChecked(mGPUFreqTmu.isTmuEnabled());
            tmu.addOnSwitchListener((switchView, isChecked)
                    -> mGPUFreqTmu.enableTmu(isChecked, getActivity()));

            thrott.addItem(tmu);
        }


        int value1 = 0;
        int value2 = 0;
        int value3 = 0;
        int value4 = 0;
        int value5 = 0;
        List<String> freqs = mGPUFreqExynos.getFreqs();
        List<Integer> list = mGPUFreqExynos.getAvailableFreqsSort();
        int th1Val = mGPUFreqTmu.getThrottling1();
        int th2Val = mGPUFreqTmu.getThrottling2();
        int th3Val = mGPUFreqTmu.getThrottling3();
        int th4Val = mGPUFreqTmu.getThrottling4();
        int tripVal = mGPUFreqTmu.getTripping();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == th1Val) {
                value1 = i;
            }
            if (list.get(i) == th2Val) {
                value2 = i;
            }
            if (list.get(i) == th3Val) {
                value3 = i;
            }
            if (list.get(i) == th4Val) {
                value4 = i;
            }
            if (list.get(i) == tripVal) {
                value5 = i;
            }
        }

        if(mGPUFreqTmu.hasThrottling1()) {
            SeekBarView th1 = new SeekBarView();
            th1.setTitle(getString(R.string.gpu_throttling1));
            th1.setSummary(getString(R.string.gpu_throttling1_summary));
            th1.setUnit(getString(R.string.mhz));
            th1.setItems(freqs);
            th1.setProgress(value1);
            th1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mGPUFreqTmu.setThrottling1(value, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            thrott.addItem(th1);
        }

        if(mGPUFreqTmu.hasThrottling2()) {
            SeekBarView th2 = new SeekBarView();
            th2.setTitle(getString(R.string.gpu_throttling2));
            th2.setSummary(getString(R.string.gpu_throttling2_summary));
            th2.setUnit(getString(R.string.mhz));
            th2.setItems(freqs);
            th2.setProgress(value2);
            th2.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mGPUFreqTmu.setThrottling2(value, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            thrott.addItem(th2);
        }

        if(mGPUFreqTmu.hasThrottling3()) {
            SeekBarView th3 = new SeekBarView();
            th3.setTitle(getString(R.string.gpu_throttling3));
            th3.setSummary(getString(R.string.gpu_throttling3_summary));
            th3.setUnit(getString(R.string.mhz));
            th3.setItems(freqs);
            th3.setProgress(value3);
            th3.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mGPUFreqTmu.setThrottling3(value, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            thrott.addItem(th3);
        }

        if(mGPUFreqTmu.hasThrottling4()) {
            SeekBarView th4 = new SeekBarView();
            th4.setTitle(getString(R.string.gpu_throttling4));
            th4.setSummary(getString(R.string.gpu_throttling4_summary));
            th4.setUnit(getString(R.string.mhz));
            th4.setItems(freqs);
            th4.setProgress(value4);
            th4.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mGPUFreqTmu.setThrottling4(value, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            thrott.addItem(th4);
        }

        if(mGPUFreqTmu.hasTripping()) {
            SeekBarView trip = new SeekBarView();
            trip.setTitle(getString(R.string.gpu_tripping));
            trip.setSummary(getString(R.string.gpu_tripping_summary));
            trip.setUnit(getString(R.string.mhz));
            trip.setItems(freqs);
            trip.setProgress(value5);
            trip.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mGPUFreqTmu.setTripping(value, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            thrott.addItem(trip);
        }

        if (thrott.size() > 0) {
            items.add(thrott);
        }
    }

    private void adrenoboostInit(List<RecyclerViewItem> items) {
        CardView abCard = new CardView(getActivity());
        abCard.setTitle(getString(R.string.gpu_adreno_boost_title));

        SelectView aB = new SelectView();
        aB.setTitle(getString(R.string.gpu_adreno_boost_title));
        aB.setSummary(getString(R.string.gpu_adreno_boost_summary));
        aB.setItems(Arrays.asList(getResources().getStringArray(R.array.gpu_adreno_boost)));
        aB.setItem(AdrenoBoost.getAdrenoBoost(getActivity()));
        aB.setOnItemSelected((selectView, position, item)
                -> AdrenoBoost.setAdrenoBoost(position, getActivity()));

        abCard.addItem(aB);

        if (abCard.size() > 0) {
            items.add(abCard);
        }
    }

    private void freqInit(List<RecyclerViewItem> items) {

        CardView driverCard = new CardView(getActivity());
        driverCard.setTitle(getString(R.string.gpu_driver_title));

        if (mGPUFreqExynos.hasDriverVersion()) {
            DescriptionView driver = new DescriptionView();
            driver.setTitle(getString(R.string.gpu_driver_version));
            driver.setSummary(mGPUFreqExynos.getDriverVersion());
            driverCard.addItem(driver);
        }

        DescriptionView lib = new DescriptionView();
        lib.setTitle(getString(R.string.gpu_lib_version));
        lib.setSummary(AppSettings.getString("gpu_lib_version", "", getActivity()));
        driverCard.addItem(lib);

        if (driverCard.size() > 0) {
            items.add(driverCard);
        }

        CardView freqCard = new CardView(getActivity());
        freqCard.setTitle(getString(R.string.frequencies));

        if (mGPUFreq.has2dCurFreq() && mGPUFreq.get2dAvailableFreqs() != null) {
            m2dCurFreq = new XYGraphView();
            m2dCurFreq.setTitle(getString(R.string.gpu_2d_freq));
            freqCard.addItem(m2dCurFreq);
        }

        if (mGPUFreqExynos.hasCurFreq() && mGPUFreqExynos.getAvailableFreqs() != null) {
            mCurFreq = new XYGraphView();
            mCurFreq.setTitle(getString(R.string.gpu_freq));
            freqCard.addItem(mCurFreq);
        }

        if (mGPUFreqExynos.hasUtilization()) {
            mUtilization = new XYGraphView();
            mUtilization.setTitle(getString(R.string.gpu_utilization));
            freqCard.addItem(mUtilization);
        }

        if (mGPUFreq.has2dMaxFreq() && mGPUFreq.get2dAvailableFreqs() != null) {
            SelectView max2dFreq = new SelectView();
            max2dFreq.setTitle(getString(R.string.gpu_2d_max_freq));
            max2dFreq.setSummary(getString(R.string.gpu_2d_max_freq_summary));
            max2dFreq.setItems(mGPUFreq.get2dAdjustedFreqs(getActivity()));
            max2dFreq.setItem((mGPUFreq.get2dMaxFreq() / 1000000) + getString(R.string.mhz));
            max2dFreq.setOnItemSelected((selectView, position, item)
                    -> mGPUFreq.set2dMaxFreq(mGPUFreq.get2dAvailableFreqs().get(position), getActivity()));

            freqCard.addItem(max2dFreq);
        }

        if (mGPUFreqExynos.hasMaxFreq() && mGPUFreqExynos.getAvailableFreqs() != null) {
            SelectView maxFreq = new SelectView();
            maxFreq.setTitle(getString(R.string.gpu_max_freq));
            maxFreq.setSummary(getString(R.string.gpu_max_freq_summary));
            maxFreq.setItems(mGPUFreqExynos.getAdjustedFreqs(getActivity()));
            maxFreq.setItem(mGPUFreqExynos.getMaxFreq() + getString(R.string.mhz));
            maxFreq.setOnItemSelected((selectView, position, item)
                    -> mGPUFreqExynos.setMaxFreq(Objects.requireNonNull(mGPUFreqExynos.getAvailableFreqs()).get(position), getActivity()));

            freqCard.addItem(maxFreq);
        }

        if (mGPUFreqExynos.hasMinFreq() && mGPUFreqExynos.getAvailableFreqs() != null) {
            SelectView minFreq = new SelectView();
            minFreq.setTitle(getString(R.string.gpu_min_freq));
            minFreq.setSummary(getString(R.string.gpu_min_freq_summary));
            minFreq.setItems(mGPUFreqExynos.getAdjustedFreqs(getActivity()));
            minFreq.setItem(mGPUFreqExynos.getMinFreq() + getString(R.string.mhz));
            minFreq.setOnItemSelected((selectView, position, item)
                    -> mGPUFreqExynos.setMinFreq(Objects.requireNonNull(mGPUFreqExynos.getAvailableFreqs()).get(position), getActivity()));

            freqCard.addItem(minFreq);
        }

        if (freqCard.size() > 0) {
            items.add(freqCard);
        }
    }

    private void powerPolicyInit (List<RecyclerViewItem> items){
        CardView powCard = new CardView(getActivity());
        powCard.setTitle(getString(R.string.gpu_power_policy_card));

        SelectView powPol = new SelectView();
        powPol.setTitle(getString(R.string.gpu_power_policy));
        powPol.setSummary(getString(R.string.gpu_power_policy_summary));
        powPol.setItems(mGPUFreqExynos.getPowerPolicies());
        powPol.setItem(mGPUFreqExynos.getPowerPolicy());
        powPol.setOnItemSelected((selectView, position, item)
                -> mGPUFreqExynos.setPowerPolicy(item, getActivity()));

        powCard.addItem(powPol);

        if (powCard.size() > 0) {
            items.add(powCard);
        }
    }

    private void governorInit(List<RecyclerViewItem> items) {
        CardView govCard = new CardView(getActivity());
        govCard.setTitle(getString(R.string.gpu_governor));

        if (mGPUFreq.has2dGovernor()) {
            SelectView governor2d = new SelectView();
            governor2d.setTitle(getString(R.string.gpu_2d_governor));
            governor2d.setSummary(getString(R.string.gpu_2d_governor_summary));
            governor2d.setItems(mGPUFreq.get2dAvailableGovernors());
            governor2d.setItem(mGPUFreq.get2dGovernor());
            governor2d.setOnItemSelected((selectView, position, item)
                    -> mGPUFreq.set2dGovernor(item, getActivity()));

            govCard.addItem(governor2d);
        }

        if (mGPUFreqExynos.hasGovernor()) {
            SelectView governor = new SelectView();
            governor.setTitle(getString(R.string.gpu_governor));
            governor.setSummary(getString(R.string.gpu_governor_summary));
            governor.setItems(mGPUFreqExynos.getAvailableGovernors());
            governor.setItem(mGPUFreqExynos.getGovernor());
            governor.setOnItemSelected((selectView, position, item)
                    -> mGPUFreqExynos.setGovernor(item, getActivity()));

            govCard.addItem(governor);

            TitleView tun = new TitleView();
            tun.setText(getString(R.string.gov_tunables));
            govCard.addItem(tun);

            if (mGPUFreqExynos.hasHighspeedClock()) {
                List<String> freqs = new ArrayList<>();
                List<Integer> list = mGPUFreqExynos.getAvailableFreqsSort();
                if(list != null) {
                    int value = 0;
                    for (int i = 0; i < list.size(); i++) {
                        freqs.add(String.valueOf(list.get(i)));
                        if (list.get(i) == mGPUFreqExynos.getHighspeedClock()) {
                            value = i;
                        }
                    }

                    SeekBarView seekbar = new SeekBarView();
                    seekbar.setTitle(getString(R.string.tun_highspeed_clock));
                    seekbar.setSummary(getString(R.string.tun_highspeed_clock_summary));
                    seekbar.setUnit(getString(R.string.mhz));
                    seekbar.setItems(freqs);
                    seekbar.setProgress(value);
                    seekbar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                        @Override
                        public void onStop(SeekBarView seekBarView, int position, String value) {
                            mGPUFreqExynos.setHighspeedClock(value, getActivity());
                        }

                        @Override
                        public void onMove(SeekBarView seekBarView, int position, String value) {
                        }
                    });

                    govCard.addItem(seekbar);
                }
            }

            if (mGPUFreqExynos.hasHighspeedLoad()) {

                SeekBarView seekbar = new SeekBarView();
                seekbar.setTitle(getString(R.string.tun_highspeed_load));
                seekbar.setSummary(getString(R.string.tun_highspeed_load_summary));
                seekbar.setUnit(getString(R.string.percent));
                seekbar.setMax(100);
                seekbar.setMin(1);
                seekbar.setProgress(mGPUFreqExynos.getHighspeedLoad() - 1);
                seekbar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        mGPUFreqExynos.setHighspeedLoad((position + 1), getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                govCard.addItem(seekbar);
            }

            if (mGPUFreqExynos.hasHighspeedDelay()) {

                SeekBarView seekbar = new SeekBarView();
                seekbar.setTitle(getString(R.string.tun_highspeed_delay));
                seekbar.setSummary(getString(R.string.tun_highspeed_delay_summary));
                seekbar.setUnit(getString(R.string.ms));
                seekbar.setMax(5);
                seekbar.setMin(0);
                seekbar.setProgress(mGPUFreqExynos.getHighspeedDelay());
                seekbar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        mGPUFreqExynos.setHighspeedDelay(position, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                govCard.addItem(seekbar);
            }

            if (mGPUFreq.hasTunables(governor.getValue())) {
                DescriptionView tunables = new DescriptionView();
                tunables.setTitle(getString(R.string.gpu_governor_tunables));
                tunables.setSummary(getString(R.string.governor_tunables_summary));
                tunables.setOnItemClickListener(( item) -> {
                        String gov = mGPUFreq.getGovernor();
                        setForegroundText(gov);
                        mGPUGovernorTunableFragment.setError(getString(R.string.tunables_error, governor));
                        mGPUGovernorTunableFragment.setPath(mGPUFreq.getTunables(mGPUFreq.getGovernor()),
                                ApplyOnBootFragment.GPU);
                        showForeground();
                });

                govCard.addItem(tunables);
            }
        }

        if (govCard.size() > 0) {
            items.add(govCard);
        }
    }

    private void voltageInit(List<RecyclerViewItem> items) {

        List<Integer> freqs = mGPUFreqExynos.getAvailableFreqs();
        List<String> voltages = mGPUFreqExynos.getVoltages();
        List<String> voltagesStock = mGPUFreqExynos.getStockVoltages();

        if (freqs != null && voltages != null && voltagesStock != null && freqs.size() == voltages.size()) {

            CardView voltCard = new CardView(getActivity());
            voltCard.setTitle(getString(R.string.gpu_voltage));

            List<String> progress = new ArrayList<>();
            for (float i = mVoltMinValue; i < (mVoltMaxValue + mVoltStep); i += mVoltStep) {
                String global = String.valueOf(i / mGPUFreqExynos.getVoltageOffset());
                progress.add(global);
            }

            seekbarProfInit(mSeekbarProf, freqs, voltages, voltagesStock, progress);

            voltCard.addItem(mSeekbarProf);

            boolean enableGlobal = AppSettings.getBoolean("gpu_global_volts", true, getActivity());

            SwitchView voltControl = new SwitchView();
            voltControl.setTitle(getString(R.string.cpu_manual_volt));
            voltControl.setSummaryOn(getString(R.string.cpu_manual_volt_summaryOn));
            voltControl.setSummaryOff(getString(R.string.cpu_manual_volt_summaryOff));
            voltControl.setChecked(enableGlobal);
            voltControl.addOnSwitchListener((switchView, isChecked) -> {
                    if (isChecked) {
                        AppSettings.saveBoolean("gpu_global_volts", true, getActivity());
                        AppSettings.saveBoolean("gpu_individual_volts", false, getActivity());
                        reload();
                    } else {
                        AppSettings.saveBoolean("gpu_global_volts", false, getActivity());
                        AppSettings.saveBoolean("gpu_individual_volts", true, getActivity());
                        AppSettings.saveInt("gpu_seekbarPref_value", mDefZeroPosition, getActivity());
                        reload();
                    }
            });

            voltCard.addItem(voltControl);

            if (voltCard.size() > 0) {
                items.add(voltCard);
            }


            TitleView tunables = new TitleView();
            tunables.setText(getString(R.string.gpu_volts));
            items.add(tunables);

            for (int i = 0; i < freqs.size(); i++) {
                SeekBarView seekbar = new SeekBarView();
                seekbarInit(seekbar, freqs.get(i), voltages.get(i), voltagesStock.get(i));
                mVoltages.add(seekbar);
            }
            items.addAll(mVoltages);
        }
    }

    private void seekbarProfInit(SeekBarView seekbar, final List<Integer> freqs, final List<String> voltages,
                                 final List<String> voltagesStock, List<String> progress) {

        boolean enableSeekbar = AppSettings.getBoolean("gpu_global_volts", true, getActivity());
        int global = AppSettings.getInt("gpu_seekbarPref_value", mDefZeroPosition, getActivity());

        int value = 0;
        for (int i = 0; i < progress.size(); i++) {
            if (i == global){
                value = i;
                break;
            }
        }

        seekbar.setTitle(getString(R.string.cpu_volt_profile));
        seekbar.setSummary(getString(R.string.cpu_volt_profile_summary));
        seekbar.setUnit(getString(R.string.mv));
        seekbar.setItems(progress);
        seekbar.setProgress(value);
        seekbar.setEnabled(enableSeekbar);
        if(!enableSeekbar) seekbar.setAlpha(0.4f);
        else seekbar.setAlpha(1f);
        seekbar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                for (int i = 0; i < voltages.size(); i++) {
                    String volt = String.valueOf(Utils.strToFloat(voltagesStock.get(i)) + Utils.strToFloat(value));
                    int freq = freqs.get(i);
                    mGPUFreqExynos.setVoltage(freq, volt, getActivity());
                    AppSettings.saveInt("gpu_seekbarPref_value", position, getActivity());
                }
                getHandler().postDelayed(() -> reload(), 200);
            }
            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });
    }

    private void seekbarInit(SeekBarView seekbar, final Integer freq, String voltage,
            String voltageStock) {

        int mOffset = mGPUFreqExynos.getVoltageOffset();
        float mMin = (Utils.strToFloat(voltageStock) - (- mVoltMinValue / 1000)) * mOffset;
        float mMax = ((Utils.strToFloat(voltageStock) + (mVoltMaxValue / 1000)) * mOffset) + mVoltStep;

        List<String> progress = new ArrayList<>();
        for(float i = mMin ; i < mMax; i += mVoltStep){
            String string = String.valueOf(i / mOffset);
            progress.add(string);
        }

        int value = 0;
        for (int i = 0; i < progress.size(); i++) {
            if (Objects.equals(progress.get(i), voltage)){
                value = i;
                break;
            }
        }

        boolean enableSeekbar = AppSettings.getBoolean("gpu_individual_volts", false, getActivity());

        seekbar.setTitle(freq + " " + getString(R.string.mhz));
        seekbar.setSummary(getString(R.string.def) + ": " + voltageStock + " " + getString(R.string.mv));
        seekbar.setUnit(getString(R.string.mv));
        seekbar.setItems(progress);
        seekbar.setProgress(value);
        seekbar.setEnabled(enableSeekbar);
        if(!enableSeekbar) seekbar.setAlpha(0.4f);
        else seekbar.setAlpha(1f);
        seekbar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {

            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mGPUFreqExynos.setVoltage(freq, value, getActivity());
            getHandler().postDelayed(() -> reload(), 200);
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });
    }

    private void simpleGpuInit(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> simpleGpu = new ArrayList<>();
        TitleView title = new TitleView();
        title.setText(getString(R.string.simple_gpu_algorithm));

        if (SimpleGPU.hasSimpleGpuEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.simple_gpu_algorithm));
            enable.setSummary(getString(R.string.simple_gpu_algorithm_summary));
            enable.setChecked(SimpleGPU.isSimpleGpuEnabled());
            enable.addOnSwitchListener((switchView, isChecked)
                    -> SimpleGPU.enableSimpleGpu(isChecked, getActivity()));

            simpleGpu.add(enable);
        }

        if (SimpleGPU.hasSimpleGpuLaziness()) {
            SeekBarView laziness = new SeekBarView();
            laziness.setTitle(getString(R.string.laziness));
            laziness.setSummary(getString(R.string.laziness_summary));
            laziness.setMax(10);
            laziness.setProgress(SimpleGPU.getSimpleGpuLaziness());
            laziness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    SimpleGPU.setSimpleGpuLaziness(position, getActivity());
                }
            });

            simpleGpu.add(laziness);
        }

        if (SimpleGPU.hasSimpleGpuRampThreshold()) {
            SeekBarView rampThreshold = new SeekBarView();
            rampThreshold.setTitle(getString(R.string.ramp_thresold));
            rampThreshold.setSummary(getString(R.string.ramp_thresold_summary));
            rampThreshold.setMax(10);
            rampThreshold.setProgress(SimpleGPU.getSimpleGpuRampThreshold());
            rampThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    SimpleGPU.setSimpleGpuRampThreshold(position, getActivity());
                }
            });

            simpleGpu.add(rampThreshold);
        }

        if (simpleGpu.size() > 0) {
            items.add(title);
            items.addAll(simpleGpu);
        }
    }

    private void adrenoIdlerInit(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> adrenoIdler = new ArrayList<>();
        TitleView title = new TitleView();
        title.setText(getString(R.string.adreno_idler));

        if (AdrenoIdler.hasAdrenoIdlerEnable()) {
            SwitchView enable = new SwitchView();
            enable.setTitle(getString(R.string.adreno_idler));
            enable.setSummary(getString(R.string.adreno_idler_summary));
            enable.setChecked(AdrenoIdler.isAdrenoIdlerEnabled());
            enable.addOnSwitchListener((switchView, isChecked)
                    -> AdrenoIdler.enableAdrenoIdler(isChecked, getActivity()));

            adrenoIdler.add(enable);
        }

        if (AdrenoIdler.hasAdrenoIdlerDownDiff()) {
            SeekBarView downDiff = new SeekBarView();
            downDiff.setTitle(getString(R.string.down_differential));
            downDiff.setSummary(getString(R.string.down_differential_summary));
            downDiff.setMax(99);
            downDiff.setProgress(AdrenoIdler.getAdrenoIdlerDownDiff());
            downDiff.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AdrenoIdler.setAdrenoIdlerDownDiff(position, getActivity());
                }
            });

            adrenoIdler.add(downDiff);
        }

        if (AdrenoIdler.hasAdrenoIdlerIdleWait()) {
            SeekBarView idleWait = new SeekBarView();
            idleWait.setTitle(getString(R.string.idle_wait));
            idleWait.setSummary(getString(R.string.idle_wait_summary));
            idleWait.setMax(99);
            idleWait.setProgress(AdrenoIdler.getAdrenoIdlerIdleWait());
            idleWait.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AdrenoIdler.setAdrenoIdlerIdleWait(position, getActivity());
                }
            });

            adrenoIdler.add(idleWait);
        }

        if (AdrenoIdler.hasAdrenoIdlerIdleWorkload()) {
            SeekBarView idleWorkload = new SeekBarView();
            idleWorkload.setTitle(getString(R.string.workload));
            idleWorkload.setSummary(getString(R.string.workload_summary));
            idleWorkload.setMax(10);
            idleWorkload.setMin(1);
            idleWorkload.setProgress(AdrenoIdler.getAdrenoIdlerIdleWorkload() - 1);
            idleWorkload.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }

                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    AdrenoIdler.setAdrenoIdlerIdleWorkload(position + 1, getActivity());
                }
            });

            adrenoIdler.add(idleWorkload);
        }

        if (adrenoIdler.size() > 0) {
            items.add(title);
            items.addAll(adrenoIdler);
        }
    }

    private void reload() {
        List<Integer> freqs = mGPUFreqExynos.getAvailableFreqs();
        List<String> voltages = mGPUFreqExynos.getVoltages();
        List<String> voltagesStock = mGPUFreqExynos.getStockVoltages();

        if (freqs != null && voltages != null && voltagesStock != null) {
            for (int i = 0; i < mVoltages.size(); i++) {
                seekbarInit(mVoltages.get(i), freqs.get(i), voltages.get(i), voltagesStock.get(i));
            }

            List<String> progress = new ArrayList<>();
            for (float i = mVoltMinValue; i < (mVoltMaxValue + mVoltStep); i += mVoltStep) {
                String global = String.valueOf(i / mGPUFreqExynos.getVoltageOffset());
                progress.add(global);
            }
            seekbarProfInit(mSeekbarProf, freqs, voltages, voltagesStock, progress);
        }
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (m2dCurFreq != null) {
            int freq = mGPUFreq.get2dCurFreq();
            float maxFreq = mGPUFreq.get2dAvailableFreqs().get(mGPUFreq.get2dAvailableFreqs().size() - 1);
            m2dCurFreq.setText((freq / 1000000) + getString(R.string.mhz));
            float per = (float) freq / maxFreq * 100f;
            m2dCurFreq.addPercentage(Math.round(per > 100 ? 100 : per < 0 ? 0 : per));
        }

        if (mCurFreq != null) {
            int load = -1;
            String text = "";
            if (mGPUFreq.hasBusy()) {
                load = mGPUFreq.getBusy();
                load = load > 100 ? 100 : load < 0 ? 0 : load;
                text += load + "% - ";
            }

            int freq = mGPUFreqExynos.getCurFreq();
            float maxFreq = mGPUFreqExynos.getAvailableFreqsSort()
                    .get(mGPUFreqExynos.getAvailableFreqsSort().size() - 1);
            mCurFreq.setText(freq / mGPUFreqExynos.getCurFreqOffset() + getString(R.string.mhz));
            float per = (float) freq / maxFreq * 100f;
            mCurFreq.addPercentage(load >= 0 ? load : Math.round(per > 100 ? 100 : per < 0 ? 0 : per));
        }

        if (mCurFreq != null) {
            int val = mGPUFreqExynos.getUtilization();
            float maxVal = 100;
            mUtilization.setText(val + getString(R.string.percent));
            float per = (float) val / maxVal * 100f;
            mUtilization.addPercentage(Math.round(per > 100 ? 100 : per < 0 ? 0 : per));
        }
    }
}
