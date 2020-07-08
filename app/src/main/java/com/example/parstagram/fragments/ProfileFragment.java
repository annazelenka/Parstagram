package com.example.parstagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.parstagram.LoginActivity;
import com.example.parstagram.MainActivity;
import com.example.parstagram.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends PostsFragment {

    @Override
    protected void handleProfileSetup(View view) {
        btnLogout.setVisibility(View.VISIBLE);
        ivProfilePic.setVisibility(View.VISIBLE);
        tvUsername.setVisibility(View.VISIBLE);
        btnChangeProfilePic.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);

        ParseUser user = ParseUser.getCurrentUser();
        tvUsername.setText(user.getUsername());
        ParseFile profilePic = (ParseFile) user.get("profilePic");

        int profileImageRadius = 80;

        if (profilePic != null) {
            Glide.with(this)
                    .load(profilePic.getUrl())
                    .transform(new RoundedCorners(profileImageRadius))
                    .into(ivProfilePic);
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), LoginActivity.class);
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                startActivity(i);
                getActivity().finish();
            }
        });

        btnChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: implement the ability to change profile pic
            }
        });
    }

    @Override
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.addDescendingOrder(Post.KEY_CREATED_KEY);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
