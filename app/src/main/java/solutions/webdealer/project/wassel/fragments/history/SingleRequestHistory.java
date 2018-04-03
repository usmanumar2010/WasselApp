package solutions.webdealer.project.wassel.fragments.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import static com.facebook.FacebookSdk.getApplicationContext;

public class SingleRequestHistory extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private CameraPosition mCameraPosition;
    SupportMapFragment mapFragment;

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    SharedPreferences sharedPreferences;
    JSONObject jsonObject;
    String data;
    String name, number, category, price, rating, latitude, longitude, address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.single_request_history, container, false);

/*        sharedPreferences = getApplicationContext().getSharedPreferences("HistoryData", Context.MODE_PRIVATE);
        data = sharedPreferences.getString("SingleHistoryObject", null);
        try {
            jsonObject = new JSONObject(data);
            name = jsonObject.getString("driverName");
            number = jsonObject.getString("driverContactNumber");
            category = jsonObject.getString("categoryName");
            price = jsonObject.getString("price");
            rating = jsonObject.getString("userRating");

        } catch (JSONException e) {
            e.printStackTrace();
        }*/


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Order delivered location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
    }

    public interface OnFragmentInteractionListener {
    }

}

