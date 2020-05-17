package com.example.saludable.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saludable.Interfaces.IRecyclerItemClickListener;
import com.example.saludable.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MiMaratonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView username, maratondate, maratonname, maratondescription;

    public CircleImageView maratonimage;

    IRecyclerItemClickListener iRecyclerItemClickListener;

    public MiMaratonViewHolder(@NonNull View itemView) {

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
