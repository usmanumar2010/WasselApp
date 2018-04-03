package solutions.webdealer.project.wassel.activities.navigations;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import solutions.webdealer.project.wassel.activities.registrations.LogIn;
import solutions.webdealer.project.wassel.fragments.order.OrderStartedUser;
import solutions.webdealer.project.wassel.fragments.order.OrderStartedUserActivity;
import solutions.webdealer.project.wassel.fragments.setting.AccountSetting;
import solutions.webdealer.project.wassel.fragments.profile.DriverProfile;
import solutions.webdealer.project.wassel.fragments.history.OrdersHistory;
import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.fragments.history.SingleRequestHistory;
import solutions.webdealer.project.wassel.fragments.order.NoRequestUser;
import solutions.webdealer.project.wassel.fragments.profile.UserProfile;
import solutions.webdealer.project.wassel.services.FirebaseMessagingService;
import solutions.webdealer.project.wassel.services.LocationChangeService;

public class NavigationUser extends AppCompatActivity
        implements UserProfile.OnFragmentInteractionListener, DriverProfile.OnFragmentInteractionListener,
        OrdersHistory.OnFragmentInteractionListener, SingleRequestHistory.OnFragmentInteractionListener,
        AccountSetting.OnFragmentInteractionListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    CircularImageView userProfilePic;
    ImageView drawerOpenButton;
    TextView toolbarTitle, requests, history, profile, account, logout;
    TextView firstName, lastName, city;
    DrawerLayout drawerLayout;

    FragmentManager fm;
    FragmentTransaction ft;

    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 102;
    final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 103;

    String userId;
    Boolean orderStatus;
    String orderType, orderAmount;

    ProgressDialog progressDialog;
    Intent intent;
    private SharedPreferences sharedPreferences3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_user);

        fm = getSupportFragmentManager();


        intent = getIntent();

        if (intent != null) {

            if (this.getIntent().getExtras() != null) {

                if (intent.hasExtra("icallNavUser")) {
                    if (intent.getStringExtra("icallNavUser").equalsIgnoreCase("yeah")) {
                        ft = fm.beginTransaction();
                        OrderStartedUser obj = new OrderStartedUser();
                        ft.replace(R.id.frag_container, obj);
                        ft.commit();

                    }
                }
            }
        }

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);  // For User Data
        userId = sharedPreferences.getString("UserId", null);
        sharedPreferences3 = getSharedPreferences("UserOrderOn", Context.MODE_PRIVATE);
        orderStatus = sharedPreferences3.getBoolean("OrderStatus", false);

        if(orderStatus==true)
        {
            SharedPreferences sharedPreferences1 = getApplicationContext().getSharedPreferences("StoreTheLatLang", Context.MODE_PRIVATE);
//            editor = sharedPreferences1.edit();
             String latare=sharedPreferences1.getString("latitudeAre", "0.0");
            String longare=sharedPreferences1.getString("longitudeAre","0.0");
            String namewas=sharedPreferences1.getString("nameIs","unknown");
             String numberwas= sharedPreferences1.getString("usernumberis","0000");
            String orderlatitude=sharedPreferences1.getString("orderlatitde","0.1");
            String orderlongitude=sharedPreferences1.getString("orderlongitude","0.1");
            Intent intentss = new Intent(getApplicationContext(), OrderStartedUserActivity.class);
            intentss.putExtra("driverlatitude",latare);
            intentss.putExtra("driverlongitude",longare);
            intentss.putExtra("name",namewas);
            intentss.putExtra("UserNumber",numberwas);
            intentss.putExtra("title","userConfirmOrder");
            intentss.putExtra("Userlatitude",orderlatitude);
            intentss.putExtra("Userlongitude",orderlongitude);
            startActivity(intentss);
        }



        if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), R.string.networkError, Toast.LENGTH_SHORT).show();
        } else {
            setUserDetail(userId); // For Profile Update
        }

        sharedPreferences = getSharedPreferences("UserOrder", Context.MODE_PRIVATE); // For Order Data

        sharedPreferences = getSharedPreferences("LoginStatus", MODE_APPEND); // To check Login status
        editor = sharedPreferences.edit();
        editor.clear();
        editor.putBoolean("LoginCheck", true);
        editor.putInt("Type", 0);
        editor.commit();

        firstName = (TextView) findViewById(R.id.tv_firstName);
        lastName = (TextView) findViewById(R.id.tv_lastName);
        city = (TextView) findViewById(R.id.tv_city);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Wassel");
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't bk
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CALL_PHONE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);

                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        sharedPreferences = getSharedPreferences("IsOrderOn", Context.MODE_PRIVATE);  // To check Is any current order
        orderStatus = sharedPreferences.getBoolean("OrderStatus", false);

        ft = fm.beginTransaction();
        NoRequestUser obj = new NoRequestUser();
        ft.add(R.id.frag_container, obj);
        ft.commit();

        userProfilePic = (CircularImageView) findViewById(R.id.userProfilePic);
        drawerOpenButton = (ImageView) findViewById(R.id.drawerOpenButton);

        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.home);
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

        requests = (TextView) findViewById(R.id.tv_requests);
        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarTitle.setText(R.string.requests);
                drawerLayout.closeDrawer(GravityCompat.START);
                if (orderStatus == true) {
//                    ft = fm.beginTransaction();
//                    NoRequestUser obj = new NoRequestUser();
//                    ft.replace(R.id.frag_container, obj);
//                    ft.commit();
                    SharedPreferences sharedPreferences1 = getApplicationContext().getSharedPreferences("StoreTheLatLang", Context.MODE_PRIVATE);
//            editor = sharedPreferences1.edit();
                    String latare=sharedPreferences1.getString("latitudeAre", "0.0");
                    String longare=sharedPreferences1.getString("longitudeAre","0.0");
                    String namewas=sharedPreferences1.getString("nameIs","unknown");
                    String numberwas= sharedPreferences1.getString("usernumberis","0000");
                    String orderlatitude=sharedPreferences1.getString("orderlatitde","0.1");
                    String orderlongitude=sharedPreferences1.getString("orderlongitude","0.1");
                    Intent intentss = new Intent(getApplicationContext(), OrderStartedUserActivity.class);
                    intentss.putExtra("driverlatitude",latare);
                    intentss.putExtra("driverlongitude",longare);
                    intentss.putExtra("name",namewas);
                    intentss.putExtra("UserNumber",numberwas);
                    intentss.putExtra("Userlatitude",orderlatitude);
                    intentss.putExtra("Userlongitude",orderlongitude);
                    intentss.putExtra("title","userConfirmOrder");
                    startActivity(intentss);
                } else {
                    ft = fm.beginTransaction();
                    NoRequestUser obj = new NoRequestUser();
                    ft.replace(R.id.frag_container, obj);
                    ft.commit();
                }

