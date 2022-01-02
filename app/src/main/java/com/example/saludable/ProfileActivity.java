package com.example.saludable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.saludable.Model.MiResultado;
import com.example.saludable.Utils.Common;
import com.example.saludable.localdatabase.DaoResultados;
import com.example.saludable.localdatabase.DaoUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class ProfileActivity extends AppCompatActivity {


    private TextView userName, userStatus, userFullname, userCountry, userEdad, userPeso, userAltura, userGenero, velpromtot, velpromult, tiempromtot, tiempromult, disttot, distult, userimc, caltot, calult, pastot, pasult, pesotot, pesoult;
    private CircleImageView userProfileImage;
    private DaoUsers daoUsers;
    private Toolbar mToolbar;
    private Button settings;

    private DatabaseReference MaratonDatosUser;
    private FirebaseAuth mAuth;
    private DaoResultados daoResultados;

    private String currentUserId;
    private ArrayList<MiResultado> miresultado = new ArrayList<MiResultado> ();

    private LineChartView TChart, VChart, CChart, DChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_profile );
            TChart = findViewById ( R.id.timechart );
            VChart = findViewById ( R.id.velchart );
            CChart = findViewById ( R.id.calchart );
            DChart = findViewById ( R.id.distchart );
            TChart.setOnValueTouchListener ( new ValueTouchListener ( "tiempo" ) );
            VChart.setOnValueTouchListener ( new ValueTouchListener ( "velocidad" ) );
            CChart.setOnValueTouchListener ( new ValueTouchListener ( "calorias" ) );
            DChart.setOnValueTouchListener ( new ValueTouchListener ( "distancia" ) );

            daoResultados = new DaoResultados ( this );

            mAuth = FirebaseAuth.getInstance ();
            currentUserId = mAuth.getCurrentUser ().getUid ();
            MaratonDatosUser = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( currentUserId )
                    .child ( "Resultados" ).child ( "Resultado" );
            mToolbar = findViewById ( R.id.mi_perfil_toolbar );
            setSupportActionBar ( mToolbar );
            getSupportActionBar ().setTitle ( "Mi Perfil" );
            getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

            daoUsers = new DaoUsers ( this );
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

            Common.loggedUser = daoUsers.ObtenerUsuario ();
            String urlimage = "";
            if (Common.loggedUser.getImage ().length () > 7) {
                urlimage = "file://" + Common.loggedUser.getImage ();
            } else {
                urlimage = Common.loggedUser.getProfileimage ();
            }
            Picasso.with ( ProfileActivity.this ).load ( urlimage ).placeholder ( R.drawable.profile ).into ( userProfileImage );
            userName.setText ( "@" + Common.loggedUser.getUsername () );
            userFullname.setText ( Common.loggedUser.getFullname () );
            userStatus.setText ( Common.loggedUser.getStatus () );
            userGenero.setText ( "Genero: " + Common.loggedUser.getGenero () );
            userCountry.setText ( "País: " + Common.loggedUser.getCountry () );
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
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ProfileActivity" ).child ( "OnCreate" ).child ( currentUserId ).updateChildren ( error );
        }
    }

    private void CargarDatos() {
        try {
            if (daoResultados.ObtenerDatos () != null) {
                miresultado = daoResultados.ObtenerDatos ();
                GenerarDatos ();
            } else {
                MaratonDatosUser.addListenerForSingleValueEvent ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        miresultado.clear ();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren ()) {
                            miresultado.add ( postSnapshot.getValue ( MiResultado.class ) );
                        }
                        GenerarDatos ();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                } );
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ProfileActivity" ).child ( "CargarDaros" ).child ( currentUserId ).updateChildren ( error );
        }
    }

    private void GenerarDatos() {
        try {
            List<PointValue> TValues = new ArrayList<PointValue> ();
            TValues.add ( new PointValue ( 0, 0 ) );
            List<PointValue> VValues = new ArrayList<PointValue> ();
            VValues.add ( new PointValue ( 0, 0 ) );
            List<PointValue> CValues = new ArrayList<PointValue> ();
            CValues.add ( new PointValue ( 0, 0 ) );
            ArrayList<PointValue> DValues = new ArrayList<PointValue> ();
            DValues.add ( new PointValue ( 0, 0 ) );
            int cont = 0;
            float velprom = 0, timprom = 0, distotal = 0, caltotal = 0, pastotal = 0;
            for (int i = 0; i <miresultado.size (); i += 1) {
                cont = cont + 1;
                if (i == miresultado.size ()-1) {
                    velpromult.setText ( String.valueOf ( miresultado.get ( i ).velmed ) );
                    tiempromult.setText ( String.valueOf ( Float.valueOf ( miresultado.get ( i  ).getTiempo () ) / (Float.valueOf ( miresultado.get ( i  ).getDistancia () ) / 1000) ) );
                    distult.setText ( String.valueOf ( Float.valueOf ( miresultado.get ( i ).distancia ) / 1000 ) );
                    calult.setText ( String.valueOf ( Float.valueOf ( miresultado.get ( i  ).calorias ) / 1000 ) );
                    pasult.setText ( String.valueOf ( miresultado.get ( i ).pasos ) );
                }
                TValues.add ( new PointValue ( cont, Float.valueOf ( miresultado.get ( i  ).getTiempo () ) / (Float.valueOf ( miresultado.get ( i  ).getDistancia () ) / 1000) ) );
                VValues.add ( new PointValue ( cont, Float.valueOf ( miresultado.get ( i  ).getVelmed () ) ) );
                velprom = Float.valueOf ( miresultado.get ( i  ).getVelmed () ) + velprom;
                timprom = Float.valueOf ( miresultado.get ( i  ).getTiempo () ) / (Float.valueOf ( miresultado.get ( i  ).getDistancia () ) / 1000) + timprom;
                distotal = distotal + Float.valueOf ( miresultado.get ( i ).distancia );
                caltotal = caltotal + Float.valueOf ( miresultado.get ( i ).calorias );
                pastotal = pastotal + Float.valueOf ( miresultado.get ( i ).pasos );
                CValues.add ( new PointValue ( cont, Float.valueOf ( miresultado.get ( i ).calorias ) / 1000 ) );
                DValues.add ( new PointValue ( cont, Float.valueOf ( miresultado.get ( i ).distancia) / 1000 ) );
            }
            velpromtot.setText ( String.valueOf ( velprom / cont ) );
            tiempromtot.setText ( String.valueOf ( timprom / cont ) );
            disttot.setText ( String.valueOf ( distotal / 1000 ) );
            caltot.setText ( String.valueOf ( caltotal / 1000 ) );
            pastot.setText ( String.valueOf ( pastotal ) );
            Graficar ( TValues, TChart, "Tiempo x km [min]", " Carrera" );
            Graficar ( VValues, VChart, "Velocidad media [m/s]", " Carrera" );
            Graficar ( CValues, CChart, "Calorias [kcal]", "Carrera" );
            Graficar ( DValues, DChart, "Distancia [km]", " Carrera" );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ProfileActivity" ).child ( "GenerarDatos" ).child ( currentUserId ).updateChildren ( error );
        }
    }

    private void Graficar(List<PointValue> yValues, LineChartView chart, String labely, String labelx) {
        try {
            Line line = new Line ( yValues ).setColor ( ChartUtils.COLORS[0] ).setCubic ( true );
            line.setCubic ( false );
            List<Line> lines = new ArrayList<Line> ();
            lines.add ( line );
            LineChartData data = new LineChartData ();
            data.setLines ( lines );
            Axis axisX = new Axis ();
            Axis axisY = new Axis ().setHasLines ( true );
            axisX.setName ( labelx );
            axisY.setName ( labely );
            axisY.setTextColor ( ChartUtils.COLOR_BLUE );
            axisX.setTextColor ( ChartUtils.COLOR_BLUE );
            data.setAxisXBottom ( axisX );
            data.setAxisYLeft ( axisY );
            data.setBaseValue ( Float.NEGATIVE_INFINITY );
            chart.setZoomEnabled ( false );
            chart.setScrollEnabled ( false );
            chart.setLineChartData ( data );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ProfileActivity" ).child ( "Graficar" ).child ( currentUserId ).updateChildren ( error );
        }
    }

    private void SendUserToSettingsActivity() {
        try {
            Intent loginIntent = new Intent ( ProfileActivity.this, SettingsActivity.class );
            startActivity ( loginIntent );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ProfileActivity" ).child ( "SendUserToSettingsActivity" ).child ( currentUserId ).updateChildren ( error );
        }
    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {
        String values;

        public ValueTouchListener(String tiempo) {
            values = tiempo;
        }

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText ( ProfileActivity.this, "Carrera #: " + value.getX () + " " + values + ": " + value.getY (), Toast.LENGTH_SHORT ).show ();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub
        }

    }

    @Override
    protected void onStart() {
        try {
            super.onStart ();
            userProfileImage = findViewById ( R.id.my_profile_pic );
            Common.loggedUser = daoUsers.ObtenerUsuario ();
            Picasso.with ( ProfileActivity.this ).load ( "" ).placeholder ( R.drawable.profile ).
                    into ( userProfileImage );

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ProfileActivity" ).child ( "OnCreate" ).child ( currentUserId ).updateChildren ( error );
        }

    }
}
