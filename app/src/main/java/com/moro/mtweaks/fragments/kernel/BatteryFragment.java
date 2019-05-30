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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.fragments.DescriptionFragment;
import com.moro.mtweaks.fragments.recyclerview.RecyclerViewFragment;
import com.moro.mtweaks.utils.AppSettings;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.battery.Battery;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SeekBarView;
import com.moro.mtweaks.views.recyclerview.StatsView;
import com.moro.mtweaks.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 26.06.16.
 */
public class BatteryFragment extends RecyclerViewFragment {

    private Battery mBattery;

    private StatsView mLevel;
    private StatsView mVoltage;
    private StatsView mCurrent;
    private StatsView mCurrentAvg;
    //private StatsView mCharType;
    private StatsView mCharSource;
    private StatsView mTemp;
    private StatsView mStatus;
    private StatsView mHealth;

    private int mBatteryLevel;
    private int mBatteryVoltage;
    private int mBatteryCurrent;
    private int mBatteryCurrentAvg;
    //private static String sBatteryCharType;
    private String mBatteryCharSource;
    private double mBatteryTemp;
    private String mBatteryStatus;
    private String mBatteryHealth;
    private String mBatteryHealthValue;

    public int getSpanCount() {
        return super.getSpanCount() + 2;
}

    @Override
    protected void init() {
        super.init();

        mBattery = Battery.getInstance(Objects.requireNonNull(getActivity()));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (mBattery.hasChargeS7()) {
            //chartypeInit(items);
            charsourceInit(items);
            statusInit(items);
            currentavgInit(items);
            currentInit(items);
            voltageInit(items);
            tempInit(items);
            levelInit(items);
            healthInit(items);
            chargeS7Init(items);
        } else {
            levelInit(items);
            voltageInit(items);
        }
        if (mBattery.hasForceFastCharge()) {
            forceFastChargeInit(items);
        }
        if (mBattery.hasBlx()) {
            blxInit(items);
        }
        chargeRateInit(items);
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (itemsSize() > 2) {
            addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        }
        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.capacity),
                mBattery.getCapacity() + getString(R.string.mah)));
    }

    private void chargeS7Init(List<RecyclerViewItem> items) {

        CardView unsCharge = new CardView(getActivity());
        unsCharge.setTitle(getString(R.string.unstable_charge_card));
        unsCharge.setFullSpan(true);

        if (mBattery.hasUnstableCharge()) {
            SwitchView uCharge = new SwitchView();
            uCharge.setTitle(getString(R.string.enable_unstable_charge));
            uCharge.setSummary(getString(R.string.enable_unstable_charge_summary));
            uCharge.setChecked(mBattery.isUnstableChargeEnabled());
            uCharge.addOnSwitchListener((switchView, isChecked)
                    -> mBattery.enableUnstableCharge(isChecked, getActivity()));

            unsCharge.addItem(uCharge);
        }

        if (unsCharge.size() > 0) {
            items.add(unsCharge);
        }


        CardView storeCard = new CardView(getActivity());
        storeCard.setTitle(getString(R.string.store_mode));
        storeCard.setFullSpan(true);

        if (mBattery.hasS7StoreMode()){
            SwitchView sMode = new SwitchView();
            sMode.setTitle(getString(R.string.store_mode));
            sMode.setSummary(getString(R.string.store_mode_summary));
            sMode.setChecked(mBattery.isS7StoreModeEnabled());
            sMode.addOnSwitchListener((switchView, isChecked)
                    -> mBattery.enableS7StoreMode(isChecked, getActivity()));

            storeCard.addItem(sMode);


            SeekBarView smMax = new SeekBarView();
            smMax.setTitle(getString(R.string.store_mode_max));
            smMax.setSummary(getString(R.string.store_mode_max_summary));
            smMax.setMax(100);
            smMax.setMin(1);
            smMax.setUnit(getString(R.string.percent));
            smMax.setProgress(Utils.strToInt(mBattery.getS7StoreModeMax()) -1 );
            smMax.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7StoreModeMax(position +1, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            storeCard.addItem(smMax);


            SeekBarView smMin = new SeekBarView();
            smMin.setTitle(getString(R.string.store_mode_min));
            smMin.setSummary(getString(R.string.store_mode_min_summary));
            smMin.setMax(100);
            smMin.setMin(1);
            smMin.setUnit(getString(R.string.percent));
            smMin.setProgress(Utils.strToInt(mBattery.getS7StoreModeMin()) -1 );
            smMin.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7StoreModeMin(position +1, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            storeCard.addItem(smMin);
        }

        if (storeCard.size() > 0) {
            items.add(storeCard);
        }


        CardView hvPower = new CardView(getActivity());
        hvPower.setTitle(getString(R.string.hv_power_supply));
        hvPower.setFullSpan(true);

        if (mBattery.hasS7HvInput()) {
            SeekBarView hv_input = new SeekBarView();
            hv_input.setTitle(getString(R.string.hv_input));
            hv_input.setSummary(getString(R.string.def) + ": " + AppSettings.getString("bat_s7_hv_input", "", getActivity()) + getString(R.string.ma));
            hv_input.setMax(3000);
            hv_input.setMin(400);
            hv_input.setUnit(getString(R.string.ma));
            hv_input.setOffset(25);
            hv_input.setProgress(Utils.strToInt(mBattery.getS7HvInput()) / 25 - 16);
            hv_input.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7HvInput((position + 16) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            hvPower.addItem(hv_input);
        }

        if (mBattery.hasS7HvCharge()) {
            SeekBarView hv_charge = new SeekBarView();
            hv_charge.setTitle(getString(R.string.hv_charge));
            hv_charge.setSummary(getString(R.string.def) + ": " + AppSettings.getString("bat_s7_hv_charge", "", getActivity()) + getString(R.string.ma));
            hv_charge.setMax(3150);
            hv_charge.setMin(1000);
            hv_charge.setUnit(getString(R.string.ma));
            hv_charge.setOffset(25);
            hv_charge.setProgress(Utils.strToInt(mBattery.getS7HvCharge()) / 25 - 40);
            hv_charge.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7HvCharge((position + 40) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            hvPower.addItem(hv_charge);
        }

        if (hvPower.size() > 0) {
            items.add(hvPower);
        }


        CardView acMains = new CardView(getActivity());
        acMains.setTitle(getString(R.string.ac_mains));
        acMains.setFullSpan(true);

        if (mBattery.hasS7AcInput()) {
            SeekBarView ac_input = new SeekBarView();
            ac_input.setTitle(getString(R.string.ac_input));
            ac_input.setSummary(getString(R.string.def) + ": " + AppSettings.getString("bat_s7_ac_input", "", getActivity()) + getString(R.string.ma));
            ac_input.setMax(3150);
            ac_input.setMin(400);
            ac_input.setUnit(getString(R.string.ma));
            ac_input.setOffset(25);
            ac_input.setProgress(Utils.strToInt(mBattery.getS7AcInput()) / 25 - 16);
            ac_input.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7AcInput((position + 16) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            acMains.addItem(ac_input);
        }

        if (mBattery.hasS7AcCharge()) {
            SeekBarView ac_charge = new SeekBarView();
            ac_charge.setTitle(getString(R.string.ac_charge));
            ac_charge.setSummary(getString(R.string.def) + ": " + AppSettings.getString("bat_s7_ac_charge", "", getActivity()) + getString(R.string.ma));
            ac_charge.setMax(3150);
            ac_charge.setMin(400);
            ac_charge.setUnit(getString(R.string.ma));
            ac_charge.setOffset(25);
            ac_charge.setProgress(Utils.strToInt(mBattery.getS7AcCharge()) / 25 - 16);
            ac_charge.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7AcCharge((position + 16) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            acMains.addItem(ac_charge);
        }

        if (mBattery.hasS7AcInputScreen()) {
            SeekBarView ac_input_screen = new SeekBarView();
            ac_input_screen.setTitle(getString(R.string.ac_input_screen));
            ac_input_screen.setSummary(getString(R.string.def) + ": " + AppSettings.getString("bat_s7_ac_input_screen", "", getActivity()) + getString(R.string.ma));
            ac_input_screen.setMax(3150);
            ac_input_screen.setMin(400);
            ac_input_screen.setUnit(getString(R.string.ma));
            ac_input_screen.setOffset(25);
            ac_input_screen.setProgress(Utils.strToInt(mBattery.getS7AcInputScreen()) / 25 - 16);
            ac_input_screen.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7AcInputScreen((position + 16) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            acMains.addItem(ac_input_screen);
        }

        if (mBattery.hasS7AcChargeScreen()) {
            SeekBarView ac_charge_screen = new SeekBarView();
            ac_charge_screen.setTitle(getString(R.string.ac_charge_screen));
            ac_charge_screen.setSummary(getString(R.string.def) + ": " + AppSettings.getString("bat_s7_ac_charge_screen", "", getActivity()) + getString(R.string.ma));
            ac_charge_screen.setMax(3150);
            ac_charge_screen.setMin(400);
            ac_charge_screen.setUnit(getString(R.string.ma));
            ac_charge_screen.setOffset(25);
            ac_charge_screen.setProgress(Utils.strToInt(mBattery.getS7AcChargeScreen()) / 25 - 16);
            ac_charge_screen.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7AcChargeScreen((position + 16) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            acMains.addItem(ac_charge_screen);
        }

        if (acMains.size() > 0) {
            items.add(acMains);
        }


        CardView usbCard = new CardView(getActivity());
        usbCard.setTitle(getString(R.string.usb_port));
        usbCard.setFullSpan(true);

        if(mBattery.hasS7UsbInput()) {
            SeekBarView usb_input = new SeekBarView();
            usb_input.setTitle(getString(R.string.usb_input));
            usb_input.setSummary(getString(R.string.def) + ": " + AppSettings.getString("bat_s7_usb_input", "", getActivity()) + getString(R.string.ma));
            usb_input.setMax(1200);
            usb_input.setMin(100);
            usb_input.setUnit(getString(R.string.ma));
            usb_input.setOffset(25);
            usb_input.setProgress(Utils.strToInt(mBattery.getS7UsbInput()) / 25 - 4);
            usb_input.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7UsbInput((position + 4) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            usbCard.addItem(usb_input);
        }

        if(mBattery.hasS7UsbCharge()) {
            SeekBarView usb_charge = new SeekBarView();
            usb_charge.setTitle(getString(R.string.usb_charge));
            usb_charge.setSummary(getString(R.string.def) + ": " + AppSettings.getString("bat_s7_usb_charge", "", getActivity()) + getString(R.string.ma));
            usb_charge.setMax(1200);
            usb_charge.setMin(100);
            usb_charge.setUnit(getString(R.string.ma));
            usb_charge.setOffset(25);
            usb_charge.setProgress(Utils.strToInt(mBattery.getS7UsbCharge()) / 25 - 4);
            usb_charge.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7UsbCharge((position + 4) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            usbCard.addItem(usb_charge);
        }

        if (usbCard.size() > 0) {
            items.add(usbCard);
        }


        CardView carCard = new CardView(getActivity());
        carCard.setTitle(getString(R.string.car_dock));
        carCard.setFullSpan(true);

        if(mBattery.hasS7CarInput()) {
            SeekBarView car_input = new SeekBarView();
            car_input.setTitle(getString(R.string.car_input));
            car_input.setSummary(getString(R.string.def) + ": " + AppSettings.getString("bat_s7_car_input", "", getActivity()) + getString(R.string.ma));
            car_input.setMax(2300);
            car_input.setMin(800);
            car_input.setUnit(getString(R.string.ma));
            car_input.setOffset(25);
            car_input.setProgress(Utils.strToInt(mBattery.getS7CarInput()) / 25 - 32);
            car_input.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7CarInput((position + 32) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            carCard.addItem(car_input);
        }

        if(mBattery.hasS7CarCharge()) {
            SeekBarView car_charge = new SeekBarView();
            car_charge.setTitle(getString(R.string.car_charge));
            car_charge.setSummary(getString(R.string.def) + ": " + AppSettings.getString("bat_s7_car_charge", "", getActivity()) + getString(R.string.ma));
            car_charge.setMax(2300);
            car_charge.setMin(800);
            car_charge.setUnit(getString(R.string.ma));
            car_charge.setOffset(25);
            car_charge.setProgress(Utils.strToInt(mBattery.getS7CarCharge()) / 25 - 32);
            car_charge.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7CarCharge((position + 32) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            carCard.addItem(car_charge);
        }

        if (carCard.size() > 0) {
            items.add(carCard);
        }


        CardView wcCard = new CardView(getActivity());
        wcCard.setTitle(getString(R.string.wireless_power));
        wcCard.setFullSpan(true);

        if(mBattery.hasS7WcInput()) {
            SeekBarView wc_input = new SeekBarView();
            wc_input.setTitle(getString(R.string.wc_input));
            wc_input.setSummary(getString(R.string.def) + ": " + AppSettings.getString("bat_s7_wc_input", "", getActivity()) + getString(R.string.ma));
            wc_input.setMax(1500);
            wc_input.setMin(800);
            wc_input.setUnit(getString(R.string.ma));
            wc_input.setOffset(25);
            wc_input.setProgress(Utils.strToInt(mBattery.getS7WcInput()) / 25 - 32);
            wc_input.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7WcInput((position + 32) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            wcCard.addItem(wc_input);
        }

        if(mBattery.hasS7WcCharge()) {
            SeekBarView wc_charge = new SeekBarView();
            wc_charge.setTitle(getString(R.string.wc_charge));
            wc_charge.setSummary(getString(R.string.def) + ": " + AppSettings.getString("bat_s7_wc_charge", "", getActivity()) + getString(R.string.ma));
            wc_charge.setMax(2300);
            wc_charge.setMin(800);
            wc_charge.setUnit(getString(R.string.ma));
            wc_charge.setOffset(25);
            wc_charge.setProgress(Utils.strToInt(mBattery.getS7WcCharge()) / 25 - 32);
            wc_charge.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setS7WcCharge((position + 32) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            wcCard.addItem(wc_charge);
        }

        if (wcCard.size() > 0) {
            items.add(wcCard);
        }
    }

    private void levelInit(List<RecyclerViewItem> items) {
        mLevel = new StatsView();
        mLevel.setTitle(getString(R.string.level));

        items.add(mLevel);
    }

    private void voltageInit(List<RecyclerViewItem> items) {
        mVoltage = new StatsView();
        mVoltage.setTitle(getString(R.string.voltage));

        items.add(mVoltage);
    }

    private void currentInit(List<RecyclerViewItem> items) {
        mCurrent = new StatsView();
        mCurrent.setTitle(getString(R.string.current_now));

        items.add(mCurrent);
    }

    private void currentavgInit(List<RecyclerViewItem> items) {
        mCurrentAvg = new StatsView();
        mCurrentAvg.setTitle(getString(R.string.current_avg));

        items.add(mCurrentAvg);
    }

    private void tempInit(List<RecyclerViewItem> items) {
        mTemp = new StatsView();
        mTemp.setTitle(getString(R.string.temp));

        items.add(mTemp);
    }

    private void statusInit(List<RecyclerViewItem> items) {
        mStatus = new StatsView();
        mStatus.setTitle(getString(R.string.status));
        mStatus.setFullSpan(true);

        items.add(mStatus);
    }

    private void healthInit(List<RecyclerViewItem> items) {
        mHealth = new StatsView();
        mHealth.setTitle(getString(R.string.health));

        items.add(mHealth);
    }
/*
    private void chartypeInit(List<RecyclerViewItem> items) {
        mCharType = new StatsView();
        mCharType.setTitle(getString(R.string.char_type));
        mCharType.setFullSpan(true);

        items.add(mCharType);
    }
*/
    private void charsourceInit(List<RecyclerViewItem> items) {
        mCharSource = new StatsView();
        mCharSource.setTitle(getString(R.string.char_source));
        mCharSource.setFullSpan(true);

        items.add(mCharSource);
    }

    private void forceFastChargeInit(List<RecyclerViewItem> items) {
        SwitchView forceFastCharge = new SwitchView();
        forceFastCharge.setTitle(getString(R.string.usb_fast_charge));
        forceFastCharge.setSummary(getString(R.string.usb_fast_charge_summary));
        forceFastCharge.setChecked(mBattery.isForceFastChargeEnabled());
        forceFastCharge.addOnSwitchListener((switchView, isChecked)
                -> mBattery.enableForceFastCharge(isChecked, getActivity()));

        items.add(forceFastCharge);
    }

    private void blxInit(List<RecyclerViewItem> items) {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.disabled));
        for (int i = 0; i <= 100; i++) {
            list.add(String.valueOf(i));
        }

        SeekBarView blx = new SeekBarView();
        blx.setTitle(getString(R.string.blx));
        blx.setSummary(getString(R.string.blx_summary));
        blx.setItems(list);
        blx.setProgress(mBattery.getBlx());
        blx.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mBattery.setBlx(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(blx);
    }

    private void chargeRateInit(List<RecyclerViewItem> items) {
        CardView chargeRateCard = new CardView(getActivity());
        chargeRateCard.setTitle(getString(R.string.charge_rate));

        if (mBattery.hasChargeRateEnable()) {
            SwitchView chargeRate = new SwitchView();
            chargeRate.setSummary(getString(R.string.charge_rate));
            chargeRate.setChecked(mBattery.isChargeRateEnabled());
            chargeRate.addOnSwitchListener((switchView, isChecked)
                    -> mBattery.enableChargeRate(isChecked, getActivity()));

            chargeRateCard.addItem(chargeRate);
        }

        if (mBattery.hasChargingCurrent()) {
            SeekBarView chargingCurrent = new SeekBarView();
            chargingCurrent.setTitle(getString(R.string.charging_current));
            chargingCurrent.setSummary(getString(R.string.charging_current_summary));
            chargingCurrent.setUnit(getString(R.string.ma));
            chargingCurrent.setMax(1500);
            chargingCurrent.setMin(100);
            chargingCurrent.setOffset(10);
            chargingCurrent.setProgress(mBattery.getChargingCurrent() / 10 - 10);
            chargingCurrent.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setChargingCurrent((position + 10) * 10, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            chargeRateCard.addItem(chargingCurrent);
        }

        if (chargeRateCard.size() > 0) {
            items.add(chargeRateCard);
        }
    }

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            mBatteryVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            mBatteryCurrent = Utils.strToInt(Utils.readFile("/sys/devices/battery/power_supply/battery/current_now"));
            mBatteryCurrentAvg = Utils.strToInt(Utils.readFile("/sys/devices/battery/power_supply/battery/current_avg"));
            //sBatteryCharType = Utils.readFile("/sys/devices/battery/power_supply/battery/charge_type");
            mBatteryCharSource = mBattery.getS7ChargeSource(context);
            mBatteryTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10D;
            mBatteryStatus = Utils.readFile("/sys/devices/battery/power_supply/battery/status");
            mBatteryHealth = Utils.readFile("/sys/devices/battery/power_supply/battery/health");
            mBatteryHealthValue = mBattery.getHealthValue();
        }
    };

    @Override
    protected void refresh() {
        super.refresh();
        if (mLevel != null) {
            mLevel.setStat(mBatteryLevel + "%");
        }
        if (mVoltage != null) {
            mVoltage.setStat(mBatteryVoltage + getString(R.string.mv));
        }
        if (mCurrent != null) {
            mCurrent.setStat(mBatteryCurrent + getString(R.string.ma));
        }
        if (mCurrent != null) {
            mCurrentAvg.setStat(mBatteryCurrentAvg + getString(R.string.ma));
        }
        /*if (mCharType != null) {
            mCharType.setStat(mBatteryCharType);
        }*/
        if (mCharSource != null) {
            mCharSource.setStat(mBatteryCharSource);
        }
        if (mCurrent != null) {
            mTemp.setStat(mBatteryTemp + getString(R.string.celsius));
        }
        if (mStatus != null) {
            mStatus.setStat(mBatteryStatus);
        }
        if (mHealth != null) {
            if (mBatteryHealthValue != null) {
                mHealth.setStat(mBatteryHealth + " / " + mBatteryHealthValue + getString(R.string.percent));
            } else {
                mHealth.setStat(mBatteryHealth);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(mBatteryReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

}
