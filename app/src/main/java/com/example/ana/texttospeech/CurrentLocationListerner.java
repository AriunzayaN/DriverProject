package com.example.ana.texttospeech;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Ana on 3/29/2017.
 */

public class CurrentLocationListerner implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());

        String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();

        //I make a log to see the results
        Log.e("MY CURRENT LOCATION", myLocation );

        try{
            URL url = new URL("https://curewitz.com/location/");

            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);

            OutputStreamWriter writer = new

                    OutputStreamWriter(conn.getOutputStream());

            writer.write("_method=POST&id=0&latitude="+latitude+"&longitude="+longitude+"");

            writer.flush();

            String line;

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = reader.readLine()) != null) {

                System.out.println(line);

            }

//            Log.d("A", )
            writer.close();

            reader.close();
        }catch (IOException e){
           Log.e("NO JSON", "json was not retrieved");
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
