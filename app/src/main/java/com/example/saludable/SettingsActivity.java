package com.example.saludable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsActivity extends AppCompatActivity {


    final static int Gallery_pick = 1;
    private Toolbar mToolbar;
    private EditText userName, userProfName, userStatus, userCountry, userGenero, userAltura, userPeso, userEdad;
    private Button UpdateAccountSettingsButton;
    private CircleImageView userProfileImage;
    private ProgressDialog loadingBar;
    private RecyclerView postList;
    private DatabaseReference SettingsUserRef, PostRef;
    private FirebaseAuth mAuth;
    private StorageReference UserProfileImageRef;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private String currentUserId, updateprofilename, updateprofileimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_settings );


        mAuth = FirebaseAuth.getInstance ();
        currentUserId = mAuth.getCurrentUser ().getUid ();
        SettingsUserRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( currentUserId );
        UserProfileImageRef = FirebaseStorage.getInstance ().getReference ().child ( "ProfileImages" );
        PostRef = FirebaseDatabase.getInstance ().getReference ().child ( "Posts" );

        mToolbar = findViewById ( R.id.settings_toolbar );
        setSupportActionBar ( mToolbar );
        getSupportActionBar ().setTitle ( "Account Settings" );
        getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );


        userName = findViewById ( R.id.settings_username );
        userProfName = findViewById ( R.id.settings_profile_full_name );
        userStatus = findViewById ( R.id.settings_status );
        userCountry = findViewById ( R.id.settings_country );
        userGenero = findViewById ( R.id.settings_genero );
        userEdad = findViewById ( R.id.settings_edad );
        userAltura = findViewById ( R.id.settings_estatura );
        userPeso = findViewById ( R.id.settings_peso );
        userProfileImage = findViewById ( R.id.settings_profile_image );
        UpdateAccountSettingsButton = findViewById ( R.id.update_account_settings_button );
        loadingBar = new ProgressDialog ( this );

        SettingsUserRef.addValueEventListener ( new ValueEventListener () {
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


                    updateprofilename = myProfileName;
                    updateprofileimage = myProfileImage;

                    Picasso.with ( SettingsActivity.this ).load ( myProfileImage ).placeholder ( R.drawable.profile ).into ( userProfileImage );
                    userName.setText ( myUsername );
                    userProfName.setText ( myProfileName );
                    userStatus.setText ( myProfileStatus );
                    userCountry.setText ( myCountry );
                    userGenero.setText ( myGenero );
                    userPeso.setText ( myPeso );
                    userAltura.setText ( myAltura );
                    userEdad.setText ( myEdad );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        UpdateAccountSettingsButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo ();
            }
        } );

        userProfileImage.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent ();
                galleryIntent.setAction ( Intent.ACTION_GET_CONTENT );
                galleryIntent.setType ( "image/" );
                startActivityForResult ( galleryIntent, Gallery_pick );
            }

        } );

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if (requestCode == Gallery_pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData ();
            CropImage.activity ()
                    .setGuidelines ( CropImageView.Guidelines.ON )
                    .setAspectRatio ( 1, 1 )
                    .start ( this );
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult ( data );

            if (resultCode == RESULT_OK) {

                loadingBar.setTitle ( "Profile Image" );
                loadingBar.setMessage ( "Please wait, while we updating your profile image..." );
                loadingBar.setCanceledOnTouchOutside ( true );
                loadingBar.show ();


                Uri resultUri = result.getUri ();

                final StorageReference filePath = UserProfileImageRef.child ( currentUserId + ".jpg" );

                filePath.putFile ( resultUri ).addOnCompleteListener ( new OnCompleteListener<UploadTask.TaskSnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful ()) {
                            Toast.makeText ( SettingsActivity.this, "Profile image stored successfully to Firebase", Toast.LENGTH_SHORT ).show ();

                            filePath.getDownloadUrl ().addOnCompleteListener ( new OnCompleteListener<Uri> () {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    final String downloadUri = task.getResult ().toString ();
                                    SettingsUserRef.child ( "profileimage" ).setValue ( downloadUri )
                                            .addOnCompleteListener ( new OnCompleteListener<Void> () {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful ()) {
                                                        Intent selfIntent = new Intent ( SettingsActivity.this, SettingsActivity.class );
                                                        startActivity ( selfIntent );

                                                        Toast.makeText ( SettingsActivity.this, "Profile image stored to Firebase Databse Storage", Toast.LENGTH_SHORT ).show ();
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

                Toast.makeText ( this, "Error Occured: Image can be cropped. Try Again", Toast.LENGTH_SHORT ).show ();
                loadingBar.dismiss ();
            }
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

        if (TextUtils.isEmpty ( username )) {
            Toast.makeText ( this, "Please write your username...", Toast.LENGTH_SHORT ).show ();

        } else if (TextUtils.isEmpty ( profilename )) {
            Toast.makeText ( this, "Please write your username...", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( status )) {
            Toast.makeText ( this, "Please write your username...", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( country )) {
            Toast.makeText ( this, "Please write your username...", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( genero )) {
            Toast.makeText ( this, "Please write your username...", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( edad )) {
            Toast.makeText ( this, "Please write your username...", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( altura )) {
            Toast.makeText ( this, "Please write your username...", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( peso )) {
            Toast.makeText ( this, "Please write your username...", Toast.LENGTH_SHORT ).show ();
        } else {

            loadingBar.setTitle ( "Profile Image" );
            loadingBar.setMessage ( "Please wait, while we updating your profile image..." );
            loadingBar.setCanceledOnTouchOutside ( true );
            loadingBar.show ();

            UpdateAccountInfo ( username, profilename, status, country, genero, edad, altura, peso );
        }
    }

    private void UpdateAccountInfo(String username, final String profilename, String status, String country, String genero, String edad, String altura, String peso) {

        HashMap userMap = new HashMap ();

        userMap.put ( "username", username );
        userMap.put ( "fullname", profilename );
        userMap.put ( "status", status );
        userMap.put ( "country", country );
        userMap.put ( "genero", genero );
        userMap.put ( "edad", edad );
        userMap.put ( "altura", altura );
        userMap.put ( "peso", peso );

        SettingsUserRef.updateChildren ( userMap ).addOnCompleteListener ( new OnCompleteListener () {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful ()) {
                    SendUserToMainActivity ();
                    Toast.makeText ( SettingsActivity.this, "Upload succesfully...", Toast.LENGTH_SHORT ).show ();
                    loadingBar.dismiss ();
                } else {
                    String message = task.getException ().getMessage ();
                    Toast.makeText ( SettingsActivity.this, "Error Ocurred: " + message, Toast.LENGTH_SHORT ).show ();
                    loadingBar.dismiss ();

                }
            }
        } );

    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent ( SettingsActivity.this, MainActivity.class );
        mainIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( mainIntent );
        finish ();
    }

}
