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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.fragments.BaseFragment;
import com.moro.mtweaks.fragments.recyclerview.RecyclerViewFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.cpuvoltage.VoltageCl1;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SeekBarView;
import com.moro.mtweaks.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 07.05.16.
 */
public class CPUVoltageCl1Fragment extends RecyclerViewFragment {

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

        if (VoltageCl1.hasOverrideVmin()) {
            SwitchView overrideVmin = new SwitchView();
            overrideVmin.setTitle(getString(R.string.override_vmin));
            overrideVmin.setSummary(getString(R.string.override_vmin_summary));
            overrideVmin.setChecked(VoltageCl1.isOverrideVminEnabled());
            overrideVmin.setFullSpan(true);
            overrideVmin.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    VoltageCl1.enableOverrideVmin(isChecked, getActivity());
                }
            });

            items.add(overrideVmin);
        }

        List<String> freqs = VoltageCl1.getFreqs();
        List<String> voltages = VoltageCl1.getVoltages();
        List<String> voltagesStock = VoltageCl1.getStockVoltages();
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

        final int min = (Utils.strToInt(voltageStock) - 300);

        seekbar.setTitle(freq + " " + getString(R.string.mhz));
        seekbar.setSummary(getString(R.string.def) + ": " + voltageStock + " " + getString(R.string.mv));
        seekbar.setUnit(getString(R.string.mv));
        seekbar.setMax(1300);
        seekbar.setMin(min);
        seekbar.setOffset(25);
        seekbar.setProgress(Utils.strToInt(voltage) / 25 - (min / 25));
        seekbar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {

            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                int a = (position + (min / 25)) * 25;
                String str = Integer.toString(a);
                VoltageCl1.setVoltage(freq, str, getActivity());
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
        List<String> freqs = VoltageCl1.getFreqs();
        List<String> voltages = VoltageCl1.getVoltages();
        List<String> voltagesStock = VoltageCl1.getStockVoltages();
        if (freqs != null && voltages != null) {
            for (int i = 0; i < mVoltages.size(); i++) {
                seekbarInit(mVoltages.get(i), freqs.get(i), voltages.get(i), voltagesStock.get(i));
            }
        }
    }

    public static class GlobalOffsetFragment extends BaseFragment {

        public static GlobalOffsetFragment newInstance(CPUVoltageCl1Fragment cpuVoltageFragment) {
            GlobalOffsetFragment fragment = new GlobalOffsetFragment();
            fragment.mCPUVoltageFragment = cpuVoltageFragment;
            return fragment;
        }

        private CPUVoltageCl1Fragment mCPUVoltageFragment;
        private int mGlobaloffset;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_global_offset, container, false);
            final TextView offset = (TextView) rootView.findViewById(R.id.offset);
            offset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobaloffset));
            rootView.findViewById(R.id.button_minus).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGlobaloffset = mGlobaloffset - 25;
                    offset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobaloffset));
                    VoltageCl1.setGlobalOffset(mGlobaloffset, getActivity());
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
                    mGlobaloffset = mGlobaloffset + 25;
                    offset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobaloffset));
                    VoltageCl1.setGlobalOffset(mGlobaloffset, getActivity());
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
    }
}
