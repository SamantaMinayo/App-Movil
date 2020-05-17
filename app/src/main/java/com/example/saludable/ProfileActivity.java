package com.example.saludable;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.saludable.Utils.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class ProfileActivity extends AppCompatActivity {


    private TextView userName, userStatus, userFullname, userCountry, userEdad, userPeso, userAltura, userGenero, velpromtot, velpromult, tiempromtot, tiempromult, disttot, distult, userimc, caltot, calult, pastot, pasult, pesotot, pesoult;
    private CircleImageView userProfileImage;
    private Toolbar mToolbar;
    private Button settings;

    private DatabaseReference profileuserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;
    private LineChartView chartcalorias, charttime, chartvelocidad;
    private ArrayList xDato = new ArrayList ();
    private ArrayList yDato = new ArrayList ();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_profile );

            mAuth = FirebaseAuth.getInstance ();
            currentUserId = mAuth.getCurrentUser ().getUid ();
            profileuserRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( currentUserId );

            mToolbar = findViewById ( R.id.mi_perfil_toolbar );
            setSupportActionBar ( mToolbar );
            getSupportActionBar ().setTitle ( "Mi Perfil" );
            getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

            settings = findViewById ( R.id.profile_settings );
            userName = findViewById ( R.id.my_profile_username );
            userimc = findViewById ( R.id.my_profile_IMC );
            userStatus = findViewById ( R.id.my_profile_state );
            userFullname = findViewById ( R.id.my_profile_full_name );
            userCountry = findViewById ( R.id.my_profile_country );
            userEdad = findViewById ( R.id.my_profile_edad );
            userPeso = findViewById ( R.id.my_profile_peso );
            userAltura = findViewById ( R.id.my_profile_altura );
            userGenero = findViewById ( R.id.my_profile_genero );
            velpromtot = findViewById ( R.id.profile_velpromtotal );
            velpromult = findViewById ( R.id.profile_velpromfinal );
            tiempromtot = findViewById ( R.id.profile_tiempopromtotal );
            tiempromult = findViewById ( R.id.profile_tiempopromfinal );
            disttot = findViewById ( R.id.profile_distanciatotal );
            distult = findViewById ( R.id.profile_distanciafinal );
            caltot = findViewById ( R.id.profile_caloriastotal );
            calult = findViewById ( R.id.profile_calorias_ultimo );
            pastot = findViewById ( R.id.profile_pasostotal );
            pasult = findViewById ( R.id.profile_pasosultimo );
            pesotot = findViewById ( R.id.profile_peso_total );
            pesoult = findViewById ( R.id.profile_peso_ultimo );
            chartcalorias = findViewById ( R.id.chartcalprof );
            charttime = findViewById ( R.id.charttimeprof );
            chartvelocidad = findViewById ( R.id.chartvelprof );

            userProfileImage = findViewById ( R.id.my_profile_pic );

            String myProfileImage = Common.loggedUser.getProfileimage ();
            Picasso.with ( ProfileActivity.this ).load ( myProfileImage ).placeholder ( R.drawable.profile ).into ( userProfileImage );
            userName.setText ( "@" + Common.loggedUser.getUsername () );
            userFullname.setText ( Common.loggedUser.getFullname () );
            userStatus.setText ( Common.loggedUser.getStatus () );
            userGenero.setText ( "Genero: " + Common.loggedUser.getGenero () );
            userCountry.setText ( "Country: " + Common.loggedUser.getCountry () );
            userPeso.setText ( "Peso: " + Common.loggedUser.getPeso () );
            userAltura.setText ( "Altura: " + Common.loggedUser.getAltura () );
            userEdad.setText ( "Edad: " + Common.loggedUser.getEdad () );
            userimc.setText ( "IMC: " + Common.loggedUser.getImc () );

            settings.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    SendUserToSettingsActivity ();
                }
            } );

            CargarDatos ();

        } catch (Exception e) {
        }
    }

    private void CargarDatos() {

        profileuserRef.child ( "carreras" ).child ( "velocidad" ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    float vltotal = 0;
                    float carreras = dataSnapshot.getChildrenCount ();
                    xDato = new ArrayList ();
                    yDato = new ArrayList ();
                    xDato.add ( "0" );
                    yDato.add ( Float.parseFloat ( "0.0" ) );
                    String velult = "";
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren ()) {
                        String km = childDataSnapshot.getKey ();
                        float vl = Float.parseFloat ( childDataSnapshot.getValue ().toString () );
                        vltotal = vltotal + vl;
                        xDato.add ( km );
                        yDato.add ( vl );
                        velult = String.valueOf ( vl );
                    }

                    Graficar ( xDato, yDato, chartvelocidad, "Carrera", "Velocidad [m/s]" );
                    if (carreras > 0) {
                        String velpro = String.valueOf ( vltotal / carreras );
                        velpromtot.setText ( velpro );
                        velpromult.setText ( velult );
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        profileuserRef.child ( "carreras" ).child ( "tiempo" ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    xDato = new ArrayList ();
                    yDato = new ArrayList ();
                    xDato.add ( "0" );
                    yDato.add ( Float.parseFloat ( "0.0" ) );
                    float tmtotal = 0;
                    float carreras = dataSnapshot.getChildrenCount ();
                    String timeult = "";

                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren ()) {
                        String km = childDataSnapshot.getKey ();
                        String tm = childDataSnapshot.getValue ().toString ();
                        xDato.add ( km );
                        float tim;
                        String[] tiempo = tm.split ( ":" );
                        if (tiempo.length == 3) {
                            tim = Float.parseFloat ( tiempo[0] ) * 60 + Float.parseFloat ( tiempo[1] ) + Float.parseFloat ( tiempo[2] ) / 60;
                        } else {
                            tim = Float.parseFloat ( tiempo[0] ) + Float.parseFloat ( tiempo[1] ) / 60;
                        }
                        tmtotal = tmtotal + tim;
                        timeult = String.valueOf ( tim );
                        yDato.add ( tim );
                    }
                    if (carreras > 0) {
                        String tmpro = String.valueOf ( tmtotal / carreras );
                        tiempromtot.setText ( tmpro );
                        tiempromult.setText ( timeult );
                    }
                    Graficar ( xDato, yDato, charttime, "Carrera", "Tiempo [min]" );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        profileuserRef.child ( "carreras" ).child ( "calorias" ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    xDato = new ArrayList ();
                    yDato = new ArrayList ();
                    xDato.add ( "0" );
                    yDato.add ( Float.parseFloat ( "0.0" ) );
                    float caltotal = 0;
                    String calultimo = "";

                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren ()) {
                        String km = childDataSnapshot.getKey ();
                        String cal = childDataSnapshot.getValue ().toString ();
                        xDato.add ( km );
                        yDato.add ( Float.parseFloat ( cal ) );
                        caltotal = caltotal + Float.parseFloat ( cal );
                        calultimo = cal;
                    }

                    caltot.setText ( String.valueOf ( caltotal ) );
                    calult.setText ( calultimo );
                    Graficar ( xDato, yDato, chartcalorias, "Carrera", "Calorias [cal]" );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        profileuserRef.child ( "carreras" ).child ( "distancia" ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    String dis = "";
                    float distotal = 0;
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren ()) {
                        dis = childDataSnapshot.getValue ().toString ();
                        distotal = distotal + Float.parseFloat ( dis );
                    }
                    disttot.setText ( String.valueOf ( distotal ) );
                    distult.setText ( dis );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        profileuserRef.child ( "carreras" ).child ( "pasos" ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    String pas = "";
                    float pastotal = 0;
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren ()) {
                        pas = childDataSnapshot.getValue ().toString ();
                        pastotal = pastotal + Float.parseFloat ( pas );
                    }
                    pastot.setText ( String.valueOf ( pastotal ) );
                    pasult.setText ( pas );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        profileuserRef.child ( "carreras" ).child ( "peso" ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    String peso = "";
                    float pesototal = 0;
                    float carreras = dataSnapshot.getChildrenCount ();

                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren ()) {
                        peso = childDataSnapshot.getValue ().toString ();
                        pesototal = pesototal + Float.parseFloat ( peso );
                    }
                    if (carreras > 0) {
                        pesotot.setText ( String.valueOf ( pesototal / carreras ) );
                        pesoult.setText ( peso );
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        ArrayList axisData = new ArrayList ();
        ArrayList yAxisData = new ArrayList ();
        axisData.add ( "0" );
        yAxisData.add ( (float) 0.0 );
        Graficar ( axisData, yAxisData, charttime, "Carrera", "Tiempo [min]" );
        Graficar ( axisData, yAxisData, chartcalorias, "Carrera", "Calorias [cal]" );
        Graficar ( axisData, yAxisData, chartvelocidad, "Carrera", "Velocidad [m/s]" );
    }

    private void Graficar(ArrayList axisData, ArrayList yAxisData, LineChartView chart, String ejex, String ejey) {
        List yAxisValues = new ArrayList ();
        List axisValues = new ArrayList ();

        Line line = new Line ( yAxisValues ).setColor ( Color.parseColor ( "#33FFC3" ) );
        for (int i = 0; i < axisData.size (); i++) {
            axisValues.add ( i, new AxisValue ( i ).setLabel ( (String) axisData.get ( i ) ) );
        }

        for (int i = 0; i < yAxisData.size (); i++) {
            yAxisValues.add ( new PointValue ( i, (Float) yAxisData.get ( i ) ) );
        }
        List lines = new ArrayList ();

        lines.add ( line );

        LineChartData data = new LineChartData ();
        data.setLines ( lines );
        chart.setLineChartData ( data );

        Axis axis = new Axis ();
        axis.setValues ( axisValues );
        data.setAxisXBottom ( axis );
        axis.setName ( ejex );

        Axis yAxis = new Axis ();
        data.setAxisYLeft ( yAxis );

        axis.setTextSize ( 12 );
        axis.setTextColor ( Color.parseColor ( "#03A9F4" ) );
        axis.setHasLines ( true );
        yAxis.setTextColor ( Color.parseColor ( "#03A9F4" ) );
        yAxis.setTextSize ( 12 );
        yAxis.setName ( ejey );
        yAxis.setHasLines ( true );
        Viewport viewport = new Viewport ( chart.getMaximumViewport () );
        chart.setPadding ( 5, 5, 0, 0 );
        chart.setMaximumViewport ( viewport );
        chart.setCurrentViewport ( viewport );
        chart.animate ();
    }

    private void SendUserToSettingsActivity() {
        try {
            Intent loginIntent = new Intent ( ProfileActivity.this, SettingsActivity.class );
            startActivity ( loginIntent );
        } catch (Exception e) {
        }
    }

}
