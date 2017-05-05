package com.example.ana.texttospeech;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TextToSpeechTest extends AppCompatActivity {
    private Button btnEnter;
    private EditText txtMultLine;

    Button[] delButtonArray = new Button[8];
    EditText[] textsArrayFrom = new EditText[8];
    EditText[] textsArrayTo = new EditText[8];
    String[] u_idArray = new String[8];

    TextToSpeech speakText;
    FetchTable fetchTable = new FetchTable();
    JSONArray jsa;

    int column = 3;
    int row = 8;

    String[][] tableArray = new String[column][row];
    private HashMap<String, Integer> speakTableMap = new HashMap<String, Integer>();

    Double longitude;
    Double latitude;

    LinearLayout toArray;
    LinearLayout fromArray;

    // Maps location
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        btnEnter = (Button) findViewById(R.id.btnSpeak);
        txtMultLine = (EditText) findViewById(R.id.txtMultiple);


        fromArray = (LinearLayout) findViewById(R.id.from_array);
        toArray= (LinearLayout) findViewById(R.id.to_array);
        delButtonArray[0] = (Button) findViewById(R.id.btnDel1);
        delButtonArray[1] = (Button) findViewById(R.id.btnDel2);
        delButtonArray[2] = (Button) findViewById(R.id.btnDel3);
        delButtonArray[3] = (Button) findViewById(R.id.btnDel4);
        delButtonArray[4] = (Button) findViewById(R.id.btnDel5);
        delButtonArray[5] = (Button) findViewById(R.id.btnDel6);
        delButtonArray[6] = (Button) findViewById(R.id.btnDel8);
        delButtonArray[7] = (Button) findViewById(R.id.btnDel8);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();

//                Location loc = new Location(latitude.toString() + longitude.toString());
                final Thread timer = new Thread() {
                    public void run() {
                        try {
                            URL url = new URL("https://frogger.cs.endicott.edu/shuttle-tracker/location/");

                            URLConnection conn = url.openConnection();

                            conn.setDoOutput(true);

                            OutputStreamWriter writer = new

                                    OutputStreamWriter(conn.getOutputStream());

                            writer.write("_method=POST&id=0&latitude=" + latitude + "&longitude=" + longitude + "");

                            writer.flush();

                            String line;

                            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                            while ((line = reader.readLine()) != null) {

                                System.out.println(line);

                            }

//            Log.d("A", )
                            writer.close();

                            reader.close();
                        } catch (IOException e) {
                            Log.e("NO JSON", "json was not retrieved");
                        }

                    }
                };
                timer.start();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }

        };

        locationFunction();


        // Text to speech initialization
        speakText = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speakText.setLanguage(Locale.UK);
                    speakText.setSpeechRate((float) 0.7);
                }
            }
        });
