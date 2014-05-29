package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 */
public class Paginate<T> {

    @SerializedName("lists")
    public List<T> lists;

    @SerializedName("splitPage")
    public Page page;

    public static class Page {
        @SerializedName("countPage")
        public int countPage;

        @SerializedName("countRec")
        public int countRec;

        @SerializedName("currPage")
        public int currPage;

        @SerializedName("endRecord")
        public int endRecord;

        @SerializedName("pageSize")
        public int pageSize;

        @SerializedName("startRecord")
        public int startRecord;
    }
}
