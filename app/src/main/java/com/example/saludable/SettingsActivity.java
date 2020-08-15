package com.example.saludable;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saludable.Utils.Common;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsActivity extends AppCompatActivity {


    final static int Gallery_pick = 1;
    private Toolbar mToolbar;
    private EditText userName, userProfName, userStatus, userCountry, userAltura, userPeso, userEdad;
    private TextView userGenero;
    private Button UpdateAccountSettingsButton;
    private CircleImageView userProfileImage;
    private ProgressDialog loadingBar;
    private RecyclerView postList;
    private DatabaseReference SettingsUserRef, PostRef;
    private FirebaseAuth mAuth;
    private StorageReference UserProfileImageRef;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private String currentUserId, updateprofilename, updateprofileimage;
    private RadioButton radioButton, fem, mas;
    private RadioGroup radioGroup;
    private final int REQUEST_STORAGE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_settings );


            if (ActivityCompat.checkSelfPermission ( this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions ( this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE );

            mAuth = FirebaseAuth.getInstance ();
            currentUserId = mAuth.getCurrentUser ().getUid ();
            SettingsUserRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( currentUserId ).child ( "Informacion" );
            UserProfileImageRef = FirebaseStorage.getInstance ().getReference ().child ( "ProfileImages" );
            PostRef = FirebaseDatabase.getInstance ().getReference ().child ( "Posts" );

            mToolbar = findViewById ( R.id.settings_toolbar );
            setSupportActionBar ( mToolbar );
            getSupportActionBar ().setTitle ( "Account Settings" );
            getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

            fem = findViewById ( R.id.setradFeMale );
            mas = findViewById ( R.id.setradMale );
            userName = findViewById ( R.id.settings_username );
            userProfName = findViewById ( R.id.settings_profile_full_name );
            userStatus = findViewById ( R.id.settings_status );
            userCountry = findViewById ( R.id.settings_country );
            userEdad = findViewById ( R.id.settings_edad );
            userAltura = findViewById ( R.id.settings_estatura );
            userPeso = findViewById ( R.id.settings_peso );
            userGenero = findViewById ( R.id.setting_genero );
            radioGroup = findViewById ( R.id.setradGroup );
            userProfileImage = findViewById ( R.id.settings_profile_image );
            UpdateAccountSettingsButton = findViewById ( R.id.update_account_settings_button );
            loadingBar = new ProgressDialog ( this );

            if (Common.loggedUser != null) {
                Picasso.with ( SettingsActivity.this ).load ( Common.loggedUser.getProfileimage () ).placeholder ( R.drawable.profile ).into ( userProfileImage );
                userName.setText ( Common.loggedUser.getUsername () );
                userProfName.setText ( Common.loggedUser.getFullname () );
                userStatus.setText ( Common.loggedUser.getStatus () );
                userCountry.setText ( Common.loggedUser.getCountry () );
                userGenero.setText ( Common.loggedUser.getGenero () );
                String gen = Common.loggedUser.getGenero ();
                if (gen.equals ( "Femenino" )) {
                    fem.setChecked ( true );
                } else {
                    mas.setChecked ( true );
                }

                userPeso.setText ( Common.loggedUser.getPeso () );
                userAltura.setText ( Common.loggedUser.getAltura () );
                userEdad.setText ( Common.loggedUser.getEdad () );

            } else {
                SettingsUserRef.addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists ()) {

                            if (dataSnapshot.hasChild ( "profileimage" )) {
                                String myProfileImage = dataSnapshot.child ( "profileimage" ).getValue ().toString ();
                                updateprofileimage = myProfileImage;
                                Picasso.with ( SettingsActivity.this ).load ( myProfileImage ).placeholder ( R.drawable.profile ).into ( userProfileImage );
                            }
                            if (dataSnapshot.hasChild ( "username" )) {
                                String myUsername = dataSnapshot.child ( "username" ).getValue ().toString ();
                                userName.setText ( myUsername );
                            }

                            if (dataSnapshot.hasChild ( "fullname" )) {
                                String myProfileName = dataSnapshot.child ( "fullname" ).getValue ().toString ();
                                updateprofilename = myProfileName;
                                userProfName.setText ( myProfileName );
                            }
                            if (dataSnapshot.hasChild ( "status" )) {
                                String myProfileStatus = dataSnapshot.child ( "status" ).getValue ().toString ();
                                userStatus.setText ( myProfileStatus );
                            }
                            if (dataSnapshot.hasChild ( "country" )) {
                                String myCountry = dataSnapshot.child ( "country" ).getValue ().toString ();
                                userCountry.setText ( myCountry );
                            }
                            if (dataSnapshot.hasChild ( "genero" )) {
                                String myGenero = dataSnapshot.child ( "genero" ).getValue ().toString ();
                                userGenero.setText ( myGenero );
                                if (myGenero.equals ( "Femenino" )) {
                                    fem.setChecked ( true );
                                } else {
                                    mas.setChecked ( true );
                                }
                            }
                            if (dataSnapshot.hasChild ( "peso" )) {
                                String myPeso = dataSnapshot.child ( "peso" ).getValue ().toString ();
                                userPeso.setText ( myPeso );
                            }
                            if (dataSnapshot.hasChild ( "altura" )) {
                                String myAltura = dataSnapshot.child ( "altura" ).getValue ().toString ();
                                userAltura.setText ( myAltura );
                            }
                            if (dataSnapshot.hasChild ( "edad" )) {
                                String myEdad = dataSnapshot.child ( "edad" ).getValue ().toString ();
                                userEdad.setText ( myEdad );
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );

            }

            UpdateAccountSettingsButton.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    ValidateAccountInfo ();
                }
            } );

            userProfileImage.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {

                    openGallery ();
                }
            } );
        } catch (Exception e) {
        }
    }

    private void openGallery() {

        CropImage.activity ()
                .setGuidelines ( CropImageView.Guidelines.ON )
                .setAspectRatio ( 1, 1 )
                .start ( this );
    }
    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId ();

        radioButton = findViewById ( radioId );
        userGenero.setText ( radioButton.getText ().toString () );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {

            super.onActivityResult ( requestCode, resultCode, data );

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult ( data );

                if (resultCode == RESULT_OK) {

                    loadingBar.setTitle ( "Imagen de Perfil" );
                    loadingBar.setMessage ( "Espere mientras actualizamos su imagen de perfil" );
                    loadingBar.setCanceledOnTouchOutside ( true );
                    loadingBar.show ();


                    Uri resultUri = result.getUri ();

                    final StorageReference filePath = UserProfileImageRef.child ( currentUserId + ".jpg" );

                    filePath.putFile ( resultUri ).addOnCompleteListener ( new OnCompleteListener<UploadTask.TaskSnapshot> () {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful ()) {
                                Toast.makeText ( SettingsActivity.this, "Imagen almacenada correctamente", Toast.LENGTH_SHORT ).show ();

                                filePath.getDownloadUrl ().addOnCompleteListener ( new OnCompleteListener<Uri> () {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        final String downloadUri = task.getResult ().toString ();
                                        SettingsUserRef.child ( "profileimage" ).setValue ( downloadUri )
                                                .addOnCompleteListener ( new OnCompleteListener<Void> () {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful ()) {


                                                            Picasso.with ( SettingsActivity.this ).load ( downloadUri ).placeholder ( R.drawable.profile ).into ( userProfileImage );
                                                            Common.loggedUser.setProfileimage ( downloadUri );
                                                            Toast.makeText ( SettingsActivity.this, "Imagen almacenada correctamente", Toast.LENGTH_SHORT ).show ();
                                                            loadingBar.dismiss ();
                                                        } else {
                                                            String message = task.getException ().getMessage ();
                                                            Toast.makeText ( SettingsActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT ).show ();
                                                            loadingBar.dismiss ();
                                                        }
                                                    }
                                                } );
                                    }
                                } );

                            }
                        }
                    } );
                } else {

                    Toast.makeText ( this, "A ocurrido un error", Toast.LENGTH_SHORT ).show ();
                    loadingBar.dismiss ();
                }
            }

        } catch (Exception e) {

        }
    }

    private void ValidateAccountInfo() {
        String username = userName.getText ().toString ();
        String profilename = userProfName.getText ().toString ();
        String status = userStatus.getText ().toString ();
        String country = userCountry.getText ().toString ();
        String genero = userGenero.getText ().toString ();
        String edad = userEdad.getText ().toString ();
        String altura = userAltura.getText ().toString ();
        String peso = userPeso.getText ().toString ();

        if (TextUtils.isEmpty ( username ) || TextUtils.isEmpty ( profilename ) || TextUtils.isEmpty ( status ) || TextUtils.isEmpty ( country ) || TextUtils.isEmpty ( genero ) || TextUtils.isEmpty ( edad )
                || TextUtils.isEmpty ( altura ) || TextUtils.isEmpty ( peso )) {
            Toast.makeText ( this, "Verifique que todos los campos esten completos", Toast.LENGTH_SHORT ).show ();

        } else {

            loadingBar.setTitle ( "Imagen de Perfil" );
            loadingBar.setMessage ( "Espere mientras actualizamos su imagen de perfil" );
            loadingBar.setCanceledOnTouchOutside ( true );
            loadingBar.show ();

            UpdateAccountInfo ( username, profilename, status, country, genero, edad, altura, peso );
        }
    }

    private void UpdateAccountInfo(String username, final String profilename, String status, String country, String genero, String edad, String altura, String peso) {
        try {
            HashMap userMap = new HashMap ();
            double h = Integer.parseInt ( altura );
            double p = Integer.parseInt ( peso );
            double cal = (p / (h * h)) * 10000;
            MathContext m = new MathContext ( 4 );
            BigDecimal imcnum = new BigDecimal ( cal );
            String imc = String.valueOf ( imcnum.round ( m ) );

            double rango = Double.valueOf ( edad ) / 5;
            double decimal = rango % 1;
            double entero = rango - decimal;
            String rangos = String.valueOf ( (int) entero );
            double paso = Double.valueOf ( altura ) * 0.41;
            String factorpaso = String.valueOf ( paso );

            userMap.put ( "username", username );
            userMap.put ( "fullname", profilename );
            userMap.put ( "status", status );
            userMap.put ( "country", country );
            userMap.put ( "genero", genero );
            userMap.put ( "edad", edad );
            userMap.put ( "altura", altura );
            userMap.put ( "peso", peso );
            userMap.put ( "imc", imc );
            userMap.put ( "rango", rangos );
            userMap.put ( "paso", factorpaso );

            SettingsUserRef.updateChildren ( userMap ).addOnCompleteListener ( new OnCompleteListener () {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful ()) {
                        SendUserToMainActivity ();
                        Toast.makeText ( SettingsActivity.this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT ).show ();
                        loadingBar.dismiss ();
                    } else {
                        String message = task.getException ().getMessage ();
                        Toast.makeText ( SettingsActivity.this, "Error Ocurred: " + message, Toast.LENGTH_SHORT ).show ();
                        loadingBar.dismiss ();
                    }
                }
            } );
        } catch (Exception e) {
        }
    }

    private void SendUserToMainActivity() {
        try {
            Intent mainIntent = new Intent ( SettingsActivity.this, MainActivity.class );
            mainIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( mainIntent );
            finish ();
        } catch (Exception e) {
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResult );

        if (requestCode == REQUEST_STORAGE) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText ( this, "Permission granted", Toast.LENGTH_SHORT ).show ();
            } else {
                Toast.makeText ( this, "Permission denied", Toast.LENGTH_SHORT ).show ();
            }
        }
    }
}
