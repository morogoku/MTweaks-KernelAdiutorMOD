package com.moro.mtweaks.views.recyclerview;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.LinearLayout;

import com.moro.mtweaks.R;

/**
 * Created by Morogoku on 08.12.17.
 */
public class ImageView extends RecyclerViewItem {

    private View mRootView;
    private AppCompatImageView mImageView;
    private LinearLayout mLayoutView;

    private LinearLayout.LayoutParams mLp;
    private Drawable mImage;
    private int mGravity;
    private int mBackgroundColor;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_image_view;
    }

    @Override
    public void onCreateView(View view) {
        super.onCreateView(view);

        mRootView = view;
        mImageView = view.findViewById(R.id.image);
        mLayoutView = view.findViewById(R.id.image_layout);

        super.onCreateView(view);

    }

    public void setBackgroundColor (int color){
        mBackgroundColor = color;
        refresh();
    }

    public void setDrawable(Drawable drawable) {
        mImage = drawable;
        refresh();
    }

    public void setGravity(int gravity){
        mGravity = gravity;
        refresh();
    }

    public void setLayoutParams(int width, int height){
        mLp = new LinearLayout.LayoutParams(width, height);
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mImageView != null && mImage != null) {
            mImageView.setImageDrawable(mImage);
            if (mLp != null) {
                mImageView.setLayoutParams(mLp);
            }
            if (mLayoutView != null){
                if (mGravity != 0) {
                    mLayoutView.setGravity(mGravity);
                }
                if (mBackgroundColor != 0){
                    mLayoutView.setBackgroundColor(mBackgroundColor);
                }
            }
        }
        if (mRootView != null && getOnItemClickListener() != null) {
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getOnItemClickListener() != null) {
                        getOnItemClickListener().onClick(ImageView.this);
                    }
                }
            });
        }
    }
}
