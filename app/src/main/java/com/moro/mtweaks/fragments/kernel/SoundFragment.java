/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
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
package com.moro.mtweaks.fragments.kernel;

import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;

import com.grx.soundcontrol.GrxMicVolumeManager;
import com.moro.mtweaks.R;
import com.moro.mtweaks.fragments.ApplyOnBootFragment;
import com.moro.mtweaks.fragments.recyclerview.RecyclerViewFragment;
import com.moro.mtweaks.utils.AppSettings;
import com.moro.mtweaks.utils.Utils;
import com.moro.mtweaks.utils.kernel.sound.ArizonaSound;
import com.moro.mtweaks.utils.kernel.sound.MoroSound;
import com.moro.mtweaks.utils.kernel.sound.Sound;
import com.moro.mtweaks.utils.kernel.vm.ZRAM;
import com.moro.mtweaks.views.recyclerview.ButtonView2;
import com.moro.mtweaks.views.recyclerview.CardView;
import com.moro.mtweaks.views.recyclerview.CheckBoxView;
import com.moro.mtweaks.views.recyclerview.DescriptionView;
import com.moro.mtweaks.views.recyclerview.RecyclerViewItem;
import com.moro.mtweaks.views.recyclerview.SeekBarView;
import com.moro.mtweaks.views.recyclerview.SelectView;
import com.moro.mtweaks.views.recyclerview.SwitchView;
import com.moro.mtweaks.views.recyclerview.TitleView;

import com.grx.soundcontrol.GrxEqualizerManager;
import com.grx.soundcontrol.GrxVolumeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 26.06.16.
 */
public class SoundFragment extends RecyclerViewFragment {

    private Sound mSound;

    @Override
    protected void init() {
        super.init();

        mSound = Sound.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (mSound.hasSoundControlEnable()) {
            soundControlEnableInit(items);
        }
        if (mSound.hasHighPerfModeEnable()) {
            highPerfModeEnableInit(items);
        }
        if (mSound.hasHeadphoneGain()) {
            headphoneGainInit(items);
        }
        if (mSound.hasHandsetMicrophoneGain()) {
            handsetMicrophoneGainInit(items);
        }
        if (mSound.hasCamMicrophoneGain()) {
            camMicrophoneGainInit(items);
        }
        if (mSound.hasHeadphoneFlar()) {
            headphoneFlarInit(items);
        }
        if (mSound.hasHeadphoneMoro()) {
            moroSoundControlInit(items);
        }
        if (ArizonaSound.supported()) {
            arizonaSoundInit(items);
        }
        if (mSound.hasSpeakerGain()) {
            speakerGainInit(items);
        }
        if (mSound.hasMicrophoneFlar()) {
            microphoneFlarInit(items);
        }
        if (mSound.hasHeadphonePowerAmpGain()) {
            headphonePowerAmpGainInit(items);
        }
        if (mSound.hasHeadphoneTpaGain()) {
            headphoneTpaGainInit(items);
        }
        if (mSound.hasLockOutputGain()) {
            lockOutputGainInit(items);
        }
        if (mSound.hasLockMicGain()) {
            lockMicGainInit(items);
        }
        if (mSound.hasMicrophoneGain()) {
            microphoneGainInit(items);
        }
        if (mSound.hasVolumeGain()) {
            volumeGainInit(items);
        }
        if (MoroSound.supported()) {
            moroSoundInit(items);
        }
    }

    private void soundControlEnableInit(List<RecyclerViewItem> items) {
        SwitchView soundControl = new SwitchView();
        soundControl.setSummary(getString(R.string.sound_control));
        soundControl.setChecked(mSound.isSoundControlEnabled());
        soundControl.addOnSwitchListener((switchView, isChecked)
                -> mSound.enableSoundControl(isChecked, getActivity()));

        items.add(soundControl);
    }

    private void highPerfModeEnableInit(List<RecyclerViewItem> items) {
        SwitchView highPerfMode = new SwitchView();
        highPerfMode.setSummary(getString(R.string.headset_highperf_mode));
        highPerfMode.setChecked(mSound.isHighPerfModeEnabled());
        highPerfMode.addOnSwitchListener((switchView, isChecked)
                -> mSound.enableHighPerfMode(isChecked, getActivity()));

        items.add(highPerfMode);
    }

