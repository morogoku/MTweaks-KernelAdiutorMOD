package com.moro.mtweaks.views.recyclerview;

import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.moro.mtweaks.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morogoku on 05/11/2017.
 */

public class CheckBoxView extends RecyclerViewItem {

    public interface OnCheckboxListener {
        void onChanged(CheckBoxView checkboxView, boolean isChecked);
    }

    private AppCompatTextView mTitle;
    private AppCompatTextView mSummary;
    private CheckBox mCheckbox;

    private CharSequence mTitleText;
    private CharSequence mSummaryText;
    private CharSequence mSummaryOnText;
    private CharSequence mSummaryOffText;
    private boolean mChecked;
    private boolean mEnabled = true;
    private float mAlpha = 1f;
    private View mView;

    private List<OnCheckboxListener> mOnCheckboxListeners = new ArrayList<>();

    @Override
    public int getLayoutRes() {
        return R.layout.rv_checkbox_view;
    }

    @Override
    public void onCreateView(View view) {
        mView = view;
        mTitle = mView.findViewById(R.id.title);
        mSummary = mView.findViewById(R.id.summary);
        mCheckbox = mView.findViewById(R.id.chbox);

        super.onCreateView(mView);

        mView.setEnabled(mEnabled);

        mView.setOnClickListener(v -> {
            mCheckbox.setChecked(!mChecked);
            if (mSummary != null && mSummaryOnText != null && mSummaryOffText != null) {
                refresh();
            }
        });


        mCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mChecked = isChecked;
            List<OnCheckboxListener> applied = new ArrayList<>();
            for (OnCheckboxListener OnCheckboxListener : mOnCheckboxListeners) {
                if (applied.indexOf(OnCheckboxListener) == -1) {
                    OnCheckboxListener.onChanged(CheckBoxView.this, isChecked);
                    applied.add(OnCheckboxListener);
                }
            }
        });
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummaryText = summary;
        refresh();
    }

    public void setSummaryOn(CharSequence summary) {
        mSummaryOnText = summary;
        refresh();
    }

    public void setSummaryOff(CharSequence summary) {
        mSummaryOffText = summary;
        refresh();
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        refresh();
    }

    public void setEnabled(boolean enable) {
        mEnabled = enable;
        refresh();
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
        refresh();
    }

    public CharSequence getTitle() {
        return mTitleText;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void addOnCheckboxListener(OnCheckboxListener OnCheckboxListener) {
        mOnCheckboxListeners.add(OnCheckboxListener);
    }

    public void clearOnCheckboxListener() {
        mOnCheckboxListeners.clear();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitle != null) {
            if (mTitleText != null) {
                mTitle.setText(mTitleText);
                mTitle.setVisibility(View.VISIBLE);
            } else {
                mTitle.setVisibility(View.GONE);
            }
        }
        if (mSummary != null && mSummaryText != null) {
            mSummary.setText(mSummaryText);
        }
        if (mSummary != null && mSummaryOnText != null && mSummaryOffText != null){
            if (mChecked) {
                mSummary.setText(mSummaryOnText);
            }else {
                mSummary.setText(mSummaryOffText);
            }
        }
        if (mView != null) mView.setEnabled(mEnabled);
        if (mCheckbox != null) {
            mCheckbox.setChecked(mChecked);
            mCheckbox.setEnabled(mEnabled);
            mCheckbox.setAlpha(mAlpha);
        }
    }




}
