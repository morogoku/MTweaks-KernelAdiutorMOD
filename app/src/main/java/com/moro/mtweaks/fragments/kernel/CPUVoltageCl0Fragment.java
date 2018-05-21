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
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.cpuvoltage.VoltageCl0;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SeekBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 07.05.16.
 */
public class CPUVoltageCl0Fragment extends RecyclerViewFragment {

    private List<SeekBarView> mVoltages = new ArrayList<>();

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mVoltages.clear();

        final List<String> freqs = VoltageCl0.getFreqs();
        final List<String> voltages = VoltageCl0.getVoltages();
        final List<String> voltagesStock = VoltageCl0.getStockVoltages();

        if (freqs != null && voltages != null && voltagesStock != null && freqs.size() == voltages.size()) {

            CardView freqCard = new CardView(getActivity());
            freqCard.setTitle(getString(R.string.cpu_volt_control));

            List<String> progress = new ArrayList<>();
            for (float i = -100000f; i < 31250f; i += 6250) {
                String global = String.valueOf(i / VoltageCl0.getOffset());
                progress.add(global);
            }

            SeekBarView seekbarProf = new SeekBarView();
            seekbarProf.setTitle(getString(R.string.cpu_volt_profile));
            seekbarProf.setSummary(getString(R.string.cpu_volt_profile_summary));
            seekbarProf.setUnit(getString(R.string.mv));
            seekbarProf.setItems(progress);
            seekbarProf.setProgress(16);
            seekbarProf.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    for (int i = 0; i < voltages.size(); i++) {
                        String volt = String.valueOf(Utils.strToFloat(voltagesStock.get(i)) + Utils.strToFloat(value));
                        String freq = freqs.get(i);
                        VoltageCl0.setVoltage(freq, volt, getActivity());
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

            freqCard.addItem(seekbarProf);
            items.add(freqCard);

            for (int i = 0; i < freqs.size(); i++) {
                SeekBarView seekbar = new SeekBarView();
                seekbarInit(seekbar, freqs.get(i), voltages.get(i), voltagesStock.get(i));
                mVoltages.add(seekbar);
            }
        }
        items.addAll(mVoltages);
    }

    private void seekbarInit(SeekBarView seekbar, final String freq, String voltage,
                             String voltageStock) {

        int mStep = 6250;
        int mOffset = VoltageCl0.getOffset();
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
                VoltageCl0.setVoltage(freq, value, getActivity());
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

    private void reload() {
        List<String> freqs = VoltageCl0.getFreqs();
        List<String> voltages = VoltageCl0.getVoltages();
        List<String> voltagesStock = VoltageCl0.getStockVoltages();

        if (freqs != null && voltages != null && voltagesStock!= null) {
            for (int i = 0; i < mVoltages.size(); i++) {
                seekbarInit(mVoltages.get(i), freqs.get(i), voltages.get(i), voltagesStock.get(i));
            }
        }
    }
}
