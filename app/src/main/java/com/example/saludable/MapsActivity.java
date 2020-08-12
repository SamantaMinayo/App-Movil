package com.example.saludable;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.saludable.Service.MyService;
import com.example.saludable.Service.Utils;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    double vel = 2.0;
    private String latitudinicial = "0.0";
    double lat = 0.0;
    double log = 0.0;
    private String longitudinicial = "0.0", distanciat = "0.0", velocidadt = "0.0";
    private GoogleMap mMap;
    private Marker marcador;
    private DatabaseReference UsuarioCarreraRef, UsCarrInformationRef, CarreraUserInf, CarreraRef, RegistrarUsuario;
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
    PowerManager powerManager;
    PowerManager.WakeLock wakelock;
    private Toolbar mToolbar;

    private final int REQUEST_ACCESS_FINE = 0;

    private static final String TAG = MainActivity.class.getSimpleName ();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private MyService mService = null;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate ( savedInstanceState );

        myReceiver = new MyReceiver ();

        setContentView ( R.layout.activity_maps );
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
                .findFragmentById ( R.id.map );
        mapFragment.getMapAsync ( this );


        mAuth = FirebaseAuth.getInstance ();
        current_user_id = mAuth.getCurrentUser ().getUid ();
        PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
        UsuarioCarreraRef = FirebaseDatabase.getInstance ().getReference ().child ( "UsuariosCarreras" ).child ( current_user_id ).child ( PostKey );
        UsCarrInformationRef = FirebaseDatabase.getInstance ().getReference ().child ( "UsuariosCarreras" ).child ( current_user_id ).child ( PostKey );
        CarreraUserInf = FirebaseDatabase.getInstance ().getReference ().child ( "CarrerasRealizadas" );
        CarreraRef = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( PostKey );
        RegistrarUsuario = FirebaseDatabase.getInstance ().getReference ().child ( "UsuariosCarreras" ).child ( current_user_id ).child ( PostKey );
        CarreraRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    nombrecarrera = dataSnapshot.child ( "maratonname" ).getValue ().toString ();
                    imagencarrera = dataSnapshot.child ( "maratonimage" ).getValue ().toString ();
                    descripcion = dataSnapshot.child ( "description" ).getValue ().toString ();
                    uid = dataSnapshot.child ( "uid" ).getValue ().toString ();
                    estado = dataSnapshot.child ( "estado" ).getValue ().toString ();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates ( this )) {
            if (!checkPermissions ()) {
                requestPermissions ();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart ();
        PreferenceManager.getDefaultSharedPreferences ( this )
                .registerOnSharedPreferenceChangeListener ( this );

        mRequestLocationUpdatesButton = findViewById ( R.id.inicio_button );
        mRemoveLocationUpdatesButton = findViewById ( R.id.fin_button );

        mRequestLocationUpdatesButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (!checkPermissions ()) {
                    requestPermissions ();
                } else {
                    mService.requestLocationUpdates ();
                }
            }
        } );

        mRemoveLocationUpdatesButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                mService.removeLocationUpdates ();
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
        LocalBroadcastManager.getInstance ( this ).unregisterReceiver ( myReceiver );
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
        PreferenceManager.getDefaultSharedPreferences ( this )
                .unregisterOnSharedPreferenceChangeListener ( this );
        super.onStop ();
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

            if ((latitudA == "0.0") && (longitudA == "0.0")) {

            } else {
                Polyline line = mMap.addPolyline ( new PolylineOptions ()
                        .add ( new LatLng ( locationA.getLatitude (), locationA.getLongitude () ), new LatLng ( location.getLatitude (), location.getLongitude () ) )
                        .width ( 5 )
                        .color ( Color.RED ) );
            }



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
                if (contguardada == 0) {
                    contguardada = 1;
                }
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
                            EliminarRegistros ();
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
                    EliminarRegistros ();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    public void EliminarRegistros() {
        loadingBar.setTitle ( "Cancelar Inscripcion" );
        loadingBar.setMessage ( "Espere mientras cancelamos su inscripcion en la carrera." );
        loadingBar.setCanceledOnTouchOutside ( true );
        loadingBar.show ();
        RegistrarUsuario.removeValue ();
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

    public void agrerarMarcador(double lat, double log) {
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals ( Utils.KEY_REQUESTING_LOCATION_UPDATES )) {
            setButtonsState ( sharedPreferences.getBoolean ( Utils.KEY_REQUESTING_LOCATION_UPDATES,
                    false ) );
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            mRequestLocationUpdatesButton.setEnabled ( false );
            mRemoveLocationUpdatesButton.setEnabled ( true );
        } else {
            mRequestLocationUpdatesButton.setEnabled ( true );
            mRemoveLocationUpdatesButton.setEnabled ( false );
        }
    }

    /**
     * Receiver for broadcasts sent by {@link MyService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra ( MyService.EXTRA_LOCATION );
            if (location != null) {
                Toast.makeText ( MapsActivity.this, Utils.getLocationText ( location ),
                        Toast.LENGTH_SHORT ).show ();
                agrerarMarcador ( location.getLatitude (), location.getLongitude () );
            }
        }
    }
}

