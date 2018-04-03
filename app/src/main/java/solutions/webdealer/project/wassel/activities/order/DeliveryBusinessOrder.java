package solutions.webdealer.project.wassel.activities.order;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import solutions.webdealer.project.wassel.activities.navigations.NavigationDriver;
import solutions.webdealer.project.wassel.activities.navigations.NavigationUser;
import solutions.webdealer.project.wassel.fragments.order.OrderStartedUserActivity;

public class DeliveryBusinessOrder extends AppCompatActivity {

    int PLACE_PICKER_REQUEST = 1;

    EditText sourceAddress, destAddress, order;
    ImageView sourceImage, destImage;
    Button postOrder;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    String userId = null;
    int pick = 0;
    public double Latitude;
    public double Longitude;
    public double dropLatitude;
    private double dropLongitude;
    public String pickAddress;
    public String dropAddress;
    private String titlehere;
    private JSONObject jsonObjectofbrodcast;
    private String categoryName;
    private double pickLatitude;
    private double pickLongitude;
    private String orderDescription;
    private String orderId;
    private String subCategoryId;
    private Intent intent;
    private String paymentAmount_is;
    private String paymentType_is;
    private String driver_id_is;
    private String driverlatitude;
    private String driverlongitude;
    private SharedPreferences sharedPreferences1;
    private SharedPreferences.Editor editor;
    private static final String MYORDERMAJORDETAIL ="myordermajordetail" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_business);


        intent = getIntent();

        if (intent != null) {

            if (this.getIntent().getExtras() != null) {

                if (intent.hasExtra("title")) {
                    if (intent.getStringExtra("title").equalsIgnoreCase("driverNotifyuserPick&drop")) {


                        paymentAmount_is = intent.getStringExtra("paymentAmount");
                        paymentType_is = intent.getStringExtra("paymentType");
                        orderId = intent.getStringExtra("order_id");
                        driver_id_is = intent.getStringExtra("driver_id");
                        driverlatitude = intent.getStringExtra("driverlatitude");
                        driverlongitude = intent.getStringExtra("driverlongitude");
//                        Latitude=intent.getStringExtra("Userlatitude").toString();
//                        Longitude=intent.getStringExtra("Userlongitude").toString();
                        orderConfirmationDialog(paymentType_is, paymentAmount_is);


                    }
                }
            }
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Finding Nearby Driver");
        progressDialog.setCancelable(false);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("UserId", null);

        sourceAddress = (EditText) findViewById(R.id.et_sourceAddress);
        destAddress = (EditText) findViewById(R.id.et_destAddress);
        order = (EditText) findViewById(R.id.et_order);

        sourceImage = (ImageView) findViewById(R.id.iv_sourceImage);
        destImage = (ImageView) findViewById(R.id.iv_destImage);

        sourceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(DeliveryBusinessOrder.this), PLACE_PICKER_REQUEST);
                    pick = 000;
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        destImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(DeliveryBusinessOrder.this), PLACE_PICKER_REQUEST);
                    pick = 111;
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        postOrder = (Button) findViewById(R.id.bt_postOrder);
        postOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "please tell us about your order", Toast.LENGTH_SHORT).show();
                } else if (sourceAddress.getText().toString().equalsIgnoreCase("") || destAddress.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "pick and drop off addresses cannot be empty", Toast.LENGTH_SHORT).show();
                }