/*                fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                NoRequestUser obj = new NoRequestUser();
                ft.add(R.id.frag_container, obj);
                ft.addToBackStack(null);
                ft.commit();*/

            }
        });

        history = (TextView) findViewById(R.id.tv_history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarTitle.setText(R.string.history);
                drawerLayout.closeDrawer(GravityCompat.START);
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
                toolbarTitle.setText(R.string.profile);
                drawerLayout.closeDrawer(GravityCompat.START);
                ft = fm.beginTransaction();
                UserProfile obj = new UserProfile();
                ft.replace(R.id.frag_container, obj);
                ft.commit();
            }
        });

        account = (TextView) findViewById(R.id.tv_account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarTitle.setText(R.string.account);
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
                progressDialog.show();

                sharedPreferences = getSharedPreferences("LoginStatus", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                stopService(new Intent(getApplication(), FirebaseMessagingService.class));
                stopService(new Intent(getApplication(), LocationChangeService.class));

                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Log Out Successfully", Toast.LENGTH_LONG).show();
            }
        });

//        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, final Intent intent) {
//                final String dataString = intent.getExtras().get("UserMessage").toString();
//                NavigationUser.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            JSONObject jsonObject = new JSONObject(dataString);
//                            orderType = jsonObject.getString("paymentType");
//                            orderAmount = jsonObject.getString("paymentAmount");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        orderConfirmationDialog(orderType, orderAmount);
//                        addNotification();
//                    }
//                });
//                Log.d("receiver", "Got message: " + dataString);
//
//            }
//        };

//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("user_notifications_broadcast"));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    private void setUserDetail(final String userId) {
        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/editUserDetail";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.get("status").toString();
                    if (status.equalsIgnoreCase("true")) {
                        JSONObject jsonUserData = (JSONObject) jsonResponse.get("user");

                        firstName.setText(jsonUserData.get("firstName").toString());
                        lastName.setText(jsonUserData.get("lastName").toString());
                        city.setText(jsonUserData.get("city").toString());

                        Picasso.with(getApplicationContext())
                                .load(jsonUserData.get("profilePicture").toString())
                                .placeholder(R.drawable.applogo)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .into(userProfilePic);
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

    public void orderConfirmationDialog(String type, String amount) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.order_confirmation_from_user);
        dialog.setCancelable(false);
        dialog.show();

        TextView ordType = (TextView) dialog.findViewById(R.id.tv_type);
        TextView ordAmount = (TextView) dialog.findViewById(R.id.tv_price);
        ordType.setText(type);
        ordAmount.setText(amount);
        Button confirm = (Button) dialog.findViewById(R.id.bt_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nkn = "njknkjnk";
                userConfirmOrder();
                dialog.dismiss();
            }
        });
    }

    private void userConfirmOrder() {
        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/userConfirmOrder";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener() {
            @Override
            public void onResponse(Object response) {

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
                params.put("order_id", "");
                params.put("driver_id", "");
/*                params.put("paymentType", "");
                params.put("paymentAmount", "");*/
                params.put("acceptStatus", "1");

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void userDeclineOrder() {
        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/userConfirmOrder";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener() {
            @Override
            public void onResponse(Object response) {

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
                params.put("order_id", "");
                params.put("driver_id", "");
                params.put("paymentType", "");
                params.put("paymentAmount", "");
                params.put("acceptStatus", "0");

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void addNotification() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setSound(uri)
                        .setSmallIcon(R.drawable.applogo)
                        .setContentTitle("Wassel")
                        .setContentText("You have new notification")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL);

        Intent notificationIntent = new Intent(this, NavigationUser.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
        manager.cancelAll();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frag_container);
            if (currentFragment instanceof NoRequestUser) {
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
                                NavigationUser.super.onBackPressed();
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
                NoRequestUser obj = new NoRequestUser();
                ft.replace(R.id.frag_container, obj);
                ft.commit();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
