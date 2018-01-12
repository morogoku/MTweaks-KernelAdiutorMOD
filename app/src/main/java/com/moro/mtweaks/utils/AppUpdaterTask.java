package com.moro.mtweaks.utils;

import android.content.Context;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.moro.mtweaks.R;

/**
 * Created by Morogoku on 12/01/2018.
 */

public class AppUpdaterTask {

    public static void appCheckNotification(Context context){
        if (Prefs.getBoolean("show_update_notif", true, context)) {
            new AppUpdater(context)
                    .setDisplay(Display.NOTIFICATION)
                    .setUpdateFrom(UpdateFrom.JSON)
                    .setIcon(R.drawable.logo)
                    .setUpdateJSON(context.getString(R.string.appupdater_json))
                    .start();
        }
    }

    public static void appCheckDialog(Context context){
        if (Prefs.getBoolean("show_update_notif", true, context)) {
            new AppUpdater(context)
                    .setDisplay(Display.DIALOG)
                    .setUpdateFrom(UpdateFrom.JSON)
                    .setUpdateJSON(context.getString(R.string.appupdater_json))
                    .start();
        }
    }

    public static void appCheckDialogAllways(Context context){
            new AppUpdater(context)
                    .setDisplay(Display.DIALOG)
                    .setUpdateFrom(UpdateFrom.JSON)
                    .setUpdateJSON(context.getString(R.string.appupdater_json))
                    .showAppUpdated(true)
                    .start();
    }
}
