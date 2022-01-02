package com.example.saludable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class AyudaActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    CarouselView carouselView;
    int[] sampleImages = {R.drawable.ayuda1, R.drawable.ayuda2, R.drawable.ayuda3, R.drawable.ayuda4, R.drawable.ayuda5, R.drawable.ayuda6,R.drawable.ayuda7,R.drawable.ayuda8};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        mToolbar = findViewById ( R.id.ayuda_toolbar );
        setSupportActionBar ( mToolbar );
        getSupportActionBar ().setTitle ( "Ayuda" );
        getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );
        carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);
    }
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };
}