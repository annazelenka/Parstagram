package com.example.parstagram;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@ParseClassName("Post") // name needs to match what you named it in Parse Dashboard
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_KEY = "createdAt";
    private static final String KEY_PROFILE_PIC = "profilePic";
    private static final String KEY_NUM_LIKES = "numLikes";
    private static final String KEY_USERS_LIKED = "usersThatLiked";



    public Post() {
    } // empty constructor needed by Parcelable library


    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseFile getProfilePic() { return getUser().getParseFile(KEY_PROFILE_PIC); }

    public void setProfilePic(ParseFile profilePic) { getUser().put(KEY_PROFILE_PIC, profilePic); }



    public String getFormattedTimestamp() {
        Format formatter = new SimpleDateFormat("h:mm a, MMMM d, yyyy");
        String strDate = formatter.format(getCreatedAt());
        return strDate;
    }



    public void updatePostLikesInDatabase(ParseUser currentUser, boolean userAlreadyLiked) {
        String currentUsername = currentUser.getUsername();

        if (!userAlreadyLiked) {
            add(KEY_USERS_LIKED, currentUsername);
            increment(KEY_NUM_LIKES);
            Log.i("Post", "blah");
        } else {
            increment(KEY_NUM_LIKES, -1);
            removeAll(KEY_USERS_LIKED, Arrays.asList(currentUsername));
        }

    }

    public JSONArray getUsersThatLiked() {
        //return getParseObject(KEY_USERS_LIKED);
        return getJSONArray("usersThatLiked");

    }


    public int  getNumLikes() {
        return getInt(KEY_NUM_LIKES);
    }

}
