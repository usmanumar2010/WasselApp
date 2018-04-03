package solutions.webdealer.project.wassel.activities.history;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import solutions.webdealer.project.wassel.R;

public class SingleOrderHistory extends AppCompatActivity implements OnMapReadyCallback {


    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    SharedPreferences sharedPreferences;
    JSONObject jsonObject;
    String data;
    String userName, userNumber, orderCategory, orderPrice, orderDescription, rating, latitude, longitude, address;

    TextView typeName, name, number, category, price, order;
    RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order_history);

        sharedPreferences = getApplicationContext().getSharedPreferences("HistoryData", Context.MODE_PRIVATE);
        data = sharedPreferences.getString("SingleHistoryObject", null);

        typeName = (TextView) findViewById(R.id.tv_type);
        name = (TextView) findViewById(R.id.tv_name);
        number = (TextView) findViewById(R.id.tv_number);
        category = (TextView) findViewById(R.id.tv_category);
        price = (TextView) findViewById(R.id.tv_price);
        order = (TextView) findViewById(R.id.tv_order);

        Intent intent = getIntent();
        String type = intent.getExtras().getString("type", null);
        if (type.equalsIgnoreCase("0")) {
            try {
                jsonObject = new JSONObject(data);
                userName = jsonObject.getString("driverName");
                userNumber = jsonObject.getString("driverContactNumber");
                orderCategory = jsonObject.getString("categoryName");
                orderPrice = jsonObject.getString("price");
                orderDescription = jsonObject.getString("orderDescription");
                rating = jsonObject.get("userRating").toString();

                typeName.setText("Driver Name");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("1")) {
            try {
                jsonObject = new JSONObject(data);
                userName = jsonObject.getString("UserName");
//                userNumber = jsonObject.getString("");
                userNumber = "1234";
                orderCategory = jsonObject.getString("categoryName");
                orderPrice = jsonObject.getString("price");
                orderDescription = jsonObject.getString("orderDescription");
                rating = jsonObject.get("driverRating").toString();
                latitude = jsonObject.getString("latitude");
                longitude = jsonObject.getString("longitude");
                typeName.setText("Customer Name");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        name.setText(userName);
        number.setText(userNumber);
        category.setText(orderCategory);
        price.setText(orderPrice);
        order.setText(orderDescription);

        ratingBar = (RatingBar) findViewById(R.id.rb_rating);
        ratingBar.setIsIndicator(true);
        float rat = Float.parseFloat(rating);
        ratingBar.setRating(rat);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Order delivered location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
    }
}
