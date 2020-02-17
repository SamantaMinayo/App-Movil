package com.example.saludable;

import android.os.Bundle;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private TextView userName, userStatus, userFullname, userCountry, userEdad, userPeso, userAltura, userGenero, velpromtot, velpromult, tiempromtot, tiempromult, disttot, distult, userimc;
    private CircleImageView userProfileImage;

    private DatabaseReference profileuserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_profile );

            mAuth = FirebaseAuth.getInstance ();
            currentUserId = mAuth.getCurrentUser ().getUid ();
            profileuserRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( currentUserId );


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


            userProfileImage = findViewById ( R.id.my_profile_pic );

            profileuserRef.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists ()) {
                        if (dataSnapshot.hasChild ( "profileimage" )) {
                            String myProfileImage = dataSnapshot.child ( "profileimage" ).getValue ().toString ();
                            Picasso.with ( ProfileActivity.this ).load ( myProfileImage ).placeholder ( R.drawable.profile ).into ( userProfileImage );
                        }
                        if (dataSnapshot.hasChild ( "username" )) {
                            String myUsername = dataSnapshot.child ( "username" ).getValue ().toString ();
                            userName.setText ( "@" + myUsername );
                        }
                        if (dataSnapshot.hasChild ( "fullname" )) {
                            String myProfileName = dataSnapshot.child ( "fullname" ).getValue ().toString ();
                            userFullname.setText ( myProfileName );
                        }
                        if (dataSnapshot.hasChild ( "status" )) {
                            String myProfileStatus = dataSnapshot.child ( "status" ).getValue ().toString ();
                            userStatus.setText ( myProfileStatus );
                        }
                        if (dataSnapshot.hasChild ( "country" )) {
                            String myCountry = dataSnapshot.child ( "country" ).getValue ().toString ();
                            userCountry.setText ( "Country: " + myCountry );
                        }
                        if (dataSnapshot.hasChild ( "genero" )) {
                            String myGenero = dataSnapshot.child ( "genero" ).getValue ().toString ();
                            userGenero.setText ( "Genero: " + myGenero );
                        }
                        if (dataSnapshot.hasChild ( "peso" )) {
                            String myPeso = dataSnapshot.child ( "peso" ).getValue ().toString ();
                            userPeso.setText ( "Peso: " + myPeso );
                        }
                        if (dataSnapshot.hasChild ( "altura" )) {
                            String myAltura = dataSnapshot.child ( "altura" ).getValue ().toString ();
                            userAltura.setText ( "Altura: " + myAltura );
                        }
                        if (dataSnapshot.hasChild ( "edad" )) {
                            String myEdad = dataSnapshot.child ( "edad" ).getValue ().toString ();
                            userEdad.setText ( "Edad: " + myEdad );
                        }
                        if (dataSnapshot.hasChild ( "velocidadpromtotal" )) {
                            String myveltot = dataSnapshot.child ( "velocidadpromtotal" ).getValue ().toString ();
                            velpromtot.setText ( myveltot );
                        }
                        if (dataSnapshot.hasChild ( "velocidadpromedioultima" )) {
                            String myvelfin = dataSnapshot.child ( "velocidadpromedioultima" ).getValue ().toString ();
                            velpromult.setText ( myvelfin );
                        }
                        if (dataSnapshot.hasChild ( "tiempopromtotal" )) {
                            String mytiemtot = dataSnapshot.child ( "tiempopromtotal" ).getValue ().toString ();
                            tiempromtot.setText ( mytiemtot );
                        }
                        if (dataSnapshot.hasChild ( "tiempopromultimo" )) {
                            String mytiemfin = dataSnapshot.child ( "tiempopromultimo" ).getValue ().toString ();
                            tiempromult.setText ( mytiemfin );
                        }
                        if (dataSnapshot.hasChild ( "distanciatotal" )) {
                            String mydistot = dataSnapshot.child ( "distanciatotal" ).getValue ().toString ();
                            disttot.setText ( mydistot );
                        }
                        if (dataSnapshot.hasChild ( "distanciaultima" )) {
                            String mydisfin = dataSnapshot.child ( "distanciaultima" ).getValue ().toString ();
                            distult.setText ( mydisfin );
                        }
                        if (dataSnapshot.hasChild ( "imc" )) {
                            String myimc = dataSnapshot.child ( "imc" ).getValue ().toString ();
                            userimc.setText ( "IMC: " + myimc );
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        } catch (Exception e) {
        }
    }
}
