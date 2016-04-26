package paskal.zzzt.dukunbanjir.Activity;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import paskal.zzzt.dukunbanjir.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    RequestQueue requestQueue;
    String showUrl = "http://1303037.ti.polindra.ac.id:88/api.php/ta?transform=1";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

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

    //fungsi GET JSON to MAP
    public  void RequestJSON(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, showUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray Locations = response.getJSONArray("ta");
                    for (int i = 0; i < Locations.length(); i++) {
                        JSONObject Location = Locations.getJSONObject(i);

                        Double lang         = Location.getDouble("lng");
                        Double lat          = Location.getDouble("lat");
                        //String kendaraan    = Location.getString("kendaraan");

                        LatLng latLng_angkot = new LatLng(lat, lang);

                        MarkerOptions marker_angkot = new MarkerOptions().position(latLng_angkot);
                        mMap.addMarker(marker_angkot.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                .title("adsd")).hideInfoWindow();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                final Dialog dialogVolleyError = new Dialog(MapsActivity.this, R.style.FullHeightDialog);
                dialogVolleyError.setContentView(R.layout.dialog_json);
                //dialogVolleyError.requestWindowFeature(Window.FEATURE_NO_TITLE);
                TextView dialogTextContent = (TextView)dialogVolleyError.findViewById(R.id.dErrorJson);
                dialogTextContent.setText(error.getMessage());
                LinearLayout tb = (LinearLayout) dialogVolleyError.findViewById(R.id.tbErrorJson);
                tb.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialogVolleyError.dismiss();
                        RequestJSON();
                    }
                });
                dialogVolleyError.show();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        RequestJSON();

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-6.4, 108);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
