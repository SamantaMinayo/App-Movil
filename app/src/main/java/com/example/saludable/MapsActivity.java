package com.example.saludable;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static String latitudinicial = "0.0";
    private static String longitudinicial = "0.0";
    private static boolean monitorear = false;
    private static ValueEventListener ls;
    private final int TIEMPO = 30000;
    double lat = 0.0;
    double log = 0.0;
    double vel = 0.0;
    private GoogleMap mMap;
    private Marker marcador;
    private DatabaseReference UsuarioCarreraRef, UsCarrInformationRef;
    private FirebaseAuth mAuth;
    private String PostKey, current_user_id, saveCurrenTime;
    private long countPost = 0;
    private Date anterior;
    private ProgressDialog loadingBar;
    LocationListener locListener = new LocationListener () {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion ( location );
            SavingPostInformation ( location );
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
                .findFragmentById ( R.id.map );
        mapFragment.getMapAsync ( this );


        mAuth = FirebaseAuth.getInstance ();
        current_user_id = mAuth.getCurrentUser ().getUid ();
        PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
        UsuarioCarreraRef = FirebaseDatabase.getInstance ().getReference ().child ( "UsuariosCarreras" ).child ( current_user_id ).child ( PostKey );

        UsCarrInformationRef = FirebaseDatabase.getInstance ().getReference ().child ( "UsuariosCarreras" ).child ( current_user_id ).child ( PostKey );
        loadingBar = new ProgressDialog ( this );




  /*      iniciar.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                monitorear=true;
                Monitorear ();
            }

        } );*/
        //
        // SavingPostInformation ();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion ();
    }

    private void agrerarMarcador(double lat, double log) {
        LatLng coordenada = new LatLng ( lat, log );
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom ( coordenada, 16 );
        if (marcador != null) marcador.remove ();
        marcador = mMap.addMarker ( new MarkerOptions ()
                .position ( coordenada )
                .title ( "Mi posision actual" )
                .icon ( BitmapDescriptorFactory.fromResource ( R.mipmap.ic_launcher ) ) );
        mMap.animateCamera ( miUbicacion );
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude ();
            log = location.getLongitude ();
            agrerarMarcador ( lat, log );
        }
    }

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService ( Context.LOCATION_SERVICE );
        Location location = locationManager.getLastKnownLocation ( LocationManager.GPS_PROVIDER );
        actualizarUbicacion ( location );
        locationManager.requestLocationUpdates ( LocationManager.GPS_PROVIDER, 10000, 0, locListener );
    }

    private void SavingPostInformation(final Location locations) {


        //Query latitud = UsuarioCarreraRef.child ( PostKey+cont );
        ls = new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists ()) {
                    countPost = dataSnapshot.getChildrenCount ();
                    String contador = String.valueOf ( countPost );
                    if (countPost > 1) {
                        String cont = String.valueOf ( countPost - 1 );
                        if (dataSnapshot.child ( PostKey + cont ).hasChild ( "latitud" )) {
                            latitudinicial = dataSnapshot.child ( PostKey + cont ).child ( "latitud" ).getValue ().toString ();
                        }
                        if (dataSnapshot.child ( PostKey + cont ).hasChild ( "longitud" )) {
                            longitudinicial = dataSnapshot.child ( PostKey + cont ).child ( "longitud" ).getValue ().toString ();
                        }
                    }
                    Guardar ( locations, contador, latitudinicial, longitudinicial );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        UsuarioCarreraRef.addValueEventListener ( ls );

    }

    private void Guardar(Location location, String cont, String latitudA, String longitudA) {
        lat = location.getLatitude ();
        log = location.getLongitude ();
        vel = location.getSpeed ();
        Location locationA = new Location ( "punto A" );
        locationA.setLatitude ( Double.parseDouble ( latitudA ) );
        locationA.setLongitude ( Double.parseDouble ( longitudA ) );

        float distance = locationA.distanceTo ( location );

        Calendar calFordTime = Calendar.getInstance ();
        SimpleDateFormat currentTime = new SimpleDateFormat ( "HH:mm:ss" );
        saveCurrenTime = currentTime.format ( calFordTime.getTime () );


        String latitud = String.valueOf ( lat );
        String longitud = String.valueOf ( log );
        String velo = String.valueOf ( vel );
        String distancia = String.valueOf ( distance );

        HashMap puntos = new HashMap ();

        puntos.put ( "latitud", latitud );
        puntos.put ( "longitud", longitud );
        puntos.put ( "vel", velo );
        puntos.put ( "hora", saveCurrenTime );
        puntos.put ( "distancia", distancia );
        puntos.put ( "contador", cont );

        UsuarioCarreraRef.removeEventListener ( ls );

        UsCarrInformationRef.child ( PostKey + cont ).updateChildren ( puntos ).addOnCompleteListener ( new OnCompleteListener () {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful ()) {
                    Toast.makeText ( MapsActivity.this, "New Post is Update succesfully..", Toast.LENGTH_SHORT ).show ();
                    loadingBar.dismiss ();
                }
            }
        } );

    }

}
