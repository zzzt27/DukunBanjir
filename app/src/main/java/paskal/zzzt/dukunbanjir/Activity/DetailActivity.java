package paskal.zzzt.dukunbanjir.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import paskal.zzzt.dukunbanjir.Data.AppController;
import paskal.zzzt.dukunbanjir.Paralax.BaseActivity;
import paskal.zzzt.dukunbanjir.R;

/**
 * Created by ManUnited on 3/17/2016.
 */
public class DetailActivity extends BaseActivity implements OnMapReadyCallback,
        ObservableScrollViewCallbacks, SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener {

    private GoogleMap mMap;
    private View mImageView;
    //private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    private static String TAG = DetailActivity.class.getSimpleName();
    //private String urlJsonObj ="http://percobaaan.esy.es/api.php/ta/1";
    private String jsonResponseAir;
    private String jsonResponseStatus;
    private TextView tvStatus;
    private TextView tvAir;
    private ProgressDialog pDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout btnABOUT;
    private LinearLayout btnGUIDE;
    private LinearLayout btnEXIT;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail1);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvAir = (TextView) findViewById(R.id.tvTinggi);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        btnABOUT = (LinearLayout) findViewById(R.id.btnAbout);
        btnGUIDE = (LinearLayout) findViewById(R.id.btnGuide);
        btnEXIT = (LinearLayout) findViewById(R.id.btnExit);


        //onclick
        btnGUIDE.setOnClickListener(this);
        btnABOUT.setOnClickListener(this);
        btnEXIT.setOnClickListener(this);

        //Dialog Json
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        //json
        //makeJsonObjectRequest();


        //map
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //toolbar & observable
        mImageView = findViewById(R.id.map);
        //mToolbarView = findViewById(R.id.toolbar);
        //mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.colorPrimary)));
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);


        //SwipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.green));
        swipeRefreshLayout.setColorSchemeColors(Color.WHITE);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        //makeJsonObjectRequest();
                                    }
                                }
        );

    }



    //fungsi parsing json
   /* public void makeJsonObjectRequest() {
        //parsing json

        swipeRefreshLayout.setRefreshing(true);
        showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG,"json" + response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
                    String status = response.getString("status");
                    String air = response.getString("tinggi_air");


                    //jsonResponse = "";
                    jsonResponseStatus =  status;
                    jsonResponseAir = air;

                    if (status.equals("AMAN")) {
                        tvStatus.setTextColor(Color.GREEN);
                    } else {
                        tvStatus.setTextColor(Color.RED);
                    }


                    //aq.id(R.id.tvStatus).text(jsonResponseStatus);
                    //aq.id(R.id.tvTinggi).text(jsonResponseAir);

                     tvAir.setText(jsonResponseAir);
                     tvStatus.setText(jsonResponseStatus);


                } catch (JSONException e) {
                    e.printStackTrace();

                    final Dialog openDialog = new Dialog(DetailActivity.this, R.style.FullHeightDialog);
                    openDialog.setContentView(R.layout.dialog_json);
                    //openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    TextView dialogTextContent = (TextView)openDialog.findViewById(R.id.dErrorJson);
                    dialogTextContent.setText(e.getMessage());
                    LinearLayout tb = (LinearLayout) openDialog.findViewById(R.id.tbErrorJson);
                    tb.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            openDialog.dismiss();
                            makeJsonObjectRequest();
                        }
                    });
                    openDialog.show();

                }
                swipeRefreshLayout.setRefreshing(false);
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    final Dialog dialogVolleyError = new Dialog(DetailActivity.this, R.style.FullHeightDialog);
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
                            makeJsonObjectRequest();
                        }
                    });
                    dialogVolleyError.show();

                } else if (error instanceof AuthFailureError) {
                    //TODO

                    final Dialog dialogVolleyError = new Dialog(DetailActivity.this, R.style.FullHeightDialog);
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
                            makeJsonObjectRequest();
                        }
                    });
                    dialogVolleyError.show();

                } else if (error instanceof ServerError) {
                    //TODO

                    final Dialog dialogVolleyError = new Dialog(DetailActivity.this, R.style.FullHeightDialog);
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
                            makeJsonObjectRequest();
                        }
                    });
                    dialogVolleyError.show();

                } else if (error instanceof NetworkError) {
                    //TODO

                    final Dialog dialogVolleyError = new Dialog(DetailActivity.this, R.style.FullHeightDialog);
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
                            makeJsonObjectRequest();
                        }
                    });
                    dialogVolleyError.show();

                } else if (error instanceof ParseError) {
                    //TODO

                    final Dialog dialogVolleyError = new Dialog(DetailActivity.this, R.style.FullHeightDialog);
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
                            makeJsonObjectRequest();
                        }
                    });
                    dialogVolleyError.show();
                }


                // hide the progress dialog
                hidepDialog();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }*/



    //fungsi MAP
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng bangkir = new LatLng(-6.385585, 108.292804);
        //mMap.addMarker(new MarkerOptions().position(bangkir).title("Disini ada faisal"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-6.385585, 108.292804), 18));
        //mMap.animateCamera(CameraUpdateFactory.zoomIn());
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                        //.anchor(0.0f, -0.3f) // Anchors the marker on the bottom left
                .position(new LatLng(-6.385585, 108.292804))
                .title("lorem ipsum")
                .snippet(jsonResponseAir));

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        //mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void onRefresh() {
        //makeJsonObjectRequest();
    }


    //fungsi onclick
    @Override
    public void onClick(View view) {
        if (view.equals(btnABOUT)){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }else if (view.equals(btnGUIDE)){
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        }else if (view.equals(btnEXIT)){

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing Activity")
                    .setMessage("Are you sure you want to close this activity?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}




