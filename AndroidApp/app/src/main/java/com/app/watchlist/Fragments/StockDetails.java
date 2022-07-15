package com.app.watchlist.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.watchlist.Api.ApiClient;
import com.app.watchlist.Api.ApiInterface;
import com.app.watchlist.Api.PojoClass.ChartModel;
import com.app.watchlist.Api.PojoClass.StockModel;
import com.app.watchlist.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockDetails extends Fragment {

    //ArrayList to parse data from api and for displaying chart
    ArrayList<StockModel.Quote> stockModels;
    List<PointValue> values;

    //Various layouts and views
    TextView txtStockSymbol,txtAskPrice,txtBidPrice,txtLastPrice;
    String symbol="";
    LineChartView chart;

    //Handler and runnable  to make api calls for a particular interval of time
    Runnable runnable;
    Handler handler;
    final int delay = 5000;

    //Api call interface for making the api calls using retrofit client
    ApiInterface apiService;

    public StockDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_stock_details, container, false);

        //Function to initialise all the necessary components for the fragments
        viewInitialisation(v);
        //Function to get data from shared preferences
        getData();
        //Function to set data on to the screen
        setData();

        return v;
    }

    public void viewInitialisation(View v){
        //initialising edittext  and charts
        txtStockSymbol = v.findViewById(R.id.txtStockSymbol);
        txtAskPrice = v.findViewById(R.id.txtAskPrice);
        txtBidPrice = v.findViewById(R.id.txtBidPrice);
        txtLastPrice = v.findViewById(R.id.txtLastPrice);
        chart = v.findViewById(R.id.chart);

        //initialising array list
        stockModels = new ArrayList<>();
        values = new ArrayList<>();

        //initialising retrofit client
        apiService = ApiClient.getClient().create(ApiInterface.class);

        //initialising handlers
        handler = new Handler();
    }

    public void getData(){
        //initialising bundle to get the data passed from fragments
        Bundle bundle = new Bundle();
        bundle = getArguments();
        if(bundle!=null)
            symbol = bundle.getString("symbol");
    }

    public void setData(){
        getStockData();
        //Refreshing the api data every 5 seconds to get the latest prices
        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                getStockData();
                handler.postDelayed(runnable, delay);
            }
        }, delay);

        //api call to get the stock history data for past 30 days based on the particular symbol
        getStockHistoryData();
    }

    //Retrofit Api call to get the stock data based on the symbols present in bundle
    public void getStockData(){
        Call<Map<String, StockModel>> call = apiService.getWatchListData("quote",symbol,"pk_656641decac14fe0b70bf4895fa1ab67");
        call.enqueue(new Callback<Map<String,StockModel>>() {
            @Override
            public void onResponse(Call<Map<String,StockModel>> call, Response<Map<String,StockModel>> response) {
                stockModels.clear();
                stockModels.add(response.body().get(symbol.toUpperCase()).getQuote());
                displayData();
            }

            @Override
            public void onFailure(Call<Map<String,StockModel>> call, Throwable t) {
                Log.d("Error","Response = "+t.toString());
            }
        });
    }

    //Retrofit Api call to get the stock history data based on the symbols present in bundle
    public void getStockHistoryData(){
        Call<Map<String, ChartModel>> call1 = apiService.getChartData("chart","1m",symbol,"pk_656641decac14fe0b70bf4895fa1ab67");
        call1.enqueue(new Callback<Map<String,ChartModel>>() {
            @Override
            public void onResponse(Call<Map<String,ChartModel>> call, Response<Map<String,ChartModel>> response) {
                try {
                    List<ChartModel.Chart> chartList = response.body().get(symbol).getChart();
                    for (int i = 0; i < chartList.size(); i++) {
                        values.add(new PointValue(i, chartList.get(i).getHigh()));
                    }
                    displayChart();
                }
                catch (Exception e){
                    Log.d("Error",e.getLocalizedMessage());
                }
            }
            @Override
            public void onFailure(Call<Map<String,ChartModel>> call, Throwable t) {

            }
        });
    }

    //Setting the data on screen
    public void displayData(){
        txtStockSymbol.setText(stockModels.get(0).getSymbol());
        String temp = "$"+stockModels.get(0).getIexAskPrice();
        txtAskPrice.setText(temp);
        temp = "$"+stockModels.get(0).getIexBidPrice();
        txtBidPrice.setText(temp);
        temp = "$"+stockModels.get(0).getIexRealtimePrice();
        txtLastPrice.setText(temp);
    }

    //displaying a line chart with high values returned by the api
    public void displayChart(){
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Date");
        axisY.setName("Price ($)");
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        chart.setLineChartData(data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //removing runnable call backs
        handler.removeCallbacks(runnable);
        requireActivity().recreate();

    }
}