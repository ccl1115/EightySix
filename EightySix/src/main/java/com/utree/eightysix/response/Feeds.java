package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;

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
