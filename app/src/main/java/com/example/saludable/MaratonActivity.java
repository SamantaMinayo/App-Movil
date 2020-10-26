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
import com.example.saludable.Utils.Common;
import com.example.saludable.ViewHolder.MaratonViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class MaratonActivity extends AppCompatActivity implements IFirebaseLoadDone {


    FirebaseRecyclerAdapter<Maraton, MaratonViewHolder> adapter, searchAdapter;
    RecyclerView recycler_all_maraton;
    IFirebaseLoadDone firebaseLoadDone;
    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<> ();
    List<Maraton> maratones = new ArrayList<Maraton> ();
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate ( savedInstanceState );
            setContentView ( R.layout.activity_maraton );

            mToolbar = findViewById ( R.id.marathon_page_toolbar );
            setSupportActionBar ( mToolbar );
            getSupportActionBar ().setTitle ( "Buscar Carreras" );
            getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );

            searchBar = findViewById ( R.id.material_search_bar );
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
                            recycler_all_maraton.setAdapter ( adapter );
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
            recycler_all_maraton = findViewById ( R.id.all_maratons_post_list );
            recycler_all_maraton.setHasFixedSize ( true );
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager ( this );
            recycler_all_maraton.setLayoutManager ( layoutManager );
            recycler_all_maraton.addItemDecoration ( new DividerItemDecoration ( this, ((LinearLayoutManager) layoutManager).getOrientation () ) );


            firebaseLoadDone = this;

            loadMaratonList ();
            loadSearchData ();

        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MaratonActivity" ).child ( "OnCreate" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void loadSearchData() {
        try {
            final List<String> lstMaratonName = new ArrayList<> ();
            DatabaseReference MaratonList = FirebaseDatabase.getInstance ()
                    .getReference ( "Carreras" ).child ( "Nuevas" );
            MaratonList.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot MaratonSnapshot : dataSnapshot.getChildren ()) {
                        Maraton maraton = MaratonSnapshot.getValue ( Maraton.class );
                        lstMaratonName.add ( maraton.maratonname );
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
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MaratonActivity" ).child ( "LoadSearchData" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }
    }

    private void loadMaratonList() {
        try {
            Query query = FirebaseDatabase.getInstance ().getReference ().
                    child ( "Carreras" ).
                    child ( "Nuevas" )
                    .orderByChild ( "estado" )
                    .equalTo ( "false" );

            FirebaseRecyclerOptions<Maraton> options = new FirebaseRecyclerOptions.Builder<Maraton> ()
                    .setQuery ( query, Maraton.class )
                    .build ();

            adapter = new FirebaseRecyclerAdapter<Maraton, MaratonViewHolder> ( options ) {
                @Override
                protected void onBindViewHolder(@NonNull MaratonViewHolder maratonViewHolder, int position, @NonNull Maraton maraton) {

                    maratones.add ( maraton );
                    maratonViewHolder.maratonname.setText ( maraton.maratonname );
                    Picasso.with ( getApplication () ).load ( maraton.maratonimage ).into ( maratonViewHolder.maratonimage );
                    maratonViewHolder.maratondescription.setText ( maraton.description );
                    maratonViewHolder.maratonplace.setText ( maraton.place );
                    maratonViewHolder.maratondate.setText ( maraton.maratondate );
                    maratonViewHolder.maratontime.setText ( maraton.maratontime );
                    final String PostKey = getRef ( position ).getKey ();
                    maratonViewHolder.setiRecyclerItemClickListener ( new IRecyclerItemClickListener () {
                        @Override
                        public void onItemClickListener(View view, int position) {
                            Intent clickPostIntent = new Intent ( MaratonActivity.this, ClickMaratonActivity.class );
                            clickPostIntent.putExtra ( "PostKey", PostKey );
                            startActivity ( clickPostIntent );
                        }
                    } );

                }

                @NonNull
                @Override
                public MaratonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View itemView = LayoutInflater.from ( parent.getContext () )
                            .inflate ( R.layout.all_carreras_layout, parent, false );
                    return new MaratonViewHolder ( itemView );
                }
            };

            adapter.startListening ();
            recycler_all_maraton.setAdapter ( adapter );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MaratonActivity" ).child ( "loadMaratonList" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
        }

    }

    private void startSearch(String text_search) {
        try {
            Query query = FirebaseDatabase.getInstance ().getReference ()
                    .child ( "Carreras" )
                    .child ( "Nuevas" )
                    .orderByChild ( "maratonname" )
                    .startAt ( text_search ).limitToFirst ( 2 );
            FirebaseRecyclerOptions<Maraton> options = new FirebaseRecyclerOptions.Builder<Maraton> ()
                    .setQuery ( query, Maraton.class )
                    .build ();

            searchAdapter = new FirebaseRecyclerAdapter<Maraton, MaratonViewHolder> ( options ) {
                @Override
                protected void onBindViewHolder(@NonNull MaratonViewHolder maratonViewHolder, int position, @NonNull Maraton maraton) {

                    maratones.add ( maraton );
                    maratonViewHolder.maratonname.setText ( maraton.maratonname );
                    Picasso.with ( getApplication () ).load ( maraton.maratonimage ).into ( maratonViewHolder.maratonimage );
                    maratonViewHolder.maratondescription.setText ( maraton.description );
                    maratonViewHolder.maratonplace.setText ( maraton.place );
                    maratonViewHolder.maratondate.setText ( maraton.maratondate );
                    maratonViewHolder.maratontime.setText ( maraton.maratontime );
                    final String PostKey = getRef ( position ).getKey ();
                    maratonViewHolder.setiRecyclerItemClickListener ( new IRecyclerItemClickListener () {
                        @Override
                        public void onItemClickListener(View view, int position) {
                            Intent clickPostIntent = new Intent ( MaratonActivity.this, ClickMaratonActivity.class );
                            clickPostIntent.putExtra ( "PostKey", PostKey );
                            startActivity ( clickPostIntent );
                        }
                    } );

                }

                @NonNull
                @Override
                public MaratonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View itemView = LayoutInflater.from ( parent.getContext () )
                            .inflate ( R.layout.all_carreras_layout, parent, false );
                    return new MaratonViewHolder ( itemView );
                }
            };
            searchAdapter.startListening ();
            recycler_all_maraton.setAdapter ( searchAdapter );
        } catch (Exception e) {
            HashMap error = new HashMap ();
            error.put ( "error", e.getMessage () );
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MaratonActivity" ).child ( "startSearch" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
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
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MaratonActivity" ).child ( "onStop" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
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
            FirebaseDatabase.getInstance ().getReference ().child ( "Error" ).child ( "MaratonActivity" ).child ( "onResume" ).child ( Common.loggedUser.getUid () ).updateChildren ( error );
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
