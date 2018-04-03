package solutions.webdealer.project.wassel.fragments.history;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.history.SingleOrderHistory;
import solutions.webdealer.project.wassel.adapters.UserDriverHistoryAdapter;
import solutions.webdealer.project.wassel.skeleton.UserDriverBoth;

import static com.facebook.FacebookSdk.getApplicationContext;

public class OrdersHistory extends Fragment {

    ArrayList<UserDriverBoth> historyList = new ArrayList<>();
    ListView history;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String Id;
    String type;
    ProgressDialog progressDialog;
    JSONArray jsonArray;
    JSONObject detail;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests_history, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading...");
        progressDialog.setCancelable(false);

        jsonArray = new JSONArray();
        detail = new JSONObject();

        sharedPreferences = getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        Id = sharedPreferences.getString("UserId", null);
        type = sharedPreferences.getString("UserType", null);

        history = (ListView) view.findViewById(R.id.lv_history);

        if (type.equalsIgnoreCase("0")) {
            if (!isNetworkAvailable()) {
                Toast.makeText(getApplicationContext(), R.string.networkError, Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.show();
                getUserHistory(Id);
            }
        } else if (type.equalsIgnoreCase("1")) {
            if (!isNetworkAvailable()) {
                Toast.makeText(getApplicationContext(), R.string.networkError, Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.show();
                getDriverHistory(Id);
            }
        }

        history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sharedPreferences = getApplicationContext().getSharedPreferences("HistoryData", Context.MODE_PRIVATE);
                try {
                    editor = sharedPreferences.edit();
                    editor.putString("SingleHistoryObject", jsonArray.get(position).toString());
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), SingleOrderHistory.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //   Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public interface OnFragmentInteractionListener {
    }

    public void getUserHistory(final String userId) {

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/userHistory";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("true")) {
                        jsonArray = jsonObject.getJSONArray("history");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            detail = (JSONObject) jsonArray.get(i);
                            float rating = (float) detail.getDouble("userRating");
                            historyList.add(new UserDriverBoth(detail.getString("driverName"), detail.getString("driverContactNumber"), detail.getString("categoryName"), detail.getString("price"), rating));
                        }
                        UserDriverHistoryAdapter adapter = new UserDriverHistoryAdapter(0, historyList, getApplicationContext());
                        history.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
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

    public void getDriverHistory(final String userId) {

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverHistory";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("true")) {
                        jsonArray = jsonObject.getJSONArray("history");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            detail = (JSONObject) jsonArray.get(i);
                            float rating = (float) detail.getDouble("driverRating");
                            historyList.add(new UserDriverBoth(detail.getString("UserName"), "1234", detail.getString("categoryName"), detail.getString("price"), rating));
                        }
                        UserDriverHistoryAdapter adapter = new UserDriverHistoryAdapter(1, historyList, getApplicationContext());
                        history.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
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
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
