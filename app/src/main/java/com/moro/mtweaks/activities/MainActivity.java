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
package com.moro.mtweaks.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.moro.mtweaks.BuildConfig;
import com.moro.mtweaks.R;
import com.moro.mtweaks.database.tools.profiles.Profiles;
import com.moro.mtweaks.fragments.kernel.GPUFragment;
import com.moro.mtweaks.services.profile.Tile;
import com.moro.mtweaks.utils.AppSettings;
import com.moro.mtweaks.utils.Device;
import com.moro.mtweaks.utils.Log;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.battery.Battery;
import com.moro.mtweaks.utils.kernel.cpu.CPUBoost;
import com.moro.mtweaks.utils.kernel.cpu.CPUFreq;
import com.moro.mtweaks.utils.kernel.cpu.MSMPerformance;
import com.moro.mtweaks.utils.kernel.cpu.Temperature;
import com.moro.mtweaks.utils.kernel.cpuhotplug.Hotplug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.QcomBcl;
import com.moro.mtweaks.utils.kernel.cpuvoltage.VoltageCl0;
import com.moro.mtweaks.utils.kernel.cpuvoltage.VoltageCl1;
import com.moro.mtweaks.utils.kernel.gpu.GPU;
import com.moro.mtweaks.utils.kernel.gpu.GPUFreqExynos;
import com.moro.mtweaks.utils.kernel.io.IO;
import com.moro.mtweaks.utils.kernel.ksm.KSM;
import com.moro.mtweaks.utils.kernel.misc.Vibration;
import com.moro.mtweaks.utils.kernel.screen.Screen;
import com.moro.mtweaks.utils.kernel.sound.Sound;
import com.moro.mtweaks.utils.kernel.spectrum.Spectrum;
import com.moro.mtweaks.utils.kernel.thermal.Thermal;
import com.moro.mtweaks.utils.kernel.vm.ZSwap;
import com.moro.mtweaks.utils.kernel.wake.Wake;
import com.moro.mtweaks.utils.kernel.boefflawakelock.BoefflaWakelock;
import com.moro.mtweaks.utils.root.RootUtils;

import java.lang.ref.WeakReference;

/**
 * Created by willi on 14.04.16.
 */
public class MainActivity extends BaseActivity {

    private TextView mRootAccess;
    private TextView mBusybox;
    private TextView mCollectInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize Boeffla Wakelock Blocker Files
        if(BoefflaWakelock.supported()) {
            BoefflaWakelock.CopyWakelockBlockerDefault();
        }

        // If setting is applied on boot, mAppliedOnBoot = 1
        int mAppliedOnboot = Utils.strToInt(RootUtils.getProp("mtweaks.applied_onboot"));

        // If voltages are saved on Service.java, mVoltageSaved = 1
        int mVoltageSaved = Utils.strToInt(RootUtils.getProp("mtweaks.voltage_saved"));

        // Check if system is rebooted
        Boolean mIsBooted = AppSettings.getBoolean("is_booted", true, this);
        if (mIsBooted) {
            // reset the Global voltages seekbar
            if (!AppSettings.getBoolean("cpucl1voltage_onboot", false, this)) {
                AppSettings.saveInt("CpuCl1_seekbarPref_value", GPUFragment.mDefZeroPosition, this);
            }
            if (!AppSettings.getBoolean("cpucl0voltage_onboot", false, this)) {
                AppSettings.saveInt("CpuCl0_seekbarPref_value", GPUFragment.mDefZeroPosition, this);
            }
            if (!AppSettings.getBoolean("gpu_onboot", false, this)) {
                AppSettings.saveInt("gpu_seekbarPref_value", GPUFragment.mDefZeroPosition, this);
            }
        }
        AppSettings.saveBoolean("is_booted", false, this);

        // Check if exist /data/.mtweaks folder
        if (!Utils.existFile("/data/.mtweaks")) {
            RootUtils.runCommand("mkdir /data/.mtweaks");
        }

        // Initialice profile Sharedpreference
        int prof = Utils.strToInt(Spectrum.getProfile());
        AppSettings.saveInt("spectrum_profile", prof, this);

        // Check if kernel is changed
        String kernel_old = AppSettings.getString("kernel_version_old", "", this);
        String kernel_new = Device.getKernelVersion(true);

