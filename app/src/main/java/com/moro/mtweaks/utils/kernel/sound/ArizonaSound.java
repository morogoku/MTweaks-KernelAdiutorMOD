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

public class ArizonaSound {

    private static final String ARIZONA_SOUND = "/sys/devices/virtual/misc/arizona_control/";

    private static final String ARIZONA_HP_L = ARIZONA_SOUND + "hp_left_dvol";
    private static final String ARIZONA_HP_R = ARIZONA_SOUND + "hp_right_dvol";
    private static final String ARIZONA_EP = ARIZONA_SOUND + "ep_dvol";
    private static final String ARIZONA_SPK = ARIZONA_SOUND + "speaker_dvol";

    private static final String ARIZONA_SW_SOUND = ARIZONA_SOUND + "switch_enable_sound";
    private static final String ARIZONA_SW_MONO = ARIZONA_SOUND + "switch_hp_mono";
    //private static final String ARIZONA_SW_EQ_CH = ARIZONA_SOUND + "switch_eq_hp_per_ch";
    private static final String ARIZONA_SW_EQ = ARIZONA_SOUND + "switch_eq_hp";

    private static final String ARIZONA_EQ_HPL_1 = ARIZONA_SOUND + "eq_hpl_gain_1";
    private static final String ARIZONA_EQ_HPL_2 = ARIZONA_SOUND + "eq_hpl_gain_2";
    private static final String ARIZONA_EQ_HPL_3 = ARIZONA_SOUND + "eq_hpl_gain_3";
    private static final String ARIZONA_EQ_HPL_4 = ARIZONA_SOUND + "eq_hpl_gain_4";
    private static final String ARIZONA_EQ_HPL_5 = ARIZONA_SOUND + "eq_hpl_gain_5";
    private static final String ARIZONA_EQ_HPL_6 = ARIZONA_SOUND + "eq_hpl_gain_6";
    private static final String ARIZONA_EQ_HPL_7 = ARIZONA_SOUND + "eq_hpl_gain_7";
    private static final String ARIZONA_EQ_HPL_8 = ARIZONA_SOUND + "eq_hpl_gain_8";
/*
    private static final String ARIZONA_EQ_HPR_1 = ARIZONA_SOUND + "eq_hpr_gain_1";
    private static final String ARIZONA_EQ_HPR_2 = ARIZONA_SOUND + "eq_hpr_gain_2";
    private static final String ARIZONA_EQ_HPR_3 = ARIZONA_SOUND + "eq_hpr_gain_3";
    private static final String ARIZONA_EQ_HPR_4 = ARIZONA_SOUND + "eq_hpr_gain_4";
    private static final String ARIZONA_EQ_HPR_5 = ARIZONA_SOUND + "eq_hpr_gain_5";
    private static final String ARIZONA_EQ_HPR_6 = ARIZONA_SOUND + "eq_hpr_gain_6";
    private static final String ARIZONA_EQ_HPR_7 = ARIZONA_SOUND + "eq_hpr_gain_7";
    private static final String ARIZONA_EQ_HPR_8 = ARIZONA_SOUND + "eq_hpr_gain_8";
*/
    private static final List<String> sEqGains = new ArrayList<>();
    private static final LinkedHashMap<String, String> sEqProfiles = new LinkedHashMap<>();

    static {
        sEqGains.add(ARIZONA_EQ_HPL_1);
        sEqGains.add(ARIZONA_EQ_HPL_2);
        sEqGains.add(ARIZONA_EQ_HPL_3);
        sEqGains.add(ARIZONA_EQ_HPL_4);
        sEqGains.add(ARIZONA_EQ_HPL_5);
        sEqGains.add(ARIZONA_EQ_HPL_6);
        sEqGains.add(ARIZONA_EQ_HPL_7);
        sEqGains.add(ARIZONA_EQ_HPL_8);

        sEqProfiles.put("Flat", "0,0,0,0,0,0,0,0");
        sEqProfiles.put("Extreme Bass", "12,8,5,3,1,0,-1,1");
        sEqProfiles.put("Bass-Treble Balance", "10,7,3,1,0,2,3,5");
        sEqProfiles.put("Treble Gain", "-5,-2,1,0,5,4,3,2");
        sEqProfiles.put("Classical", "0,0,0,0,-2,-3,-4,-5");
        sEqProfiles.put("DeepBass", "10,-2,-1,8,3,4,3,8");
        sEqProfiles.put("Eargasm", "12,9,6,4,1,0,2,3");
    }


