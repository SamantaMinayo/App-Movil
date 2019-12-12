package com.example.saludable;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        mAuth = FirebaseAuth.getInstance ();

        mToolbar = findViewById ( R.id.main_page_toolbar );
        setSupportActionBar ( mToolbar );
        getSupportActionBar ().setTitle ( "Home" );

        drawerLayout = findViewById ( R.id.drawable_layout );
        actionBarDrawerToggle = new ActionBarDrawerToggle ( MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_open );
        drawerLayout.addDrawerListener ( actionBarDrawerToggle );
        actionBarDrawerToggle.syncState ();
        getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );
        navigationView = findViewById ( R.id.navigation_view );
        View navView = navigationView.inflateHeaderView ( R.layout.navigation_header );


        navigationView.setNavigationItemSelectedListener ( new NavigationView.OnNavigationItemSelectedListener () {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector ( item );

                return false;
            }
        } );
    }


    @Override
    protected void onStart() {
        super.onStart ();

        FirebaseUser currentUser = mAuth.getCurrentUser ();

        if (currentUser == null) {
            SendUserTologinActivity ();
        }
    }

    private void SendUserTologinActivity() {
        Intent loginIntent = new Intent ( MainActivity.this, LoginActivity.class );
        loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( loginIntent );
        finish ();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected ( item )) {
            return true;
        }
        return super.onOptionsItemSelected ( item );
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.nav_profile:
                Toast.makeText ( this, "Profile", Toast.LENGTH_SHORT ).show ();
                break;
            case R.id.nav_home:
                Toast.makeText ( this, "Home", Toast.LENGTH_SHORT ).show ();
                break;
            case R.id.nav_marathon:
                Toast.makeText ( this, "Marathon", Toast.LENGTH_SHORT ).show ();
                break;
            case R.id.nav_find_marathon:
                Toast.makeText ( this, "Find Marathon", Toast.LENGTH_SHORT ).show ();
                break;
            case R.id.nav_message:
                Toast.makeText ( this, "Messages", Toast.LENGTH_SHORT ).show ();
                break;
            case R.id.nav_settings:
                Toast.makeText ( this, "Settings", Toast.LENGTH_SHORT ).show ();
                break;
            case R.id.nav_Logout:
                Toast.makeText ( this, "logout", Toast.LENGTH_SHORT ).show ();
                break;

        }

    }


}
