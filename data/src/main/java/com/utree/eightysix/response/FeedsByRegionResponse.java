package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.FeedsByRegion;
import com.utree.eightysix.rest.Response;

/**
 */
public class FeedsByRegionResponse extends Response {

    @SerializedName("object")
    public FeedsByRegion object;

    @SerializedName("extra")
    public Extra extra;

    public static class Extra {
        @SerializedName("signConsecutiveTimes")
        public int signConsecutiveTimes;

        @SerializedName("signMissingTimes")
        public int signMissingTimes;

        @SerializedName("signed")
        public int signed;
    }
}
