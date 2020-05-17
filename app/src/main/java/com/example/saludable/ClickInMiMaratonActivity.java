package com.example.saludable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickInMiMaratonActivity extends AppCompatActivity {

    private String PostKey, cod;
    private DatabaseReference ClickMaratonRef, CarreraInf;
    private Button monitorear;
    private EditText codigo;
    private String smaratonName, smaratondescription, smaratondate, smaratontime, smaratonImage, smaratonPlace;
    private TextView maratonName, maratondescription, maratondate, maratontime, maratonPlace, mensaje;
    private ImageView maratonImage;
    private ProgressDialog loadingBar;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_click_in_mi_maraton );
        PostKey = getIntent ().getExtras ().get ( "PostKey" ).toString ();
        monitorear = findViewById ( R.id.in_monitorear_maraton_button );
        codigo = findViewById ( R.id.in_codigo_carrera );
        maratonImage = findViewById ( R.id.in_maraton_image_principal );
        maratonName = findViewById ( R.id.in_maraton_name_principal );
        maratondescription = findViewById ( R.id.in_maraton_description_principal );
        maratonPlace = findViewById ( R.id.in_maraton_place_principal );
        maratondate = findViewById ( R.id.in_maraton_date );
        maratontime = findViewById ( R.id.in_maraton_time );
        mensaje = findViewById ( R.id.in_Mensaje );
        mToolbar = findViewById ( R.id.in_carrera_toolbar );
        setSupportActionBar ( mToolbar );
        getSupportActionBar ().setTitle ( "Carrera" );
        getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );
        loadingBar = new ProgressDialog ( this );


        ClickMaratonRef = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" ).child ( PostKey );

        ClickMaratonRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {

                    if (dataSnapshot.hasChild ( "maratonimage" )) {
                        smaratonImage = dataSnapshot.child ( "maratonimage" ).getValue ().toString ();
                        Picasso.with ( ClickInMiMaratonActivity.this ).load ( smaratonImage ).into ( maratonImage );
                    }
                    if (dataSnapshot.hasChild ( "description" )) {
                        smaratondescription = dataSnapshot.child ( "description" ).getValue ().toString ();
                        maratondescription.setText ( smaratondescription );
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
                    if (dataSnapshot.hasChild ( "codigo" )) {
                        cod = dataSnapshot.child ( "codigo" ).getValue ().toString ();
                    } else {
                        cod = "";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );

        monitorear.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (cod.isEmpty ()) {
                    Toast.makeText ( ClickInMiMaratonActivity.this, "El administrador no ha proporcionado un codigo de carrera.", Toast.LENGTH_SHORT ).show ();
                    loadingBar.dismiss ();
                } else {
                    if (!codigo.getText ().toString ().isEmpty ()) {
                        if (codigo.getText ().toString ().equals ( cod )) {
                            SendUserToMonitorActivity ();
                        } else {
                            Toast.makeText ( ClickInMiMaratonActivity.this, "Codigo de carrera erroneo.", Toast.LENGTH_SHORT ).show ();
                            loadingBar.dismiss ();
                        }
                    } else {
                        Toast.makeText ( ClickInMiMaratonActivity.this, "Ingrese Codigo de carrera", Toast.LENGTH_SHORT ).show ();
                        loadingBar.dismiss ();
                    }
                }
            }
        } );
    }

    private void SendUserToMonitorActivity() {
        try {
            Intent loginIntent = new Intent ( ClickInMiMaratonActivity.this, MapsActivity.class );
            loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            loginIntent.putExtra ( "PostKey", PostKey );
            startActivity ( loginIntent );
            finish ();
        } catch (Exception e) {
        }
    }
}
