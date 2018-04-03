package solutions.webdealer.project.wassel.activities.verifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.navigations.NavigationDriver;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CheckApprovedStatus extends AppCompatActivity {

    Button checkStatus, uploadAgain;
    SharedPreferences sharedPreferences;
    String Id;
    String msg_1 = "your information is not approved, please upload valid information";
    String msg_2 = "you are not approved yet";
    String msg_3 = "you are now approved";
    ProgressDialog progressDialog;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_approved_status);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        Id = sharedPreferences.getString("UserId", null);

        checkStatus = (Button) findViewById(R.id.bt_checkStatus);
        uploadAgain = (Button) findViewById(R.id.bt_againUpload);

        checkStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "network error", Toast.LENGTH_SHORT).show();
                } else {
                    checkStatus(Id);
                }
            }
        });
        uploadAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPreferences = getSharedPreferences("ApprovalStatus", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.remove("Status");
                editor.putString("Status", "1");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), LisenceVehicleVerification.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void checkStatus(final String userId) {

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/checkStatus";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String status = jsonResponse.getString("status");
                    if (status.equalsIgnoreCase("true")) {
                        String message = jsonResponse.getString("message");
                        if (message.equalsIgnoreCase(msg_1)) {
                            uploadAgain.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } else if (message.equalsIgnoreCase(msg_2)) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } else if (message.equalsIgnoreCase(msg_3)) {
                            progressDialog.dismiss();

                            sharedPreferences = getSharedPreferences("ApprovalStatus", MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.remove("Status");
                            editor.putString("Status", "3");
                            editor.commit();

                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), NavigationDriver.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    } else if (status.equalsIgnoreCase("false")) {

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
                params.put("driver_id", userId);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
