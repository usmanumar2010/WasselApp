package solutions.webdealer.project.wassel.fragments.order;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.Report.Report;
import solutions.webdealer.project.wassel.activities.navigations.NavigationDriver;
import solutions.webdealer.project.wassel.activities.navigations.NavigationUser;

import static java.security.AccessController.getContext;

public class OrderStartedUserActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private ImageView direcArrow;
    private Button contactDriver;
    private Button submitReport;
    private SlidingUpPanelLayout slidingLayout;
    private Intent intent;
    private JSONObject jsonObjectofbrodcast;
    public String titlehere;
    Double Lat;
    Double lang;
    Marker marker1;
    Marker marker2;
    private String driverlatitude;
    private String driverlongitude;
    private String name_is;
    private String user_number;
    private SharedPreferences sharedPreferences1;
    SharedPreferences.Editor editor;
    private String orderlatitude;
    private String orderlongitude;

    GoogleMap mgooglemap;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        setContentView(R.layout.fragment_order_started_user);
        direcArrow = (ImageView) findViewById(R.id.iv_direcArrow);
        contactDriver = (Button) findViewById(R.id.bt_contactDriver);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        intent = getIntent();

        if (intent != null) {

            if (this.getIntent().getExtras() != null) {

                if (intent.hasExtra("title")) {
                    if (intent.getStringExtra("title").equalsIgnoreCase("userConfirmOrder")) {

                        driverlatitude = intent.getStringExtra("driverlatitude");
                        driverlongitude = intent.getStringExtra("driverlongitude");
                        name_is = intent.getStringExtra("name");
                        user_number = intent.getStringExtra("UserNumber");
                        orderlatitude = intent.getStringExtra("Userlatitude");
                        orderlongitude = intent.getStringExtra("Userlongitude");
                        mapFragment.getMapAsync(this);
//                        mapFragment.getMapAsync(this);
                       // setdrivermarkerNew(Double.valueOf(driverlatitude), Double.valueOf(driverlongitude),mgooglemap);


                    }
                } else {

                }
            }
        }


        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                final String dataString = intent.getExtras().get("UserMessage").toString();
                OrderStartedUserActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            jsonObjectofbrodcast = new JSONObject(dataString);
//                            orderId = jsonObjectofbrodcast.get("order_id").toString();
                            titlehere = jsonObjectofbrodcast.getString("title").toString();

                            if (titlehere.equalsIgnoreCase("driverLocation")) {
                                Lat = Double.valueOf(jsonObjectofbrodcast.getString("latitude"));
                                lang = Double.valueOf(jsonObjectofbrodcast.getString("longitude"));
                                setdrivermarkerNew(Lat, lang,mgooglemap);
                            } else if (titlehere.equalsIgnoreCase("driverReachedAtDestination")) {
                                Intent intent2 = new Intent(getApplicationContext(), NavigationUser.class);
                                startActivity(intent2);

                                sharedPreferences1 = getApplicationContext().getSharedPreferences("UserOrderOn", Context.MODE_PRIVATE);
                                editor = sharedPreferences1.edit();
                                editor.clear();
                                editor.putBoolean("OrderStatus", false);
                                editor.commit();

                                sharedPreferences1 = getApplicationContext().getSharedPreferences("StoreTheLatLang", Context.MODE_PRIVATE);
                                editor = sharedPreferences1.edit();
                                editor.clear();
                            }
                            else if(titlehere.equalsIgnoreCase("driverPickedUserOrder")  )
                            {
                                orderPickedUp();
                            }
                            else if(titlehere.equalsIgnoreCase("driverReachedAtDestination"))
                            {
                                orderReachedDestination();
                            }
//                                if(!Lat.equals("") && !lang.equals(""))
//                                {
//                                    setdrivermarker(Lat,lang);
//                                }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                Log.d("receiver", "Got message: " + dataString);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("update_driver_location"));


        sharedPreferences1 = getApplicationContext().getSharedPreferences("UserOrderOn", Context.MODE_PRIVATE);
        editor = sharedPreferences1.edit();
        editor.clear();
        editor.putBoolean("OrderStatus", true);
        editor.commit();


        sharedPreferences1 = getApplicationContext().getSharedPreferences("StoreTheLatLang", Context.MODE_PRIVATE);
        editor = sharedPreferences1.edit();
        editor.clear();
        editor.putString("latitudeAre", String.valueOf(driverlatitude));
        editor.putString("longitudeAre", String.valueOf(driverlongitude));
        editor.putString("nameIs", name_is);
        editor.putString("usernumberis", user_number);
        editor.putString("orderlatitde", orderlatitude);
        editor.putString("orderlongitude", orderlongitude);
        editor.commit();

