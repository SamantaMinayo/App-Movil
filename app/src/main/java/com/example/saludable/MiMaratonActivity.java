package com.example.saludable;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saludable.Interfaces.IFirebaseLoadDone;
import com.example.saludable.Interfaces.IRecyclerItemClickListener;
import com.example.saludable.Model.MiMaraton;
import com.example.saludable.ViewHolder.MiMaratonViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MiMaratonActivity extends AppCompatActivity implements IFirebaseLoadDone {


    FirebaseRecyclerAdapter<MiMaraton, MiMaratonViewHolder> adapter, searchAdapter;
    RecyclerView recycler_all_mi_maraton;
    IFirebaseLoadDone firebaseLoadDone;
    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<> ();
    private FirebaseAuth mAuth;
    private String current_user_id;

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_mi_maraton );

            mAuth = FirebaseAuth.getInstance ();
            current_user_id = mAuth.getCurrentUser ().getUid ();

            mToolbar = findViewById ( R.id.mi_maratons_toolbar );
            setSupportActionBar ( mToolbar );
            getSupportActionBar ().setTitle ( "Mis Carreras" );
            getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

            searchBar = findViewById ( R.id.my_material_search_bar );
            searchBar.setCardViewElevation ( 10 );
            searchBar.addTextChangeListener ( new TextWatcher () {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest = new ArrayList<> ();
                    for (String search : suggestList) {
                        if (search.toLowerCase ().contains ( searchBar.getText ().toLowerCase () ))
                            suggest.add ( search );
                    }
                    searchBar.setLastSuggestions ( suggest );
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            } );

            searchBar.setOnSearchActionListener ( new MaterialSearchBar.OnSearchActionListener () {
                @Override
                public void onSearchStateChanged(boolean enabled) {
                    if (!enabled) {
                        if (adapter != null) {
                            recycler_all_mi_maraton.setAdapter ( adapter );
                        }
                    }
                }

                @Override
                public void onSearchConfirmed(CharSequence text) {

                    startSearch ( text.toString () );
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            } );

            recycler_all_mi_maraton = findViewById ( R.id.all_my_maratons_post_list );
            recycler_all_mi_maraton.setHasFixedSize ( true );
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager ( this );
            recycler_all_mi_maraton.setLayoutManager ( layoutManager );
            recycler_all_mi_maraton.addItemDecoration ( new DividerItemDecoration ( this, ((LinearLayoutManager) layoutManager).getOrientation () ) );


            firebaseLoadDone = this;

            loadMiMaratonList ();
            loadSearchData ();

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MiMaratonActivity" ).child ( "OnCreate" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    private void loadSearchData() {
        try {
            final List<String> lstMiMaratonName = new ArrayList<> ();
            DatabaseReference MiMaratonList = FirebaseDatabase.getInstance ()
                    .getReference ( "Users" ).child ( current_user_id ).child ( "Inscripcion" );
            MiMaratonList.addListenerForSingleValueEvent ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot MaratonSnapshot : dataSnapshot.getChildren ()) {
                        MiMaraton mimaraton = MaratonSnapshot.getValue ( MiMaraton.class );
                        lstMiMaratonName.add ( mimaraton.maratonname );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    firebaseLoadDone.onFirebaseLoadFaile ( databaseError.getMessage () );
                }
            } );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MiMaratonActivity" ).child ( "LoadSearchData" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    private void loadMiMaratonList() {
        try {
            Query query = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( current_user_id ).child ( "Resultados" ).child ( "Lista" );

            FirebaseRecyclerOptions<MiMaraton> options = new FirebaseRecyclerOptions.Builder<MiMaraton> ()
                    .setQuery ( query, MiMaraton.class )
                    .build ();

            adapter = new FirebaseRecyclerAdapter<MiMaraton, MiMaratonViewHolder> ( options ) {
                @Override
                protected void onBindViewHolder(@NonNull MiMaratonViewHolder maratonViewHolder, int position, @NonNull MiMaraton maraton) {

                    maratonViewHolder.maratonname.setText ( maraton.maratonname );
                    maratonViewHolder.maratondate.setText ( maraton.date );
                    Picasso.with ( getApplication () ).load ( maraton.maratonimagen ).into ( maratonViewHolder.maratonimage );
                    maratonViewHolder.maratondescription.setText ( maraton.maratondescription );
                    final String PostKey = getRef ( position ).getKey ();
                    maratonViewHolder.setiRecyclerItemClickListener ( new IRecyclerItemClickListener () {
                        @Override
                        public void onItemClickListener(View view, int position) {
                            Intent clickPostIntent = new Intent ( MiMaratonActivity.this, ClickMiMaratonActivity.class );
                            clickPostIntent.putExtra ( "PostKey", PostKey );
                            startActivity ( clickPostIntent );
                        }
                    } );

                }

                @NonNull
                @Override
                public MiMaratonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View itemView = LayoutInflater.from ( parent.getContext () )
                            .inflate ( R.layout.all_mi_carreras_layout, parent, false );
                    return new MiMaratonViewHolder ( itemView );
                }
            };

            adapter.startListening ();
            recycler_all_mi_maraton.setAdapter ( adapter );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MiMaratonActivity" ).child ( "LoadUserList" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    private void startSearch(String text_search) {
        try {
            Query query = FirebaseDatabase.getInstance ()
                    .getReference ()
                    .child ( "Users" )
                    .child ( current_user_id )
                    .child ( "Resultados" )
                    .child ( "Lista" )
                    .orderByChild ( "maratonname" )
                    .startAt ( text_search );

            FirebaseRecyclerOptions<MiMaraton> options = new FirebaseRecyclerOptions.Builder<MiMaraton> ()
                    .setQuery ( query, MiMaraton.class )
                    .build ();

            searchAdapter = new FirebaseRecyclerAdapter<MiMaraton, MiMaratonViewHolder> ( options ) {
                @Override
                protected void onBindViewHolder(@NonNull MiMaratonViewHolder maratonViewHolder, int position, @NonNull MiMaraton maraton) {

                    maratonViewHolder.maratonname.setText ( maraton.maratonname );
                    maratonViewHolder.maratondate.setText ( maraton.date );
                    Picasso.with ( getApplication () ).load ( maraton.maratonimagen ).into ( maratonViewHolder.maratonimage );
                    maratonViewHolder.maratondescription.setText ( maraton.maratondescription );
                    final String PostKey = getRef ( position ).getKey ();
                    maratonViewHolder.setiRecyclerItemClickListener ( new IRecyclerItemClickListener () {
                        @Override
                        public void onItemClickListener(View view, int position) {
                            Intent clickPostIntent = new Intent ( MiMaratonActivity.this, ClickMiMaratonActivity.class );
                            clickPostIntent.putExtra ( "PostKey", PostKey );
                            startActivity ( clickPostIntent );
                        }
                    } );

                }

                @NonNull
                @Override
                public MiMaratonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View itemView = LayoutInflater.from ( parent.getContext () )
                            .inflate ( R.layout.all_mi_carreras_layout, parent, false );
                    return new MiMaratonViewHolder ( itemView );
                }
            };
            searchAdapter.startListening ();
            recycler_all_mi_maraton.setAdapter ( searchAdapter );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MiMaratonActivity" ).child ( "startSearch" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    @Override
    protected void onStop() {
        try {
            super.onStop ();
            adapter.stopListening ();
            super.onStop ();
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MiMaratonActivity" ).child ( "OnStop" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume ();
            if (adapter != null) {
                adapter.startListening ();
            }

            if (searchAdapter != null) {
                searchAdapter.startListening ();
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MiMaratonActivity" ).child ( "OnResume" ).child ( current_user_id ).updateChildren ( error );
        }

    }

    @Override
    public void onFirebaseLoadMaratonDone(List<String> lstMaraton) {
        searchBar.setLastSuggestions ( lstMaraton );

    }

    @Override
    public void onFirebaseLoadFaile(String message) {
        Toast.makeText ( this, message, Toast.LENGTH_SHORT ).show ();

    }

}
