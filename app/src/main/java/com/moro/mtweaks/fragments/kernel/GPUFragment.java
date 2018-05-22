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
import com.moro.mtweaks.utils.kernel.gpu.GPUFreq;
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
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 12.05.16.
 */
public class GPUFragment extends RecyclerViewFragment {

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

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        freqInit(items);
        if (GPUFreq.hasPowerPolicy()){
            powerPolicyInit(items);
        }
        if (GPUFreq.hasGovernor()){
            governorInit(items);
        }
        if (GPUFreq.hasBackup()){
            voltageInit(items);
        }
        if (SimpleGPU.supported()) {
            simpleGpuInit(items);
        }
        if (AdrenoIdler.supported()) {
            adrenoIdlerInit(items);
        }
    }

    private void freqInit(List<RecyclerViewItem> items) {
        CardView freqCard = new CardView(getActivity());
        freqCard.setTitle(getString(R.string.frequencies));

        if (GPUFreq.has2dCurFreq() && GPUFreq.get2dAvailableFreqs() != null) {
            m2dCurFreq = new XYGraphView();
            m2dCurFreq.setTitle(getString(R.string.gpu_2d_freq));
            freqCard.addItem(m2dCurFreq);
        }

        if (GPUFreq.hasCurFreq() && GPUFreq.getAvailableS7Freqs() != null) {
            mCurFreq = new XYGraphView();
            mCurFreq.setTitle(getString(R.string.gpu_freq));
            freqCard.addItem(mCurFreq);
        }

        if (GPUFreq.has2dMaxFreq() && GPUFreq.get2dAvailableFreqs() != null) {
            SelectView max2dFreq = new SelectView();
            max2dFreq.setTitle(getString(R.string.gpu_2d_max_freq));
            max2dFreq.setSummary(getString(R.string.gpu_2d_max_freq_summary));
            max2dFreq.setItems(GPUFreq.get2dAdjustedFreqs(getActivity()));
            max2dFreq.setItem((GPUFreq.get2dMaxFreq() / 1000000) + getString(R.string.mhz));
            max2dFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    GPUFreq.set2dMaxFreq(GPUFreq.get2dAvailableFreqs().get(position), getActivity());
                }
            });

            freqCard.addItem(max2dFreq);
        }

        if (GPUFreq.hasMaxFreq() && GPUFreq.getAvailableS7Freqs() != null) {
            SelectView maxFreq = new SelectView();
            maxFreq.setTitle(getString(R.string.gpu_max_freq));
            maxFreq.setSummary(getString(R.string.gpu_max_freq_summary));
            maxFreq.setItems(GPUFreq.getAdjustedFreqs(getActivity()));
            maxFreq.setItem((GPUFreq.getMaxFreq() / GPUFreq.getMaxFreqOffset()) + getString(R.string.mhz));
            maxFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    GPUFreq.setMaxFreq(GPUFreq.getAvailableS7Freqs().get(position), getActivity());
                }
            });

            freqCard.addItem(maxFreq);
        }

        if (GPUFreq.hasMinFreq() && GPUFreq.getAvailableS7Freqs() != null) {
            SelectView minFreq = new SelectView();
            minFreq.setTitle(getString(R.string.gpu_min_freq));
            minFreq.setSummary(getString(R.string.gpu_min_freq_summary));
            minFreq.setItems(GPUFreq.getAdjustedFreqs(getActivity()));
            minFreq.setItem((GPUFreq.getMinFreq() / GPUFreq.getMinFreqOffset()) + getString(R.string.mhz));
            minFreq.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    GPUFreq.setMinFreq(GPUFreq.getAvailableS7Freqs().get(position), getActivity());
                }
            });

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
        powPol.setItems(GPUFreq.getPowerPolicies());
        powPol.setItem(GPUFreq.getPowerPolicy());
        powPol.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                GPUFreq.setPowerPolicy(item, getActivity());
            }
        });

        powCard.addItem(powPol);

        if (powCard.size() > 0) {
            items.add(powCard);
        }
    }

    private void governorInit(List<RecyclerViewItem> items) {
        CardView govCard = new CardView(getActivity());
        govCard.setTitle(getString(R.string.gpu_governor));

        if (GPUFreq.has2dGovernor()) {
            SelectView governor2d = new SelectView();
            governor2d.setTitle(getString(R.string.gpu_2d_governor));
            governor2d.setSummary(getString(R.string.gpu_2d_governor_summary));
            governor2d.setItems(GPUFreq.get2dAvailableGovernors());
            governor2d.setItem(GPUFreq.get2dGovernor());
            governor2d.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    GPUFreq.set2dGovernor(item, getActivity());
                }
            });

            govCard.addItem(governor2d);
        }

        if (GPUFreq.hasGovernor()) {
            SelectView governor = new SelectView();
            governor.setTitle(getString(R.string.gpu_governor));
            governor.setSummary(getString(R.string.gpu_governor_summary));
            governor.setItems(GPUFreq.getAvailableS7Governors());
            governor.setItem(GPUFreq.getS7Governor());
            governor.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    GPUFreq.setS7Governor(item, getActivity());
                }
            });

            govCard.addItem(governor);

            TitleView tun = new TitleView();
            tun.setText(getString(R.string.gov_tunables));
            govCard.addItem(tun);

            if (GPUFreq.hasHighspeedClock()) {
                List<String> freqs = new ArrayList<>();
                List<Integer> list = GPUFreq.getAvailableS7FreqsSort();
                int value = 0;
                for (int i = 0; i < list.size(); i++) {
                    freqs.add(String.valueOf(list.get(i)));
                    if (list.get(i) == GPUFreq.getHighspeedClock()) {
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
                        GPUFreq.setHighspeedClock(value, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                govCard.addItem(seekbar);
            }

            if (GPUFreq.hasHighspeedLoad()) {

                SeekBarView seekbar = new SeekBarView();
                seekbar.setTitle(getString(R.string.tun_highspeed_load));
                seekbar.setSummary(getString(R.string.tun_highspeed_load_summary));
                seekbar.setUnit(getString(R.string.percent));
                seekbar.setMax(100);
                seekbar.setMin(1);
                seekbar.setProgress(GPUFreq.getHighspeedLoad() - 1);
                seekbar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        GPUFreq.setHighspeedLoad((position + 1), getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                govCard.addItem(seekbar);
            }

            if (GPUFreq.hasHighspeedDelay()) {

                SeekBarView seekbar = new SeekBarView();
                seekbar.setTitle(getString(R.string.tun_highspeed_delay));
                seekbar.setSummary(getString(R.string.tun_highspeed_delay_summary));
                seekbar.setUnit(getString(R.string.ms));
                seekbar.setMax(5);
                seekbar.setMin(0);
                seekbar.setProgress(GPUFreq.getHighspeedDelay());
                seekbar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        GPUFreq.setHighspeedDelay(position, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });

                govCard.addItem(seekbar);
            }

            if (GPUFreq.hasTunables(governor.getValue())) {
                DescriptionView tunables = new DescriptionView();
                tunables.setTitle(getString(R.string.gpu_governor_tunables));
                tunables.setSummary(getString(R.string.governor_tunables_summary));
                tunables.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                    @Override
                    public void onClick(RecyclerViewItem item) {
                        String governor = GPUFreq.getGovernor();
                        setForegroundText(governor);
                        mGPUGovernorTunableFragment.setError(getString(R.string.tunables_error, governor));
                        mGPUGovernorTunableFragment.setPath(GPUFreq.getTunables(GPUFreq.getGovernor()),
                                ApplyOnBootFragment.GPU);
                        showForeground();
                    }
                });

                govCard.addItem(tunables);
            }
        }

        if (govCard.size() > 0) {
            items.add(govCard);
        }
    }

    private void voltageInit(List<RecyclerViewItem> items) {

        List<Integer> freqs = GPUFreq.getAvailableS7Freqs();
        List<String> voltages = GPUFreq.getVoltages();
        List<String> voltagesStock = GPUFreq.getStockVoltages();

        if (freqs != null && voltages != null && voltagesStock != null && freqs.size() == voltages.size()) {

            CardView voltCard = new CardView(getActivity());
            voltCard.setTitle(getString(R.string.gpu_voltage));

            List<String> progress = new ArrayList<>();
            for (float i = -100000f; i < 31250f; i += 6250) {
                String global = String.valueOf(i / GPUFreq.getOffset());
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
            voltControl.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
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
                }
            });

            voltCard.addItem(voltControl);

            if (voltCard.size() > 0) {
                items.add(voltCard);
            }


            TitleView tunables = new TitleView();
            tunables.setText(getString(R.string.cpuCl1_volt));
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
                    GPUFreq.setVoltage(freq, volt, getActivity());
                    AppSettings.saveInt("gpu_seekbarPref_value", position, getActivity());
                }
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reload();
                    }
                }, 200);
            }
            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });
    }

    private void seekbarInit(SeekBarView seekbar, final Integer freq, String voltage,
            String voltageStock) {

        int mStep = 6250;
        int mOffset = GPUFreq.getOffset();
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
                GPUFreq.setVoltage(freq, value, getActivity());
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    reload();
                }
            }, 200);
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
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    SimpleGPU.enableSimpleGpu(isChecked, getActivity());
                }
            });

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
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    AdrenoIdler.enableAdrenoIdler(isChecked, getActivity());
                }
            });

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
        List<Integer> freqs = GPUFreq.getAvailableS7Freqs();
        List<String> voltages = GPUFreq.getVoltages();
        List<String> voltagesStock = GPUFreq.getStockVoltages();

        if (freqs != null && voltages != null && voltagesStock != null) {
            for (int i = 0; i < mVoltages.size(); i++) {
                seekbarInit(mVoltages.get(i), freqs.get(i), voltages.get(i), voltagesStock.get(i));
            }

            List<String> progress = new ArrayList<>();
            for (float i = -100000f; i < 31250f; i += 6250) {
                String global = String.valueOf(i / GPUFreq.getOffset());
                progress.add(global);
            }
            seekbarProfInit(mSeekbarProf, freqs, voltages, voltagesStock, progress);
        }
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (m2dCurFreq != null) {
            int freq = GPUFreq.get2dCurFreq();
            float maxFreq = GPUFreq.get2dAvailableFreqs().get(GPUFreq.get2dAvailableFreqs().size() - 1);
            m2dCurFreq.setText((freq / 1000000) + getString(R.string.mhz));
            float per = (float) freq / maxFreq * 100f;
            m2dCurFreq.addPercentage(Math.round(per > 100 ? 100 : per < 0 ? 0 : per));
        }

        if (mCurFreq != null) {
            int load = -1;
            String text = "";
            if (GPUFreq.hasBusy()) {
                load = GPUFreq.getBusy();
                load = load > 100 ? 100 : load < 0 ? 0 : load;
                text += load + "% - ";
            }

            int freq = GPUFreq.getCurFreq();
            float maxFreq = GPUFreq.getAvailableS7FreqsSort().get(GPUFreq.getAvailableS7FreqsSort().size() - 1);
            text += freq / GPUFreq.getCurFreqOffset() + getString(R.string.mhz);
            mCurFreq.setText(text);
            float per = (float) freq / maxFreq * 100f;
            mCurFreq.addPercentage(load >= 0 ? load : Math.round(per > 100 ? 100 : per < 0 ? 0 : per));
        }
    }
}
