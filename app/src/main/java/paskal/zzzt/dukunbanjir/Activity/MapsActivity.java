package paskal.zzzt.dukunbanjir.Activity;

import android.Manifest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import paskal.zzzt.dukunbanjir.Data.MarkerJSONParser;
import paskal.zzzt.dukunbanjir.R;

public class MapsActivity extends FragmentActivity {

    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
      /*  // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
  /*  @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/


// Getting reference to SupportMapFragment
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Creating GoogleMap from SupportMapFragment
        mGoogleMap = fragment.getMap();

        // Enabling MyLocation button for the Google Map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        // Setting OnClickEvent listener for the GoogleMap
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latlng) {
                addMarker(latlng);
                sendToServer(latlng);
            }
        });

        // Starting locations retrieve task
        new RetrieveTask().execute();
    }

    // Adding marker on the GoogleMaps
    private void addMarker(LatLng latlng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title(latlng.latitude + "," + latlng.longitude);
        mGoogleMap.addMarker(markerOptions);
    }

    // Invoking background thread to store the touched location in Remove MySQL server
    private void sendToServer(LatLng latlng) {
        new SaveTask().execute(latlng);
    }
    // Background thread to save the location in remove MySQL server
    private class SaveTask extends AsyncTask<LatLng, Void, Void> {
        @Override
        protected Void doInBackground(LatLng... params) {
            String lat = Double.toString(params[0].latitude);
            String lng = Double.toString(params[0].longitude);
            String strUrl = "http://192.168.1.3/location_marker_mysql/save.php";
            URL url = null;
            try {
                url = new URL(strUrl);

                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                        connection.getOutputStream());

                outputStreamWriter.write("lat=" + lat + "&lng="+lng);
                outputStreamWriter.flush();
                outputStreamWriter.close();

                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";

                while( (line = reader.readLine()) != null){
                    sb.append(line);
                }

                reader.close();
                iStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    // Background task to retrieve locations from remote mysql server
    private class RetrieveTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            String strUrl = "http://192.168.1.3/location_marker_mysql/retrieve.php";
            URL url = null;
            StringBuffer sb = new StringBuffer();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
                String line = "";
                while( (line = reader.readLine()) != null){
                    sb.append(line);
                }

                reader.close();
                iStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    // Background thread to parse the JSON data retrieved from MySQL server
    private class ParserTask extends AsyncTask<String, Void, List<HashMap<String, String>>>{
        @Override
        protected List<HashMap<String,String>> doInBackground(String... params) {
            MarkerJSONParser markerParser = new MarkerJSONParser();
            JSONObject json = null;
            try {
                json = new JSONObject(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<HashMap<String, String>> markersList = markerParser.parse(json);
            return markersList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            for(int i=0; i<result.size();i++){
                HashMap<String, String> marker = result.get(i);
                LatLng latlng = new LatLng(Double.parseDouble(marker.get("lat")), Double.parseDouble(marker.get("lng")));
                addMarker(latlng);
            }
        }
    }

}