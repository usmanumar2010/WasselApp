package solutions.webdealer.project.wassel.activities.order;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.navigations.NavigationUser;
import solutions.webdealer.project.wassel.fragments.order.NoRequestUser;
import solutions.webdealer.project.wassel.fragments.order.OrderStartedUser;
import solutions.webdealer.project.wassel.fragments.order.OrderStartedUserActivity;
import solutions.webdealer.project.wassel.services.LocationChangeService;

public class SimplePlaceOrder extends AppCompatActivity {

    private static final String MYORDERMAJORDETAIL ="myordermajordetail" ;
    EditText subCatName, order;
    CheckBox checkBox;
    Button pickLocation, postOrder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userId;
    String category_id;
    String userOrder;
    String other = null;
    Boolean orderStatus;
    StringRequest postOrderRequest;
    String restaurantIdis;

    String Longitude;
    String Latitude;
    String intentLongitude;
    String intentLatitude;
    FragmentManager fm;
    FragmentTransaction ft;

    ProgressDialog progressDialog;
    TextView address;

    int PLACE_PICKER_REQUEST = 1;
    Boolean addressStatus = false;
    public String order_id_is;
    public String paymentType_is;
    public String paymentAmount_is;
    public String driver_id_is;
    Intent intent;
    private String SAVE_LAT_LONG = "save_lat_long";
    private String driverlatitude;
    private String driverlongitude;
    private SharedPreferences sharedPreferences1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);


        intent = getIntent();

        if (intent != null) {

            if (this.getIntent().getExtras() != null) {

                if (intent.hasExtra("title")) {
                    if (intent.getStringExtra("title").equalsIgnoreCase("driverNotifyuser")) {


                        paymentAmount_is = intent.getStringExtra("paymentAmount");
                        paymentType_is = intent.getStringExtra("paymentType");
                        order_id_is = intent.getStringExtra("order_id");
                        driver_id_is = intent.getStringExtra("driver_id");
                        driverlatitude=intent.getStringExtra("driverlatitude");
                        driverlongitude=intent.getStringExtra("driverlongitude");
                        Latitude=intent.getStringExtra("orderLatitude");
                        Longitude=intent.getStringExtra("orderLongitude");
//                        Latitude=intent.getStringExtra("Userlatitude").toString();
//                        Longitude=intent.getStringExtra("Userlongitude").toString();
                        orderConfirmationDialog(paymentType_is, paymentAmount_is);


                    }


                } else if (intent.hasExtra("OtherRestaurant")) {
                    other = intent.getStringExtra("OtherRestaurant");
                    restaurantIdis = intent.getStringExtra("restaurantidIs");//restaurantidIs
                }
            }
        }


        address = (TextView) findViewById(R.id.tv_address);

        progressDialog = new ProgressDialog(this);
        fm = getSupportFragmentManager();


        sharedPreferences = getSharedPreferences("Category", Context.MODE_PRIVATE);
        category_id = sharedPreferences.getString("CatId", null);
        //   Toast.makeText(getActivity(), category_id, Toast.LENGTH_SHORT).show();

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("UserId", null);

        subCatName = (EditText) findViewById(R.id.et_subCatName);
        order = (EditText) findViewById(R.id.et_order);

