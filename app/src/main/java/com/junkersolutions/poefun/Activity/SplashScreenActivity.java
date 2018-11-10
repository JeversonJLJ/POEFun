package com.junkersolutions.poefun.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.junkersolutions.poefun.BuildConfig;
import com.junkersolutions.poefun.Class.Useful;
import com.junkersolutions.poefun.R;

/**
 * Created by jever on 11/07/2017.
 */

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (!Useful.firebasePersistenceCalledAlready) {
                Useful.firebasePersistenceCalledAlready = true;
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_splash_screen);
        Handler handle = new Handler();

        if (!BuildConfig.DEBUG){
            handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMainActivity();
                    finish();
                }
            }, 3000);
        }else{
            showMainActivity();
            finish();
        }


    }

    private void showMainActivity() {

        Intent intent = new Intent(SplashScreenActivity.this,
                MainActivity.class);

        startActivity(intent);

    }

}