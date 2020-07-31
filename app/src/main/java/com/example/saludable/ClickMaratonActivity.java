package com.example.saludable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.saludable.Model.Maraton;
import com.example.saludable.Utils.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ClickMaratonActivity extends AppCompatActivity {


    private ImageView maratonImage;
    private TextView maratonName, maratondescription, maratondate, maratontime, maratoncontactname, maratoncontactnumber, maratonPlace, mensaje;
    private Button registermaratonButton, cancelregistermaratonButton, monitorearmaratonButton;

    private DatabaseReference RegistrarUsuario, RegistrarCarrera, MaratonDatosRef;
    private FirebaseAuth mAuth;

    private String PostKey, current_user_id;

    private ProgressDialog loadingBar;
    private Toolbar mToolbar;


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
            RegistrarCarrera = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( "Inscripcion" ).child ( PostKey ).child ( current_user_id );

            maratonImage = findViewById ( R.id.maraton_image_principal );
            maratonName = findViewById ( R.id.maraton_name_principal );
            maratondescription = findViewById ( R.id.maraton_description_principal );
            maratonPlace = findViewById ( R.id.maraton_place_principal );

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

            registermaratonButton.setVisibility ( View.VISIBLE );
            cancelregistermaratonButton.setVisibility ( View.INVISIBLE );
            monitorearmaratonButton.setVisibility ( View.INVISIBLE );

            Common.carrera = null;

            CargarDatosCarrera ();
            VerificarInscripcion ();

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
                    SendUserToMaratonActivity ();
                }
            } );

        } catch (Exception e) {

        }
    }

    private void CargarDatosCarrera() {
        MaratonDatosRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    Common.carrera = dataSnapshot.getValue ( Maraton.class );
                    Picasso.with ( ClickMaratonActivity.this ).load ( Common.carrera.maratonimage ).into ( maratonImage );
                    maratondescription.setText ( Common.carrera.description );
                    maratoncontactname.setText ( "Contacto: " + Common.carrera.contactname );
                    maratoncontactnumber.setText ( "  Cel:  " + Common.carrera.contactnumber );
                    maratontime.setText ( Common.carrera.maratontime );
                    maratondate.setText ( "Fecha del Maraton:  " + Common.carrera.maratondate + " " );
                    maratonPlace.setText ( "Lugar del Maraton:  " + Common.carrera.place );
                    maratonName.setText ( Common.carrera.maratonname );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    private void VerificarInscripcion() {
        RegistrarCarrera.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    registermaratonButton.setVisibility ( View.INVISIBLE );
                    cancelregistermaratonButton.setVisibility ( View.VISIBLE );
                    monitorearmaratonButton.setVisibility ( View.VISIBLE );
                    mensaje.setVisibility ( View.VISIBLE );
                } else {
                    registermaratonButton.setVisibility ( View.VISIBLE );
                    cancelregistermaratonButton.setVisibility ( View.INVISIBLE );
                    monitorearmaratonButton.setVisibility ( View.INVISIBLE );
                    mensaje.setVisibility ( View.INVISIBLE );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
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

                            registermaratonButton.setVisibility ( View.VISIBLE );
                            cancelregistermaratonButton.setVisibility ( View.INVISIBLE );
                            monitorearmaratonButton.setVisibility ( View.INVISIBLE );
                            mensaje.setText ( "" );

                            Toast.makeText ( ClickMaratonActivity.this, "Ha cancelado su inscripcion correctamente.", Toast.LENGTH_SHORT ).show ();
                            loadingBar.dismiss ();
                        } else {
                            String message = task.getException ().getMessage ();
                            Toast.makeText ( ClickMaratonActivity.this, "A ocurrido un error: " + message, Toast.LENGTH_SHORT ).show ();
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
                crear.put ( "date", Common.carrera.maratondate + " : " + Common.carrera.maratontime );
                crear.put ( "maratonimagen", Common.carrera.maratonimage );
                crear.put ( "maratondescription", Common.carrera.description );
                RegistrarUsuario.updateChildren ( crear );
                HashMap usuario = new HashMap ();
                usuario.put ( "userName", Common.loggedUser.fullname );
                usuario.put ( "userGenero", Common.loggedUser.genero );
                usuario.put ( "userEdad", Common.loggedUser.edad );
                usuario.put ( "userImagen", Common.loggedUser.profileimage );
                RegistrarCarrera.updateChildren ( usuario ).addOnCompleteListener ( new OnCompleteListener () {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful ()) {
                            registermaratonButton.setVisibility ( View.INVISIBLE );
                            cancelregistermaratonButton.setVisibility ( View.VISIBLE );
                            monitorearmaratonButton.setVisibility ( View.VISIBLE );
                            mensaje.setText ( "USUARIO INSCRITO EXITOSAMENTE!" );
                            Toast.makeText ( ClickMaratonActivity.this, "Su inscripcion se realizo exitosamente", Toast.LENGTH_SHORT ).show ();
                            loadingBar.dismiss ();
                        } else {
                            String message = task.getException ().getMessage ();
                            Toast.makeText ( ClickMaratonActivity.this, "A ocurrido un error: " + message, Toast.LENGTH_SHORT ).show ();
                            loadingBar.dismiss ();
                        }
                    }
                } );
            }
        } catch (Exception e) {
        }
    }

    private void SendUserToMaratonActivity() {
        try {
            Intent ClickMaratonIntent = new Intent ( ClickMaratonActivity.this, MapsActivity.class );
            ClickMaratonIntent.putExtra ( "PostKey", PostKey );
            startActivity ( ClickMaratonIntent );
        } catch (Exception e) {
        }
    }
}