//


        // Speak button to read the text
        btnEnter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String toSpeak = txtMultLine.getText().toString();

                speakText.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        // Thread that refreshes every e 3 seconds
        final Thread timer = new Thread() {

            public void run() {

                Timer timer = new Timer();

                TimerTask timerTask = new TimerTask() {

                    @Override

                    public void run() {

                    }

                };

                timer.schedule(timerTask, 0, 3000);

            }

        };

        timer.start();

        final Handler handler = new Handler();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        jsa = fetchTable.doInBackground();

                        if (jsa == null) {
                            Arrays.fill(u_idArray, null);
                            for (int i = 0; i < u_idArray.length; i++) {
                                if (u_idArray[i] == null)
                                    delButtonArray[i].setVisibility(View.INVISIBLE);
                            }
                            speakTableMap.clear();
                            fromArray.removeAllViews();
                            toArray.removeAllViews();
                            txtMultLine.setText("No requests yet.");
                        } else {
                            speakTableMap.clear();
                            txtMultLine.setText("");
                            buildTable(jsa);
                            try {
                                nonSpeakTable(jsa);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    ;
                });
            }
        }, 0, 3000);

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                jsa = fetchTable.doInBackground();
//
//                if(jsa == null){
//                    txtLatLon.setText("No requests yet.");
//                }else{
//                    buildTable(jsa);
//                    try {
//                        nonSpeakTable(jsa);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }, 3000);

    }

    public void onPause() {
        if (speakText != null) {
            speakText.stop();
            speakText.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                locationFunction();
                break;
            default:
                break;
        }
    }

    public void locationFunction() {

        //        // Checking permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }

        // Getting location and sending it every 3 seconds
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locationListener);
    }

    // Builds a single line texts that says how many people are at what building.
    public void buildTable(JSONArray jsa) {
        for (int i = 0; i < jsa.length(); i++) {
            try {

                JSONObject jso = jsa.getJSONObject(i);
                String buildingFrom = jso.getString("building_from");

                if(speakTableMap.containsKey(buildingFrom)){
                    Integer count = speakTableMap.get(buildingFrom);
                    speakTableMap.put(buildingFrom, count+1);
                }else{
                    speakTableMap.put(buildingFrom, 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(!speakTableMap.isEmpty()){
            for (Map.Entry<String , Integer> entry : speakTableMap.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                txtMultLine.append(String.valueOf(value) + " at " + key + ", \n");
                txtMultLine.setTextColor(Color.parseColor("#eaefed"));
            }
        }

    }

    public void nonSpeakTable(JSONArray jsa) throws JSONException {
        Arrays.fill(u_idArray, null);
        fromArray.removeAllViews();
        toArray.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        if (jsa != null) {
            for (int i = 0; i < jsa.length(); i++) {
                // Turning the json array into json object.
                JSONObject jso = jsa.getJSONObject(i);

                // Taking the individual fields to use.
                String buildingFrom = jso.getString("building_from");
                String buildingTo = jso.getString("building_to");
                String id = jso.getString("u_id");

                // Getting the id into a string array.
                u_idArray[i] = id;
                delButtonArray[i].setVisibility(View.VISIBLE);

                // Getting the "from" destination into a text array and displaying it in linear layout.
                textsArrayFrom[i] = new EditText(this);
                textsArrayFrom[i].setTextSize(18);
                textsArrayFrom[i].setTextColor(Color.parseColor("#eaefed"));
                textsArrayFrom[i].setFocusable(false);
                textsArrayFrom[i].setLayoutParams(lp);
                textsArrayFrom[i].setId(i);
                textsArrayFrom[i].setText(buildingFrom);
                fromArray.addView(textsArrayFrom[i]);

                // Getting the "to" destination into a line array and displaying it in linear layout.
                textsArrayTo[i] = new EditText(this);
                textsArrayTo[i].setTextSize(18);
                textsArrayTo[i].setTextColor(Color.parseColor("#eaefed"));
                textsArrayTo[i].setFocusable(false);
                textsArrayTo[i].setLayoutParams(lp);
                textsArrayTo[i].setId(i);
                textsArrayTo[i].setText(buildingTo);
                toArray.addView(textsArrayTo[i]);

                deleteButton();

            }
        }

    }

    // Function to initialize buttons and method to remove request
    public void deleteButton() {
        for (int i = 0; i < u_idArray.length; i++) {
            if (u_idArray[i] == null)
                delButtonArray[i].setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < u_idArray.length; i++) {
            final int finalI = i;
            delButtonArray[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    URL url = null;
                    try {
                        url = new URL("https://frogger.cs.endicott.edu/shuttle-tracker/request/");


                        URLConnection conn = url.openConnection();

                        conn.setDoOutput(true);

                        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                        writer.write("_method=DELETE&u_id=" + u_idArray[finalI]);

                        Log.d("SENDING ID", "https://frogger.cs.endicott.edu/shuttle-tracker/request/" +
                                "?_method=DELETE&u_id=" + u_idArray[finalI]);
                        writer.flush();

                        String line;

                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        while ((line = reader.readLine()) != null) {

                            System.out.println(line);

                        }

                        writer.close();

                        reader.close();

                        nonSpeakTable(jsa);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }

    }


}
