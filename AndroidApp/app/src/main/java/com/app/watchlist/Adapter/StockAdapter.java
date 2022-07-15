package com.app.watchlist.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.watchlist.Api.PojoClass.StockModel;
import com.app.watchlist.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.MyviewHolder> {

    Context context;
    ArrayList<StockModel.Quote> stockData;

    public StockAdapter(Context context, ArrayList<StockModel.Quote> stockData) {
        this.context = context;
        this.stockData = stockData;
    }

    @Override
    public StockAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stock_row,parent,false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(StockAdapter.MyviewHolder holder, int position) {
        
        holder.txtSymbol.setText(stockData.get(position).getSymbol());
        String askPrice = "$"+stockData.get(position).getIexAskPrice();
        String bidPrice = "$"+stockData.get(position).getIexAskPrice();
        String lastPrice = "$"+stockData.get(position).getIexBidPrice();
        holder.txtAskPrice.setText(askPrice);
        holder.txtBidPrice.setText(bidPrice);
        holder.txtLastPrice.setText(lastPrice);

        if(position %2 == 1)
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        else
            holder.itemView.setBackgroundColor(Color.parseColor("#F4F4F4"));


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                FrameLayout frameLayout = activity.findViewById(R.id.frame_container);
                frameLayout.setVisibility(View.VISIBLE);
                FloatingActionButton floatingActionButton = activity.findViewById(R.id.floatingAddWatchlist);
                floatingActionButton.setVisibility(View.GONE);
                LinearLayout linearLayout = activity.findViewById(R.id.linearLayout);
                linearLayout.setVisibility(View.GONE);
                Fragment fragment = new com.app.watchlist.Fragments.StockDetails();
                Bundle bundle = new Bundle();
                bundle.putString("symbol",holder.txtSymbol.getText().toString());
                fragment.setArguments(bundle);
                FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frame_container, fragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(stockData != null){
            return stockData.size();
        }
        return 0;

    }

    public static class MyviewHolder extends RecyclerView.ViewHolder {
        TextView txtSymbol,txtAskPrice,txtBidPrice,txtLastPrice;
        LinearLayout linearLayout;

        public MyviewHolder(View itemView) {
            super(itemView);
            txtSymbol = (TextView)itemView.findViewById(R.id.txtSymbol);
            txtAskPrice = (TextView)itemView.findViewById(R.id.txtAskPrice);
            txtBidPrice = (TextView)itemView.findViewById(R.id.txtBidPrice);
            txtLastPrice = (TextView)itemView.findViewById(R.id.txtLastPrice);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }
    }
}
