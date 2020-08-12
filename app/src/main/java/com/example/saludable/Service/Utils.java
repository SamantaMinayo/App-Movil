package com.example.saludable.Service;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.preference.PreferenceManager;

import com.example.saludable.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.util.Date;

public class Utils {

    public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";
    public GoogleMap mMap;
    public Marker marcador;

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
    public static String getLocationText(Location location) {
        if (location == null) {

            return "Unknown location";
        } else {
            //  agregarMarcador(location.getLatitude(),location.getLongitude());
            return "(" + location.getLatitude () + ", " + location.getLongitude () + ")";

        }

    }

    static String getLocationTitle(Context context) {
        return context.getString ( R.string.location_updated,
                DateFormat.getDateTimeInstance ().format ( new Date () ) );
    }

    public void agregarMarcador(double lat, double log) {
        try {
            Location locationA = new Location ( "punto A" );
            locationA.setLatitude ( Double.parseDouble ( "-0.338574" ) );
            locationA.setLongitude ( Double.parseDouble ( "-78.450000" ) );

            Polyline line = mMap.addPolyline ( new PolylineOptions ()
                    .add ( new LatLng ( locationA.getLatitude (), locationA.getLongitude () ), new LatLng ( lat, log ) )
                    .width ( 5 )
                    .color ( Color.RED ) );

            LatLng coordenada = new LatLng ( lat, log );
            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom ( coordenada, 17 );

            if (marcador != null) marcador.remove ();
            marcador = mMap.addMarker ( new MarkerOptions ()
                    .position ( coordenada )
                    .title ( "Mi posision actual" )
            );
            mMap.animateCamera ( miUbicacion );
        } catch (Exception e) {
            String error = "ERROR";
        }

    }
}
