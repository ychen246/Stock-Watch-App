package com.yujunchen.stockwatch;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView symbol;
        TextView name;
        TextView price;
        TextView change;

        MyViewHolder(View view) {
            super(view);
            symbol = view.findViewById(R.id.symbolText);
            name = view.findViewById(R.id.nameText);
            price = view.findViewById(R.id.priceText);
            change = view.findViewById(R.id.changeText);
        }

}
