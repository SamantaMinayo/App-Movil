package com.example.saludable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {


    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreateAccountButton;
    private ProgressDialog loadingBar;
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_register );

            mAuth = FirebaseAuth.getInstance ();

            UserEmail = findViewById ( R.id.register_email );
            UserPassword = findViewById ( R.id.register_password );
            UserConfirmPassword = findViewById ( R.id.register_confirm_password );
            CreateAccountButton = findViewById ( R.id.register_create_account );
            loadingBar = new ProgressDialog ( this );

            CreateAccountButton.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    CreateNewAccount ();
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

            if (currentUser != null) {
                SendUserToMainActivity ();
            }
        } catch (Exception e) {
        }
    }

    private void SendUserToMainActivity() {
        try {

            Intent mainIntent = new Intent ( RegisterActivity.this, MainActivity.class );
            mainIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( mainIntent );
            finish ();
        } catch (Exception e) {
        }
    }


    private void CreateNewAccount() {
        try {
            String email = UserEmail.getText ().toString ();
            String password = UserPassword.getText ().toString ();
            String confirmpassword = UserConfirmPassword.getText ().toString ();
            if (TextUtils.isEmpty ( email )) {
                Toast.makeText ( this, "Porfavor ingrese su correo", Toast.LENGTH_SHORT ).show ();
            } else if (TextUtils.isEmpty ( password )) {
                Toast.makeText ( this, "Porfafor ingrese password.", Toast.LENGTH_SHORT ).show ();
            } else if (TextUtils.isEmpty ( email )) {
                Toast.makeText ( this, "Porfavor confirme password...", Toast.LENGTH_SHORT ).show ();
            } else if (!password.equals ( confirmpassword )) {
                Toast.makeText ( this, "No coinciden los password.", Toast.LENGTH_SHORT ).show ();
            } else {
                loadingBar.setTitle ( "Creando una nueva cuenta" );
                loadingBar.setMessage ( "Espere mientras creamos su cuenta." );
                loadingBar.show ();
                loadingBar.setCanceledOnTouchOutside ( true );
                mAuth.createUserWithEmailAndPassword ( email, password )
                        .addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful ()) {
                                    SendUserToSetupActivity ();
                                    Toast.makeText ( RegisterActivity.this,
                                            "Usted ha sido autenticado correctamente",
                                            Toast.LENGTH_SHORT ).show ();
                                    loadingBar.dismiss ();
                                } else {
                                    String message = task.getException ().getMessage ();
                                    Toast.makeText ( RegisterActivity.this,
                                            "Error Occured: " +
                                                    message, Toast.LENGTH_SHORT ).show ();
                                    loadingBar.dismiss ();
                                }
                            }
                        } );
            }
        } catch (Exception e) {
            Log.d ( TAG, "CreateNewAccount", e );
        }
    }


    private void SendUserToSetupActivity() {
        try {

            Intent setupIntent = new Intent ( RegisterActivity.this, SetupActivity.class );
            setupIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( setupIntent );
            finish ();
        } catch (Exception e) {
        }
    }
}
