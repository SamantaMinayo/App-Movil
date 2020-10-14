package com.example.saludable;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.saludable.Model.Dato;
import com.example.saludable.Utils.Common;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
/*import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;*/

public class ProfileActivity extends AppCompatActivity {


    private TextView userName, userStatus, userFullname, userCountry, userEdad, userPeso, userAltura, userGenero, velpromtot, velpromult, tiempromtot, tiempromult, disttot, distult, userimc, caltot, calult, pastot, pasult, pesotot, pesoult;
    private CircleImageView userProfileImage;
    private Toolbar mToolbar;
    private Button settings;

    private DatabaseReference profileuserRef, MaratonDatosUser;
    private FirebaseAuth mAuth;

    private String currentUserId;
    //private LineChartView chartcalorias, charttime, chartvelocidad;
    private ArrayList xDato = new ArrayList ();
    private ArrayList yDato = new ArrayList ();
    private ArrayList<Dato> miresultado = new ArrayList<Dato> ();

    private LineChart TChart, VChart, CChart, DChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_profile );

            TChart = findViewById ( R.id.timechart );
            VChart = findViewById ( R.id.velchart );
            CChart = findViewById ( R.id.calchart );
            DChart = findViewById ( R.id.distchart );
            //     mChart.setOnChartGestureListener ( ProfileActivity.this );
            //   mChart.setOnChartValueSelectedListener ( ProfileActivity.this );

            mAuth = FirebaseAuth.getInstance ();
            currentUserId = mAuth.getCurrentUser ().getUid ();
            profileuserRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( currentUserId );
            MaratonDatosUser = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( currentUserId )
                    .child ( "Resultados" ).child ( "Resultado" );

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

            ArrayList axisData = new ArrayList ();
            ArrayList yAxisData = new ArrayList ();
            axisData.add ( "0" );
            yAxisData.add ( (float) 0.0 );
            CargarDatos ();

        } catch (Exception e) {
        }
    }

    private void CargarDatos() {

        MaratonDatosUser.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren ()) {
                    miresultado.add ( postSnapshot.getValue ( Dato.class ) );
                }
                GenerarDatos ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


    }

    private void GenerarDatos() {
        ArrayList<Entry> TValues = new ArrayList<> ();
        TValues.add ( new Entry ( 0, 0 ) );
        ArrayList<Entry> VValues = new ArrayList<> ();
        VValues.add ( new Entry ( 0, 0 ) );
        ArrayList<Entry> CValues = new ArrayList<> ();
        CValues.add ( new Entry ( 0, 0 ) );
        ArrayList<Entry> DValues = new ArrayList<> ();
        DValues.add ( new Entry ( 0, 0 ) );
        int cont = 0;
        float velprom = 0;
        float timprom = 0;
        float distotal = 0;
        float caltotal = 0;
        float pastotal = 0;

        for (Dato dato : miresultado) {
            cont = cont + 1;
            if (cont == 1) {
                velpromult.setText ( String.valueOf ( dato.velocidad ) );
                tiempromult.setText ( String.valueOf ( Float.valueOf ( dato.getTiempo () ) / (Float.valueOf ( dato.getDistancia () ) / 1000) ) );
                distult.setText ( String.valueOf ( Float.valueOf ( dato.distancia ) / 1000 ) );
                calult.setText ( String.valueOf ( Float.valueOf ( dato.calorias ) / 1000 ) );
                pasult.setText ( String.valueOf ( dato.pasos ) );
            }
            TValues.add ( new Entry ( cont, Float.valueOf ( dato.getTiempo () ) / (Float.valueOf ( dato.getDistancia () ) / 1000) ) );
            VValues.add ( new Entry ( cont, Float.valueOf ( dato.getVelocidad () ) ) );
            velprom = Float.valueOf ( dato.getVelocidad () ) + velprom;
            timprom = Float.valueOf ( dato.getTiempo () ) / (Float.valueOf ( dato.getDistancia () ) / 1000) + timprom;
            distotal = distotal + Float.valueOf ( dato.distancia );
            caltotal = caltotal + Float.valueOf ( dato.calorias );
            pastotal = pastotal + Float.valueOf ( dato.pasos );
            CValues.add ( new Entry ( cont, caltotal / 1000 ) );
            DValues.add ( new Entry ( cont, distotal / 1000 ) );
        }

        velpromtot.setText ( String.valueOf ( velprom / cont ) );
        tiempromtot.setText ( String.valueOf ( timprom / cont ) );
        disttot.setText ( String.valueOf ( distotal / 1000 ) );
        caltot.setText ( String.valueOf ( caltotal / 1000 ) );
        pastot.setText ( String.valueOf ( pastotal ) );
        Graficar ( TValues, TChart, "Tiempo x km [min] vs Carrera" );
        Graficar ( VValues, VChart, "Velocidad media [m/s] vs Carrera" );
        Graficar ( CValues, CChart, "Calorias [kcal] vs Carrera" );
        Graficar ( DValues, DChart, "Distancia [km] vs Carrera" );


    }

    private void Graficar(ArrayList<Entry> yValues, LineChart chart, String label) {
        chart.setDragEnabled ( true );
        chart.setScaleEnabled ( true );
        LineDataSet set1 = new LineDataSet ( yValues, label );
        set1.setFillAlpha ( 110 );
        set1.setLineWidth ( 3f );
        set1.setColor ( Color.parseColor ( "#23BAC4" ) );
        set1.setDrawIcons ( false );
        set1.enableDashedLine ( 15f, 10f, 0f );
        set1.enableDashedHighlightLine ( 15f, 10f, 0f );
        set1.setCircleColor ( Color.DKGRAY );
        set1.setLineWidth ( 2f );
        set1.setCircleRadius ( 4f );
        set1.setDrawCircleHole ( false );
        set1.setValueTextSize ( 9f );
        //set1.setDrawFilled(true);
        set1.setFormLineWidth ( 1f );
        set1.setFormLineDashEffect ( new DashPathEffect ( new float[]{10f, 5f}, 0f ) );
        set1.setFormSize ( 15.f );

        ArrayList<ILineDataSet> dataSets = new ArrayList<> ();
        dataSets.add ( set1 );

        LineData data = new LineData ( dataSets );

        chart.setData ( data );

    }

    private void SendUserToSettingsActivity() {
        try {
            Intent loginIntent = new Intent ( ProfileActivity.this, SettingsActivity.class );
            startActivity ( loginIntent );
        } catch (Exception e) {
        }
    }

}
