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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.verifications.MobileVerification;
import solutions.webdealer.project.wassel.adapters.CustomSpinnerAdapter;

public class RegisterDriver extends AppCompatActivity {

    JSONObject jsonObject = new JSONObject();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText firstName, lastName, emailId, password, confirmPass, mobile;
    CheckBox checkBox;
    Button register;

    Spinner city;

    String userToken;

    ProgressDialog progressDialog;
    ArrayList<String> cityNames = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        progressDialog = new ProgressDialog(this);

        userToken = FirebaseInstanceId.getInstance().getToken(); ////// Firebase Access Token

        firstName = (EditText) findViewById(R.id.et_firstName);
        lastName = (EditText) findViewById(R.id.et_lastName);
        emailId = (EditText) findViewById(R.id.et_emailId);
        password = (EditText) findViewById(R.id.et_password);
        confirmPass = (EditText) findViewById(R.id.et_confirmPass);
        mobile = (EditText) findViewById(R.id.et_mobile);

        city = (Spinner) findViewById(R.id.sp_city);

        cityNames.add("Al medina");
        cityNames.add("Jaddah");
        cityNames.add("Jazan");
        cityNames.add("Riyadh");
//        CustomSpinnerAdapter customSpinner = new CustomSpinnerAdapter(getApplicationContext().getApplicationContext(), cityNames);
//        city.setAdapter(customSpinner);

        ArrayAdapter<String> langgAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, cityNames);
        langgAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        city.setAdapter(langgAdapter);

        checkBox = (CheckBox) findViewById(R.id.checkbox);

        register = (Button) findViewById(R.id.bt_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isNetworkAvailable()) {
                    Toast.makeText(getApplication(), "Internet not available", Toast.LENGTH_SHORT).show();
                } else if (firstName.getText().toString().equalsIgnoreCase("") || lastName.getText().toString().equalsIgnoreCase("") ||
                        emailId.getText().toString().equalsIgnoreCase("") || password.getText().toString().equalsIgnoreCase("") ||
                        confirmPass.getText().toString().equalsIgnoreCase("") || mobile.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplication(), "Please fill Empty fields", Toast.LENGTH_SHORT).show();
                } else if (isEmailValid(emailId.getText().toString()) == false) {
                    Toast.makeText(getApplication(), "Email Id not valid", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().length() < 6) {
                    Toast.makeText(getApplication(), "Password must have 6 characters", Toast.LENGTH_SHORT).show();
                } else if (!confirmPass.getText().toString().equalsIgnoreCase(password.getText().toString())) {
                    Toast.makeText(getApplication(), "Password not matches", Toast.LENGTH_SHORT).show();
                } else if (checkBox.isChecked() == false) {
                    Toast.makeText(getApplication(), "You must have agreed on our terms and conditions", Toast.LENGTH_SHORT).show();
                } else {

                    progressDialog.setTitle("Wassel");
                    progressDialog.setMessage("please wait");
                    progressDialog.show();

                    signUpDriver();

                }
            }
        });
    }

    public void signUpDriver() {
        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/signUpDriver";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jsonObject = new JSONObject(response);
                    String status = jsonObject.get("status").toString();
                    if (status.equals("true")) {
                        String userId = jsonObject.get("user_id").toString();
                        String userType = jsonObject.get("userType").toString();

                        Toast.makeText(getApplicationContext(), "SignUp Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        Intent intent = new Intent(RegisterDriver.this, MobileVerification.class);

                        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putString("UserId", userId);
                        editor.putString("UserType", userType);
                        editor.commit();

                        startActivity(intent);
                    } else if (status.equals("false")) {
                        String message = jsonObject.get("message").toString();
                        progressDialog.dismiss();
                        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    //   Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("firstName", firstName.getText().toString());
                params.put("lastName", lastName.getText().toString());
                params.put("email", emailId.getText().toString());
                params.put("password", password.getText().toString());
                params.put("mobileNumber", mobile.getText().toString());
                params.put("city", city.getSelectedItem().toString());
                params.put("fcm_token", userToken);

                return params;
            }
        };

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
