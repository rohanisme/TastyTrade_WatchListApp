package com.app.watchlist.Api.PojoClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class SearchModel {

    @SerializedName("items")
    @Expose
    private ArrayList<SearchData> searchData = null;

    public ArrayList<SearchData> getSearchData() {
        return searchData;
    }

    public class SearchData {
        @SerializedName("symbol")
        @Expose
        private String symbol;
        @SerializedName("description")
        @Expose
        private String description;

        public String getSymbol() {
            return symbol;
        }

        public String getDescription() {
            return description;
        }
    }
}
;