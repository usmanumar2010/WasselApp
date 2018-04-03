package solutions.webdealer.project.wassel.activities.order;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import java.util.Map;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.navigations.NavigationUser;
import solutions.webdealer.project.wassel.adapters.RestaurantListAdapter;

public class RestaurantsList extends AppCompatActivity {

    ListView restaurantList;
    ArrayList<String> restaurantNames = new ArrayList<>();
    ArrayList<RestaurantsListClass> restaurants=new ArrayList<>();
    SharedPreferences sharedPreferences;
    String userId, categoryId;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        userId = sharedPreferences.getString("UserId", null);

        sharedPreferences = getSharedPreferences("Category", MODE_PRIVATE);
        categoryId = sharedPreferences.getString("CatId", null);

        if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), R.string.networkError, Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            getRestaurantList(userId, categoryId);
        }

        restaurantList = (ListView) findViewById(R.id.lv_resturantList);

        restaurantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {

                Toast.makeText(getApplicationContext(), restaurants.get(position).getRestaurants(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SimplePlaceOrder.class);
                intent.putExtra("OtherRestaurant", restaurants.get(position).getRestaurants());
                intent.putExtra("restaurantidIs",restaurants.get(position).getId());
                startActivity(intent);
            }
        });
    }

    public void getRestaurantList(final String userId, final String categoryId) {

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/getSubCategoriesLocation";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.get("status").toString();
                    if (status.equalsIgnoreCase("true")) {
                        JSONArray jsonArray = (JSONArray) jsonObject.get("restaurants");
                        for (int i = 0; i < jsonArray.length(); i++) {
//                            restaurantNames.add((String) jsonArray.get(i));
//                            restaurantNames.add(jsonArray.getJSONObject(i).getString("name"));
                            restaurants.add(new RestaurantsListClass(jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getString("id")));

                        }
                        restaurants.add(new RestaurantsListClass("Other","0"));

                        RestaurantListAdapter restaurantListAdapter = new RestaurantListAdapter(getApplicationContext(), restaurants);
                        restaurantList.setAdapter(restaurantListAdapter);
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
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("category_id", categoryId);
                return params;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void addNotification() {
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setSound(uri)
                        .setSmallIcon(R.drawable.applogo)
                        .setContentTitle("Wassel")
                        .setContentText("You have new notification");

        Intent notificationIntent = new Intent(this, NavigationUser.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
