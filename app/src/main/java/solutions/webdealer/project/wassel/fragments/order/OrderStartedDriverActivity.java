package solutions.webdealer.project.wassel.fragments.order;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.navigations.NavigationDriver;
import solutions.webdealer.project.wassel.services.LocationChangeService;

public class OrderStartedDriverActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences1;
    private ImageView direcArrow;
    SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private String orderDescription;
    private SlidingUpPanelLayout slidingLayout;
    private String category;
    private Intent intent;
    private String droplat;
    private String droplongi;
    private String nameofDriver;
    private String numberofUser;
    private String driverId;
    private String orderId;
    private String useridis;
    String countis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_started_driver);

        try {


            intent = getIntent();

            if (intent != null) {

                if (this.getIntent().getExtras() != null) {

                    if (intent.hasExtra("title")) {
                        if (intent.getStringExtra("title").equalsIgnoreCase("userConfirmOrder")) {


                            droplat = intent.getStringExtra("userLatitude");
                            droplongi = intent.getStringExtra("userLongitude");
                            nameofDriver = intent.getStringExtra("name");
                            numberofUser = intent.getStringExtra("userNumber");
                            orderId = intent.getStringExtra("order_id");
                            driverId = intent.getStringExtra("driver_id");
                            useridis = intent.getStringExtra("user_id");


                            SharedPreferences sharedPreferences4 = getApplicationContext().getSharedPreferences("saveDataDriver", Context.MODE_PRIVATE);
                            editor = sharedPreferences4.edit();
                            editor.clear();
                            editor.putString("userLatitude", droplat);
                            editor.putString("userLongitude", droplongi);
                            editor.putString("name", nameofDriver);
                            editor.putString("userNumber", numberofUser);
                            editor.putString("order_id", orderId);
                            editor.putString("driver_id", driverId);
                            editor.putString("user_id", useridis);
                            editor.commit();

                        }
                    }
                }
//        View view = inflater.inflate(R.layout.fragment_order_started_driver, container, false);

                sharedPreferences1 = getApplicationContext().getSharedPreferences("IsOrderOn", Context.MODE_PRIVATE);
                editor = sharedPreferences1.edit();
                editor.clear();
                editor.putBoolean("OrderStatus", true);
                editor.commit();

                direcArrow = (ImageView) findViewById(R.id.iv_direcArrow);


                sharedPreferences = getApplicationContext().getSharedPreferences("OrderData", Context.MODE_PRIVATE);
                orderDescription = sharedPreferences.getString("Order", null);
                category = sharedPreferences.getString("Category", null);
//        subCategory = sharedPreferences.getString("SubCategory", null);        Latitude = Double.valueOf(sharedPreferences.getString("Latitude", null));
//        Longitude = Double.valueOf(sharedPreferences.getString("Longitude", null));

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
//
//                        Double Lat = Double.valueOf(droplat);
//                        Double lang = Double.valueOf(droplongi);
                        Double Lat=122.121;
                        Double lang=37.21;
                        LatLng sydney = new LatLng(Lat, lang);
                        googleMap.addMarker(new MarkerOptions().position(sydney).title("Order Delivery Location"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                        googleMap.getUiSettings().setZoomGesturesEnabled(true);
                        googleMap.getUiSettings().setScrollGesturesEnabled(true);
                        googleMap.getUiSettings().setMapToolbarEnabled(false);
                    }
                });

                slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
                Button conButtton = (Button) findViewById(R.id.contact_driver);
                Button reachedDestination = (Button) findViewById(R.id.reachedDestination);
                reachedDestination.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        totalBill();

                    }
                });

                Button driButton = (Button) findViewById(R.id.orderDelivered);
                Button orderPickedUp = (Button) findViewById(R.id.orderPickedUp);
                orderPickedUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRating();
                    }
                });
                conButtton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", numberofUser, null));
                        startActivity(intent);
                    }
                });

                driButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        orderdeliveredfunction();
                    }
                });
                //some "demo" event
                slidingLayout.setPanelSlideListener(onSlideListener());


            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void orderdeliveredfunction() {
        sharedPreferences1 = getApplicationContext().getSharedPreferences("IsOrderOn", Context.MODE_PRIVATE);
        editor = sharedPreferences1.edit();
        editor.clear();
        editor.putBoolean("OrderStatus", false);
        editor.commit();

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverReachedAtDestination";

        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    stopService(new Intent(getApplication(), LocationChangeService.class));
                    Intent intent = new Intent(getApplicationContext(), NavigationDriver.class);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("driver_id", driverId);
                params.put("user_id", useridis);


                return params;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                direcArrow.setImageDrawable(getResources().getDrawable(R.drawable.downward_arrow));
            }

            @Override
            public void onPanelCollapsed(View view) {
                direcArrow.setImageDrawable(getResources().getDrawable(R.drawable.upward_arrow));
            }

            @Override
            public void onPanelExpanded(View view) {
            }

            @Override
            public void onPanelAnchored(View view) {
            }

            @Override
            public void onPanelHidden(View view) {
            }
        };
    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        LatLng sydney = new LatLng(31.5546, 74.3572);
//        googleMap.addMarker(new MarkerOptions().position(sydney)
//                .title("Order Delivery Location"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
//        googleMap.getUiSettings().setZoomGesturesEnabled(true);
//        googleMap.getUiSettings().setScrollGesturesEnabled(false);
//        googleMap.getUiSettings().setMapToolbarEnabled(false);
//    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(OrderStartedDriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(OrderStartedDriverActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(OrderStartedDriverActivity.this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(OrderStartedDriverActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(OrderStartedDriverActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(OrderStartedDriverActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(OrderStartedDriverActivity.this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void showRating() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.rate_user);
        dialog.setCancelable(false);
        dialog.show();


        Button submit = (Button) dialog.findViewById(R.id.bt_submit);
        final com.whinc.widget.ratingbar.RatingBar userRating = (com.whinc.widget.ratingbar.RatingBar) dialog.findViewById(R.id.ratingBar);

        String ratingss;

        userRating.setOnRatingChangeListener(new com.whinc.widget.ratingbar.RatingBar.OnRatingChangeListener() {
            @Override
            public void onChange(com.whinc.widget.ratingbar.RatingBar ratingBar, int i, int i1) {
                countis = Integer.toString(i1);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
              ratingApi(countis);


            }

        });


    }


    public void totalBill() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.total_bill_dialog);
        dialog.setCancelable(false);
        dialog.show();


        Button submit = (Button) dialog.findViewById(R.id.submit_bill);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }

        });


    }


    private void ratingApi(final String rat) {
        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverRating";

        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("status")){

                        Toast.makeText(OrderStartedDriverActivity.this, "rating is registered", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("driver_id", driverId);
                params.put("user_id", useridis);
                params.put("order_id", orderId);
                params.put("rating", rat);


                return params;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

}

