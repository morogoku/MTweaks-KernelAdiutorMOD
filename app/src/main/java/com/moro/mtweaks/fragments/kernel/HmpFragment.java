package com.moro.mtweaks.fragments.kernel;

import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.fragments.RecyclerViewFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.hmp.Hmp;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.DescriptionView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SeekBarView;
import com.moro.mtweaks.views.recyclerview.TitleView;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by MoroGoku on 10/10/2017.
 */

public class HmpFragment  extends RecyclerViewFragment {

    private final LinkedHashMap<Integer, String> sProfiles = new LinkedHashMap<>();
    private SeekBarView mUpThreshold;
    private SeekBarView mDownThreshold;

    @Override
    protected void init() {
        super.init();

        sProfiles.clear();
        sProfiles.put(R.string.stock, "524 214");
        sProfiles.put(R.string.battery, "700 256");
        sProfiles.put(R.string.performance, "430 150");

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        CardView card = new CardView(getActivity());
        card.setTitle(getString(R.string.hmp_long));

        DescriptionView hmp = new DescriptionView();
        hmp.setSummary(getString(R.string.hmp_desc));
        card.addItem(hmp);

        if(Hmp.hasUpThreshold()) {
            mUpThreshold = new SeekBarView();
            mUpThreshold.setTitle(getString(R.string.hmp_up_threshold));
            mUpThreshold.setSummary(getString(R.string.hmp_up_threshold_summary));
            mUpThreshold.setMax(1024);
            mUpThreshold.setMin(1);
            mUpThreshold.setProgress(Utils.strToInt(Hmp.getUpThreshold()) -1);
            mUpThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Hmp.setUpThreshold((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            card.addItem(mUpThreshold);
        }

        if(Hmp.hasDownThreshold()){
            mDownThreshold = new SeekBarView();
            mDownThreshold.setTitle(getString(R.string.hmp_down_threshold));
            mDownThreshold.setSummary(getString(R.string.hmp_down_threshold_summary));
            mDownThreshold.setMax(1024);
            mDownThreshold.setMin(1);
            mDownThreshold.setProgress(Utils.strToInt(Hmp.getDownThreshold()) -1);
            mDownThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Hmp.setDownThreshold((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            card.addItem(mDownThreshold);
        }

        if (card.size() > 0) {
            items.add(card);
        }

        if(Hmp.hasUpThreshold() && Hmp.hasDownThreshold()){

            TitleView profilesTitle = new TitleView();
            profilesTitle.setText(getString(R.string.profile));
            items.add(profilesTitle);

            for (int id : sProfiles.keySet()) {
                DescriptionView profile = new DescriptionView();
                profile.setTitle(getString(id));
                profile.setSummary(sProfiles.get(id));
                profile.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                    @Override
                    public void onClick(RecyclerViewItem item) {
                        Hmp.setHmpProfile(((DescriptionView) item).getSummary().toString(), getActivity());
                        refreshHmpProfile();
                    }
                });

                items.add(profile);
            }
        }
    }

    private void refreshHmpProfile() {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mUpThreshold.setProgress(Utils.strToInt(Hmp.getUpThreshold()) -1);
                mDownThreshold.setProgress(Utils.strToInt(Hmp.getDownThreshold()) -1);
            }
        }, 250);
    }
}
