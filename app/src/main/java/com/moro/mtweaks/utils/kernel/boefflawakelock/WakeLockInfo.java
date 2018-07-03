package com.moro.mtweaks.utils.kernel.boefflawakelock;

public class WakeLockInfo {

    public String wName = "";
    public int wTime = 0;
    public int wWakeups = 0;
    public boolean wState = true;

    WakeLockInfo(String name, int time, int wakeups){
        wName = name;
        wTime = time;
        wWakeups = wakeups;
    }
}
