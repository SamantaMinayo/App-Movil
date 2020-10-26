package com.example.saludable;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private ImageView GoogleSingButton;
    private GoogleApiClient mGoogleSignInClient;
    private static final int CODIGO_PERMISO = 22;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_login );

            if (!checkPermissions ()) {
                requestPermissions ();
            }

            FirebaseMessaging.getInstance ().subscribeToTopic ( "NewMaraton" );

            mAuth = FirebaseAuth.getInstance ();
            NeedNewAccountLink = findViewById ( R.id.register_account_link );
            UserEmail = findViewById ( R.id.login_email );
            UserPassword = findViewById ( R.id.login_password );
            LoginButton = findViewById ( R.id.login_button );
            GoogleSingButton = findViewById ( R.id.google_signin_button );

            loadingBar = new ProgressDialog ( this );

            NeedNewAccountLink.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    SendUserToRegisterActivity ();
                }

            } );

            LoginButton.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View view) {

                    AllowingUserToLogin ();
                }
            } );

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder ( GoogleSignInOptions.DEFAULT_SIGN_IN )
                    .requestIdToken ( getString ( R.string.default_web_client_id ) )
                    .requestEmail ()
                    .build ();

            mGoogleSignInClient = new GoogleApiClient.Builder ( this ).
                    enableAutoManage ( this, new GoogleApiClient.OnConnectionFailedListener () {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Toast.makeText ( LoginActivity.this, "Conexión con Google fallida", Toast.LENGTH_SHORT ).show ();

                        }
                    } ).addApi ( Auth.GOOGLE_SIGN_IN_API, gso ).build ();

            GoogleSingButton.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    signIn ();
                }

            } );

            // Check that the user hasn't revoked permissions by going to Settings.
        } catch (Exception e) {
        }
    }

    private void signIn() {
        try {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent ( mGoogleSignInClient );
            startActivityForResult ( signInIntent, RC_SIGN_IN );
        } catch (Exception e) {
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult ( requestCode, resultCode, data );
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {

                loadingBar.setTitle ( "Inicio de sesion con Google" );
                loadingBar.setMessage ( "Espere mientras lo autenticamos con la cuenta de Google" );
                loadingBar.setCanceledOnTouchOutside ( true );
                loadingBar.show ();

                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent ( data );

                if (result.isSuccess ()) {
                    GoogleSignInAccount account = result.getSignInAccount ();
                    firebaseAuthWithGoogle ( account );
                    Toast.makeText ( this, "Usuario autenticado correctamente con la cuenta de Google", Toast.LENGTH_SHORT ).show ();

                } else {
                    Toast.makeText ( this, "No se ha podido autenticar el usuario con la cuenta de Google.", Toast.LENGTH_SHORT ).show ();
                    loadingBar.dismiss ();
                }
            }
        } catch (Exception e) {
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        try {

            Log.d ( TAG, "firebaseAuthWithGoogle:" + acct.getId () );

            AuthCredential credential = GoogleAuthProvider.getCredential ( acct.getIdToken (), null );
            mAuth.signInWithCredential ( credential )
                    .addOnCompleteListener ( this, new OnCompleteListener<AuthResult> () {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful ()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d ( TAG, "signInWithCredential:success" );
                                SendUserToMainActivity ();
                                loadingBar.dismiss ();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w ( TAG, "signInWithCredential:failure", task.getException () );
                                String message = task.getException ().toString ();
                                SendUserToLoginActivity ();
                                Toast.makeText ( LoginActivity.this, "Not Authenticated: " + message, Toast.LENGTH_SHORT ).show ();
                                loadingBar.dismiss ();
                            }
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

    private void AllowingUserToLogin() {
        try {
            String email = UserEmail.getText ().toString ();
            String password = UserPassword.getText ().toString ();
            if (TextUtils.isEmpty ( email )) {
                Toast.makeText ( this, "Porfavor ingrese su correo", Toast.LENGTH_SHORT ).show ();
            } else if (TextUtils.isEmpty ( password )) {
                Toast.makeText ( this, "Porfavor ingrese su contrasena", Toast.LENGTH_SHORT ).show ();
            } else {
                loadingBar.setTitle ( "Login" );
                loadingBar.setMessage ( "Espero mientras autenticamos el usuario ingresado." );
                loadingBar.setCanceledOnTouchOutside ( true );
                loadingBar.show ();

                mAuth.signInWithEmailAndPassword ( email, password )
                        .addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful ()) {
                                    SendUserToMainActivity ();
                                    Toast.makeText ( LoginActivity.this, "Usted se autentico correctamente", Toast.LENGTH_SHORT ).show ();
                                    loadingBar.dismiss ();
                                } else {
                                    String message = task.getException ().getMessage ();
                                    Toast.makeText ( LoginActivity.this, "A ocurrido un error: " + message, Toast.LENGTH_SHORT ).show ();
                                    loadingBar.dismiss ();
                                }
                            }
                        } );
            }
        } catch (Exception e) {
        }

    }

    private boolean checkPermissions() {
        try {
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission ( this,
                    Manifest.permission.ACCESS_FINE_LOCATION ) && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission ( this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE );
        } catch (Exception e) {
            return false;
        }
    }

    private void requestPermissions() {
        try {
            ActivityCompat.requestPermissions ( LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODIGO_PERMISO );
        } catch (Exception e) {

        }
    }

    private void SendUserToMainActivity() {
        try {
            Intent mainIntent = new Intent ( LoginActivity.this, MainActivity.class );
            mainIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( mainIntent );
            finish ();
        } catch (Exception e) {
        }
    }

    private void SendUserToLoginActivity() {
        try {
            Intent mainIntent = new Intent ( LoginActivity.this, LoginActivity.class );
            mainIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( mainIntent );
            finish ();
        } catch (Exception e) {
        }

    }

    private void SendUserToRegisterActivity() {
        try {
            Intent registerIntent = new Intent ( LoginActivity.this, RegisterActivity.class );
            startActivity ( registerIntent );
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
