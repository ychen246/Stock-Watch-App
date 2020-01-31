package com.yujunchen.stockwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class NameDownloader extends AsyncTask<String, Integer, String> {

    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;

    private static final String DATA_URL =
            "https://api.iextrading.com/1.0/ref-data/symbols";

    private static final String TAG = "AsyncStockLoader";

    NameDownloader(MainActivity ma) { mainActivity = ma; }

    HashMap stockHash = new HashMap();

    //ArrayList<Stock> stockSymb = new ArrayList<>();

    @Override
    protected String doInBackground(String... params) {
        /*if(params != null){
            String stockSym = params[0];
            StockLookup(stockSym);
        }*/
        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }


        } catch (Exception e) {
            return null;
        }

        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        parseJSON(s);
        mainActivity.nameDownload(stockHash);
    }

    private void parseJSON(String s) {
        try {
            JSONArray jObjMain = new JSONArray(s);

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jStock = (JSONObject) jObjMain.get(i);
                String name = jStock.getString("name");
                String symbol = jStock.getString("symbol");
                stockHash.put(symbol, name);
                //stockSymb.add(new Stock(name, symbol));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
