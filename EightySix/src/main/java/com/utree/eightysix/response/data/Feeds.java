package com.utree.eightysix.response.data;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.response.Paginate;

/**
*/
public class Feeds {
    @SerializedName("friendAnonymousPostCount")
    public int friendsPosts;

    @SerializedName("myPraiseCount")
    public int myPraiseCount;

    @SerializedName("posts")
    public Paginate<Post> posts;
}
