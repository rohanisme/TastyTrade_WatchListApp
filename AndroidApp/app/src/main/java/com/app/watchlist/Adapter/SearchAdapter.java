package com.app.watchlist.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.app.watchlist.Api.PojoClass.SearchModel;
import com.app.watchlist.R;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyviewHolder> {

    Context context;
    ArrayList<SearchModel.SearchData> searchData;
    private static ClickListener clickListener;

    public SearchAdapter(Context context, ArrayList<SearchModel.SearchData> searchData) {
        this.context = context;
        this.searchData = searchData;
    }

    @Override
    public SearchAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_row,parent,false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.MyviewHolder holder, int position) {
        
        holder.txtSymbol.setText(searchData.get(position).getSymbol());
        String description = "$" + searchData.get(position).getDescription();
        holder.txtDescription.setText(description);

        if(position %2 == 1)
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        else
            holder.itemView.setBackgroundColor(Color.parseColor("#F4F4F4"));

    }

    @Override
    public int getItemCount() {
        if(searchData != null){
            return searchData.size();
        }
        return 0;

    }

    public static class MyviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtSymbol,txtDescription;

        public MyviewHolder(View itemView) {
            super(itemView);
            txtSymbol = (TextView)itemView.findViewById(R.id.txtSymbol);
            txtDescription = (TextView)itemView.findViewById(R.id.txtDescription);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        SearchAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
