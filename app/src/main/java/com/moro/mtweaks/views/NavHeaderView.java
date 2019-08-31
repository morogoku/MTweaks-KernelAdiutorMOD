/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.moro.mtweaks.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moro.mtweaks.R;
import com.moro.mtweaks.utils.Device;
import com.moro.mtweaks.utils.Log;
import com.moro.mtweaks.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by willi on 28.12.15.
 */
public class NavHeaderView extends LinearLayout {

    private String mKernelImage;

    public NavHeaderView(Context context) {
        this(context, null);
    }

    public NavHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavHeaderView(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        String kernelVersion = Device.getKernelVersion(false);
        // Read kernel.json
        try {
            JSONArray tempArray = new JSONArray(Utils.readAssetFile(context, "kernel.json"));
            for (int i = 0; i < tempArray.length(); i++) {
                JSONObject kernel = tempArray.getJSONObject(i);
                if (kernelVersion.contains(kernel.getString("name"))) {
                    mKernelImage = kernel.getString("image");
                    break;
                }
            }
        } catch (JSONException ignored) {
            Log.e("Can't read kernel.json");
        }


        LayoutInflater.from(context).inflate(R.layout.nav_header_view, this);
        ImageView navPic = findViewById(R.id.nav_header_pic);

        if (mKernelImage != null){
            int id = getResources().getIdentifier(mKernelImage, "drawable", context.getPackageName());
            Drawable drawable = context.getDrawable(id);
            navPic.setImageDrawable(drawable);
        } else {
            navPic.setImageResource(R.drawable.logo);
        }
    }
}