    private void headphoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView headphoneGain = new SeekBarView();
        headphoneGain.setTitle(getString(R.string.headphone_gain));
        headphoneGain.setItems(mSound.getHeadphoneGainLimits());
        headphoneGain.setProgress(mSound.getHeadphoneGainLimits().indexOf(mSound.getHeadphoneGain()));
        headphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setHeadphoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(headphoneGain);
    }

    private void handsetMicrophoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView handsetMicrophoneGain = new SeekBarView();
        handsetMicrophoneGain.setTitle(getString(R.string.handset_microphone_gain));
        handsetMicrophoneGain.setItems(mSound.getHandsetMicrophoneGainLimits());
        handsetMicrophoneGain.setProgress(mSound.getHandsetMicrophoneGainLimits()
                .indexOf(mSound.getHandsetMicrophoneGain()));
        handsetMicrophoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setHandsetMicrophoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(handsetMicrophoneGain);
    }

    private void camMicrophoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView camMicrophoneGain = new SeekBarView();
        camMicrophoneGain.setTitle(getString(R.string.cam_microphone_gain));
        camMicrophoneGain.setItems(mSound.getCamMicrophoneGainLimits());
        camMicrophoneGain.setProgress(mSound.getCamMicrophoneGainLimits().indexOf(mSound.getCamMicrophoneGain()));
        camMicrophoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setCamMicrophoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(camMicrophoneGain);
    }

    private void speakerGainInit(List<RecyclerViewItem> items) {
        SeekBarView speakerGain = new SeekBarView();
        speakerGain.setTitle(getString(R.string.speaker_gain));
        speakerGain.setItems(mSound.getSpeakerGainLimits());
        speakerGain.setProgress(mSound.getSpeakerGainLimits().indexOf(mSound.getSpeakerGain()));
        speakerGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setSpeakerGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(speakerGain);
    }

    private void headphonePowerAmpGainInit(List<RecyclerViewItem> items) {
        SeekBarView headphonePowerAmpGain = new SeekBarView();
        headphonePowerAmpGain.setTitle(getString(R.string.headphone_poweramp_gain));
        headphonePowerAmpGain.setItems(mSound.getHeadphonePowerAmpGainLimits());
        headphonePowerAmpGain.setProgress(mSound.getHeadphonePowerAmpGainLimits()
                .indexOf(mSound.getHeadphonePowerAmpGain()));
        headphonePowerAmpGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setHeadphonePowerAmpGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(headphonePowerAmpGain);
    }

    private void headphoneTpaGainInit(List<RecyclerViewItem> items) {
        SeekBarView headphoneTpaGain = new SeekBarView();
        headphoneTpaGain.setTitle(getString(R.string.headphone_tpa6165_gain));
        headphoneTpaGain.setItems(mSound.getHeadphoneTpaGainLimits());
        headphoneTpaGain.setProgress(mSound.getHeadphoneTpaGainLimits()
                .indexOf(mSound.getHeadphoneTpaGain()));
        headphoneTpaGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setHeadphoneTpaGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(headphoneTpaGain);
    }

    private void lockOutputGainInit(List<RecyclerViewItem> items) {
        SwitchView lockOutputGain = new SwitchView();
        lockOutputGain.setTitle(getString(R.string.lock_output_gain));
        lockOutputGain.setSummary(getString(R.string.lock_output_gain_summary));
        lockOutputGain.setChecked(mSound.isLockOutputGainEnabled());
        lockOutputGain.addOnSwitchListener((switchView, isChecked)
                -> mSound.enableLockOutputGain(isChecked, getActivity()));

        items.add(lockOutputGain);
    }

    private void lockMicGainInit(List<RecyclerViewItem> items) {
        SwitchView lockMicGain = new SwitchView();
        lockMicGain.setTitle(getString(R.string.lock_mic_gain));
        lockMicGain.setSummary(getString(R.string.lock_mic_gain_summary));
        lockMicGain.setChecked(mSound.isLockMicGainEnabled());
        lockMicGain.addOnSwitchListener((switchView, isChecked)
                -> mSound.enableLockMicGain(isChecked, getActivity()));

        items.add(lockMicGain);
    }

    private void microphoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView microphoneGain = new SeekBarView();
        microphoneGain.setTitle(getString(R.string.microphone_gain));
        microphoneGain.setItems(mSound.getMicrophoneGainLimits());
        microphoneGain.setProgress(mSound.getMicrophoneGainLimits().indexOf(mSound.getMicrophoneGain()));
        microphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setMicrophoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(microphoneGain);
    }

    private void volumeGainInit(List<RecyclerViewItem> items) {
        SeekBarView volumeGain = new SeekBarView();
        volumeGain.setTitle(getString(R.string.volume_gain));
        volumeGain.setItems(mSound.getVolumeGainLimits());
        volumeGain.setProgress(mSound.getVolumeGainLimits().indexOf(mSound.getVolumeGain()));
        volumeGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setVolumeGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(volumeGain);
    }

    private void headphoneFlarInit(List<RecyclerViewItem> items) {

        TitleView title = new TitleView();
        title.setText(getString(R.string.sound_control));

        SeekBarView headphoneFlar = new SeekBarView();
        headphoneFlar.setTitle(getString(R.string.headphone_gain));
        headphoneFlar.setItems(mSound.getHeadphoneFlarLimits());
        headphoneFlar.setProgress(mSound.getHeadphoneFlarLimits().indexOf(mSound.getHeadphoneFlar()));
        headphoneFlar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setHeadphoneFlar(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });
        items.add(title);
        items.add(headphoneFlar);
    }

    private void microphoneFlarInit(List<RecyclerViewItem> items) {
        SeekBarView microphoneFlar = new SeekBarView();
        microphoneFlar.setTitle(getString(R.string.microphone_gain));
        microphoneFlar.setItems(mSound.getMicrophoneFlarLimits());
        microphoneFlar.setProgress(mSound.getMicrophoneFlarLimits().indexOf(mSound.getMicrophoneFlar()));
        microphoneFlar.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mSound.setMicrophoneFlar(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(microphoneFlar);
    }

    private void moroSoundControlInit(List<RecyclerViewItem> items) {

        CardView moroCard = new CardView(getActivity());
        moroCard.setTitle(getString(R.string.moro_sound_control));

        DescriptionView moroDesc = new DescriptionView();
        moroDesc.setSummary(getString(R.string.moro_sound_summary));
        moroCard.addItem(moroDesc);

        if (mSound.hasHeadphoneMoro()) {
            SeekBarView headphoneMoro = new SeekBarView();
            headphoneMoro.setTitle(getString(R.string.headphone_gain));
            headphoneMoro.setSummary(getString(R.string.def) + ": 640");
            headphoneMoro.setItems(mSound.getHeadphoneMoroLimits());
            headphoneMoro.setProgress(mSound.getHeadphoneMoroLimits().indexOf(mSound.getHeadphoneMoro()));
            headphoneMoro.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setHeadphoneMoro(value, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });
            moroCard.addItem(headphoneMoro);
        }

        if (mSound.hasEarpieceMoro()) {
            SeekBarView earpieceMoro = new SeekBarView();
            earpieceMoro.setTitle(getString(R.string.earpiece_gain));
            earpieceMoro.setSummary(getString(R.string.earpiece_gain_summary) + "\n\n" + getString(R.string.def) + ": 640");
            earpieceMoro.setItems(mSound.getHeadphoneMoroLimits());
            earpieceMoro.setProgress(mSound.getHeadphoneMoroLimits().indexOf(mSound.getEarpieceMoro()));
            earpieceMoro.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setEarpieceMoro(value, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });
            moroCard.addItem(earpieceMoro);
        }

        if (mSound.hasMoroSpeakerGain()) {
            SeekBarView speakerMoro = new SeekBarView();
            speakerMoro.setTitle(getString(R.string.speaker_gain));
            speakerMoro.setSummary(getString(R.string.def) + ": 20");
            speakerMoro.setItems(mSound.getSpeakerGainLimits());
            speakerMoro.setProgress(mSound.getSpeakerGainLimits().indexOf(mSound.getSpeakerGain()));
            speakerMoro.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mSound.setSpeakerGain(value, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });
            moroCard.addItem(speakerMoro);
        }

        if (moroCard.size() > 0){
            items.add(moroCard);
        }
    }

    private void arizonaSoundInit(List<RecyclerViewItem> items) {

        List<SeekBarView> mEqGain = new ArrayList<>();
        mEqGain.clear();

        boolean isSoundEnabled;
        boolean hasSound = ArizonaSound.hasSoundSw();

        if (hasSound) {
            isSoundEnabled = ArizonaSound.isSoundSwEnabled();
        } else {
            isSoundEnabled = true;
        }

        SeekBarView hp = new SeekBarView();
        SeekBarView ep = new SeekBarView();
        SeekBarView spk = new SeekBarView();
        CheckBoxView mono = new CheckBoxView();
        SwitchView eqsw = new SwitchView();
        SelectView eqprofile = new SelectView();


        if (ArizonaSound.hasSoundSw()) {
            CardView asCard = new CardView(getActivity());
            asCard.setTitle(getString(R.string.arizona_title));

            SwitchView es = new SwitchView();
            es.setTitle(getString(R.string.arizona_sound_sw));
            es.setSummaryOn(getString(R.string.enabled));
            es.setSummaryOff(getString(R.string.disabled));
            es.setChecked(ArizonaSound.isSoundSwEnabled());
            es.addOnSwitchListener((switchView, isChecked) -> {
                ArizonaSound.enableSoundSw(isChecked, getActivity());
                getHandler().postDelayed(() -> {
                        // Refresh HP
                        hp.setEnabled(isChecked);
                        hp.setProgress(Utils.strToInt(ArizonaSound.getHeadphone()));

                        // Refresh EP
                        ep.setEnabled(isChecked);
                        ep.setProgress(Utils.strToInt(ArizonaSound.getEarpiece()));

                        // Refresh Speaker
                        spk.setEnabled(isChecked);
                        spk.setProgress(Utils.strToInt(ArizonaSound.getSpeaker()));

                        // Refresh Mono Switch
                        mono.setEnabled(isChecked);
                        mono.setChecked(ArizonaSound.isMonoSwEnabled());

                        // Refresh EQ Switch
                        eqsw.setEnabled(isChecked);
                        eqsw.setChecked(ArizonaSound.isEqSwEnabled());

                        // Reset EQ
                        if(!isChecked){
                            eqprofile.setItem(0);
                            AppSettings.saveInt("arizona_eq_profile", 0, getActivity());

                            List<String> limit = ArizonaSound.getEqLimit();
                            List<String> values = ArizonaSound.getEqValues();
                            for (int i = 0; i < 8; i++) {
                                mEqGain.get(i).setProgress(limit.indexOf(values.get(i)));
                            }
                        }
                }, 100);
            });
            asCard.addItem(es);
            if (asCard.size() > 0) items.add(asCard);
        }


        CardView gainCard = new CardView(getActivity());
        if (hasSound) {
            gainCard.setTitle(getString(R.string.arizona_volume_title));
        } else {
            gainCard.setTitle(getString(R.string.arizona_title));
        }

        if (ArizonaSound.hasHeadphone()) {
            hp.setTitle(getString(R.string.headphone_gain));
            hp.setMin(0);
            hp.setMax(190);
            hp.setUnit(getString(R.string.db));
            hp.setEnabled(isSoundEnabled);
            hp.setProgress(Utils.strToInt(ArizonaSound.getHeadphone()));
            hp.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ArizonaSound.setHeadphone(value, getContext());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });
            gainCard.addItem(hp);
        }

        if(ArizonaSound.hasEarpiece()) {
            ep.setTitle(getString(R.string.earpiece_gain));
            ep.setSummary(getString(R.string.earpiece_gain_summary));
            ep.setMin(0);
            ep.setMax(30);
            ep.setUnit(getString(R.string.db));
            ep.setEnabled(isSoundEnabled);
            ep.setProgress(Utils.strToInt(ArizonaSound.getEarpiece()));
            ep.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ArizonaSound.setEarpiece(value, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });
            gainCard.addItem(ep);
        }

        if(ArizonaSound.hasSpeaker()){
            spk.setTitle(getString(R.string.speaker_gain));
            spk.setMin(0);
            spk.setMax(30);
            spk.setUnit(getString(R.string.db));
            spk.setEnabled(isSoundEnabled);
            spk.setProgress(Utils.strToInt(ArizonaSound.getSpeaker()));
            spk.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    ArizonaSound.setSpeaker(value, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });
            gainCard.addItem(spk);
        }

        if(ArizonaSound.hasMonoSw()) {
            mono.setTitle(getString(R.string.arizona_mono_tit));
            mono.setSummary(getString(R.string.arizona_mono_desc));
            mono.setEnabled(isSoundEnabled);
            mono.setChecked(ArizonaSound.isMonoSwEnabled());
            mono.addOnCheckboxListener((checkboxView, isChecked) ->
                    ArizonaSound.enableMonoSw(isChecked, getActivity()));
            gainCard.addItem(mono);
        }

        if (gainCard.size() > 0) items.add(gainCard);


        CardView eqCard = new CardView(getActivity());
        eqCard.setTitle(getString(R.string.arizona_eq_title));

        if (ArizonaSound.hasEqSw()) {
            eqsw.setTitle(getString(R.string.arizona_eq_sw));
            eqsw.setSummaryOn(getString(R.string.enabled));
            eqsw.setSummaryOff(getString(R.string.disabled));
            eqsw.setEnabled(isSoundEnabled);
            eqsw.setChecked(ArizonaSound.isEqSwEnabled());
            eqsw.addOnSwitchListener((switchView, isChecked) -> {
                ArizonaSound.enableEqSw(isChecked, getActivity());
                eqprofile.setEnabled(isChecked);
                for (int i = 0; i < 8; i++) {
                    mEqGain.get(i).setEnabled(isChecked);
                }
            });
            eqCard.addItem(eqsw);

            eqprofile.setTitle(getString(R.string.arizona_eqprofile_tit));
            eqprofile.setSummary(getString(R.string.arizona_eqprofile_desc));
            eqprofile.setEnabled(isSoundEnabled);
            eqprofile.setItems(ArizonaSound.getEqProfileList());
            eqprofile.setItem(AppSettings.getInt("arizona_eq_profile", 0, getActivity()));
            eqprofile.setOnItemSelected((selectView, position, item) -> {
                AppSettings.saveInt("arizona_eq_profile", position, getActivity());
                List<String> values = ArizonaSound.getEqProfileValues(item);
                for (int i = 0; i < 8; i++) {
                    ArizonaSound.setEqValues(values.get(i), i, getActivity());
                    mEqGain.get(i).setProgress(ArizonaSound.getEqLimit().indexOf(values.get(i)));
                }
            });
            eqCard.addItem(eqprofile);


            String[] names = getResources().getStringArray(R.array.eq_names);
            String[] descriptions = getResources().getStringArray(R.array.eq_summary);
            List<String> values = ArizonaSound.getEqValues();
            List<String> eqLimit = ArizonaSound.getEqLimit();

            for (int i = 0; i < 8; i++) {
                final int id = i;
                SeekBarView eqgain = new SeekBarView();
                eqgain.setTitle(names[i]);
                eqgain.setSummary(descriptions[i]);
                eqgain.setEnabled(ArizonaSound.isEqSwEnabled());
                eqgain.setItems(eqLimit);
                eqgain.setProgress(ArizonaSound.getEqLimit().indexOf(values.get(i)));
                eqgain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        ArizonaSound.setEqValues(value, id, getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                    }
                });
                eqCard.addItem(eqgain);
                mEqGain.add(eqgain);
            }
        }

        if (eqCard.size() > 0) items.add(eqCard);
    }

    private void moroSoundInit(List<RecyclerViewItem> items) {

        boolean isSoundEnabled = MoroSound.isSoundSwEnabled();
        boolean hasEq = MoroSound.hasEqSw();
        boolean hasDualSpeaker = MoroSound.hasDualSpeakerSw();
        boolean hasHeadphoneMono = MoroSound.hasHeadphoneMonoSw();
        boolean hasMic = MoroSound.hasMicSw();
        boolean hasReset = MoroSound.hasReset();

        GrxVolumeManager volumeManager = new GrxVolumeManager();
        GrxMicVolumeManager micVolumeManager = new GrxMicVolumeManager();
        GrxEqualizerManager equalizerManager = new GrxEqualizerManager();
        SwitchView dualSpeaker = new SwitchView();
        SwitchView headphoneMono = new SwitchView();


        CardView asCard = new CardView(getActivity());
        asCard.setTitle(getString(R.string.moro_sound_control) + " v" + MoroSound.getVersion());

        if (MoroSound.hasSoundSw()) {
            SwitchView es = new SwitchView();
            es.setTitle(getString(R.string.arizona_sound_sw));
            es.setSummaryOn(getString(R.string.enabled));
            es.setSummaryOff(getString(R.string.disabled));
            es.setChecked(isSoundEnabled);
            es.addOnSwitchListener((switchView, isChecked) -> {
                MoroSound.enableSoundSw(isChecked, getActivity());
                getHandler().postDelayed(() -> {
                    volumeManager.setMainSwitchEnabled(isChecked);
                    equalizerManager.setMainSwitchEnabled(isChecked);
                    if (hasMic) micVolumeManager.setMainSwitchEnabled(isChecked);
                    if (hasDualSpeaker) dualSpeaker.setEnabled(isChecked);
                    if (hasHeadphoneMono) headphoneMono.setEnabled(isChecked);
                    }, 100);
            });
            asCard.addItem(es);
        }

        if (hasDualSpeaker) {
            dualSpeaker.setTitle(getString(R.string.moro_sound_dual_speaker));
            dualSpeaker.setSummary(getString(R.string.moro_sound_dual_speaker_desc));
            dualSpeaker.setChecked(MoroSound.isDualSpeakerSwEnabled());
            dualSpeaker.setEnabled(isSoundEnabled);
            dualSpeaker.addOnSwitchListener((switchView, isChecked) ->
                    MoroSound.enableDualSpeakerSw(isChecked, getActivity())
            );
            asCard.addItem(dualSpeaker);
        }

        if (hasHeadphoneMono) {
            headphoneMono.setTitle(getString(R.string.moro_sound_headphone_mono));
            headphoneMono.setSummary(getString(R.string.moro_sound_headphone_mono_desc));
            headphoneMono.setChecked(MoroSound.isHeadphoneMonoSwEnabled());
            headphoneMono.setEnabled(isSoundEnabled);
            headphoneMono.addOnSwitchListener((switchView, isChecked) ->
                    MoroSound.enableHeadphoneMonoSw(isChecked, getActivity())
            );
            asCard.addItem(headphoneMono);
        }

        if (hasReset) {
            ButtonView2 reset = new ButtonView2();
            reset.setTitle(getString(R.string.moro_sound_reset));
            reset.setSummary(getString(R.string.moro_sound_reset_summary));
            reset.setButtonText(getString(R.string.moro_sound_reset));
            reset.setOnItemClickListener(view -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(getString(R.string.wkl_alert_title));
                alert.setMessage(getString(R.string.moro_sound_reset_message));
                alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {});
                alert.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                    MoroSound.setResetBtn("1", getActivity());
                    getHandler().postDelayed(() -> {
                        volumeManager.resetValues();
                        micVolumeManager.resetValues();
                        if (hasEq) equalizerManager.resetValues();
                        if (hasDualSpeaker) dualSpeaker.setChecked(false);
                        if (hasHeadphoneMono) headphoneMono.setChecked(false);
                    }, 100);
                });
                alert.show();
            });
            asCard.addItem(reset);
        }

        if (asCard.size() > 0) items.add(asCard);


        /* grx - volume card recycleritemview */

        CardView volumeCard = new CardView(getActivity());
        volumeCard.setTitle(getString(R.string.moro_sound_volume_control));
        volumeCard.addItem(volumeManager);
        items.add(volumeCard);

        if (hasMic) {
            CardView mCard = new CardView(getActivity());
            mCard.setTitle(getString(R.string.moro_sound_mic));
            mCard.addItem(micVolumeManager);
            items.add(mCard);
        }

        if (hasEq) {
            CardView eqCard = new CardView(getActivity());
            eqCard.setTitle(getString(R.string.arizona_eq_title));
            eqCard.addItem(equalizerManager);
            items.add(eqCard);
        }
    }
}
