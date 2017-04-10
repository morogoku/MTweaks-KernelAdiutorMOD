package com.moro.kerneladiutor.fragments.kernel;

import com.moro.kerneladiutor.R;
import com.moro.kerneladiutor.fragments.ApplyOnBootFragment;
import com.moro.kerneladiutor.fragments.RecyclerViewFragment;
import com.moro.kerneladiutor.utils.Utils;
import com.moro.kerneladiutor.utils.kernel.wakelock.Wakelock;
import com.moro.kerneladiutor.views.recyclerview.CardView;
import com.moro.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.moro.kerneladiutor.views.recyclerview.SeekBarView;
import com.moro.kerneladiutor.views.recyclerview.SwitchView;

import java.util.List;

/**
 * Created by Morogoku on 10/04/2017.
 */

public class WakelockFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        if (Wakelock.hasWakelock()) {
            wakelocksInit(items);
        }
    }

    private void wakelocksInit(List<RecyclerViewItem> items){

        CardView wake = new CardView(getActivity());
        wake.setTitle(getString(R.string.wkl_control));

        if(Wakelock.hasSensorHub()) {
            SwitchView sh = new SwitchView();
            sh.setTitle(getString(R.string.wkl_sensorhub));
            sh.setSummary(getString(R.string.wkl_sensorhub_summary));
            sh.setChecked(Wakelock.isSensorHubEnabled());
            sh.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Wakelock.enableSensorHub(isChecked, getActivity());
                }
            });

            wake.addItem(sh);
        }

        if(Wakelock.hasSSP()) {
            SwitchView ssp = new SwitchView();
            ssp.setTitle(getString(R.string.wkl_ssp));
            ssp.setSummary(getString(R.string.wkl_ssp_summary));
            ssp.setChecked(Wakelock.isSSPEnabled());
            ssp.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Wakelock.enableSSP(isChecked, getActivity());
                }
            });

            wake.addItem(ssp);
        }

        if(Wakelock.hasGPS()) {
            SwitchView gps = new SwitchView();
            gps.setTitle(getString(R.string.wkl_gps));
            gps.setSummary(getString(R.string.wkl_gps_summary));
            gps.setChecked(Wakelock.isGPSEnabled());
            gps.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Wakelock.enableGPS(isChecked, getActivity());
                }
            });

            wake.addItem(gps);
        }

        if(Wakelock.hasWireless()) {
            SwitchView wifi = new SwitchView();
            wifi.setTitle(getString(R.string.wkl_wireless));
            wifi.setSummary(getString(R.string.wkl_wireless_summary));
            wifi.setChecked(Wakelock.isWirelessEnabled());
            wifi.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Wakelock.enableWireless(isChecked, getActivity());
                }
            });

            wake.addItem(wifi);
        }

        if(Wakelock.hasBluetooth()) {
            SwitchView bt = new SwitchView();
            bt.setTitle(getString(R.string.wkl_bluetooth));
            bt.setSummary(getString(R.string.wkl_bluetooth_summary));
            bt.setChecked(Wakelock.isBluetoothEnabled());
            bt.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Wakelock.enableBluetooth(isChecked, getActivity());
                }
            });

            wake.addItem(bt);
        }

        if(Wakelock.hasBattery()) {
            SeekBarView bat = new SeekBarView();
            bat.setTitle(getString(R.string.wkl_battery));
            bat.setSummary(getString(R.string.wkl_battery_summary));
            bat.setMax(15);
            bat.setMin(1);
            bat.setProgress(Utils.strToInt(Wakelock.getBattery()) - 1);
            bat.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Wakelock.setBattery((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            wake.addItem(bat);
        }

        if(Wakelock.hasNFC()) {
            SeekBarView nfc = new SeekBarView();
            nfc.setTitle(getString(R.string.wkl_nfc));
            nfc.setSummary(getString(R.string.wkl_nfc_summary));
            nfc.setMax(3);
            nfc.setMin(1);
            nfc.setProgress(Utils.strToInt(Wakelock.getNFC()) - 1);
            nfc.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Wakelock.setNFC((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            wake.addItem(nfc);
        }

        if (wake.size() > 0) {
            items.add(wake);
        }
    }
}
