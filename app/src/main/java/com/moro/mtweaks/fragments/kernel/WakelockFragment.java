package com.moro.mtweaks.fragments.kernel;

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.fragments.RecyclerViewFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.wakelock.BoefflaWakelock;
import com.moro.mtweaks.utils.kernel.wakelock.Wakelock;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.DescriptionView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SeekBarView;
import com.moro.mtweaks.views.recyclerview.SelectView;
import com.moro.mtweaks.views.recyclerview.SwitchView;
import com.moro.mtweaks.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Morogoku on 10/04/2017.
 */

public class WakelockFragment extends RecyclerViewFragment {

    private List<CardView> mWakeCard = new ArrayList<>();

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
        if (BoefflaWakelock.supported()){
            boefflaWakelockInit(items);
        }
    }

    private void boefflaWakelockInit(List<RecyclerViewItem> items){
        mWakeCard.clear();

        TitleView bwbT = new TitleView();
        bwbT.setText(getString(R.string.boeffla_wakelock) + " v" + BoefflaWakelock.getVersion());
        items.add(bwbT);

        DescriptionView bwbD = new DescriptionView();
        bwbD.setSummary(getString(R.string.boeffla_wakelock_summary));
        items.add(bwbD);

        SelectView bwOrder = new SelectView();
        bwOrder.setTitle(getString(R.string.wkl_order));
        bwOrder.setSummary(getString(R.string.wkl_order_summary));
        bwOrder.setItems(Arrays.asList(getResources().getStringArray(R.array.b_wakelocks_oder)));
        bwOrder.setItem(getString(R.string.wkl_time));
        bwOrder.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                BoefflaWakelock.setWakelockOrder(position);
                bwCardReload();
            }
        });
        items.add(bwOrder);

        List<BoefflaWakelock.ListWake> wakelocksB = BoefflaWakelock.getWakelockListBlocked();
        String titleB = getString(R.string.wkl_blocked);
        CardView cardB = new CardView(getActivity());
        bwCardInit(cardB, titleB, wakelocksB);
        mWakeCard.add(cardB);

        List<BoefflaWakelock.ListWake> wakelocksA = BoefflaWakelock.getWakelockListAllowed();
        String titleA = getString(R.string.wkl_allowed);
        CardView cardA = new CardView(getActivity());
        bwCardInit(cardA, titleA, wakelocksA);
        mWakeCard.add(cardA);

        items.addAll(mWakeCard);
    }

    private void bwCardInit(CardView card, String title, List<BoefflaWakelock.ListWake> wakelocks){
        card.clearItems();
        card.setTitle(title);

        for(BoefflaWakelock.ListWake wake : wakelocks){

            final String name = wake.getName();
            String wakeup = String.valueOf(wake.getWakeup());
            String time = String.valueOf(wake.getTime() / 1000);
            time = Utils.sToString(Utils.strToLong(time));

            SwitchView sw = new SwitchView();
            sw.setTitle(name);
            sw.setSummary(getString(R.string.wkl_total_time) + ": " + time + "\n" +
                    getString(R.string.wkl_wakep_count) + ": " + wakeup);
            sw.setChecked(!BoefflaWakelock.isWakelockBlocked(name));
            sw.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    if(isChecked) {
                        BoefflaWakelock.setWakelockAllowed(name, getActivity());
                    }else{
                        BoefflaWakelock.setWakelockBlocked(name, getActivity());
                    }
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bwCardReload();
                        }
                    }, 50);
                }
            });

            card.addItem(sw);
        }
    }

    private void bwCardReload() {

        List<BoefflaWakelock.ListWake> wakelocksB = BoefflaWakelock.getWakelockListBlocked();
        String titleB = getString(R.string.wkl_blocked);
        bwCardInit(mWakeCard.get(0), titleB, wakelocksB);

        List<BoefflaWakelock.ListWake> wakelocksA = BoefflaWakelock.getWakelockListAllowed();
        String titleA = getString(R.string.wkl_allowed);
        bwCardInit(mWakeCard.get(1), titleA, wakelocksA);
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
