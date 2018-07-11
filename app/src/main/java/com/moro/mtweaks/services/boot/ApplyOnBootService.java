/*
 * Copyright (C) 2015-2017 Willi Ye <williye97@gmail.com>
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
package com.moro.mtweaks.services.boot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.moro.mtweaks.R;
import com.moro.mtweaks.utils.AppSettings;
import com.moro.mtweaks.utils.AppUpdaterTask;
import com.moro.mtweaks.utils.Device;
import com.moro.mtweaks.utils.NotificationId;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.cpuvoltage.VoltageCl0;
import com.moro.mtweaks.utils.kernel.cpuvoltage.VoltageCl1;
import com.moro.mtweaks.utils.kernel.gpu.GPUFreqExynos;
import com.moro.mtweaks.utils.kernel.boefflawakelock.BoefflaWakelock;
import com.moro.mtweaks.utils.root.RootUtils;

/**
 * Created by willi on 03.05.16.
 */
public class ApplyOnBootService extends Service {

    static final String CHANNEL_ID = "onboot_notification_channel";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.apply_on_boot), NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification.Builder builder = new Notification.Builder(
                    this, CHANNEL_ID);
            builder.setContentTitle(getString(R.string.apply_on_boot))
                    .setSmallIcon(R.mipmap.ic_launcher);
            startForeground(NotificationId.APPLY_ON_BOOT, builder.build());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Initialize AppUpdate check
        AppUpdaterTask.appCheckNotification(this);

        //Initialize Boeffla Wakelock Blocker Files
        if(BoefflaWakelock.supported()) {
            BoefflaWakelock.CopyWakelockBlockerDefault();
        }

        // Check if kernel is changed
        String kernel_old = AppSettings.getString("kernel_version_old", "", this);
        String kernel_new = Device.getKernelVersion(true);
        // If is changed save voltage files
        if (!kernel_old.equals(kernel_new)) {
            // Save backup of Cluster0 stock voltages
            if (VoltageCl0.supported()) {
                RootUtils.runCommand("cp " + VoltageCl0.CL0_VOLTAGE + " " + VoltageCl0.BACKUP);
                AppSettings.saveBoolean("cl0_voltage_saved", true, this);
            }
            // Save backup of Cluster1 stock voltages
            if (VoltageCl1.supported()) {
                RootUtils.runCommand("cp " + VoltageCl1.CL1_VOLTAGE + " " + VoltageCl1.BACKUP);
                AppSettings.saveBoolean("cl1_voltage_saved", true, this);
            }
            // Save backup of GPU stock voltages
            if (GPUFreqExynos.getInstance().supported() && GPUFreqExynos.getInstance().hasVoltage()) {
                RootUtils.runCommand("cp " + GPUFreqExynos.getInstance().AVAILABLE_VOLTS + " " + GPUFreqExynos.BACKUP);
                AppSettings.saveBoolean("gpu_voltage_saved", true, this);
            }
            RootUtils.runCommand("setprop mtweaks.voltage_saved 1");
        }

        Messenger messenger = null;
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                messenger = (Messenger) extras.get("messenger");
            }
        }

        if (messenger == null) {
            Utils.setupStartActivity(this);
        }

        boolean applyOnBoot = ApplyOnBoot.apply(this, this::stopSelf);

        if (!applyOnBoot) {
            if (messenger != null) {
                try {
                    Message message = Message.obtain();
                    message.arg1 = 1;
                    messenger.send(message);
                } catch (RemoteException ignored) {
                }
            }
            stopSelf();
        }
        return START_NOT_STICKY;
    }

}
