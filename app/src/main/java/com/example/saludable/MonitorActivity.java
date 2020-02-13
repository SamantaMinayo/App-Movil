package com.example.saludable;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class MonitorActivity extends AppCompatActivity {


    SensorManager sensorManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private DatabaseReference UsuarioCarreraRef, UsCarrInformationRef;
    private FirebaseAuth mAuth;
    private String PostKey, current_user_id, saveCurrenTime;
    private long countPost = 0;
    private ProgressDialog loadingBar;
    private int MY_PERMISSION_REQUEST_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        //setContentView ( R.layout.activity_monitor );

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient ( this );

        mAuth = FirebaseAuth.getInstance ();
        current_user_id = mAuth.getCurrentUser ().getUid ();
        PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
        UsuarioCarreraRef = FirebaseDatabase.getInstance ().getReference ().child ( "UsuariosCarreras" ).child ( current_user_id ).child ( PostKey );

        UsCarrInformationRef = FirebaseDatabase.getInstance ().getReference ().child ( "UsuariosCarreras" ).child ( current_user_id ).child ( PostKey );
        loadingBar = new ProgressDialog ( this );

        sensorManager = (SensorManager) getSystemService ( Context.SENSOR_SERVICE );

        UpLatLongFirebase ();
    }

    @Override
    protected void onResume() {
        super.onResume ();


    }


    @Override
    protected void onPause() {
        super.onPause ();

    }


    private void UpLatLongFirebase() {
        if (ActivityCompat.checkSelfPermission ( this,
                Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission ( this,
                Manifest.permission.ACCESS_COARSE_LOCATION )
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions ( MonitorActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_FINE_LOCATION );
        }


        mFusedLocationClient.getLastLocation ().addOnSuccessListener ( this, new OnSuccessListener<Location> () {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    SavingPostInformation ( location );
                }
            }
        } );
    }

    private void SavingPostInformation(final Location location) {

        UsuarioCarreraRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    countPost = dataSnapshot.getChildrenCount ();

                    String latitud = String.valueOf ( location.getLatitude () );
                    String longitud = String.valueOf ( location.getLongitude () );
                    String vel = String.valueOf ( location.getSpeed () );
                    String time = Long.toString ( location.getTime () );

                    Calendar calFordTime = Calendar.getInstance ();
                    SimpleDateFormat currentTime = new SimpleDateFormat ( "HH:mm:ss" );
                    saveCurrenTime = currentTime.format ( calFordTime.getTime () );

                    HashMap puntos = new HashMap ();
                    String cont = Long.toString ( countPost );

                    puntos.put ( "latitud", latitud );
                    puntos.put ( "longitud", longitud );
                    puntos.put ( "vel", vel );
                    puntos.put ( "time", time );
                    puntos.put ( "hora", saveCurrenTime );
                    puntos.put ( "counter", cont );

                    UsCarrInformationRef.child ( cont + PostKey ).updateChildren ( puntos ).addOnCompleteListener ( new OnCompleteListener () {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful ()) {
                                Toast.makeText ( MonitorActivity.this, "New Post is Update succesfully..", Toast.LENGTH_SHORT ).show ();
                                loadingBar.dismiss ();
                            }
                        }
                    } );
                } else {
                    countPost = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }
}
