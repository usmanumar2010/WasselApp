package solutions.webdealer.project.wassel.activities.setting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import static com.facebook.FacebookSdk.getApplicationContext;

public class ChangePassword extends AppCompatActivity {

    EditText oldPassword, newPassword, confirmPassword;
    Button changePassword;

    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    String Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        Id = sharedPreferences.getString("UserId", null);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);

        oldPassword = (EditText) findViewById(R.id.et_oldPassword);
        newPassword = (EditText) findViewById(R.id.et_newPassword);
        confirmPassword = (EditText) findViewById(R.id.et_confirmPassword);
        changePassword = (Button) findViewById(R.id.bt_changePassword);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPassword.getText().toString().equalsIgnoreCase("") || newPassword.getText().toString().equalsIgnoreCase("")
                        || confirmPassword.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), R.string.emptyField, Toast.LENGTH_SHORT).show();
                } else if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), R.string.networkError, Toast.LENGTH_SHORT).show();
                } else if (newPassword.getText().length() < 6) {
                    Toast.makeText(getApplicationContext(), "password must have 6 characters", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.getText().toString().equalsIgnoreCase(confirmPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "password not matches", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    resetPassword(Id, oldPassword.getText().toString(), newPassword.getText().toString());
                }
            }
        });
    }

    public void resetPassword(final String Id, final String oldPassword, final String newPassword) {

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/changePassword";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    String status = jsonResponse.get("status").toString();
                    if (status.equalsIgnoreCase("true")) {
                        progressDialog.dismiss();
                        finish();
                        Toast.makeText(getApplicationContext(), "password changed successfully", Toast.LENGTH_SHORT).show();
                    } else if (status.equalsIgnoreCase("false")) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "password not changed", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.somethingWrong, Toast.LENGTH_SHORT).show();
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
                params.put("user_id", Id);
                params.put("oldPass", oldPassword);
                params.put("newPass", newPassword);
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
