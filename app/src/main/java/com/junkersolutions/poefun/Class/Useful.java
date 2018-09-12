package com.junkersolutions.poefun.Class;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.junkersolutions.poefun.*;
import com.junkersolutions.poefun.Activity.MainActivity;
import com.junkersolutions.poefun.Dialog.Dialog;
import com.junkersolutions.poefun.Fragments.SoundsFragment;
import com.junkersolutions.poefun.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by jever on 02/07/2017.
 */

public class Useful {
    public static final int MY_READ_EXTERNAL_STORAGE = 1;
    public static final int MY_WRITE_EXTERNAL_STORAGE = 2;
    public static final int MY_WRITE_SETTINGS = 3;
    public static final int MY_CALL_APP_DETAILS = 4;
    private static int CURRENT_DOWNLOADS;
    public static boolean firebasePersistenceCalledAlready = false;
    public static final String APP_STORAGE_PATCH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/POEFun";

    public static boolean checkWriteSettingsPermission(final Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.System.canWrite(activity)) {
                Dialog.showDialogMessage(activity, activity.getString(com.junkersolutions.poefun.R.string.write_settings_permission_message), new Dialog.OnClickOkDialogMessage() {
                    @Override
                    public void onClickOkDialogMessage() {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:com.junkersolutions.poefun"));
                        activity.startActivityForResult(intent, MY_WRITE_SETTINGS);

                    }
                });
                return false;

            }
            return true;
        }
        return true;
    }

    public static boolean checkStorageWritePermission(Activity activity) {

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE);
            return false;
        }

        return true;
    }

    public static boolean checkStorageReadPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_EXTERNAL_STORAGE);


            return false;
        }
        return true;
    }

    public static void callInternalAppDetailsScreen(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);

        activity.startActivityForResult(intent, MY_CALL_APP_DETAILS);
    }


    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        if (file.exists())
            return true;
        else
            return false;

    }

    public static void firebaseDownloadFile(final Activity activity, final StorageReference storageRef, final File file) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (CURRENT_DOWNLOADS >= 5) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    CURRENT_DOWNLOADS++;
                    storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            CURRENT_DOWNLOADS--;

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            CURRENT_DOWNLOADS--;
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    public static boolean checkWifiConected(Activity activity) {
        ConnectivityManager connManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }
        return false;
    }

    public static ArrayList pullLinks(String text) {
        ArrayList links = new ArrayList();
        try {
            String regex = "\\(?\\b(https?://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(text);
            while (m.find()) {
                String urlStr = m.group();
                if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                    urlStr = urlStr.substring(1, urlStr.length() - 1);
                }
                links.add(urlStr);
            }
        } catch (Exception e) {
            return links;
        }
        return links;
    }

    public static ArrayList pullImageLinks(String text) {
        ArrayList links = pullLinks(text);
        ArrayList imageLinks = new ArrayList();
        try {
            for (Object link : links) {
                String[] split = link.toString().split("\\.");
                if (split[split.length - 1].equalsIgnoreCase("bmp")) {
                    imageLinks.add(link.toString());
                } else if (split[split.length - 1].equalsIgnoreCase("gif")) {
                    imageLinks.add(link.toString());
                } else if (split[split.length - 1].equalsIgnoreCase("jpg")) {
                    imageLinks.add(link.toString());
                } else if (split[split.length - 1].equalsIgnoreCase("png")) {
                    imageLinks.add(link.toString());
                } else if (split[split.length - 1].equalsIgnoreCase("webp")) {
                    imageLinks.add(link.toString());
                }
            }
        } catch (Exception e) {
            return imageLinks;
        }
        return imageLinks;
    }

    public static String convertLabTime(String time) {

        int totalSeconds = Integer.parseInt(time);
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        if (hours > 0)
            return hours + " hours " + minutes + " minutes " + seconds + " seconds";
        else
            return minutes + " minutes " + seconds + " seconds";

    }


    public interface OnAsynchronousFirebaseDownloadFile {
        void onAsynchronousFirebaseDownloadFile(String filePatch, boolean sucess, Exception e);
    }

    public static void asynchronousFirebaseDownloadFile(final Activity activity, final StorageReference storageRef, final File file, final OnAsynchronousFirebaseDownloadFile asynchronousFirebaseDownloadFile) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (CURRENT_DOWNLOADS >= 5) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    CURRENT_DOWNLOADS++;
                    storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            CURRENT_DOWNLOADS--;
                            asynchronousFirebaseDownloadFile.onAsynchronousFirebaseDownloadFile(file.getAbsolutePath(), true, null);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            CURRENT_DOWNLOADS--;
                            asynchronousFirebaseDownloadFile.onAsynchronousFirebaseDownloadFile(file.getAbsolutePath(), false, exception);
                        }
                    });
                } catch (Exception e) {
                    asynchronousFirebaseDownloadFile.onAsynchronousFirebaseDownloadFile(file.getAbsolutePath(), false, e);
                }
            }
        }).start();
    }


    private void saveFile(String filePath, String fileName, byte[] file) {
        new File(filePath).mkdirs();
        File soundFile = new File(filePath, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(soundFile);
            outputStream.write(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }


    public static void createNotificationChannel(Context context) {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                List<NotificationChannel> channelList = mNotificationManager.getNotificationChannels();
                for (int i = 0; channelList != null && i < channelList.size(); i++) {
                    mNotificationManager.deleteNotificationChannel(channelList.get(i).getId());
                }

                Preferences preferences = new Preferences(context);
                NotificationChannel channel = new NotificationChannel("poe_fun_news_"+preferences.getNotificationChannelCount() , context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(context.getString(R.string.channel_description));
                channel.enableLights(true);
                channel.enableVibration(true);

                if (!preferences.getNotificationSound().isEmpty()) {
                    AudioAttributes att = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build();
                    channel.setSound(Uri.parse(preferences.getNotificationSound()), att);
                }
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                mNotificationManager.createNotificationChannel(channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


