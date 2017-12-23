/*
 * Copyright (C) 2017 Willi Ye <williye97@gmail.com>
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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.moro.mtweaks.R;
import com.moro.mtweaks.activities.MainActivity;
import com.moro.mtweaks.database.Settings;
import com.moro.mtweaks.database.tools.customcontrols.Controls;
import com.moro.mtweaks.database.tools.profiles.Profiles;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.fragments.kernel.CPUHotplugFragment;
import com.moro.mtweaks.services.profile.Tile;
import com.moro.mtweaks.utils.NotificationId;
import com.moro.mtweaks.utils.Device;
import com.moro.mtweaks.utils.Prefs;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.boefflawakelock.BoefflaWakelock;
import com.moro.mtweaks.utils.kernel.cpu.CPUFreq;
import com.moro.mtweaks.utils.kernel.cpu.MSMPerformance;
import com.moro.mtweaks.utils.kernel.cpuhotplug.CoreCtl;
import com.moro.mtweaks.utils.kernel.cpuhotplug.MPDecision;
import com.moro.mtweaks.utils.kernel.cpuvoltage.VoltageCl0;
import com.moro.mtweaks.utils.kernel.cpuvoltage.VoltageCl1;
import com.moro.mtweaks.utils.kernel.gpu.GPUFreq;
import com.moro.mtweaks.utils.root.Control;
import com.moro.mtweaks.utils.root.RootFile;
import com.moro.mtweaks.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 24.11.17.
 */

public class ApplyOnBoot {

    private static final String TAG = ApplyOnBoot.class.getSimpleName();
    private static boolean sCancel;

    public interface ApplyOnBootListener {
        void onFinish();
    }

