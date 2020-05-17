package com.example.saludable.Service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyService extends Service implements LocationListener {

    private final Context ctx;

    double latitud;
    double longitud;
    Location location;
    boolean gpsActivo;
    GoogleMap mMap;
    Marker marcador;
    LocationManager locationManager;

    public MyService() {
        super ();
        this.ctx = this.getApplicationContext ();
    }

    public MyService(Context c) {
        super ();
        this.ctx = c;
        GetLocation ();
    }

    public void setView(GoogleMap v) {
        mMap = v;
        LatLng coordenada = new LatLng ( latitud, longitud );

        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom ( coordenada, 17 );

        marcador = mMap.addMarker ( new MarkerOptions ()
                .position ( coordenada )
                .title ( "Mi posision actual" ) );

        mMap.animateCamera ( miUbicacion );

    }

    public void GetLocation() {
        try {
            locationManager = (LocationManager) this.ctx.getSystemService ( LOCATION_SERVICE );
            gpsActivo = locationManager.isProviderEnabled ( LocationManager.GPS_PROVIDER );

        } catch (Exception e) {
        }

        if (gpsActivo) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission ( Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && checkSelfPermission ( Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
            }
            locationManager.requestLocationUpdates ( LocationManager.GPS_PROVIDER
                    , 10000
                    , 1, this );
            location = locationManager.getLastKnownLocation ( LocationManager.GPS_PROVIDER );
            latitud = location.getLatitude ();
            longitud = location.getLongitude ();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
