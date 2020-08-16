package com.example.saludable.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saludable.Interfaces.IRecyclerItemClickListener;
import com.example.saludable.R;

public class MaratonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView username, contactname, contactnumber, maratontime, maratondate, maratonplace, maratonname, admindate, admintime, maratondescription;
    public ImageView maratonimage;
    IRecyclerItemClickListener iRecyclerItemClickListener;

    public MaratonViewHolder(@NonNull View itemView) {

        super ( itemView );
        username = itemView.findViewById ( R.id.maraton_description );
        maratontime = itemView.findViewById ( R.id.maraton_time );
        maratondate = itemView.findViewById ( R.id.maraton_date );
        maratonplace = itemView.findViewById ( R.id.maraton_place );
        maratonname = itemView.findViewById ( R.id.maraton_name );
        maratonimage = itemView.findViewById ( R.id.click_maraton_image );
        maratondescription = itemView.findViewById ( R.id.maraton_description );
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
