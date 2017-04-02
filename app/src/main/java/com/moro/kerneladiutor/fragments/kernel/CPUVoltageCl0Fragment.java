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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moro.kerneladiutor.R;
import com.moro.kerneladiutor.fragments.ApplyOnBootFragment;
import com.moro.kerneladiutor.fragments.BaseFragment;
import com.moro.kerneladiutor.fragments.RecyclerViewFragment;
import com.moro.kerneladiutor.utils.Prefs;
import com.moro.kerneladiutor.utils.Utils;
import com.moro.kerneladiutor.utils.kernel.cpuvoltage.VoltageCl0;
import com.moro.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.moro.kerneladiutor.views.recyclerview.SeekBarView;
import com.moro.kerneladiutor.views.recyclerview.SwitchView;

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
        addViewPagerFragment(GlobalOffsetFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mVoltages.clear();

        List<String> freqs = VoltageCl0.getFreqs();
        List<String> voltages = VoltageCl0.getVoltages();
        List<String> voltagesStock = VoltageCl0.getStockVoltages();

        if (freqs != null && voltages != null && freqs.size() == voltages.size()) {
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

        if (freqs != null && voltages != null) {
            for (int i = 0; i < mVoltages.size(); i++) {
                seekbarInit(mVoltages.get(i), freqs.get(i), voltages.get(i), voltagesStock.get(i));
            }
        }
    }

    public static class GlobalOffsetFragment extends BaseFragment {

        TextView vOffset;
        int mGlobalOffset;
        private CPUVoltageCl0Fragment mCPUVoltageFragment;

        public static GlobalOffsetFragment newInstance(CPUVoltageCl0Fragment cpuVoltageFragment) {
            GlobalOffsetFragment fragment = new GlobalOffsetFragment();
            fragment.mCPUVoltageFragment = cpuVoltageFragment;
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_global_offset, container, false);
            vOffset = (TextView) rootView.findViewById(R.id.offset);

            rootView.findViewById(R.id.button_minus).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGlobalOffset = mGlobalOffset - 25;
                    vOffset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobalOffset));
                    VoltageCl0.setGlobalOffset(mGlobalOffset, getActivity());
                    Prefs.saveInt("globalOffset_Cl0", mGlobalOffset, getActivity());
                    if (mCPUVoltageFragment != null) {
                        mCPUVoltageFragment.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCPUVoltageFragment.reload();
                            }
                        }, 200);
                    }
                }
            });
            rootView.findViewById(R.id.button_plus).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGlobalOffset = mGlobalOffset + 25;
                    vOffset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobalOffset));
                    VoltageCl0.setGlobalOffset(mGlobalOffset, getActivity());
                    Prefs.saveInt("globalOffset_Cl0", mGlobalOffset, getActivity());
                    if (mCPUVoltageFragment != null) {
                        mCPUVoltageFragment.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCPUVoltageFragment.reload();
                            }
                        }, 200);
                    }
                }
            });
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);
            mGlobalOffset = Prefs.getInt("globalOffset_Cl0", 0, getActivity());
            vOffset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobalOffset));
        }
    }
}
