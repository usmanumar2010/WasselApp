package solutions.webdealer.project.wassel.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import solutions.webdealer.project.wassel.MySingleton;
import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.activities.navigations.NavigationDriver;
import solutions.webdealer.project.wassel.activities.navigations.NavigationUser;
import solutions.webdealer.project.wassel.activities.order.DeliveryBusinessOrder;
import solutions.webdealer.project.wassel.activities.order.SimplePlaceOrder;
import solutions.webdealer.project.wassel.fragments.order.OrderStartedUserActivity;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    public static int counter = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
           Log.d(TAG, "From: " + remoteMessage.getFrom());

            Map<String, String> message = remoteMessage.getData();
            //   message.values();

            JSONObject jsonObject = new JSONObject(message);
            String destination = jsonObject.getString("destination");
            String titleTag=jsonObject.getString("title");

            if (destination.equalsIgnoreCase("driver") && titleTag.equalsIgnoreCase("userPostOrder") ) {
//                Intent broadCastIntent = new Intent("driver_notifications_broadcast");
//                broadCastIntent.putExtra("UserMessage", jsonObject.toString());
//                LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent);

                Intent intent = new Intent(getApplicationContext(), NavigationDriver.class);
                intent.putExtra("subcategory_id", jsonObject.getString("subcategory_id").toString());
                intent.putExtra("orderAddress", jsonObject.getString("orderAddress").toString());
                intent.putExtra("Userlatitude", jsonObject.getString("Userlatitude"));
                intent.putExtra("Userlongitude", jsonObject.getString("Userlongitude"));
                intent.putExtra("title", jsonObject.getString("title"));
                intent.putExtra("destination", jsonObject.getString("destination"));
                intent.putExtra("orderDescription",jsonObject.getString("orderDescription").toString());
                intent.putExtra("categoryName",jsonObject.getString("categoryName"));
                intent.putExtra("order_id",jsonObject.getString("order_id"));
                intent.putExtra("subCategoryName",jsonObject.getString("subCategory"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sendNotification(getApplicationContext(), intent,jsonObject,1);

            }
            else if(destination.equalsIgnoreCase("user") && titleTag.equalsIgnoreCase("driverNotifyuserPick&drop"))
            {
                Intent intent = new Intent(getApplicationContext(), DeliveryBusinessOrder.class);
                intent.putExtra("paymentType", jsonObject.getString("paymentType").toString());
                intent.putExtra("driver_id", jsonObject.getString("driver_id").toString());
                intent.putExtra("driver_mobileNumber", jsonObject.getString("driver_mobileNumber"));
                intent.putExtra("title", jsonObject.getString("title"));
                intent.putExtra("destination", jsonObject.getString("destination"));
                intent.putExtra("paymentAmount",jsonObject.getString("paymentAmount"));
                intent.putExtra("order_id",jsonObject.getString("order_id").toString());
                intent.putExtra("driver_name",jsonObject.getString("driver_name"));
                intent.putExtra("driverlatitude",jsonObject.getString("latitude"));
                intent.putExtra("driverlongitude",jsonObject.getString("longitude"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sendNotification(getApplicationContext(), intent,jsonObject,3);



            }
            else if(destination.equalsIgnoreCase("user") && titleTag.equalsIgnoreCase("driverLocation")) {
                Intent intent = new Intent(getApplicationContext(), SimplePlaceOrder.class);
                intent.putExtra("latitude", jsonObject.getString("latitude").toString());
                intent.putExtra("latitude", jsonObject.getString("longitude").toString());
                intent.putExtra("title", jsonObject.getString("title").toString());
                intent.putExtra("destination", jsonObject.getString("destination").toString());

                Intent broadCastIntent = new Intent("update_driver_location");
                broadCastIntent.putExtra("UserMessage", jsonObject.toString());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadCastIntent);

            }else if(titleTag.equalsIgnoreCase("driverReachedAtDestination") && destination.equalsIgnoreCase("user")) {
                Intent intent = new Intent(getApplicationContext(),OrderStartedUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                Intent broadCastIntent = new Intent("update_driver_location");
                broadCastIntent.putExtra("UserMessage", jsonObject.toString());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadCastIntent);

            }
            else if(destination.equalsIgnoreCase("user") &&titleTag.equalsIgnoreCase("driverRateduser") ) {


            }if (destination.equalsIgnoreCase("user")) {
//                Intent broadCastIntent = new Intent("user_notifications_broadcast");
//                broadCastIntent.putExtra("UserMessage", jsonObject.toString());
//                LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent);

                Intent intent = new Intent(getApplicationContext(), SimplePlaceOrder.class);
                intent.putExtra("paymentType", jsonObject.getString("paymentType").toString());
                intent.putExtra("driver_id", jsonObject.getString("driver_id").toString());
                intent.putExtra("driver_mobileNumber", jsonObject.getString("driver_mobileNumber"));
                intent.putExtra("title", jsonObject.getString("title"));
                intent.putExtra("destination", jsonObject.getString("destination"));
                intent.putExtra("paymentAmount",jsonObject.getString("paymentAmount"));
                intent.putExtra("order_id",jsonObject.getString("order_id").toString());
                intent.putExtra("driver_name",jsonObject.getString("driver_name"));
                intent.putExtra("driverlatitude",jsonObject.getString("latitude"));
                intent.putExtra("driverlongitude",jsonObject.getString("longitude"));
                intent.putExtra("orderLatitude",jsonObject.getString("Userlatitude"));
                intent.putExtra("orderLongitude",jsonObject.getString("Userlongitude"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sendNotification(getApplicationContext(),intent,jsonObject,2);

            }
            else if(destination.equalsIgnoreCase("driver") && titleTag.equalsIgnoreCase("pickDrop"))
            {

                Intent intent = new Intent(getApplicationContext(), NavigationDriver.class);
              //  intent.putExtra("category_id", jsonObject.getString("subcategory_id").toString());
//                intent.putExtra("orderAddress", jsonObject.getString("orderAddress").toString());
                intent.putExtra("title", jsonObject.getString("title"));
                intent.putExtra("destination", jsonObject.getString("destination"));
                intent.putExtra("orderDescription",jsonObject.getString("orderDescription").toString());
                intent.putExtra("categoryName",jsonObject.getString("categoryName"));
                intent.putExtra("order_id",jsonObject.getString("order_id"));
                intent.putExtra("dropLongitude",jsonObject.getString("dropLongitude"));
                intent.putExtra("dropLatitude",jsonObject.getString("dropLongitude"));
                intent.putExtra("pickLongitude",jsonObject.getString("pickLongitude"));
                intent.putExtra("pickLatitude",jsonObject.getString("pickLatitude"));
                intent.putExtra("dropAddress",jsonObject.getString("dropAddress"));
                intent.putExtra("pickAddress",jsonObject.getString("pickAddress"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sendNotification(getApplicationContext(), intent,jsonObject,1);

            }
            else if(destination.equalsIgnoreCase("driver") && titleTag.equalsIgnoreCase("userConfirmOrder"))
            {
                Intent intent = new Intent(getApplicationContext(), NavigationDriver.class);
                 intent.putExtra("title", jsonObject.getString("title").toString());
                intent.putExtra("name", jsonObject.getString("name").toString());
                intent.putExtra("user_id", jsonObject.getString("user_id").toString());
                intent.putExtra("order_id", jsonObject.getString("order_id").toString());
                intent.putExtra("userNumber",jsonObject.getString("UserNumber"));
                intent.putExtra("userLatitude",jsonObject.getString("Userlatitude"));
                intent.putExtra("userLongitude",jsonObject.getString("Userlongitude"));
                intent.putExtra("driver_id",jsonObject.getString("driver_id"));
                intent.putExtra("UserNumber",jsonObject.getString("UserNumber"));
                sendNotification(getApplicationContext(), intent,jsonObject,1);

            }
        } catch (Exception e) {
            Log.e("BroadCastIntentError", e.toString());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

/*            if ( *//*Check if data needs to be processed by long running job*//*  true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
             //   scheduleJob();
                Toast.makeText(getApplicationContext(), remoteMessage.getData().toString(), Toast.LENGTH_SHORT);
            } else {
                // Handle message within 10 seconds
            //    handleNow();
            }*/
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    public static void sendNotification(Context context, Intent intent,JSONObject jsonObject ,int val) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        android.support.v4.app.NotificationCompat.Builder notificationBuilder =
                new android.support.v4.app.NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.applogo)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText("you got a new notification")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent);


        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(12 /* ID of notification */, notificationBuilder.build());


        if(val==1) {
            Intent broadCastIntent = new Intent("driver_notifications_broadcast");
            broadCastIntent.putExtra("UserMessage", jsonObject.toString());
            LocalBroadcastManager.getInstance(context).sendBroadcast(broadCastIntent);
        }
        else if(val==2) {
            Intent broadCastIntent = new Intent("user_notifications_broadcast");
            broadCastIntent.putExtra("UserMessage", jsonObject.toString());
            LocalBroadcastManager.getInstance(context).sendBroadcast(broadCastIntent);
        }
        else if(val==3){
            Intent broadCastIntent = new Intent("delivery_business_filter");
            broadCastIntent.putExtra("UserMessage", jsonObject.toString());
            LocalBroadcastManager.getInstance(context).sendBroadcast(broadCastIntent);

        }



        Handler h = new Handler();
        long delayInMilliseconds = 1000;
        h.postDelayed(new Runnable() {
            public void run() {
                notificationManager.cancel(12);
                notificationManager.cancelAll();
            }
        }, delayInMilliseconds);
//-------------------------------------------------------//


//        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//
//        int MY_NOTIFICATION_ID=counter++;
//                Intent notificationIntent = new Intent(context, NavigationDriver.class); //i commented this,it is navigation of Driver so no use of it
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
////        PendingIntent pendingDismissIntent =
////                PendingIntent.getBroadcast(this,
////                        MY_NOTIFICATION_ID, intentDismiss, 0);
////
////        NotificationCompat.Builder builder =
////                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
////                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
////                        .setSound(uri)
////                        .setSmallIcon(R.drawable.applogo)
////                        .setContentTitle("Wassel")
////                        .setContentText("You have new notification")
////                        .setPriority(Notification.PRIORITY_MAX)
////                        .setDefaults(Notification.DEFAULT_SOUND)
////                        .setDefaults(Notification.DEFAULT_ALL)
////                        .setContentIntent(contentIntent);
//
//
//        Notification mynotification =
//                new NotificationCompat.Builder(context)
//                        .setContentTitle("Wassel")
//                        .setContentText("You have a new notification")
//                        .setTicker("Notification")
//                        .setWhen(System.currentTimeMillis())
//                        .setContentIntent(contentIntent)
//                        .setDefaults(Notification.DEFAULT_SOUND)
//                        .setAutoCancel(true)
//                        .setSmallIcon(R.drawable.applogo)
//                        .build();
//
//
//        // Add as notification
////        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////        manager.notify(0, builder.build());
////        manager.cancelAll();
//
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(MY_NOTIFICATION_ID, mynotification);
//        notificationManager.cancelAll();

        //-----------------


    }
}
