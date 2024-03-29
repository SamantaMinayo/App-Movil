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
import android.os.Build;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.saludable.Model.Maraton;
import com.example.saludable.Model.MiResultado;
import com.example.saludable.Model.Punto;
import com.example.saludable.Service.MyService;
import com.example.saludable.Service.Utils;
import com.example.saludable.Utils.Common;
import com.example.saludable.localdatabase.DaoPuntos;
import com.example.saludable.localdatabase.DaoResultados;
import com.example.saludable.localdatabase.DaoUsrMrtn;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private GoogleMap mMap;

    private FirebaseAuth mAuth;
    double factorpaso;

    private ProgressDialog loadingBar;
    PowerManager powerManager;
    private Location locationA;
    private DatabaseReference CarreraUserInf, CarreraUserMon,CarreraUserMonKil,
            RegistrarUsuario, RegistrarResUsuario, CarreraUserInfRes, Carrera, CarreraResInfo;
    private boolean transmision = false;
    private boolean fin = false;
    private String PostKey, current_user_id;
    private double distanciatotal = 0.0;
    private String inicio = "false";
    private double velocidadtotal = 0.0;
    private double velocidadmaxima = 0.5;
    private double velocidadminima = 2;
    private double kilometro=0;
    private int nkilometro=0;
    private HashMap anterior;
    private String ultimahora="";

    private int contregistro = 0;
    ///////////////////////////
    private String horacarrera;
    private String horainicio;
    private String horainiciofija;
    private String hanterior;
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
    private DaoUsrMrtn usrMrtn;

    private DaoResultados daoResultados;
    private DaoPuntos daoPuntos;
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
    private ArrayList<MiResultado> miresultado = new ArrayList<MiResultado> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );

            myReceiver = new MyReceiver ();

            setContentView ( R.layout.activity_maps );
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
                    .findFragmentById ( R.id.map );
            mapFragment.getMapAsync ( this );

            powerManager = (PowerManager) getSystemService ( POWER_SERVICE );
            wakelock = powerManager.newWakeLock ( PowerManager.PARTIAL_WAKE_LOCK,
                    "MyApp::MyWakelockTag" );

            usrMrtn = new DaoUsrMrtn ( this );
            mAuth = FirebaseAuth.getInstance ();
            current_user_id = mAuth.getCurrentUser ().getUid ();
            PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
            CarreraUserInf = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" )
                    .child ( "Datos" ).child ( PostKey ).child ( current_user_id );
            CarreraUserMon = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).
                    child ( "Monitoreo" ).child ( PostKey );
            CarreraUserMonKil = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).
                    child ( "Kilometro" ).child ( PostKey );

            RegistrarUsuario = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).
                    child ( current_user_id ).child ( "Inscripcion" ).child ( PostKey );
            RegistrarResUsuario = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).
                    child ( current_user_id ).child ( "Resultados" ).child ( "Lista" ).child ( PostKey );
            CarreraUserInfRes = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).
                    child ( current_user_id ).child ( "Resultados" ).child ( "Resultado" ).child ( PostKey );
            CarreraResInfo = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Resultados" ).
                    child ( PostKey ).child ( current_user_id );
            Carrera = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).
                    child ( "Nuevas" ).child ( PostKey ).child ( "inicio" );

            horacarrera = Common.carrera.getMaratontime ();
            daoResultados = new DaoResultados ( this );
            daoPuntos = new DaoPuntos ( this );
                if (!checkPermissions ()) {
                    requestPermissions ();
                }
            Carrera.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    inicio = (String) dataSnapshot.getValue ();
                    if (inicio.equals ( "fin" )) {
                        fin = true;
                        long crofinal = cronometro.getBase ();
                        cronometro.stop ();
                        cronometro.setBase ( crofinal );
                    } else if (inicio.equals ( "true" )) {
                        //cronometro.stop ();
                        cronometro.setBase ( SystemClock.elapsedRealtime () );
                        cronometro.start ();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            } );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "onCreate" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    @Override
    protected void onStart() {
        try {
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
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    if (!checkPermissions ()) {
                        requestPermissions ();
                    } else {
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
                    long crofinal = cronometro.getBase ();
                    cronometro.stop ();
                    cronometro.setBase ( crofinal );

                }
            } );

            setButtonsState ( Utils.requestingLocationUpdates ( this ) );

            bindService ( new Intent ( this, MyService.class ), mServiceConnection,
                    Context.BIND_AUTO_CREATE );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "onStart" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume ();
            LocalBroadcastManager.getInstance ( this ).registerReceiver ( myReceiver,
                    new IntentFilter ( MyService.ACTION_BROADCAST ) );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "onResume" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    @Override
    protected void onPause() {
        //LocalBroadcastManager.getInstance ( this ).unregisterReceiver ( myReceiver );
        super.onPause ();
    }

    @Override
    protected void onStop() {
        try {
            if (mBound) {
                unbindService ( mServiceConnection );
                mBound = false;
            }
            PreferenceManager.getDefaultSharedPreferences ( this )
                    .unregisterOnSharedPreferenceChangeListener ( this );
            super.onStop ();
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "onStop" ).child ( current_user_id ).updateChildren ( error );
        }
    }


    @Override
    protected void onDestroy() {
        try {
            super.onDestroy ();
            LocalBroadcastManager.getInstance ( this ).unregisterReceiver ( myReceiver );
            if (wakelock.isHeld ()) {
                wakelock.release ();
            }
            fin = false;
            transmision = false;
            mService.removeLocationUpdates ();
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "onDestroy" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
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

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "onKeyDown" ).child ( current_user_id ).updateChildren ( error );
            return false;
        }
    }

    private boolean checkPermissions() {
        try {
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission ( this,
                    Manifest.permission.ACCESS_FINE_LOCATION );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "checkPermissions" ).child ( current_user_id ).updateChildren ( error );
            return false;
        }
    }

    private void requestPermissions() {
        try {
            boolean shouldProvideRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale ( this,
                            Manifest.permission.ACCESS_FINE_LOCATION );
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
                ActivityCompat.requestPermissions ( MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS_REQUEST_CODE );
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "requestPermissions" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        try {
            Log.i ( TAG, "onRequestPermissionResult" );
            if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
                if (grantResults.length <= 0) {
                    Log.i ( TAG, "User interaction was cancelled." );
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mService.getLocation ();

                } else {
                    setButtonsState ( false );
                    Snackbar.make (
                            findViewById ( R.id.activity_maps ),
                            R.string.permission_denied_explanation,
                            Snackbar.LENGTH_INDEFINITE )
                            .setAction ( R.string.settings, new View.OnClickListener () {
                                @Override
                                public void onClick(View view) {
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

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "onRequestPermissionsResult" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "OnMapReady" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    public void agrerarMarcador(Location location) {
        try {
            LatLng coordenada = new LatLng ( location.getLatitude (), location.getLongitude () );
            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom ( coordenada, 15 );

            mMap.animateCamera ( miUbicacion );
            mMap.setMyLocationEnabled ( true );
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
                    kilometro=kilometro+locationA.distanceTo ( location ) ;
                    if(kilometro>=10){
                        nkilometro=nkilometro+1;
                        kilometro=0;
                        GuardarKilometro(nkilometro,location,anterior,ultimahora);
                    }
                    String distguar = formato1.format ( distanciatotal );

                    distancia.setText ( distguar + " m" );
                    velocidad.setText ( formato1.format ( location.getSpeed () ) + " m/s" );
                    double distcm = distanciatotal * 100;
                    pasos.setText ( formato1.format ( distcm / factorpaso ) );
                    velocidadtotal = location.getSpeed () + velocidadtotal;

                    locationA = location;
                    contregistro = contregistro + 1;
                    String tiempo = getDifferenceBetwenDates ( horacarrera, hora );
                    String timet = getDifferenceBetwenDates ( horainicio, hora );
                    horacarrera = hora;
                    horainicio = hora;
                    hanterior=hora;
                    GuardarInformacion ( location, distguar, hora, tiempo, timet );
                } else {
                    locationA = new Location ( "punto A" );
                    locationA.setLatitude ( location.getLatitude () );
                    locationA.setLongitude ( location.getLongitude () );
                }
            }
            if (inicio == "fin") {
                GuardarFinal ();
                nkilometro=nkilometro+1;
                GuardarKilometro(nkilometro,location,anterior,ultimahora);
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "AgregarMarcador" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    private void GuardarFinal() {
        try {
            String hora = currentTime.format ( calFordTime.getTime () );
            String tiempo = getDifferenceBetwenDates ( horacarrera , hora );
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

            String calorias = formato1.format ( 8 * mint * 0.0175 * Double.valueOf ( Common.loggedUser.getPeso () ) );

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
            puntos.put ( "velocidadmed", velprom );
            puntos.put ( "velocidad", velpromt );
            puntos.put ( "tiempomed", formato1.format ( min ) );
            puntos.put ( "tiempo", formato1.format ( mint ) );
            puntos.put ( "calorias", calorias );
            puntos.put ( "pasos", pas );
            puntos.put ( "genero", Common.loggedUser.getGenero () );
            puntos.put ( "rango", Common.loggedUser.getRango () );
            puntos.put ( "velmax", formato1.format ( velocidadmaxima ) );
            puntos.put ( "velmin", formato1.format ( velocidadminima ) );
            puntos.put ( "usuario", Common.loggedUser.getUsername () );
            puntos.put ( "uid", current_user_id );

            MiResultado nuevo = new MiResultado ( PostKey, formato1.format ( mint ), formato1.format ( distanciatotal ), pas, formato1.format ( velocidadmaxima ), velmed, formato1.format ( velocidadminima ), calorias );
            daoResultados.Insert ( nuevo );

            if (distanciatotal > 10 && mint > 0) {
                CarreraResInfo.updateChildren ( puntos );
                CarreraUserInfRes.updateChildren ( puntos );
                GuardarResList ();
            } else {
                daoPuntos.Eliminar ( PostKey );
                CarreraUserInf.removeValue ();
                Toast.makeText ( this, "Ha ocurrido un error. No se ha podido guardar informacion de la carrera para este usuario", Toast.LENGTH_SHORT ).show ();
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "GuardarFinal" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    private void GuardarKilometro(int nkilometro,Location location,HashMap anterior, String iniciocarrera) {
        try {
            String  hora = currentTime.format ( calFordTime.getTime () );
            String hiniciocarrera=iniciocarrera;
            if(hiniciocarrera==""){
                hiniciocarrera =horainiciofija;
            }
            String tiempo = getDifferenceBetwenDates (  hiniciocarrera,hora );
            String stempo= getDifferenceBetwenDates ( (String) anterior.get("hora"),hora);
            double distancedif=distanciatotal-Double.valueOf((String) anterior.get("distancia"));
            double distfalt= nkilometro*10-Double.valueOf((String) anterior.get("distancia"));
            String[] stempos=stempo.split(":");
            int segundosdif= Integer.parseInt(stempos[0])*3600+Integer.parseInt(stempos[1])*60+Integer.parseInt(stempos[2]);
            String[] stiempos=tiempo.split(":");
            int segundostot= Integer.parseInt(stiempos[0])*3600+Integer.parseInt(stiempos[1])*60+Integer.parseInt(stiempos[2]);
            double tiempototal=segundostot+segundosdif*distfalt/distancedif;
            int hours = (int) tiempototal / 3600;
            int remainder = (int) tiempototal - hours * 3600;
            int mins = remainder / 60;
            remainder = remainder - mins * 60;
            int secs = remainder;
            String tkil=hours+":"+mins+":"+secs;
            double mintotale=tiempototal/60;
            double velocidad=1000/segundostot;
            String kcalorias = formato1.format ( 8 * mintotale * 0.0175 * Double.valueOf ( Common.loggedUser.getPeso () ) );
            String kpas = formato1.format ( (1000 * 100) / factorpaso );
            HashMap puntos = new HashMap ();
            puntos.put("distancia", String.valueOf ( nkilometro*1000 ) );
            puntos.put("tiempo",tkil);
            puntos.put("velocidad",String.valueOf (velocidad));
            puntos.put("calorias",String.valueOf (kcalorias));
            puntos.put("pasos",String.valueOf (kpas));
            String[] hanterior=((String) anterior.get("hora")).split(":");
            double newseconds= Integer.parseInt(hanterior[2])+segundosdif*distfalt/distancedif;
            int hoursdos = (int) newseconds / 3600;
            int remainderdos = (int) newseconds - hoursdos * 3600;
            int minsdos = remainderdos / 60;
            remainderdos = remainderdos - minsdos * 60;
            int secsdos = remainderdos;

            puntos.put("hora",String.valueOf(hoursdos)+":"+String.valueOf(minsdos)+":"+secsdos);

            ultimahora=String.valueOf(hoursdos)+":"+String.valueOf(minsdos)+":"+secsdos;
            CarreraUserMonKil.child(String.valueOf(nkilometro)).child ( current_user_id ).updateChildren ( puntos );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "GuardarKilometro" ).child ( current_user_id ).updateChildren ( error );
        }
    }
    private void GuardarInformacion(Location location, String distguar, String hora, String tiempo, String timet) {
        try {
            if (!tiempo.equals ( "0:0:0" )) {
                HashMap puntos = new HashMap ();
                if (location.getSpeed () > velocidadmaxima) {
                    velocidadmaxima = location.getSpeed ();
                }
                if (location.getSpeed () < velocidadminima) {
                    if (location.getSpeed () < 0.001) {
                        puntos.put ( "velocidad", "0.001" );
                    } else {
                        velocidadminima = location.getSpeed ();
                    }
                }
                puntos.put ( "latitud", String.valueOf ( location.getLatitude () ) );
                puntos.put ( "longitud", String.valueOf ( location.getLongitude () ) );
                if (location.getSpeed () < 0.001) {
                    puntos.put ( "velocidad", "0.001" );
                } else {
                    puntos.put ( "velocidad", String.valueOf ( location.getSpeed () ) );
                }
                puntos.put ( "hora", hora );
                puntos.put ( "distancia", distguar );
                puntos.put ( "tiempo", tiempo );
                puntos.put ( "timp", timet );
                anterior=puntos;
                daoPuntos.Insert ( new Punto ( 1, String.valueOf ( contregistro ), PostKey, distguar, hora, String.valueOf ( location.getLatitude () ),
                        String.valueOf ( location.getLongitude () ), tiempo, timet, String.valueOf ( location.getSpeed () ) ) );
                CarreraUserInf.child ( String.valueOf ( contregistro ) ).updateChildren ( puntos );
            }

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "GuardarInfromacion" ).child ( current_user_id ).updateChildren ( error );

        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        try {
            if (s.equals ( Utils.KEY_REQUESTING_LOCATION_UPDATES )) {
                setButtonsState ( sharedPreferences.getBoolean ( Utils.KEY_REQUESTING_LOCATION_UPDATES,
                        false ) );
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "onSharedPreferenceChanged" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        try {
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
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "SetButtonState" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    private void Monitoreo(Location location, String username, String hora) {
        try {
            HashMap puntos = new HashMap ();

            puntos.put ( "latitud", String.valueOf ( location.getLatitude () ) );
            puntos.put ( "longitud", String.valueOf ( location.getLongitude () ) );
            puntos.put ( "usuario", username );
            puntos.put ( "hora", hora );

            CarreraUserMon.child ( current_user_id ).updateChildren ( puntos );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "Monitoreo" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    private void GuardarResList() {
        try {
            Maraton usrM = usrMrtn.Obtener ( PostKey );
            if (usrM == null) {
                usrM = new Maraton ( 1, PostKey, "fin" );
                usrMrtn.Insert ( usrM );
            } else {
                usrM = new Maraton ( 1, PostKey, "fin" );
                usrMrtn.Editar ( usrM );
            }
            RegistrarUsuario.removeValue ();

            HashMap crear = new HashMap ();
            crear.put ( "maratonname", Common.carrera.maratonname );
            crear.put ( "maratondate", Common.carrera.maratondate + " : " + Common.carrera.maratontime );
            crear.put ( "maratonimage", Common.carrera.maratonimage );
            crear.put ( "description", Common.carrera.description );
            crear.put ( "uid", Common.carrera.uid );
            RegistrarResUsuario.updateChildren ( crear );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "GuardarReList" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    public String getDifferenceBetwenDates(String dateInicio, String dateFinal) {
        try {
            String[] inicio = dateInicio.split ( ":" );
            String[] fin = dateFinal.split ( ":" );
            int start = Integer.parseInt ( inicio[0] ) * 3600 + Integer.parseInt ( inicio[1] ) * 60 + Integer.parseInt ( inicio[2] );
            int end = Integer.parseInt ( fin[0] ) * 3600 + Integer.parseInt ( fin[1] ) * 60 + Integer.parseInt ( fin[2] );
            int result = end - start;
            int hour = result / 3600;
            int minu = (result % 3600) / 60;
            int segu = ((result % 3600) % 60) % 60;
            return hour + ":" + minu + ":" + segu;
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "getDifferenceBetwenDates" ).child ( current_user_id ).updateChildren ( error );
            return "";
        }
    }

    /**
     * Receiver for broadcasts sent by {@link MyService}.
     */
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Location location = intent.getParcelableExtra ( MyService.EXTRA_LOCATION );
                if (location != null) {
                    agrerarMarcador ( location );
                    if (fin) {
                        GuardarFinal ();
                        nkilometro=nkilometro+1;
                        GuardarKilometro(nkilometro,location,anterior,ultimahora);
                    }
                }
            } catch (Exception e) {
                HashMap error = new HashMap ();
                error.put ( "error", e.getMessage () );
                FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MapaActivity" ).child ( "OnReceive" ).child ( current_user_id ).updateChildren ( error );
            }
        }
    }
}

