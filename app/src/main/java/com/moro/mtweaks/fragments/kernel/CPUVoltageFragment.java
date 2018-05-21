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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.fragments.BaseFragment;
import com.moro.mtweaks.fragments.recyclerview.RecyclerViewFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.cpuvoltage.VoltageCl0;
import com.moro.mtweaks.utils.kernel.cpuvoltage.VoltageCl1;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.GenericSelectView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SeekBarView;
import com.moro.mtweaks.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 07.05.16.
 */
public class CPUVoltageFragment extends RecyclerViewFragment {

    private List<GenericSelectView> mVoltages = new ArrayList<>();

    //@Override
    //public int getSpanCount() {
    //    return super.getSpanCount() + 2;
    //}

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
            overridevminInit(items);
        }

        bigInit(items);
        littleInit(items);

    }

    private void overridevminInit(List<RecyclerViewItem> items) {
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

    private void bigInit(List<RecyclerViewItem> items) {

        CardView bigCard = new CardView(getActivity());
        bigCard.setTitle(getString(R.string.cluster_big));

        final List<String> freqs = VoltageCl1.getFreqs();
        List<String> voltages = VoltageCl1.getVoltages();
        if (freqs != null && voltages != null && freqs.size() == voltages.size()) {
            for (int i = 0; i < freqs.size(); i++) {
                final int min = (Utils.strToInt(voltages.get(i)) - 300);
                SeekBarView volt = new SeekBarView();
                volt.setTitle(freqs.get(i) + " " + getString(R.string.mhz));
                volt.setSummary(getString(R.string.def) + ": " + voltages.get(i) + " " + getString(R.string.mv));
                volt.setUnit(getString(R.string.mv));
                volt.setMax(1300);
                volt.setMin(min);
                volt.setOffset(25);
                volt.setProgress(Utils.strToInt(voltages.get(i)) / 25 - (min / 25));
                final int I = i;
                volt.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {

                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        int a = (position + (min / 25)) * 25;
                        String str = Integer.toString(a);
                        VoltageCl1.setVoltage(freqs.get(I), str, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });


                // GenericSelectView view = new GenericSelectView();
                //initView(view, freqs.get(i), voltages.get(i));
                //mVoltages.add(view);
                bigCard.addItem(volt);
            }
        }
        //items.addAll(mVoltages);
        if (bigCard.size() > 0) {
            items.add(bigCard);
        }
    }

    private void littleInit(List<RecyclerViewItem> items) {

        CardView littleCard = new CardView(getActivity());
        littleCard.setTitle(getString(R.string.cluster_little));

        final List<String> freqs = VoltageCl0.getFreqs();
        List<String> voltages = VoltageCl0.getVoltages();
        if (freqs != null && voltages != null && freqs.size() == voltages.size()) {
            for (int i = 0; i < freqs.size(); i++) {
                final int min = (Utils.strToInt(voltages.get(i)) - 300);
                SeekBarView volt = new SeekBarView();
                volt.setTitle(freqs.get(i) + " " + getString(R.string.mhz));
                volt.setSummary(getString(R.string.def) + ": " + voltages.get(i) + " " + getString(R.string.mv));
                volt.setUnit(getString(R.string.mv));
                volt.setMax(1300);
                volt.setMin(min);
                volt.setOffset(25);
                volt.setProgress(Utils.strToInt(voltages.get(i)) / 25 - (min / 25));
                final int I = i;
                volt.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {

                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        int a = (position + (min / 25)) * 25;
                        String str = Integer.toString(a);
                        VoltageCl0.setVoltage(freqs.get(I), str, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });


                // GenericSelectView view = new GenericSelectView();
                //initView(view, freqs.get(i), voltages.get(i));
                //mVoltages.add(view);
                littleCard.addItem(volt);
            }
        }
        //items.addAll(mVoltages);
        if (littleCard.size() > 0) {
            items.add(littleCard);
        }
    }

    private void reload() {
        List<String> freqs = VoltageCl1.getFreqs();
        List<String> voltages = VoltageCl1.getVoltages();
        if (freqs != null && voltages != null) {
            for (int i = 0; i < mVoltages.size(); i++) {
                initView(mVoltages.get(i), freqs.get(i), voltages.get(i));
            }
        }
    }

    private void initView(GenericSelectView view, final String freq, String voltage) {
        String freqText = VoltageCl1.isVddVoltage() ? String.valueOf(Utils.strToInt(freq) / 1000) : freq;
        view.setTitle(freqText + getString(R.string.mhz));
        view.setSummary(voltage + getString(R.string.mv));
        view.setValue("");
        view.setValueRaw(voltage);
        view.setInputType(InputType.TYPE_CLASS_NUMBER);
        view.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
            @Override
            public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                VoltageCl1.setVoltage(freq, value, getActivity());
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reload();
                    }
                }, 200);
            }
        });
    }

    public static class GlobalOffsetFragment extends BaseFragment {

        public static GlobalOffsetFragment newInstance(CPUVoltageFragment cpuVoltageFragment) {
            GlobalOffsetFragment fragment = new GlobalOffsetFragment();
            fragment.mCPUVoltageFragment = cpuVoltageFragment;
            return fragment;
        }

        private CPUVoltageFragment mCPUVoltageFragment;
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
                    mGlobaloffset -= 5;
                    offset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobaloffset));
                    VoltageCl1.setGlobalOffset(-5, getActivity());
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
                    mGlobaloffset += 5;
                    offset.setText(Utils.strFormat("%d" + getString(R.string.mv), mGlobaloffset));
                    VoltageCl1.setGlobalOffset(5, getActivity());
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