//                else if (sourceAddress.getText().toString().equalsIgnoreCase(destAddress.getText().toString())) {
//                    Toast.makeText(getApplicationContext(), "pick and drop off addresses cannot be same", Toast.LENGTH_SHORT).show();
//                }
                else {
                    progressDialog.show();
                    String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/pickDrop";
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //   Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String status = jsonObject.get("status").toString();
                                if (status.equalsIgnoreCase("true")) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "We are forwarding your request", Toast.LENGTH_LONG).show();
                                        }
                                    }, 15000);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("user_id", userId);
                            params.put("category_id", "4");
                            params.put("orderDescription", order.getText().toString());
                            params.put("pickAddress", pickAddress);
                            params.put("pickLongitude", Double.toString(Longitude));
                            params.put("pickLatitude", Double.toString(Latitude));
                            params.put("dropAddress", dropAddress);
                            params.put("dropLongitude", Double.toString(dropLongitude));
                            params.put("dropLatitude", Double.toString(dropLatitude));


                            return params;
                        }
                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(stringRequest);
                }
            }
        });


        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                final String dataString = intent.getExtras().get("UserMessage").toString();
                DeliveryBusinessOrder.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            jsonObjectofbrodcast = new JSONObject(dataString);
//                            orderId = jsonObjectofbrodcast.get("order_id").toString();
                            titlehere = jsonObjectofbrodcast.getString("title").toString();
                            if (titlehere.equalsIgnoreCase("driverNotifyuserPick&drop")) {
//                                categoryName = jsonObjectofbrodcast.get("categoryName").toString();

                                try {
                                    JSONObject jsonObject = new JSONObject(dataString);
                                        orderId = jsonObject.getString("order_id");
                                        paymentType_is = jsonObject.getString("paymentType");
                                        paymentAmount_is = jsonObject.getString("paymentAmount");
                                        driver_id_is = jsonObject.getString("driver_id");
                                        driverlatitude=jsonObject.getString("latitude");
                                        driverlongitude=jsonObject.getString("longitude");


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                orderConfirmationDialog(paymentType_is, paymentAmount_is);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Log.d("receiver", "Got message: " + dataString);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("delivery_business_filter"));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                try {
                    Place place = PlacePicker.getPlace(data, getApplicationContext());
                    LatLng latLng = place.getLatLng();


//                    Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
                    if (pick == 000) {
                        pickAddress = place.getAddress().toString();
                        sourceAddress.setText(place.getAddress());
                        Latitude = latLng.latitude;
                        Longitude = latLng.longitude;
                    } else if (pick == 111) {
                        dropAddress = place.getAddress().toString();
                        destAddress.setText(place.getAddress());
                        dropLatitude = latLng.latitude;
                        dropLongitude = latLng.longitude;
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    Log.e("error", e.toString());
                }
            }
        }
    }

    private void addNotification() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setSound(uri)
                        .setSmallIcon(R.drawable.applogo)
                        .setContentTitle("Wassel")
                        .setContentText("You have new notification");

        Intent notificationIntent = new Intent(this, NavigationUser.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
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
                            Toast.makeText(DeliveryBusinessOrder.this, "hello", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(DeliveryBusinessOrder.this, "json Exception", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //}
                }catch (Exception e)
                {
                    Toast.makeText(DeliveryBusinessOrder.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("order_id", orderId);
                params.put("driver_id", driver_id_is);
                params.put("paymentType", paymentType_is);
                params.put("paymentAmount", paymentAmount_is);
                params.put("longitude", String.valueOf(Longitude));
                params.put("latitude", String.valueOf(Latitude));
                params.put("acceptStatus", "1");
                return params;
            }
        };

        test.setRetryPolicy(new

                DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(test);
    }

    //when user decline order ,if he does not accept the current rate

    private void userDeclineOrder() {
//        SharedPreferences prefs = getSharedPreferences(SAVE_LAT_LONG, MODE_PRIVATE);
//        final String orderslatitude = prefs.getString("orderslatitudeAre", null);
//        final String orderslogitude = prefs.getString("orderslongitudeAre", null);

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
                params.put("order_id", orderId);
                params.put("driver_id", driver_id_is);
                params.put("paymentType", paymentType_is);
                params.put("paymentAmount", paymentAmount_is);
                params.put("acceptStatus", "0");
                params.put("longitude", String.valueOf(Latitude));//pick walai lat
                params.put("latitude", String.valueOf(Longitude));//pick lang

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

}
