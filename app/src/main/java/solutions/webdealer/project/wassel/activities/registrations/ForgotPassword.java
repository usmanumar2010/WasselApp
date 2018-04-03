package solutions.webdealer.project.wassel.activities.registrations;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ForgotPassword extends AppCompatActivity {

    EditText email;
    Button submit;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);

        email = (EditText) findViewById(R.id.et_email);
        submit = (Button) findViewById(R.id.bt_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "please enter your email", Toast.LENGTH_SHORT).show();
                } else if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), R.string.networkError, Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    forgotPassword(email.getText().toString());
                }
            }
        });

    }

    public void forgotPassword(final String userEmail) {

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/forgotPassword";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.get("status").toString();
                    if (status.equalsIgnoreCase("true")) {
                        progressDialog.dismiss();
                        finish();
                        Toast.makeText(getApplicationContext(), "instructions sent to your email", Toast.LENGTH_SHORT).show();
                    } else if (status.equalsIgnoreCase("false")) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "email does not exist", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    //    Toast.makeText(getApplicationContext(), "JSON Exception" + e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.somethingWrong, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", userEmail);
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