    // HEADPHONE
    public static boolean hasHeadphone(){
        return (Utils.existFile(ARIZONA_HP_L) && Utils.existFile(ARIZONA_HP_R));
    }

    public static String getHeadphone(){
        Utils.readFile(ARIZONA_HP_L);
        return Utils.readFile(ARIZONA_HP_R);
    }

    public static void setHeadphone(String value, Context context){
        run(Control.write(value, ARIZONA_HP_L), ARIZONA_HP_L, context);
        run(Control.write(value, ARIZONA_HP_R), ARIZONA_HP_R, context);
    }


    public static boolean hasHeadphoneL(){
        return Utils.existFile(ARIZONA_HP_L);
    }

    public static String getHeadphoneL(){
        return Utils.readFile(ARIZONA_HP_L);
    }

    public static void setHeadphoneL(String value, Context context){
        run(Control.write(value, ARIZONA_HP_L), ARIZONA_HP_L, context);
    }

    public static boolean hasHeadphoneR(){
        return Utils.existFile(ARIZONA_HP_R);
    }

    public static String getHeadphoneR(){
        return Utils.readFile(ARIZONA_HP_R);
    }
    
    public static void setHeadphoneR(String value, Context context){
        run(Control.write(value, ARIZONA_HP_R), ARIZONA_HP_R, context);
    }



    // SPEAKER
    public static boolean hasSpeaker(){
        return Utils.existFile(ARIZONA_SPK);
    }

    public static String getSpeaker(){
        return Utils.readFile(ARIZONA_SPK);
    }

    public static void setSpeaker(String value, Context context){
        run(Control.write(value, ARIZONA_SPK), ARIZONA_SPK, context);
    }


    // EARPIECE
    public static boolean hasEarpiece(){
        return Utils.existFile(ARIZONA_EP);
    }

    public static String getEarpiece(){
        return Utils.readFile(ARIZONA_EP);
    }

    public static void setEarpiece(String value, Context context){
        run(Control.write(value, ARIZONA_EP), ARIZONA_EP, context);
    }


    // ENABLE SOUND SWITCH
    public static boolean hasSoundSw(){
        return Utils.existFile(ARIZONA_SW_SOUND);
    }

    public static Boolean isSoundSwEnabled(){
        return Utils.readFile(ARIZONA_SW_SOUND).equals("1");
    }

    public static void enableSoundSw(Boolean enable, Context context){
        run(Control.write(enable ? "1" : "0", ARIZONA_SW_SOUND), ARIZONA_SW_SOUND, context);
    }


    // MONO SWITCH
    public static boolean hasMonoSw(){
        return Utils.existFile(ARIZONA_SW_MONO);
    }

    public static Boolean isMonoSwEnabled(){
        return Utils.readFile(ARIZONA_SW_MONO).equals("1");
    }

    public static void enableMonoSw(Boolean enable, Context context){
        run(Control.write(enable ? "1" : "0", ARIZONA_SW_MONO), ARIZONA_SW_MONO, context);
    }


    // EQ
    public static boolean hasEqSw(){
        return Utils.existFile(ARIZONA_SW_EQ);
    }

    public static Boolean isEqSwEnabled(){
        return Utils.readFile(ARIZONA_SW_EQ).equals("1");
    }

    public static void enableEqSw(Boolean enable, Context context){
        run(Control.write(enable ? "1" : "0", ARIZONA_SW_EQ), ARIZONA_SW_EQ, context);
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
        for (int i = -12; i < 13; i++) {
            progress.add(String.valueOf(i));
        }
        return progress;
    }



    public static boolean supported() {
        return hasEarpiece() || hasHeadphone() || hasSpeaker() || hasEqSw();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SOUND, id, context);
    }
}
