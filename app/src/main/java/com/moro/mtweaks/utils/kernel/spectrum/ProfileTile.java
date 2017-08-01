package com.moro.mtweaks.utils.kernel.spectrum;

import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.moro.mtweaks.R;
import com.moro.mtweaks.utils.Prefs;
import com.moro.mtweaks.utils.Utils;

/**
 * Created by Morogoku on 01/08/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class ProfileTile extends TileService {

    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    private boolean click = false;

    @Override
    public void onStartListening() {
        resetTileStatus();
    }

    @Override
    public void onClick() {
        updateTile();
    }

    private void updateTile() {
        Tile tile = this.getQsTile();
        boolean isActive = getServiceStatus();
        Icon newIcon;
        String newLabel;
        int newState;

        // Update tile and set profile
        if (isActive && click) {
            newLabel = "Gaming";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_game);
            newState = Tile.STATE_ACTIVE;
            click = false;
            Spectrum.setProfile(3);
            Prefs.saveInt("spectrum_profile", 3, getApplicationContext());
        } else if (!isActive && click) {
            newLabel = "Battery";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_battery);
            newState = Tile.STATE_ACTIVE;
            click = true;
            Spectrum.setProfile(2);
            Prefs.saveInt("spectrum_profile", 2, getApplicationContext());
        } else if (isActive && !click){
            newLabel = "Performance";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_performance);
            newState = Tile.STATE_ACTIVE;
            click = true;
            Spectrum.setProfile(1);
            Prefs.saveInt("spectrum_profile", 1, getApplicationContext());
        } else {
            newLabel = "Balance";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_balanced);
            newState = Tile.STATE_ACTIVE;
            click = false;
            Spectrum.setProfile(0);
            Prefs.saveInt("spectrum_profile", 0, getApplicationContext());
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);
        tile.updateTile();
    }

    private boolean getServiceStatus() {

        boolean isActive = Prefs.getBoolean(SERVICE_STATUS_FLAG, false, getApplicationContext());
        isActive = !isActive;

        Prefs.saveBoolean(SERVICE_STATUS_FLAG, isActive, getApplicationContext());

        return isActive;
    }

    private void resetTileStatus() {
        int profile = Utils.strToInt(Spectrum.getProfile());
        Tile tile = this.getQsTile();
        Icon newIcon;
        String newLabel;
        int newState;

        // Update tile
        if (profile == 3){
            newLabel = "Gaming";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_game);
            newState = Tile.STATE_ACTIVE;
            click = false;
        } else if (profile == 2){
            newLabel = "Battery";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_battery);
            newState = Tile.STATE_ACTIVE;
            click = true;
        } else if (profile == 1){
            newLabel = "Performance";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_performance);
            newState = Tile.STATE_ACTIVE;
            click = true;
        } else if (profile == 0){
            newLabel = "Balance";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_balanced);
            newState = Tile.STATE_ACTIVE;
            click = false;
        } else {
            newLabel = "Custom";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_logo);
            newState = Tile.STATE_ACTIVE;
            click = false;
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);
        tile.updateTile();
    }
}
