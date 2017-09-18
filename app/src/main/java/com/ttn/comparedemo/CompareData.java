package com.ttn.comparedemo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CompareData {
    @SerializedName("data")
    public Data data;

    public class Data {
        @SerializedName("searchResponse")
        public ArrayList<SearchResponse> searchResponses;
    }

    public class SearchResponse {
        @SerializedName("name")
        public String name;
    }
}
