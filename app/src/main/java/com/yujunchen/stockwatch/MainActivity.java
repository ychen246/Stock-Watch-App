package com.yujunchen.stockwatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "Main Activity";

    private ArrayList<Stock> stockList = new ArrayList<>();

    HashMap sHash = new HashMap();

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;

    private StockAdapter mAdapter;

    private DatabaseHandler databaseHandler;

    private static final int NOT_FOUND = 1;
    private static final int NO_INTERNET = 2;
    private static final int DUPLICATE_STOCK = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);

        mAdapter = new StockAdapter(stockList, this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        new NameDownloader(this).execute();

        databaseHandler = new DatabaseHandler(this);
    }

    @Override
    protected void onResume() {
        //databaseHandler.dumpDbToLog();
        ArrayList<Stock> list = databaseHandler.loadStocks();

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }

        if(connected){
        Log.d(TAG, "onResume: " + list);
            stockList.clear();
            for (int i = 0; i < list.size(); i++) {
                Stock s = list.get(i);
                stockSearch(s.getSymbol());
            }
            Collections.sort(stockList);
        }else{
            stockList.addAll(list);
            Collections.sort(stockList);
            addDialogue(null, 2);
        }
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }

    private void doRefresh() {

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }

        if(connected) {
            ArrayList<Stock> list = databaseHandler.loadStocks();

            stockList.clear();

            for (int i = 0; i < list.size(); i++) {
                Stock s = list.get(i);
                stockSearch(s.getSymbol());
            } //Redownload all Stock info and repopulate with new info.

            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Stock Info Updated", Toast.LENGTH_SHORT).show();
        }else{
            addDialogue(null, 2);
        }
        swiper.setRefreshing(false);
    }

    public void nameDownload(HashMap hm) {
        sHash.putAll(hm);
        mAdapter.notifyDataSetChanged();
    }

    public void stockEntryUpdate(Stock s){
        boolean duplicate = false;
        for (int i = 0; i < stockList.size(); i++){
            Stock s2 = stockList.get(i);
            if(s2.getSymbol().equals(s.getSymbol())){
                duplicate = true;
            }

        }
        if(duplicate){ //if duplicate
            addDialogue(s.getSymbol(), 3);
        }else{
            databaseHandler.addStock(s);
            stockList.add(s);
            Collections.sort(stockList);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.AddStock:
                onAddStock();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);

        String stockUrl = "https://www.marketwatch.com/investing/stock/" + s.getSymbol();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(stockUrl));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        final Stock s = stockList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stockList.remove(pos);
                databaseHandler.deleteStock(s.getSymbol());
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.setTitle("Delete Stock "+ s.getName());

        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    public void onAddStock() {

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }

        if (sHash.isEmpty()){
            new NameDownloader(this).execute(); //Populate HashMap if app started with no internet access.
        }


        if(connected) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            et.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(et);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (sHash.containsKey(et.getText().toString())) {
                        stockSearch(et.getText().toString());
                    } else {
                        addDialogue(et.getText().toString(), 1);
                    }
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            builder.setMessage("Stock Selection");
            builder.setTitle("Please Enter a Stock Symbol");

            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            addDialogue(null, 2);
        }
    }

    private void addDialogue(String sym, int code){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (code) {
            case NOT_FOUND:
                builder.setTitle("SYMBOL NOT FOUND: " + sym);
                builder.setMessage("Symbol not found.");
                break;
            case NO_INTERNET:
                builder.setTitle("No internet access");
                builder.setMessage("No internet access.");
                break;
            case DUPLICATE_STOCK:
                builder.setTitle("DUPLICATE SYMBOL: " + sym);
                builder.setMessage("Stock already exist.");
                break;
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void stockSearch(String symbol) {
        new StockDownloader(this).execute(symbol);
    }
}
