package solutions.webdealer.project.wassel.activities.verifications;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.landing.SplashScreen;
import solutions.webdealer.project.wassel.activities.navigations.NavigationDriver;
import solutions.webdealer.project.wassel.activities.navigations.NavigationUser;
import solutions.webdealer.project.wassel.activities.registrations.LogIn;

public class MobileVerification extends AppCompatActivity {

    Button verifyNo;
    EditText code;
    String user_type;
    String user_id, receivedCode;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    TextView clickHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Wassel");
        progressDialog.setMessage("please wait");

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        user_type = sharedPreferences.getString("UserType", null);
        user_id = sharedPreferences.getString("UserId", null);

        sendSMS(String.valueOf(user_id));

        code = (EditText) findViewById(R.id.et_code);

        clickHere = (TextView) findViewById(R.id.tv_clickHere);
        clickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS(user_id);
            }
        });

        verifyNo = (Button) findViewById(R.id.bt_verifyNo);
        verifyNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (code.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplication(), "Please Enter your code", Toast.LENGTH_SHORT).show();
                } else if (user_type.equalsIgnoreCase("0")) {
                    if (code.getText().toString().equalsIgnoreCase(receivedCode)) {
                        verifyUser(user_id);
                        Intent intent = new Intent(getApplicationContext(), NavigationUser.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplication(), "Your code is wrong", Toast.LENGTH_SHORT).show();
                    }
                } else if (user_type.equalsIgnoreCase("1")) {
                    progressDialog.show();
                    if (code.getText().toString().equalsIgnoreCase(receivedCode)) {
                        verifyUser(user_id);
                        Intent intent = new Intent(getApplicationContext(), LisenceVehicleVerification.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getApplication(), "Your code is wrong", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }

    public void sendSMS(final String userId) {

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/sendSmsCode";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Boolean status = jsonResponse.getBoolean("status");
                    receivedCode = jsonResponse.getString("fourDigitCode");
                    if (status == true) {
                        Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Message not Sent", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //    Toast.makeText(getApplicationContext(), e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void verifyUser(final String userId) {

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/verifySmsStatus";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Boolean status = jsonResponse.getBoolean("status");
                    if (status == true) {
                        Toast.makeText(getApplicationContext(), "User Verified", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "User not Verified", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "JSON Exception" + e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
