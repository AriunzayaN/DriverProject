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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TextToSpeechTest extends AppCompatActivity {
    private Button btnEnter;
    private EditText txtEnter;
    private EditText txtLatLon;
    private EditText txtMultLine;
    TextToSpeech speakText;
    FetchTable fetchTable = new FetchTable();
    JSONArray jsa;

    Double longitude;
    Double latitude;


    // Maps location
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);
        btnEnter = (Button) findViewById(R.id.btnSpeak);
        txtEnter = (EditText) findViewById(R.id.txtEnter);
        txtLatLon = (EditText) findViewById(R.id.txtLatLon);
        txtMultLine = (EditText) findViewById(R.id.txtMultiple);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();

//                Location loc = new Location(latitude.toString() + longitude.toString());
                txtEnter.setText(latitude.toString() + longitude.toString());
                final Thread timer = new Thread() {
                    public void run() {
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
                }
            }
        });
//


        // Speak button to read the text
        btnEnter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String toSpeak = txtEnter.getText().toString();

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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               jsa = fetchTable.doInBackground();

                if(jsa == null){
                    txtLatLon.setText("It was null");
                }else{
                    buildTable(jsa);
                }
            }
        }, 3000);

//        Log.d("Before setting it: ", jsa.toString());
//    txtLatLon.setText(jsa.toString());
    }

    public void onPause(){
        if(speakText !=null){
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

    public void locationFunction(){

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
    }

    public void buildTable(JSONArray jsa){
        for (int i = 0 ; i < jsa.length(); i++){
            try {
                JSONObject jso = jsa.getJSONObject(i);
                if(!txtMultLine.getText().equals(jso.getString("building_from"))){
                    txtMultLine.append(jso.getString("building_from"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }





















//    public void determineLocation(double lat, double lon){
//        // Larger number,    lesser number                    lesser number      larger number
//        if (( lat < 42.554337 &&  lat > 42.553633)&& ( lon < -70.842358 && lon > -70.842969)){
//            txtEnter.setText("LSB");
//
//        }else if (( lat < 42.553710  && lat > 42.553068 ) && ( lon < -70.843262 && lon > -70.843716 )){
//            txtEnter.setText("Callahan");
//
//        }else if((lat < 42.552341 && lat > 42.552133) && (lon < -70.842647 && lon > -70.842673)){
//            txtEnter.setText("Public safety");
//
//        }else if((lat < 42.551589 && lat > 42.551502) && (lon < -70.841201 && lon > -70.841657)){
//            txtEnter.setText("Hawthorne Hall");
//
//        }else if((lat < 42.551281 && lat > 42.551127) && (lon < -70.841281 && lon > -70.841464)){
//            txtEnter.setText("Rogers hall");
//
//        }else if((lat < 42.552293 && lat > 42.551857) && (lon < -70.837326 && lon > -70.837752)){
//            txtEnter.setText("Brindle hall");
//
//        }else if((lat < 42.552486 && lat > 42.552052) && (lon < -70.837896 && lon > -70.838401)){
//            txtEnter.setText("Endicott Hall");
//
//        }else if((lat < 42.553123 && lat > 42.552704) && (lon < -70.837966 && lon > -70.838272)){
//            txtEnter.setText("Reynolds Hall");
//
//        }else if((lat < 42.552977 && lat > 42.552226) && (lon < -70.839481 && lon > -70.839522)){
//            txtEnter.setText("Manninen Center for the Arts");
//
//        }else if((lat < 42.553300 && lat > 42.552980) && (lon < -70.839474 && lon > -70.839715)){
//            txtEnter.setText("Woodside hall");
//
//        }else if((lat < 42.555537 && lat > 42.554755) && (lon < -70.839061 && lon > -70.840005)){
//            txtEnter.setText("Post center");
//
//        }else if((lat < 42.553312 && lat > 42.553099) && (lon < -70.838814 && lon > -70.839490)){
//            txtEnter.setText("Tower hall");
//
//        }else if((lat < 42.553628 && lat > 42.553348) && (lon < -70.839130 && lon > -70.839329)){
//            txtEnter.setText("Manchester hall");
//
//        }else if((lat < 42.553664 && lat > 42.553387) && (lon < -70.838776 && lon > -70.838991)){
//            txtEnter.setText("Gloucester hall");
//
//        }else if((lat < 42.553735 && lat > 42.553530) && (lon < -70.838041 && lon > -70.838331)){
//            txtEnter.setText("The lodge");
//
//        }else if((lat < 42.553873 && lat > 42.553719) && (lon < -70.838508 && lon > -70.838674)){
//            txtEnter.setText("Grove hall");
//
//        }else if((lat < 42.554486 && lat > 42.554435) && (lon < -70.838690 && lon > -70.839243)){
//            txtEnter.setText("Physical plant");
//
//        }else if((lat < 42.553324 && lat > 42.553091) && (lon < -70.840329 && lon > -70.841273)){
//            txtEnter.setText("Diane Meyers Halle Library");
//
//        }else if((lat < 42.554715 && lat > 42.553763) && (lon < -70.840415 && lon > -70.841444)){
//            txtEnter.setText("Wax academic center");
//
//        }else if((lat < 42.555363 && lat > 42.555046) && (lon < -70.840892 && lon > -70.841512)){
//            txtEnter.setText("Williston hall");
//
//        }else if((lat < 42.554629 && lat > 42.554147) && (lon < -70.843518 && lon > -70.844044)){
//            txtEnter.setText("Kennedy hall");
//
//        }else if((lat < 42.552987 && lat > 42.552716) && (lon < -70.842794 && lon > -70.843052)){
//            txtEnter.setText("Chapel");
//
//        }else if((lat < 42.553174 && lat > 42.553067) && (lon < -70.845262 && lon > -70.845412)){
//            txtEnter.setText("Cliff house");
//
//        }else if((lat < 42.553075 && lat > 42.552889) && (lon < -70.844699 && lon > -70.844795)){
//            txtEnter.setText("Essex hall");
//
//        }else if((lat < 42.553668 && lat > 42.553593) && (lon < -70.844876 && lon > -70.845777)){
//            txtEnter.setText("Bayview hall");
//
//        }else if((lat < 42.553873 && lat > 42.553850) && (lon < -70.845723 && lon > -70.846726)){
//            txtEnter.setText("Marblehead hall");
//
//        }else if((lat < 42.553115 && lat > 42.552941) && (lon < -70.845820 && lon > -70.845938)){
//            txtEnter.setText("Rockport house");
//
//        }else if((lat < 42.553293 && lat > 42.553103) && (lon < -70.846007 && lon > -70.846018)){
//            txtEnter.setText("The ledge");
//
//        }else if((lat < 42.553415 && lat > 42.553241) && (lon < -70.846630 && lon > -70.846758)){
//            txtEnter.setText("The farm house");
//
//        }else if((lat < 42.554008 && lat > 42.554778) && (lon < -70.846437 && lon > -70.846769)){
//            txtEnter.setText("Stoneridge hall");
//
//        }else if((lat < 42.555715 && lat > 42.554893) && (lon < -70.845519 && lon > -70.846458)){
//            txtEnter.setText("The village");
//
//        }else if((lat < 42.556339 && lat > 42.555798) && (lon < -70.844189 && lon > -70.845632)){
//            txtEnter.setText("Standish hall");
//
//        }else if((lat < 42.556687 && lat > 42.556683) && (lon < -70.846447 && lon > -70.847574)){
//            txtEnter.setText("Center for nursing");
//
//        }else if((lat < 42.557122 && lat > 42.556960) && (lon < -70.846576 && lon > -70.847933)){
//            txtEnter.setText("Raymond Bourque arena");
//
//        }else if((lat < 42.554197 && lat > 42.553723) && (lon < -70.849103 && lon > -70.849446)){
//            txtEnter.setText("Van Loan school");
//
//        }else if((lat < 42.553684 && lat > 42.553557) && (lon < -70.849344 && lon > -70.849430)){
//            txtEnter.setText("The Cottage");
//
//        }else if((lat < 42.553123 && lat > 42.552617) && (lon < -70.846909 && lon > -70.849789)){
//            txtEnter.setText("Wylie Inn and Conference center");
//
//        }else if((lat < 42.551174 && lat > 42.550937) && (lon < -70.842242 && lon > -70.842542)){
//            txtEnter.setText("Beacon hall");
//
//        }else if((lat < 42.550914 && lat > 42.550898) && (lon < -70.841464 && lon > -70.842215)){
//            txtEnter.setText("Winthrop hall");
//
//        }else if((lat < 42.550783 && lat > 42.550669) && (lon < -70.840804 && lon > -70.841088)){
//            txtEnter.setText("Birchmont");
//
//        }else if((lat < 42.550846 && lat > 42.550763) && (lon < -70.838642 && lon > -70.839130)){
//            txtEnter.setText("Misslewood tent");
//
//        }else if((lat < 42.550874 && lat > 42.550578) && (lon < -70.838090 && lon > -70.838299)){
//            txtEnter.setText("Misslewood");
//
//        }else if((lat < 42.551238 && lat > 42.551095) && (lon < -70.837199 && lon > -70.837424)){
//            txtEnter.setText("Carriage house");
//
//        }else if((lat < 42.551234 && lat > 42.551163) && (lon < -70.836883 && lon > -70.837060)){
//            txtEnter.setText("Beach house");
//
//        }else if((lat < 42.551805 && lat > 42.551730) && (lon < -70.835916 && lon > -70.836205)){
//            txtEnter.setText("Hamilton hall");
//
//        }else if((lat < 42.551991 && lat > 42.551957) && (lon < -70.835512 && lon > -70.835912)){
//            txtEnter.setText("Wenham hall");
//
//        }else if((lat < 42.552471 && lat > 42.551920) && (lon < -70.834351 && lon > -70.834785)){
//            txtEnter.setText("Beechwood");
//
//        }else if((lat < 42.551491 && lat > 42.551080) && (lon < -70.839742 && lon > -70.840187)){
//            txtEnter.setText("College hall");
//
//        }else if((lat < 42.551404 && lat > 42.551360) && (lon < -70.839254 && lon > -70.839463)){
//            txtEnter.setText("Brooks hall");
//
//        }else if((lat < 42.551680 && lat > 42.551455) && (lon < -70.839018 && lon > -70.839238)){
//            txtEnter.setText("Trexler hall");
//
//        }else if((lat < 42.551965 && lat > 42.551957) && (lon < -70.838790 && lon > -70.839096)){
//            txtEnter.setText("Hale hall");
//
//        }
//
//    }
}
