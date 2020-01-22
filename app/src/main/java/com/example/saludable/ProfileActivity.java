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
                    String myProfileImage = dataSnapshot.child ( "profileimage" ).getValue ().toString ();
                    String myUsername = dataSnapshot.child ( "username" ).getValue ().toString ();
                    String myProfileName = dataSnapshot.child ( "fullname" ).getValue ().toString ();
                    String myProfileStatus = dataSnapshot.child ( "status" ).getValue ().toString ();
                    String myCountry = dataSnapshot.child ( "country" ).getValue ().toString ();
                    String myGenero = dataSnapshot.child ( "genero" ).getValue ().toString ();
                    String myPeso = dataSnapshot.child ( "peso" ).getValue ().toString ();
                    String myAltura = dataSnapshot.child ( "altura" ).getValue ().toString ();
                    String myEdad = dataSnapshot.child ( "edad" ).getValue ().toString ();
                    String myveltot = dataSnapshot.child ( "velocidadpromtotal" ).getValue ().toString ();
                    String myvelfin = dataSnapshot.child ( "velocidadpromedioultima" ).getValue ().toString ();
                    String mytiemtot = dataSnapshot.child ( "tiempopromtotal" ).getValue ().toString ();
                    String mytiemfin = dataSnapshot.child ( "tiempopromultimo" ).getValue ().toString ();
                    String mydistot = dataSnapshot.child ( "distanciatotal" ).getValue ().toString ();
                    String mydisfin = dataSnapshot.child ( "distanciaultima" ).getValue ().toString ();
                    String myimc = dataSnapshot.child ( "imc" ).getValue ().toString ();

                    Picasso.with ( ProfileActivity.this ).load ( myProfileImage ).placeholder ( R.drawable.profile ).into ( userProfileImage );
                    userName.setText ( "@" + myUsername );
                    userFullname.setText ( myProfileName );
                    userStatus.setText ( myProfileStatus );
                    userCountry.setText ( "Country: " + myCountry );
                    userGenero.setText ( "Gener: " + myGenero );
                    userPeso.setText ( "Peso: " + myPeso );
                    userAltura.setText ( "Altura: " + myAltura );
                    userEdad.setText ( "Edad: " + myEdad );
                    velpromult.setText ( myvelfin );
                    velpromtot.setText ( myveltot );
                    tiempromtot.setText ( mytiemtot );
                    tiempromult.setText ( mytiemfin );
                    disttot.setText ( mydistot );
                    distult.setText ( mydisfin );
                    userimc.setText ( "IMC: " + myimc );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
}
