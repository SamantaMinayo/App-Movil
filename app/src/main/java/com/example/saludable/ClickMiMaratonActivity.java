package com.example.saludable;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class ClickMiMaratonActivity extends AppCompatActivity {

    private LineChartView chartvel, charttime;
    private String PostKey, current_user_id;
    private DatabaseReference CarreraUserInf, CarreraInf;
    private FirebaseAuth mAuth;
    private ArrayList xDato = new ArrayList ();
    private ArrayList yDato = new ArrayList ();
    private ArrayList yDaton = new ArrayList ();
    private ArrayList yDatom = new ArrayList ();


    private ArrayList yDatos = new ArrayList ();
    private Toolbar mToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_click_mi_maraton );

        chartvel = findViewById ( R.id.chartvel );
        charttime = findViewById ( R.id.charttime );

        PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
        mAuth = FirebaseAuth.getInstance ();
        current_user_id = mAuth.getCurrentUser ().getUid ();

        mToolbar = findViewById ( R.id.mi_carrera_toolbar );
        setSupportActionBar ( mToolbar );
        getSupportActionBar ().setTitle ( "Mi Carrera" );
        getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

        CarreraUserInf = FirebaseDatabase.getInstance ().getReference ().child ( "CarrerasRealizadas" ).child ( "Usuarios" ).child ( current_user_id ).child ( PostKey );
        CarreraInf = FirebaseDatabase.getInstance ().getReference ().child ( "CarrerasRealizadas" ).child ( "Carreras" ).child ( PostKey );

        CarreraUserInf.child ( "tiempo" ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                xDato = new ArrayList ();
                yDaton = new ArrayList ();
                xDato.add ( "0" );
                yDaton.add ( Float.parseFloat ( "0.0" ) );

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren ()) {
                    String km = String.valueOf ( Float.parseFloat ( childDataSnapshot.getKey () ) / 2 );
                    String tm = childDataSnapshot.getValue ().toString ();
                    xDato.add ( km );
                    float tim;
                    String[] tiempo = tm.split ( ":" );
                    if (tiempo.length == 3) {
                        tim = Float.parseFloat ( tiempo[0] ) * 60 + Float.parseFloat ( tiempo[1] ) + Float.parseFloat ( tiempo[2] ) / 60;
                    } else {
                        tim = Float.parseFloat ( tiempo[0] ) + Float.parseFloat ( tiempo[1] ) / 60;
                    }

                    yDaton.add ( tim );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        CarreraUserInf.child ( "velocidad" ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                xDato = new ArrayList ();
                yDatom = new ArrayList ();
                xDato.add ( "0" );
                yDatom.add ( Float.parseFloat ( "0.0" ) );
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren ()) {
                    String km = String.valueOf ( Float.parseFloat ( childDataSnapshot.getKey () ) / 2 );
                    String vl = childDataSnapshot.getValue ().toString ();
                    xDato.add ( km );
                    yDatom.add ( Float.parseFloat ( vl ) );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        CarreraInf.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                xDato = new ArrayList ();
                yDato = new ArrayList ();
                yDatos = new ArrayList ();
                xDato.add ( "0" );

                yDato.add ( Float.parseFloat ( "0.0" ) );
                yDatos.add ( Float.parseFloat ( "0.0" ) );

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren ()) {
                    float participante = childDataSnapshot.getChildrenCount ();

                    String id = childDataSnapshot.getKey ();
                    xDato.add ( id );
                    float timet = (float) 0.0;
                    float veltot = (float) 0.0;
                    for (DataSnapshot childDataSnapshotchild : dataSnapshot.child ( id ).getChildren ()) {
                        float timep = (float) 0.0;
                        String tim = (String) childDataSnapshotchild.child ( "time" ).getValue ();
                        float velp = Float.parseFloat ( (String) childDataSnapshotchild.child ( "vel" ).getValue () );

                        String[] tiempo = tim.split ( ":" );
                        if (tiempo.length == 3) {
                            timep = Float.parseFloat ( tiempo[0] ) * 60 + Float.parseFloat ( tiempo[1] ) + Float.parseFloat ( tiempo[2] ) / 60;
                        } else {
                            timep = Float.parseFloat ( tiempo[0] ) + Float.parseFloat ( tiempo[1] ) / 60;
                        }
                        timet = timet + timep;
                        veltot = veltot + velp;
                    }
                    yDato.add ( timet / participante );
                    yDatos.add ( veltot / participante );
                }
                AddGraficar ( xDato, yDato, yDaton, charttime, "Distancia [km]", "tiempo [min]" );
                AddGraficar ( xDato, yDatos, yDatom, chartvel, "Distancia [km]", "velocidad [m/s]" );


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );

        ArrayList axisData = new ArrayList ();
        ArrayList yAxisData = new ArrayList ();
        axisData.add ( "0" );
        yAxisData.add ( (float) 0.0 );
        AddGraficar ( axisData, yAxisData, yAxisData, charttime, "Distancia [km]", "Tiempo [min]" );
        AddGraficar ( axisData, yAxisData, yAxisData, chartvel, "Distancia [km]", "Velocidad [m/s]" );

    }

    private void AddGraficar(ArrayList axisData, ArrayList yAxisData, ArrayList yinAxisdata, LineChartView chart, String ejex, String ejey) {
        List yAxisValues = new ArrayList ();
        List axisValues = new ArrayList ();

        Line line = new Line ( yAxisValues ).setColor ( Color.parseColor ( "#CB3234" ) );
        for (int i = 0; i < axisData.size (); i++) {
            axisValues.add ( i, new AxisValue ( i ).setLabel ( (String) axisData.get ( i ) ) );
        }

        for (int i = 0; i < yAxisData.size (); i++) {
            yAxisValues.add ( new PointValue ( i, (Float) yAxisData.get ( i ) ) );
        }
        List lines = new ArrayList ();

        lines.add ( line );
        yAxisValues = new ArrayList ();
        line = new Line ( yAxisValues ).setColor ( Color.parseColor ( "#2271B3" ) );

        for (int i = 0; i < yinAxisdata.size (); i++) {
            yAxisValues.add ( new PointValue ( i, (Float) yinAxisdata.get ( i ) ) );
        }

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

}
