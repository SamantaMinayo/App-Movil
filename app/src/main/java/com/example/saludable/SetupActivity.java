package com.example.saludable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    String currentUserID;
    private Button SaveInformation;
    private CircleImageView ProfileImage;
    private EditText UserName, FullName, Country, Altura, Peso, Edad, Genero;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_setup );


        mAuth = FirebaseAuth.getInstance ();
        currentUserID = mAuth.getCurrentUser ().getUid ();
        UserRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( currentUserID );
        loadingBar = new ProgressDialog ( this );

        UserName = findViewById ( R.id.setup_username );
        FullName = findViewById ( R.id.setup_fullname );
        Country = findViewById ( R.id.setup_country );
        Altura = findViewById ( R.id.setup_estatura );
        Peso = findViewById ( R.id.setup_peso );
        Edad = findViewById ( R.id.setup_edad );
        Genero = findViewById ( R.id.setup_genero );
        SaveInformation = findViewById ( R.id.setup_button );
        ProfileImage = findViewById ( R.id.setup_profile_image );


        SaveInformation.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                SaveAccountSetupInformation ();
            }
        } );


    }

    private void SaveAccountSetupInformation() {
        String username = UserName.getText ().toString ();
        String country = Country.getText ().toString ();
        String altura = Altura.getText ().toString ();
        String fullname = FullName.getText ().toString ();
        String peso = Peso.getText ().toString ();
        String edad = Edad.getText ().toString ();
        String genero = Genero.getText ().toString ();
        if (TextUtils.isEmpty ( username )) {
            Toast.makeText ( this, "Please write your username..", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( country )) {
            Toast.makeText ( this, "Please write your country..", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( altura )) {
            Toast.makeText ( this, "Please write your altura..", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( fullname )) {
            Toast.makeText ( this, "Please write your fullname..", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( peso )) {
            Toast.makeText ( this, "Please write your peso..", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( edad )) {
            Toast.makeText ( this, "Please write your age..", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( genero )) {
            Toast.makeText ( this, "Please write your genero..", Toast.LENGTH_SHORT ).show ();
        } else {

            loadingBar.setTitle ( "Saving Information" );
            loadingBar.setMessage ( "Please wait, while we are saving your information" );
            loadingBar.show ();
            loadingBar.setCanceledOnTouchOutside ( true );

            HashMap userMap = new HashMap ();
            userMap.put ( "username", username );
            userMap.put ( "fullname", fullname );
            userMap.put ( "country", country );
            userMap.put ( "altura", altura );
            userMap.put ( "peso", peso );
            userMap.put ( "edad", edad );
            userMap.put ( "genero", genero );
            userMap.put ( "status", "Here Saludable" );
            userMap.put ( "gender", "none" );
            userMap.put ( "dob", "none" );
            userMap.put ( "relationship", "none" );
            UserRef.updateChildren ( userMap ).addOnCompleteListener ( new OnCompleteListener () {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful ()) {
                        SendUserToMainActivity ();
                        Toast.makeText ( SetupActivity.this, "Your Account is create successfully..", Toast.LENGTH_SHORT ).show ();
                        loadingBar.dismiss ();
                    } else {
                        String message = task.getException ().getMessage ();
                        Toast.makeText ( SetupActivity.this, "Error Occured" + message, Toast.LENGTH_SHORT ).show ();
                        loadingBar.dismiss ();
                    }

                }
            } );
        }


    }

    private void SendUserToMainActivity() {
        Intent setupIntent = new Intent ( SetupActivity.this, MainActivity.class );
        setupIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( setupIntent );
        finish ();
    }
}