        if (!kernel_old.equals(kernel_new)){
            // Reset max limit of max_poll_percent
            AppSettings.saveBoolean("max_pool_percent_saved", false, this);
            AppSettings.saveBoolean("memory_pool_percent_saved", false, this);
            AppSettings.saveString("kernel_version_old", kernel_new, this);

            if (mVoltageSaved != 1) {
                // Reset voltage_saved to recopy voltage stock files
                AppSettings.saveBoolean("cl0_voltage_saved", false, this);
                AppSettings.saveBoolean("cl1_voltage_saved", false, this);
                AppSettings.saveBoolean("gpu_voltage_saved", false, this);
            }

            // Reset battery_saved to recopy battery stock values
            AppSettings.saveBoolean("s7_battery_saved", false, this);
        }

        // Check if MTweaks version is changed
        String appVersionOld = AppSettings.getString("app_version_old", "", this);
        String appVersionNew = Utils.appVersion();
        AppSettings.saveBoolean("show_changelog", true, this);

        if (appVersionOld.equals(appVersionNew)){
            AppSettings.saveBoolean("show_changelog", false, this);
        } else {
            AppSettings.saveString("app_version_old", appVersionNew, this);
        }

        // save battery stock values
        if (!AppSettings.getBoolean("s7_battery_saved", false, this)){
            Battery.getInstance(this).saveS7StockValues(this);
        }

        // Save backup of Cluster0 stock voltages
        if (!Utils.existFile(VoltageCl0.BACKUP) || !AppSettings.getBoolean("cl0_voltage_saved", false, this) ){
            if (VoltageCl0.supported()){
                RootUtils.runCommand("cp " + VoltageCl0.CL0_VOLTAGE + " " + VoltageCl0.BACKUP);
                AppSettings.saveBoolean("cl0_voltage_saved", true, this);
            }
        }

        // Save backup of Cluster1 stock voltages
        if (!Utils.existFile(VoltageCl1.BACKUP) || !AppSettings.getBoolean("cl1_voltage_saved", false, this)){
            if (VoltageCl1.supported()){
                RootUtils.runCommand("cp " + VoltageCl1.CL1_VOLTAGE + " " + VoltageCl1.BACKUP);
                AppSettings.saveBoolean("cl1_voltage_saved", true, this);
            }
        }

        // Save backup of GPU stock voltages
        if (!Utils.existFile(GPUFreqExynos.BACKUP) || !AppSettings.getBoolean("gpu_voltage_saved", false, this)){
            if (GPUFreqExynos.getInstance().supported() && GPUFreqExynos.getInstance().hasVoltage()){
                RootUtils.runCommand("cp " + GPUFreqExynos.getInstance().AVAILABLE_VOLTS + " " + GPUFreqExynos.BACKUP);
                AppSettings.saveBoolean("gpu_voltage_saved", true, this);
            }
        }

        // If has MaxPoolPercent save file
        if (!AppSettings.getBoolean("max_pool_percent_saved", false, this)) {
            if (ZSwap.hasMaxPoolPercent()) {
                RootUtils.runCommand("cp /sys/module/zswap/parameters/max_pool_percent /data/.mtweaks/max_pool_percent");
                AppSettings.saveBoolean("max_pool_percent_saved", true, this);
            }
        }

        //Check memory pool percent unit
        if (!AppSettings.getBoolean("memory_pool_percent_saved", false, this)){
        int pool = ZSwap.getMaxPoolPercent();
        if (pool >= 100) AppSettings.saveBoolean("memory_pool_percent", false, this);
        if (pool < 100) AppSettings.saveBoolean("memory_pool_percent", true, this);
            AppSettings.saveBoolean("memory_pool_percent_saved", true, this);
        }

        setContentView(R.layout.activity_main);

        View splashBackground = findViewById(R.id.splash_background);
        mRootAccess = findViewById(R.id.root_access_text);
        mBusybox = findViewById(R.id.busybox_text);
        mCollectInfo = findViewById(R.id.info_collect_text);

