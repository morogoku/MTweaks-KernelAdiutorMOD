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

    private XYGraphView m2dCurFreq;
    private XYGraphView mCurFreq;
    private List<SeekBarView> mVoltages = new ArrayList<>();
    private SeekBarView mSeekbarProf = new SeekBarView();

    private PathReaderFragment mGPUGovernorTunableFragment;

    @Override
    protected BaseFragment getForegroundFragment() {
        return mGPUGovernorTunableFragment = new PathReaderFragment();
    }

    @Override
    protected void init() {
        super.init();

        mGPUFreq = GPUFreq.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mVoltages.clear();

        freqInit(items);
        if (GPUFreqExynos.hasPowerPolicy()){
            powerPolicyInit(items);
        }
        if (GPUFreqExynos.hasGovernor()){
            governorInit(items);
        }
        if (GPUFreqTmu.supported()){
            throttlingInit(items);
        }
        if (GPUFreqExynos.hasVoltage()){
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

        if(GPUFreqTmu.hasTmu()) {
            SwitchView tmu = new SwitchView();
            tmu.setTitle(getString(R.string.gpu_tmu));
            tmu.setSummary(getString(R.string.gpu_tmu_summary));
            tmu.setChecked(GPUFreqTmu.isTmuEnabled());
            tmu.addOnSwitchListener((switchView, isChecked)
                    -> GPUFreqTmu.enableTmu(isChecked, getActivity()));

            thrott.addItem(tmu);
        }


        int value = 0;
        List<String> freqs = GPUFreqExynos.getFreqs();
        List<Integer> list = GPUFreqExynos.getAvailableFreqsSort();
        if(list != null) {

            if(GPUFreqTmu.hasThrottling1()) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) == GPUFreqTmu.getThrottling1()) {
                        value = i;
                    }
                }

                SeekBarView th1 = new SeekBarView();
                th1.setTitle(getString(R.string.gpu_throttling1));
                th1.setSummary(getString(R.string.gpu_throttling1_summary));
                th1.setUnit(getString(R.string.mhz));
                th1.setItems(freqs);
                th1.setProgress(value);
                th1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        GPUFreqTmu.setThrottling1(value, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                thrott.addItem(th1);
            }

            if(GPUFreqTmu.hasThrottling2()) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) == GPUFreqTmu.getThrottling2()) {
                        value = i;
                    }
                }

                SeekBarView th2 = new SeekBarView();
                th2.setTitle(getString(R.string.gpu_throttling2));
                th2.setSummary(getString(R.string.gpu_throttling2_summary));
                th2.setUnit(getString(R.string.mhz));
                th2.setItems(freqs);
                th2.setProgress(value);
                th2.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        GPUFreqTmu.setThrottling2(value, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                thrott.addItem(th2);
            }

            if(GPUFreqTmu.hasThrottling3()) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) == GPUFreqTmu.getThrottling3()) {
                        value = i;
                    }
                }

                SeekBarView th3 = new SeekBarView();
                th3.setTitle(getString(R.string.gpu_throttling3));
                th3.setSummary(getString(R.string.gpu_throttling3_summary));
                th3.setUnit(getString(R.string.mhz));
                th3.setItems(freqs);
                th3.setProgress(value);
                th3.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        GPUFreqTmu.setThrottling3(value, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                thrott.addItem(th3);
            }

            if(GPUFreqTmu.hasThrottling4()) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) == GPUFreqTmu.getThrottling4()) {
                        value = i;
                    }
                }

                SeekBarView th4 = new SeekBarView();
                th4.setTitle(getString(R.string.gpu_throttling4));
                th4.setSummary(getString(R.string.gpu_throttling4_summary));
                th4.setUnit(getString(R.string.mhz));
                th4.setItems(freqs);
                th4.setProgress(value);
                th4.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        GPUFreqTmu.setThrottling4(value, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                thrott.addItem(th4);
            }

            if(GPUFreqTmu.hasTripping()) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) == GPUFreqTmu.getTripping()) {
                        value = i;
                    }
                }

                SeekBarView trip = new SeekBarView();
                trip.setTitle(getString(R.string.gpu_tripping));
                trip.setSummary(getString(R.string.gpu_tripping_summary));
                trip.setUnit(getString(R.string.mhz));
                trip.setItems(freqs);
                trip.setProgress(value);
                trip.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        GPUFreqTmu.setTripping(value, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                thrott.addItem(trip);
            }
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
        CardView freqCard = new CardView(getActivity());
        freqCard.setTitle(getString(R.string.frequencies));

        if (mGPUFreq.has2dCurFreq() && mGPUFreq.get2dAvailableFreqs() != null) {
            m2dCurFreq = new XYGraphView();
            m2dCurFreq.setTitle(getString(R.string.gpu_2d_freq));
            freqCard.addItem(m2dCurFreq);
        }

        if (GPUFreqExynos.hasCurFreq() && GPUFreqExynos.getAvailableFreqs() != null) {
            mCurFreq = new XYGraphView();
            mCurFreq.setTitle(getString(R.string.gpu_freq));
            freqCard.addItem(mCurFreq);
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

        if (GPUFreqExynos.hasMaxFreq() && GPUFreqExynos.getAvailableFreqs() != null) {
            SelectView maxFreq = new SelectView();
            maxFreq.setTitle(getString(R.string.gpu_max_freq));
            maxFreq.setSummary(getString(R.string.gpu_max_freq_summary));
            maxFreq.setItems(GPUFreqExynos.getAdjustedFreqs(getActivity()));
            maxFreq.setItem((GPUFreqExynos.getMaxFreq() / GPUFreqExynos.getMaxFreqOffset()) + getString(R.string.mhz));
            maxFreq.setOnItemSelected((selectView, position, item)
                    -> GPUFreqExynos.setMaxFreq(Objects.requireNonNull(GPUFreqExynos.getAvailableFreqs()).get(position), getActivity()));

            freqCard.addItem(maxFreq);
        }

        if (GPUFreqExynos.hasMinFreq() && GPUFreqExynos.getAvailableFreqs() != null) {
            SelectView minFreq = new SelectView();
            minFreq.setTitle(getString(R.string.gpu_min_freq));
            minFreq.setSummary(getString(R.string.gpu_min_freq_summary));
            minFreq.setItems(GPUFreqExynos.getAdjustedFreqs(getActivity()));
            minFreq.setItem((GPUFreqExynos.getMinFreq() / GPUFreqExynos.getMinFreqOffset()) + getString(R.string.mhz));
            minFreq.setOnItemSelected((selectView, position, item)
                    -> GPUFreqExynos.setMinFreq(Objects.requireNonNull(GPUFreqExynos.getAvailableFreqs()).get(position), getActivity()));

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
        powPol.setItems(GPUFreqExynos.getPowerPolicies());
        powPol.setItem(GPUFreqExynos.getPowerPolicy());
        powPol.setOnItemSelected((selectView, position, item)
                -> GPUFreqExynos.setPowerPolicy(item, getActivity()));

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

        if (GPUFreqExynos.hasGovernor()) {
            SelectView governor = new SelectView();
            governor.setTitle(getString(R.string.gpu_governor));
            governor.setSummary(getString(R.string.gpu_governor_summary));
            governor.setItems(GPUFreqExynos.getAvailableGovernors());
            governor.setItem(GPUFreqExynos.getGovernor());
            governor.setOnItemSelected((selectView, position, item)
                    -> GPUFreqExynos.setGovernor(item, getActivity()));

            govCard.addItem(governor);

            TitleView tun = new TitleView();
            tun.setText(getString(R.string.gov_tunables));
            govCard.addItem(tun);

            if (GPUFreqExynos.hasHighspeedClock()) {
                List<String> freqs = new ArrayList<>();
                List<Integer> list = GPUFreqExynos.getAvailableFreqsSort();
                if(list != null) {
                    int value = 0;
                    for (int i = 0; i < list.size(); i++) {
                        freqs.add(String.valueOf(list.get(i)));
                        if (list.get(i) == GPUFreqExynos.getHighspeedClock()) {
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
                            GPUFreqExynos.setHighspeedClock(value, getActivity());
                        }

                        @Override
                        public void onMove(SeekBarView seekBarView, int position, String value) {
                        }
                    });

                    govCard.addItem(seekbar);
                }
            }

            if (GPUFreqExynos.hasHighspeedLoad()) {

                SeekBarView seekbar = new SeekBarView();
                seekbar.setTitle(getString(R.string.tun_highspeed_load));
                seekbar.setSummary(getString(R.string.tun_highspeed_load_summary));
                seekbar.setUnit(getString(R.string.percent));
                seekbar.setMax(100);
                seekbar.setMin(1);
                seekbar.setProgress(GPUFreqExynos.getHighspeedLoad() - 1);
                seekbar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        GPUFreqExynos.setHighspeedLoad((position + 1), getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                govCard.addItem(seekbar);
            }

            if (GPUFreqExynos.hasHighspeedDelay()) {

                SeekBarView seekbar = new SeekBarView();
                seekbar.setTitle(getString(R.string.tun_highspeed_delay));
                seekbar.setSummary(getString(R.string.tun_highspeed_delay_summary));
                seekbar.setUnit(getString(R.string.ms));
                seekbar.setMax(5);
                seekbar.setMin(0);
                seekbar.setProgress(GPUFreqExynos.getHighspeedDelay());
                seekbar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        GPUFreqExynos.setHighspeedDelay(position, getActivity());
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

        List<Integer> freqs = GPUFreqExynos.getAvailableFreqs();
        List<String> voltages = GPUFreqExynos.getVoltages();
        List<String> voltagesStock = GPUFreqExynos.getStockVoltages();

        if (freqs != null && voltages != null && voltagesStock != null && freqs.size() == voltages.size()) {

            CardView voltCard = new CardView(getActivity());
            voltCard.setTitle(getString(R.string.gpu_voltage));

            List<String> progress = new ArrayList<>();
            for (float i = -100000f; i < 31250f; i += 6250) {
                String global = String.valueOf(i / GPUFreqExynos.getVoltageOffset());
                progress.add(global);
            }

            seekbarProfInit(mSeekbarProf, freqs, voltages, voltagesStock, progress);

            voltCard.addItem(mSeekbarProf);

            Boolean enableGlobal = AppSettings.getBoolean("gpu_global_volts", true, getActivity());

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
                        AppSettings.saveInt("gpu_seekbarPref_value", 16, getActivity());
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

        Boolean enableSeekbar = AppSettings.getBoolean("gpu_global_volts", true, getActivity());
        int global = AppSettings.getInt("gpu_seekbarPref_value", 16, getActivity());

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
                    GPUFreqExynos.setVoltage(freq, volt, getActivity());
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

        int mStep = 6250;
        int mOffset = GPUFreqExynos.getVoltageOffset();
        float mMin = (Utils.strToFloat(voltageStock) - 100) * mOffset;
        float mMax = ((Utils.strToFloat(voltageStock) + 25) * mOffset) + mStep;

        List<String> progress = new ArrayList<>();
        for(float i = mMin ; i < mMax; i += mStep){
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

        Boolean enableSeekbar = AppSettings.getBoolean("gpu_individual_volts", false, getActivity());

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
                GPUFreqExynos.setVoltage(freq, value, getActivity());
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
        List<Integer> freqs = GPUFreqExynos.getAvailableFreqs();
        List<String> voltages = GPUFreqExynos.getVoltages();
        List<String> voltagesStock = GPUFreqExynos.getStockVoltages();

        if (freqs != null && voltages != null && voltagesStock != null) {
            for (int i = 0; i < mVoltages.size(); i++) {
                seekbarInit(mVoltages.get(i), freqs.get(i), voltages.get(i), voltagesStock.get(i));
            }

            List<String> progress = new ArrayList<>();
            for (float i = -100000f; i < 31250f; i += 6250) {
                String global = String.valueOf(i / GPUFreqExynos.getVoltageOffset());
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

            int freq = GPUFreqExynos.getCurFreq();
            float maxFreq = Objects
                    .requireNonNull(GPUFreqExynos.getAvailableFreqsSort())
                    .get(Objects.requireNonNull(GPUFreqExynos.getAvailableFreqsSort()).size() - 1);
            text += freq / GPUFreqExynos.getCurFreqOffset() + getString(R.string.mhz);
            mCurFreq.setText(text);
            float per = (float) freq / maxFreq * 100f;
            mCurFreq.addPercentage(load >= 0 ? load : Math.round(per > 100 ? 100 : per < 0 ? 0 : per));
        }
    }
}
