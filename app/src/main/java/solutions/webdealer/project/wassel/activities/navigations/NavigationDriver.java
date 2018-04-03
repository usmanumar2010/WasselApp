package solutions.webdealer.project.wassel.activities.navigations;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

import solutions.webdealer.project.wassel.activities.registrations.LogIn;
import solutions.webdealer.project.wassel.activities.verifications.LisenceVehicleVerification;
import solutions.webdealer.project.wassel.fragments.order.NoRequestUser;
import solutions.webdealer.project.wassel.fragments.order.OrderStartedDriverActivity;
import solutions.webdealer.project.wassel.fragments.order.OrderStartedUserActivity;
import solutions.webdealer.project.wassel.fragments.setting.AccountSetting;
import solutions.webdealer.project.wassel.fragments.profile.DriverProfile;
import solutions.webdealer.project.wassel.fragments.order.NoOrderDriver;
import solutions.webdealer.project.wassel.fragments.order.OrderStartedDriver;
import solutions.webdealer.project.wassel.fragments.history.OrdersHistory;
import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.fragments.profile.UserProfile;
import solutions.webdealer.project.wassel.services.FirebaseMessagingService;
import solutions.webdealer.project.wassel.services.LocationChangeService;

public class NavigationDriver extends AppCompatActivity
        implements UserProfile.OnFragmentInteractionListener, DriverProfile.OnFragmentInteractionListener,
        OrdersHistory.OnFragmentInteractionListener, NoOrderDriver.OnFragmentInteractionListener,
        OrderStartedDriver.OnFragmentInteractionListener, LocationListener {

    ImageView drawerOpenButton;
    CircularImageView driverProfilePic;
    TextView toolbarTitle, orders, history, account, profile, logout;
    DrawerLayout drawerLayout;
    FragmentManager fm;
    FragmentTransaction ft;

    SharedPreferences sharedPreferences, sharedpreferences1, sharedPreferences2, sharedPreferences3;
    SharedPreferences.Editor editor;
    Boolean orderStatus;
    Boolean isInBackground;

    TextView city, firstName, lastName;
    static int counter = 0;

    String userId;
    String orderId, orderDescription, categoryName, orderAddress;
    String subCategoryName = null;
    String tempPaymentType, tempPaymentAmount;
    Double userLatitude, userLongitude;
    Double driverLatitude = null, driverLongitude = null;
    Double pickLatitude, dropLatitude, pickLongitude, dropLongitude;
    String pickAddress, dropAddress;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    JSONObject jsonObjectofbrodcast = null;


    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    // Declaring a Location Manager
    protected LocationManager locationManager;
    public String subCategoryId;

    Intent intent;
    private String userNumberis;
    private String nameis;
    private String userIdHere;
    private String orderIdHere;
    private String titlehere;
    private String userLatitudehere;
    private String userLongitudeHere;
    private String driverIdHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_driver);

        try {
/*        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (IllegalStateException e) {

        }*/

//            getLocation();
            intent = getIntent();

            if (intent != null) {

                if (this.getIntent().getExtras() != null) {

                    if (intent.hasExtra("title")) {
                        if (intent.getStringExtra("title").equalsIgnoreCase("userPostOrder")) {


                            orderDescription = intent.getStringExtra("orderDescription");
                            categoryName = intent.getStringExtra("categoryName");
                            orderAddress = intent.getStringExtra("orderAddress");
                            subCategoryName = intent.getStringExtra("subCategoryName");
                            orderId = intent.getStringExtra("order_id");
                            newOrderDialog(orderDescription, categoryName, subCategoryName, orderAddress);

                        } else if (intent.getStringExtra("title").equalsIgnoreCase("pickDrop")) {
                            orderDescription = intent.getStringExtra("orderDescription");
                            categoryName = intent.getStringExtra("categoryName");
                            pickAddress = intent.getStringExtra("pickAddress");
                            dropAddress = intent.getStringExtra("dropAddress");
                            orderId = intent.getStringExtra("order_id");

                            pickDropOrderDialog(categoryName, orderDescription, pickAddress, dropAddress);

                        } else if (intent.getStringExtra("title").equalsIgnoreCase("userConfirmOrder")) {
                            userNumberis = intent.getStringExtra("userNumber");
                            nameis = intent.getStringExtra("name");
                            userIdHere = intent.getStringExtra("user_id");
                            orderIdHere = intent.getStringExtra("order_id");
                            titlehere = intent.getStringExtra("title");
                            userLatitudehere = intent.getStringExtra("userLatitude");
                            userLongitudeHere = intent.getStringExtra("userLongitude");
                            driverIdHere = intent.getStringExtra("driver_id");
                            userNumberis = intent.getStringExtra("UserNumber");
                            showOrderAcceptedDialog(nameis, userNumberis, titlehere, userLatitudehere, userLongitudeHere);
                        }

                    }
                }


            }

//            Double lat = getLatitude();
//            Double lng = getLongitude();

//            getLocation();

            if (canGetLocation) {
                Toast.makeText(this, String.valueOf(driverLatitude), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, String.valueOf(driverLongitude), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "we are not able to getlocation() thorugh GPS", Toast.LENGTH_SHORT).show();


                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.on_your_gps_layout);
                dialog.setCancelable(false);
                dialog.show();


                Button submit = (Button) dialog.findViewById(R.id.bt_okay);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        //yeah !we got the permission

                        dialog.dismiss();


                    }
                });
            }

            sharedPreferences2 = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            userId = sharedPreferences2.getString("UserId", null);
            if (!isNetworkAvailable()) {
                Toast.makeText(getApplicationContext(), R.string.networkError, Toast.LENGTH_SHORT).show();
            } else {
                setDriverDetail(userId);
            }

            sharedPreferences3 = getSharedPreferences("IsOrderOn", Context.MODE_PRIVATE);
            orderStatus = sharedPreferences3.getBoolean("OrderStatus", false);

            if (orderStatus == true) {
                SharedPreferences sharedPreferences4 = getApplicationContext().getSharedPreferences("saveDataDriver", Context.MODE_PRIVATE);
                String latiofDriver = sharedPreferences4.getString("userLatitude", "0.0");
                String longiofDriver = sharedPreferences4.getString("userLongitude", "0.0");
                String nameofDriver = sharedPreferences4.getString("name", "unknown");
                String numberofDriver = sharedPreferences4.getString("userNumber", "0000");
                String orederidofDriver = sharedPreferences4.getString("order_id", "0");
                String driveridofDriver = sharedPreferences4.getString("driver_id", "0");
                String useridis = sharedPreferences4.getString("user_id", "0");
                Intent intent = new Intent(getApplicationContext(), OrderStartedDriverActivity.class);
                intent.putExtra("userLatitude", latiofDriver);
                intent.putExtra("userLongitude", longiofDriver);
                intent.putExtra("name", nameofDriver);
                intent.putExtra("userNumber", numberofDriver);
                intent.putExtra("order_id", orederidofDriver);
                intent.putExtra("driver_id", driveridofDriver);
                intent.putExtra("title", "userConfirmOrder");
                intent.putExtra("user_id", useridis);
                startActivity(intent);
            }


            driverProfilePic = (CircularImageView) findViewById(R.id.driverProfilePic);

            city = (TextView) findViewById(R.id.tv_city);
            firstName = (TextView) findViewById(R.id.tv_firstName);
            firstName = (TextView) findViewById(R.id.tv_firstName);
            lastName = (TextView) findViewById(R.id.tv_lastName);

            sharedPreferences = getSharedPreferences("LoginStatus", MODE_APPEND);
            editor = sharedPreferences.edit();
            editor.clear();
            editor.putBoolean("LoginCheck", true);
            editor.putInt("Type", 1);
            editor.commit();

