package com.example.saludable.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saludable.ClickMaratonActivity;
import com.example.saludable.ClickMiMaratonActivity;
import com.example.saludable.Interfaces.IRecyclerItemClickListener;
import com.example.saludable.Model.Maraton;
import com.example.saludable.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class MaratonAdapter extends RecyclerView.Adapter<MaratonAdapter.MyViewHolder> {
    Context ctx;
    String activity;
    private ArrayList<Maraton> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MaratonAdapter(ArrayList<Maraton> myDataset, Context ctxe, String act) {
        mDataset = myDataset;
        ctx = ctxe;
        activity = act;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MaratonAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from ( parent.getContext () )
                .inflate ( R.layout.all_mi_carreras_layout, parent, false );
        return new MaratonAdapter.MyViewHolder ( itemView );
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder maratonViewHolder, int position) {
        maratonViewHolder.maratonname.setText ( mDataset.get ( position ).maratonname );
        maratonViewHolder.maratondate.setText ( mDataset.get ( position ).maratondate );
        if (mDataset.get ( position ).image != null) {
            File img = new File ( mDataset.get ( position ).image );
            Picasso.with ( ctx ).load ( "file://" + img ).into ( maratonViewHolder.maratonimage );
        } else {
            Picasso.with ( ctx ).load ( mDataset.get ( position ).maratonimage ).into ( maratonViewHolder.maratonimage );
        }
        maratonViewHolder.maratondescription.setText ( mDataset.get ( position ).description );
        final String PostKey = mDataset.get ( position ).uid;
        maratonViewHolder.setiRecyclerItemClickListener ( new IRecyclerItemClickListener () {
            @Override
            public void onItemClickListener(View view, int position) {
                if (activity.equals ( "ins" )) {
                    Intent clickPostIntent = new Intent ( view.getContext (), ClickMaratonActivity.class );
                    clickPostIntent.putExtra ( "PostKey", PostKey );
                    view.getContext ().startActivity ( clickPostIntent );
                } else if (activity.equals ( "fin" )) {
                    Intent clickPostIntent = new Intent ( view.getContext (), ClickMiMaratonActivity.class );
                    clickPostIntent.putExtra ( "PostKey", PostKey );
                    view.getContext ().startActivity ( clickPostIntent );
                }

            }
        } );

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset == null) {
            return 0;
        } else {
            return mDataset.size ();
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView username, maratontime, maratondate, maratonplace, maratonname, admindate, admintime, maratondescription;
        public ImageView maratonimage;
        IRecyclerItemClickListener iRecyclerItemClickListener;

        public MyViewHolder(@NonNull View itemView) {

            super ( itemView );
            maratondate = itemView.findViewById ( R.id.my_maraton_date );
            maratonname = itemView.findViewById ( R.id.my_maraton_name );
            maratonimage = itemView.findViewById ( R.id.my_maraton_image );
            maratondescription = itemView.findViewById ( R.id.my_maraton_description );
            itemView.setOnClickListener ( this );
        }

        public void setiRecyclerItemClickListener(IRecyclerItemClickListener iRecyclerItemClickListener) {
            this.iRecyclerItemClickListener = iRecyclerItemClickListener;
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemClickListener.onItemClickListener ( view, getAdapterPosition () );
        }


    }
}