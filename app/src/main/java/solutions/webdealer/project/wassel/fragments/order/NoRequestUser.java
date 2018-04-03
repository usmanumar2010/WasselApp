package solutions.webdealer.project.wassel.fragments.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.order.MainCategories;

public class NoRequestUser extends Fragment {

    Button postOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_request_user, container, false);

        postOrder = (Button) view.findViewById(R.id.bt_postOrder);
        postOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MainCategories.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public interface OnFragmentInteractionListener {
    }
}
