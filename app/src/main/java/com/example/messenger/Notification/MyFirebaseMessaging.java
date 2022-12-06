package com.example.messenger.Notification;

import static com.example.messenger.Notification.NotificationExr.FCM_CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.messenger.MessageActivity;
import com.example.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.URI;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    public static final String TAG="my token";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG,"Message Call");
        Log.d(TAG,"Message Received form: "+remoteMessage.getFrom());

        if (remoteMessage.getNotification()!=null){
            String title=remoteMessage.getNotification().getTitle();
            //String title=remoteMessage.getData().get("title");
            String body=remoteMessage.getNotification().getBody();
            //String body=remoteMessage.getData().get("body");

            Log.d(TAG,"das;hfas: "+title);
            Log.d(TAG,"das;hfas: "+body);

            Notification notification=new NotificationCompat.Builder(this,FCM_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setColor(Color.BLUE)
                    .build();

            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(1002,notification);
        }

        if (remoteMessage.getData().size()>0){
            Log.d(TAG,"Message Received Data: "+remoteMessage.getData().size());

            for (String key:remoteMessage.getData().keySet()){
                Log.d(TAG,"Message Key: "+key+" Data: "+remoteMessage.getData().get(key));
            }

            Log.d(TAG,"Message Received Data: "+remoteMessage.getData().toString());
        }


//        String sented=remoteMessage.getData().get("sented");
//        String user=remoteMessage.getData().get("user");
//
//        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
//        if (firebaseUser!=null && sented.equals(firebaseUser.getUid())){
//            sendNotification(remoteMessage);
//        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int y=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, MessageActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("userid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,y,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManager noti= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int i=0;
        if (y>0){
            i=y;
        }
        noti.notify(i,builder.build());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
