package com.moro.mtweaks.fragments.kernel;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.fragments.RecyclerViewFragment;
import com.moro.mtweaks.utils.Prefs;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.boefflawakelock.BoefflaWakelock;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.DescriptionView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SelectView;
import com.moro.mtweaks.views.recyclerview.SwitchView;
import com.moro.mtweaks.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private void warningDialog() {

        View checkBoxView = View.inflate(getActivity(), R.layout.alertdialog_wakelock_fragment, null);
        CheckBox checkBox = checkBoxView.findViewById(R.id.chbox);
        checkBox.setChecked(true);
        checkBox.setText(getString(R.string.wkl_alert_checkbox));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAlertCheckbox = isChecked;
            }
        });


        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getString(R.string.wkl_alert_title));
        alert.setMessage(getString(R.string.wkl_alert_message));
        alert.setView(checkBoxView);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Prefs.saveBoolean("show_wakelock_dialog", mAlertCheckbox, getActivity());
            }
        });

        alert.show();
    }

    @Override
    public void onStart(){
        super.onStart();

        boolean showDialog = Prefs.getBoolean("show_wakelock_dialog", true, getActivity());

        if(showDialog) warningDialog();

    }
}
