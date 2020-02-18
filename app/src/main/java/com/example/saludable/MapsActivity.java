package com.example.saludable;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
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
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    double vel = 2.0;
    private String latitudinicial = "0.0";
    double lat = 0.0;
    double log = 0.0;
    private String longitudinicial = "0.0", distanciat = "0.0", velocidadt = "0.0";
    private GoogleMap mMap;
    private Marker marcador;
    private DatabaseReference UsuarioCarreraRef, UsCarrInformationRef, CarreraUserInf, CarreraRef;
    private FirebaseAuth mAuth;
    private String PostKey, current_user_id, saveCurrenTime;
    private long countPost = 1;
    private long contguardada = 0;
    private ProgressDialog loadingBar;
    private Location location;
    private LocationManager locationManager;
    private Button inicio, fin;
    private TextView mensaje;
    private Chronometer cronometro;
    private int kilometro = 0;
    private boolean iniciar = false;
    private String imagencarrera, nombrecarrera, uid, descripcion, estado;

    LocationListener locListener = new LocationListener () {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion ( location );
            if (iniciar == true) {
                SavingInformation ( location );
            }
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

        inicio = findViewById ( R.id.inicio_button );
        fin = findViewById ( R.id.fin_button );
        cronometro = findViewById ( R.id.cronometro );
        mensaje = findViewById ( R.id.monitor_message );

        mAuth = FirebaseAuth.getInstance ();
        current_user_id = mAuth.getCurrentUser ().getUid ();
        PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
        UsuarioCarreraRef = FirebaseDatabase.getInstance ().getReference ().child ( "UsuariosCarreras" ).child ( current_user_id ).child ( PostKey );
        UsCarrInformationRef = FirebaseDatabase.getInstance ().getReference ().child ( "UsuariosCarreras" ).child ( current_user_id ).child ( PostKey );
        CarreraUserInf = FirebaseDatabase.getInstance ().getReference ().child ( "CarrerasRealizadas" );
        CarreraRef = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( PostKey );


        loadingBar = new ProgressDialog ( this );

        inicio.setEnabled ( true );
        fin.setEnabled ( false );

        inicio.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                iniciar = true;
                Monitorear ( true );
            }
        } );
        fin.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                iniciar = false;
                Monitorear ( false );

            }
        } );
        inicio.setVisibility ( View.INVISIBLE );
        fin.setVisibility ( View.INVISIBLE );
        mensaje.setVisibility ( View.INVISIBLE );

        CarreraRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    nombrecarrera = dataSnapshot.child ( "maratonname" ).getValue ().toString ();
                    imagencarrera = dataSnapshot.child ( "maratonimage" ).getValue ().toString ();
                    descripcion = dataSnapshot.child ( "description" ).getValue ().toString ();
                    uid = dataSnapshot.child ( "uid" ).getValue ().toString ();
                    estado = dataSnapshot.child ( "estado" ).getValue ().toString ();
                    if (estado.equals ( "true" )) {
                        Monitorear ( false );
                        if (location != null) SavingInformation ( location );

                        inicio.setVisibility ( View.INVISIBLE );
                        fin.setVisibility ( View.INVISIBLE );
                        mensaje.setVisibility ( View.VISIBLE );
                        mensaje.setText ( "La carrera a terminado. Mire sus datos en sus carreras realizadas" );

                    } else {
                        inicio.setVisibility ( View.VISIBLE );
                        fin.setVisibility ( View.VISIBLE );
                        mensaje.setText ( "" );

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        location = null;
        miUbicacion ();
    }

    private void Monitorear(boolean b) {
        try {
            if (ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (b == true) {
                cronometro.setBase ( SystemClock.elapsedRealtime () );
                cronometro.start ();
                inicio.setEnabled ( false );
                fin.setEnabled ( true );
                miUbicacion ();
                mensaje.setText ( "" );
            } else {
                locationManager.removeUpdates ( locListener );
                inicio.setEnabled ( true );
                fin.setEnabled ( false );
                if (location != null) SavingInformation ( location );
                GuardarInformacionFinal ();
            }
        } catch (Exception e) {
        }
    }

    private void agrerarMarcador(double lat, double log) {
        try {
            LatLng coordenada = new LatLng ( lat, log );

            if (marcador != null) marcador.remove ();
            marcador = mMap.addMarker ( new MarkerOptions ()
                    .position ( coordenada )
                    .title ( "Mi posision actual" )
            );
            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom ( coordenada, 18 );
            CameraPosition cameraPosition = CameraPosition.builder ().
                    target ( coordenada ).zoom ( 17 ).build ();
            mMap.animateCamera ( miUbicacion );
            mMap.moveCamera ( CameraUpdateFactory.newCameraPosition ( cameraPosition ) );
        } catch (Exception e) {
            String error = "ERROR";
        }

    }

    private void actualizarUbicacion(Location location) {
        try {
            if (location != null) {
                lat = location.getLatitude ();
                log = location.getLongitude ();
                agrerarMarcador ( lat, log );
            }
        } catch (Exception e) {
        }
    }

    private void miUbicacion() {
        try {
            if (ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager = (LocationManager) getSystemService ( Context.LOCATION_SERVICE );
            location = locationManager.getLastKnownLocation ( LocationManager.GPS_PROVIDER );
            actualizarUbicacion ( location );
            locationManager.requestLocationUpdates ( LocationManager.GPS_PROVIDER, 20000, 0, locListener );
        } catch (Exception e) {
        }
    }

    private void SavingInformation(final Location locations) {
        try {

            UsuarioCarreraRef.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists ()) {
                        countPost = dataSnapshot.getChildrenCount ();
                        if (countPost > 1) {
                            String cont = String.valueOf ( countPost - 1 );
                            if (dataSnapshot.child ( PostKey + cont ).hasChild ( "latitud" )) {
                                latitudinicial = dataSnapshot.child ( PostKey + cont ).child ( "latitud" ).getValue ().toString ();
                            }
                            if (dataSnapshot.child ( PostKey + cont ).hasChild ( "longitud" )) {
                                longitudinicial = dataSnapshot.child ( PostKey + cont ).child ( "longitud" ).getValue ().toString ();
                            }
                            if (dataSnapshot.child ( PostKey + cont ).hasChild ( "distancia" )) {
                                distanciat = dataSnapshot.child ( PostKey + cont ).child ( "distancia" ).getValue ().toString ();
                            }
                            if (dataSnapshot.child ( PostKey + cont ).hasChild ( "velocidadtotal" )) {
                                velocidadt = dataSnapshot.child ( PostKey + cont ).child ( "velocidadtotal" ).getValue ().toString ();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
            UsuarioCarreraRef.removeEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
            if (countPost > 0) {
                String contador = String.valueOf ( countPost );
                Guardar ( locations, contador, latitudinicial, longitudinicial, distanciat, velocidadt );
            }
        } catch (Exception e) {
        }
    }

    private void Guardar(Location location, String cont, String latitudA, String longitudA, String distanciatA, String velocidadtA) {
        try {

            lat = location.getLatitude ();
            log = location.getLongitude ();
            vel = location.getSpeed ();
            Location locationA = new Location ( "punto A" );
            locationA.setLatitude ( Double.parseDouble ( latitudA ) );
            locationA.setLongitude ( Double.parseDouble ( longitudA ) );

            float dist = (float) Double.parseDouble ( distanciatA );
            float distance = locationA.distanceTo ( location ) + dist;

            float veloct = (float) Double.parseDouble ( velocidadtA );
            float velocidadtotal = (float) (veloct + vel);

            Calendar calFordTime = Calendar.getInstance ();
            SimpleDateFormat currentTime = new SimpleDateFormat ( "HH:mm:ss" );
            saveCurrenTime = currentTime.format ( calFordTime.getTime () );
            String tiempo = cronometro.getText ().toString ();

            if (countPost == 1) {
                distance = (float) 0.0;
                HashMap inscrito = new HashMap ();
                inscrito.put ( "inscrito", "false" );
                UsuarioCarreraRef.updateChildren ( inscrito );
            }

            if (distance > 10) {
                kilometro = kilometro + 1;
                contguardada = countPost - contguardada;
                float velocidadkilometro = velocidadtotal / contguardada;
                GuardarInformacionKilometro ( kilometro, velocidadkilometro, tiempo, saveCurrenTime );
                distance = distance - 10;
                velocidadtotal = (float) vel;
            }

            String latitud = String.valueOf ( lat );
            String longitud = String.valueOf ( log );
            String velo = String.valueOf ( vel );
            String distancia = String.valueOf ( distance );
            String veltot = String.valueOf ( velocidadtotal );

            HashMap puntos = new HashMap ();

            puntos.put ( "latitud", latitud );
            puntos.put ( "longitud", longitud );
            puntos.put ( "velocidad", velo );
            puntos.put ( "hora", saveCurrenTime );
            puntos.put ( "distancia", distancia );
            puntos.put ( "contador", cont );
            puntos.put ( "tiempo", tiempo );
            puntos.put ( "velocidadtotal", veltot );

            UsCarrInformationRef.child ( PostKey + cont ).updateChildren ( puntos ).addOnCompleteListener ( new OnCompleteListener () {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful ()) {
                        Toast.makeText ( MapsActivity.this, "New Post is Update succesfully..", Toast.LENGTH_SHORT ).show ();
                        loadingBar.dismiss ();
                    }
                }
            } );
        } catch (Exception e) {
        }
    }

    private void GuardarInformacionKilometro(int km, float velocid, String tiempokm, String hora) {

        try {

            HashMap user = new HashMap ();
            user.put ( "vel", String.valueOf ( velocid ) );
            user.put ( "time", tiempokm );

            CarreraUserInf.child ( "Carreras" ).child ( PostKey ).child ( String.valueOf ( km ) ).child ( current_user_id ).updateChildren ( user ).addOnCompleteListener ( new OnCompleteListener () {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful ()) {
                        Toast.makeText ( MapsActivity.this, "New Post is Update succesfully..", Toast.LENGTH_SHORT ).show ();
                        loadingBar.dismiss ();
                    }
                }
            } );

            HashMap velocidadusr = new HashMap ();
            velocidadusr.put ( String.valueOf ( km ), String.valueOf ( velocid ) );
            HashMap tiempousr = new HashMap ();
            tiempousr.put ( String.valueOf ( km ), tiempokm );

            CarreraUserInf.child ( "Usuarios" ).child ( current_user_id ).child ( PostKey ).child ( "tiempo" ).updateChildren ( tiempousr ).addOnCompleteListener ( new OnCompleteListener () {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful ()) {
                        Toast.makeText ( MapsActivity.this, "New Post is Update succesfully..", Toast.LENGTH_SHORT ).show ();
                        loadingBar.dismiss ();
                    }
                }
            } );

            CarreraUserInf.child ( "Usuarios" ).child ( current_user_id ).child ( PostKey ).child ( "velocidad" ).updateChildren ( velocidadusr ).addOnCompleteListener ( new OnCompleteListener () {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful ()) {
                        Toast.makeText ( MapsActivity.this, "New Post is Update succesfully..", Toast.LENGTH_SHORT ).show ();
                        loadingBar.dismiss ();
                    }
                }
            } );


        } catch (Exception e) {
        }
    }

    private void GuardarInformacionFinal() {

        UsCarrInformationRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists ()) {
                    if (dataSnapshot.hasChild ( "inscrito" )) {
                        if (dataSnapshot.child ( "inscrito" ).getValue ().toString ().equals ( "false" )) {
                            inicio.setVisibility ( View.INVISIBLE );
                            fin.setVisibility ( View.INVISIBLE );
                            mensaje.setVisibility ( View.VISIBLE );
                            mensaje.setText ( "Usted a finalizado la transmision. Mire sus datos en sus carreras realizadas" );
                        } else {
                            inicio.setVisibility ( View.INVISIBLE );
                            fin.setVisibility ( View.INVISIBLE );
                            mensaje.setVisibility ( View.VISIBLE );
                            mensaje.setText ( "Se produjo un error mientras intentabamos monitorear su participacion. Lamentamos los inconvenientes" );
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        UsCarrInformationRef.removeEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );


        if (location != null) SavingInformation ( location );

        CarreraUserInf.child ( "Usuarios" ).child ( current_user_id ).child ( PostKey ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists ()) {
                    HashMap carrusr = new HashMap ();
                    carrusr.put ( "nombre", nombrecarrera );
                    carrusr.put ( "imagen", imagencarrera );
                    carrusr.put ( "descripcion", descripcion );
                    carrusr.put ( "uid", uid );
                    CarreraUserInf.child ( "Usuarios" ).child ( current_user_id ).child ( PostKey ).updateChildren ( carrusr );
                } else {
                    inicio.setVisibility ( View.INVISIBLE );
                    fin.setVisibility ( View.INVISIBLE );
                    mensaje.setVisibility ( View.VISIBLE );
                    mensaje.setText ( "Se produjo un error mientras intentabamos monitorear su participacion. Lamentamos los inconvenientes" );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );



    }

}