/*        if (orderStatus == true) {
            fm = getFragmentManager();
            ft = fm.beginTransaction();
            OrderStartedDriver obj = new OrderStartedDriver();
            ft.add(R.id.frag_container, obj);
            ft.commit();
        } else {
            fm = getFragmentManager();
            ft = fm.beginTransaction();
            OrderStartedDriver obj = new OrderStartedDriver();
            ft.add(R.id.frag_container, obj);l
            ft.commit();
        }*/

            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            NoOrderDriver obj = new NoOrderDriver();
            ft.add(R.id.frag_container, obj);
            ft.commit();

            drawerOpenButton = (ImageView) findViewById(R.id.drawerOpenButton);
            toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
            toolbarTitle.setText("Home");
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

            drawerOpenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else
                        drawerLayout.openDrawer(GravityCompat.START);
                }
            });

            orders = (TextView) findViewById(R.id.tv_requests);
            orders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toolbarTitle.setText("Order");
                    drawerLayout.closeDrawer(GravityCompat.START);
                    if (orderStatus == true) {
//                        fm = getSupportFragmentManager();
//                        ft = fm.beginTransaction();
//                        OrderStartedDriver obj = new OrderStartedDriver();
//                        ft.add(R.id.frag_container, obj);
//                        ft.commit();

                        SharedPreferences sharedPreferences4 = getApplicationContext().getSharedPreferences("saveDataDriver", Context.MODE_PRIVATE);
                        String latiofDriver = sharedPreferences4.getString("userLatitude", "0.0");
                        String longiofDriver = sharedPreferences4.getString("userLongitude", "0.0");
                        String nameofDriver = sharedPreferences4.getString("name", "unknown");
                        String numberofDriver = sharedPreferences4.getString("userNumber", "0000");
                        String orederidofDriver = sharedPreferences4.getString("order_id", "0");
                        String driveridofDriver = sharedPreferences4.getString("driver_id", "0");
                        Intent intent = new Intent(getApplicationContext(), OrderStartedDriverActivity.class);
                        intent.putExtra("userLatitude", latiofDriver);
                        intent.putExtra("userLongitude", longiofDriver);
                        intent.putExtra("name", nameofDriver);
                        intent.putExtra("userNumber", numberofDriver);
                        intent.putExtra("order_id", orederidofDriver);
                        intent.putExtra("driver_id", driveridofDriver);
                        intent.putExtra("title", "userConfirmOrder");
                        startActivity(intent);
                    } else {
                        fm = getSupportFragmentManager();
                        ft = fm.beginTransaction();
                        NoOrderDriver obj = new NoOrderDriver();
                        ft.add(R.id.frag_container, obj);
                        ft.commit();
                    }
                }
            });

            history = (TextView) findViewById(R.id.tv_history);
            history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toolbarTitle.setText("History");
                    drawerLayout.closeDrawer(GravityCompat.START);
                    fm = getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    OrdersHistory obj = new OrdersHistory();
                    ft.replace(R.id.frag_container, obj);
                    ft.commit();

                }
            });

            profile = (TextView) findViewById(R.id.tv_profile);
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toolbarTitle.setText("Profile");
                    drawerLayout.closeDrawer(GravityCompat.START);
                    fm = getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    DriverProfile obj = new DriverProfile();
                    ft.replace(R.id.frag_container, obj);
                    ft.commit();

                }
            });

            account = (TextView) findViewById(R.id.tv_account);
            account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toolbarTitle.setText("Account");
                    drawerLayout.closeDrawer(GravityCompat.START);
                    ft = fm.beginTransaction();
                    AccountSetting obj = new AccountSetting();
                    ft.replace(R.id.frag_container, obj);
                    ft.commit();
                }
            });

            logout = (TextView) findViewById(R.id.tv_logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sharedpreferences1 = getSharedPreferences("LoginStatus", MODE_PRIVATE);
                    editor = sharedpreferences1.edit();
                    editor.clear();
                    editor.commit();

                    stopService(new Intent(getApplication(), FirebaseMessagingService.class));
                    stopService(new Intent(getApplication(), LocationChangeService.class));

                    Intent intent = new Intent(getApplicationContext(), LogIn.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    Toast.makeText(getApplicationContext(), "Log Out Successfully", Toast.LENGTH_LONG).show();

                }
            });

            BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, final Intent intent) {
                    final String dataString = intent.getExtras().get("UserMessage").toString();
                    NavigationDriver.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                jsonObjectofbrodcast = new JSONObject(dataString);
//                            orderId = jsonObjectofbrodcast.get("order_id").toString();
                                titlehere = jsonObjectofbrodcast.getString("title").toString();
                                if (!titlehere.equalsIgnoreCase("userConfirmOrder")) {
                                    categoryName = jsonObjectofbrodcast.get("categoryName").toString();


                                    if (categoryName.equalsIgnoreCase("Delivery Business")) {
                                        pickLatitude = jsonObjectofbrodcast.getDouble("pickLatitude");
                                        pickLongitude = jsonObjectofbrodcast.getDouble("pickLongitude");
                                        orderDescription = jsonObjectofbrodcast.get("orderDescription").toString();
                                        pickAddress = jsonObjectofbrodcast.getString("pickAddress");
                                        orderId = jsonObjectofbrodcast.get("order_id").toString();
                                        dropLatitude = jsonObjectofbrodcast.getDouble("dropLatitude");
                                        dropLongitude = jsonObjectofbrodcast.getDouble("dropLongitude");
                                        dropAddress = jsonObjectofbrodcast.getString("dropAddress");
                                        pickDropOrderDialog(categoryName, orderDescription, pickAddress, dropAddress);

                                    } else {
//                                subCategoryName = jsonObject.get("subCategory").toString();
                                        subCategoryId = jsonObjectofbrodcast.get("subcategory_id").toString();
                                        orderAddress = jsonObjectofbrodcast.get("orderAddress").toString();
                                        userLatitude = jsonObjectofbrodcast.getDouble("Userlatitude");
                                        userLongitude = jsonObjectofbrodcast.getDouble("Userlongitude");
                                        orderDescription = jsonObjectofbrodcast.get("orderDescription").toString();
                                        orderId = jsonObjectofbrodcast.get("order_id").toString();


//                                Intent notificationIntent = new Intent(getApplicationContext(), NavigationDriver.class);//i add this
//                                notificationIntent.putExtra("subcategory_id", jsonObjectofbrodcast.getString("subcategory_id").toString());
//                                notificationIntent.putExtra("orderAddress", jsonObjectofbrodcast.getString("orderAddress").toString());
//                                notificationIntent.putExtra("Userlatitude", jsonObjectofbrodcast.getString("Userlatitude"));
//                                notificationIntent.putExtra("Userlongitude", jsonObjectofbrodcast.getString("Userlongitude"));
//                                notificationIntent.putExtra("title", jsonObjectofbrodcast.getString("title"));
//                                notificationIntent.putExtra("destination", jsonObjectofbrodcast.getString("destination"));
//                                addNotification(notificationIntent);
                                        newOrderDialog(orderDescription, categoryName, subCategoryName, orderAddress);
                                    }

                                }
                                if (titlehere.equalsIgnoreCase("userConfirmOrder")) {


                                    userNumberis = jsonObjectofbrodcast.getString("UserNumber");
                                    nameis = jsonObjectofbrodcast.getString("name");
                                    userIdHere = jsonObjectofbrodcast.getString("user_id");
                                    orderIdHere = jsonObjectofbrodcast.getString("order_id");
                                    driverIdHere = jsonObjectofbrodcast.getString("driver_id");
                                    titlehere = jsonObjectofbrodcast.getString("title");
                                    userLatitudehere = jsonObjectofbrodcast.getString("Userlatitude");
                                    userLongitudeHere = jsonObjectofbrodcast.getString("Userlongitude");
                                    showOrderAcceptedDialog(nameis, userNumberis, titlehere, userLatitudehere, userLongitudeHere);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Log.d("receiver", "Got message: " + dataString);
                }
            };

            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("driver_notifications_broadcast"));

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frag_container);
            if (currentFragment instanceof NoOrderDriver) {
                final AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setCancelable(false);
                builder.setTitle("Alert !")
                        .setMessage("Are you sure you want to exit the app?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                NavigationDriver.super.onBackPressed();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                toolbarTitle.setText("Home");
                ft = fm.beginTransaction();
                NoOrderDriver obj = new NoOrderDriver();
                ft.replace(R.id.frag_container, obj);
                ft.commit();
            }
        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onResume() {
        super.onResume();
        isInBackground = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInBackground = true;
    }

    public void orderAccepted(final String order_id, final String driver_id) {

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverSelectOrder";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Network Error" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_id", order_id);
                params.put("driver_id", driver_id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void setDriverDetail(final String userId) {
        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/editDriverDetail";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.get("status").toString();
                    if (status.equalsIgnoreCase("true")) {
                        JSONObject jsonUserData = (JSONObject) jsonResponse.get("user");

                        firstName.setText(jsonUserData.get("firstName").toString() + " ");
                        lastName.setText(jsonUserData.get("lastName").toString());
                        city.setText(jsonUserData.get("city").toString());

                        Picasso.with(getApplicationContext())
                                .load(jsonUserData.get("profilePicture").toString())
                                .placeholder(R.drawable.applogo)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .into(driverProfilePic);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void newOrderDialog(final String description, final String category, final String subCategory, final String address) {
        android.os.Handler handler = new android.os.Handler();


        final Dialog dialog = new Dialog(NavigationDriver.this);
        dialog.setContentView(R.layout.new_order_dialog);
        dialog.setCancelable(false);
        dialog.show();
        TextView cat = (TextView) dialog.findViewById(R.id.tv_cat);
        TextView subCat = (TextView) dialog.findViewById(R.id.tv_subCat);
        TextView order = (TextView) dialog.findViewById(R.id.tv_order);
        TextView add = (TextView) dialog.findViewById(R.id.tv_address);
        cat.setText(category);
//                subCat.setText(subCategory);
        order.setText(description);
        add.setText(address);

        Button accept = (Button) dialog.findViewById(R.id.bt_accept);
        Button decline = (Button) dialog.findViewById(R.id.bt_decline);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                paymentTypeDialog();
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


//        new CountDownTimer(3000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//            }
//
//            @Override
//            public void onFinish() {
//
//                dialog.dismiss();
//
//                Toast.makeText(NavigationDriver.this,"YourToast",
//                        Toast.LENGTH_SHORT).show();
//            }
//
//        }.start();

        android.os.Handler handler1 = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
            }
        }, 15000);
    }

    public void pickDropOrderDialog(String category, String description, String pickAdd, String dropAdd) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.pick_drop_order_dialog);
        dialog.setCancelable(false);
        dialog.show();

        final TextView cat = (TextView) dialog.findViewById(R.id.tv_cat);
        final TextView order = (TextView) dialog.findViewById(R.id.tv_order);
        final TextView pickAddress = (TextView) dialog.findViewById(R.id.tv_pickAddress);
        final TextView dropAddress = (TextView) dialog.findViewById(R.id.tv_dropAddress);
        cat.setText(category);
        order.setText(description);
        pickAddress.setText(pickAdd);
        dropAddress.setText(dropAdd);

        Button accept = (Button) dialog.findViewById(R.id.bt_accept);
        Button decline = (Button) dialog.findViewById(R.id.bt_decline);


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                paymentTypeDialogDelivery();
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


//        new CountDownTimer(3000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//            }
//
//            @Override
//            public void onFinish() {
//
//                dialog.dismiss();
//
//                Toast.makeText(NavigationDriver.this,"YourToast",
//                        Toast.LENGTH_SHORT).show();
//            }
//
//        }.start();
    }

    public void paymentTypeDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.payment_choice_dialog);
        dialog.setCancelable(false);
        dialog.show();

        final RadioButton perKm = (RadioButton) dialog.findViewById(R.id.rb_perKm);
        final RadioButton fixedPrice = (RadioButton) dialog.findViewById(R.id.rb_fixedPrice);
        final EditText price = (EditText) dialog.findViewById(R.id.et_fixedPrice);
        Button submit = (Button) dialog.findViewById(R.id.bt_submit);

        perKm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fixedPrice.isChecked()) {
                    fixedPrice.setChecked(false);
                    price.setVisibility(View.GONE);
                    tempPaymentType = "perKm";
                }
            }
        });

        fixedPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (perKm.isChecked()) {
                    perKm.setChecked(false);
                    price.setVisibility(View.VISIBLE);
                    tempPaymentType = "fixed";//user pressed fic price so add it into a tempPaymenttype
                    tempPaymentAmount = price.getText().toString();//tempPaymentAmount should also be enter while checking fix price
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prc = price.getText().toString();
                if (fixedPrice.isChecked()) {
                    if (prc.equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "please enter price", Toast.LENGTH_SHORT).show();
                    } else {
                        driverNotifyUser("fixed", prc);//if fix price entered then send fixed and price
                        dialog.dismiss();
                    }
                } else if (perKm.isChecked()) {
                    driverNotifyUser("perKm", "10");//id perKm then
                    dialog.dismiss();
                }
            }
        });


    }

    public void paymentTypeDialogDelivery() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_for_delivery_business);
        dialog.setCancelable(false);
        dialog.show();

        final RadioButton perKm = (RadioButton) dialog.findViewById(R.id.rb_perKm_delivery);
        final RadioButton fixedPrice = (RadioButton) dialog.findViewById(R.id.rb_fixedPrice_delivery);
        final EditText price = (EditText) dialog.findViewById(R.id.et_fixedPrice_delivery);
        Button submit = (Button) dialog.findViewById(R.id.bt_submit_delivery);

        perKm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fixedPrice.isChecked()) {
                    fixedPrice.setChecked(false);
                    price.setVisibility(View.GONE);
                    tempPaymentType = "perKm";
                }
            }
        });

        fixedPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (perKm.isChecked()) {
                    perKm.setChecked(false);
                    price.setVisibility(View.VISIBLE);
                    tempPaymentType = "fixed";//user pressed fic price so add it into a tempPaymenttype
                    tempPaymentAmount = price.getText().toString();//tempPaymentAmount should also be enter while checking fix price
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prc = price.getText().toString();
                if (fixedPrice.isChecked()) {
                    if (prc.equalsIgnoreCase("")) {
                        Toast.makeText(getApplicationContext(), "please enter price", Toast.LENGTH_SHORT).show();
                    } else {
                        driverNotifyUserDelivery("fixed", prc);//if fix price entered then send fixed and price
                        dialog.dismiss();
                    }
                } else if (perKm.isChecked()) {
                    driverNotifyUserDelivery("perKm", "10");//id perKm then
                    dialog.dismiss();
                }
            }
        });


    }


    private void showOrderAcceptedDialog(final String nameis, final String userNumberis, final String titleHere, final String userLAt, final String userLong) {


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.order_accepted_dialog);
        dialog.setCancelable(false);
        dialog.show();


        Button submit = (Button) dialog.findViewById(R.id.bt_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //permissions then oepen the map
                //put number and name in the intent


                if (ContextCompat.checkSelfPermission(NavigationDriver.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NavigationDriver.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && checkPlayServices()) {


                    ActivityCompat.requestPermissions(NavigationDriver.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            0);

                } else {
                    //yeah !we got the permission


                    dialog.dismiss();

                    //start service
                    Intent intent2 = new Intent(getApplicationContext(), LocationChangeService.class);
                    intent2.putExtra("driver_id", driverIdHere);
                    intent2.putExtra("order_id", orderIdHere);
                    startService(intent2);
                    //and go to the next activity
                    Intent intent = new Intent(getApplicationContext(), OrderStartedDriverActivity.class);
                    intent.putExtra("name", nameis);
                    intent.putExtra("order_id", orderIdHere);
                    intent.putExtra("driver_id", driverIdHere);
                    intent.putExtra("userNumber", userNumberis);
                    intent.putExtra("title", titleHere);
                    intent.putExtra("userLatitude", userLAt);
                    intent.putExtra("userLongitude", userLong);
                    intent.putExtra("user_id", userIdHere);
                    startActivity(intent);

                }
            }
        });
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    private void addNotification(Intent intent) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        int MY_NOTIFICATION_ID = counter++;
//                Intent notificationIntent = new Intent(this, NavigationDriver.class); //i commented this,it is navigation of Driver so no use of it
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        PendingIntent pendingDismissIntent =
//                PendingIntent.getBroadcast(this,
//                        MY_NOTIFICATION_ID, intentDismiss, 0);
//
//        NotificationCompat.Builder builder =
//                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
//                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
//                        .setSound(uri)
//                        .setSmallIcon(R.drawable.applogo)
//                        .setContentTitle("Wassel")
//                        .setContentText("You have new notification")
//                        .setPriority(Notification.PRIORITY_MAX)
//                        .setDefaults(Notification.DEFAULT_SOUND)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setContentIntent(contentIntent);


        Notification mynotification =
                new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("Wassel")
                        .setContentText("You have a new notification")
                        .setTicker("Notification")
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.applogo)
                        .build();


        // Add as notification
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(0, builder.build());
//        manager.cancelAll();


        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MY_NOTIFICATION_ID, mynotification);
        notificationManager.cancelAll();
    }

    public void driverNotifyUser(final String type, final String amount) {
//        if (driverLatitude == null && driverLongitude == null) {
//
//            Toast.makeText(this, "Kindly on your LOCATION or GPS", Toast.LENGTH_SHORT).show();
//
//        } else {


            String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverNotifyuser";
            final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
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
                    params.put("driver_id", userId);
                    params.put("order_id", orderId);
                    params.put("category_id", "2");
                    params.put("paymentType", type);
                    params.put("paymentAmount", amount);
                    params.put("longitude", String.valueOf(driverLongitude));
                    params.put("latitude", String.valueOf(driverLatitude));
//                params.put("paymentType", type);
//                params.put("paymentAmount", "20");
//                params.put("longitude", "11");
//                params.put("latitude", "12");
//                params.put("longitude", driverLongitude.toString());
//                params.put("latitude", driverLatitude.toString());
                    return params;
                }
            };


            stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
 //       }
    }

    public void driverNotifyUserDelivery(final String type, final String amount) {
        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverNotifyuserPickDrop";
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
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
                params.put("driver_id", userId);
                params.put("order_id", orderId);
                params.put("category_id", "2");
                params.put("paymentType", type);
                params.put("paymentAmount", amount);
                params.put("longitude", String.valueOf(driverLongitude));
                params.put("latitude", String.valueOf(driverLatitude));
//                params.put("paymentType", type);
//                params.put("paymentAmount", "20");
//                params.put("longitude", "11");
//                params.put("latitude", "12");
//                params.put("longitude", driverLongitude.toString());
//                params.put("latitude", driverLatitude.toString());
                return params;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled

                Toast.makeText(this, "KINDLY ON YOUR GPS ,PLEASE", Toast.LENGTH_SHORT).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            driverLatitude = location.getLatitude();
                            driverLongitude = location.getLongitude();

                            Toast.makeText(this, String.valueOf(driverLatitude), Toast.LENGTH_LONG).show();
                            Toast.makeText(this, String.valueOf(driverLongitude), Toast.LENGTH_LONG).show();

                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                driverLatitude = location.getLatitude();
                                driverLongitude = location.getLongitude();

                                Toast.makeText(this, String.valueOf(driverLatitude), Toast.LENGTH_LONG).show();
                                Toast.makeText(this, String.valueOf(driverLongitude), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Error", e.getMessage());
        }

        return location;
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getLocation();
    }

    @Override
    public void onLocationChanged(Location location) {

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