package com.junkersolutions.poefun.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.junkersolutions.poefun.Adapters.ViewPagerAdapter;
import com.junkersolutions.poefun.BuildConfig;
import com.junkersolutions.poefun.Class.Useful;
import com.junkersolutions.poefun.Dialog.Dialog;
import com.junkersolutions.poefun.Fragments.BulkItemExchangeFragment;
import com.junkersolutions.poefun.Fragments.LeaderboardsFragment;
import com.junkersolutions.poefun.Fragments.NewsFragment;
import com.junkersolutions.poefun.Fragments.SoundsFragment;
import com.junkersolutions.poefun.R;
import com.junkersolutions.poefun.Service.ServiceNews;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static boolean showingPermissionMessage = false;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private MenuInflater inflater;

    ViewPager mViewPager;
    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    ViewPagerAdapter adapter;

    //Fragments
    LeaderboardsFragment leaderboardsFragment;
    NewsFragment newsFragment;
    SoundsFragment soundsFragment;
    BulkItemExchangeFragment bulkItemExchangeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Call Splash Screen
        //Intent intent = new Intent(MainActivity.this,
        //        SplashScreenActivity.class);
        //startActivity(intent);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        MobileAds.initialize(this, getString(R.string.admob_app_id));

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest;
        if (BuildConfig.DEBUG) {
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }

        adView.loadAd(adRequest);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabMain);
        allotEachTabWithEqualWidth();
        //Set start tab as Sounds tab
        //tabLayout.getTabAt(1).select();
        //viewPager.setCurrentItem(1,false);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), false);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

    }

    private void allotEachTabWithEqualWidth() {

        ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = slidingTabStrip.getChildAt(i);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tab.getLayoutParams();
            layoutParams.weight = 1;
            tab.setLayoutParams(layoutParams);
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
        Intent serviceIntent = new Intent(getBaseContext(), ServiceNews.class);
        startService(serviceIntent);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        leaderboardsFragment = new LeaderboardsFragment();
        newsFragment = new NewsFragment();
        soundsFragment = new SoundsFragment();
        bulkItemExchangeFragment = new BulkItemExchangeFragment();
        adapter.addFragment(newsFragment, "News");
        adapter.addFragment(leaderboardsFragment, "Leaderboards");
        adapter.addFragment(soundsFragment, "Sounds");
        adapter.addFragment(bulkItemExchangeFragment, "Currency");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_main) {

        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(i);
        } else if (id == R.id.nav_email) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jjjunkersolutions@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.subject_email));
            intent.putExtra(Intent.EXTRA_TEXT, "");
            try {
                startActivity(Intent.createChooser(intent, "Send Email"));
            } catch (Exception e) {

            }
        }
        else if (id == R.id.nav_privacy_policy) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://poefun-73c8e.firebaseapp.com/index.html"));
            startActivity(intent);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Useful.MY_CALL_APP_DETAILS ||
                requestCode == Useful.MY_WRITE_SETTINGS) {
            if (Useful.checkStorageWritePermission(this) &&
                    Useful.checkStorageReadPermission(this)) {
                restartApp();
                return;
            }

            if (requestCode == Useful.MY_CALL_APP_DETAILS)
                Dialog.showDialogMessage(this, getString(R.string.storage_permission_message), new Dialog.OnClickOkDialogMessage() {
                    @Override
                    public void onClickOkDialogMessage() {
                        Useful.checkStorageWritePermission(MainActivity.this);
                        Useful.checkStorageReadPermission(MainActivity.this);
                    }
                });


        }
    }


    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!showingPermissionMessage) {
            for (String permission : permissions) {
                if (requestCode == Useful.MY_WRITE_SETTINGS) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        showingPermissionMessage = true;
                        Dialog.showDialogMessage(this, getString(R.string.write_settings_permission_message), new Dialog.OnClickOkDialogMessage() {
                            @Override
                            public void onClickOkDialogMessage() {
                                Useful.checkWriteSettingsPermission(MainActivity.this);
                                showingPermissionMessage = false;
                            }
                        });
                    } else if (Useful.checkStorageReadPermission(this)) {
                        restartApp();
                        finish();
                    }
                }
                if (requestCode == Useful.MY_READ_EXTERNAL_STORAGE) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        showingPermissionMessage = true;
                        Dialog.showDialogMessage(this, getString(R.string.storage_permission_message), new Dialog.OnClickOkDialogMessage() {
                            @Override
                            public void onClickOkDialogMessage() {
                                Useful.callInternalAppDetailsScreen(MainActivity.this);
                                showingPermissionMessage = false;
                            }
                        });
                    } else if (Useful.checkStorageReadPermission(this)) {
                        restartApp();
                        finish();
                    }
                }
                if (requestCode == Useful.MY_WRITE_EXTERNAL_STORAGE) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        showingPermissionMessage = true;
                        Dialog.showDialogMessage(this, getString(R.string.storage_permission_message), new Dialog.OnClickOkDialogMessage() {
                            @Override
                            public void onClickOkDialogMessage() {
                                Useful.callInternalAppDetailsScreen(MainActivity.this);
                                showingPermissionMessage = false;
                            }
                        });
                    } else if (Useful.checkStorageWritePermission(this)) {
                        restartApp();
                        finish();
                    }
                }
            }
        }
    }

    private void restartApp() {
        Intent intent = new Intent(MainActivity.this,
                SplashScreenActivity.class);
        startActivity(intent);
    }


}