//        other = getIntent().getStringExtra("OtherRestaurant");
        if (category_id.equalsIgnoreCase("1")) {
            subCatName.setVisibility(View.GONE);
        } else if (category_id.equalsIgnoreCase("2") && other.equalsIgnoreCase("other")) {
            subCatName.setHint("Enter Restaurant Name");
        } else if (category_id.equalsIgnoreCase("2")) {
            subCatName.setVisibility(View.GONE);
        } else if (category_id.equalsIgnoreCase("3")) {
            subCatName.setHint("Enter Medical Store name");
        }

        checkBox = (CheckBox) findViewById(R.id.call_me_checkbox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    order.getText().clear();
                    order.setEnabled(false);
                } else if (!checkBox.isChecked()) {
                    order.setEnabled(true);
                }
            }
        });

        pickLocation = (Button) findViewById(R.id.bt_pickLocation);
        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {

                        startActivityForResult(builder.build(SimplePlaceOrder.this), PLACE_PICKER_REQUEST);

                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                } else if (!isNetworkAvailable()) {
                    Toast.makeText(SimplePlaceOrder.this, "network error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        postOrder = (Button) findViewById(R.id.bt_postOrder);
        postOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(SimplePlaceOrder.this, "network error", Toast.LENGTH_SHORT).show();
                } else if (!checkBox.isChecked() && order.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(SimplePlaceOrder.this, "please enter your order", Toast.LENGTH_SHORT).show();
                } else if (checkBox.isChecked()) {
                    if (subCatName.getVisibility() == View.VISIBLE && subCatName.getText().toString().equalsIgnoreCase("")) {
                        if (category_id.equalsIgnoreCase("2") && other.equalsIgnoreCase("other")) {
                            Toast.makeText(SimplePlaceOrder.this, "please enter restaurant name", Toast.LENGTH_LONG).show();
                        } else if (category_id.equalsIgnoreCase("3")) {
                            Toast.makeText(SimplePlaceOrder.this, "please enter medical store name", Toast.LENGTH_LONG).show();
                        }
                    } else if (subCatName.getVisibility() == View.VISIBLE && subCatName.getText().length() > 0) {
                        if (addressStatus == false) {
                            Toast.makeText(SimplePlaceOrder.this, "please select your delivery address", Toast.LENGTH_LONG).show();
                        } else if (addressStatus == true) {
                            progressDialog.show();

//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                public void run() {
//                                    progressDialog.dismiss();
//                                    Toast.makeText(SimplePlaceOrder.this, "No driver available right now, please try later", Toast.LENGTH_LONG).show();
//                                }
//                            }, 15000);


                            userOrder = "Our Rider will call you when he get there";
                            userPostOrder(userOrder);
                        }
                    } else if (subCatName.getVisibility() == View.GONE) {
                        if (addressStatus == false) {
                            Toast.makeText(SimplePlaceOrder.this, "please select your delivery address", Toast.LENGTH_LONG).show();
                        } else if (addressStatus == true) {
                            progressDialog.show();

//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                public void run() {
//                                    progressDialog.dismiss();
//                                    Toast.makeText(SimplePlaceOrder.this, "No driver available right now, please try later", Toast.LENGTH_LONG).show();
//                                }
//                            }, 15000);

                            userOrder = "Our Rider will call you when he get there";
                            userPostOrder(userOrder);
                        }
                    }


                    //when checkbox is not checked

                } else if (!checkBox.isChecked()) {
                    //when edit text for name is available but address is restaurant name is empty
                    if (subCatName.getVisibility() == View.VISIBLE && subCatName.getText().toString().equalsIgnoreCase("")) {
                        if (category_id.equalsIgnoreCase("2") && other.equalsIgnoreCase("other")) {
                            Toast.makeText(SimplePlaceOrder.this, "please enter restaurant name", Toast.LENGTH_LONG).show();
                        } else if (category_id.equalsIgnoreCase("3")) {
                            Toast.makeText(SimplePlaceOrder.this, "please enter medical store name", Toast.LENGTH_LONG).show();
                        }
                    }
                    // if visisible and not empty then check pick place is added
                    else if (subCatName.getVisibility() == View.VISIBLE && subCatName.getText().length() > 0) {
                        if (addressStatus == false) {
                            Toast.makeText(SimplePlaceOrder.this, "please select your delivery address", Toast.LENGTH_LONG).show();
                        } else if (addressStatus == true) {
                            progressDialog.show();

//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                public void run() {
//                                    progressDialog.dismiss();
//                                    Toast.makeText(SimplePlaceOrder.this, "No driver available right now, please try later", Toast.LENGTH_LONG).show();
//                                }
//                            }, 15000);

                            userOrder = order.getText().toString();
                            userPostOrder(userOrder);
                        }
                    }
                    //
                    else if (subCatName.getVisibility() == View.GONE) {
                        if (addressStatus == false) {
                            Toast.makeText(SimplePlaceOrder.this, "please select your delivery address", Toast.LENGTH_LONG).show();
                        } else if (addressStatus == true) {
                            progressDialog.show();

//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                public void run() {
//                                    progressDialog.dismiss();
////                                    Toast.makeText(SimplePlaceOrder.this, "No driver available right now, please try later", Toast.LENGTH_LONG).show();
//                                    Toast.makeText(SimplePlaceOrder.this, "We are searching for drivers to compelete your request..", Toast.LENGTH_LONG).show();
//                                }
//                            }, 15000);

                            userOrder = order.getText().toString();
                            userPostOrder(userOrder);
                        }
                    }
                }
            }
        });

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                final String dataString = intent.getExtras().get("UserMessage").toString();
                progressDialog.dismiss();
                SimplePlaceOrder.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObject = new JSONObject(dataString);
                            if (jsonObject.getString("title").equalsIgnoreCase("driverNotifyUser")) {
                                order_id_is = jsonObject.getString("order_id");
                                paymentType_is = jsonObject.getString("paymentType");
                                paymentAmount_is = jsonObject.getString("paymentAmount");
                                driver_id_is = jsonObject.getString("driver_id");
                                driverlatitude=jsonObject.getString("latitude");
                                driverlongitude=jsonObject.getString("longitude");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        orderConfirmationDialog(paymentType_is, paymentAmount_is);
//                        addNotification();

                    }
                });
                Log.d("receiver", "Got message: " + dataString);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("user_notifications_broadcast"));
    }

    public void userPostOrder(final String order) {


        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/userPostOrder";
        RequestQueue requestQueue = Volley.newRequestQueue(SimplePlaceOrder.this);

        postOrderRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(SimplePlaceOrder.this, response.toString(), Toast.LENGTH_LONG).show();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getBoolean("status")) {

                        progressDialog.setMessage("Finding Nearby Driver");
                        progressDialog.setCancelable(false);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                            }
                        }, 15000);

                    } else {
                        Toast.makeText(SimplePlaceOrder.this, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    //   Toast.makeText(SimplePlaceOrder.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(SimplePlaceOrder.this, error.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("category_id", category_id);
                if (restaurantIdis != null) {
                    params.put("subcategory_id", restaurantIdis);
                }
                params.put("orderDescription", order);
                //user-order mai yai put ho raha as yai as dropLatitude dropLogitude
                params.put("longitude", Longitude);
                params.put("latitude", Latitude);
                params.put("address", address.getText().toString());

                return params;
            }
        };

        postOrderRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(postOrderRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                try {
                    addressStatus = false;
                    Place place = PlacePicker.getPlace(data, getApplication());
                    LatLng latLng = place.getLatLng();
                    Latitude =Double.toString(latLng.latitude) ;
                    Longitude = Double.toString(latLng.longitude);
                    String toastMsg = String.format("Place: %s", place.getAddress());
                    Toast.makeText(SimplePlaceOrder.this, toastMsg, Toast.LENGTH_LONG).show();
                    address.setText(toastMsg);


                    SharedPreferences myorderlatandlong = getApplicationContext().getSharedPreferences(SAVE_LAT_LONG, MODE_PRIVATE);
                    SharedPreferences.Editor editor = myorderlatandlong.edit();
                    editor.putString("orderslatitudeAre", Double.toString(latLng.latitude));
                    editor.putString("orderslongitudeAre", Double.toString(latLng.longitude));
                    editor.commit();

                    addressStatus = true;

                } catch (Exception e) {
                    Toast.makeText(SimplePlaceOrder.this, e.toString(), Toast.LENGTH_LONG).show();
                    Log.e("error", e.toString());
                }
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void orderConfirmationDialog(final String paymentType_is, final String paymentAmount_is) {


//        progressDialog.dismiss();


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.order_confirmation_from_user);
        dialog.setCancelable(false);
        dialog.show();

        TextView textView1 = (TextView) dialog.findViewById(R.id.tv_type);
        TextView textView2 = (TextView) dialog.findViewById(R.id.tv_price);

        textView1.setText(paymentType_is);
        textView2.setText(paymentAmount_is);

        Button confirm = (Button) dialog.findViewById(R.id.bt_confirm);

        Button decline = (Button) dialog.findViewById(R.id.bt_decline);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sharedPreferences = getApplicationContext().getSharedPreferences("driverlatlong", MODE_PRIVATE);
//                SharedPreferences.Editor editor4 = sharedPreferences.edit();
//                editor4.clear();
//                editor4.putString("driverlatitudeare", driverlatitude);
//                editor4.putString("driverlogitudeare", driverlongitude);
//                editor4.commit();

                userConfirmOrder();
                dialog.dismiss();
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDeclineOrder();
                dialog.dismiss();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
            }
        }, 25000);

    }


    //this code is calling when user press confirm key to accept driver offer
    private void userConfirmOrder() {

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/userConfirmOrder";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest test = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                try {
                    final String status = null;


                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            // Code here will run in UI thread
                            Toast.makeText(SimplePlaceOrder.this, "hello", Toast.LENGTH_SHORT).show();
//                        ft = fm.beginTransaction();
//                        OrderStartedUser obj = new OrderStartedUser();
//                        ft.add(R.id.frag_container, obj);
//                        ft.commit();

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (jsonObject.getBoolean("status")) {

                                    JSONObject data =  jsonObject.getJSONObject("result");

//                                    Intent intent2 =new Intent(getApplicationContext(), LocationChangeService.class);
//                                    startService(intent2);
                                    sharedPreferences=getApplicationContext().getSharedPreferences(MYORDERMAJORDETAIL, Context.MODE_PRIVATE);
                                    editor = sharedPreferences.edit();
                                    editor.putString("order_id_is", data.getString("order_id"));
                                    editor.putString("driver_id_is",data.getString("driver_id"));
                                    editor.commit();

                                    String nameOfdriver=data.getString("name");
                                    String orderIdHere=data.getString("order_id");
                                    String driverIdHere=data.getString("driver_id");
                                    String titleHere=data.getString("title");
                                    String driverLatHere=data.getString("Driverlatitude");
                                    String driverNumber=data.getString("driverNumber");
//                                    String driverNumber=data.getString("UserNumber");
                                    String driverLangHere=data.getString("Driverlongitude");
                                    String userlati=data.getString("Userlatitude");
                                    String userlongi=data.getString("Userlongitude");

                                    sharedPreferences1 = getApplicationContext().getSharedPreferences("StoreTheLatLang", Context.MODE_PRIVATE);
                                    editor = sharedPreferences1.edit();
                                    editor.putString("latitudeAre", driverLatHere);
                                    editor.putString("longitudeAre",driverLangHere);
                                    editor.putString("nameIs",nameOfdriver);
                                    editor.putString("usernumberis",driverNumber);
                                    editor.commit();

                                    Intent intent = new Intent(getApplicationContext(), OrderStartedUserActivity.class);
                                    intent.putExtra("name",nameOfdriver);
                                    intent.putExtra("order_id",orderIdHere);
                                    intent.putExtra("driver_id",driverIdHere);
                                    intent.putExtra("title",titleHere);
                                    intent.putExtra("driverlatitude",driverLatHere);
                                    intent.putExtra("UserNumber",driverNumber);
                                    intent.putExtra("driverlongitude",driverLangHere);
                                    intent.putExtra("Userlongitude",userlongi);
                                    intent.putExtra("Userlatitude",userlati);
                                    startActivity(intent);

                                }
                            }catch (JSONException e)
                            {
                                Toast.makeText(SimplePlaceOrder.this, "json Exception", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //}
                }catch (Exception e)
                {
                    Toast.makeText(SimplePlaceOrder.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //parmas shoud come here
                params.put("order_id", order_id_is);
                params.put("driver_id", driver_id_is);
                params.put("paymentType", paymentType_is);
                params.put("paymentAmount", paymentAmount_is);
                params.put("longitude", Longitude);
                params.put("latitude", Latitude);
                params.put("acceptStatus", "1");
                return params;
            }
        };

        test.setRetryPolicy(new

                DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(test);
    }

    //when user decline order ,if he does not accept the current rate

    private void userDeclineOrder() {
        SharedPreferences prefs = getSharedPreferences(SAVE_LAT_LONG, MODE_PRIVATE);
        final String orderslatitude = prefs.getString("orderslatitudeAre", null);
        final String orderslogitude = prefs.getString("orderslongitudeAre", null);

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/userConfirmOrder";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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
                params.put("order_id", order_id_is);
                params.put("driver_id", driver_id_is);
                params.put("paymentType", paymentType_is);
                params.put("paymentAmount", paymentAmount_is);
                params.put("acceptStatus", "0");
                params.put("longitude", orderslogitude);
                params.put("latitude", orderslatitude);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void addNotification() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(this, NavigationUser.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setSound(uri)
                        .setSmallIcon(R.drawable.applogo)
                        .setContentTitle("Wassel")
                        .setContentText("You have new notification")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentIntent(contentIntent);

//        Notification mynotification =
//                new NotificationCompat.Builder(getApplicationContext())
//                        .setContentTitle("Wassel")
//                        .setContentText("You have a new notification")
//                        .setTicker("Notification")
//                        .setWhen(System.currentTimeMillis())
//                        .setContentIntent(contentIntent)
//                        .setDefaults(Notification.DEFAULT_SOUND)
//                        .setAutoCancel(true)
//                        .setSmallIcon(R.drawable.applogo)
//                        .build();


        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
        manager.cancelAll();

//        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, mynotification);
//        notificationManager.cancelAll();

    }


}
