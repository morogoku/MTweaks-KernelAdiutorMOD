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
import com.moro.mtweaks.fragments.RecyclerViewFragment;
import com.moro.mtweaks.utils.Prefs;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.battery.Battery;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SeekBarView;
import com.moro.mtweaks.views.recyclerview.StatsView;
import com.moro.mtweaks.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 26.06.16.
 */
public class BatteryFragment extends RecyclerViewFragment {

    private StatsView mLevel;
    private StatsView mVoltage;
    private StatsView mCurrent;
    private StatsView mCurrentAvg;
    //private StatsView mCharType;
    private StatsView mCharSource;
    private StatsView mTemp;
    private StatsView mStatus;
    private StatsView mHealth;

    private static int sBatteryLevel;
    private static int sBatteryVoltage;
    private static int sBatteryCurrent;
    private static int sBatteryCurrentAvg;
    //private static String sBatteryCharType;
    private static String sBatteryCharSource;
    private static double sBatteryTemp;
    private static String sBatteryStatus;
    private static String sBatteryHealth;

    public int getSpanCount() {
        return super.getSpanCount() + 2;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (Battery.hasChargeS7()) {
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

        if (Battery.hasForceFastCharge()) {
            forceFastChargeInit(items);
        }
        if (Battery.hasBlx()) {
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
        if (Battery.hasCapacity(getActivity())) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.capacity),
                    Battery.getCapacity(getActivity()) + getString(R.string.mah)));
        }
    }

    private void chargeS7Init(List<RecyclerViewItem> items) {

        if (Battery.hasUnstableCharge()) {
            CardView unsCharge = new CardView(getActivity());
            unsCharge.setTitle(getString(R.string.unstable_charge_card));
            unsCharge.setFullSpan(true);

            SwitchView uCharge = new SwitchView();
            uCharge.setTitle(getString(R.string.enable_unstable_charge));
            uCharge.setSummary(getString(R.string.enable_unstable_charge_summary));
            uCharge.setChecked(Battery.isUnstableChargeEnabled());
            uCharge.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Battery.enableUnstableCharge(isChecked, getActivity());
                }
            });

            unsCharge.addItem(uCharge);

            items.add(unsCharge);
        }

        CardView hvPower = new CardView(getActivity());
        hvPower.setTitle(getString(R.string.hv_power_supply));
        hvPower.setFullSpan(true);

        if (Battery.hasS7HvInput()) {
            SeekBarView hv_input = new SeekBarView();
            hv_input.setTitle(getString(R.string.hv_input));
            hv_input.setSummary(getString(R.string.def) + ": " + Prefs.getString("bat_s7_hv_input", "", getActivity()) + getString(R.string.ma));
            hv_input.setMax(3000);
            hv_input.setMin(400);
            hv_input.setUnit(getString(R.string.ma));
            hv_input.setOffset(25);
            hv_input.setProgress(Utils.strToInt(Battery.getS7HvInput()) / 25 - 16);
            hv_input.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setS7HvInput((position + 16) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            hvPower.addItem(hv_input);
        }

        if (Battery.hasS7HvCharge()) {
            SeekBarView hv_charge = new SeekBarView();
            hv_charge.setTitle(getString(R.string.hv_charge));
            hv_charge.setSummary(getString(R.string.def) + ": " + Prefs.getString("bat_s7_hv_charge", "", getActivity()) + getString(R.string.ma));
            hv_charge.setMax(3150);
            hv_charge.setMin(1000);
            hv_charge.setUnit(getString(R.string.ma));
            hv_charge.setOffset(25);
            hv_charge.setProgress(Utils.strToInt(Battery.getS7HvCharge()) / 25 - 40);
            hv_charge.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setS7HvCharge((position + 40) * 25, getActivity());
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

        if (Battery.hasS7AcInput()) {
            SeekBarView ac_input = new SeekBarView();
            ac_input.setTitle(getString(R.string.ac_input));
            ac_input.setSummary(getString(R.string.def) + ": " + Prefs.getString("bat_s7_ac_input", "", getActivity()) + getString(R.string.ma));
            ac_input.setMax(3150);
            ac_input.setMin(400);
            ac_input.setUnit(getString(R.string.ma));
            ac_input.setOffset(25);
            ac_input.setProgress(Utils.strToInt(Battery.getS7AcInput()) / 25 - 16);
            ac_input.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setS7AcInput((position + 16) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            acMains.addItem(ac_input);
        }

        if (Battery.hasS7AcCharge()) {
            SeekBarView ac_charge = new SeekBarView();
            ac_charge.setTitle(getString(R.string.ac_charge));
            ac_charge.setSummary(getString(R.string.def) + ": " + Prefs.getString("bat_s7_ac_charge", "", getActivity()) + getString(R.string.ma));
            ac_charge.setMax(3150);
            ac_charge.setMin(400);
            ac_charge.setUnit(getString(R.string.ma));
            ac_charge.setOffset(25);
            ac_charge.setProgress(Utils.strToInt(Battery.getS7AcCharge()) / 25 - 16);
            ac_charge.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setS7AcCharge((position + 16) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            acMains.addItem(ac_charge);
        }

        if (Battery.hasS7AcInputScreen()) {
            SeekBarView ac_input_screen = new SeekBarView();
            ac_input_screen.setTitle(getString(R.string.ac_input_screen));
            ac_input_screen.setSummary(getString(R.string.def) + ": " + Prefs.getString("bat_s7_ac_input_screen", "", getActivity()) + getString(R.string.ma));
            ac_input_screen.setMax(3150);
            ac_input_screen.setMin(400);
            ac_input_screen.setUnit(getString(R.string.ma));
            ac_input_screen.setOffset(25);
            ac_input_screen.setProgress(Utils.strToInt(Battery.getS7AcInputScreen()) / 25 - 16);
            ac_input_screen.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setS7AcInputScreen((position + 16) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            acMains.addItem(ac_input_screen);
        }

        if (Battery.hasS7AcChargeScreen()) {
            SeekBarView ac_charge_screen = new SeekBarView();
            ac_charge_screen.setTitle(getString(R.string.ac_charge_screen));
            ac_charge_screen.setSummary(getString(R.string.def) + ": " + Prefs.getString("bat_s7_ac_charge_screen", "", getActivity()) + getString(R.string.ma));
            ac_charge_screen.setMax(3150);
            ac_charge_screen.setMin(400);
            ac_charge_screen.setUnit(getString(R.string.ma));
            ac_charge_screen.setOffset(25);
            ac_charge_screen.setProgress(Utils.strToInt(Battery.getS7AcChargeScreen()) / 25 - 16);
            ac_charge_screen.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setS7AcChargeScreen((position + 16) * 25, getActivity());
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

        if(Battery.hasS7UsbInput()) {
            SeekBarView usb_input = new SeekBarView();
            usb_input.setTitle(getString(R.string.usb_input));
            usb_input.setSummary(getString(R.string.def) + ": " + Prefs.getString("bat_s7_usb_input", "", getActivity()) + getString(R.string.ma));
            usb_input.setMax(1200);
            usb_input.setMin(100);
            usb_input.setUnit(getString(R.string.ma));
            usb_input.setOffset(25);
            usb_input.setProgress(Utils.strToInt(Battery.getS7UsbInput()) / 25 - 4);
            usb_input.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setS7UsbInput((position + 4) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            usbCard.addItem(usb_input);
        }

        if(Battery.hasS7UsbCharge()) {
            SeekBarView usb_charge = new SeekBarView();
            usb_charge.setTitle(getString(R.string.usb_charge));
            usb_charge.setSummary(getString(R.string.def) + ": " + Prefs.getString("bat_s7_usb_charge", "", getActivity()) + getString(R.string.ma));
            usb_charge.setMax(1200);
            usb_charge.setMin(100);
            usb_charge.setUnit(getString(R.string.ma));
            usb_charge.setOffset(25);
            usb_charge.setProgress(Utils.strToInt(Battery.getS7UsbCharge()) / 25 - 4);
            usb_charge.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setS7UsbCharge((position + 4) * 25, getActivity());
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

        if(Battery.hasS7CarInput()) {
            SeekBarView car_input = new SeekBarView();
            car_input.setTitle(getString(R.string.car_input));
            car_input.setSummary(getString(R.string.def) + ": " + Prefs.getString("bat_s7_car_input", "", getActivity()) + getString(R.string.ma));
            car_input.setMax(2300);
            car_input.setMin(800);
            car_input.setUnit(getString(R.string.ma));
            car_input.setOffset(25);
            car_input.setProgress(Utils.strToInt(Battery.getS7CarInput()) / 25 - 32);
            car_input.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setS7CarInput((position + 32) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            carCard.addItem(car_input);
        }

        if(Battery.hasS7CarCharge()) {
            SeekBarView car_charge = new SeekBarView();
            car_charge.setTitle(getString(R.string.car_charge));
            car_charge.setSummary(getString(R.string.def) + ": " + Prefs.getString("bat_s7_car_charge", "", getActivity()) + getString(R.string.ma));
            car_charge.setMax(2300);
            car_charge.setMin(800);
            car_charge.setUnit(getString(R.string.ma));
            car_charge.setOffset(25);
            car_charge.setProgress(Utils.strToInt(Battery.getS7CarCharge()) / 25 - 32);
            car_charge.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setS7CarCharge((position + 32) * 25, getActivity());
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

        if(Battery.hasS7WcInput()) {
            SeekBarView wc_input = new SeekBarView();
            wc_input.setTitle(getString(R.string.wc_input));
            wc_input.setSummary(getString(R.string.def) + ": " + Prefs.getString("bat_s7_wc_input", "", getActivity()) + getString(R.string.ma));
            wc_input.setMax(1500);
            wc_input.setMin(800);
            wc_input.setUnit(getString(R.string.ma));
            wc_input.setOffset(25);
            wc_input.setProgress(Utils.strToInt(Battery.getS7WcInput()) / 25 - 32);
            wc_input.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setS7WcInput((position + 32) * 25, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            wcCard.addItem(wc_input);
        }

        if(Battery.hasS7WcCharge()) {
            SeekBarView wc_charge = new SeekBarView();
            wc_charge.setTitle(getString(R.string.wc_charge));
            wc_charge.setSummary(getString(R.string.def) + ": " + Prefs.getString("bat_s7_wc_charge", "", getActivity()) + getString(R.string.ma));
            wc_charge.setMax(2300);
            wc_charge.setMin(800);
            wc_charge.setUnit(getString(R.string.ma));
            wc_charge.setOffset(25);
            wc_charge.setProgress(Utils.strToInt(Battery.getS7WcCharge()) / 25 - 32);
            wc_charge.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setS7WcCharge((position + 32) * 25, getActivity());
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
        forceFastCharge.setChecked(Battery.isForceFastChargeEnabled());
        forceFastCharge.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Battery.enableForceFastCharge(isChecked, getActivity());
            }
        });

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
        blx.setProgress(Battery.getBlx());
        blx.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Battery.setBlx(position, getActivity());
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

        if (Battery.hasChargeRateEnable()) {
            SwitchView chargeRate = new SwitchView();
            chargeRate.setSummary(getString(R.string.charge_rate));
            chargeRate.setChecked(Battery.isChargeRateEnabled());
            chargeRate.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Battery.enableChargeRate(isChecked, getActivity());
                }
            });

            chargeRateCard.addItem(chargeRate);
        }

        if (Battery.hasChargingCurrent()) {
            SeekBarView chargingCurrent = new SeekBarView();
            chargingCurrent.setTitle(getString(R.string.charging_current));
            chargingCurrent.setSummary(getString(R.string.charging_current_summary));
            chargingCurrent.setUnit(getString(R.string.ma));
            chargingCurrent.setMax(1500);
            chargingCurrent.setMin(100);
            chargingCurrent.setOffset(10);
            chargingCurrent.setProgress(Battery.getChargingCurrent() / 10 - 10);
            chargingCurrent.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setChargingCurrent((position + 10) * 10, getActivity());
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
            sBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            sBatteryVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            sBatteryCurrent = Utils.strToInt(Utils.readFile("/sys/devices/battery/power_supply/battery/current_now"));
            sBatteryCurrentAvg = Utils.strToInt(Utils.readFile("/sys/devices/battery/power_supply/battery/current_avg"));
            //sBatteryCharType = Utils.readFile("/sys/devices/battery/power_supply/battery/charge_type");
            sBatteryCharSource = Battery.getS7ChargeSource(context);
            sBatteryTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10D;
            sBatteryStatus = Utils.readFile("/sys/devices/battery/power_supply/battery/status");
            sBatteryHealth = Utils.readFile("/sys/devices/battery/power_supply/battery/health");
        }
    };

    @Override
    protected void refresh() {
        super.refresh();
        if (mLevel != null) {
            mLevel.setStat(sBatteryLevel + "%");
        }
        if (mVoltage != null) {
            mVoltage.setStat(sBatteryVoltage + getString(R.string.mv));
        }
        if (mCurrent != null) {
            mCurrent.setStat(sBatteryCurrent + getString(R.string.ma));
        }
        if (mCurrent != null) {
            mCurrentAvg.setStat(sBatteryCurrentAvg + getString(R.string.ma));
        }
        /*if (mCharType != null) {
            mCharType.setStat(sBatteryCharType);
        }*/
        if (mCharSource != null) {
            mCharSource.setStat(sBatteryCharSource);
        }
        if (mCurrent != null) {
            mTemp.setStat(sBatteryTemp + getString(R.string.celsius));
        }
        if (mStatus != null) {
            mStatus.setStat(sBatteryStatus);
        }
        if (mHealth != null) {
            mHealth.setStat(sBatteryHealth);
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
