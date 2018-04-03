package solutions.webdealer.project.wassel.activities.registrations;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric.sdk.android.Fabric;
import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.verifications.MobileVerification;
import solutions.webdealer.project.wassel.adapters.CustomSpinnerAdapter;

public class RegisterUser extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth mAuth;

    JSONObject jsonObject = new JSONObject();

    private static final String TWITTER_KEY = "YonmGFoqan2JxQZ8NcHF4UN3D";
    private static final String TWITTER_SECRET = "0dEgb8EDWpu2A4BY8YpzhwiCUWpnqD1pYwieId1rq26NI6ny61";

    private LoginButton facebookLoginButton;
    private TwitterLoginButton twitterLoginButton;

    CallbackManager callbackManager;
    String TAG = "FacebookLogin";

    EditText firstName, lastName, emailId, password, confirmPass, mobile;

    CheckBox checkBox;

    ImageView iv_facebook;
    ImageView iv_twitter;

    Button register;
    AppCompatActivity activity;

    ProgressDialog progressDialog;

    Spinner city;
    ArrayList<String> cityNames = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        activity = this;

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


        checkBox = (CheckBox) findViewById(R.id.cb_checkbox);

        register = (Button) findViewById(R.id.bt_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkAvailable() == false) {
                    Toast.makeText(getApplication(), R.string.networkError, Toast.LENGTH_SHORT).show();
                } else if (firstName.getText().toString().equalsIgnoreCase("") || lastName.getText().toString().equalsIgnoreCase("") ||
                        emailId.getText().toString().equalsIgnoreCase("") || password.getText().toString().equalsIgnoreCase("") ||
                        confirmPass.getText().toString().equalsIgnoreCase("") || mobile.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplication(), R.string.emptyField, Toast.LENGTH_SHORT).show();
                } else if (isEmailValid(emailId.getText().toString()) == false) {
                    Toast.makeText(getApplication(), R.string.invalidEmail, Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().length() < 6) {
                    Toast.makeText(getApplication(), "password must have 6 characters", Toast.LENGTH_SHORT).show();
                } else if (!confirmPass.getText().toString().equalsIgnoreCase(password.getText().toString())) {
                    Toast.makeText(getApplication(), "password not matches", Toast.LENGTH_SHORT).show();
                } else if (checkBox.isChecked() == false) {
                    Toast.makeText(getApplication(), "You must have agreed on our terms and conditions", Toast.LENGTH_SHORT).show();
                } else {

                    progressDialog.setTitle("Wassel");
                    progressDialog.setMessage("Please wait");
                    progressDialog.show();

                    signUpUser();
                }
            }
        });

        // **********************************For Facebook***************************************

        iv_facebook = (ImageView) findViewById(R.id.iv_facebook);
        iv_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLoginButton.performClick();
            }
        });
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (LoginButton) findViewById(R.id.login_button);
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });


        // ******************************For Twitter***********************************

        iv_twitter = (ImageView) findViewById(R.id.iv_twitter);
        iv_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterLoginButton.performClick();
            }
        });
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()

                final TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                TwitterAuthClient authClient = new TwitterAuthClient();

                authClient.requestEmail(session, new Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        // Do something with the result, which provides the email address
                        //   String email = session
                        //   Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();

                        String email = result.data;

                        Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        emailId.setText(email);
                        //   iv_social_heading.setVisibility(View.INVISIBLE);
                        //   linear_8.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        // Do something on failure
                        Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
    }

    // *******************************************************************************

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential).addOnCompleteListener(RegisterUser.this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Name, email address, and profile photo Url

                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    Uri photoUrl = user.getPhotoUrl();

                    String[] splited = name.split("\\s+");

                    firstName.setText(splited[0]);
                    lastName.setText(splited[1]);
                    emailId.setText(email);

                    Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), photoUrl.toString(), Toast.LENGTH_SHORT).show();

                }
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
             //       Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signUpUser() {
        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/signUpUser";
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

                        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putString("UserId", userId);
                        editor.putString("UserType", userType);
                        editor.commit();

                        Intent intent = new Intent(RegisterUser.this, MobileVerification.class);
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
                //    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstName", firstName.getText().toString());
                params.put("lastName", lastName.getText().toString());
                params.put("email", emailId.getText().toString());
                params.put("password", password.getText().toString());
                params.put("mobileNumber", mobile.getText().toString());
                params.put("city", city.getSelectedItem().toString());
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
