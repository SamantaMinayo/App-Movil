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

public class SetupActivity extends AppCompatActivity {


    private Button SaveInformation;
    private CircleImageView ProfileImage;
    private EditText UserName, FullName, Country, Altura, Peso, Edad, Genero;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private StorageReference UserProfileImageRef;

    private ProgressDialog loadingBar;

    final static int Gallery_Pick = 1;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_setup );

            mAuth = FirebaseAuth.getInstance ();
            currentUserID = mAuth.getCurrentUser ().getUid ();
            UserRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( currentUserID );
            UserProfileImageRef = FirebaseStorage.getInstance ().getReference ().child ( "ProfileImages" );


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

            ProfileImage.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent ();
                    galleryIntent.setAction ( Intent.ACTION_GET_CONTENT );
                    galleryIntent.setType ( "image/*" );
                    startActivityForResult ( galleryIntent, Gallery_Pick );
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData ();
            CropImage.activity ()
                    .setGuidelines ( CropImageView.Guidelines.ON )
                    .setAspectRatio ( 1, 1 )
                    .start ( this );
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult ( data );

            if (resultCode == RESULT_OK) {

                loadingBar.setTitle ( "Imagen de Perfil" );
                loadingBar.setMessage ( "Espere mientras cargamos su imagen de perfil" );
                loadingBar.setCanceledOnTouchOutside ( true );
                loadingBar.show ();
                Uri resultUri = result.getUri ();

                final StorageReference filePath = UserProfileImageRef.child ( currentUserID + ".jpg" );

                filePath.putFile ( resultUri ).addOnCompleteListener ( new OnCompleteListener<UploadTask.TaskSnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful ()) {
                            Toast.makeText ( SetupActivity.this, "Su foto de perfil se ha cargado correctamente", Toast.LENGTH_SHORT ).show ();

                            filePath.getDownloadUrl ().addOnCompleteListener ( new OnCompleteListener<Uri> () {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    final String downloadUri = task.getResult ().toString ();
                                    UserRef.child ( "profileimage" ).setValue ( downloadUri )
                                            .addOnCompleteListener ( new OnCompleteListener<Void> () {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful ()) {
                                                        Intent selfIntent = new Intent ( SetupActivity.this, SetupActivity.class );
                                                        startActivity ( selfIntent );

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

                        }
                    }
                } );
            } else {

                Toast.makeText ( this, "A ocurrido un error. Intentelo nuevamente", Toast.LENGTH_SHORT ).show ();
                loadingBar.dismiss ();
            }
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
            userMap.put ( "gender", "none" );
            userMap.put ( "dob", "none" );
            userMap.put ( "relationship", "none" );

            //datos maratones
            userMap.put ( "tiempopromtotal", "0" );
            userMap.put ( "tiempopromultimo", "0" );
            userMap.put ( "velocidadpromtotal", "0" );
            userMap.put ( "velocidadpromedioultima", "0" );
            userMap.put ( "distanciatotal", "0" );
            userMap.put ( "distanciaultima", "0" );
            //Avance por carrera
            userMap.put ( "graficavelocidad", "-" );
            userMap.put ( "graficatiempo", "-" );
            userMap.put ( "graficapeso", "-" );
            userMap.put ( "graficacalorias", "-" );

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
}
