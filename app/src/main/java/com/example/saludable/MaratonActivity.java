package com.example.saludable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MaratonActivity extends AppCompatActivity {


    private RecyclerView postList;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, MaratonRef, RegistrarUsuario;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private String current_user_id;
    private boolean inscrito = false;
    private int eliminarrow = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_maraton );

            mAuth = FirebaseAuth.getInstance ();

            UsersRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" );
            MaratonRef = FirebaseDatabase.getInstance ().getReference ().child ( "Carreras" );


            postList = findViewById ( R.id.all_maraton_list );
            postList.setHasFixedSize ( true );

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
            CheckUserExistence ();
            firebaseRecyclerAdapter.startListening ();
        }
    }


    private void CheckUserExistence() {
        try {
            current_user_id = mAuth.getCurrentUser ().getUid ();

            UsersRef.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild ( current_user_id )) {
                        SendUserToSetupActivity ();
                    } else {
                        if (!dataSnapshot.child ( current_user_id ).hasChild ( "username" )) {
                            SendUserToSetupActivity ();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
            DisplayAllMaraton ();
        } catch (Exception e) {
        }
    }

    private void SendUserToSetupActivity() {
        try {
            Intent setupIntent = new Intent ( MaratonActivity.this, SetupActivity.class );
            setupIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( setupIntent );
            finish ();
        } catch (Exception e) {
        }
    }

    private void SendUserTologinActivity() {
        try {
            Intent loginIntent = new Intent ( MaratonActivity.this, LoginActivity.class );
            loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( loginIntent );
            finish ();
        } catch (Exception e) {
        }
    }


    protected void DisplayAllMaraton() {
        try {

            FirebaseRecyclerOptions<Maraton> options = new FirebaseRecyclerOptions.Builder<Maraton> ()
                    .setQuery ( MaratonRef, Maraton.class ).build ();
            firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Maraton, MaratonActivity.CarreraViewHolder> ( options ) {
                        @Override
                        public MaratonActivity.CarreraViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from ( parent.getContext () )
                                    .inflate ( R.layout.all_carreras_layout, parent, false );
                            return new MaratonActivity.CarreraViewHolder ( view );
                        }

                        @Override
                        protected void onBindViewHolder(MaratonActivity.CarreraViewHolder maratonViewHolder, int position, @NonNull Maraton maraton) {

                            final String PostKey = getRef ( position ).getKey ();

                            maratonViewHolder.setNamecarrera ( maraton.maratonname );
                            maratonViewHolder.setDate ( maraton.date );
                            maratonViewHolder.setTime ( maraton.time );
                            maratonViewHolder.setMaratonimage ( getApplication (), maraton.maratonimage );
                            maratonViewHolder.setDescription ( maraton.description );
                            maratonViewHolder.setLugar ( maraton.place );
                            maratonViewHolder.setDate_maraton ( maraton.maratondate );
                            maratonViewHolder.setTime_maraton ( maraton.maratontime );
                            maratonViewHolder.setContactname ( maraton.contactname );
                            maratonViewHolder.setContactnumber ( maraton.contactnumber );

                            maratonViewHolder.mView.setOnClickListener ( new View.OnClickListener () {
                                @Override
                                public void onClick(View v) {
                                    Intent clickPostIntent = new Intent ( MaratonActivity.this, ClickMaratonActivity.class );
                                    clickPostIntent.putExtra ( "PostKey", PostKey );
                                    startActivity ( clickPostIntent );
                                }
                            } );
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
                TextView username = mView.findViewById ( R.id.maraton_description );
                username.setText ( description );
            }
        }

        public void setContactname(String contactname) {
            TextView username = mView.findViewById ( R.id.contact_name );
            username.setText ( "Contacto: " + contactname );
        }

        public void setContactnumber(String contactnumber) {
            TextView username = mView.findViewById ( R.id.contact_number );
            username.setText ( "    cel: " + contactnumber );
        }

        public void setTime_maraton(String time_maraton) {
            TextView username = mView.findViewById ( R.id.maraton_time );
            username.setText ( "Hora: " + time_maraton );
        }

        public void setDate_maraton(String date_maraton) {
            TextView username = mView.findViewById ( R.id.maraton_date );
            username.setText ( "Fecha: " + date_maraton );
        }

        public void setLugar(String lugar) {
            TextView username = mView.findViewById ( R.id.maraton_place );
            username.setText ( "Direccion: " + lugar );
        }

        public void setNamecarrera(String namecarrera) {
            TextView username = mView.findViewById ( R.id.maraton_name );
            username.setText ( namecarrera );
        }

        public void setMaratonimage(Context ctx, String maratonimage) {
            ImageView image = mView.findViewById ( R.id.click_maraton_image );
            Picasso.with ( ctx ).load ( maratonimage ).into ( image );
        }

        public void setTime(String time) {
            TextView postTime = mView.findViewById ( R.id.post_admin_time );
            postTime.setText ( "  " + time );
        }

        public void setDate(String date) {
            TextView postDate = mView.findViewById ( R.id.post_admin_date );
            postDate.setText ( "  " + date );
        }
    }

}