        // Hide huge banner in landscape mode
        if (Utils.getOrientation(this) == Configuration.ORIENTATION_LANDSCAPE) {
            splashBackground.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            /*
             * Launch password activity when one is set,
             * otherwise run {@link #CheckingTask}
             */
            String password;
            if (!(password = AppSettings.getPassword(this)).isEmpty()) {
                Intent intent = new Intent(this, SecurityActivity.class);
                intent.putExtra(SecurityActivity.PASSWORD_INTENT, password);
                startActivityForResult(intent, 1);
            } else {
                new CheckingTask(this).execute();
            }
        }
    }

    private void launch() {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private static class CheckingTask extends AsyncTask<Void, Integer, Void> {

        private WeakReference<MainActivity> mRefActivity;

        private boolean mHasRoot;
        private boolean mHasBusybox;

        private CheckingTask(MainActivity activity) {
            mRefActivity = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mHasRoot = RootUtils.rootAccess();
            publishProgress(0);

            if (mHasRoot) {
                mHasBusybox = RootUtils.busyboxInstalled();
                publishProgress(1);

                if (mHasBusybox) {
                    collectData();
                    publishProgress(2);
                }
            }
            return null;
        }

        /**
         * Determinate what sections are supported
         */
        private void collectData() {
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            Battery.getInstance(activity);
            CPUBoost.getInstance();

            // Assign core ctl min cpu
            CPUFreq.getInstance(activity);

            Device.CPUInfo.getInstance();
            Device.Input.getInstance();
            Device.MemInfo.getInstance();
            Device.ROMInfo.getInstance();
            Device.TrustZone.getInstance();
            GPU.supported();
            Hotplug.supported();
            IO.getInstance();
            KSM.getInstance();
            MSMPerformance.getInstance();
            QcomBcl.supported();
            Screen.supported();
            Sound.getInstance();
            Temperature.getInstance(activity);
            Thermal.supported();
            Tile.publishProfileTile(new Profiles(activity).getAllProfiles(), activity);
            Vibration.getInstance();
            VoltageCl0.supported();
            VoltageCl1.supported();
            Wake.supported();

            if (!BuildConfig.DEBUG) {
                // Send SoC type to analytics to collect stats
                Answers.getInstance().logCustom(new CustomEvent("SoC")
                        .putCustomAttribute("type", Device.getBoard()));
            }

            Log.crashlyticsI("Build Display ID: "
                    + Device.getBuildDisplayId());
            Log.crashlyticsI("ROM: "
                    + Device.ROMInfo.getInstance().getVersion());
            Log.crashlyticsI("Kernel version: "
                    + Device.getKernelVersion(true));
            Log.crashlyticsI("Board: " +
                    Device.getBoard());
        }

        /**
         * Let the user know what we are doing right now
         *
         * @param values progress
         *               0: Checking root
         *               1: Checking busybox/toybox
         *               2: Collecting information
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            int red = ContextCompat.getColor(activity, R.color.red);
            int green = ContextCompat.getColor(activity, R.color.green);
            switch (values[0]) {
                case 0:
                    activity.mRootAccess.setTextColor(mHasRoot ? green : red);
                    break;
                case 1:
                    activity.mBusybox.setTextColor(mHasBusybox ? green : red);
                    break;
                case 2:
                    activity.mCollectInfo.setTextColor(green);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            /*
             * If root or busybox/toybox are not available,
             * launch text activity which let the user know
             * what the problem is.
             */
            if (!mHasRoot || !mHasBusybox) {
                Intent intent = new Intent(activity, TextActivity.class);
                intent.putExtra(TextActivity.MESSAGE_INTENT, activity.getString(mHasRoot ?
                        R.string.no_busybox : R.string.no_root));
                intent.putExtra(TextActivity.SUMMARY_INTENT,
                        mHasRoot ? "https://play.google.com/store/apps/details?id=stericson.busybox" :
                                "https://www.google.com/search?site=&source=hp&q=root+"
                                        + Device.getVendor() + "+" + Device.getModel());
                activity.startActivity(intent);
                activity.finish();

                if (!BuildConfig.DEBUG) {
                    // Send problem to analytics to collect stats
                    Answers.getInstance().logCustom(new CustomEvent("Can't access")
                            .putCustomAttribute("no_found", mHasRoot ? "no busybox" : "no root"));
                }
                return;
            }

            activity.launch();
        }
    }

}
