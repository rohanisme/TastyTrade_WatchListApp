package com.app.watchlist.Fragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.app.watchlist.Adapter.SearchAdapter;
import com.app.watchlist.Adapter.StockAdapter;
import com.app.watchlist.Api.ApiClient;
import com.app.watchlist.Api.ApiClientSearch;
import com.app.watchlist.Api.ApiInterface;
import com.app.watchlist.Api.PojoClass.SearchModel;
import com.app.watchlist.Api.PojoClass.StockModel;
import com.app.watchlist.Configuration.SharedPreferences;
import com.app.watchlist.R;
import java.util.ArrayList;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Watchlist extends Fragment {

    //Various layouts and recyclerview
    RecyclerView recyclerView;
    EditText edtSearch;

    //Shared preferences to get the locally saved data
    SharedPreferences sharedPreferences;

    //Recycler view adapter to display the stock data and also to search the stocks
    StockAdapter stockAdapter;
    SearchAdapter searchAdapter;


    //ArrayList to parse data from api and shared preferences
    ArrayList<StockModel.Quote> stockModels;
    ArrayList<String> symbols;
    ArrayList<SearchModel.SearchData> searchData;

    //Global Variables
    String watchlistName="",symbolsList="";

    //Handler and runnable  to make api calls for  a particular interval of time
    Runnable runnable;
    Handler handler;
    final int delay = 5000;

    //Api call interface for making the api calls using retrofit client
    ApiInterface apiService;


    public Watchlist() {
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
        View v=inflater.inflate(R.layout.fragment_watchlist, container, false);

        //Function to initialise all the necessary components for the fragments
        viewInitialisation(v);
        //Function to get data from shared preferences
        getData();
        //Function to set data on to the screen
        setData();
        //Function to handle all button clicks and text change listeners
        onClicks();


        return v;
    }

    public void viewInitialisation(View v){
        //initialising edittext and recyclerview
        edtSearch = v.findViewById(R.id.edtSearch);
        recyclerView = v.findViewById(R.id.recyclerView);

        //initialising recyclerview with linear layout for display of data in vertical orientation
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //initialising array list
        stockModels = new ArrayList<>();
        symbols = new ArrayList<>();
        searchData = new ArrayList<>();

        //initialising the stock adapter with empty data
        stockAdapter = new StockAdapter(getActivity(),stockModels);
        recyclerView.setAdapter(stockAdapter);

        //initialising the search adapter with empty data
        searchAdapter = new SearchAdapter(getActivity(), searchData);

        //initialising shared preferences to get local saved watchlist and symbols
        sharedPreferences = new SharedPreferences(getActivity());

        //initialising handlers
        handler = new Handler();

        //initialising retrofit client
        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    public void getData(){
        //initialising bundle to get the data passed from fragments
        Bundle bundle = new Bundle();
        bundle = getArguments();

        //Getting the watchlist name from the previous fragment
        if(bundle!=null)
            watchlistName = bundle.getString("watchlistName");

        symbols = sharedPreferences.getItems(watchlistName);

        symbolsList = "";
        //Converting the symbols from arraylist to string to pass it to the api for responses
        if(symbols!=null)
            for(int i=0;i<symbols.size();i++){
                symbolsList = symbolsList + symbols.get(i)+",";
            }
    }
    
    public void setData(){
        //Getting stock data from api and display it on screen
        getStockData();
        //Refreshing the api data every 5 seconds to get the latest prices
        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                getStockData();
                handler.postDelayed(runnable, delay);
            }
        }, delay);
    }

    public void onClicks(){
        searchAdapter.setOnItemClickListener(new SearchAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                symbols = sharedPreferences.getItems(watchlistName);
                if(symbols!=null)
                    symbols.add(searchData.get(position).getSymbol());
                else{
                    symbols = new ArrayList<>();
                    symbols.add(searchData.get(position).getSymbol());
                }
                sharedPreferences.saveItems(watchlistName,symbols);

                //Converting the symbols from arraylist to string to pass it to the api for responses
                symbolsList = "";
                if(symbols!=null)
                    for(int i=0;i<symbols.size();i++){
                        symbolsList = symbolsList + symbols.get(i)+",";
                    }

                hideKeyboard(requireActivity());
                setData();
                edtSearch.setText("");
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence.toString())){
                    if(charSequence.toString().length()<=1){
                        searchData.clear();
                        stockModels.clear();
                        symbols = sharedPreferences.getItems(watchlistName);

                        symbolsList = "";
                        if(symbols!=null)
                            for(int j=0;j<symbols.size();j++){
                                symbolsList = symbolsList + symbols.get(j)+",";
                            }
                       setData();
                    }
                    else {
                        //removing runnable call backs
                        handler.removeCallbacks(runnable);
                        getSearchData();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //Retrofit Api call to get the stock data based on the symbols present in wishlist
    public void getStockData(){
        Call<Map<String,StockModel>> call = apiService.getWatchListData("quote",symbolsList,"pk_656641decac14fe0b70bf4895fa1ab67");
        call.enqueue(new Callback<Map<String,StockModel>>() {
            @Override
            public void onResponse(Call<Map<String,StockModel>> call, Response<Map<String,StockModel>> response) {
                try {
                    stockModels.clear();
                    if (symbols != null)
                        for (int i = 0; i < symbols.size(); i++)
                            stockModels.add(response.body().get(symbols.get(i).toUpperCase()).getQuote());
                    stockAdapter = new StockAdapter(getActivity(),stockModels);
                    displayData();
                }
                catch (Exception e){
                    Log.d("Error",e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<Map<String,StockModel>> call, Throwable t) {
                Log.d("Error","Response = "+t.toString());
            }
        });
    }

    //Displaying the stock data in recylerview
    public void displayData(){
        recyclerView.setAdapter(stockAdapter);
    }

    //Api call to get the search data based on the text entered in the search field
    public void getSearchData(){
        ApiInterface apiService = ApiClientSearch.getClient().create(ApiInterface.class);
        Call<Map<String, SearchModel>> call = apiService.getSearchData(edtSearch.getText().toString());
        call.enqueue(new Callback<Map<String, SearchModel>>() {
            @Override
            public void onResponse(Call<Map<String, SearchModel>> call, Response<Map<String, SearchModel>> response) {
                try {
                    searchData = response.body().get("data").getSearchData();
                    searchAdapter = new SearchAdapter(getActivity(), searchData);
                    recyclerView.setAdapter(searchAdapter);
                } catch (Exception e) {
                    Log.d("Error", e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<Map<String, SearchModel>> call, Throwable t) {

            }
        });
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        //removing runnable call backs
        handler.removeCallbacks(runnable);
    }



    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}