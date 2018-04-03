package solutions.webdealer.project.wassel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.skeleton.UserDriverBoth;

public class UserDriverHistoryAdapter extends ArrayAdapter<UserDriverBoth> {

    private int type;
    private ArrayList<UserDriverBoth> arrayList;
    Context context;

    public UserDriverHistoryAdapter (int type, ArrayList<UserDriverBoth> data, Context context) {
        super(context, R.layout.history_list_row, data);
        this.type = type;
        this.arrayList = data;
        this.context=context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int postion, View convertView, ViewGroup parrent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.history_list_row, parrent, false);
        TextView typeName = (TextView) view.findViewById(R.id.tv_typeName);
        TextView name = (TextView) view.findViewById(R.id.tv_name);
        TextView number = (TextView) view.findViewById(R.id.tv_contactNo);
        TextView category = (TextView) view.findViewById(R.id.tv_category);
        TextView price = (TextView) view.findViewById(R.id.tv_price);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rb_rating);

        if (type == 0) {
            typeName.setText("Driver Name");
        } else if (type == 1) {
            typeName.setText("Customer Name");
        }

        name.setText(arrayList.get(postion).getName());
        number.setText(arrayList.get(postion).getNumber());
        category.setText(arrayList.get(postion).getCategory());
        price.setText(arrayList.get(postion).getPrice());
        ratingBar.setRating(arrayList.get(postion).getRatingStar());
        ratingBar.setIsIndicator(true);

        return view;
    }
}
