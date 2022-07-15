package com.app.watchlist.Fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class AddWatchlist extends Fragment {

    //Various layouts and views
    EditText edtWatchlistName,edtSearch;
    RecyclerView recyclerView;
    Button btnAdd,btnDelete;

    //Shared preferences to get the locally saved data
    SharedPreferences sharedPreferences;

    //Global variables
    String watchlistName="";
    String symbolsList="";

    //Recycler view adapter to display the stock data and also to search the stocks
    StockAdapter stockAdapter;
    SearchAdapter searchAdapter;

    //ArrayList to parse data from api and shared preferences
    ArrayList<String> watchlist;
    ArrayList<String> symbols;
    ArrayList<StockModel.Quote> stockModels;
    ArrayList<SearchModel.SearchData> searchData;

    //Handler and runnable  to make api calls for  a particular interval of time
    Runnable runnable;
    Handler handler;
    final int delay = 5000;

    //Api call interface for making the api calls using retrofit client
    ApiInterface apiService;

    public AddWatchlist() {
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
        View v=inflater.inflate(R.layout.fragment_add_watchlist_, container, false);

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
        //initialising edittext, buttons and recyclerview
        edtWatchlistName = v.findViewById(R.id.edtWatchlistName);
        edtSearch = v.findViewById(R.id.edtSearch);
        recyclerView = v.findViewById(R.id.recyclerView);
        btnAdd = v.findViewById(R.id.btnAdd);
        btnDelete = v.findViewById(R.id.btnDelete);

        //initialising array list
        stockModels = new ArrayList<>();
        searchData = new ArrayList<>();
        watchlist = new ArrayList<>();
        symbols = new ArrayList<>();

        //initialising the stock adapter with empty data
        stockAdapter = new StockAdapter(getActivity(),stockModels);
        recyclerView.setAdapter(stockAdapter);

        //initialising the search adapter with empty data
        searchAdapter = new SearchAdapter(getActivity(), searchData);

        //initialising shared preferences to get local saved watchlist and symbols
        sharedPreferences = new SharedPreferences(getActivity());

        //initialising recyclerview with linear layout for display of data in vertical orientation
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

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
        if (bundle != null) {
            watchlistName = bundle.getString("watchlist");
            edtWatchlistName.setText(watchlistName);
            symbols = sharedPreferences.getItems(watchlistName);
            //Updating and setting views for updating a watching
            btnAdd.setText("Update Watchlist");
            btnDelete.setVisibility(View.VISIBLE);
        }

        symbolsList = "";
        //Converting the symbols from arraylist to string to pass it to the api for responses
        if(symbols!=null)
            for(int i=0;i<symbols.size();i++){
                symbolsList = symbolsList + symbols.get(i)+",";
            }

        //Getting the complete list of watchlist names saved in shared preferences
        watchlist = sharedPreferences.getItems("watchlist");
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
    }

    public void onClicks(){
        //Button add to save data to shared preferences and create respective watchlist with the symbols
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edtWatchlistName.getText().toString())){
                    edtWatchlistName.setError("Enter Watch List Name");
                    edtWatchlistName.requestFocus();
                    return;
                }

                if(watchlist.contains(edtWatchlistName.getText().toString())&&TextUtils.isEmpty(watchlistName)){
                    edtWatchlistName.setError("WatchList Exist");
                    edtWatchlistName.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(watchlistName))
                    watchlist.add(edtWatchlistName.getText().toString());
                else{
                    sharedPreferences.deleteItems(watchlistName);
                    int i = watchlist.indexOf(watchlistName);
                    watchlist.set(i,edtWatchlistName.getText().toString());
                }

                sharedPreferences.saveItems("watchlist",watchlist);
                sharedPreferences.saveItems(edtWatchlistName.getText().toString(),symbols);
                sharedPreferences.deleteItems("Dummy");
                requireActivity().onBackPressed();

            }
        });

        //Button delete to delete all symbols data from a watchlist along with the watchlist name from the shared preferences
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.deleteItems(watchlistName);
                watchlist.remove(watchlistName);
                sharedPreferences.saveItems("watchlist",watchlist);
                sharedPreferences.deleteItems("Dummy");
                requireActivity().onBackPressed();
            }
        });

        //Search text change listener to query data from search api and show auto complete results
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
                        if(!TextUtils.isEmpty(watchlistName))
                            symbols = sharedPreferences.getItems(watchlistName);
                        else
                            symbols = sharedPreferences.getItems("Dummy");

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

        //Search Adapter row on click to add the symbols to the watchlist and save to shared preferences
        searchAdapter.setOnItemClickListener(new SearchAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                if(TextUtils.isEmpty(watchlistName)){
                    symbols = sharedPreferences.getItems("Dummy");
                    if(symbols!=null)
                        symbols.add(searchData.get(position).getSymbol());
                    else{
                        symbols = new ArrayList<>();
                        symbols.add(searchData.get(position).getSymbol());
                    }
                    sharedPreferences.saveItems("Dummy",symbols);
                }
                else{
                    symbols = sharedPreferences.getItems(watchlistName);
                    if(symbols!=null)
                        symbols.add(searchData.get(position).getSymbol());
                    else{
                        symbols = new ArrayList<>();
                        symbols.add(searchData.get(position).getSymbol());
                    }
                    sharedPreferences.saveItems(watchlistName,symbols);
                }

                symbolsList = "";
                //Converting the symbols from arraylist to string to pass it to the api for responses
                for(int i=0;i<symbols.size();i++){
                    symbolsList = symbolsList + symbols.get(i)+",";
                }

                hideKeyboard(requireActivity());
                setData();
                edtSearch.setText("");

            }
        });

        //Swipe to delete a symbol from the watchlist
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                stockModels.remove(viewHolder.getAdapterPosition());
                symbols.remove(viewHolder.getAdapterPosition());
                stockAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    //Retrofit Api call to get the stock data based on the symbols present in wishlist
    public void getStockData(){
        Call<Map<String,StockModel>> call = apiService.getWatchListData("quote",symbolsList,"pk_656641decac14fe0b70bf4895fa1ab67");
        call.enqueue(new Callback<Map<String,StockModel>>() {
            @Override
            public void onResponse(Call<Map<String,StockModel>> call, Response<Map<String,StockModel>> response) {
                stockModels.clear();
                if(symbols!=null)
                    for(int i=0;i<symbols.size();i++)
                        stockModels.add(response.body().get(symbols.get(i).toUpperCase()).getQuote());
                stockAdapter = new StockAdapter(getActivity(),stockModels);
                displayData();
            }
            @Override
            public void onFailure(Call<Map<String,StockModel>> call, Throwable t) {
                Log.d("Error","Response = "+t.toString());
            }
        });
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

    //Displaying the stock data in recylerview
    public void displayData(){
        recyclerView.setAdapter(stockAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //removing runnable call backs
        handler.removeCallbacks(runnable);
        sharedPreferences.deleteItems("Dummy");
        requireActivity().recreate();
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