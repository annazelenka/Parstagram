package com.example.parstagram;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//responsible for displaying data from the model into a row in the recycler view
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    List<String> comments;
    Context context;

    public CommentsAdapter(Context context, List<String> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    //creates each view
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Use layout inflater to inflate a view
        //View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent,false);

        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    //take date at position and puts it into view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Grab item at position
        String item = comments.get(position);
        //Bind item into specified View Holder
        holder.bind(item);
    }

    //Tells the RecyclerView how many items are in the list
    @Override
    public int getItemCount() {
        return comments.size();
    }

    //Container to provide easy access to views that represent each row in the list
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentUser;
        TextView tvComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCommentUser = itemView.findViewById(R.id.tvCommentUser);
            tvComment = itemView.findViewById(R.id.tvComment);
        }

        //Update the view inside the View Holder with this data
        public void bind(String item) {
            int spaceIndex = item.indexOf(' ');
            tvCommentUser.setText(item.substring(0,spaceIndex));
            tvComment.setText(item.substring(spaceIndex+1));

        }
    }
}