package com.yujunchen.stockwatch;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "CountryAdapter";
    private List<Stock> stockList;
    private MainActivity mainAct;

    StockAdapter(List<Stock> empList, MainActivity ma) {
        this.stockList = empList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_entry, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.symbol.setText(stock.getSymbol());
        holder.name.setText(stock.getName());
        holder.price.setText(String.valueOf(stock.getLatestPrice()));
        if(stock.getChange() > 0) {
            holder.change.setText(String.format("▲ %.2f ", stock.getChange()) + "(" + String.format("%.2f", stock.getChangePercentage()) + "%)");
            holder.symbol.setTextColor(Color.GREEN);
            holder.name.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.change.setTextColor(Color.GREEN);
        }else if(stock.getChange() < 0){
            holder.change.setText(String.format("▼ %.2f ", stock.getChange()) + "(" + String.format("%.2f", stock.getChangePercentage()) + "%)");
            holder.symbol.setTextColor(Color.RED);
            holder.name.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.change.setTextColor(Color.RED);
        } else {
            holder.change.setText(String.format("%.2f ", stock.getChange()) + "(" + String.format("%.2f", stock.getChangePercentage()) + "%)");
            holder.symbol.setTextColor(Color.WHITE);
            holder.name.setTextColor(Color.WHITE);
            holder.price.setTextColor(Color.WHITE);
            holder.change.setTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

}
