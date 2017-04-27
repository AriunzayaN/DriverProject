package com.example.ana.texttospeech;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Ana on 4/8/2017.
 */

public class FetchTable extends AsyncTask<Void, Void, JSONArray>{
    @Override
    public JSONArray doInBackground(Void... params) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URLConnection urlConn;



        try
        {
            URL url = new URL("https://curewitz.com/request/");
            urlConn = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));


            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null)
            {
                stringBuffer.append(line);
                Log.d("JSON ", line);
            }

            JSONArray jsonArray = new JSONArray(String.valueOf(stringBuffer));
            Log.d("JSON STRING", jsonArray.toString());

            br.close();

            return jsonArray;

        }
        catch(Exception ex)
        {
            Log.e("App", "Null", ex);
            return null;
        }
    }

//    @Override
//    protected void onPostExecute(JSONObject response)
//    {
//        if(response != null)
//        {
//            try {
//                Log.e("App", "Success: " + response.getString("yourJsonElement") );
//            } catch (JSONException ex) {
//                Log.e("App", "Failure", ex);
//            }
//        }
//    }
}
