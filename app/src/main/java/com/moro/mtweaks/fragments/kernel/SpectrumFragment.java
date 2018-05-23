package com.moro.mtweaks.fragments.kernel;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.DescriptionFragment;
import com.moro.mtweaks.fragments.recyclerview.RecyclerViewFragment;
import com.moro.mtweaks.utils.AppSettings;
import com.moro.mtweaks.utils.kernel.spectrum.Spectrum;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.DescriptionView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;

import java.util.List;

/**
 * Created by Morogoku on 28/07/2017.
 */

public class SpectrumFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.spec_title), getString(R.string.spec_info)));

    }

    private CardView oldCard;
    private DescriptionView oldDesc;


    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        final int balColor = ContextCompat.getColor(getContext(), R.color.colorBalance);
        final int perColor = ContextCompat.getColor(getContext(), R.color.colorPerformance);
        final int batColor = ContextCompat.getColor(getContext(), R.color.colorBattery);
        final int gamColor = ContextCompat.getColor(getContext(), R.color.colorGaming);


        //CardView Balanced
        final CardView card0 = new CardView(getActivity());
        card0.setTitle(getString(R.string.spec_balanced));
        card0.setExpandable(false);

        final DescriptionView desc0 = new DescriptionView();
        desc0.setSummary(getString(R.string.spec_balanced_summary));
        desc0.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_balanced));

        card0.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card0, desc0, 0, balColor);
            }
        });

        card0.addItem(desc0);
        items.add(card0);


        //CardView Performance
        final CardView card1 = new CardView(getActivity());
        card1.setTitle(getString(R.string.spec_performance));
        card1.setExpandable(false);

        final DescriptionView desc1 = new DescriptionView();
        desc1.setSummary(getString(R.string.spec_performance_summary));
        desc1.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_performance));

        card1.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card1, desc1, 1, perColor);
            }

        });

        card1.addItem(desc1);
        items.add(card1);


        //CardView Battery
        final CardView card2 = new CardView(getActivity());
        card2.setTitle(getString(R.string.spec_battery));
        card2.setExpandable(false);

        final DescriptionView desc2 = new DescriptionView();
        desc2.setSummary(getString(R.string.spec_battery_summary));
        desc2.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_battery));

        card2.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card2, desc2, 2, batColor);
            }

        });

        card2.addItem(desc2);
        items.add(card2);


        //CardView Gaming
        final CardView card3 = new CardView(getActivity());
        card3.setTitle(getString(R.string.spec_gaming));
        card3.setExpandable(false);

        final DescriptionView desc3 = new DescriptionView();
        desc3.setSummary(getString(R.string.spec_gaming_summary));
        desc3.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_spectrum_game));

        card3.setOnItemClickListener(new CardView.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                cardClick(card3, desc3, 3, gamColor);
            }

        });

        card3.addItem(desc3);
        items.add(card3);


        //Detects the selected profile on launch
        int mProfile = AppSettings.getInt("spectrum_profile", 0, getActivity());

        if(mProfile == 0){
            card0.GrxSetInitSelection(true, balColor);
            desc0.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card0;
            oldDesc = desc0;
        } else if(mProfile == 1){
            card1.GrxSetInitSelection(true, perColor);
            desc1.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card1;
            oldDesc = desc1;
        } else if(mProfile == 2){
            card2.GrxSetInitSelection(true, batColor);
            desc2.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card2;
            oldDesc = desc2;
        } else if(mProfile == 3){
            card3.GrxSetInitSelection(true, gamColor);
            desc3.GrxSetInitSelection(true, Color.WHITE);
            oldCard = card3;
            oldDesc = desc3;
        }

    }

    // Method that completes card onClick tasks
    private void cardClick(CardView card, DescriptionView desc, int prof, int color) {
        if (oldCard != card && oldDesc != desc) {
            ColorStateList ogColor = card.getCardBackgroundColor();
            ColorStateList odColor = desc.getTextColors();
            card.setCardBackgroundColor(color);
            desc.setTextColor(Color.WHITE);
            if(oldCard != null) oldCard.setCardBackgroundColor(ogColor);
            if(oldDesc != null) oldDesc.setTextColor(odColor);
            Spectrum.setProfile(prof);
            oldCard = card;
            oldDesc = desc;
            AppSettings.saveInt("spectrum_profile", prof, getActivity());
        }
    }
}
