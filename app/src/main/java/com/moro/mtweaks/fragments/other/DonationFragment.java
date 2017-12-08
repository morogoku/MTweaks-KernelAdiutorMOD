package com.moro.mtweaks.fragments.other;

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.RecyclerViewFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.views.recyclerview.DescriptionView;
import com.moro.mtweaks.views.recyclerview.ImageView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.TitleView;

import java.util.List;
import java.util.Locale;

/**
 * Created by Morogoku on 05/12/2017.
 */

public class DonationFragment extends RecyclerViewFragment {


    @Override
    protected boolean showViewPager() {
        return false;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        TitleView title = new TitleView();
        title.setText(getString(R.string.donation_title));

        items.add(title);


        DescriptionView desc = new DescriptionView();
        desc.setDrawable(getResources().getDrawable(R.drawable.logo));
        desc.setSummary(getString(R.string.donation_summary));
        desc.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Utils.launchUrl("https://www.paypal.me/morogoku", getActivity());
            }
        });

        items.add(desc);


        String leng = Locale.getDefault().getLanguage();

        ImageView img = new ImageView();
        if(leng.contains("es")){
            img.setDrawable(getResources().getDrawable(R.drawable.ic_donar_paypal));
        }else {
            img.setDrawable(getResources().getDrawable(R.drawable.ic_donate_paypal));
        }
        img.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                Utils.launchUrl("https://www.paypal.me/morogoku", getActivity());
            }
        });

        items.add(img);

    }
}
