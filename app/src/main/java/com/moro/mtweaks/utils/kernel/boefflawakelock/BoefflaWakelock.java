package com.moro.mtweaks.utils.kernel.boefflawakelock;

import android.content.Context;

import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;
import com.moro.mtweaks.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Morogoku on 27/10/2017.
 *
 * Wakelocks array items
 * 0 name
 * 1 active_count
 * 2 event_count
 * 3 wakeup_count
 * 4 expire_count
 * 5 active_since
 * 6 total_time
 * 7 max_time
 * 8 last_change
 * 9 prevent_suspend_time
 * 10 time_while_screen_off
 *
 */

public class BoefflaWakelock {

    private static final String PARENT = "/sys/devices/virtual/misc/boeffla_wakelock_blocker";
    private static final String VERSION = PARENT + "/version";
    //private static final String DEBUG = PARENT + "/debug";
    private static final String WAKELOCK_BLOCKER = PARENT + "/wakelock_blocker";
    private static final String WAKELOCK_BLOCKER_DEFAULT = PARENT + "/wakelock_blocker_default";
    private static final String WAKELOCK_SOURCES = "/sys/kernel/debug/wakeup_sources";

    // Wakelocks Order: 0-Name, 1-Time, 2-Wakeups
    private static int mWakelockOrder = 1;


    public static String getVersion(){
        return Utils.readFile(VERSION);
    }

    public static void CopyWakelockBlockerDefault(){
        try {
            String wbd = Utils.readFile(WAKELOCK_BLOCKER_DEFAULT);
            if (!wbd.contentEquals("")) {
                String list = "";
                try {
                    list = Utils.readFile(WAKELOCK_BLOCKER);
                    if (list.contentEquals("")) {
                        list = wbd;
                    } else {
                        list = list + ";" + wbd;
                    }
                } catch (Exception ignored) {
                }

                RootUtils.runCommand("echo '" + list + "' > " + WAKELOCK_BLOCKER);
                RootUtils.runCommand("echo '" + "" + "' > " + WAKELOCK_BLOCKER_DEFAULT);
            }
        }catch(Exception ignored){
        }
    }

    public static void setWakelockBlocked(String wakelock, Context context){
        String list = "";
        try {
            list = Utils.readFile(WAKELOCK_BLOCKER);
            if (list.contentEquals("")) {
                list = wakelock;
            } else {
                list += ";" + wakelock;
            }
        } catch (Exception ignored){
        }

        run(Control.write(list, WAKELOCK_BLOCKER), WAKELOCK_BLOCKER, context);
    }

    public static void setWakelockAllowed(String wakelock, Context context){
        StringBuilder list = new StringBuilder();
        try {
            String[] wakes = Utils.readFile(WAKELOCK_BLOCKER).split(";");
            for(String wake : wakes){
                if(!wake.contentEquals(wakelock)){
                    if (list.toString().contentEquals("")) {
                        list = new StringBuilder(wake);
                    } else {
                        list.append(";").append(wake);
                    }
                }
            }
        } catch (Exception ignored){
        }

        run(Control.write(list.toString(), WAKELOCK_BLOCKER), WAKELOCK_BLOCKER, context);
    }

    public static int getWakelockOrder(){
        return mWakelockOrder;
    }

    public static void setWakelockOrder(int order){
        mWakelockOrder = order;
    }

    public static List<WakeLockInfo> getWakelockInfo(){

        List<WakeLockInfo> wakelocksinfo = new ArrayList<>();

        try {
            String[] lines = Utils.readFile(WAKELOCK_SOURCES).split("\\r?\\n");
            for (String line : lines) {
                if (!line.startsWith("name")) {
                    String[] wl = line.split("\\s+");
                    wakelocksinfo.add(new WakeLockInfo(wl[0], Integer.valueOf(wl[6]), Integer.valueOf(wl[3])));
                }
            }
        }catch (Exception ignored) {
        }


        String[] blocked = null;

        try {
            blocked = Utils.readFile(WAKELOCK_BLOCKER).split(";");
        }catch (Exception ignored){
        }

        if( blocked != null){
            for (String name_bloqued : blocked) {
                for (WakeLockInfo wakeLockInfo : wakelocksinfo) {
                    if (wakeLockInfo.wName.equals(name_bloqued)) {
                        wakeLockInfo.wState = false;
                        break;
                    }
                }
            }
        }

        Collections.sort(wakelocksinfo, (w2, w1) -> {
            if(mWakelockOrder == 0) {
                return w2.wName.compareTo(w1.wName);
            } else if ( mWakelockOrder == 1){
                return Integer.compare(w1.wTime, w2.wTime);
            } else if (mWakelockOrder == 2){
                return Integer.compare(w1.wWakeups, w2.wWakeups);
            }
            return 0;
        });

        return wakelocksinfo;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.BOEFFLA_WAKELOCK, id, context);
    }

    public static boolean supported() {
        return Utils.existFile(PARENT);
    }
}
