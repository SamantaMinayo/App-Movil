package com.example.saludable.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saludable.Interfaces.IRecyclerItemClickListener;
import com.example.saludable.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView postdate, posttime, postdescript, postadname;

    public CircleImageView postimgprof;
    public ImageView postimage;

    IRecyclerItemClickListener iRecyclerItemClickListener;

    public PostViewHolder(@NonNull View itemView) {

        super ( itemView );
        posttime = itemView.findViewById ( R.id.post_time );
        postadname = itemView.findViewById ( R.id.post_profile_name );
        postdate = itemView.findViewById ( R.id.post_date );
        postimage = itemView.findViewById ( R.id.post_img );
        postdescript = itemView.findViewById ( R.id.post_descrpt );
        postimgprof = itemView.findViewById ( R.id.post_profile_image );
    }

    public void setiRecyclerItemClickListener(IRecyclerItemClickListener iRecyclerItemClickListener) {
        this.iRecyclerItemClickListener = iRecyclerItemClickListener;
    }
}
