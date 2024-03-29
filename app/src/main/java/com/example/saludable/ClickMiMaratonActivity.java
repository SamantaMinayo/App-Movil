package com.example.saludable;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.saludable.Model.Maraton;
import com.example.saludable.Model.MaratonResult;
import com.example.saludable.Model.MiResultado;
import com.example.saludable.Model.Punto;
import com.example.saludable.Utils.Common;
import com.example.saludable.localdatabase.DaoMarRes;
import com.example.saludable.localdatabase.DaoMaraton;
import com.example.saludable.localdatabase.DaoPuntos;
import com.example.saludable.localdatabase.DaoResultados;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

public class ClickMiMaratonActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String PostKey, current_user_id;
    private DatabaseReference DatosCarreraResult, ResultadoCarrera, MaratonDatosRef, MaratonPointsRef,MaratonPointKil;
    private FirebaseAuth mAuth;
    private ArrayList<MiResultado> listaresultadosglobales = new ArrayList<MiResultado> ();
    private ArrayList<Punto> listadatosloc = new ArrayList<Punto> ();
    private ArrayList<MiResultado> listadatoskil = new ArrayList<MiResultado> ();

    private DaoMaraton daoMaraton;
    private DaoResultados daoResultados;
    private DaoMarRes daoMarRes;
    private DaoPuntos daoPuntos;


    private Toolbar mToolbar;
    private TextView velocidad, pasos, calorias, tiempo;
    private TextView velocidadprom, pasosprom, caloriasprom, tiempoprom;
    private TextView descripcion, nombre, lugar, distancia, fecha;
    private TextView dist, mivelmas, mivelmin, miritmo;
    private TextView maxvel, minvel, mejtime, peortime, mejritmo, ritmoprom, peorritmo;
    private CircleImageView imagen;
    private DecimalFormat formato1;
    private ArrayList<MiResultado> miresultado = new ArrayList<MiResultado> ();
    private List<PointValue> TValues = new ArrayList<PointValue> ();
    private List<PointValue> VValues = new ArrayList<PointValue> ();
    private List<MiResultado> mires= new ArrayList<MiResultado>();

    private LineChartView chart;
    private PreviewLineChartView previewChart;
    private LineChartData data;
    private LineChartData previewData;

    private LineChartView chartvel,charttiempok,chartvelk;
    private PreviewLineChartView previewChartvel;
    private LineChartData datavel,datatiempok,datavelk;
    private LineChartData previewDatavel;
    private Marker marcador;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_click_mi_maraton );

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
                    .findFragmentById ( R.id.mapmi );
            mapFragment.getMapAsync ( this );

            Instanciar ();
            CargarDatosCarreraKil();
            Common.carrera = daoMaraton.ObtenerMaraton ( PostKey );
            if (Common.carrera == null) {
                CargarDatosCarrera ();
            } else {
                if (!Common.carrera.getEstado ().equals ( "fin" )) {
                    CargarDatosCarrera ();
                    Picasso.with ( ClickMiMaratonActivity.this ).load ( Common.carrera.maratonimage ).into ( imagen );
                    descripcion.setText ( Common.carrera.description );
                    fecha.setText ( Common.carrera.maratondate );
                    lugar.setText ( "Lugar: " + Common.carrera.place );
                    nombre.setText ( Common.carrera.maratonname );
                    distancia.setText ( "Distancia: " + Common.carrera.maratondist + " km" );
                } else {
                    Picasso.with ( ClickMiMaratonActivity.this ).load ( Common.carrera.maratonimage ).into ( imagen );
                    descripcion.setText ( Common.carrera.description );
                    fecha.setText ( Common.carrera.maratondate );
                    lugar.setText ( "Lugar: " + Common.carrera.place );
                    nombre.setText ( Common.carrera.maratonname );
                    distancia.setText ( "Distancia: " + Common.carrera.maratondist + " km" );
                }
            }

            CargarMisResultados ();
            CargarResultadosCarrera ();
            CargarMisDatos ();
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "onCreate" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }

    }

    private void Instanciar() {
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
        dist = findViewById ( R.id.distanciatot );
        mivelmas = findViewById ( R.id.velocidadmax );
        mivelmin = findViewById ( R.id.velocidadmin );
        miritmo = findViewById ( R.id.ritmo );
        maxvel = findViewById ( R.id.maxvelo );
        minvel = findViewById ( R.id.minvel );
        mejtime = findViewById ( R.id.tiempomejor );
        peortime = findViewById ( R.id.tiempopeor );
        mejritmo = findViewById ( R.id.mejorritmo );
        ritmoprom = findViewById ( R.id.ritmoprom );
        peorritmo = findViewById ( R.id.peorritmo );
        chart = findViewById ( R.id.chart );
        previewChart = findViewById ( R.id.chart_preview );

        chartvel = findViewById ( R.id.chartvel );
        charttiempok=findViewById ( R.id.charttiempok );
        chartvelk=findViewById ( R.id.chartvelocidadk);
        previewChartvel = findViewById ( R.id.chart_previewvel );
        daoMaraton = new DaoMaraton ( this );
        daoResultados = new DaoResultados ( this );
        daoMarRes = new DaoMarRes ( this );
        daoPuntos = new DaoPuntos ( this );


        formato1 = new DecimalFormat ( "#.00" );

        PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
        mAuth = FirebaseAuth.getInstance ();
        current_user_id = mAuth.getCurrentUser ().getUid ();


        mToolbar = findViewById ( R.id.mi_carrera_toolbar );
        setSupportActionBar ( mToolbar );
        getSupportActionBar ().setTitle ( "Mi Carrera" );
        getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );


        ResultadoCarrera = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( current_user_id ).child ( "Resultados" ).child ( "Resultado" ).child ( PostKey );
        MaratonDatosRef = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Nuevas" ).child ( PostKey );
        DatosCarreraResult = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Resultados" ).child ( PostKey );
        MaratonPointsRef = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Datos" ).child ( PostKey ).child ( current_user_id );
        MaratonPointKil=FirebaseDatabase.getInstance().getReference().child("Carreras").child("Kilometro").child ( PostKey );
    }

    private void CargarDatosCarrera() {
        try {
            MaratonDatosRef.addListenerForSingleValueEvent ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists ()) {
                        Common.carrera = dataSnapshot.getValue ( Maraton.class );
                        Common.carrera.setImage ( "" );
                        daoMaraton.InsertEditar ( Common.carrera );
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
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "CargarDatosCarrera" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void CargarDatosCarreraKil() {
        try {
            MaratonPointKil.addListenerForSingleValueEvent ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists ()) {
                        for (DataSnapshot ptSnapshot : dataSnapshot.getChildren ()) {
                             double velocidadpromk=0;
                             double caloriaspromk=0;
                             double tiempopromk=0;
                             double pasospromk=0;
                             int cantidad=0;
                                for(DataSnapshot kil : ptSnapshot.getChildren()){
                                    cantidad=cantidad+1;
                                    String calorias= (String) kil.child("calorias").getValue();
                                    String pasos= (String) kil.child("pasos").getValue();
                                    String velocidad= (String) kil.child("velocidad").getValue();
                                    String[] tiempos=((String) kil.child("tiempo").getValue()).split(":");
                                    double newtempo=Double.parseDouble(tiempos[0])*60+Double.parseDouble(tiempos[1])+Double.parseDouble(tiempos[2])/60;
                                    velocidadpromk= velocidadpromk+Double.parseDouble(velocidad);
                                    caloriaspromk=caloriaspromk+Double.parseDouble(calorias);
                                    pasospromk=pasospromk+Double.parseDouble(pasos);
                                    tiempopromk=tiempopromk+newtempo;
                                    if(kil.getKey().equals(current_user_id)){
                                        MiResultado miresult= new MiResultado();
                                        miresult.setCalorias(calorias);
                                        miresult.setPasos(pasos);
                                        miresult.setVelocidadmed(velocidad);
                                        miresult.setTiempomed(String.valueOf(newtempo));
                                        mires.add(miresult);
                                    }
                                }
                            MiResultado mrkilometro= new MiResultado();
                            mrkilometro.setCalorias(String.valueOf(caloriaspromk/cantidad));
                            mrkilometro.setVelocidadmed(String.valueOf(velocidadpromk/cantidad));
                            mrkilometro.setPasos(String.valueOf(pasospromk/cantidad));
                            mrkilometro.setTiempomed(String.valueOf(tiempopromk/cantidad));
                            listadatoskil.add(mrkilometro);

                        }
                        ///Mandar a graficar
                        GenerarGraficaKilometro();
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

    private void CargarMisResultados() {
        try {
            Common.miResult = daoResultados.ObtenerResultado(PostKey);
            if (Common.miResult == null) {
                ResultadoCarrera.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //miresultado.add ( dataSnapshot.getValue ( MiResultado.class ) );
                        //for (MiResultado datos : miresultado) {
                        MiResultado datos = dataSnapshot.getValue(MiResultado.class);
                        datos.setUid(dataSnapshot.getKey());
                        pasos.setText(datos.getPasos());
                        velocidad.setText(datos.getVelocidad() + " m/s");
                        calorias.setText(datos.getCalorias() + " cal");

                        float distance = Float.valueOf(datos.getDistancia()) / 1000;
                        dist.setText(distance + "km");
                        float time = Float.valueOf(datos.getTiempo());
                        float segundos = time % 1;
                        float mintotales = time - segundos;
                        float calculo = mintotales / 60;
                        float decimales = calculo % 1;
                        float horas = calculo - decimales;
                        float minutos = mintotales - horas * 60;
                        String sec, minutosm;
                        if (segundos < 10) {
                            sec = "0" + (int) segundos;
                        } else {
                            sec = String.valueOf((int) segundos);
                        }
                        if (minutos < 10) {
                            minutosm = "0" + (int) minutos;
                        } else {
                            minutosm = String.valueOf((int) minutos);
                        }
                        tiempo.setText((int) horas + ":" + minutosm + ":" + sec);
                        float ritmo = time / distance;
                        float rsegundos = ritmo % 1;
                        float rmintotales = ritmo - rsegundos;
                        float rcalculo = rmintotales / 60;
                        float rdecimales = rcalculo % 1;
                        float rhoras = rcalculo - rdecimales;
                        float rminutos = rmintotales - rhoras * 60;
                        miritmo.setText((int) rminutos + "'" + (int) (rsegundos * 60) + "''");
                        daoResultados.Insert(datos);
                        //}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                float time = Float.valueOf(Common.miResult.getTiempo());
                float segundos = time % 1;
                float mintotales = time - segundos;
                float calculo = mintotales / 60;
                float decimales = calculo % 1;
                float horas = calculo - decimales;
                float minutos = mintotales - horas * 60;
                String sec, minutosm;
                if (segundos < 10) {
                    sec = "0" + (int) segundos;
                } else {
                    sec = String.valueOf((int) segundos);
                }
                if (minutos < 10) {
                    minutosm = "0" + (int) minutos;
                } else {
                    minutosm = String.valueOf((int) minutos);
                }
                float distance = Float.valueOf(Common.miResult.getDistancia()) / 1000;
                float ritmo = time / distance;
                float rsegundos = ritmo % 1;
                float rmintotales = ritmo - rsegundos;
                float rcalculo = rmintotales / 60;
                float rdecimales = rcalculo % 1;
                float rhoras = rcalculo - rdecimales;
                float rminutos = rmintotales - rhoras * 60;
                miritmo.setText((int) rminutos + "'" + (int) (rsegundos * 60) + "''");
                tiempo.setText((int) horas + ":" + minutosm + ":" + sec);
                pasos.setText(Common.miResult.getPasos());
                velocidad.setText(Common.miResult.getVelmed() + " m/s");
                calorias.setText(Common.miResult.getCalorias() + " cal");
                dist.setText(formato1.format(distance) + " km");
                mivelmas.setText(formato1.format(Float.valueOf(Common.miResult.getVelmax())) + " m/s");
                mivelmin.setText(formato1.format(Float.valueOf(Common.miResult.getVelmin())) + " m/s");
            }
        }catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
           FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "CargarMisResultados" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void CargarResultadosCarrera() {
        try {

            Common.maratonResult = daoMarRes.ObtenerMaratonRes ( PostKey );
            if (Common.maratonResult == null) {
                if (daoMaraton.ObtenerMaraton ( PostKey ).getEstado () == "fin") {
                    DatosCarreraResult.orderByChild ( "velmed" ).limitToFirst ( 1 ).addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren ()) {
                                maxvel.setText ( postSnapshot.getValue ( MiResultado.class ).getVelmed()+ "m/s" );
                                MaratonResult nuevo = new MaratonResult ( PostKey, "", "","", postSnapshot.getValue ( MiResultado.class ).getVelocidad (), "", "", "", "", "", "", "" );
                                MaratonResult resultado = daoMarRes.ObtenerMaratonRes ( PostKey );
                                if (resultado == null) {
                                    daoMarRes.Insert ( nuevo );
                                } else {
                                    daoMarRes.Editar ( nuevo );
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                    DatosCarreraResult.orderByChild ( "velmed" ).limitToLast ( 1 ).addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren ()) {
                                minvel.setText ( postSnapshot.getValue ( MiResultado.class ).getVelmed() + "m/s" );
                                MaratonResult nuevo = new MaratonResult ( PostKey, "", postSnapshot.getValue ( MiResultado.class ).getVelocidad (), "", "", "", "", "", "", "", "", "" );
                                MaratonResult resultado = daoMarRes.ObtenerMaratonRes ( PostKey );
                                if (resultado == null) {
                                    daoMarRes.Insert ( nuevo );
                                } else {
                                    daoMarRes.Editar ( nuevo );
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                    DatosCarreraResult.orderByChild ( "tiempo" ).limitToFirst ( 1 ).addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren ()) {
                                float time = Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getTiempo () );
                                float segundos = time % 1;
                                float mintotales = time - segundos;
                                float calculo = mintotales / 60;
                                float decimales = calculo % 1;
                                float horas = calculo - decimales;
                                float minutos = mintotales - horas * 60;
                                String sec, minutosm;
                                if (segundos < 10) {
                                    sec = "0" + (int) segundos;
                                } else {
                                    sec = String.valueOf ( (int) segundos );
                                }
                                if (minutos < 10) {
                                    minutosm = "0" + (int) minutos;
                                } else {
                                    minutosm = String.valueOf ( (int) minutos );
                                }
                                mejtime.setText ( (int) horas + ":" + minutosm + ":" + sec );
                                float rtime = Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getTiempo () ) / (Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getDistancia () ) / 1000);
                                float rsegundos = rtime % 1;
                                float rmintotales = rtime - rsegundos;
                                float rcalculo = rmintotales / 60;
                                float rdecimales = rcalculo % 1;
                                float rhoras = rcalculo - rdecimales;
                                float rminutos = rmintotales - rhoras * 60;
                                mejritmo.setText ( (int) rminutos + "'" + (int) (rsegundos * 60) + "''" );
                                MaratonResult nuevo = new MaratonResult ( PostKey, "", "", "", "", "", (int) rminutos + "'" + (int) (rsegundos * 60) + "''", "", "", (int) horas + ":" + (int) minutos + ":" + (int) (segundos * 60), "", "" );
                                MaratonResult resultado = daoMarRes.ObtenerMaratonRes ( PostKey );
                                if (resultado == null) {
                                    daoMarRes.Insert ( nuevo );
                                } else {
                                    daoMarRes.Editar ( nuevo );
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                    DatosCarreraResult.orderByChild ( "tiempo" ).limitToLast ( 1 ).addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren ()) {
                                float time = Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getTiempo () );
                                float segundos = time % 1;
                                float mintotales = time - segundos;
                                float calculo = mintotales / 60;
                                float decimales = calculo % 1;
                                float horas = calculo - decimales;
                                float minutos = mintotales - horas * 60;

                                String sec, minutosm;
                                if (segundos < 10) {
                                    sec = "0" + (int) segundos;
                                } else {
                                    sec = String.valueOf ( (int) segundos );
                                }
                                if (minutos < 10) {
                                    minutosm = "0" + (int) minutos;
                                } else {
                                    minutosm = String.valueOf ( (int) minutos );
                                }
                                peortime.setText ( (int) horas + ":" + minutosm + ":" + sec );
                                float rtime = Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getTiempo () ) / (Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getDistancia () ) / 1000);
                                float rsegundos = rtime % 1;
                                float rmintotales = rtime - rsegundos;
                                float rcalculo = rmintotales / 60;
                                float rdecimales = rcalculo % 1;
                                float rhoras = rcalculo - rdecimales;
                                float rminutos = rmintotales - rhoras * 60;
                                peorritmo.setText ( (int) rminutos + "'" + (int) (rsegundos * 60) + "''" );
                                MaratonResult nuevo = new MaratonResult ( PostKey, "", "", "", "", "", "",
                                        (int) rminutos + "'" + (int) (rsegundos * 60) + "''", "", "", (int) horas + ":" + (int) minutos + ":" + (int) (segundos * 60), "" );
                                MaratonResult resultado = daoMarRes.ObtenerMaratonRes ( PostKey );
                                if (resultado == null) {
                                    daoMarRes.Insert ( nuevo );
                                } else {
                                    daoMarRes.Editar ( nuevo );
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                    DatosCarreraResult.addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren ()) {
                                listaresultadosglobales.add ( postSnapshot.getValue ( MiResultado.class ) );
                            }
                            CargarDatosGlob ();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                } else {
                    DatosCarreraResult.orderByChild ( "velmed" ).limitToFirst ( 1 ).addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren ()) {
                                maxvel.setText ( postSnapshot.getValue ( MiResultado.class ).getVelmed () + "m/s" );
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                    DatosCarreraResult.orderByChild ( "velmed" ).limitToLast ( 1 ).addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren ()) {
                                minvel.setText ( postSnapshot.getValue ( MiResultado.class ).getVelmed () + "m/s" );
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                    DatosCarreraResult.orderByChild ( "tiempo" ).limitToFirst ( 1 ).addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren ()) {
                                float time = Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getTiempo () );
                                float segundos = time % 1;
                                float mintotales = time - segundos;
                                float calculo = mintotales / 60;
                                float decimales = calculo % 1;
                                float horas = calculo - decimales;
                                float minutos = mintotales - horas * 60;
                                String sec, minutosm;
                                if (segundos < 10) {
                                    sec = "0" + (int) segundos;
                                } else {
                                    sec = String.valueOf ( (int) segundos );
                                }
                                if (minutos < 10) {
                                    minutosm = "0" + (int) minutos;
                                } else {
                                    minutosm = String.valueOf ( (int) minutos );
                                }
                                mejtime.setText ( (int) horas + ":" + minutosm + ":" + sec );
                                float rtime = Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getTiempo () ) / (Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getDistancia () ) / 1000);
                                float rsegundos = rtime % 1;
                                float rmintotales = rtime - rsegundos;
                                float rcalculo = rmintotales / 60;
                                float rdecimales = rcalculo % 1;
                                float rhoras = rcalculo - rdecimales;
                                float rminutos = rmintotales - rhoras * 60;
                                mejritmo.setText ( (int) rminutos + "'" + (int) (rsegundos * 60) + "''" );
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                    DatosCarreraResult.orderByChild ( "tiempo" ).limitToLast ( 1 ).addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren ()) {
                                float time = Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getTiempo () );
                                float segundos = time % 1;
                                float mintotales = time - segundos;
                                float calculo = mintotales / 60;
                                float decimales = calculo % 1;
                                float horas = calculo - decimales;
                                float minutos = mintotales - horas * 60;
                                String sec, minutosm;
                                if (segundos < 10) {
                                    sec = "0" + (int) segundos;
                                } else {
                                    sec = String.valueOf ( (int) segundos );
                                }
                                if (minutos < 10) {
                                    minutosm = "0" + (int) minutos;
                                } else {
                                    minutosm = String.valueOf ( (int) minutos );
                                }
                                peortime.setText ( (int) horas + ":" + minutosm + ":" + sec );

                                float rtime = Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getTiempo () ) / (Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getDistancia () ) / 1000);
                                float rsegundos = rtime % 1;
                                float rmintotales = rtime - rsegundos;
                                float rcalculo = rmintotales / 60;
                                float rdecimales = rcalculo % 1;
                                float rhoras = rcalculo - rdecimales;
                                float rminutos = rmintotales - rhoras * 60;
                                peorritmo.setText ( (int) rminutos + "'" + (int) (rsegundos * 60) + "''" );
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                    DatosCarreraResult.addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren ()) {
                                listaresultadosglobales.add ( postSnapshot.getValue ( MiResultado.class ) );
                            }
                            CargarDatosGlob ();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                }

            } else {
                pasosprom.setText ( Common.maratonResult.getPasos () );
                velocidadprom.setText ( Common.maratonResult.getVelmed () );
                maxvel.setText ( Common.maratonResult.getVelmax () );
                minvel.setText ( Common.maratonResult.getVelmin () );
                tiempoprom.setText ( Common.maratonResult.getTime () );
                if (Common.maratonResult.getMejtime ().split ( ":" )[1].length () < 2) {
                    mejtime.setText ( Common.maratonResult.getMejtime ().split ( ":" )[0] + ":" + "0" + Common.maratonResult.getMejtime ().split ( ":" )[1] + ":" + Common.maratonResult.getMejtime ().split ( ":" )[2] );
                } else {
                    mejtime.setText ( Common.maratonResult.getMejtime () );
                }
                peortime.setText ( Common.maratonResult.getPeortime () );
                mejritmo.setText ( Common.maratonResult.getMaxritmo () );
                peorritmo.setText ( Common.maratonResult.getMinritmo () );
                ritmoprom.setText ( Common.maratonResult.getRitmo () );
                caloriasprom.setText ( Common.maratonResult.getCalorias () );
            }


        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "CargarResultadosCarrera" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }

    }

    private void CargarMisDatos() {
        try {
            ArrayList<Punto> puntos = daoPuntos.ObtenerPuntos ( PostKey );
            if (puntos == null) {
                MaratonPointsRef.addListenerForSingleValueEvent ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot pointSnapshot : dataSnapshot.getChildren ()) {
                            listadatosloc.add ( pointSnapshot.getValue ( Punto.class ) );
                            Punto point = pointSnapshot.getValue ( Punto.class );
                            point.setUid ( pointSnapshot.getKey () );
                            point.setCarrera ( PostKey );
                            daoPuntos.Insert ( point );
                        }
                        GraficarTrayectoria ();
                        GenerarDatos ();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            } else {
                listadatosloc = puntos;
                GenerarDatos ();
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "CargarTrayectoria" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void CargarDatosGlob() {
        try {
            float tiempoglo = 0;
            float velocidadglo = 0;
            float pasosglo = 0;
            float caloriasglo = 0;
            float ritmopro = 0;
            int cantglo = 0;
            for (MiResultado dato : listaresultadosglobales) {
                cantglo = cantglo + 1;
                tiempoglo = tiempoglo + Float.valueOf ( dato.getTiempo () );
                velocidadglo = velocidadglo + Float.valueOf ( dato.getVelmed());
                pasosglo = pasosglo + Float.valueOf ( dato.getPasos () );
                caloriasglo = caloriasglo + Float.valueOf ( dato.getCalorias () );
                ritmopro = ritmopro + Float.valueOf ( dato.getTiempo () ) / (Float.valueOf ( dato.getDistancia () ) / 1000);
            }

            float time = tiempoglo / cantglo;
            float segundos = time % 1;
            float mintotales = time - segundos;
            float calculo = mintotales / 60;
            float decimales = calculo % 1;
            float horas = calculo - decimales;
            float minutos = mintotales - horas * 60;

            String sec, minutosm;
            if (segundos < 10) {
                sec = "0" + (int) segundos;
            } else {
                sec = String.valueOf ( (int) segundos );
            }
            if (minutos < 10) {
                minutosm = "0" + (int) minutos;
            } else {
                minutosm = String.valueOf ( (int) minutos );
            }
            tiempoprom.setText ( (int) horas + ":" + minutosm + ":" + sec );

            velocidadprom.setText ( formato1.format ( velocidadglo / cantglo ) + " m/s" );
            pasosprom.setText ( formato1.format ( pasosglo / cantglo ) );
            caloriasprom.setText ( formato1.format ( caloriasglo / cantglo ) + " cal" );
            float rtime = ritmopro / cantglo;
            float rsegundos = rtime % 1;
            float rmintotales = rtime - rsegundos;
            float rcalculo = rmintotales / 60;
            float rdecimales = rcalculo % 1;
            float rhoras = rcalculo - rdecimales;
            float rminutos = rmintotales - rhoras * 60;
            ritmoprom.setText ( (int) rminutos + "'" + (int) (rsegundos * 60) + "''" );
            if (Common.carrera.estado == "fin") {
                MaratonResult nuevo = new MaratonResult ( PostKey, formato1.format ( pasosglo / cantglo ), "", formato1.format ( velocidadglo / cantglo ), "",
                        formato1.format ( caloriasglo / cantglo ), "", "", (int) rminutos + "'" + (int) (rsegundos * 60) + "''", "", "",
                        (int) horas + ":" + (int) minutos + ":" + (int) (segundos * 60) );
                MaratonResult resultado = daoMarRes.ObtenerMaratonRes ( PostKey );
                if (resultado == null) {
                    daoMarRes.Insert ( nuevo );
                } else {
                    daoMarRes.Editar ( nuevo );
                }
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "CargarDatosGloba;es" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void GraficarTrayectoria() {
        try {
            Location locationa = null;
            int conta = 0;
            for (Punto point : listadatosloc) {
                conta = conta + 1;
                if (locationa == null) {
                    locationa = new Location ( "punto A" );
                    double lat = Double.valueOf ( point.latitud );
                    double longi = Double.valueOf ( point.longitud );
                    locationa.setLatitude ( lat );
                    locationa.setLongitude ( longi );
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
                if (conta == listadatosloc.size () / 2) {

                    LatLng coordenada = new LatLng ( Double.valueOf ( point.latitud ), Double.valueOf ( point.longitud ) );
                    CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom ( coordenada, 14 );

                    mMap.animateCamera ( miUbicacion );
                }

            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "GraficarTrayectoria" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void GenerarDatos() {
        try {
            int time = 0;
            TValues.add ( new PointValue ( 0, 0 ) );
            VValues.add ( new PointValue ( 0, 0 ) );
            for (Punto dato : listadatosloc) {
                String[] inicio = dato.getTimp ().split ( ":" );
                int start = Integer.parseInt ( inicio[0] ) * 3600 + Integer.parseInt ( inicio[1] ) * 60 + Integer.parseInt ( inicio[2] );
                time = time + start;

                TValues.add ( new PointValue ( Float.valueOf ( dato.getDistancia () ) / 1000, time ) );
                VValues.add ( new PointValue ( Float.valueOf ( dato.getDistancia () ) / 1000, Float.valueOf ( dato.getVelocidad () ) ) );
            }
            GraficaTiempo ();
            GraficaVelocidad ();


        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "GenerarDatos" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    private void GraficaVelocidad() {
        try {
            Line line = new Line(VValues);
            line.setColor(ChartUtils.COLOR_GREEN);
            line.setHasPoints(false);// too many values so don't draw points.
            List<Line> lines = new ArrayList<Line>();
            lines.add(line);
            datavel = new LineChartData(lines);
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            axisX.setName("Distancia [m]");
            axisX.setTextColor(ChartUtils.COLOR_BLUE);
            axisX.setMaxLabelChars(4);
            axisX.setFormatter(new SimpleAxisValueFormatter().setAppendedText("km".toCharArray()));
            axisY.setName("Velocidad [m/s]").setTextColor(ChartUtils.COLOR_BLUE);
            datavel.setAxisXBottom(axisX);
            datavel.setAxisYLeft(axisY);
            previewDatavel = new LineChartData(datavel);
            previewDatavel.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);

            chartvel.setLineChartData(datavel);
            chartvel.setZoomEnabled(false);
            chartvel.setScrollEnabled(false);

            previewChartvel.setLineChartData(previewDatavel);
            previewChartvel.setViewportChangeListener(new ViewportListenervel());
            Viewport tempViewport = new Viewport(chartvel.getMaximumViewport());
            float dx = tempViewport.width() / 3;
            tempViewport.inset(dx, 0);
            previewChartvel.setCurrentViewport(tempViewport);
            previewChartvel.setZoomType(ZoomType.HORIZONTAL);
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "Grafica Velocidad" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    private void GraficaTiempo() {
        try{
            Line line = new Line ( TValues );
            line.setColor ( ChartUtils.COLOR_BLUE );
            line.setHasPoints ( false );// too many values so don't draw points.
            List<Line> lines = new ArrayList<Line> ();
            lines.add ( line );
            data = new LineChartData ( lines );
            Axis axisX = new Axis ();
            axisX.setName ( "Distancia [km]" );
            axisX.setTextColor ( ChartUtils.COLOR_BLUE );
            axisX.setMaxLabelChars ( 4 );
            axisX.setFormatter ( new SimpleAxisValueFormatter ().setAppendedText ( "km".toCharArray () ) );
            Axis axisY = new Axis ().setHasLines ( true );
            axisY.setName ( "tiempo [s]" );
            axisY.setTextColor ( ChartUtils.COLOR_BLUE );
            data.setAxisXBottom ( axisX );
            data.setAxisYLeft ( axisY );
            previewData = new LineChartData ( data );
            previewData.getLines ().get ( 0 ).setColor ( ChartUtils.DEFAULT_DARKEN_COLOR );

            chart.setLineChartData ( data );
            chart.setZoomEnabled ( false );
            chart.setScrollEnabled ( false );

            previewChart.setLineChartData ( previewData );
            previewChart.setViewportChangeListener ( new ViewportListener () );
            Viewport tempViewport = new Viewport ( chart.getMaximumViewport () );
            float dx = tempViewport.width () / 3;
            tempViewport.inset ( dx, 0 );
            previewChart.setCurrentViewport ( tempViewport );
            previewChart.setZoomType ( ZoomType.HORIZONTAL );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "GraficaTiempo" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    private void GenerarGraficaKilometro() {
        try {
            List<PointValue> KVelocidad = new ArrayList<PointValue>();
            List<PointValue> MKVelocidad = new ArrayList<PointValue>();
            List<PointValue> KTiempo = new ArrayList<PointValue>();
            List<PointValue> MKTiempo = new ArrayList<PointValue>();
            KTiempo.add(new PointValue(0, 0));
            MKTiempo.add(new PointValue(0, 0));
            KVelocidad.add(new PointValue(0, 0));
            MKVelocidad.add(new PointValue(0, 0));
            int k = 0;
            for (MiResultado m : listadatoskil) {
                KTiempo.add(new PointValue(k + 1, Float.valueOf(m.getTiempomed())));
                KVelocidad.add(new PointValue(k + 1, Float.valueOf(m.getVelocidadmed())));
                k = k + 1;
            }
            int j = 0;
            for (MiResultado m : mires) {
                MKTiempo.add(new PointValue(j + 1, Float.valueOf(m.getTiempomed())));
                MKVelocidad.add(new PointValue(j + 1, Float.valueOf(m.getVelocidadmed())));
                j = j + 1;
            }
            Line line = new Line(KTiempo);
            line.setColor(ChartUtils.COLOR_GREEN);
            Line mline = new Line(MKTiempo);
            mline.setColor(ChartUtils.COLOR_BLUE);
            line.setHasPoints(true);// too many values so don't draw points.
            mline.setHasPoints(true);// too many values so don't draw points.

            Line linev = new Line(KVelocidad);
            linev.setColor(ChartUtils.COLOR_GREEN);
            Line mlinev = new Line(MKVelocidad);
            mlinev.setColor(ChartUtils.COLOR_BLUE);
            linev.setHasPoints(true);// too many values so don't draw points.
            mlinev.setHasPoints(true);// too many values so don't draw points.

            List<Line> lines = new ArrayList<Line>();
            lines.add(line);
            lines.add(mline);

            List<Line> linesv = new ArrayList<Line>();
            linesv.add(linev);
            linesv.add(mlinev);

            datatiempok = new LineChartData(lines);
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            axisX.setName("Distancia [km]");
            axisX.setTextColor(ChartUtils.COLOR_BLUE);
            axisX.setMaxLabelChars(4);
            axisX.setFormatter(new SimpleAxisValueFormatter().setAppendedText("km".toCharArray()));
            axisY.setName("Tiempo [min]").setTextColor(ChartUtils.COLOR_BLUE);
            datatiempok.setAxisXBottom(axisX);
            datatiempok.setAxisYLeft(axisY);
            charttiempok.setLineChartData(datatiempok);
            charttiempok.setZoomEnabled(false);
            charttiempok.setScrollEnabled(false);

            datavelk = new LineChartData(linesv);
            Axis axisXv = new Axis();
            Axis axisYv = new Axis().setHasLines(true);
            axisXv.setName("Distancia [km]");
            axisXv.setTextColor(ChartUtils.COLOR_BLUE);
            axisXv.setMaxLabelChars(4);
            axisXv.setFormatter(new SimpleAxisValueFormatter().setAppendedText("km".toCharArray()));
            axisYv.setName("Velociad [m/s]").setTextColor(ChartUtils.COLOR_BLUE);
            datavelk.setAxisXBottom(axisXv);
            datavelk.setAxisYLeft(axisYv);
            chartvelk.setLineChartData(datavelk);
            chartvelk.setZoomEnabled(false);
            chartvelk.setScrollEnabled(false);
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "GenerarGraficaKilometro" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<Punto> puntos = daoPuntos.ObtenerPuntos ( PostKey );
        if (puntos != null) {
            listadatosloc = puntos;
            GraficarTrayectoria ();
        }
    }

    private class ViewportListener implements ViewportChangeListener {
        @Override
        public void onViewportChanged(Viewport newViewport) {
            // don't use animation, it is unnecessary when using preview chart.
            chart.setCurrentViewport ( newViewport );
        }
    }

    private class ViewportListenervel implements ViewportChangeListener {

        @Override
        public void onViewportChanged(Viewport newViewport) {
            // don't use animation, it is unnecessary when using preview chart.
            chartvel.setCurrentViewport ( newViewport );
        }

    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {
        String values;

        public ValueTouchListener(String tiempo) {
            values = tiempo;
        }

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            if (values == "tiempo") {
                String Stime = "";
                String seco = "";
                String minu = "";
                float tot = value.getY ();
                int horas = (int) (tot / 3600);
                int min = (int) (tot / 60);
                int sec = (int) tot - horas * 3600 - min * 60;
                if (min < 10) {
                    minu = "0" + min;
                } else {
                    minu = String.valueOf ( min );
                }
                if (sec < 10) {
                    seco = "0" + sec;
                } else {
                    seco = String.valueOf ( sec );
                }
                if (horas == 0) {
                    Stime = minu + ":" + seco;
                } else {
                    Stime = horas + ":" + minu + ":" + seco;
                }

                Toast.makeText ( ClickMiMaratonActivity.this, "distancia: " + value.getX () + " " + values + ": " + Stime, Toast.LENGTH_SHORT ).show ();
            } else {
                Toast.makeText ( ClickMiMaratonActivity.this, "distancia: " + value.getX () + " " + values + ": " + value.getY (), Toast.LENGTH_SHORT ).show ();

            }

            if (pointIndex == listadatosloc.size ()) {
                pointIndex = pointIndex - 1;
            }
            LatLng coordenada = new LatLng ( Double.valueOf ( listadatosloc.get ( pointIndex ).latitud ), Double.valueOf ( listadatosloc.get ( pointIndex ).longitud ) );
            if (marcador != null) marcador.remove ();
            marcador = mMap.addMarker ( new MarkerOptions ()
                    .position ( coordenada )
                    .title ( "Mi posision actual" )
                    .icon ( BitmapDescriptorFactory.defaultMarker ( 100 ) )
            );
        }

        @Override
        public void onValueDeselected() {

        }
    }
}
