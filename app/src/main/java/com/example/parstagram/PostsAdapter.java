package com.example.parstagram;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    // Parcelable requires implementing a default constructor
    PostsAdapter() { }

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvUsername;
        TextView tvDescription;
        TextView tvTimestamp;
        TextView tvUsername2;
        ImageView ivImage;
        ImageView ivProfilePic;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimestamp = itemView.findViewById(R.id.tvTimeStamp);
            tvUsername2 = itemView.findViewById(R.id.tvUsername2);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);

            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            // Bind post data to view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvTimestamp.setText(post.getFormattedTimestamp());
            tvUsername2.setText(post.getUser().getUsername());

            int profileImageRadius = 120;

            if (post.getImage() != null) {
                Glide.with(context)
                        .load(post.getImage().getUrl())
                        .into(ivImage);
            }

            if (post.getProfilePic() != null) {
                Glide.with(context)
                        .load(post.getProfilePic().getUrl())
                        .transform(new RoundedCorners(profileImageRadius))
                        .into(ivProfilePic);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);

                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra("postDescription", post.getDescription());
                intent.putExtra("postUsername", post.getUser().getUsername());
                intent.putExtra("postImageUrl", post.getImage().getUrl());
                intent.putExtra("postTimestamp", post.getFormattedTimestamp());
                context.startActivity(intent);
            }

        }

    }
}
