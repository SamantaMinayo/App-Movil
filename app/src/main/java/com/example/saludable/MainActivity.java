package com.example.saludable;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saludable.Model.Maraton;
import com.example.saludable.Model.MaratonResult;
import com.example.saludable.Model.MiResultado;
import com.example.saludable.Model.Post;
import com.example.saludable.Model.Punto;
import com.example.saludable.Model.User;
import com.example.saludable.Utils.Common;
import com.example.saludable.ViewHolder.PostViewHolder;
import com.example.saludable.localdatabase.DaoMarRes;
import com.example.saludable.localdatabase.DaoMaraton;
import com.example.saludable.localdatabase.DaoPuntos;
import com.example.saludable.localdatabase.DaoResultados;
import com.example.saludable.localdatabase.DaoUsers;
import com.example.saludable.localdatabase.DaoUsrMrtn;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private Toolbar mToolbar;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView NavProfileImage;
    private TextView NavProfileusername;
    private static final int CODIGO_PERMISO = 22;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostRef;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private String current_user_id;
    private DaoUsers daoUsers;
    private DaoMaraton daoMaraton;
    private DaoResultados daoResultados;
    private DaoMarRes daoMarRes;
    private DaoPuntos daoPuntos;
    private DaoUsrMrtn daoUsrMrtn;
    private ArrayList<MiResultado> listaresultadosglobales = new ArrayList<MiResultado> ();
    private DecimalFormat formato1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_main );

            FirebaseMessaging.getInstance ().subscribeToTopic ( "NewMaraton" );

            if (!checkPermissions ()) {
                requestPermissions ();
            }

            mAuth = FirebaseAuth.getInstance ();
            current_user_id = mAuth.getCurrentUser ().getUid ();

            UsersRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( current_user_id );
            PostRef = FirebaseDatabase.getInstance ().getReference ().child ( "Posts" );

            mToolbar = findViewById ( R.id.main_page_toolbar );
            setSupportActionBar ( mToolbar );
            getSupportActionBar ().setTitle ( "Home" );

            drawerLayout = findViewById ( R.id.drawable_layout );
            actionBarDrawerToggle = new ActionBarDrawerToggle ( MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_open );
            drawerLayout.addDrawerListener ( actionBarDrawerToggle );
            actionBarDrawerToggle.syncState ();
            getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

            daoMaraton = new DaoMaraton ( this );
            daoResultados = new DaoResultados ( this );
            daoMarRes = new DaoMarRes ( this );
            daoPuntos = new DaoPuntos ( this );
            daoUsrMrtn = new DaoUsrMrtn ( this );
            formato1 = new DecimalFormat ( "#.00" );

            navigationView = findViewById ( R.id.navigation_view );
            postList = findViewById ( R.id.all_users_post_list );
            postList.setHasFixedSize ( true );

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager ( this );
            linearLayoutManager.setReverseLayout ( true );
            linearLayoutManager.setStackFromEnd ( true );
            postList.setLayoutManager ( linearLayoutManager );

            View navView = navigationView.inflateHeaderView ( R.layout.navigation_header );
            NavProfileImage = navView.findViewById ( R.id.nav_profile_image );
            NavProfileusername = navView.findViewById ( R.id.nav_user_full_name );

            daoUsers = new DaoUsers ( MainActivity.this );

            navigationView.setNavigationItemSelectedListener ( new NavigationView.OnNavigationItemSelectedListener () {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    UserMenuSelector ( item );
                    return false;
                }
            } );
        } catch (Exception e) {
        }

    }


    @Override
    protected void onStart() {
        try {
            super.onStart ();
            FirebaseUser currentUser = mAuth.getCurrentUser ();
            if (currentUser == null) {
                SendUserTologinActivity ();
            } else {
                CheckUserExistence ();
            }

        } catch (Exception e) {
        }
    }


    private void CheckUserExistence() {
        try {

            Common.loggedUser = daoUsers.ObtenerUsuario ();

            if (Common.loggedUser != null) {
                NavProfileusername.setText ( Common.loggedUser.getFullname () );
                Picasso.with ( MainActivity.this ).load ( "file://" + Common.loggedUser.getProfileimage () ).placeholder ( R.drawable.profile ).into ( NavProfileImage );
            } else {
                UsersRef.addListenerForSingleValueEvent ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists ()) {
                            SendUserToSetupActivity ();
                        } else {
                            if (!dataSnapshot.child ( "Informacion" ).hasChild ( "username" )) {
                                SendUserToSetupActivity ();
                            } else {
                                Common.loggedUser = dataSnapshot.child ( "Informacion" ).getValue ( User.class );
                                Common.loggedUser.setUid ( current_user_id );
                                NavProfileusername.setText ( Common.loggedUser.getFullname () );
                                Picasso.with ( MainActivity.this ).load ( Common.loggedUser.getProfileimage () ).placeholder ( R.drawable.profile ).into ( NavProfileImage );
                                LlenarDB ();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            }

            DisplayAllPosts ();

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MainActivity" ).child ( "OnCreate" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void LlenarDB() {

        CargarUsuario ();
        CargarMisCarrerasResult ();
        CargarMisCarrerasIns ();
    }

    private void CargarUsuario() {
        User usr = daoUsers.ObtenerUsuario ();
        if (usr == null) {
            daoUsers.Insert ( Common.loggedUser );
            final long ONE_MEGABYTE = 512 * 512;
            FirebaseStorage.getInstance ().getReference ().child ( "ProfileImages" )
                    .child ( Common.loggedUser.getUid () + ".jpg" ).getBytes ( ONE_MEGABYTE )
                    .addOnSuccessListener ( new OnSuccessListener<byte[]> () {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray ( bytes, 0, bytes.length );

                            try {
                                daoUsers.InsertImagen ( Common.loggedUser.getUid (), bitmap, getApplication () );
                            } catch (IOException e) {
                                e.printStackTrace ();
                            }

                        }
                    } );
        }
    }

    private void CargarMisCarrerasResult() {

        if (daoUsrMrtn.ObtenerMaratonList ( "fin" ) == null) {
            FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( current_user_id )
                    .child ( "Resultados" ).child ( "Lista" ).addListenerForSingleValueEvent ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    for (DataSnapshot marSnapshot : dataSnapshot.getChildren ()) {
                        final Maraton mar = marSnapshot.getValue ( Maraton.class );
                        Maraton mimar = daoUsrMrtn.Obtener ( mar.getUid () );
                        if (mimar == null) {
                            daoUsrMrtn.Insert ( new Maraton ( 1, mar.getUid (), "fin" ) );
                        }
                        if (daoPuntos.ObtenerPuntos ( mar.getUid () ) == null) {
                            FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Datos" )
                                    .child ( mar.getUid () ).child ( current_user_id ).addListenerForSingleValueEvent ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot pointsSnapshot) {
                                    for (DataSnapshot ptSnapshot : pointsSnapshot.getChildren ()) {
                                        Punto point = ptSnapshot.getValue ( Punto.class );
                                        point.setUid ( ptSnapshot.getKey () );
                                        point.setCarrera ( mar.getUid () );
                                        daoPuntos.Insert ( point );
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            } );

                        }
                        if (daoMaraton.ObtenerMaraton ( mar.getUid () ) == null) {
                            FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Nuevas" )
                                    .child ( mar.getUid () ).addListenerForSingleValueEvent ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot datasSnapshot) {
                                    if (datasSnapshot.exists ()) {
                                        daoMaraton.InsertEditar ( datasSnapshot.getValue ( Maraton.class ) );
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            } );
                            final long ONE_MEGABYTE = 1024 * 1024;
                            FirebaseStorage.getInstance ().getReference ().child ( "MarathonImages" )
                                    .child ( mar.getUid () ).getBytes ( ONE_MEGABYTE )
                                    .addOnSuccessListener ( new OnSuccessListener<byte[]> () {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray ( bytes, 0, bytes.length );

                                            try {
                                                daoMaraton.InsertImagen ( mar.getUid (), bitmap, getApplication () );
                                            } catch (IOException e) {
                                                e.printStackTrace ();
                                            }

                                        }
                                    } );
                        }
                        if (daoResultados.ObtenerResultado ( mar.getUid () ) == null) {
                            FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( current_user_id ).child ( "Resultados" ).child ( "Resultado" ).
                                    child ( mar.getUid () ).addListenerForSingleValueEvent ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists ()) {
                                        MiResultado dato = dataSnapshot.getValue ( MiResultado.class );
                                        dato.setUid ( dataSnapshot.getKey () );
                                        daoResultados.Insert ( dato );
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            } );
                        }
                        if (daoMarRes.ObtenerMaratonRes ( mar.getUid () ) == null) {
                            FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Resultados" ).child ( mar.getUid () )
                                    .orderByChild ( "velocidad" ).limitToFirst ( 1 ).addValueEventListener ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren ()) {
                                        MaratonResult nuevo = new MaratonResult ( mar.getUid (), "", postSnapshot.getValue ( MiResultado.class ).getVelocidad (), "", "", "", "", "", "", "", "", "" );
                                        MaratonResult resultado = daoMarRes.ObtenerMaratonRes ( mar.getUid () );
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
                            FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Resultados" ).child ( mar.getUid () )
                                    .orderByChild ( "velocidad" ).limitToLast ( 1 ).addValueEventListener ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren ()) {
                                        MaratonResult nuevo = new MaratonResult ( mar.getUid (), "", "", "", postSnapshot.getValue ( MiResultado.class ).getVelocidad (), "", "", "", "", "", "", "" );
                                        MaratonResult resultado = daoMarRes.ObtenerMaratonRes ( mar.getUid () );
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
                            FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Resultados" ).child ( mar.getUid () )
                                    .orderByChild ( "tiempo" ).limitToFirst ( 1 ).addValueEventListener ( new ValueEventListener () {
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
                                        float rtime = Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getTiempo () ) / (Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getDistancia () ) / 1000);
                                        float rsegundos = rtime % 1;
                                        float rmintotales = rtime - rsegundos;
                                        float rcalculo = rmintotales / 60;
                                        float rdecimales = rcalculo % 1;
                                        float rhoras = rcalculo - rdecimales;
                                        float rminutos = rmintotales - rhoras * 60;
                                        MaratonResult nuevo = new MaratonResult ( mar.getUid (), "", "", "", "", "", (int) rminutos + "'" + (int) (rsegundos * 60) + "''", "", "", (int) horas + ":" + (int) minutos + ":" + (int) (segundos * 60), "", "" );
                                        MaratonResult resultado = daoMarRes.ObtenerMaratonRes ( mar.getUid () );
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
                            FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Resultados" ).child ( mar.getUid () )
                                    .orderByChild ( "tiempo" ).limitToLast ( 1 ).addValueEventListener ( new ValueEventListener () {
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
                                        float rtime = Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getTiempo () ) / (Float.valueOf ( postSnapshot.getValue ( MiResultado.class ).getDistancia () ) / 1000);
                                        float rsegundos = rtime % 1;
                                        float rmintotales = rtime - rsegundos;
                                        float rcalculo = rmintotales / 60;
                                        float rdecimales = rcalculo % 1;
                                        float rhoras = rcalculo - rdecimales;
                                        float rminutos = rmintotales - rhoras * 60;
                                        MaratonResult nuevo = new MaratonResult ( mar.getUid (), "", "", "", "", "", "",
                                                (int) rminutos + "'" + (int) (rsegundos * 60) + "''", "", "", (int) horas + ":" + (int) minutos + ":" + (int) (segundos * 60), "" );
                                        MaratonResult resultado = daoMarRes.ObtenerMaratonRes ( mar.getUid () );
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
                            FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Resultados" ).child ( mar.getUid () )
                                    .addValueEventListener ( new ValueEventListener () {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for (DataSnapshot postSnapshot : snapshot.getChildren ()) {
                                                listaresultadosglobales.add ( postSnapshot.getValue ( MiResultado.class ) );
                                            }
                                            CargarDatosGlob ( mar.getUid () );
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    } );
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }

    }

    private void CargarDatosGlob(String PostKey) {
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
                velocidadglo = velocidadglo + Float.valueOf ( dato.getVelocidad () );
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
            float rtime = ritmopro / cantglo;
            float rsegundos = rtime % 1;
            float rmintotales = rtime - rsegundos;
            float rcalculo = rmintotales / 60;
            float rdecimales = rcalculo % 1;
            float rhoras = rcalculo - rdecimales;
            float rminutos = rmintotales - rhoras * 60;

            MaratonResult nuevo = new MaratonResult ( PostKey, formato1.format ( pasosglo / cantglo ), "", formato1.format ( velocidadglo / cantglo ), "",
                    formato1.format ( caloriasglo / cantglo ), "", "", (int) rminutos + "'" + (int) (rsegundos * 60) + "''", "", "",
                    (int) horas + ":" + (int) minutos + ":" + (int) (segundos * 60) );
            MaratonResult resultado = daoMarRes.ObtenerMaratonRes ( PostKey );
            if (resultado == null) {
                daoMarRes.Insert ( nuevo );
            } else {
                daoMarRes.Editar ( nuevo );
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMiMaratonActivity" ).child ( "CargarDatosGloba;es" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void CargarMisCarrerasIns() {

        if (daoUsrMrtn.ObtenerMaratonList ( "ins" ) == null) {
            FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( current_user_id )
                    .child ( "Inscripcion" ).addListenerForSingleValueEvent ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    for (DataSnapshot marSnapshot : dataSnapshot.getChildren ()) {
                        final Maraton mar = marSnapshot.getValue ( Maraton.class );
                        if (daoUsrMrtn.Obtener ( mar.getUid () ) == null) {
                            daoUsrMrtn.Insert ( new Maraton ( 1, mar.getUid (), "ins" ) );
                        }
                        if (daoMaraton.ObtenerMaraton ( mar.getUid () ) == null) {
                            FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Nuevas" )
                                    .child ( mar.getUid () ).addListenerForSingleValueEvent ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists ()) {
                                        daoMaraton.InsertEditar ( dataSnapshot.getValue ( Maraton.class ) );
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            } );
                        }
                        final long ONE_MEGABYTE = 1024 * 1024;
                        FirebaseStorage.getInstance ().getReference ().child ( "MarathonImages" )
                                .child ( mar.getUid () ).getBytes ( ONE_MEGABYTE )
                                .addOnSuccessListener ( new OnSuccessListener<byte[]> () {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray ( bytes, 0, bytes.length );

                                        try {
                                            daoMaraton.InsertImagen ( mar.getUid (), bitmap, getApplication () );
                                        } catch (IOException e) {
                                            e.printStackTrace ();
                                        }

                                    }
                                } );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }
    }

    protected void DisplayAllPosts() {
        try {
            FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post> ()
                    .setQuery ( PostRef, Post.class ).build ();

            firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Post, PostViewHolder> ( options ) {
                        @Override
                        public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from ( parent.getContext () )
                                    .inflate ( R.layout.all_post_layout, parent, false );
                            return new PostViewHolder ( view );
                        }

                        @Override
                        protected void onBindViewHolder(PostViewHolder postViewHolder, int position, @NonNull Post post) {

                            postViewHolder.postadname.setText ( post.fullname );
                            postViewHolder.posttime.setText ( post.time );
                            postViewHolder.postdate.setText ( post.date );
                            postViewHolder.postdescript.setText ( post.description );
                            Picasso.with ( getApplication () ).load ( post.profileimage ).into ( postViewHolder.postimgprof );
                            Picasso.with ( getApplication () ).load ( post.postimage ).into ( postViewHolder.postimage );
                        }
                    };
            postList.setAdapter ( firebaseRecyclerAdapter );
            firebaseRecyclerAdapter.startListening ();

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MainActivity" ).child ( "DisplayAllPosts" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            if (actionBarDrawerToggle.onOptionsItemSelected ( item )) {
                return true;
            }
            return super.onOptionsItemSelected ( item );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MainActivity" ).child ( "onOptionsItemSelected" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
            return false;
        }
    }


    private void UserMenuSelector(MenuItem item) {
        try {
            switch (item.getItemId ()) {
                case R.id.nav_profile:
                    SendUsertoProfileActivity ();
                    break;
                case R.id.nav_home:
                    Toast.makeText ( this, "Home", Toast.LENGTH_SHORT ).show ();
                    break;
                case R.id.nav_mi_inscripcion:
                    SendUserToMiInscripcionActivity ();
                    break;
                case R.id.nav_find_marathon:
                    SendUserToMaratonActivity ();
                    break;
                case R.id.nav_mi_marathon:
                    SendUserToMiMaratonActivity ();
                    break;
                case R.id.nav_ayuda:
                    Toast.makeText ( this, "Ayuda", Toast.LENGTH_SHORT ).show ();
                    break;
                case R.id.nav_Logout:
                    daoUsers.Eliminar ( Common.loggedUser.getUid () );
                    mAuth.signOut ();
                    SendUserTologinActivity ();
                    break;
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MainActivity" ).child ( "UserMenuSelected" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void SendUserToSetupActivity() {
        try {
            Intent setupIntent = new Intent ( MainActivity.this, SetupActivity.class );
            setupIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( setupIntent );
            finish ();
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MainActivity" ).child ( "SendUserToSetupActivity" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void SendUserTologinActivity() {
        try {
            Common.loggedUser = null;
            Intent loginIntent = new Intent ( MainActivity.this, LoginActivity.class );
            loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( loginIntent );
            finish ();
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MainActivity" ).child ( "SendUserToLoginActivity" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void SendUsertoProfileActivity() {
        try {
            Intent loginIntent = new Intent ( MainActivity.this, ProfileActivity.class );
            startActivity ( loginIntent );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MainActivity" ).child ( "SendUserToProfileActivity" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void SendUserToMaratonActivity() {
        try {
            Intent addNewPostIntent = new Intent ( MainActivity.this, MaratonActivity.class );
            startActivity ( addNewPostIntent );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MainActivity" ).child ( "SendUserToMaratonActivity" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void SendUserToMiMaratonActivity() {
        try {
            Intent addNewPostIntent = new Intent ( MainActivity.this, MiMaratonActivity.class );
            startActivity ( addNewPostIntent );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MainActivity" ).child ( "SendUserToMiMaratonActivity" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void SendUserToMiInscripcionActivity() {
        try {
            Intent addNewPostIntent = new Intent ( MainActivity.this, MiInscripcionActivity.class );
            startActivity ( addNewPostIntent );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MainActivity" ).child ( "SendUserToMiInscripcionActivity" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private boolean checkPermissions() {
        try {
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission ( this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE );
        } catch (Exception e) {
            return false;
        }
    }

    private void requestPermissions() {
        try {
            ActivityCompat.requestPermissions ( MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODIGO_PERMISO );
        } catch (Exception e) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        try {
            if (requestCode == CODIGO_PERMISO) {
                if (grantResults.length <= 0) {

                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText ( this, "Permiso Concedido", Toast.LENGTH_SHORT );
                } else {
                    Toast.makeText ( this, "Permiso Negado", Toast.LENGTH_SHORT );
                    Snackbar.make (
                            findViewById ( R.id.activity_login ),
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

        } catch (Exception e) {
        }
    }

}
