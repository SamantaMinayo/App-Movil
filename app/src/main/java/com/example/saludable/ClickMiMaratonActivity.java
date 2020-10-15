package com.example.saludable;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.saludable.Model.Dato;
import com.example.saludable.Model.Maraton;
import com.example.saludable.Model.Punto;
import com.example.saludable.Utils.Common;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
//import lecho.lib.hellocharts.model.Axis;
//import lecho.lib.hellocharts.model.AxisValue;
//import lecho.lib.hellocharts.model.Line;
//import lecho.lib.hellocharts.model.LineChartData;
//import lecho.lib.hellocharts.model.PointValue;
//import lecho.lib.hellocharts.model.Viewport;
//import lecho.lib.hellocharts.view.LineChartView;

public class ClickMiMaratonActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //  private LineChartView chartvel, charttime;
    private String PostKey, current_user_id;
    private DatabaseReference CarreraUserInf, CarreraInf, DatosCarreraResult, ResultadoCarrera, MaratonDatosRef, MaratonPointsRef;
    private FirebaseAuth mAuth;
    private ArrayList xDato = new ArrayList ();
    private ArrayList yDato = new ArrayList ();
    private ArrayList yDaton = new ArrayList ();
    private ArrayList yDatom = new ArrayList ();
    private ArrayList yDatos = new ArrayList ();
    private ArrayList<Dato> listadatos = new ArrayList<Dato> ();
    private ArrayList<Dato> misdatos = new ArrayList<Dato> ();
    private ArrayList<Dato> listaresultadosglobales = new ArrayList<Dato> ();
    private ArrayList<Punto> listadatosloc = new ArrayList<Punto> ();

    private long puntos = 0;
    private Toolbar mToolbar;
    private TextView velocidad, pasos, calorias, tiempo;
    private TextView velocidadprom, pasosprom, caloriasprom, tiempoprom;
    private TextView descripcion, nombre, lugar, distancia, fecha;
    private CircleImageView imagen;
    private DecimalFormat formato1;
    private ArrayList<Dato> miresultado = new ArrayList<Dato> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_click_mi_maraton );

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
                    .findFragmentById ( R.id.mapmi );
            mapFragment.getMapAsync ( this );

            pasos = findViewById ( R.id.pasostotal );
            velocidad = findViewById ( R.id.velocidadprom );
            calorias = findViewById ( R.id.caltotal );
            tiempo = findViewById ( R.id.tiempototal );
            nombre = findViewById ( R.id.mi_maraton_name );
            descripcion = findViewById ( R.id.carreradescription );
            lugar = findViewById ( R.id.carreralugar );
            distancia = findViewById ( R.id.carreradist );
            fecha = findViewById ( R.id.carrerahora );
            imagen = findViewById ( R.id.carreraimage );
            velocidadprom = findViewById ( R.id.velocidadpromglo );
            pasosprom = findViewById ( R.id.pasostotalglob );
            caloriasprom = findViewById ( R.id.caltotalglob );
            tiempoprom = findViewById ( R.id.tiempototalglob );
            formato1 = new DecimalFormat ( "#.00" );

            PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
            mAuth = FirebaseAuth.getInstance ();
            current_user_id = mAuth.getCurrentUser ().getUid ();


            mToolbar = findViewById ( R.id.mi_carrera_toolbar );
            setSupportActionBar ( mToolbar );
            getSupportActionBar ().setTitle ( "Mi Carrera" );
            getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

            CarreraUserInf = FirebaseDatabase.getInstance ().getReference ().child ( "CarrerasRealizadas" ).child ( "Usuarios" ).child ( current_user_id ).child ( PostKey );
            CarreraInf = FirebaseDatabase.getInstance ().getReference ().child ( "CarrerasRealizadas" ).child ( "Carreras" ).child ( PostKey );
            ResultadoCarrera = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( current_user_id ).child ( "Resultados" ).child ( "Resultado" ).child ( PostKey );
            MaratonDatosRef = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Nuevas" ).child ( PostKey );
            DatosCarreraResult = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Resultados" ).child ( PostKey );
            MaratonPointsRef = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Datos" ).child ( PostKey ).child ( current_user_id );
            ArrayList axisData = new ArrayList ();
            ArrayList yAxisData = new ArrayList ();
            axisData.add ( "0" );
            yAxisData.add ( (float) 0.0 );
            CargarDatosCarrera ();
            CargarResultadosCarrera ();
            LoadResult ();
            CargarTrayectoria ();
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "onCreate" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }

    }

    private void CargarTrayectoria() {
        try {
            MaratonPointsRef.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot pointSnapshot : dataSnapshot.getChildren ()) {
                        listadatosloc.add ( pointSnapshot.getValue ( Punto.class ) );
                    }
                    GraficarTrayectoria ();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "CargarTrayectoria" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void GraficarTrayectoria() {
        try {
            Location locationa = null;
            for (Punto point : listadatosloc) {
                if (locationa == null) {
                    locationa = new Location ( "punto A" );
                    double lat = Double.valueOf ( point.latitud );
                    double longi = Double.valueOf ( point.longitud );
                    locationa.setLatitude ( lat );
                    locationa.setLongitude ( longi );
                    LatLng coordenada = new LatLng ( lat, longi );

                    CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom ( coordenada, 17 );

                    mMap.animateCamera ( miUbicacion );
                } else {
                    Location locationb = new Location ( "punto B" );
                    Double lat = Double.valueOf ( point.latitud );
                    Double longi = Double.valueOf ( point.longitud );
                    locationb.setLatitude ( lat );
                    locationb.setLongitude ( longi );
                    Polyline line = mMap.addPolyline ( new PolylineOptions ()
                            .add ( new LatLng ( locationa.getLatitude (), locationa.getLongitude () ), new LatLng ( locationb.getLatitude (), locationb.getLongitude () ) )
                            .width ( 5 )
                            .color ( Color.BLUE ) );
                    locationa = locationb;
                }

            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "GraficarTrayectoria" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void CargarResultadosCarrera() {
        try {
            DatosCarreraResult.addValueEventListener (
                    new ValueEventListener () {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren ()) {
                                listaresultadosglobales.add ( postSnapshot.getValue ( Dato.class ) );
                            }
                            CargarDatosGlob ();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "CargarResultadosCarrera" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }

    }

    private void CargarDatosGlob() {
        try {
            float tiempoglo = 0;
            float velocidadglo = 0;
            float pasosglo = 0;
            float caloriasglo = 0;
            int cantglo = 0;
            for (Dato dato : listaresultadosglobales) {
                cantglo = cantglo + 1;
                tiempoglo = tiempoglo + Float.valueOf ( dato.getTiempo () );
                velocidadglo = velocidadglo + Float.valueOf ( dato.getVelocidad () );
                pasosglo = pasosglo + Float.valueOf ( dato.getPasos () );
                caloriasglo = caloriasglo + Float.valueOf ( dato.getCalorias () );
            }

            float time = tiempoglo / cantglo;
            float segundos = time % 1;
            float mintotales = time - segundos;
            float calculo = mintotales / 60;
            float decimales = calculo % 1;
            float horas = calculo - decimales;
            float minutos = mintotales - horas * 60;
            tiempoprom.setText ( (int) horas + ":" + (int) minutos + ":" + (int) (segundos * 60) );
            velocidadprom.setText ( formato1.format ( velocidadglo / cantglo ) + " m/s" );
            pasosprom.setText ( formato1.format ( pasosglo / cantglo ) );
            caloriasprom.setText ( formato1.format ( caloriasglo / cantglo ) + " cal" );

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "CargarDatosGloba;es" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void CargarDatosCarrera() {
        try {
            MaratonDatosRef.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists ()) {
                        Common.carrera = dataSnapshot.getValue ( Maraton.class );
                        Picasso.with ( ClickMiMaratonActivity.this ).load ( Common.carrera.maratonimage ).into ( imagen );
                        descripcion.setText ( Common.carrera.description );
                        fecha.setText ( Common.carrera.maratondate + " " + Common.carrera.maratontime );
                        lugar.setText ( "Lugar: " + Common.carrera.place );
                        nombre.setText ( Common.carrera.maratonname );
                        distancia.setText ( "Distancia: " + Common.carrera.maratondist + " km" );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );

            ResultadoCarrera.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    miresultado.add ( dataSnapshot.getValue ( Dato.class ) );
                    for (Dato datos : miresultado) {
                        pasos.setText ( datos.getPasos () );
                        velocidad.setText ( datos.getVelocidad () + " m/s" );
                        calorias.setText ( datos.getCalorias () + " cal" );
                        float time = Float.valueOf ( datos.getTiempo () );
                        float segundos = time % 1;
                        float mintotales = time - segundos;
                        float calculo = mintotales / 60;
                        float decimales = calculo % 1;
                        float horas = calculo - decimales;
                        float minutos = mintotales - horas * 60;
                        tiempo.setText ( (int) horas + ":" + (int) minutos + ":" + (int) (segundos * 60) );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "CargarDatosCarrera" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void LoadResult() {
        try {
            FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Graficas" ).child ( PostKey )
                    .addValueEventListener (
                            new ValueEventListener () {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    for (DataSnapshot postSnapshot : snapshot.getChildren ()) {
                                        puntos = snapshot.getChildrenCount ();
                                        for (DataSnapshot childpostSnapshot : postSnapshot.getChildren ()) {
                                            listadatos.add ( childpostSnapshot.getValue ( Dato.class ) );
                                            if (childpostSnapshot.child ( "uid" ).getValue ().equals ( current_user_id )) {
                                                misdatos.add ( childpostSnapshot.getValue ( Dato.class ) );
                                            }
                                        }
                                    }
                                    GenerarInfGrafTiempo ();
                                    GenerarInfGrafVelocidad ();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            } );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "LoadResult" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void GenerarInfGrafTiempo() {
        try {
            ArrayList axisData = new ArrayList ();
            ArrayList yAxisData = new ArrayList ();
            axisData.add ( "0" );
            yAxisData.add ( (float) 0.0 );
            for (int i = 1; i <= puntos + 1; i++) {
                float tiempo = 0;
                int point = i;
                for (Dato dato : misdatos) {
                    if (Integer.valueOf ( dato.punto ) == i) {
                        tiempo = tiempo + Float.valueOf ( dato.tiempo );
                        yAxisData.add ( tiempo );
                    }
                }
            }
            ArrayList yAxisDatat = new ArrayList ();
            yAxisDatat.add ( (float) 0.0 );
            float tiempo = 0;
            for (int i = 1; i <= puntos + 1; i++) {
                float valor = 0;
                int cant = 0;
                for (Dato dato : listadatos) {

                    if (Integer.valueOf ( dato.punto ) == i) {
                        cant = cant + 1;
                        valor = valor + Float.valueOf ( dato.tiempo );
                    }
                }
                tiempo = tiempo + Float.valueOf ( valor / cant );

                axisData.add ( String.valueOf ( i ) );

                yAxisDatat.add ( tiempo );
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "GenerarInfGrafTiempo" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void GenerarInfGrafVelocidad() {
        try {
            ArrayList axisData = new ArrayList ();
            ArrayList yAxisData = new ArrayList ();
            axisData.add ( "0" );
            yAxisData.add ( (float) 0.0 );
            for (int i = 1; i <= puntos + 1; i++) {
                int point = i;
                for (Dato dato : misdatos) {
                    if (Integer.valueOf ( dato.punto ) == i) {
                        yAxisData.add ( Float.valueOf ( dato.getVelocidad () ) );
                    }
                }
            }
            ArrayList yAxisDatat = new ArrayList ();
            yAxisDatat.add ( (float) 0.0 );
            float tiempo = 0;
            for (int i = 1; i <= puntos + 1; i++) {
                float valor = 0;
                int cant = 0;
                for (Dato dato : listadatos) {

                    if (Integer.valueOf ( dato.punto ) == i) {
                        cant = cant + 1;
                        valor = valor + Float.valueOf ( dato.getVelocidad () );
                    }
                }
                axisData.add ( String.valueOf ( i ) );
                yAxisDatat.add ( valor / cant );
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "GenerarInfGrafVelocidad" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
