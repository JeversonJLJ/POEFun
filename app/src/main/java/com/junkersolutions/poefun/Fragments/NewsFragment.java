package com.junkersolutions.poefun.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.junkersolutions.poefun.Adapters.RecyclerAdapterNews;
import com.junkersolutions.poefun.Class.Preferences;
import com.junkersolutions.poefun.R;
import com.junkersolutions.poefun.Class.Useful;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jever on 20/09/2017.
 */

public class NewsFragment extends Fragment {


    private View mRootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<RSSItem> mRSSItemList;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_news, container, false);
        mRootView.setVisibility(View.VISIBLE);

        mRecyclerView = mRootView.findViewById(R.id.recycler_view_news);
        mLayoutManager = new LinearLayoutManager(mRootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRSSItemList = new ArrayList<RSSItem>();
        mAdapter = new RecyclerAdapterNews(getActivity(), mRSSItemList);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.tab_news_swipe_refresh_layout);

        Preferences preferencias = new Preferences(this.getActivity().getApplicationContext());
        boolean onlyWifiConnected = false;
        try {
            onlyWifiConnected = preferencias.isUpdateNewsWifiConnected();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (onlyWifiConnected)
            if (!Useful.checkWifiConected(this.getActivity()))
                return mRootView;

        DatabaseReference mDataBase;
        mDataBase = FirebaseDatabase.getInstance().getReference().child("News");
        mDataBase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                try {
                    updateNews(dataSnapshot.getValue(String.class));
                }catch(Exception e){
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    updateNews(dataSnapshot.getValue(String.class));
                }catch(Exception e){
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT);
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


        return mRootView;
    }

    private void updateNews(final String url) {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecyclerView(url);
            }
        });

        mSwipeRefreshLayout.setRefreshing(true);
        updateRecyclerView(url);
    }

    private void updateRecyclerView(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RSSReader reader = new RSSReader();
                    RSSFeed feed = reader.load(url);
                    mRSSItemList = new ArrayList<>();
                    for (RSSItem item : feed.getItems())
                        mRSSItemList.add(item);

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mAdapter = new RecyclerAdapterNews(getActivity(), mRSSItemList);
                            mRecyclerView.setAdapter(mAdapter);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });


                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}