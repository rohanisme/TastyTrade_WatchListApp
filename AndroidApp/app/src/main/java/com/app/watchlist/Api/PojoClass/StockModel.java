package com.app.watchlist.Api.PojoClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockModel {

    @SerializedName("quote")
    @Expose
    private Quote quote = null;

    public Quote getQuote() {
        return quote;
    }


    public class Quote {
        @SerializedName("symbol")
        public String symbol;

        @SerializedName("iexBidPrice")
        public double iexBidPrice;

        @SerializedName("iexAskPrice")
        public double iexAskPrice;

        @SerializedName("iexRealtimePrice")
        public double iexRealtimePrice;

        public String getSymbol() {
            return symbol;
        }

        public double getIexBidPrice() {
            return iexBidPrice;
        }

        public double getIexAskPrice() {
            return iexAskPrice;
        }

        public double getIexRealtimePrice() {
            return iexRealtimePrice;
        }
    }
}
