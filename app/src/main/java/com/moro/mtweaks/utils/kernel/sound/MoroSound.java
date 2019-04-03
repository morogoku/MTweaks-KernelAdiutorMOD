package com.moro.mtweaks.utils.kernel.sound;

import android.content.Context;

import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class MoroSound {

    private static final String MORO_SOUND = "/sys/devices/virtual/misc/moro_sound/";

    private static final String HEADPHONE_GAIN = MORO_SOUND + "headphone_gain";
    private static final String EARPIECE_GAIN = MORO_SOUND + "earpiece_gain";
    private static final String SPEAKER_GAIN = MORO_SOUND + "speaker_gain";

    private static final String MAIN_SW = MORO_SOUND + "moro_sound";
    private static final String EQ_SW = MORO_SOUND + "eq";

    private static final String EQ_1_GAIN = MORO_SOUND + "eq_b1_gain";
    private static final String EQ_2_GAIN = MORO_SOUND + "eq_b2_gain";
    private static final String EQ_3_GAIN = MORO_SOUND + "eq_b3_gain";
    private static final String EQ_4_GAIN = MORO_SOUND + "eq_b4_gain";
    private static final String EQ_5_GAIN = MORO_SOUND + "eq_b5_gain";
    private static final String EQ_GAINS = MORO_SOUND + "eq_gains";

    //private static final String HEADPHONE_LIMITS = MORO_SOUND + "headphone_limits";



    private static final String VERSION = MORO_SOUND + "version";

    private static final List<String> sEqGains = new ArrayList<>();
    private static final LinkedHashMap<String, String> sEqProfiles = new LinkedHashMap<>();

    static {
        sEqGains.add(EQ_1_GAIN);
        sEqGains.add(EQ_2_GAIN);
        sEqGains.add(EQ_3_GAIN);
        sEqGains.add(EQ_4_GAIN);
        sEqGains.add(EQ_5_GAIN);

        sEqProfiles.put("Flat", "0,0,0,0,0");
        sEqProfiles.put("Extreme Bass", "12,8,3,-1,1");
        sEqProfiles.put("Bass-Treble Balance", "10,7,0,2,5");
        sEqProfiles.put("Treble Gain", "-5,1,0,4,3");
        sEqProfiles.put("Classical", "0,0,0,-3,-5");
        sEqProfiles.put("Pleasant", "4,3,2,2,3");
        sEqProfiles.put("Eargasm", "12,8,4,2,3");
        sEqProfiles.put("BeatsBass", "10,8,6,5,7");
        sEqProfiles.put("Enhanced-Sound", "9,-3,9,7,8");
        sEqProfiles.put("DeepBass", "10,-1,8,4,8");
        sEqProfiles.put("Detonation", "9,4,-2,7,11");
        sEqProfiles.put("BeastCream", "9,4,10,4,7");
    }


    // VERSION
    public static boolean hasVersion(){
        return Utils.existFile(VERSION);
    }

    public static String getVersion(){
        return Utils.readFile(VERSION);
    }


    // HEADPHONE
    public static boolean hasHeadphone(){
        return Utils.existFile(HEADPHONE_GAIN);
    }

    public static void setHeadphone(String value, Context context){
        run(Control.write(value + " " + value, HEADPHONE_GAIN), HEADPHONE_GAIN, context);
    }

    // Grx
    public static void setHeadPhoneValues(String value_left, String value_right, Context context){
        run(Control.write(value_left + " " + value_right, HEADPHONE_GAIN), HEADPHONE_GAIN, context);
    }
/*
    public static String getHeadphoneLimits(){
        return Utils.readFile(HEADPHONE_LIMITS);
    }
*/
    public static String getHeadphoneL(){
        String[] value = Utils.readFile(HEADPHONE_GAIN).split(" ");
        return value[0];
    }

    public static void setHeadphoneL(String value, Context context){
        String right = getHeadphoneR();
        run(Control.write(value + " " + right, HEADPHONE_GAIN),HEADPHONE_GAIN, context);
    }

    public static String getHeadphoneR(){
        String[] value = Utils.readFile(HEADPHONE_GAIN).split(" ");
        return value[1];
    }
    
    public static void setHeadphoneR(String value, Context context){
        String left = getHeadphoneL();
        run(Control.write(left + " " + value, HEADPHONE_GAIN), HEADPHONE_GAIN, context);
    }


    // SPEAKER
    public static boolean hasSpeaker(){
        return Utils.existFile(SPEAKER_GAIN);
    }

    public static String getSpeaker(){
        return Utils.readFile(SPEAKER_GAIN);
    }

    public static void setSpeaker(String value, Context context){
        run(Control.write(value, SPEAKER_GAIN), SPEAKER_GAIN, context);
    }


    // EARPIECE
    public static boolean hasEarpiece(){
        return Utils.existFile(EARPIECE_GAIN);
    }

    public static String getEarpiece(){
        return Utils.readFile(EARPIECE_GAIN);
    }

    public static void setEarpiece(String value, Context context){
        run(Control.write(value, EARPIECE_GAIN), EARPIECE_GAIN, context);
    }


    // ENABLE SOUND SWITCH
    public static boolean hasSoundSw(){
        return Utils.existFile(MAIN_SW);
    }

    public static Boolean isSoundSwEnabled(){
        return Utils.readFile(MAIN_SW).equals("1");
    }

    public static void enableSoundSw(Boolean enable, Context context){
        run(Control.write(enable ? "1" : "0", MAIN_SW), MAIN_SW, context);
    }


    // EQ
    public static boolean hasEqSw(){
        return Utils.existFile(EQ_SW);
    }

    public static Boolean isEqSwEnabled(){
        return Utils.readFile(EQ_SW).equals("1");
    }

    public static void enableEqSw(Boolean enable, Context context){
        run(Control.write(enable ? "1" : "0", EQ_SW), EQ_SW, context);
    }

    public static List<String> getEqProfileList(){
        return new ArrayList<>(sEqProfiles.keySet());
    }

    public static List<String> getEqProfileValues(String item){
        String [] list = Objects.requireNonNull(sEqProfiles.get(item)).split(",");
        return Arrays.asList(list);
    }

    public static List<String> getEqValues() {
        List<String> list = new ArrayList<>();
        for (String file : sEqGains) {
            list.add(Utils.readFile(file));
        }
        return list;
    }

    public static void setEqValues(String value, int id, Context context) {
        run(Control.write(value, sEqGains.get(id)), sEqGains.get(id), context);
    }

    public static List<String> getEqLimit() {
        List<String> progress = new ArrayList<>();
        for (int i = -12; i <= 12; i++) {
            progress.add(String.valueOf(i));
        }
        return progress;
    }

    // Grx
    public static String getCurrentProfileString(){
        String value="";
        int size = sEqGains.size();
        for(int i = 0 ; i< size ; i++) {
            value+=Utils.readFile(sEqGains.get(i));
            if(i<size-1) value+=",";
        }
        return value;
    }

    public static String getEqValue(int id){
        return Utils.readFile(sEqGains.get(id));
    }



    public static boolean supported() {
        return hasEarpiece() || hasHeadphone() || hasSpeaker() || hasEqSw();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SOUND, id, context);
    }








    //
}
