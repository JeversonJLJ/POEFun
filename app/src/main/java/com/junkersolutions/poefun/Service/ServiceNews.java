package com.junkersolutions.poefun.Service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.junkersolutions.poefun.Activity.MainActivity;
import com.junkersolutions.poefun.Adapters.RecyclerAdapterNews;
import com.junkersolutions.poefun.Class.Preferences;
import com.junkersolutions.poefun.R;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import java.util.ArrayList;


public class ServiceNews extends Service {

    public static boolean isServiceRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        startServiceWithNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServiceWithNotification();

        return START_STICKY;
    }

    // In case the service is deleted or crashes some how
    @Override
    public void onDestroy() {
        isServiceRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }


    void startServiceWithNotification() {
        final Context context = this;
        final boolean[] threadStarted = {false};
        final String[] url = {""};

        final Thread service = new Thread(new Runnable() {
            @Override
            public void run() {
                int lastID = 0;
                while (true) {
                    try {
                        Preferences preferencias = new Preferences(context);

                        RSSReader reader = new RSSReader();
                        RSSFeed feed = reader.load(url[0]);

                        if (!feed.getItems().get(0).getTitle().equalsIgnoreCase(preferencias.getLastNews())) {
                            preferencias.setLastNews(feed.getItems().get(0).getTitle());
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, String.valueOf(lastID))
                                    .setSmallIcon(R.drawable.ic_notification)
                                    .setColor(getResources().getColor(R.color.colorAccent))
                                    .setContentTitle(feed.getItems().get(0).getTitle())
                                    .setContentText(Html.fromHtml(feed.getItems().get(0).getDescription()).toString())
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                            Intent intent = new Intent(context, MainActivity.class);//CUSTOM ACTIVITY HERE
                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(contentIntent);
                            mBuilder.setAutoCancel(true);
                            notificationManager.notify(lastID, mBuilder.build());
                            lastID++;
                        }

                        Thread.sleep(60000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        DatabaseReference mDataBase;
        mDataBase = FirebaseDatabase.getInstance().getReference().child("News");
        mDataBase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                try {
                    url[0] = dataSnapshot.getValue(String.class);
                    if (!threadStarted[0]) {
                        threadStarted[0] = true;
                        service.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    url[0] = dataSnapshot.getValue(String.class);
                    if (!threadStarted[0]) {
                        threadStarted[0] = true;
                        service.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    void stopMyService() {
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }
}