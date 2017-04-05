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
package com.moro.kerneladiutor.fragments.kernel;

import com.moro.kerneladiutor.R;
import com.moro.kerneladiutor.fragments.ApplyOnBootFragment;
import com.moro.kerneladiutor.fragments.RecyclerViewFragment;
import com.moro.kerneladiutor.utils.Utils;
import com.moro.kerneladiutor.utils.kernel.gpu.GPUFreq;
import com.moro.kerneladiutor.views.recyclerview.CardView;
import com.moro.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.moro.kerneladiutor.views.recyclerview.SeekBarView;
import com.moro.kerneladiutor.views.recyclerview.SelectView;
import com.moro.kerneladiutor.views.recyclerview.TitleView;
import com.moro.kerneladiutor.views.recyclerview.XYGraphView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 12.05.16.
 */
public class GPUFragment extends RecyclerViewFragment {

    private XYGraphView mCurFreq;

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        freqInit(items);
        if (GPUFreq.hasGovernor()){
            governorInit(items);
        }
        if (GPUFreq.hasBackup()){
            voltageInit(items);
        }
    }

    private void freqInit(List<RecyclerViewItem> items) {
        CardView freqCard = new CardView(getActivity());
        freqCard.setTitle(getString(R.string.frequencies));

        if (GPUFreq.hasCurFreq() && GPUFreq.getAvailableS7Freqs() != null) {
            mCurFreq = new XYGraphView();
            mCurFreq.setTitle(getString(R.string.gpu_freq));
            freqCard.addItem(mCurFreq);
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

    private void governorInit(List<RecyclerViewItem> items) {
        CardView govCard = new CardView(getActivity());
        govCard.setTitle(getString(R.string.gpu_governor));

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
        }

        TitleView tunables = new TitleView();
        tunables.setText(getString(R.string.gov_tunables));
        govCard.addItem(tunables);

        if (GPUFreq.hasHighspeedClock()){
            List<String> freqs = new ArrayList<>();
            List<Integer> list = GPUFreq.getAvailableS7FreqsSort();
            int value = 0;
            for (int i = 0; i < list.size(); i++) {
                freqs.add(String.valueOf(list.get(i)));
                if (list.get(i) == GPUFreq.getHighspeedClock()){
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

        if (GPUFreq.hasHighspeedLoad()){

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

        if (GPUFreq.hasHighspeedDelay()){

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

        if (govCard.size() > 0) {
            items.add(govCard);
        }
    }

    private void voltageInit(List<RecyclerViewItem> items) {
        CardView voltCard = new CardView(getActivity());
        voltCard.setTitle(getString(R.string.gpu_voltage));

        List<Integer> freqs = GPUFreq.getAvailableS7Freqs();
        List<String> voltages = GPUFreq.getVoltages();
        List<String> voltagesStock = GPUFreq.getStockVoltages();

        if (freqs != null && voltages != null && freqs.size() == voltages.size()) {
            for (int i = 0; i < freqs.size(); i++) {
                SeekBarView seekbar = new SeekBarView();
                seekbarInit(seekbar, freqs.get(i), voltages.get(i), voltagesStock.get(i));
                voltCard.addItem(seekbar);
            }
        }
        
        if (voltCard.size() > 0) {
            items.add(voltCard);
        }
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

        seekbar.setTitle(freq + " " + getString(R.string.mhz));
        seekbar.setSummary(getString(R.string.def) + ": " + voltageStock + " " + getString(R.string.mv));
        seekbar.setUnit(getString(R.string.mv));
        seekbar.setItems(progress);
        seekbar.setProgress(value);
        seekbar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {

            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                GPUFreq.setVoltage(freq, value, getActivity());
                /*getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reload();
                    }
                }, 200);*/
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mCurFreq != null) {
            int load = -1;
            String text = "";

            int freq = GPUFreq.getCurFreq();
            float maxFreq = GPUFreq.getAvailableS7FreqsSort().get(GPUFreq.getAvailableS7FreqsSort().size() - 1);
            text += freq / GPUFreq.getCurFreqOffset() + getString(R.string.mhz);
            mCurFreq.setText(text);
            float per = (float) freq / maxFreq * 100f;
            mCurFreq.addPercentage(load >= 0 ? load : Math.round(per > 100 ? 100 : per < 0 ? 0 : per));
        }

    }
}
