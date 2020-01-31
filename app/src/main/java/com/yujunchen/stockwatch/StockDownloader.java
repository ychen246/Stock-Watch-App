package com.yujunchen.stockwatch;

import android.annotation.SuppressLint;
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

public class StockDownloader extends AsyncTask<String, Integer, String> {

    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;

    private static final String TAG = "AsyncCountryLoader";

    StockDownloader(MainActivity ma) {
        mainActivity = ma;
    }





    @Override
    protected String doInBackground(String... params) {
        String stockSymb = params[0];
        String DATA_URL = "https://cloud.iexapis.com/stable/stock/" + stockSymb + "/quote?token=sk_9409cf5e69024c058b47c334f6feb460";

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
        Stock stock = parseJSON(s);
        mainActivity.stockEntryUpdate(stock);
    }

    private Stock parseJSON(String s) {

        Stock stockEntry;
        try {
            JSONObject jObjStock = new JSONObject(s);
            String name = jObjStock.getString("companyName");
            String symbol = jObjStock.getString("symbol");
            double price = Double.parseDouble(jObjStock.getString("latestPrice"));
            double change = Double.parseDouble(jObjStock.getString("change"));
            double changePerc = Double.parseDouble(jObjStock.getString("changePercent"));

            stockEntry = new Stock(symbol, name);

            stockEntry.setLatestPrice(price);
            stockEntry.setChange(change);
            stockEntry.setChangePercentage(changePerc);

            return stockEntry;

        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
