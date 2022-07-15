package com.app.watchlist.Api;

import com.app.watchlist.Api.PojoClass.ChartModel;
import com.app.watchlist.Api.PojoClass.SearchModel;
import com.app.watchlist.Api.PojoClass.StockModel;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("stock/market/batch")
    Call<Map<String,StockModel>> getWatchListData(@Query("types") String types, @Query("symbols") String symbols, @Query("token") String token);

    @GET("stock/market/batch")
    Call<Map<String,ChartModel>> getChartData(@Query("types") String types, @Query("range") String range,@Query("symbols") String symbols, @Query("token") String token);

    @GET("symbols/search/{symbol}")
    Call<Map<String, SearchModel>> getSearchData(@Path("symbol") String symbol);

}