    public static boolean apply(final Context context, final ApplyOnBootListener listener) {

        //Initialize AppUpdate check
        new AppUpdater(context)
                //.setDisplay(Display.SNACKBAR)
                //.setDisplay(Display.DIALOG)
                .setDisplay(Display.NOTIFICATION)
                .setUpdateFrom(UpdateFrom.JSON)
                .setIcon(R.drawable.logo)
                .setUpdateJSON("https://raw.githubusercontent.com/morogoku/MTweaks-KernelAdiutorMOD/master/app/update.json")
                .start();

        //Initialize Boeffla Wakelock Blocker Files
        if(BoefflaWakelock.supported()) {
            BoefflaWakelock.CopyWakelockBlockerDefault();
        }

        // Check if kernel is changed
        String kernel_old = Prefs.getString("kernel_version_old", "", context);
        String kernel_new = Device.getKernelVersion(true);
        // If is changed save voltage files
        if (!kernel_old.equals(kernel_new)) {
            // Save backup of Cluster0 stock voltages
            if (VoltageCl0.supported()) {
                RootUtils.runCommand("cp " + VoltageCl0.CL0_VOLTAGE + " " + VoltageCl0.BACKUP);
                Prefs.saveBoolean("cl0_voltage_saved", true, context);
            }
            // Save backup of Cluster1 stock voltages
            if (VoltageCl1.supported()) {
                RootUtils.runCommand("cp " + VoltageCl1.CL1_VOLTAGE + " " + VoltageCl1.BACKUP);
                Prefs.saveBoolean("cl1_voltage_saved", true, context);
            }
            // Save backup of GPU stock voltages
            if (GPUFreq.supported() && GPUFreq.hasVoltage()) {
                RootUtils.runCommand("cp " + GPUFreq.AVAILABLE_VOLTS + " " + GPUFreq.BACKUP);
                Prefs.saveBoolean("gpu_voltage_saved", true, context);
            }
            RootUtils.runCommand("setprop mtweaks.voltage_saved 1");
        }

        if (!Prefs.getBoolean(ApplyOnBootFragment.getAssignment(CPUHotplugFragment.class), false, context)) {
            Prefs.remove("core_ctl_min_cpus_big", context);
        }

        boolean enabled = false;
        final Settings settings = new Settings(context);
        Controls controls = new Controls(context);

        final HashMap<String, Boolean> mCategoryEnabled = new HashMap<>();
        final HashMap<String, String> mCustomControls = new HashMap<>();
        final List<String> mProfiles = new ArrayList<>();

        List<Profiles.ProfileItem> profiles = new Profiles(context).getAllProfiles();
        Tile.publishProfileTile(profiles, context);

        for (Settings.SettingsItem item : settings.getAllSettings()) {
            if (!mCategoryEnabled.containsKey(item.getCategory())) {
                boolean categoryEnabled = Prefs.getBoolean(item.getCategory(), false, context);
                mCategoryEnabled.put(item.getCategory(), categoryEnabled);
                if (!enabled && categoryEnabled) {
                    enabled = true;
                }
            }
        }
        for (Controls.ControlItem item : controls.getAllControls()) {
            if (item.isOnBootEnabled() && item.getArguments() != null) {
                mCustomControls.put(item.getApply(), item.getArguments());
            }
        }
        for (Profiles.ProfileItem profileItem : profiles) {
            if (profileItem.isOnBootEnabled()) {
                for (Profiles.ProfileItem.CommandItem commandItem : profileItem.getCommands()) {
                    mProfiles.add(commandItem.getCommand());
                }
            }
        }

        final boolean initdEnabled = Prefs.getBoolean("initd_onboot", false, context);
        enabled = enabled || mCustomControls.size() > 0 || mProfiles.size() > 0 || initdEnabled;
        if (!enabled) {
            return false;
        }

        final int seconds = Utils.strToInt(Prefs.getString("applyonbootdelay", "10", context));
        final boolean hideNotification = Prefs.getBoolean("applyonboothide", false, context);
        final boolean confirmationNotification = Prefs.getBoolean("applyonbootconfirmationnotification",
                true, context);
        final boolean toast = Prefs.getBoolean("applyonboottoast", false, context);
        final boolean script = Prefs.getBoolean("applyonbootscript", false, context);

        PendingIntent cancelIntent = PendingIntent.getBroadcast(context, 1,
                new Intent(context, CancelReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Intent launchIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);

        final NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, ApplyOnBootService.CHANNEL_ID);

        if (!hideNotification) {
            builder.setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.apply_on_boot_text, seconds))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .addAction(0, context.getString(R.string.cancel), cancelIntent)
                    .setOngoing(true)
                    .setWhen(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                builder.setPriority(Notification.PRIORITY_MAX);
            }
        }

        final NotificationCompat.Builder builderComplete =
                new NotificationCompat.Builder(context, ApplyOnBootService.CHANNEL_ID);
        if (!hideNotification) {
            builderComplete.setContentTitle(context.getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true);
        }

        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                sCancel = false;
                for (int i = 0; i < seconds; i++) {
                    if (!hideNotification) {
                        if (sCancel) {
                            break;
                        }
                        builder.setContentText(context.getString(R.string.apply_on_boot_text, seconds - i));
                        builder.setProgress(seconds, i, false);
                        notificationManager.notify(NotificationId.APPLY_ON_BOOT, builder.build());
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!hideNotification) {
                    notificationManager.cancel(NotificationId.APPLY_ON_BOOT);
                    if (confirmationNotification) {
                        builderComplete.setContentText(context.getString(sCancel ? R.string.apply_on_boot_canceled :
                                R.string.apply_on_boot_complete));
                        notificationManager.notify(NotificationId.APPLY_ON_BOOT_CONFIRMATION, builderComplete.build());
                    }

                    if (sCancel) {
                        sCancel = false;
                        listener.onFinish();
                        return;
                    }
                }
                RootUtils.SU su = new RootUtils.SU(true, TAG);

                if (initdEnabled) {
                    RootUtils.mount(true, "/system", su);
                    su.runCommand("for i in `ls /system/etc/init.d`;do chmod 755 $i;done");
                    su.runCommand("[ -d /system/etc/init.d ] && run-parts /system/etc/init.d");
                    RootUtils.mount(false, "/system", su);
                }

                List<String> commands = new ArrayList<>();
                for (Settings.SettingsItem item : settings.getAllSettings()) {
                    String category = item.getCategory();
                    String setting = item.getSetting();
                    String id = item.getId();
                    CPUFreq.ApplyCpu applyCpu;
                    if (mCategoryEnabled.get(category)) {
                        if (category.equals(ApplyOnBootFragment.CPU)
                                && id.contains("%d")
                                && setting.startsWith("#")
                                && ((applyCpu =
                                new CPUFreq.ApplyCpu(setting.substring(1))).toString() != null)) {
                            synchronized (this) {
                                commands.addAll(getApplyCpu(applyCpu, su, context));
                            }
                        } else {
                            commands.add(setting);
                        }
                    }
                }

                if (script) {
                    StringBuilder s = new StringBuilder("#!/system/bin/sh\n\n");
                    for (String command : commands) {
                        s.append(command).append("\n");
                    }
                    RootFile file = new RootFile("/data/local/tmp/kerneladiutortmp.sh", su);
                    file.mkdir();
                    file.write(s.toString(), false);
                    file.execute();
                } else {
                    for (String command : commands) {
                        synchronized (this) {
                            su.runCommand(command);
                        }
                    }
                }
                for (String script : mCustomControls.keySet()) {
                    RootFile file = new RootFile("/data/local/tmp/kerneladiutortmp.sh", su);
                    file.mkdir();
                    file.write(script, false);
                    file.execute(mCustomControls.get(script));
                }

                List<String> profileCommands = new ArrayList<>();
                for (String command : mProfiles) {
                    CPUFreq.ApplyCpu applyCpu;
                    if (command.startsWith("#")
                            && ((applyCpu =
                            new CPUFreq.ApplyCpu(command.substring(1))).toString() != null)) {
                        synchronized (this) {
                            profileCommands.addAll(getApplyCpu(applyCpu, su, context));
                        }
                    }
                    profileCommands.add(command);
                }

                if (script) {
                    StringBuilder s = new StringBuilder("#!/system/bin/sh\n\n");
                    for (String command : profileCommands) {
                        s.append(command).append("\n");
                    }
                    RootFile file = new RootFile("/data/local/tmp/kerneladiutortmp.sh", su);
                    file.mkdir();
                    file.write(s.toString(), false);
                    file.execute();
                } else {
                    for (String command : profileCommands) {
                        synchronized (this) {
                            su.runCommand(command);
                        }
                    }
                }

                su.close();

                if (toast) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utils.toast(R.string.apply_on_boot_complete, context);
                        }
                    });
                }

                listener.onFinish();
            }
        }).start();
        return true;
    }

    public static List<String> getApplyCpu(CPUFreq.ApplyCpu applyCpu, RootUtils.SU su) {
        return getApplyCpu(applyCpu, su, null);
    }

    private static List<String> getApplyCpu(CPUFreq.ApplyCpu applyCpu, RootUtils.SU su, Context context) {
        List<String> commands = new ArrayList<>();
        boolean cpulock = Utils.existFile(CPUFreq.CPU_LOCK_FREQ, su);
        if (cpulock) {
            commands.add(Control.write("0", CPUFreq.CPU_LOCK_FREQ));
        }
        boolean mpdecision = Utils.hasProp(MPDecision.HOTPLUG_MPDEC, su)
                && Utils.isPropRunning(MPDecision.HOTPLUG_MPDEC, su);
        if (mpdecision) {
            commands.add(Control.stopService(MPDecision.HOTPLUG_MPDEC));
        }
        for (int i = applyCpu.getMin(); i <= applyCpu.getMax(); i++) {
            boolean offline = !Utils.existFile(Utils.strFormat(applyCpu.getPath(), i), su);

            List<Integer> bigCpuRange = applyCpu.getBigCpuRange();
            List<Integer> LITTLECpuRange = applyCpu.getLITTLECpuRange();
            String coreCtlMinPath = null;
            String msmPerformanceMinPath = null;
            if (offline) {

                if (applyCpu.isBigLITTLE()) {
                    if (Utils.existFile(Utils.strFormat(CoreCtl.CORE_CTL, i), su)) {
                        coreCtlMinPath = Utils.strFormat(CoreCtl.CORE_CTL + CoreCtl.MIN_CPUS, i);
                        commands.add(Control.write(String.valueOf(bigCpuRange.size()), coreCtlMinPath));
                    }

                    if (Utils.existFile(MSMPerformance.MAX_CPUS, su)) {
                        msmPerformanceMinPath = MSMPerformance.MAX_CPUS;
                        commands.add(Control.write(LITTLECpuRange.size() + ":" + bigCpuRange.size(),
                                msmPerformanceMinPath));
                    }
                }

                commands.add(Control.write("1", Utils.strFormat(CPUFreq.CPU_ONLINE, i)));
            }
            commands.add(Control.chmod("644", Utils.strFormat(applyCpu.getPath(), i)));
            commands.add(Control.write(applyCpu.getValue(), Utils.strFormat(applyCpu.getPath(), i)));
            commands.add(Control.chmod("444", Utils.strFormat(applyCpu.getPath(), i)));
            if (offline) {

                if (coreCtlMinPath != null) {
                    commands.add(Control.write(String.valueOf(context == null ?
                            CPUFreq.sCoreCtlMinCpu : Prefs.getInt("core_ctl_min_cpus_big",
                            applyCpu.getCoreCtlMin(), context)), coreCtlMinPath));
                }
                if (msmPerformanceMinPath != null) {
                    commands.add(Control.write("-1:-1", msmPerformanceMinPath));
                }

                commands.add(Control.write("0", Utils.strFormat(CPUFreq.CPU_ONLINE, i)));
            }
        }
        if (mpdecision) {
            commands.add(Control.startService(MPDecision.HOTPLUG_MPDEC));
        }
        if (cpulock) {
            commands.add(Control.write("1", CPUFreq.CPU_LOCK_FREQ));
        }
        return commands;
    }

    public static class CancelReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            sCancel = true;
        }

    }

}
