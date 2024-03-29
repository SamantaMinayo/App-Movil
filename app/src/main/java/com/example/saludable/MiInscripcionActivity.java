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
import com.example.saludable.Model.Maraton;
import com.example.saludable.ViewHolder.MaratonAdapter;
import com.example.saludable.ViewHolder.MiMaratonViewHolder;
import com.example.saludable.localdatabase.DaoUsrMrtn;
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


public class MiInscripcionActivity extends AppCompatActivity implements IFirebaseLoadDone {


    FirebaseRecyclerAdapter<Maraton, MiMaratonViewHolder> adapter, searchAdapter;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    RecyclerView recycler_all_mi_maraton;
    IFirebaseLoadDone firebaseLoadDone;
    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<> ();
    private FirebaseAuth mAuth;
    private String current_user_id;
    private ArrayList<Maraton> lista;
    private DaoUsrMrtn daoUsrMrtn;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_mi_inscripcion );

            mAuth = FirebaseAuth.getInstance ();
            current_user_id = mAuth.getCurrentUser ().getUid ();

            mToolbar = findViewById ( R.id.mi_inscripcion_toolbar );
            setSupportActionBar ( mToolbar );
            getSupportActionBar ().setTitle ( "Mis Proximas Carreras" );
            getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

            daoUsrMrtn = new DaoUsrMrtn ( this );
            searchBar = findViewById ( R.id.my_inscripcion_search_bar );
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

            recycler_all_mi_maraton = findViewById ( R.id.all_my_inscripcion_post_list );
            recycler_all_mi_maraton.setHasFixedSize ( true );
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager ( this );
            recycler_all_mi_maraton.setLayoutManager ( layoutManager );
            recycler_all_mi_maraton.addItemDecoration ( new DividerItemDecoration ( this, ((LinearLayoutManager) layoutManager).getOrientation () ) );


            firebaseLoadDone = this;
            loadSearchData ();

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MiInscripcionActivity" ).child ( "OnCreate" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    @Override
    protected void onStart() {
        super.onStart ();
        lista = daoUsrMrtn.ObtenerMaratonList ( "ins" );
        loadInsList ();

    }

    private void loadSearchData() {
        try {
            final List<String> lstMiMaratonName = new ArrayList<> ();
            DatabaseReference MiMaratonList = FirebaseDatabase.getInstance ()
                    .getReference ( "Users" ).child ( current_user_id ).child ( "Inscripcion" );
            MiMaratonList.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot MaratonSnapshot : dataSnapshot.getChildren ()) {
                        Maraton mimaraton = MaratonSnapshot.getValue ( Maraton.class );
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
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MiInscripcionActivity" ).child ( "LoadSearchData" ).child ( current_user_id ).updateChildren ( error );
        }
    }

    private void loadInsList() {
        try {


            Query query = FirebaseDatabase.getInstance ().getReference ().child ( "Users" ).child ( current_user_id ).child ( "Inscripcion" );

            FirebaseRecyclerOptions<Maraton> options = new FirebaseRecyclerOptions.Builder<Maraton> ()
                    .setQuery ( query, Maraton.class )
                    .build ();

            adapter = new FirebaseRecyclerAdapter<Maraton, MiMaratonViewHolder> ( options ) {
                @Override
                protected void onBindViewHolder(@NonNull MiMaratonViewHolder maratonViewHolder, int position, @NonNull Maraton maraton) {
                    maratonViewHolder.maratonname.setText ( maraton.maratonname );
                    maratonViewHolder.maratondate.setText ( maraton.maratondate );
                    Picasso.with ( getApplication () ).load ( maraton.maratonimage ).into ( maratonViewHolder.maratonimage );
                    maratonViewHolder.maratondescription.setText ( maraton.description );
                    final String PostKey = getRef ( position ).getKey ();
                    maratonViewHolder.setiRecyclerItemClickListener ( new IRecyclerItemClickListener () {
                        @Override
                        public void onItemClickListener(View view, int position) {
                            Intent clickPostIntent = new Intent ( MiInscripcionActivity.this, ClickMaratonActivity.class );
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
            if (lista != null) {
                mAdapter = new MaratonAdapter ( lista, getApplication (), "ins" );
                layoutManager = new LinearLayoutManager ( this );
                recycler_all_mi_maraton.setLayoutManager ( layoutManager );
                recycler_all_mi_maraton.setAdapter ( mAdapter );
            }
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MiInscripcionActivity" ).child ( "loadInsList" ).child ( current_user_id ).updateChildren ( error );
        }

    }

    private void startSearch(String text_search) {
        try {
            Query query = FirebaseDatabase.getInstance ()
                    .getReference ()
                    .child ( "Users" )
                    .child ( current_user_id )
                    .child ( "Inscripcion" )
                    .orderByChild ( "maratonname" )
                    .startAt ( text_search );

            FirebaseRecyclerOptions<Maraton> options = new FirebaseRecyclerOptions.Builder<Maraton> ()
                    .setQuery ( query, Maraton.class )
                    .build ();

            searchAdapter = new FirebaseRecyclerAdapter<Maraton, MiMaratonViewHolder> ( options ) {
                @Override
                protected void onBindViewHolder(@NonNull MiMaratonViewHolder maratonViewHolder, int position, @NonNull Maraton maraton) {

                    maratonViewHolder.maratonname.setText ( maraton.maratonname );
                    maratonViewHolder.maratondate.setText ( "Fecha: " + maraton.maratondate );
                    maratonViewHolder.maratondescription.setText ( maraton.description );
                    Picasso.with ( getApplication () ).load ( maraton.maratonimage ).into ( maratonViewHolder.maratonimage );
                    final String PostKey = getRef ( position ).getKey ();
                    maratonViewHolder.setiRecyclerItemClickListener ( new IRecyclerItemClickListener () {
                        @Override
                        public void onItemClickListener(View view, int position) {
                            Intent clickPostIntent = new Intent ( MiInscripcionActivity.this, ClickMiMaratonActivity.class );
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
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MiInscripcionActivity" ).child ( "startSearch" ).child ( current_user_id ).updateChildren ( error );
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
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MiInscripcionActivity" ).child ( "OnStop" ).child ( current_user_id ).updateChildren ( error );
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
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MiInscripcionActivity" ).child ( "OnResume" ).child ( current_user_id ).updateChildren ( error );
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
