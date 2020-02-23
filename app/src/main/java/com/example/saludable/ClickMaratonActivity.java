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

    private DatabaseReference ClickMaratonRef, UsuarioInscrito, RegistrarUsuario, CarreraUserInf;
    private FirebaseAuth mAuth;

    private String smaratonName, smaratondescription, smaratondate, smaratontime, smaratoncontactname, smaratoncontactnumber, smaratonImage, smaratonPlace;
    private String PostKey, current_user_id;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_click_maraton );

            mAuth = FirebaseAuth.getInstance ();
            current_user_id = mAuth.getCurrentUser ().getUid ();

            PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
            ClickMaratonRef = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( PostKey );
            CarreraUserInf = FirebaseDatabase.getInstance ().getReference ().child ( "CarrerasRealizadas" ).child ( "Usuarios" ).child ( current_user_id ).child ( PostKey );
            UsuarioInscrito = FirebaseDatabase.getInstance ().getReference ().child ( "UsuariosCarreras" ).child ( current_user_id );
            RegistrarUsuario = FirebaseDatabase.getInstance ().getReference ().child ( "UsuariosCarreras" ).child ( current_user_id ).child ( PostKey );
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


            loadingBar = new ProgressDialog ( this );

            registermaratonButton = findViewById ( R.id.register_maraton_button );
            cancelregistermaratonButton = findViewById ( R.id.cancel_register_maraton_button );

            registermaratonButton.setVisibility ( View.INVISIBLE );
            cancelregistermaratonButton.setVisibility ( View.INVISIBLE );
            monitorearmaratonButton.setVisibility ( View.INVISIBLE );

            ClickMaratonRef.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists ()) {

                        if (dataSnapshot.hasChild ( "maratonimage" )) {
                            smaratonImage = dataSnapshot.child ( "maratonimage" ).getValue ().toString ();
                            Picasso.with ( ClickMaratonActivity.this ).load ( smaratonImage ).into ( maratonImage );
                        }
                        if (dataSnapshot.hasChild ( "description" )) {
                            smaratondescription = dataSnapshot.child ( "description" ).getValue ().toString ();
                            maratondescription.setText ( smaratondescription );
                        }
                        if (dataSnapshot.hasChild ( "contactname" )) {
                            smaratoncontactname = dataSnapshot.child ( "contactname" ).getValue ().toString ();
                            maratoncontactname.setText ( "Contacto: " + smaratoncontactname );
                        }
                        if (dataSnapshot.hasChild ( "contactnumber" )) {
                            smaratoncontactnumber = dataSnapshot.child ( "contactnumber" ).getValue ().toString ();
                            maratoncontactnumber.setText ( "  Cel:  " + smaratoncontactnumber );
                        }
                        if (dataSnapshot.hasChild ( "maratontime" )) {
                            smaratontime = dataSnapshot.child ( "maratontime" ).getValue ().toString ();
                            maratontime.setText ( smaratontime );
                        }
                        if (dataSnapshot.hasChild ( "maratondate" )) {
                            smaratondate = dataSnapshot.child ( "maratondate" ).getValue ().toString ();
                            maratondate.setText ( "Fecha del Maraton:  " + smaratondate + " " );
                        }
                        if (dataSnapshot.hasChild ( "place" )) {
                            smaratonPlace = dataSnapshot.child ( "place" ).getValue ().toString ();
                            maratonPlace.setText ( "Lugar del Maraton:  " + smaratonPlace );
                        }
                        if (dataSnapshot.hasChild ( "maratonname" )) {
                            smaratonName = dataSnapshot.child ( "maratonname" ).getValue ().toString ();
                            maratonName.setText ( smaratonName );

                        }

                        if (dataSnapshot.hasChild ( "estado" )) {
                            if (dataSnapshot.child ( "estado" ).getValue ().toString ().equals ( "true" )) {
                                registermaratonButton.setVisibility ( View.INVISIBLE );
                                cancelregistermaratonButton.setVisibility ( View.INVISIBLE );
                                monitorearmaratonButton.setVisibility ( View.INVISIBLE );
                                mensaje.setText ( "Carrera ya Realizada" );
                            } else {
                                UsuarioInscrito.addValueEventListener ( new ValueEventListener () {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists ()) {
                                            if (dataSnapshot.hasChild ( PostKey )) {
                                                String prueba = (String) dataSnapshot.child ( PostKey ).child ( "inscrito" ).getValue ();
                                                if (prueba.equals ( "true" )) {
                                                    registermaratonButton.setVisibility ( View.INVISIBLE );
                                                    cancelregistermaratonButton.setVisibility ( View.VISIBLE );
                                                    monitorearmaratonButton.setVisibility ( View.VISIBLE );
                                                    mensaje.setText ( "USUARIO INSCRITO EXITOSAMENTE!" );
                                                } else {

                                                    CarreraUserInf.addValueEventListener ( new ValueEventListener () {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists ()) {
                                                                registermaratonButton.setVisibility ( View.INVISIBLE );
                                                                cancelregistermaratonButton.setVisibility ( View.INVISIBLE );
                                                                monitorearmaratonButton.setVisibility ( View.INVISIBLE );
                                                                mensaje.setText ( "USTED YA REGISTRO DATOS EN ESTA CARRERA." );
                                                            } else {
                                                                registermaratonButton.setVisibility ( View.VISIBLE );
                                                                cancelregistermaratonButton.setVisibility ( View.INVISIBLE );
                                                                monitorearmaratonButton.setVisibility ( View.INVISIBLE );
                                                                mensaje.setText ( "" );
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    } );
                                                }

                                            } else {
                                                registermaratonButton.setVisibility ( View.VISIBLE );
                                                cancelregistermaratonButton.setVisibility ( View.INVISIBLE );
                                                monitorearmaratonButton.setVisibility ( View.INVISIBLE );
                                                mensaje.setText ( "" );
                                            }
                                        } else {
                                            CarreraUserInf.addValueEventListener ( new ValueEventListener () {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists ()) {
                                                        registermaratonButton.setVisibility ( View.INVISIBLE );
                                                        cancelregistermaratonButton.setVisibility ( View.INVISIBLE );
                                                        monitorearmaratonButton.setVisibility ( View.INVISIBLE );
                                                        mensaje.setText ( "USTED YA REGISTRO DATOS EN ESTA CARRERA." );
                                                    } else {
                                                        registermaratonButton.setVisibility ( View.VISIBLE );
                                                        cancelregistermaratonButton.setVisibility ( View.INVISIBLE );
                                                        monitorearmaratonButton.setVisibility ( View.INVISIBLE );
                                                        mensaje.setText ( "" );
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );


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
                    SendUserToMonitorActivity ();
                }
            } );

        } catch (Exception e) {

        }
    }

    private void Inscripcion(boolean inscribir) {
        try {

            if (inscribir == false) {
                loadingBar.setTitle ( "Cancelar Inscripcion" );
                loadingBar.setMessage ( "Espere mientras cancelamos su inscripcion en la carrera." );
                loadingBar.setCanceledOnTouchOutside ( true );
                loadingBar.show ();
                RegistrarUsuario.removeValue ().addOnCompleteListener ( new OnCompleteListener () {
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
                crear.put ( "inscrito", "true" );
                RegistrarUsuario.updateChildren ( crear ).addOnCompleteListener ( new OnCompleteListener () {
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

    private void SendUserToMonitorActivity() {
        try {
            Intent ClickMaratonIntent = new Intent ( ClickMaratonActivity.this, MapsActivity.class );
            ClickMaratonIntent.putExtra ( "PostKey", PostKey );
            startActivity ( ClickMaratonIntent );
        } catch (Exception e) {
        }
    }
}
