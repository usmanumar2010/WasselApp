package solutions.webdealer.project.wassel.activities.order;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.navigations.NavigationUser;

public class MainCategories extends AppCompatActivity {

    LinearLayout pharma, resturant, grocery, delivery;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_categories);

        sharedPreferences = getSharedPreferences("Category", Context.MODE_PRIVATE);

        grocery = (LinearLayout) findViewById(R.id.rl_grocery);
        resturant = (LinearLayout) findViewById(R.id.rl_restaurant);
        pharma = (LinearLayout) findViewById(R.id.rl_pharma);https://www.google.com.pk/url?sa=t&rct=j&q=&esrc=s&source=web&cd=15&ved=0ahUKEwjq1u6L_KLVAhUEbbwKHQGiAmAQFgh1MA4&url=https%3A%2F%2Fwww.pcrisk.com%2Fremoval-guides%2F8835-search-yahoo-com-redirect&usg=AFQjCNH-rpml_bcX8I6lICk-HolGlKheHw&chttps://www.google.com.pk/url?sa=t&rct=j&q=&esrc=s&source=web&cd=15&ved=0ahUKEwjq1u6L_KLVAhUEbbwKHQGiAmAQFgh1MA4&url=https%3A%2F%2Fwww.pcrisk.com%2Fremoval-guides%2F8835-search-yahoo-com-redirect&usg=AFQjCNH-rpml_bcX8I6lICk-HolGlKheHw&cad=rjaad=rja
        delivery = (LinearLayout) findViewById(R.id.rl_delivery);

        grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = sharedPreferences.edit();
                editor.remove("CatId");
                editor.putString("CatId", "1");
                editor.commit();

                Intent intent = new Intent(getApplication(), SimplePlaceOrder.class);
                startActivity(intent);

            }
        });
        resturant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = sharedPreferences.edit();
                editor.remove("CatId");
                editor.putString("CatId", "2");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), RestaurantsList.class);
                startActivity(intent);

            }
        });
        pharma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = sharedPreferences.edit();
                editor.remove("CatId");
                editor.putString("CatId", "3");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), SimplePlaceOrder.class);
                startActivity(intent);
            }
        });
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = sharedPreferences.edit();
                editor.remove("CatId");
                editor.putString("CatId", "4");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), DeliveryBusinessOrder.class);
                startActivity(intent);
            }
        });
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

}
