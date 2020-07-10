package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class PostDetailsActivity extends AppCompatActivity {
    public static final int POST_DETAIL_RESULT = 19;

    TextView tvUsername;
    TextView tvDescription;
    TextView tvTimestamp;
    TextView tvUsername2;
    TextView tvNumLikes;
    ImageView ivImage;
    ImageView ivProfilePic;
    ImageButton btnLike;
    ImageButton btnComment;
    RecyclerView rvComments;
    CommentsAdapter commentsAdapter;
    EditText etComment;
    Button btnPostComment;


    List<String> comments;
    boolean currentUserLikedPost;
    ParseUser currentUser;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvUsername = findViewById(R.id.tvUsername);
        ivImage = findViewById(R.id.ivImage);
        tvDescription = findViewById(R.id.tvDescription);
        tvTimestamp = findViewById(R.id.tvTimeStamp);
        tvUsername2 = findViewById(R.id.tvUsername2);
        tvNumLikes = findViewById(R.id.tvNumLikes);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        btnLike = findViewById(R.id.btnLike);
        btnComment = findViewById(R.id.btnComment);
        etComment = findViewById(R.id.etComment);
        btnPostComment = findViewById(R.id.btnPostComment);


        currentUserLikedPost = false;
        currentUser = ParseUser.getCurrentUser();
        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));
        setResult(POST_DETAIL_RESULT); // set result code and bundle data for response

        comments = new ArrayList<String>();
        loadComments();


        rvComments = findViewById(R.id.rvItems);
        commentsAdapter = new CommentsAdapter(PostDetailsActivity.this, comments);
        rvComments.setAdapter(commentsAdapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        populateViews();
    }

    public void populateViews() {

        tvDescription.setText(post.getDescription());
        tvUsername.setText(post.getUser().getUsername());
        tvTimestamp.setText(post.getFormattedTimestamp());
        tvUsername2.setText(post.getUser().getUsername());

        etComment.setVisibility(View.GONE);
        btnPostComment.setVisibility(View.GONE);

        int profileImageRadius = 120;

        if (post.getImage() != null) {
            Glide.with(this)
                    .load(post.getImage().getUrl())
                    .into(ivImage);
        }

        if (post.getProfilePic() != null) {
            Glide.with(this)
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
                    btnLike.startAnimation(AnimationUtils.loadAnimation(PostDetailsActivity.this, R.anim.image_click));
                    currentUserLikedPost = true;
                } else {
                    currentUserLikedPost = false;
                }


                savePost();
                updateHeartIcon(post);
                updateNumLikes(post);
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etComment.setVisibility(View.VISIBLE);
                btnPostComment.setVisibility(View.VISIBLE);
            }
        });

        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etComment.getText().toString();

                if (comment.isEmpty()) {
                    Toast.makeText(PostDetailsActivity.this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    comment = currentUser.getUsername() + " " + comment.trim();
                    post.addCommentToDatabase(comment);
                    // add comment to recycler view
                    //Add item to model
                    comments.add(comment);
                    //Notify Adapter that we've inserted an item
                    commentsAdapter.notifyItemInserted(comments.size() - 1);
                    etComment.setText("");
                    savePost();
                }
            }
        });

    }

    private void updateHeartIcon(Post post) {
        String currentUsername = currentUser.getUsername();
        JSONArray usersThatLiked = post.getUsersThatLiked();


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
                    currentUserLikedPost = true;
                }

            }
        }

        if (currentUserLikedPost) {
            btnLike.setImageResource(R.drawable.ufi_heart_active);
        } else {
            btnLike.setImageResource(R.drawable.ufi_heart);
        }
    }

    private void updateNumLikes(Post post) {
        int numLikes = post.getNumLikes();
        tvNumLikes.setText(String.valueOf(numLikes) + " likes");
    }

    private void loadComments() {
        comments = post.getParsedComments();
    }

    private void savePost() {
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("ComposeFragment", "Error while saving", e);
                    Toast.makeText(PostDetailsActivity.this, "error while saving!", Toast.LENGTH_SHORT).show();

                    return;
                }
                Toast.makeText(PostDetailsActivity.this, "saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

