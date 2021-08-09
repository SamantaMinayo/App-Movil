package com.example.saludable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.saludable.Model.Maraton;
import com.example.saludable.Model.Punto;
import com.example.saludable.Service.Utils;
import com.example.saludable.Utils.Common;
import com.example.saludable.localdatabase.DaoMaraton;
import com.example.saludable.localdatabase.DaoPuntos;
import com.example.saludable.localdatabase.DaoUsrMrtn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClickMaratonActivity extends AppCompatActivity {


    private ImageView maratonImage;
    private EditText codigo;
    private TextView maratonName, maratondescription, maratondate, maratontime, maratoncontactname, maratoncontactnumber, maratonPlace, mensaje, maratondist;
    private Button registermaratonButton, cancelregistermaratonButton, monitorearmaratonButton, web;
    private WebView maratontrayectoria;

    DaoMaraton daoMaraton;
    DaoUsrMrtn daousrMrtn;
    private DatabaseReference RegistrarUsuario, RegistrarCarrera, MaratonDatosRef, ResultadoUsuario, UsuarioMon;
    private FirebaseAuth mAuth;

    private String PostKey, current_user_id;
    private String codigos;
    Bitmap bitmap;
    private ProgressDialog loadingBar;
    private Toolbar mToolbar;
    private DaoPuntos daoPuntos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_click_maraton );

            mAuth = FirebaseAuth.getInstance ();
            current_user_id = mAuth.getCurrentUser ().getUid ();

            PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
            MaratonDatosRef = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Nuevas" ).child ( PostKey );
            RegistrarUsuario = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( current_user_id ).child ( "Inscripcion" ).child ( PostKey );
            ResultadoUsuario = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( current_user_id ).child ( "Resultados" ).child ( "Lista" ).child ( PostKey );
            RegistrarCarrera = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Inscripcion" ).child ( PostKey ).child ( current_user_id );
            UsuarioMon = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Inicio" ).child ( PostKey ).child ( current_user_id );
            maratonImage = findViewById ( R.id.maraton_image_principal );
            maratonName = findViewById ( R.id.maraton_name_principal );
            maratondescription = findViewById ( R.id.maraton_description_principal );
            maratonPlace = findViewById ( R.id.maraton_place_principal );
            maratondist = findViewById ( R.id.maraton_distance );
            codigo = findViewById ( R.id.codigo );
            web = findViewById ( R.id.web );
            daoMaraton = new DaoMaraton ( this );
            daousrMrtn = new DaoUsrMrtn ( this );
            daoPuntos = new DaoPuntos ( this );

            maratontrayectoria = findViewById ( R.id.maraton_trayectoria );
            final WebSettings ajustesVisorWeb = maratontrayectoria.getSettings ();
            ajustesVisorWeb.setJavaScriptEnabled ( true );

            maratondate = findViewById ( R.id.maraton_date );
            maratontime = findViewById ( R.id.maraton_time );
            maratoncontactname = findViewById ( R.id.maraton_contact_name_principal );
            maratoncontactnumber = findViewById ( R.id.maraton_contact_number_principal );
            monitorearmaratonButton = findViewById ( R.id.monitorear_maraton_button );
            mensaje = findViewById ( R.id.Mensaje );

            mToolbar = findViewById ( R.id.carrera_toolbar );
            setSupportActionBar ( mToolbar );
            getSupportActionBar ().setTitle ( "Carrera" );
            getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

            loadingBar = new ProgressDialog ( this );

            registermaratonButton = findViewById ( R.id.register_maraton_button );
            cancelregistermaratonButton = findViewById ( R.id.cancel_register_maraton_button );

            CargarDatosCarrera ();
            VerificarResultados ();

            registermaratonButton.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    Inscripcion ( true );
                }
            } );

            cancelregistermaratonButton.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    Inscripcion ( false );
                }
            } );

            monitorearmaratonButton.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    if (codigo.getText ().toString ().equals ( codigos )) {
                        HashMap usuario = new HashMap ();
                        usuario.put ( "userName", Common.loggedUser.fullname );
                        usuario.put ( "userImagen", Common.loggedUser.profileimage );
                        usuario.put ( "uid", current_user_id );
                        UsuarioMon.updateChildren ( usuario );
                        SendUserToMaratonActivity ();
                    } else {
                        Toast.makeText ( ClickMaratonActivity.this,
                                "Codigo Incorrecto. Vuelva a intenter", Toast.LENGTH_SHORT ).show ();
                        loadingBar.dismiss ();
                    }
                }
            } );
            web.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse ( Common.carrera.maratontrayectoriaweb );
                    Intent intent = new Intent ( Intent.ACTION_VIEW, uri );
                    startActivity ( intent );
                }
            } );

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMaratonActivity" ).child ( "onCreate" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    @Override
    protected void onStart() {
        try {
            super.onStart ();
            MaratonDatosRef.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    codigos = (String) dataSnapshot.child ( "codigo" ).getValue ();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
            Utils.setRequestingLocationUpdates ( this, false );
            VerificarResultados ();
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMaratonActivity" ).child ( "onStart" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void CargarDatosCarrera() {
        try {
                MaratonDatosRef.addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists ()) {
                            Common.carrera = dataSnapshot.getValue ( Maraton.class );
                            Picasso.with ( ClickMaratonActivity.this ).load ( Common.carrera.maratonimage ).into ( maratonImage );
                            maratondescription.setText ( Common.carrera.description );
                            maratoncontactname.setText ( Common.carrera.contactname );
                            maratoncontactnumber.setText ( Common.carrera.contactnumber );
                            maratontime.setText ( Common.carrera.maratontime );
                            maratondate.setText ( Common.carrera.maratondate + " " );
                            maratonPlace.setText ( "Lugar: " + Common.carrera.place );
                            maratonName.setText ( Common.carrera.maratonname );
                            maratondist.setText ( "Distancia: " + Common.carrera.maratondist + " km" );
                            maratontrayectoria.loadUrl ( Common.carrera.maratontrayectoriaweb );
                            maratontrayectoria.setWebViewClient ( new WebViewClient () );
                            maratonImage.buildDrawingCache ();
                            bitmap = maratonImage.getDrawingCache ();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            Common.carrera = daoMaraton.ObtenerMaraton ( PostKey );
            if (Common.carrera != null) {
                final long ONE_MEGABYTE = 1024 * 1024;
                FirebaseStorage.getInstance ().getReference ().child ( "MarathonImages" )
                        .child ( PostKey ).getBytes ( ONE_MEGABYTE )
                        .addOnSuccessListener ( new OnSuccessListener<byte[]> () {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray ( bytes, 0, bytes.length );

                                try {
                                    daoMaraton.InsertImagen ( PostKey, bitmap, getApplication () );
                                } catch (IOException e) {
                                    e.printStackTrace ();
                                }

                            }
                        } );
                daoMaraton.InsertEditar ( Common.carrera );
                Picasso.with ( ClickMaratonActivity.this ).load ( "file://" + Common.carrera.image ).into ( maratonImage );
                maratondescription.setText ( Common.carrera.description );
                maratoncontactname.setText ( Common.carrera.contactname );
                maratoncontactnumber.setText ( Common.carrera.contactnumber );
                maratontime.setText ( Common.carrera.maratontime );
                maratondate.setText ( Common.carrera.maratondate + " " );
                maratonPlace.setText ( "Lugar: " + Common.carrera.place );
                maratonName.setText ( Common.carrera.maratonname );
                maratondist.setText ( "Distancia: " + Common.carrera.maratondist + " km" );
                maratontrayectoria.loadUrl ( Common.carrera.maratontrayectoriaweb );
                maratontrayectoria.setWebViewClient ( new WebViewClient () );
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMaratonActivity" ).child ( "CargarDatosCarrera" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }


    private void VerificarResultados() {
        try {

            ArrayList<Punto> puntos = daoPuntos.ObtenerPuntos ( PostKey );
            if (puntos == null) {
                VerificarInscripcion ();
                ResultadoUsuario.addListenerForSingleValueEvent ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists ()) {
                            registermaratonButton.setEnabled ( false );
                            cancelregistermaratonButton.setEnabled ( false );
                            monitorearmaratonButton.setEnabled ( false );
                            registermaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.backbutton ) );
                            cancelregistermaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.backbutton ) );
                            monitorearmaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.backbutton ) );
                            mensaje.setText ( "Usted ya registro datos en esta carrera. Dirigase a mis Resultados" );
                        } else {
                            VerificarInscripcion ();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            } else {
                registermaratonButton.setEnabled ( false );
                cancelregistermaratonButton.setEnabled ( false );
                monitorearmaratonButton.setEnabled ( false );
                mensaje.setText ( "Usted ya registro datos en esta carrera. Dirigase a mis Resultados" );
                registermaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.backbutton ) );
                cancelregistermaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.backbutton ) );
                monitorearmaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.backbutton ) );

            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMaratonActivity" ).child ( "VerificarResultados" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void VerificarInscripcion() {
        try {
            Maraton mar = daoMaraton.ObtenerMaraton ( PostKey );
            if (mar == null) {
                RegistrarCarrera.addListenerForSingleValueEvent ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists ()) {
                            registermaratonButton.setEnabled ( false );
                            cancelregistermaratonButton.setEnabled ( true );
                            monitorearmaratonButton.setEnabled ( true );
                            registermaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.backbutton ) );
                            cancelregistermaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.button ) );
                            monitorearmaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.button ) );
                            mensaje.setText ( "Ingrese el codigo de la carrera para iniciar el monitoreo" );
                        } else {
                            registermaratonButton.setEnabled ( true );
                            cancelregistermaratonButton.setEnabled ( false );
                            monitorearmaratonButton.setEnabled ( false );
                            registermaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.button ) );
                            cancelregistermaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.backbutton ) );
                            monitorearmaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.backbutton ) );
                            mensaje.setText ( "INSCRIBETE!!" );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            } else {
                registermaratonButton.setEnabled ( false );
                cancelregistermaratonButton.setEnabled ( true );
                monitorearmaratonButton.setEnabled ( true );
                registermaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.backbutton ) );
                cancelregistermaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.button ) );
                monitorearmaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.button ) );
                mensaje.setText ( "Ingrese el codigo de la carrera para iniciar el monitoreo" );
            }

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMaratonActivity" ).child ( "VerificarInscripcion" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void Inscripcion(boolean inscribir) {
        try {
            if (inscribir == false) {
                loadingBar.setTitle ( "Cancelar Inscripcion" );
                loadingBar.setMessage ( "Espere mientras cancelamos su inscripcion en la carrera." );
                loadingBar.setCanceledOnTouchOutside ( true );
                loadingBar.show ();
                RegistrarUsuario.removeValue ();
                RegistrarCarrera.removeValue ().addOnCompleteListener ( new OnCompleteListener () {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful ()) {
                            registermaratonButton.setEnabled ( true );
                            cancelregistermaratonButton.setEnabled ( false );
                            monitorearmaratonButton.setEnabled ( false );
                            registermaratonButton.setBackground ( getResources ().
                                    getDrawable ( R.drawable.button ) );
                            cancelregistermaratonButton.setBackground ( getResources ().
                                    getDrawable ( R.drawable.backbutton ) );
                            monitorearmaratonButton.setBackground ( getResources ().
                                    getDrawable ( R.drawable.backbutton ) );
                            mensaje.setText ( "INSCRIBETE!" );
                            Toast.makeText ( ClickMaratonActivity.this,
                                    "Ha cancelado su inscripcion correctamente.",
                                    Toast.LENGTH_SHORT ).show ();
                            daoMaraton.Eliminar ( PostKey );
                            daousrMrtn.Eliminar ( PostKey );
                            loadingBar.dismiss ();
                        } else {
                            String message = task.getException ().getMessage ();
                            Toast.makeText ( ClickMaratonActivity.this, "A ocurrido un error: "
                                    + message, Toast.LENGTH_SHORT ).show ();
                            loadingBar.dismiss ();
                        }
                    }
                } );
            } else {
                loadingBar.setTitle ( "Inscripcion" );
                loadingBar.setMessage ( "Espere mientra lo inscribimos en la carrera." );
                loadingBar.setCanceledOnTouchOutside ( true );
                loadingBar.show ();
                HashMap crear = new HashMap ();
                crear.put ( "maratonname", Common.carrera.maratonname );
                crear.put ( "maratonddate", Common.carrera.maratondate + " : " +
                        Common.carrera.maratontime );
                crear.put ( "maratonimage", Common.carrera.maratonimage );
                crear.put ( "description", Common.carrera.description );
                crear.put ( "uid", Common.carrera.uid );
                RegistrarUsuario.updateChildren ( crear );
                HashMap usuario = new HashMap ();
                usuario.put ( "userName", Common.loggedUser.fullname );
                usuario.put ( "userGenero", Common.loggedUser.genero );
                usuario.put ( "userEdad", Common.loggedUser.edad );
                usuario.put ( "userImagen", Common.loggedUser.profileimage );
                usuario.put ( "uid", current_user_id );
                RegistrarCarrera.updateChildren ( usuario ).addOnCompleteListener ( new OnCompleteListener () {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful ()) {
                            registermaratonButton.setEnabled ( false );
                            cancelregistermaratonButton.setEnabled ( true );
                            monitorearmaratonButton.setEnabled ( true );
                            registermaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.backbutton ) );
                            cancelregistermaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.button ) );
                            monitorearmaratonButton.setBackground ( getResources ().getDrawable ( R.drawable.button ) );
                            mensaje.setText ( "Ingrese el codigo de la carrera para iniciar el monitoreo" );
                            Toast.makeText ( ClickMaratonActivity.this, "Su inscripcion se realizo exitosamente", Toast.LENGTH_SHORT ).show ();

                            final long ONE_MEGABYTE = 512 * 512;
                            FirebaseStorage.getInstance ().getReference ().child ( "MarathonImages" )
                                    .child ( PostKey ).getBytes ( ONE_MEGABYTE )
                                    .addOnSuccessListener ( new OnSuccessListener<byte[]> () {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray ( bytes, 0, bytes.length );

                                            try {
                                                daoMaraton.InsertImagen ( PostKey, bitmap, getApplication () );
                                            } catch (IOException e) {
                                                e.printStackTrace ();
                                            }
                                        }
                                    } );
                            daoMaraton.InsertEditar ( Common.carrera );
                            Maraton usrM = daousrMrtn.Obtener ( PostKey );
                            if (usrM == null) {
                                usrM = new Maraton ( 1, PostKey, "ins" );
                                daousrMrtn.Insert ( usrM );
                            } else {
                                usrM = new Maraton ( 1, PostKey, "ins" );
                                daousrMrtn.Editar ( usrM );
                            }
                            loadingBar.dismiss ();
                        } else {
                            String message = task.getException ().getMessage ();
                            Toast.makeText ( ClickMaratonActivity.this,
                                    "A ocurrido un error: " + message, Toast.LENGTH_SHORT )
                                    .show ();
                            loadingBar.dismiss ();
                        }
                    }
                } );
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMaratonActivity" ).child ( "Inscripcion" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void SendUserToMaratonActivity() {
        try {
            Intent ClickMaratonIntent = new Intent ( ClickMaratonActivity.this, MapsActivity.class );
            ClickMaratonIntent.putExtra ( "PostKey", PostKey );
            startActivity ( ClickMaratonIntent );

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "ClickMaratonActivity" ).child ( "SendUserToMaratonActivity" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }
}
