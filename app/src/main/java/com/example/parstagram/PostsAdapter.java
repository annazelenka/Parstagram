package com.example.parstagram;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

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
        TextView tvNumLikes;
        ImageView ivImage;
        ImageView ivProfilePic;
        ImageButton btnLike;

        boolean currentUserLikedPost;
        ParseUser currentUser;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimestamp = itemView.findViewById(R.id.tvTimeStamp);
            tvUsername2 = itemView.findViewById(R.id.tvUsername2);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            btnLike = itemView.findViewById(R.id.btnLike);

            itemView.setOnClickListener(this);
            currentUserLikedPost = false;

            currentUser = ParseUser.getCurrentUser();
        }

        public void bind(final Post post) {
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

            updateHeartIcon(post);
            updateNumLikes(post);

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    post.updatePostLikesInDatabase(currentUser, currentUserLikedPost);

                    if (!currentUserLikedPost) {
                        // credit to https://stackoverflow.com/questions/2270751/android-imageview-onclick-animation
                        btnLike.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));
                        currentUserLikedPost = true;
                    } else {
                        currentUserLikedPost = false;
                    }


                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e("ComposeFragment", "Error while saving", e);
                                Toast.makeText(context, "error while saving!", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(context, "Liked!", Toast.LENGTH_LONG).show();
                        }
                    });
                    updateHeartIcon(post);
                    updateNumLikes(post);
                }
            });


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

        private void updateHeartIcon(Post post) {
            String currentUsername = currentUser.getUsername();
            JSONArray usersThatLiked = post.getUsersThatLiked();

            boolean userAlreadyLiked = false;

            if (usersThatLiked != null) {
                for (int i = 0; i < usersThatLiked.length(); i++) {
                    String username = "";
                    try {
                        username = usersThatLiked.get(i).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("Post", "failed to retrieve username");
                    }
                    if (username.equals(currentUsername)) {
                        userAlreadyLiked = true;
                    }

                }
            }

            if (userAlreadyLiked) {
                btnLike.setImageResource(R.drawable.ufi_heart_active);
            } else {
                btnLike.setImageResource(R.drawable.ufi_heart);
            }
        }

        private void updateNumLikes(Post post) {
            int numLikes = post.getNumLikes();
            tvNumLikes.setText(String.valueOf(numLikes) + " likes");
        }




    }
}
