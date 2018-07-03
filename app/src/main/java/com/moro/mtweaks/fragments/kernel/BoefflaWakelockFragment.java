package com.moro.mtweaks.fragments.kernel;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.fragments.recyclerview.RecyclerViewFragment;
import com.moro.mtweaks.utils.AppSettings;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.boefflawakelock.BoefflaWakelock;
import com.moro.mtweaks.utils.kernel.boefflawakelock.WakeLockInfo;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.DescriptionView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SelectView;
import com.moro.mtweaks.views.recyclerview.SwitchView;
import com.moro.mtweaks.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by MoroGoku on 10/11/2017.
 */

public class BoefflaWakelockFragment extends RecyclerViewFragment {

    private List<CardView> mWakeCard = new ArrayList<>();
    boolean mAlertCheckbox = true;

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

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
        bwOrder.setItem(BoefflaWakelock.getWakelockOrder());
        bwOrder.setOnItemSelected((selectView, position, item) -> {
            BoefflaWakelock.setWakelockOrder(position);
            bwCardReload();
        });
        items.add(bwOrder);


        List<WakeLockInfo> wakelocksinfo = BoefflaWakelock.getWakelockInfo();

        CardView cardViewB = new CardView(getActivity());
        String titleB = getString(R.string.wkl_blocked);
        grxbwCardInit(cardViewB, titleB, wakelocksinfo, false);
        mWakeCard.add(cardViewB);

        CardView cardViewA = new CardView(getActivity());
        String titleA = getString(R.string.wkl_allowed);
        grxbwCardInit(cardViewA, titleA, wakelocksinfo, true);
        mWakeCard.add(cardViewA);

        items.addAll(mWakeCard);
    }


    private void grxbwCardInit(CardView card, String title, List<WakeLockInfo> wakelocksinfo, Boolean state){
        card.clearItems();
        card.setTitle(title);

        for(WakeLockInfo wakeLockInfo : wakelocksinfo){

            if(wakeLockInfo.wState == state) {

                final String name = wakeLockInfo.wName;
                String wakeup = String.valueOf(wakeLockInfo.wWakeups);
                String time = String.valueOf(wakeLockInfo.wTime / 1000);
                time = Utils.sToString(Utils.strToLong(time));

                SwitchView sw = new SwitchView();
                sw.setTitle(name);
                sw.setSummary(getString(R.string.wkl_total_time) + ": " + time + "\n" +
                        getString(R.string.wkl_wakep_count) + ": " + wakeup);
                sw.setChecked(wakeLockInfo.wState);
                sw.addOnSwitchListener((switchView, isChecked) -> {
                    if (isChecked) {
                        BoefflaWakelock.setWakelockAllowed(name, getActivity());
                    } else {
                        BoefflaWakelock.setWakelockBlocked(name, getActivity());
                    }
                    getHandler().postDelayed(this::bwCardReload, 250);
                });

                card.addItem(sw);
            }
        }
    }

    private void bwCardReload() {

        List<WakeLockInfo> wakelocksinfo = BoefflaWakelock.getWakelockInfo();

        String titleB = getString(R.string.wkl_blocked);
        grxbwCardInit(mWakeCard.get(0), titleB, wakelocksinfo, false);

        String titleA = getString(R.string.wkl_allowed);
        grxbwCardInit(mWakeCard.get(1), titleA, wakelocksinfo, true);

    }

    private void warningDialog() {

        View checkBoxView = View.inflate(getActivity(), R.layout.alertdialog_wakelock_fragment, null);
        CheckBox checkBox = checkBoxView.findViewById(R.id.chbox);
        checkBox.setChecked(true);
        checkBox.setText(getString(R.string.wkl_alert_checkbox));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked)
                -> mAlertCheckbox = isChecked);


        AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        alert.setTitle(getString(R.string.wkl_alert_title));
        alert.setMessage(getString(R.string.wkl_alert_message));
        alert.setView(checkBoxView);
        alert.setPositiveButton("OK", (dialog, id)
                -> AppSettings.saveBoolean("show_wakelock_dialog", mAlertCheckbox, getActivity()));

        alert.show();
    }

    @Override
    public void onStart(){
        super.onStart();

        boolean showDialog = AppSettings.getBoolean("show_wakelock_dialog", true, getActivity());

        if(showDialog) warningDialog();

    }
}
