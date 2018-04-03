package solutions.webdealer.project.wassel.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.prefs.PreferenceChangeEvent;

import solutions.webdealer.project.wassel.R;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PasswordReset extends Fragment {

    EditText oldPassword, newPassword, confirmPassword;
    Button changePassword;

    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setTitle("Wassel");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_password_reset, container, false);

        oldPassword = (EditText) view.findViewById(R.id.et_oldPassword);
        newPassword = (EditText) view.findViewById(R.id.et_newPassword);
        confirmPassword = (EditText) view.findViewById(R.id.et_confirmPassword);
        changePassword = (Button) view.findViewById(R.id.bt_submit);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPassword.getText().toString().equalsIgnoreCase("") || newPassword.getText().toString().equalsIgnoreCase("")
                        || confirmPassword.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Please fill empty fields", Toast.LENGTH_SHORT).show();
                } else if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
         //           resetPassword(email.getText().toString());
                }
            }
        });

        return view;
    }

    public interface OnFragmentInteractionListener {
    }

    public void resetPassword(final String userEmail) {

        String url = "http://wassel4684.cloudapp.net/api/changePassword";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    //    Toast.makeText(getApplicationContext(), "Response" + response, Toast.LENGTH_LONG).show();
                    String status = jsonResponse.get("status").toString();
                    if (status.equalsIgnoreCase("true")) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Instruction sent to your email", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "Network Error" + error, Toast.LENGTH_SHORT).show();
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
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
