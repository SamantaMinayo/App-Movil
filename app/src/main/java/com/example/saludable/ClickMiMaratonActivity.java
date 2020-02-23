package com.example.saludable;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    private LineChartView chart;
    private String PostKey, current_user_id;
    private DatabaseReference CarreraUserInf;
    private FirebaseAuth mAuth;
    private ArrayList xDato = new ArrayList ();
    private ArrayList yDato = new ArrayList ();
    private String[] axisDatax;
    private float[] axisDatay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_click_mi_maraton );

        chart = findViewById ( R.id.chart );
        PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
        mAuth = FirebaseAuth.getInstance ();
        current_user_id = mAuth.getCurrentUser ().getUid ();

        CarreraUserInf = FirebaseDatabase.getInstance ().getReference ().child ( "CarrerasRealizadas" ).child ( "Usuarios" ).child ( current_user_id ).child ( PostKey );
        xDato = new ArrayList ();
        yDato = new ArrayList ();
        xDato.add ( "0" );
        yDato.add ( Float.parseFloat ( "0.0" ) );

        CarreraUserInf.child ( "tiempo" ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren ()) {
                    String hola = childDataSnapshot.getKey ();
                    String adios = (String) childDataSnapshot.getValue ();
                    xDato.add ( adios );
                    yDato.add ( Float.parseFloat ( hola ) );
                }
                axisDatax = new String[xDato.size ()];
                axisDatay = new float[yDato.size ()];

                for (int i = 0; i < xDato.size (); i++) {
                    axisDatax[i] = (String) xDato.get ( i );
                }
                for (int i = 0; i < yDato.size (); i++) {
                    axisDatay[i] = (Float) yDato.get ( i );
                }
                Graficar ( axisDatax, axisDatay, chart );


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        CarreraUserInf.removeEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
        String[] axisData = {"0"};
        float[] yAxisData = {0};
        Graficar ( axisData, yAxisData, chart );

    }

    private void Graficar(String[] axisData, float[] yAxisData, LineChartView chart) {
        List yAxisValues = new ArrayList ();
        List axisValues = new ArrayList ();

        Line line = new Line ( yAxisValues ).setColor ( Color.parseColor ( "#9C27B0" ) );
        for (int i = 0; i < axisData.length; i++) {
            axisValues.add ( i, new AxisValue ( i ).setLabel ( axisData[i] ) );
        }

        for (int i = 0; i < yAxisData.length; i++) {
            yAxisValues.add ( new PointValue ( i, yAxisData[i] ) );
        }

        List lines = new ArrayList ();
        lines.add ( line );

        LineChartData data = new LineChartData ();
        data.setLines ( lines );
        chart.setLineChartData ( data );

        Axis axis = new Axis ();
        axis.setValues ( axisValues );
        data.setAxisXBottom ( axis );
        axis.setName ( "Tiempo" );

        Axis yAxis = new Axis ();
        data.setAxisYLeft ( yAxis );

        axis.setTextSize ( 12 );
        axis.setTextColor ( Color.parseColor ( "#03A9F4" ) );
        yAxis.setTextColor ( Color.parseColor ( "#03A9F4" ) );
        yAxis.setTextSize ( 12 );
        yAxis.setName ( "Distancia" );
        Viewport viewport = new Viewport ( chart.getMaximumViewport () );
        chart.setMaximumViewport ( viewport );
        chart.setCurrentViewport ( viewport );
        chart.animate ();
    }
}
