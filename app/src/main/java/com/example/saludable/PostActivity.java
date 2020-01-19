package com.example.saludable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    private static final int Gallery_Pick = 1;
    private Toolbar mToolbar;
    private ProgressDialog loadingBar;
    private ImageButton SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;
    private Uri ImageUri;
    private String Description, saveCurrentData, saveCurrenTime, postRandomName, downloadUri, current_user_id;


    private StorageReference PostImageReference;
    private DatabaseReference UsersRef, Postsref;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_post );

        mAuth = FirebaseAuth.getInstance ();
        current_user_id = mAuth.getCurrentUser ().getUid ();

        PostImageReference = FirebaseStorage.getInstance ().getReference ();
        UsersRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" );
        Postsref = FirebaseDatabase.getInstance ().getReference ().child ( "Posts" );

        SelectPostImage = findViewById ( R.id.select_post_image );
        UpdatePostButton = findViewById ( R.id.update_post_button );
        PostDescription = findViewById ( R.id.post_description );
        loadingBar = new ProgressDialog ( this );

        mToolbar = findViewById ( R.id.update_post_page_toolbar );
        setSupportActionBar ( mToolbar );
        getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );
        getSupportActionBar ().setDisplayShowHomeEnabled ( true );
        getSupportActionBar ().setTitle ( "Update Post" );


        SelectPostImage.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                OpenGallery ();
            }
        } );

        UpdatePostButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                ValidatePostInfo ();
            }
        } );
    }

    private void ValidatePostInfo() {
        Description = PostDescription.getText ().toString ();
        if (ImageUri == null) {
            Toast.makeText ( this, "Please select post image", Toast.LENGTH_SHORT ).show ();
        } else if (TextUtils.isEmpty ( Description )) {
            Toast.makeText ( this, "Please say something about you image", Toast.LENGTH_SHORT ).show ();
        } else {

            loadingBar.setTitle ( "Add New Post" );
            loadingBar.setMessage ( "Please wait, while we updating your new post" );
            loadingBar.show ();
            loadingBar.setCanceledOnTouchOutside ( true );

            StoringImageToFirebaseStorage ();
        }
    }

    private void StoringImageToFirebaseStorage() {
        Calendar calFord = Calendar.getInstance ();
        SimpleDateFormat currentDate = new SimpleDateFormat ( "dd-MM-yyyy" );
        saveCurrentData = currentDate.format ( calFord.getTime () );

        Calendar calFordTime = Calendar.getInstance ();
        SimpleDateFormat currentTime = new SimpleDateFormat ( "HH:mm" );
        saveCurrenTime = currentTime.format ( calFordTime.getTime () );

        postRandomName = saveCurrentData + saveCurrenTime;

        final StorageReference filePath = PostImageReference.child ( "PostImages" ).child ( ImageUri.getLastPathSegment () + postRandomName + ".jpg" );
        filePath.putFile ( ImageUri ).addOnCompleteListener ( new OnCompleteListener<UploadTask.TaskSnapshot> () {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful ()) {
                    Toast.makeText ( PostActivity.this, "image upload succesfully to storage ", Toast.LENGTH_SHORT ).show ();

                    filePath.getDownloadUrl ().addOnCompleteListener ( new OnCompleteListener<Uri> () {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful ()) {
                                downloadUri = task.getResult ().toString ();
                                SavingPostInformationToDatabase ();
                            }

                        }
                    } );
                } else {
                    String message = task.getException ().toString ();
                    Toast.makeText ( PostActivity.this, "Error Ocurred: " + message, Toast.LENGTH_SHORT ).show ();
                }
            }
        } );
    }

    private void SavingPostInformationToDatabase() {
        UsersRef.child ( current_user_id ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists ()) {
                    String userfullName = dataSnapshot.child ( "fullname" ).getValue ().toString ();
                    String userProfileImage = dataSnapshot.child ( "profileimage" ).getValue ().toString ();

                    HashMap postsMap = new HashMap ();
                    postsMap.put ( "uid", current_user_id );
                    postsMap.put ( "date", saveCurrentData );
                    postsMap.put ( "time", saveCurrenTime );
                    postsMap.put ( "description", Description );
                    postsMap.put ( "postimage", downloadUri );
                    postsMap.put ( "profileimage", userProfileImage );
                    postsMap.put ( "fullname", userfullName );

                    Postsref.child ( current_user_id + postRandomName ).updateChildren ( postsMap )
                            .addOnCompleteListener ( new OnCompleteListener () {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful ()) {
                                        SendUserToMainActivity ();
                                        Toast.makeText ( PostActivity.this, "New Post is Update succesfully..", Toast.LENGTH_SHORT ).show ();
                                        loadingBar.dismiss ();
                                    } else {
                                        String message = task.getException ().toString ();
                                        Toast.makeText ( PostActivity.this, "Error Ocurred: " + message, Toast.LENGTH_SHORT ).show ();
                                        loadingBar.dismiss ();
                                    }
                                }
                            } );
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent ();
        galleryIntent.setAction ( Intent.ACTION_GET_CONTENT );
        galleryIntent.setType ( "image/*" );
        startActivityForResult ( galleryIntent, Gallery_Pick );

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData ();
            SelectPostImage.setImageURI ( ImageUri );

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId ();
        if (id == android.R.id.home) {
            SendUserToMainActivity ();
        }
        return super.onOptionsItemSelected ( item );
    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent ( PostActivity.this, MainActivity.class );
        startActivity ( mainIntent );
    }
}
