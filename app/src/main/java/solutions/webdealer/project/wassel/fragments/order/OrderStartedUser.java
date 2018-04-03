package solutions.webdealer.project.wassel.fragments.order;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.Report.Report;

public class OrderStartedUser extends Fragment {

    Button submitReport, contactDriver;
    ImageView direcArrow;

    SlidingUpPanelLayout slidingLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_started_user, container, false);

        direcArrow = (ImageView) view.findViewById(R.id.iv_direcArrow);

        contactDriver = (Button) view.findViewById(R.id.bt_contactDriver);
        contactDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "12345", null));
                startActivity(intent);
            }
        });

        submitReport = (Button) view.findViewById(R.id.bt_submitReport);
        submitReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Report.class);
                startActivity(intent);
            }
        });

        slidingLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);

        slidingLayout.setPanelSlideListener(onSlideListener());

        return view;
    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                direcArrow.setImageDrawable(getResources().getDrawable(R.drawable.downward_arrow));
            }

            @Override
            public void onPanelCollapsed(View view) {
                direcArrow.setImageDrawable(getResources().getDrawable(R.drawable.upward_arrow));
            }

            @Override
            public void onPanelExpanded(View view) {
            }

            @Override
            public void onPanelAnchored(View view) {
            }

            @Override
            public void onPanelHidden(View view) {
            }
        };
    }

    public interface OnFragmentInteractionListener {
    }

}

