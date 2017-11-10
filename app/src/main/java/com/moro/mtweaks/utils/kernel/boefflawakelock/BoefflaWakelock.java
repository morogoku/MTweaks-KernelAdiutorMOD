package com.moro.mtweaks.utils.kernel.boefflawakelock;

import android.content.Context;

import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;
import com.moro.mtweaks.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public static boolean isWakelockBlocked(String wakelock){
        try {
            String[] wbs = Utils.readFile(WAKELOCK_BLOCKER).split(";");
            for (String wb : wbs) {
                if (wb.contentEquals(wakelock)) {
                    return true;
                }
            }
        }catch (Exception ignored){
        }
        return false;
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
        String list = "";
        try {
            String[] wakes = Utils.readFile(WAKELOCK_BLOCKER).split(";");
            for(String wake : wakes){
                if(!wake.contentEquals(wakelock)){
                    if (list.contentEquals("")) {
                        list = wake;
                    } else {
                        list += ";" + wake;
                    }
                }
            }
        } catch (Exception ignored){
        }

        run(Control.write(list, WAKELOCK_BLOCKER), WAKELOCK_BLOCKER, context);
    }

    private static List<String> getWakelockNames(){
        List<String> list = new ArrayList<>();
        try {
            String[] lines = Utils.readFile(WAKELOCK_SOURCES).split("\\r?\\n");
            for (String line : lines) {
                if (!line.startsWith("name")) {
                    String[] wl = line.split("\\s+");
                    list.add(wl[0]);
                }
            }
        }catch (Exception ignored) {
        }
        return list;
    }

    private static List<Integer> getWakelockTimes(){
        List<Integer> list = new ArrayList<>();
        try {
            String[] lines = Utils.readFile(WAKELOCK_SOURCES).split("\\r?\\n");
            for (String line : lines) {
                if (!line.startsWith("name")) {
                    String[] wl = line.split("\\s+");
                    list.add(Utils.strToInt(wl[6]));
                }
            }
        }catch (Exception ignored) {
        }
        return list;
    }

    private static List<Integer> getWakelockWakeups(){
        List<Integer> list = new ArrayList<>();
        try {
            String[] lines = Utils.readFile(WAKELOCK_SOURCES).split("\\r?\\n");
            for (String line : lines) {
                if (!line.startsWith("name")) {
                    String[] wl = line.split("\\s+");
                    list.add(Utils.strToInt(wl[3]));
                }
            }
        }catch (Exception ignored) {
        }
        return list;
    }

    public static List<ListWake> getWakelockList(){

        List<ListWake> list = new ArrayList<>();

        try {
            List<String> ListName = getWakelockNames();
            List<Integer> ListTime = getWakelockTimes();
            List<Integer> ListWakeup = getWakelockWakeups();

            for (int i = 0; i < ListName.size(); i++) {
                list.add(new ListWake(ListName.get(i), ListTime.get(i), ListWakeup.get(i)));
            }

            Collections.sort(list, new Comparator<ListWake>() {
                @Override
                public int compare(ListWake w2, ListWake w1) {
                    try{
                        return Integer.valueOf(w1.getTime()).compareTo(w2.getTime());
                    }catch (Exception ignored){
                    }
                    return 0;
                }
            });

        }catch (Exception ignored){
        }

        return list;
    }

    public static void setWakelockOrder(int order){
        mWakelockOrder = order;
    }

    public static List<ListWake> getWakelockListBlocked(){

        List<ListWake> list = new ArrayList<>();

        try {
            List<String> ListName = getWakelockNames();
            List<Integer> ListTime = getWakelockTimes();
            List<Integer> ListWakeup = getWakelockWakeups();

            for (int i = 0; i < ListName.size(); i++) {
                if(isWakelockBlocked(ListName.get(i))) {
                    list.add(new ListWake(ListName.get(i), ListTime.get(i), ListWakeup.get(i)));
                }
            }

            Collections.sort(list, new Comparator<ListWake>() {
                @Override
                public int compare(ListWake w2, ListWake w1) {
                    if(mWakelockOrder == 0) {
                        return w1.getName().compareTo(w2.getName());
                    } else if ( mWakelockOrder == 1){
                        return Integer.valueOf(w1.getTime()).compareTo(w2.getTime());
                    } else if (mWakelockOrder == 2){
                        return Integer.valueOf(w1.getWakeup()).compareTo(w2.getWakeup());
                    }

                    return 0;
                }
            });

        }catch (Exception ignored){
        }

        return list;
    }

    public static List<ListWake> getWakelockListAllowed(){

        List<ListWake> list = new ArrayList<>();

        try {
            List<String> ListName = getWakelockNames();
            List<Integer> ListTime = getWakelockTimes();
            List<Integer> ListWakeup = getWakelockWakeups();

            for (int i = 0; i < ListName.size(); i++) {
                if(!isWakelockBlocked(ListName.get(i))) {
                    list.add(new ListWake(ListName.get(i), ListTime.get(i), ListWakeup.get(i)));
                }
            }

            Collections.sort(list, new Comparator<ListWake>() {
                @Override
                public int compare(ListWake w2, ListWake w1) {
                    if(mWakelockOrder == 0) {
                        return w2.getName().compareTo(w1.getName());
                    } else if ( mWakelockOrder == 1){
                        return Integer.valueOf(w1.getTime()).compareTo(w2.getTime());
                    } else if (mWakelockOrder == 2){
                        return Integer.valueOf(w1.getWakeup()).compareTo(w2.getWakeup());
                    }
                    return 0;
                }
            });

        }catch (Exception ignored){
        }

        return list;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.BOEFFLA_WAKELOCK, id, context);
    }

    public static boolean supported() {
        return Utils.existFile(PARENT);
    }

    public static class ListWake {

        private String mName;
        private int mTime;
        private int mWakeup;

        ListWake(String name, int time, int wakeup){
            mName = name;
            mTime = time;
            mWakeup = wakeup;
        }

        public String getName(){
            return mName;
        }

        public int getTime(){
            return mTime;
        }

        public int getWakeup() {
            return mWakeup;
        }
    }
}
