package com.app.watchlist.Api.PojoClass;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChartModel {

    @SerializedName("chart")
    @Expose
    private List<Chart> chart = null;

    public List<Chart> getChart() {
        return chart;
    }

    public class Chart {
        @SerializedName("close")
        @Expose
        private Float close;
        @SerializedName("high")
        @Expose
        private Float high;
        @SerializedName("low")
        @Expose
        private Float low;
        @SerializedName("open")

        public Float getHigh() {
            return high;
        }

    }
}
