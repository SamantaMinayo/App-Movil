package com.example.saludable;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickMaratonActivity extends AppCompatActivity {


    private ImageView maratonImage;
    private TextView maratonName, maratondescription, maratondate, maratontime, maratoncontactname, maratoncontactnumber, maratonPlace, mensaje;
    private Button registermaratonButton, cancelregistermaratonButton;

    private DatabaseReference ClickMaratonRef, UsuarioInscrito;
    private FirebaseAuth mAuth;

    private String smaratonName, smaratondescription, smaratondate, smaratontime, smaratoncontactname, smaratoncontactnumber, smaratonImage, smaratonPlace, smaratonUid;
    private String PostKey, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_click_maraton );

        mAuth = FirebaseAuth.getInstance ();
        current_user_id = mAuth.getCurrentUser ().getUid ();

        PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
        ClickMaratonRef = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( PostKey );
        UsuarioInscrito = FirebaseDatabase.getInstance ().getReference ().child ( "UsuariosCarreras" );

        maratonImage = findViewById ( R.id.maraton_image_principal );
        maratonName = findViewById ( R.id.maraton_name_principal );
        maratondescription = findViewById ( R.id.maraton_description_principal );
        maratonPlace = findViewById ( R.id.maraton_place_principal );
        maratondate = findViewById ( R.id.maraton_date );
        maratontime = findViewById ( R.id.maraton_time );
        maratoncontactname = findViewById ( R.id.maraton_contact_name_principal );
        maratoncontactnumber = findViewById ( R.id.maraton_contact_number_principal );
        mensaje = findViewById ( R.id.Mensaje );

        registermaratonButton = findViewById ( R.id.register_maraton_button );
        cancelregistermaratonButton = findViewById ( R.id.cancel_register_maraton_button );

        registermaratonButton.setVisibility ( View.INVISIBLE );
        cancelregistermaratonButton.setVisibility ( View.INVISIBLE );

        ClickMaratonRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {

                    smaratonImage = dataSnapshot.child ( "maratonimage" ).getValue ().toString ();
                    smaratondescription = dataSnapshot.child ( "description" ).getValue ().toString ();
                    smaratoncontactname = dataSnapshot.child ( "contactname" ).getValue ().toString ();
                    smaratoncontactnumber = dataSnapshot.child ( "contactnumber" ).getValue ().toString ();
                    smaratontime = dataSnapshot.child ( "time_maraton" ).getValue ().toString ();
                    smaratondate = dataSnapshot.child ( "date_maraton" ).getValue ().toString ();
                    smaratonPlace = dataSnapshot.child ( "lugar" ).getValue ().toString ();
                    smaratonName = dataSnapshot.child ( "namecarrera" ).getValue ().toString ();
                    smaratonUid = dataSnapshot.child ( "uid" ).getValue ().toString ();


                    maratonName.setText ( smaratonName );
                    maratondescription.setText ( smaratondescription );
                    maratonPlace.setText ( "Lugar del Maraton:  " + smaratonPlace );
                    maratondate.setText ( "Fecha del Maraton:  " + smaratondate + " " );
                    maratontime.setText ( smaratontime );
                    maratoncontactname.setText ( "Contacto: " + smaratoncontactname );
                    maratoncontactnumber.setText ( "  Cel:  " + smaratoncontactnumber );

                    Picasso.with ( ClickMaratonActivity.this ).load ( smaratonImage ).into ( maratonImage );


                    UsuarioInscrito.addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String id = smaratonUid + current_user_id;
                            if (dataSnapshot.hasChild ( id )) {
                                registermaratonButton.setVisibility ( View.INVISIBLE );
                                cancelregistermaratonButton.setVisibility ( View.VISIBLE );
                                mensaje.setText ( "USUARIO INSCRITO EXITOSAMENTE!" );
                            } else {
                                registermaratonButton.setVisibility ( View.VISIBLE );
                                cancelregistermaratonButton.setVisibility ( View.INVISIBLE );
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
