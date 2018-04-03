package solutions.webdealer.project.wassel.activities.landing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.order.SimplePlaceOrder;
import solutions.webdealer.project.wassel.activities.registrations.LogIn;
import solutions.webdealer.project.wassel.services.FirebaseMessagingService;

public class SplashScreen extends AppCompatActivity {

    int SPLASH_TIME_OUT = 2000;
    private GoogleApiClient mGoogleApiClient;
    private String userId;
    private boolean loginStatuss;
    private int typeIsUser;
    SharedPreferences sharedpreferences2;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        SharedPreferences sharedpreferences1 = getSharedPreferences("LoginStatus", MODE_PRIVATE);
        loginStatuss = sharedpreferences1.getBoolean("LoginCheck", false);
        typeIsUser = sharedpreferences1.getInt("Type", 0);


//        if (loginStatuss) {
//            if (isNetworkAvailable()) {
//                if (typeIsUser == 0) {
//                    SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);  // For User Data
//                    userId = sharedPreferences.getString("UserId", null);
//                    callStatus();
//                }
//                else if(typeIsUser==1){
//                    Intent i = new Intent(SplashScreen.this, LogIn.class);
//                    startActivity(i);
//                }
//            } else {
//                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
//
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                }, SPLASH_TIME_OUT);
//            }
//        }else
//        {
//            Intent i = new Intent(SplashScreen.this, LogIn.class);
//            startActivity(i);
//        }


    }



    public void callStatus() {


//        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/checkUserStatus";
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    if (jsonObject.getBoolean("status")) {
//                        SplashScreen.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                sharedpreferences2 = getApplicationContext().getSharedPreferences("UserOrderOn", Context.MODE_PRIVATE);
//                                editor = sharedpreferences2.edit();
//                                editor.clear();
//                                editor.putBoolean("OrderStatus", true);
//                                editor.commit();
//                                Intent i = new Intent(SplashScreen.this, LogIn.class);
//                                startActivity(i);
//
//                            }
//                        });
//
//                    } else {
//                        SplashScreen.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                sharedpreferences2 = getApplicationContext().getSharedPreferences("UserOrderOn", Context.MODE_PRIVATE);
//                                editor = sharedpreferences2.edit();
//                                editor.clear();
//                                editor.putBoolean("OrderStatus", false);
//                                editor.commit();
//                                Intent i = new Intent(SplashScreen.this, LogIn.class);
//                                startActivity(i);
//                            }
//                        });
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), error.toString() + " " + "error in splash", Toast.LENGTH_SHORT).show();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("user_id", userId);
//                return params;
//            }
//        };
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.add(stringRequest);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}