//            setOrderMarker(orderlatitude,orderlongitude);

        TextView tv = (TextView) findViewById(R.id.tv_driverName);
        tv.setText(name_is);

        contactDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", user_number, null));
                startActivity(intent);
            }
        });

        submitReport = (Button) findViewById(R.id.bt_submitReport);
        submitReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Report.class);
                startActivity(intent);
            }
        });

        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        slidingLayout.setPanelSlideListener(onSlideListener());

        }catch(Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


//    private void setOrderMarker(final String orderlatitude, final String orderlongitude) {
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//
//
//
//                Lat = Double.valueOf(orderlatitude);
//                lang = Double.valueOf(orderlongitude);
//                LatLng sydney = new LatLng(Lat, lang);
//                googleMap.addMarker(new MarkerOptions().position(sydney).title("order delivery Location"));
//                googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//                googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
//                googleMap.getUiSettings().setZoomGesturesEnabled(true);
//                googleMap.getUiSettings().setScrollGesturesEnabled(false);
//                googleMap.getUiSettings().setMapToolbarEnabled(false);
//            }
//        });
//    }

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

//    public void setdrivermarker(final Double lati, final Double longi) {
//        mapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//
//
//
//                Lat = lati;
//                lang = longi;
//                LatLng sydney = new LatLng(Lat, lang);
//
//                if (marker1 != null) {
//                    marker1.remove();
//                }
//                Double latss = Double.valueOf(orderlongitude);
//                Double longss = Double.valueOf(orderlongitude);
//                LatLng orderplace = new LatLng(latss, longss);
//
//
//                marker1 = googleMap.addMarker(new MarkerOptions().position(sydney).title("driver Location"));
//                googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//                googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
//                googleMap.getUiSettings().setZoomGesturesEnabled(true);
//                googleMap.getUiSettings().setScrollGesturesEnabled(true);
//                googleMap.getUiSettings().setMapToolbarEnabled(false);
//            }
//        });
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mgooglemap=googleMap;

        Lat = Double.valueOf(driverlatitude);
        lang = Double.valueOf(driverlongitude);

        LatLng sydney = new LatLng(Lat, lang);

        if (marker1 != null) {
            marker1.remove();
        }

        setdrivermarkerNew(Double.valueOf(driverlatitude),Double.valueOf(driverlongitude),mgooglemap);

        Double latss = Double.valueOf(orderlongitude);
        Double longss = Double.valueOf(orderlongitude);

        addMarkerfunc(mgooglemap,latss,longss);

//        LatLng orderplace = new LatLng(latss, longss);

//
//        marker1 = googleMap.addMarker(new MarkerOptions().position(sydney).title("driver Location"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
//        googleMap.getUiSettings().setZoomGesturesEnabled(true);
//        googleMap.getUiSettings().setScrollGesturesEnabled(true);
//        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
    }
    private void addMarkerfunc(GoogleMap map, double lat, double lon) {
        map.addMarker(new MarkerOptions().position(new LatLng(31.502044, 74.278409))
                .title("order delivery location")
                .draggable(true));
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.applogo))

    }

    private void setdrivermarkerNew(Double aDouble, Double aDouble1,GoogleMap map) {


        Lat = aDouble;
        lang = aDouble1;
        LatLng sydney = new LatLng(Lat, lang);
//        LatLng sydney = new LatLng(31.500178, 74.277154);

        if (marker1 != null) {
            marker1.remove();
        }
//        Double latss = Double.valueOf(orderlongitude);
//        Double longss = Double.valueOf(orderlongitude);
//        LatLng orderplace = new LatLng(latss, longss);


        marker1 = map.addMarker(new MarkerOptions().position(sydney).title("driver Location"));

    }


    public void orderReachedDestination() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.order_reached_destination);
        dialog.setCancelable(false);
        dialog.show();

        Button submit = (Button) dialog.findViewById(R.id.okay_reached);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }

        });


    }

    public void orderPickedUp() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.order_picked_up_dialog);
        dialog.setCancelable(false);
        dialog.show();

        Button submit = (Button) dialog.findViewById(R.id.okay_picked_up);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();


            }

        });


    }




}

