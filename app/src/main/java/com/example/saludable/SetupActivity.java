package com.example.saludable;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText UserName, FullName, Country, Altura, Peso, FechaNacimiento;
    private Button SaveInformation;
    private CircleImageView ProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_setup );

        UserName = findViewById ( R.id.setup_username );
        FullName = findViewById ( R.id.setup_fullname );
        Country = findViewById ( R.id.setup_country );
        Altura = findViewById ( R.id.setup_estatura );
        Peso = findViewById ( R.id.setup_peso );
        FechaNacimiento = findViewById ( R.id.setup_fecha_nacimiento );
        SaveInformation = findViewById ( R.id.setup_button );
        ProfileImage = findViewById ( R.id.setup_profile_image );


    }
}
