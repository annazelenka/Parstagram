package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcel;
import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class PostDetailsActivity extends AppCompatActivity {

    TextView tvUsername;
    TextView tvDescription;
    TextView tvTimestamp;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        ivImage = findViewById(R.id.ivImage);

        //Post post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));
        tvUsername.setText(getIntent().getStringExtra("postUsername"));
        tvDescription.setText(getIntent().getStringExtra("postDescription"));
        tvTimestamp.setText(getIntent().getStringExtra("postTimestamp"));
        int radius = 50;
        int margin = 50;

        String imageUrl = getIntent().getStringExtra("postImageUrl");


        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    //.transform(new RoundedCornersTransformation(radius, margin))
                    .into(ivImage);
        }

    }
}