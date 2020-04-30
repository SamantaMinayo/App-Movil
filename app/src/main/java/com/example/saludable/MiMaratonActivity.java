package com.example.saludable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MiMaratonActivity extends AppCompatActivity {

    private RecyclerView postList;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, MaratonRef;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_mi_maraton );
            mAuth = FirebaseAuth.getInstance ();

            UsersRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" );

            postList = findViewById ( R.id.all_mi_maraton_list );
            postList.setHasFixedSize ( true );

            mToolbar = findViewById ( R.id.mi_maraton_toolbar );
            setSupportActionBar ( mToolbar );
            getSupportActionBar ().setTitle ( "Mis carreras" );
            getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );


            LinearLayoutManager linearLayoutManager = new LinearLayoutManager ( this );
            linearLayoutManager.setReverseLayout ( true );
            linearLayoutManager.setStackFromEnd ( true );

            postList.setLayoutManager ( linearLayoutManager );
        } catch (Exception e) {
        }
    }

    @Override
    protected void onStart() {
        super.onStart ();

        FirebaseUser currentUser = mAuth.getCurrentUser ();

        if (currentUser == null) {
            SendUserTologinActivity ();
        } else {
            current_user_id = mAuth.getCurrentUser ().getUid ();
            DisplayAllMiMaraton ();
            firebaseRecyclerAdapter.startListening ();
        }
    }

    private void SendUserTologinActivity() {
        try {
            Intent loginIntent = new Intent ( MiMaratonActivity.this, LoginActivity.class );
            loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( loginIntent );
            finish ();
        } catch (Exception e) {
        }
    }

    protected void DisplayAllMiMaraton() {
        try {

            MaratonRef = FirebaseDatabase.getInstance ().getReference ().child ( "CarrerasRealizadas" ).child ( "Usuarios" ).child ( current_user_id );

            FirebaseRecyclerOptions<MiMaraton> options = new FirebaseRecyclerOptions.Builder<MiMaraton> ()
                    .setQuery ( MaratonRef, MiMaraton.class ).build ();
            firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<MiMaraton, MiMaratonActivity.CarreraViewHolder> ( options ) {
                        @Override
                        public MiMaratonActivity.CarreraViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from ( parent.getContext () )
                                    .inflate ( R.layout.all_mi_carreras_layout, parent, false );
                            return new MiMaratonActivity.CarreraViewHolder ( view );
                        }

                        @Override
                        protected void onBindViewHolder(MiMaratonActivity.CarreraViewHolder maratonViewHolder, int position, @NonNull MiMaraton maraton) {

                            final String PostKey = getRef ( position ).getKey ();
                            if (maraton != null) {
                                if (!maraton.nombre.isEmpty () && !maraton.imagen.isEmpty () && !maraton.descripcion.isEmpty ()) {
                                    maratonViewHolder.setNamecarrera ( maraton.nombre );
                                    maratonViewHolder.setMaratonimage ( getApplication (), maraton.imagen );
                                    maratonViewHolder.setDescription ( maraton.descripcion );
                                    maratonViewHolder.mView.setOnClickListener ( new View.OnClickListener () {
                                        @Override
                                        public void onClick(View v) {
                                            Intent clickPostIntent = new Intent ( MiMaratonActivity.this, ClickMiMaratonActivity.class );
                                            clickPostIntent.putExtra ( "PostKey", PostKey );
                                            startActivity ( clickPostIntent );
                                        }
                                    } );
                                }
                            }
                        }
                    };
            postList.setAdapter ( firebaseRecyclerAdapter );

        } catch (Exception e) {
        }
    }

    public class CarreraViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Context mContext;

        public CarreraViewHolder(View itemView) {
            super ( itemView );
            mView = itemView;
            mContext = itemView.getContext ();
        }

        public void setDescription(String description) {
            if (!description.isEmpty ()) {
                TextView username = mView.findViewById ( R.id.my_maraton_description );
                username.setText ( description );
            }
        }

        public void setNamecarrera(String namecarrera) {
            TextView username = mView.findViewById ( R.id.my_maraton_name );
            username.setText ( namecarrera );
        }

        public void setMaratonimage(Context ctx, String maratonimage) {
            CircleImageView image = mView.findViewById ( R.id.my_maraton_image );
            Picasso.with ( ctx ).load ( maratonimage ).into ( image );
        }
    }

}
