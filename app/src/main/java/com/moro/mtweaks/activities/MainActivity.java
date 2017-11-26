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

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.MobileAds;
import com.moro.mtweaks.BuildConfig;
import com.moro.mtweaks.R;
import com.moro.mtweaks.database.tools.profiles.Profiles;
import com.moro.mtweaks.services.profile.Tile;
import com.moro.mtweaks.utils.Device;
import com.moro.mtweaks.utils.Prefs;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.battery.Battery;
import com.moro.mtweaks.utils.kernel.cpu.CPUBoost;
import com.moro.mtweaks.utils.kernel.cpu.CPUFreq;
import com.moro.mtweaks.utils.kernel.cpu.MSMPerformance;
import com.moro.mtweaks.utils.kernel.cpu.Temperature;
import com.moro.mtweaks.utils.kernel.cpuhotplug.Hotplug;
import com.moro.mtweaks.utils.kernel.cpuhotplug.QcomBcl;
import com.moro.mtweaks.utils.kernel.cpuvoltage.VoltageCl1;
import com.moro.mtweaks.utils.kernel.cpuvoltage.VoltageCl0;
import com.moro.mtweaks.utils.kernel.gpu.GPUFreq;
import com.moro.mtweaks.utils.kernel.io.IO;
import com.moro.mtweaks.utils.kernel.ksm.KSM;
import com.moro.mtweaks.utils.kernel.misc.Vibration;
import com.moro.mtweaks.utils.kernel.screen.Calibration;
import com.moro.mtweaks.utils.kernel.screen.Screen;
import com.moro.mtweaks.utils.kernel.sound.Sound;
import com.moro.mtweaks.utils.kernel.spectrum.Spectrum;
import com.moro.mtweaks.utils.kernel.thermal.Thermal;
import com.moro.mtweaks.utils.kernel.vm.ZSwap;
import com.moro.mtweaks.utils.kernel.wake.Wake;
import com.moro.mtweaks.utils.kernel.boefflawakelock.BoefflaWakelock;
import com.moro.mtweaks.utils.root.RootUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.fabric.sdk.android.Fabric;

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
        Boolean mIsBooted = Prefs.getBoolean("is_booted", true, this);
        if (mIsBooted) {
            // reset the Global voltages seekbar
            if (!Prefs.getBoolean("cpucl1voltage_onboot", false, this)) {
                Prefs.saveInt("CpuCl1_seekbarPref_value", 16, this);
            }
            if (!Prefs.getBoolean("cpucl0voltage_onboot", false, this)) {
                Prefs.saveInt("CpuCl0_seekbarPref_value", 16, this);
            }
            if (!Prefs.getBoolean("gpu_onboot", false, this)) {
                Prefs.saveInt("gpu_seekbarPref_value", 16, this);
            }
        }
        Prefs.saveBoolean("is_booted", false, this);

        // Check if exist /data/.mtweaks folder
        if (!Utils.existFile("/data/.mtweaks")) {
            RootUtils.runCommand("mkdir /data/.mtweaks");
        }

        // Initialice profile Sharedpreference
        int prof = Utils.strToInt(Spectrum.getProfile());
        Prefs.saveInt("spectrum_profile", prof, this);

        // Check if kernel is changed
        String kernel_old = Prefs.getString("kernel_version_old", "", this);
        String kernel_new = Device.getKernelVersion(true);

        if (!kernel_old.equals(kernel_new)){
            // Reset max limit of max_poll_percent
            Prefs.saveBoolean("max_pool_percent_saved", false, this);
            Prefs.saveBoolean("memory_pool_percent_saved", false, this);
            Prefs.saveString("kernel_version_old", kernel_new, this);

            if (mVoltageSaved != 1) {
                // Reset voltage_saved to recopy voltage stock files
                Prefs.saveBoolean("cl0_voltage_saved", false, this);
                Prefs.saveBoolean("cl1_voltage_saved", false, this);
                Prefs.saveBoolean("gpu_voltage_saved", false, this);
            }

            // Reset battery_saved to recopy battery stock values
            Prefs.saveBoolean("s7_battery_saved", false, this);

            // Reset calibration_saved to recopy screen calibration stock values
            Prefs.saveBoolean("calibration_saved", false, this);
        }

        // save battery stock values
        if (!Prefs.getBoolean("s7_battery_saved", false, this)){
            Battery.saveS7StockValues(this);
        }

        // save screen calibration stock values
        if (!Prefs.getBoolean("calibration_saved", false, this)){
            Calibration.saveCalibrationStockValues(this);
            Prefs.saveBoolean("calibration_saved", true, this);
        }

        // Save backup of Cluster0 stock voltages
        if (!Utils.existFile(VoltageCl0.BACKUP) || !Prefs.getBoolean("cl0_voltage_saved", false, this) ){
            if (VoltageCl0.supported()){
                RootUtils.runCommand("cp " + VoltageCl0.CL0_VOLTAGE + " " + VoltageCl0.BACKUP);
                Prefs.saveBoolean("cl0_voltage_saved", true, this);
            }
        }

        // Save backup of Cluster1 stock voltages
        if (!Utils.existFile(VoltageCl1.BACKUP) || !Prefs.getBoolean("cl1_voltage_saved", false, this)){
            if (VoltageCl1.supported()){
                RootUtils.runCommand("cp " + VoltageCl1.CL1_VOLTAGE + " " + VoltageCl1.BACKUP);
                Prefs.saveBoolean("cl1_voltage_saved", true, this);
            }
        }

        // Save backup of GPU stock voltages
        if (!Utils.existFile(GPUFreq.BACKUP) || !Prefs.getBoolean("gpu_voltage_saved", false, this)){
            if (GPUFreq.supported() && GPUFreq.hasVoltage()){
                RootUtils.runCommand("cp " + GPUFreq.AVAILABLE_VOLTS + " " + GPUFreq.BACKUP);
                Prefs.saveBoolean("gpu_voltage_saved", true, this);
            }
        }

        // If has MaxPoolPercent save file
        if (!Prefs.getBoolean("max_pool_percent_saved", false, this)) {
            if (ZSwap.hasMaxPoolPercent()) {
                RootUtils.runCommand("cp /sys/module/zswap/parameters/max_pool_percent /data/.mtweaks/max_pool_percent");
                Prefs.saveBoolean("max_pool_percent_saved", true, this);
            }
        }

        //Check memory pool percent unit
        if (!Prefs.getBoolean("memory_pool_percent_saved", false, this)){
            int pool = ZSwap.getMaxPoolPercent();
            if (pool >= 100) Prefs.saveBoolean("memory_pool_percent", false, this);
            if (pool < 100) Prefs.saveBoolean("memory_pool_percent", true, this);
            Prefs.saveBoolean("memory_pool_percent_saved", true, this);
        }

        // Don't initialize analytics with debug build
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        setContentView(R.layout.activity_main);

        View splashBackground = findViewById(R.id.splash_background);
        mRootAccess = (TextView) findViewById(R.id.root_access_text);
        mBusybox = (TextView) findViewById(R.id.busybox_text);
        mCollectInfo = (TextView) findViewById(R.id.info_collect_text);

        // Hide huge banner in landscape mode
        if (Utils.getOrientation(this) == Configuration.ORIENTATION_LANDSCAPE) {
            splashBackground.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            /**
             * Launch password activity when one is set,
             * otherwise run {@link CheckingTask}
             */
            String password;
            if (!(password = Prefs.getString("password", "", this)).isEmpty()) {
                Intent intent = new Intent(this, SecurityActivity.class);
                intent.putExtra(SecurityActivity.PASSWORD_INTENT, password);
                startActivityForResult(intent, 1);
            } else {
                new CheckingTask().execute();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
         * 0: License check result
         * 1: Password check result
         */
        if (requestCode == 0) {

            /*
             * -1: Default (no license check executed)
             *  0: License check was successful
             *  1: Something went wrong when checking license
             *  2: License is invalid
             *  3: Donate apk is patched/cracked
             */
            int result = data == null ? -1 : data.getIntExtra("result", -1);
            if (result == 0) {
                try {
                    ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(
                            "com.grarak.kerneladiutordonate", 0);
                    Utils.writeFile(applicationInfo.dataDir + "/license",
                            Utils.encodeString(Utils.getAndroidId(this)), false, true);
                } catch (PackageManager.NameNotFoundException ignored) {
                }
            }
            launch(result);

        } else if (requestCode == 1) {

            /*
             * 0: Password is wrong
             * 1: Password is correct
             */
            if (resultCode == 1) {
                new CheckingTask().execute();
            } else {
                finish();
            }

        }
    }

    /**
     * Launch {@link NavigationActivity} which is the actual interface
     *
     * @param code license check result see {@link #onActivityResult(int, int, Intent)}
     */
    private void launch(int code) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("result", code);
        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        Prefs.saveInt("license", code, this);
        startActivity(intent);
        finish();
    }

    private class CheckingTask extends AsyncTask<Void, Integer, Void> {

        private boolean mHasRoot;
        private boolean mHasBusybox;

        @Override
        protected Void doInBackground(Void... params) {
            // Check for root access
            mHasRoot = RootUtils.rootAccess();
            publishProgress(0);

            // If root is available continue
            if (mHasRoot) {
                // Check for busybox/toybox
                mHasBusybox = RootUtils.busyboxInstalled();
                publishProgress(1);

                // If busybox/toybox is available continue
                if (mHasBusybox) {
                    // Collect information for caching
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
            Battery.supported(MainActivity.this);
            CPUBoost.supported();

            // Assign core ctl min cpu
            CPUFreq.sCoreCtlMinCpu = Prefs.getInt("core_ctl_min_cpus_big", 2, MainActivity.this);

            Device.CPUInfo.load();
            Device.Input.supported();
            Device.MemInfo.load();
            Device.ROMInfo.load();
            Device.TrustZone.supported();
            GPUFreq.supported();
            Hotplug.supported();
            IO.supported();
            KSM.supported();
            MSMPerformance.supported();
            QcomBcl.supported();
            Screen.supported();
            Sound.supported();
            Temperature.supported(MainActivity.this);
            Thermal.supported();
            Tile.publishProfileTile(new Profiles(MainActivity.this).getAllProfiles(), MainActivity.this);
            Vibration.supported();
            VoltageCl1.supported();
            VoltageCl0.supported();
            Wake.supported();

            if (!BuildConfig.DEBUG) {
                // Send SoC type to analytics to collect stats
                Answers.getInstance().logCustom(new CustomEvent("SoC")
                        .putCustomAttribute("type", Device.getBoard()));
            }
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
            int red = ContextCompat.getColor(MainActivity.this, R.color.red);
            int green = ContextCompat.getColor(MainActivity.this, R.color.green);
            switch (values[0]) {
                case 0:
                    mRootAccess.setTextColor(mHasRoot ? green : red);
                    break;
                case 1:
                    mBusybox.setTextColor(mHasBusybox ? green : red);
                    break;
                case 2:
                    mCollectInfo.setTextColor(green);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            /*
             * If root or busybox/toybox are not available,
             * launch text activity which let the user know
             * what the problem is.
             */
            if (!mHasRoot || !mHasBusybox) {
                Intent intent = new Intent(MainActivity.this, TextActivity.class);
                intent.putExtra(TextActivity.MESSAGE_INTENT, getString(mHasRoot ?
                        R.string.no_busybox : R.string.no_root));
                intent.putExtra(TextActivity.SUMMARY_INTENT,
                        mHasRoot ? "https://play.google.com/store/apps/details?id=stericson.busybox" :
                                "https://www.google.com/search?site=&source=hp&q=root+"
                                        + Device.getVendor() + "+" + Device.getModel());
                startActivity(intent);
                finish();

                if (!BuildConfig.DEBUG) {
                    // Send problem to analytics to collect stats
                    Answers.getInstance().logCustom(new CustomEvent("Can't access")
                            .putCustomAttribute("no_found", mHasRoot ? "no busybox" : "no root"));
                }
                return;
            }

            // Initialize Google Ads
            MobileAds.initialize(MainActivity.this, "ca-app-pub-1851546461606210~9501142287");

            // Execute another AsyncTask for license checking
            new AsyncTask<Void, Void, Boolean>() {

                private ApplicationInfo mApplicationInfo;
                private PackageInfo mPackageInfo;
                private boolean mPatched;
                private boolean mInternetAvailable;
                private boolean mLicensedCached;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    try {
                        mApplicationInfo = getPackageManager().getApplicationInfo(
                                "com.grarak.kerneladiutordonate", 0);
                        mPackageInfo = getPackageManager().getPackageInfo(
                                "com.grarak.kerneladiutordonate", 0);
                        if (BuildConfig.DEBUG) {
                            Utils.DONATED = false;
                        }
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    if (mApplicationInfo != null && mPackageInfo != null
                            && mPackageInfo.versionCode == 130) {
                        try {
                            mPatched = !Utils.checkMD5("5c7a92a5b2dcec409035e1114e815b00",
                                    new File(mApplicationInfo.publicSourceDir))
                                    || Utils.isPatched(mApplicationInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (Utils.existFile(mApplicationInfo.dataDir + "/license")) {
                            String content = Utils.readFile(mApplicationInfo.dataDir + "/license");
                            if (!content.isEmpty() && (content = Utils.decodeString(content)) != null) {
                                if (content.equals(Utils.getAndroidId(MainActivity.this))) {
                                    mLicensedCached = true;
                                }
                            }
                        }

                        try {
                            if (!mLicensedCached) {
                                HttpURLConnection urlConnection = (HttpURLConnection) new URL("https://www.google.com").openConnection();
                                urlConnection.setRequestProperty("User-Agent", "Test");
                                urlConnection.setRequestProperty("Connection", "close");
                                urlConnection.setConnectTimeout(3000);
                                urlConnection.connect();
                                mInternetAvailable = urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
                            }
                        } catch (IOException ignored) {
                        }

                        return !mPatched;
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean donationValid) {
                    super.onPostExecute(donationValid);
                    if (donationValid && mLicensedCached) {
                        launch(0);
                    } else if (donationValid && mInternetAvailable) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(new ComponentName("com.grarak.kerneladiutordonate",
                                "com.grarak.kerneladiutordonate.MainActivity"));
                        startActivityForResult(intent, 0);
                    } else if (donationValid) {
                        launch(1);
                    } else {
                        if (mPatched && !BuildConfig.DEBUG) {
                            Answers.getInstance().logCustom(new CustomEvent("Pirated")
                                    .putCustomAttribute("android_id", Utils.getAndroidId(MainActivity.this)));
                        }
                        launch(mPatched ? 3 : -1);
                    }
                }
            }.execute();
        }

    }

}
