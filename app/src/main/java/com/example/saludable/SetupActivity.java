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
import androidx.core.app.ActivityCompat;

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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {


    private Button SaveInformation;
    private CircleImageView ProfileImage;
    private EditText UserName, FullName, Country, Altura, Peso, Edad;
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private StorageReference UserProfileImageRef;
    private TextView Genero;
    private ProgressDialog loadingBar;
    final static int Gallery_pick = 1;
    private final int REQUEST_STORAGE = 0;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_setup );

            if (ActivityCompat.checkSelfPermission ( this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions ( this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE );


            mAuth = FirebaseAuth.getInstance ();
            currentUserID = mAuth.getCurrentUser ().getUid ();
            UserRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( currentUserID ).child ( "Informacion" );
            UserProfileImageRef = FirebaseStorage.getInstance ().getReference ().child ( "ProfileImages" );

            loadingBar = new ProgressDialog ( this );

            UserName = findViewById ( R.id.setup_username );
            FullName = findViewById ( R.id.setup_fullname );
            Country = findViewById ( R.id.setup_country );
            Altura = findViewById ( R.id.setup_estatura );
            Peso = findViewById ( R.id.setup_peso );
            Edad = findViewById ( R.id.setup_edad );

            Genero = findViewById ( R.id.setup_genero );
            radioGroup = findViewById ( R.id.radGroup );

            SaveInformation = findViewById ( R.id.setup_button );
            ProfileImage = findViewById ( R.id.setup_profile_image );


            SaveInformation.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    SaveAccountSetupInformation ();
                }
            } );

            ProfileImage.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    openGallery ();
                }
            } );

            UserRef.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists ()) {
                        if (dataSnapshot.hasChild ( "profileimage" )) {
                            String image = dataSnapshot.child ( "profileimage" ).getValue ().toString ();
                            if (!image.isEmpty ()) {
                                Picasso.with ( SetupActivity.this ).load ( image ).placeholder ( R.drawable.profile ).into ( ProfileImage );
                            }
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

    private void openGallery() {

        Intent galleryIntent = new Intent ();
        galleryIntent.setAction ( Intent.ACTION_GET_CONTENT );
        galleryIntent.setType ( "image/" );
        galleryIntent.putExtra ( "crop", "true" );
        startActivityForResult ( galleryIntent, Gallery_pick );
    }
    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId ();
        radioButton = findViewById ( radioId );
        Genero.setText ( radioButton.getText ().toString () );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if (requestCode == Gallery_pick && resultCode == RESULT_OK && data != null) {

            loadingBar.setTitle ( "Imagen de Perfil" );
            loadingBar.setMessage ( "Espere mientras cargamos su imagen de perfil" );
            loadingBar.setCanceledOnTouchOutside ( true );
            loadingBar.show ();
            Uri resultUri = data.getData ();

            final StorageReference filePath = UserProfileImageRef.child ( currentUserID + ".jpg" );

            filePath.putFile ( resultUri ).addOnCompleteListener ( new OnCompleteListener<UploadTask.TaskSnapshot> () {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful ()) {

                        filePath.getDownloadUrl ().addOnCompleteListener ( new OnCompleteListener<Uri> () {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                final String downloadUri = task.getResult ().toString ();
                                UserRef.child ( "profileimage" ).setValue ( downloadUri )
                                        .addOnCompleteListener ( new OnCompleteListener<Void> () {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful ()) {
                                                    Toast.makeText ( SetupActivity.this, "Su imagen de perfil cargada correctamente", Toast.LENGTH_SHORT ).show ();
                                                    loadingBar.dismiss ();
                                                } else {
                                                    String message = task.getException ().getMessage ();
                                                    Toast.makeText ( SetupActivity.this, "A ocurrido un error: " + message, Toast.LENGTH_SHORT ).show ();
                                                    loadingBar.dismiss ();
                                                }
                                            }
                                        } );
                            }
                        } );

                    } else {
                        String message = task.getException ().getMessage ();
                        Toast.makeText ( SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT ).show ();
                        loadingBar.dismiss ();
                    }
                }
            } );
        } else {

            Toast.makeText ( this, "A ocurrido un error. Intentelo nuevamente", Toast.LENGTH_SHORT ).show ();
            loadingBar.dismiss ();
        }
    }



    private void SaveAccountSetupInformation() {
        String username = UserName.getText ().toString ();
        String country = Country.getText ().toString ();
        String altura = Altura.getText ().toString ();
        String fullname = FullName.getText ().toString ();
        String peso = Peso.getText ().toString ();
        String edad = Edad.getText ().toString ();
        String genero = Genero.getText ().toString ();

        if (TextUtils.isEmpty ( username ) || TextUtils.isEmpty ( country ) || TextUtils.isEmpty ( altura ) || TextUtils.isEmpty ( fullname ) || TextUtils.isEmpty ( peso ) || TextUtils.isEmpty ( edad ) || TextUtils.isEmpty ( genero )) {
            Toast.makeText ( this, "Porfavor verifique que todos los campos se encuentren llenos", Toast.LENGTH_SHORT ).show ();
        } else {

            loadingBar.setTitle ( "Guardando Informacion" );
            loadingBar.setMessage ( "Espere mientras guardamos su informacion" );
            loadingBar.show ();
            loadingBar.setCanceledOnTouchOutside ( true );

            double h = Integer.parseInt ( altura );
            double p = Integer.parseInt ( peso );
            double cal = (p / (h * h)) * 10000;
            MathContext m = new MathContext ( 4 );
            BigDecimal imcnum = new BigDecimal ( cal );
            String imc = String.valueOf ( imcnum.round ( m ) );

            HashMap userMap = new HashMap ();
            userMap.put ( "username", username );
            userMap.put ( "fullname", fullname );
            userMap.put ( "country", country );
            userMap.put ( "altura", altura );
            userMap.put ( "peso", peso );
            userMap.put ( "edad", edad );
            userMap.put ( "imc", imc );
            userMap.put ( "genero", genero );
            userMap.put ( "status", "Here Saludable" );

            UserRef.updateChildren ( userMap ).addOnCompleteListener ( new OnCompleteListener () {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful ()) {
                        SendUserToMainActivity ();
                        Toast.makeText ( SetupActivity.this, "Usuario creado correctamente.", Toast.LENGTH_SHORT ).show ();
                        loadingBar.dismiss ();
                    } else {
                        String message = task.getException ().getMessage ();
                        Toast.makeText ( SetupActivity.this, "A ocurrido un error" + message, Toast.LENGTH_SHORT ).show ();
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
