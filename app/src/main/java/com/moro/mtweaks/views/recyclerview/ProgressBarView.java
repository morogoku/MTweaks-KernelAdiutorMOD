package com.moro.mtweaks.views.recyclerview;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.moro.mtweaks.R;

/**
 * Created by MoroGoku on 27/09/2017.
 */

public class ProgressBarView extends RecyclerViewItem {

    private RoundCornerProgressBar mProgressBar;
    private AppCompatTextView mTitle;
    private AppCompatTextView mTotal;
    private AppCompatTextView mUsed;
    private AppCompatTextView mUsedLabel;
    private AppCompatTextView mFree;
    private AppCompatTextView mFreeLabel;
    private AppCompatTextView mPercent;

    private String mTitleText;
    private String mTotalText;
    private String mUsedText;
    private String mFreeText;
    private String mPercentText;

    private long mProgress;
    private int mMax = 100;
    private int mPadding = 0;
    private int mRadius = 10;
    private int mColorBackground;
    private int mColorProgress;
    private String mUnit;
    private boolean mShowUsed = true;
    private boolean mShowFree = true;
    private boolean mShowPercent = true;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_progressbar_view;
    }

    @Override
    public void onCreateView(final View view) {

        mProgressBar = (RoundCornerProgressBar) view.findViewById(R.id.progressbar);
        mTitle = (AppCompatTextView) view.findViewById(R.id.title);
        mTotal = (AppCompatTextView) view.findViewById(R.id.total);
        mUsed = (AppCompatTextView) view.findViewById(R.id.used);
        mUsedLabel = (AppCompatTextView) view.findViewById(R.id.used_label);
        mFree = (AppCompatTextView) view.findViewById(R.id.free);
        mFreeLabel = (AppCompatTextView) view.findViewById(R.id.free_label);
        mPercent = (AppCompatTextView) view.findViewById(R.id.percent);

        super.onCreateView(view);
    }

    public void setTitle(String text) {
        mTitleText = text;
        refresh();
    }

    public void setTotal(String text) {
        mTotalText = text;
        refresh();
    }

    public void setUsed(String text) {
        mUsedText = text;
        refresh();
    }

    public void setFree(String text) {
        mFreeText = text;
        refresh();
    }

    public void setProgress(long progress) {
        mProgress = progress;
        mPercentText = String.valueOf(mProgress) + " %";
        refresh();
    }

    public void setMax(int max) {
        mMax = max;
        refresh();
    }

    public void setUnit(String text) {
        mUnit = text;
        refresh();
    }

    public void setItems(long total, long progress) {
        try {
            mProgress = (progress * 100) / total;
            mTotalText = String.valueOf(total);
            mUsedText = String.valueOf(progress);
            mFreeText = String.valueOf(total - progress);
            mPercentText = String.valueOf(mProgress) + " %";
            refresh();
        }catch (Exception ignored) {
        }
    }

    public void showUsedLabel(boolean bool) {
        mShowUsed = bool;
        refresh();
    }

    public void showFreeLabel(boolean bool) {
        mShowFree = bool;
        refresh();
    }

    public void showPercent(boolean bool){
        mShowPercent = bool;
        refresh();
    }

    public void setPadding(int padding) {
        mPadding = padding;
        refresh();
    }

    public void setRadius(int radius) {
        mRadius = radius;
        refresh();
    }

    public void setProgressBackgroundColor(int colorBackground) {
        mColorBackground = colorBackground;
        refresh();
    }

    public void setProgressColor(int colorProgress) {
        mColorProgress = colorProgress;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitle != null){
            if (mTitleText != null) {
                mTitle.setText(mTitleText);
            }
        }
        if (mTotal != null && mTotalText != null) {
            String text = mTotalText;
            if (mUnit != null) text += " " + mUnit;
            mTotal.setText(text);
        }
        if (mUsed != null && mUsedText != null) {
            String text = mUsedText;
            if (mUnit != null) text += " " + mUnit;
            if (mShowUsed){
                text += " ";
                mUsedLabel.setVisibility(View.VISIBLE);
            }else {
                mUsedLabel.setVisibility(View.GONE);
            }
            mUsed.setText(text);
        }
        if (mFree != null && mFreeText != null) {
            String text = mFreeText;
            if (mUnit != null) text += " " + mUnit;
            if (mShowFree){
                text += " ";
                mFreeLabel.setVisibility(View.VISIBLE);
            }else {
                mFreeLabel.setVisibility(View.GONE);
            }
            mFree.setText(text);
        }
        if (mProgressBar != null) {
            mProgressBar.setProgress(mProgress);
            mProgressBar.setMax(mMax);
            mProgressBar.setPadding(mPadding);
            mProgressBar.setRadius(mRadius);
            if (mColorProgress != 0) mProgressBar.setProgressColor(mColorProgress);
            if (mColorBackground != 0) mProgressBar.setProgressBackgroundColor(mColorBackground);
            if (mPercent != null) {
                if(mPercentText != null){
                    mPercent.setText(mPercentText);
                    if(mShowPercent){
                        mPercent.setVisibility(View.VISIBLE);
                    }else {
                        mPercent.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
}
