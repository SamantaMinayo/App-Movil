package com.example.saludable;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saludable.Model.Post;
import com.example.saludable.Model.User;
import com.example.saludable.Utils.Common;
import com.example.saludable.ViewHolder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private Toolbar mToolbar;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView NavProfileImage;
    private TextView NavProfileusername;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostRef;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_main );

            mAuth = FirebaseAuth.getInstance ();
            UsersRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" );
            PostRef = FirebaseDatabase.getInstance ().getReference ().child ( "Posts" );

            mToolbar = findViewById ( R.id.main_page_toolbar );
            setSupportActionBar ( mToolbar );
            getSupportActionBar ().setTitle ( "Home" );

            drawerLayout = findViewById ( R.id.drawable_layout );
            actionBarDrawerToggle = new ActionBarDrawerToggle ( MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_open );
            drawerLayout.addDrawerListener ( actionBarDrawerToggle );
            actionBarDrawerToggle.syncState ();
            getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

            navigationView = findViewById ( R.id.navigation_view );
            postList = findViewById ( R.id.all_users_post_list );
            postList.setHasFixedSize ( true );

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager ( this );
            linearLayoutManager.setReverseLayout ( true );
            linearLayoutManager.setStackFromEnd ( true );
            postList.setLayoutManager ( linearLayoutManager );

            View navView = navigationView.inflateHeaderView ( R.layout.navigation_header );
            NavProfileImage = navView.findViewById ( R.id.nav_profile_image );
            NavProfileusername = navView.findViewById ( R.id.nav_user_full_name );

            navigationView.setNavigationItemSelectedListener ( new NavigationView.OnNavigationItemSelectedListener () {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    UserMenuSelector ( item );
                    return false;
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

            if (currentUser == null) {
                SendUserTologinActivity ();
            } else {
                CheckUserExistence ();
                firebaseRecyclerAdapter.startListening ();
            }
        } catch (Exception e) {
        }
    }


    private void CheckUserExistence() {
        try {

            current_user_id = mAuth.getCurrentUser ().getUid ();

            if (Common.loggedUser != null) {
                NavProfileusername.setText ( Common.loggedUser.getFullname () );
                Picasso.with ( MainActivity.this ).load ( Common.loggedUser.getProfileimage () ).placeholder ( R.drawable.profile ).into ( NavProfileImage );
            } else {
                UsersRef.addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild ( current_user_id )) {
                            SendUserToSetupActivity ();
                        } else {
                            if (!dataSnapshot.child ( current_user_id ).child ( "Informacion" ).hasChild ( "username" )) {
                                SendUserToSetupActivity ();
                            } else {
                                Common.loggedUser = dataSnapshot.child ( current_user_id ).child ( "Informacion" ).getValue ( User.class );
                                NavProfileusername.setText ( Common.loggedUser.getFullname () );
                                Picasso.with ( MainActivity.this ).load ( Common.loggedUser.getProfileimage () ).placeholder ( R.drawable.profile ).into ( NavProfileImage );
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            }
            DisplayAllUsersPosts ();

        } catch (Exception e) {
        }
    }


    protected void DisplayAllUsersPosts() {
        try {

            FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post> ()
                    .setQuery ( PostRef, Post.class ).build ();

            firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Post, PostViewHolder> ( options ) {
                        @Override
                        public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from ( parent.getContext () )
                                    .inflate ( R.layout.all_post_layout, parent, false );
                            return new PostViewHolder ( view );
                        }

                        @Override
                        protected void onBindViewHolder(PostViewHolder postViewHolder, int position, @NonNull Post post) {

                            postViewHolder.postadname.setText ( post.fullname );
                            postViewHolder.posttime.setText ( post.time );
                            postViewHolder.postdate.setText ( post.date );
                            postViewHolder.postdescript.setText ( post.description );
                            Picasso.with ( getApplication () ).load ( post.profileimage ).into ( postViewHolder.postimgprof );
                            Picasso.with ( getApplication () ).load ( post.postimage ).into ( postViewHolder.postimage );
                        }
                    };
            postList.setAdapter ( firebaseRecyclerAdapter );
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            if (actionBarDrawerToggle.onOptionsItemSelected ( item )) {
                return true;
            }
            return super.onOptionsItemSelected ( item );
        } catch (Exception e) {
            return false;
        }
    }


    private void UserMenuSelector(MenuItem item) {
        try {
            switch (item.getItemId ()) {
                case R.id.nav_profile:
                    SendUsertoProfileActivity ();
                    break;
                case R.id.nav_home:
                    Toast.makeText ( this, "Home", Toast.LENGTH_SHORT ).show ();
                    break;
                case R.id.nav_mi_inscripcion:
                    //SendUserToMiInscripcionActivity ();
                    break;
                case R.id.nav_find_marathon:
                    SendUserToMaratonActivity ();
                    break;
                case R.id.nav_mi_marathon:
                    SendUserToMiMaratonActivity ();
                    Toast.makeText ( this, "Inscripciones", Toast.LENGTH_SHORT ).show ();
                    break;
                case R.id.nav_ayuda:
                    Toast.makeText ( this, "Ayuda", Toast.LENGTH_SHORT ).show ();
                    break;
                case R.id.nav_Logout:
                    mAuth.signOut ();
                    SendUserTologinActivity ();
                    break;
            }
        } catch (Exception e) {
        }
    }

    private void SendUserToSetupActivity() {
        try {
            Intent setupIntent = new Intent ( MainActivity.this, SetupActivity.class );
            setupIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( setupIntent );
            finish ();
        } catch (Exception e) {
        }
    }

    private void SendUserTologinActivity() {
        try {
            Common.loggedUser = null;
            Intent loginIntent = new Intent ( MainActivity.this, LoginActivity.class );
            loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( loginIntent );
            finish ();
        } catch (Exception e) {
        }
    }

    private void SendUsertoProfileActivity() {
        try {
            Intent loginIntent = new Intent ( MainActivity.this, ProfileActivity.class );
            startActivity ( loginIntent );
        } catch (Exception e) {
        }
    }

    private void SendUserToMaratonActivity() {
        try {
            Intent addNewPostIntent = new Intent ( MainActivity.this, MaratonActivity.class );
            startActivity ( addNewPostIntent );
        } catch (Exception e) {
        }
    }

    private void SendUserToMiMaratonActivity() {
        try {
            Intent addNewPostIntent = new Intent ( MainActivity.this, MiMaratonActivity.class );
            startActivity ( addNewPostIntent );
        } catch (Exception e) {
        }
    }


    private void SendUserToMiInscripcionActivity() {
        try {
            //Intent addNewPostIntent = new Intent ( MainActivity.this, MiInscripcionActivity.class );
            //startActivity ( addNewPostIntent );
        } catch (Exception e) {
        }
    }

}
