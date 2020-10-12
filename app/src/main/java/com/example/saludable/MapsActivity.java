package com.example.saludable;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.saludable.Service.MyService;
import com.example.saludable.Service.Utils;
import com.example.saludable.Utils.Common;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private GoogleMap mMap;
    private Marker marcador;

    private FirebaseAuth mAuth;
    double factorpaso;

    private ProgressDialog loadingBar;
    PowerManager powerManager;
    private Location locationA;
    private DatabaseReference CarreraUserInf, CarreraUserMon,
            RegistrarUsuario, RegistrarResUsuario, CarreraUserInfRes, Carrera, CarreraResInfo;
    private boolean transmision = false;
    private boolean fin = false;
    private String PostKey, current_user_id;
    private double distanciatotal = 0.0;
    private String inicio = "false";
    private double velocidadtotal = 0.0;
    private int contregistro = 0;
    ///////////////////////////
    private String horacarrera;
    private String horainicio;
    private String horainiciofija;
    ///////////////////////////
    private Calendar calFordTime;
    private SimpleDateFormat currentTime;

    private PowerManager.WakeLock wakelock;

    private static final String TAG = MainActivity.class.getSimpleName ();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private MyService mService = null;
    private DecimalFormat formato1;

    // Tracks the bound state of the service.
    private boolean mBound = false;
    private final ServiceConnection mServiceConnection = new ServiceConnection () {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService ();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };
    // UI elements.
    private Button mRequestLocationUpdatesButton;
    private Button mRemoveLocationUpdatesButton;
    private TextView velocidad, distancia, pasos;
    private Chronometer cronometro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate ( savedInstanceState );

        myReceiver = new MyReceiver ();

        setContentView ( R.layout.activity_maps );
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
                .findFragmentById ( R.id.map );
        mapFragment.getMapAsync ( this );

        powerManager = (PowerManager) getSystemService ( POWER_SERVICE );
        wakelock = powerManager.newWakeLock ( PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag" );

        mAuth = FirebaseAuth.getInstance ();
        current_user_id = mAuth.getCurrentUser ().getUid ();
        PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
        //Guardar Datos cada 15 a 20 seg depende del GPS
        CarreraUserInf = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" )
                .child ( "Datos" ).child ( PostKey ).child ( current_user_id );
        //Dato usado para el monitoreo en tiempo real desde el administrador
        CarreraUserMon = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).
                child ( "Monitoreo" ).child ( PostKey );
        //Cuando finalice la carrera el usuario elimina su registro de inscripcion en la carrera y lo envia a realizzada
        RegistrarUsuario = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).
                child ( current_user_id ).child ( "Inscripcion" ).child ( PostKey );
        //Cuando finalice la carrera el usuario elimina su registro de inscripcion en la carrera y lo envia a realizzada
        RegistrarResUsuario = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).
                child ( current_user_id ).child ( "Resultados" ).child ( "Lista" ).child ( PostKey );
        //Guardar resultados en el nodo del usuario
        CarreraUserInfRes = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).
                child ( current_user_id ).child ( "Resultados" ).child ( "Resultado" ).child ( PostKey );
        //Guardar resultados en el nodo resultados por carrera
        CarreraResInfo = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Resultados" ).
                child ( PostKey ).child ( current_user_id );
        //Verificar si la usuario a iniciado o a finalizado.
        Carrera = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).
                child ( "Nuevas" ).child ( PostKey ).child ( "estado" );

        horacarrera = Common.carrera.getMaratontime ();
        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates ( this )) {
            if (!checkPermissions ()) {
                requestPermissions ();
            }
        }

        Carrera.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inicio = (String) dataSnapshot.getValue ();
                if (inicio.equals ( "fin" )) fin = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );

    }

    @Override
    protected void onStart() {
        super.onStart ();
        PreferenceManager.getDefaultSharedPreferences ( this )
                .registerOnSharedPreferenceChangeListener ( this );

        currentTime = new SimpleDateFormat ( "HH:mm:ss" );


        mRequestLocationUpdatesButton = findViewById ( R.id.inicio_button );
        mRemoveLocationUpdatesButton = findViewById ( R.id.fin_button );
        velocidad = findViewById ( R.id.velocidadmap );
        distancia = findViewById ( R.id.distancemap );
        pasos = findViewById ( R.id.pasosmap );
        formato1 = new DecimalFormat ( "#.00" );
        cronometro = findViewById ( R.id.cronometro );
        loadingBar = new ProgressDialog ( this );


        mRequestLocationUpdatesButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (!checkPermissions ()) {
                    requestPermissions ();
                } else {
                    // LocalBroadcastManager.getInstance ( getApplicationContext () ).registerReceiver ( myReceiver,
                    //new IntentFilter ( MyService.ACTION_BROADCAST ) );
                    transmision = true;
                    if (!wakelock.isHeld ()) {
                        wakelock.acquire ();
                    }
                    cronometro.setBase ( SystemClock.elapsedRealtime () );
                    cronometro.start ();
                    calFordTime = Calendar.getInstance ();
                    horainicio = currentTime.format ( calFordTime.getTime () );
                    horainiciofija = currentTime.format ( calFordTime.getTime () );

                    distanciatotal = 0.0;
                    velocidadtotal = 0.0;
                    mService.requestLocationUpdates ();
                }
            }
        } );

        mRemoveLocationUpdatesButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                mService.getLastLocation ();
                fin = true;
                transmision = false;
            }
        } );

        // Restore the state of the buttons when the activity (re)launches.
        setButtonsState ( Utils.requestingLocationUpdates ( this ) );

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService ( new Intent ( this, MyService.class ), mServiceConnection,
                Context.BIND_AUTO_CREATE );
    }

    @Override
    protected void onResume() {
        super.onResume ();
        LocalBroadcastManager.getInstance ( this ).registerReceiver ( myReceiver,
                new IntentFilter ( MyService.ACTION_BROADCAST ) );
    }

    @Override
    protected void onPause() {
        //LocalBroadcastManager.getInstance ( this ).unregisterReceiver ( myReceiver );
        super.onPause ();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService ( mServiceConnection );

            mBound = false;
        }
        //LocalBroadcastManager.getInstance ( this ).unregisterReceiver ( myReceiver );
        PreferenceManager.getDefaultSharedPreferences ( this )
                .unregisterOnSharedPreferenceChangeListener ( this );
        super.onStop ();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy ();
        LocalBroadcastManager.getInstance ( this ).unregisterReceiver ( myReceiver );
        if (wakelock.isHeld ()) {
            wakelock.release ();
        }
        fin = false;
        transmision = false;
        mService.removeLocationUpdates ();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (transmision) {
                new AlertDialog.Builder ( this )
                        .setIcon ( android.R.drawable.ic_dialog_alert )
                        .setTitle ( "Salir" )
                        .setMessage ( "Si fuerza la finalizacion del monitoreo es posible que se pierdan datos. Esta seguro?" )
                        .setNegativeButton ( android.R.string.cancel, null )// sin listener
                        .setPositiveButton ( android.R.string.ok, new DialogInterface.OnClickListener () {// un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fin = false;
                                transmision = false;
                                LocalBroadcastManager.getInstance ( MapsActivity.this ).unregisterReceiver ( myReceiver );
                                mService.removeLocationUpdates ();
                                if (wakelock.isHeld ()) {
                                    wakelock.release ();
                                }
                                MapsActivity.this.finish ();

                            }
                        } )
                        .show ();
                return true;
            } else {
                new AlertDialog.Builder ( this )
                        .setIcon ( android.R.drawable.ic_dialog_alert )
                        .setTitle ( "Salir" )
                        .setMessage ( "Estás seguro?" )
                        .setNegativeButton ( android.R.string.cancel, null )// sin listener
                        .setPositiveButton ( android.R.string.ok, new DialogInterface.OnClickListener () {// un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fin = false;
                                transmision = false;
                                LocalBroadcastManager.getInstance ( MapsActivity.this ).unregisterReceiver ( myReceiver );
                                mService.removeLocationUpdates ();
                                if (wakelock.isHeld ()) {
                                    wakelock.release ();
                                }
                                MapsActivity.this.finish ();

                            }
                        } )
                        .show ();
                return true;
            }

        }
        return super.onKeyDown ( keyCode, event );
    }

    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission ( this,
                Manifest.permission.ACCESS_FINE_LOCATION );
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale ( this,
                        Manifest.permission.ACCESS_FINE_LOCATION );

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i ( TAG, "Displaying permission rationale to provide additional context." );
            Snackbar.make (
                    findViewById ( R.id.activity_maps ),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE )
                    .setAction ( R.string.ok, new View.OnClickListener () {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions ( MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE );
                        }
                    } )
                    .show ();
        } else {
            Log.i ( TAG, "Requesting permission" );
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions ( MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE );
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i ( TAG, "onRequestPermissionResult" );
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i ( TAG, "User interaction was cancelled." );
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                transmision = true;
                if (!wakelock.isHeld ()) {
                    wakelock.acquire ();
                }
                cronometro.setBase ( SystemClock.elapsedRealtime () );
                cronometro.start ();
                distanciatotal = 0.0;
                velocidadtotal = 0.0;
                mService.requestLocationUpdates ();
            } else {
                // Permission denied.
                setButtonsState ( false );
                Snackbar.make (
                        findViewById ( R.id.activity_maps ),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE )
                        .setAction ( R.string.settings, new View.OnClickListener () {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent ();
                                intent.setAction (
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
                                Uri uri = Uri.fromParts ( "package",
                                        BuildConfig.APPLICATION_ID, null );
                                intent.setData ( uri );
                                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK );
                                startActivity ( intent );
                            }
                        } )
                        .show ();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    public void agrerarMarcador(Location location) {
        try {

            inicio = "true";
            LatLng coordenada = new LatLng ( location.getLatitude (), location.getLongitude () );
            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom ( coordenada, 17 );

            if (marcador != null) marcador.remove ();
            marcador = mMap.addMarker ( new MarkerOptions ()
                    .position ( coordenada )
                    .title ( "Mi posision actual" )
            );
            mMap.animateCamera ( miUbicacion );
            //hora
            calFordTime = Calendar.getInstance ();
            String hora = currentTime.format ( calFordTime.getTime () );
            Monitoreo ( location, Common.loggedUser.getUsername (), hora );
            if (transmision && inicio.equals ( "true" )) {
                if (locationA != null) {
                    Polyline line = mMap.addPolyline ( new PolylineOptions ()
                            .add ( new LatLng ( locationA.getLatitude (), locationA.getLongitude () ), new LatLng ( location.getLatitude (), location.getLongitude () ) )
                            .width ( 5 )
                            .color ( Color.BLUE ) );

                    factorpaso = Double.valueOf ( Common.loggedUser.getPaso () );
                    //datos actuales y totales
                    distanciatotal = locationA.distanceTo ( location ) + distanciatotal;
                    String distguar = formato1.format ( distanciatotal );
                    distancia.setText ( distguar + " m" );
                    velocidad.setText ( formato1.format ( location.getSpeed () ) + " m/s" );
                    double distcm = distanciatotal * 100;
                    pasos.setText ( formato1.format ( distcm / factorpaso ) );
                    velocidadtotal = location.getSpeed () + velocidadtotal;

                    locationA.setLatitude ( location.getLatitude () );
                    locationA.setLongitude ( location.getLongitude () );
                    contregistro = contregistro + 1;
                    String tiempo = getDifferenceBetwenDates ( horacarrera, hora );
                    String timet = getDifferenceBetwenDates ( horainicio, hora );
                    horacarrera = hora;
                    horainicio = hora;
                    GuardarInformacion ( location, distguar, hora, tiempo, timet );
                } else {
                    locationA = new Location ( "punto A" );
                    locationA.setLatitude ( location.getLatitude () );
                    locationA.setLongitude ( location.getLongitude () );
                }
            }
            if (inicio == "fin") {
                GuardarFinal ();
            }
        } catch (Exception e) {
            String error = "ERROR";
        }

    }

    private void GuardarFinal() {
        String hora = currentTime.format ( calFordTime.getTime () );
        String tiempo = getDifferenceBetwenDates ( horacarrera, hora );
        String tiempot;
        if (horainicio == null) {
            tiempot = "0:0:0";
        } else {
            tiempot = getDifferenceBetwenDates ( horainicio, hora );
        }
        String[] fin = tiempo.split ( ":" );
        String[] fint = tiempot.split ( ":" );
        double min = Integer.parseInt ( fin[0] ) * 60 + Integer.parseInt ( fin[1] ) + Integer.parseInt ( fin[2] ) / 60;
        double mint = Integer.parseInt ( fint[0] ) * 60 + Integer.parseInt ( fint[1] ) + Integer.parseInt ( fint[2] ) / 60;

        calFordTime = Calendar.getInstance ();
        tiempo = getDifferenceBetwenDates ( Common.carrera.getMaratontime (), hora );
        fin = tiempo.split ( ":" );
        min = Integer.parseInt ( fin[0] ) * 60 + Integer.parseInt ( fin[1] ) + Integer.parseInt ( fin[2] ) / 60;
        if (horainiciofija == null) {
            tiempot = "0:0:0";
        } else {
            tiempot = getDifferenceBetwenDates ( horainiciofija, hora );
        }
        fint = tiempot.split ( ":" );
        mint = Integer.parseInt ( fint[0] ) * 60 + Integer.parseInt ( fint[1] ) + Integer.parseInt ( fint[2] ) / 60;

        String calorias = formato1.format ( 8 * min * 0.0175 * Double.valueOf ( Common.loggedUser.getPeso () ) );

        LocalBroadcastManager.getInstance ( getApplicationContext () ).unregisterReceiver ( myReceiver );
        transmision = false;
        if (wakelock.isHeld ()) {
            wakelock.release ();
        }
        mService.removeLocationUpdates ();


        String pas = formato1.format ( (distanciatotal * 100) / factorpaso );
        String velmed = formato1.format ( velocidadtotal / contregistro );
        String velprom = formato1.format ( distanciatotal / (min * 60) );
        String velpromt = formato1.format ( distanciatotal / (mint * 60) );
        HashMap puntos = new HashMap ();

        puntos.put ( "distancia", formato1.format ( distanciatotal ) );
        puntos.put ( "velmed", velmed );
        puntos.put ( "velocidad", velprom );
        puntos.put ( "velocidadmed", velpromt );
        puntos.put ( "tiempo", formato1.format ( min ) );
        puntos.put ( "tiempomed", formato1.format ( mint ) );
        puntos.put ( "calorias", calorias );
        puntos.put ( "pasos", pas );
        puntos.put ( "genero", Common.loggedUser.getGenero () );
        puntos.put ( "rango", Common.loggedUser.getRango () );
        puntos.put ( "usuario", Common.loggedUser.getUsername () );
        puntos.put ( "uid", current_user_id );
        CarreraResInfo.updateChildren ( puntos );
        CarreraUserInfRes.updateChildren ( puntos );
        GuardarResList ();
    }

    private void GuardarInformacion(Location location, String distguar, String hora, String tiempo, String timet) {
        HashMap puntos = new HashMap ();

        puntos.put ( "latitud", String.valueOf ( location.getLatitude () ) );
        puntos.put ( "longitud", String.valueOf ( location.getLongitude () ) );
        puntos.put ( "velocidad", String.valueOf ( location.getSpeed () ) );
        puntos.put ( "hora", hora );
        puntos.put ( "distancia", distguar );
        puntos.put ( "tiempo", tiempo );
        puntos.put ( "timp", timet );

        CarreraUserInf.child ( String.valueOf ( contregistro ) ).updateChildren ( puntos );
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals ( Utils.KEY_REQUESTING_LOCATION_UPDATES )) {
            setButtonsState ( sharedPreferences.getBoolean ( Utils.KEY_REQUESTING_LOCATION_UPDATES,
                    false ) );
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        if (!fin) {
            if (requestingLocationUpdates) {
                mRequestLocationUpdatesButton.setEnabled ( false );
                mRemoveLocationUpdatesButton.setEnabled ( true );
            } else {
                mRequestLocationUpdatesButton.setEnabled ( true );
                mRemoveLocationUpdatesButton.setEnabled ( false );
            }
        } else {
            mRequestLocationUpdatesButton.setEnabled ( false );
            mRemoveLocationUpdatesButton.setEnabled ( false );
        }

    }

    private void Monitoreo(Location location, String username, String hora) {

        HashMap puntos = new HashMap ();

        puntos.put ( "latitud", String.valueOf ( location.getLatitude () ) );
        puntos.put ( "longitud", String.valueOf ( location.getLongitude () ) );
        puntos.put ( "usuario", username );
        puntos.put ( "hora", hora );

        CarreraUserMon.child ( current_user_id ).updateChildren ( puntos );
    }

    private void GuardarResList() {
        try {
            RegistrarUsuario.removeValue ();

            HashMap crear = new HashMap ();
            crear.put ( "maratonname", Common.carrera.maratonname );
            crear.put ( "date", Common.carrera.maratondate + " : " + Common.carrera.maratontime );
            crear.put ( "maratonimagen", Common.carrera.maratonimage );
            crear.put ( "maratondescription", Common.carrera.description );
            crear.put ( "uid", Common.carrera.uid );
            RegistrarResUsuario.updateChildren ( crear );
        } catch (Exception e) {
        }
    }

    public String getDifferenceBetwenDates(String dateInicio, String dateFinal) {
        String[] inicio = dateInicio.split ( ":" );
        String[] fin = dateFinal.split ( ":" );
        int start = Integer.parseInt ( inicio[0] ) * 3600 + Integer.parseInt ( inicio[1] ) * 60 + Integer.parseInt ( inicio[2] );
        int end = Integer.parseInt ( fin[0] ) * 3600 + Integer.parseInt ( fin[1] ) * 60 + Integer.parseInt ( fin[2] );
        int result = end - start;
        int hour = result / 3600;
        int minu = (result % 3600) / 60;
        int segu = ((result % 3600) % 60) % 60;
        return hour + ":" + minu + ":" + segu;
    }

    /**
     * Receiver for broadcasts sent by {@link MyService}.
     */
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Location location = intent.getParcelableExtra ( MyService.EXTRA_LOCATION );
            if (location != null) {
                agrerarMarcador ( location );
                if (fin) GuardarFinal ();
            }
        }
    }


}

