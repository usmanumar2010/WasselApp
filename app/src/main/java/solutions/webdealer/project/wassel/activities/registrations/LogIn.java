package solutions.webdealer.project.wassel.activities.registrations;

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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.navigations.NavigationDriver;
import solutions.webdealer.project.wassel.activities.navigations.NavigationUser;
import solutions.webdealer.project.wassel.activities.verifications.CheckApprovedStatus;
import solutions.webdealer.project.wassel.activities.verifications.LisenceVehicleVerification;
import solutions.webdealer.project.wassel.activities.verifications.MobileVerification;
import solutions.webdealer.project.wassel.services.FirebaseMessagingService;

public class LogIn extends AppCompatActivity {

    EditText email, password;
    TextView forgotPassword;
    Button signIn, signUp;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    Boolean loginCheck;
    int type;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");

        sharedpreferences = getSharedPreferences("LoginStatus", MODE_PRIVATE);
        loginCheck = sharedpreferences.getBoolean("LoginCheck", false);
        type = sharedpreferences.getInt("Type", 2);

        if (loginCheck == true && type == 0) {
            Intent intent = new Intent(this, NavigationUser.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (loginCheck == true && type == 1) {
            sharedpreferences = getSharedPreferences("ApprovalStatus", MODE_PRIVATE);
            String approval = sharedpreferences.getString("Status", null);
            if (approval.equalsIgnoreCase("1")) {
                Intent intent = new Intent(this, LisenceVehicleVerification.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (approval.equalsIgnoreCase("2")) {
                Intent intent = new Intent(this, CheckApprovedStatus.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (approval.equalsIgnoreCase("3")) {
                Intent intent = new Intent(this, NavigationDriver.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        } else {
            email = (EditText) findViewById(R.id.et_email);
            password = (EditText) findViewById(R.id.et_password);

            forgotPassword = (TextView) findViewById(R.id.tv_forgotPassword);
            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                    startActivity(intent);
                }
            });

            signIn = (Button) findViewById(R.id.bt_signIn);
            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isNetworkAvailable()) {
                        Toast.makeText(getApplication(), R.string.networkError, Toast.LENGTH_SHORT).show();
                    } else if (email.getText().toString().equalsIgnoreCase("") || password.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getApplication(), R.string.emptyField, Toast.LENGTH_SHORT).show();
                    } else if (!isEmailValid(email.getText().toString())) {
                        Toast.makeText(getApplication(), R.string.invalidEmail, Toast.LENGTH_SHORT).show();
                    } else {
                     //   progressDialog.setMessage(R.string.wait);
                        progressDialog.show();

                        userLogin();
                    }
                }
            });
            signUp = (Button) findViewById(R.id.bt_signUp);
            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplication(), RegisterMain.class);
                    startActivity(intent);
                }
            });
        }
    }

    public void userLogin() {
        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/logIn";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    final JSONObject jsonUserData = jsonResponse.getJSONObject("data");
                    String status = jsonResponse.get("status").toString();
                    String registrationStatus = jsonResponse.get("registrationStatus").toString();
                    if (status.equals("true")) {

                        String type = jsonUserData.getString("userType");
                        String mobileVerificationStatus = jsonUserData.getString("smsVerifyStatus");
                        String approve_status = jsonUserData.getString("approve_status");

                        sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                        editor = sharedpreferences.edit();
                        editor.putString("UserId", jsonUserData.getString("id"));
                        editor.putString("UserImage", jsonUserData.getString("profilePicture"));
                        editor.putString("UserType", jsonUserData.getString("userType"));
                        editor.putString("UserEmail", jsonUserData.getString("email"));
                        editor.commit();

                        if (type.equalsIgnoreCase("0")) {

                            startService(new Intent(getApplication(), FirebaseMessagingService.class));

                            if (mobileVerificationStatus.equalsIgnoreCase("0")) {
                                Intent intent = new Intent(getApplicationContext(), MobileVerification.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                startService(new Intent(getApplication(), FirebaseMessagingService.class));

                                Intent intent = new Intent(getApplicationContext(), NavigationUser.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.loginSuccess, Toast.LENGTH_LONG).show();
                        } else if (type.equalsIgnoreCase("1")) {

                            startService(new Intent(getApplication(), FirebaseMessagingService.class));

                            if (mobileVerificationStatus.equalsIgnoreCase("0")) {

                                Intent intent = new Intent(getApplicationContext(), MobileVerification.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else if (mobileVerificationStatus.equalsIgnoreCase("1")) {

                                sharedpreferences = getSharedPreferences("ApprovalStatus", MODE_PRIVATE);

                                if (registrationStatus.equalsIgnoreCase("0")) {

                                    editor = sharedpreferences.edit();
                                    editor.remove("Status");
                                    editor.putString("Status", "1");
                                    editor.commit();
                                    Intent intent = new Intent(getApplicationContext(), LisenceVehicleVerification.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                } else if (registrationStatus.equalsIgnoreCase("1")) {
                                    if (approve_status.equalsIgnoreCase("0")) {

                                        editor = sharedpreferences.edit();
                                        editor.remove("Status");
                                        editor.putString("Status", "2");
                                        editor.commit();

                                        Intent intent = new Intent(getApplicationContext(), CheckApprovedStatus.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else if (approve_status.equalsIgnoreCase("1")) {

                                        editor = sharedpreferences.edit();
                                        editor.remove("Status");
                                        editor.putString("Status", "3");
                                        editor.commit();

                                        Intent intent = new Intent(getApplicationContext(), NavigationDriver.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                            } else {
                                Intent intent = new Intent(getApplicationContext(), NavigationDriver.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.loginSuccess, Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getApplication(), R.string.emailPasswordIncorrect, Toast.LENGTH_LONG).show();
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
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());
                params.put("fcm_token", FirebaseInstanceId.getInstance().getToken());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
