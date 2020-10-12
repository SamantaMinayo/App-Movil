package com.example.saludable.Service;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import com.example.saludable.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

public class Utils {

    public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";
    private static DecimalFormat formato1;

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences ( context )
                .getBoolean ( KEY_REQUESTING_LOCATION_UPDATES, false );
    }

    /**
     * Stores the location updates state in SharedPreferences.
     *
     * @param requestingLocationUpdates The location updates state.
     */
    static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences ( context )
                .edit ()
                .putBoolean ( KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates )
                .apply ();
    }

    /**
     * Returns the {@code location} object as a human readable string.
     *
     * @param location The {@link Location}.
     */
    public static String getLocationText(Location location, float distancia) {
        formato1 = new DecimalFormat ( "#.00" );

        if (location == null) {

            return "Unknown location";
        } else {

            return "(" + formato1.format ( distancia ) + "m, vel: " + formato1.format ( location.getSpeed () ) + " m/s )";

        }

    }

    static String getLocationTitle(Context context) {
        return context.getString ( R.string.location_updated,
                DateFormat.getDateTimeInstance ().format ( new Date () ) );
    }

}
