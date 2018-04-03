package solutions.webdealer.project.wassel.adapters;

/**
 * Created by khurr on 4/5/2017.
 */

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.order.RestaurantsListClass;

public class RestaurantListAdapter extends BaseAdapter {
    Context context;
    ArrayList<RestaurantsListClass> resturantList;

    public RestaurantListAdapter(Context con, ArrayList<RestaurantsListClass> obj) {
        this.context = con;
        this.resturantList = obj;
    }

    @Override
    public int getCount() {
        return resturantList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int postion, View convertView, ViewGroup parrent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.resturant_item_row, parrent, false);
        TextView textView = (TextView) customView.findViewById(R.id.tv_resturantName);
        textView.setText(resturantList.get(postion).getRestaurants());

        return customView;
    }
}
