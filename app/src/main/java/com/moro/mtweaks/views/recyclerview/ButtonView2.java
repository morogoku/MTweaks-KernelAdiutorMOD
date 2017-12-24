package com.moro.mtweaks.views.recyclerview;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.moro.mtweaks.R;

/**
 * Created by Morogoku on 16/12/2017.
 */

public class ButtonView2 extends RecyclerViewItem {

    private AppCompatButton mButton;
    private AppCompatTextView mTitle;
    private AppCompatTextView mSummary;

    private CharSequence mTitleText;
    private CharSequence mSummaryText;
    private CharSequence mButtonText;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_button_view;
    }

    @Override
    public void onCreateView(View view) {
        mTitle = view.findViewById(R.id.title);
        mSummary = view.findViewById(R.id.summary);
        mButton = view.findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onClick(ButtonView2.this);
                }
            }
        });

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummaryText = summary;
        refresh();
    }

    public void setButtonText(CharSequence text) {
        mButtonText = text;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mButton != null){
            if (mTitle != null) {
                if (mTitleText != null) {
                    mTitle.setText(mTitleText);
                    mTitle.setVisibility(View.VISIBLE);
                } else {
                    mTitle.setVisibility(View.GONE);
                }
            }
            if (mSummary != null) {
                if (mSummaryText != null) {
                    mSummary.setText(mSummaryText);
                    mSummary.setVisibility(View.VISIBLE);
                } else {
                    mSummary.setVisibility(View.GONE);
                }
            }
            if (mButtonText != null){
                mButton.setText(mButtonText);
            }
        }
    }
}
