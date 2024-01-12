package com.example.smart_home_controller_pine64;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class NetworkTask extends AsyncTask<URL, Void, String> { //<Params,Progress,Result>
    private static final String TAG = "NetworkTask";


    @Override
    protected String doInBackground(URL... urls) {
        try (InputStream input = urls[0].openStream()) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder json = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }
            return json.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "ioexception: ----------------------------------------"+e.getMessage());
            return null;
        }
    }


}