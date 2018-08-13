package com.junkersolutions.poefun.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.junkersolutions.poefun.Adapters.RecyclerAdapterLeaderboards;
import com.junkersolutions.poefun.Class.NetworkUtils;
import com.junkersolutions.poefun.Class.Preferences;
import com.junkersolutions.poefun.Dialog.DialogFilter;
import com.junkersolutions.poefun.Entities.Leaderboards;
import com.junkersolutions.poefun.Entities.Leagues;
import com.junkersolutions.poefun.R;
import com.junkersolutions.poefun.Class.Useful;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jever on 20/09/2017.
 */

public class LeaderboardsFragment extends Fragment {

    public final static int QUANTITY_ITEMS_PER_SEARCH = 50;
    private View mRootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private TextView mTexViewFilter;
    private TextView mTexViewFilterLeague;
    private TextView mTexViewFilterLabyrinthAccount;
    private String mUrlLeaderboards;
    private String mUrlChallenges;
    private List<Leaderboards> mLeaderboards;
    private String mLastFinalLaddersURL;
    private RecyclerAdapterLeaderboards.OnLoadingMore onLoadingMore;
    private static String mUrlLeague;
    public static boolean UpdatingLeagues;
    public static List<Leagues> Leagues;


    public LeaderboardsFragment() {
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
        mRootView = inflater.inflate(R.layout.fragment_leaderboards, container, false);
        mRootView.setVisibility(View.VISIBLE);

        mRecyclerView = mRootView.findViewById(R.id.recycler_view_leaderboards);
        mLayoutManager = new LinearLayoutManager(mRootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mLeaderboards = new ArrayList<Leaderboards>();
        mAdapter = new RecyclerAdapterLeaderboards(getActivity(), mLeaderboards);
        mRecyclerView.setAdapter(mAdapter);

        mTexViewFilter = mRootView.findViewById(R.id.textViewFilter);
        mTexViewFilterLeague = mRootView.findViewById(R.id.textViewFilterLeague);
        mTexViewFilterLabyrinthAccount = mRootView.findViewById(R.id.textViewFilterLabyrinthAccount);


        updateFilterText();
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.tab_leaderboards_swipe_refresh_layout);

        Preferences preferencias = new Preferences(this.getActivity().getApplicationContext());
        boolean onlyWifiConnected = false;
        try {
            onlyWifiConnected = preferencias.isUpdateTrackerWifiConnected();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!onlyWifiConnected)
            if (Useful.checkWifiConected(getActivity()))
                mSwipeRefreshLayout.setRefreshing(true);

        DatabaseReference mDataBaseChallenges;
        mDataBaseChallenges = FirebaseDatabase.getInstance().getReference().child("Challenges");
        mDataBaseChallenges.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                mUrlChallenges = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mUrlChallenges = dataSnapshot.getValue(String.class);
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

        final boolean finalOnlyWifiConnected = onlyWifiConnected;
        DatabaseReference mDataBaseLeague;
        mDataBaseLeague = FirebaseDatabase.getInstance().getReference().child("Leagues");
        mDataBaseLeague.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                mUrlLeague = dataSnapshot.getValue(String.class);
                updateLeagues(getActivity());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mUrlLeague = dataSnapshot.getValue(String.class);
                updateLeagues(getActivity());
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


        DatabaseReference mDataBaseLeaderboards;
        mDataBaseLeaderboards = FirebaseDatabase.getInstance().getReference().child("Leaderboards");
        mDataBaseLeaderboards.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                mUrlLeaderboards = dataSnapshot.getValue(String.class);
                if (!finalOnlyWifiConnected)
                    if (Useful.checkWifiConected(getActivity()))
                        updateLeaderboards();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mUrlLeaderboards = dataSnapshot.getValue(String.class);
                if (!finalOnlyWifiConnected)
                    if (Useful.checkWifiConected(getActivity()))
                        updateLeaderboards();
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


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateLeaderboards();
            }
        });
        loadingMore();
        return mRootView;
    }

    private void updateFilterText() {
        Preferences preferences = new Preferences(this.getContext());
        try {
            mTexViewFilter.setText(preferences.getLeague());
            mTexViewFilterLeague.setText(preferences.getLeague());

            if (preferences.isLabyrinth() || preferences.isByAccountName()) {
                mTexViewFilter.setVisibility(View.GONE);
                mTexViewFilterLeague.setVisibility(View.VISIBLE);
                if (preferences.isLabyrinth())
                    mTexViewFilterLabyrinthAccount.setText("Labyrinth: " + preferences.getDifficulty());
                else
                    mTexViewFilterLabyrinthAccount.setText("Account: " + preferences.getAccount());
                mTexViewFilterLabyrinthAccount.setVisibility(View.VISIBLE);
            } else {
                mTexViewFilter.setVisibility(View.VISIBLE);
                mTexViewFilterLeague.setVisibility(View.GONE);
                mTexViewFilterLabyrinthAccount.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadingMore() {
        onLoadingMore = new RecyclerAdapterLeaderboards.OnLoadingMore() {
            @Override
            public void onLoadingMore() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Gson gson = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                    .create();
                            String loadingMoreURL = mLastFinalLaddersURL + "offset=" + (mLeaderboards.size() - 1) + "&limit=" + QUANTITY_ITEMS_PER_SEARCH;
                            loadingMoreURL = loadingMoreURL.replace(" ", "%20");
                            JSONObject jsonObject = new JSONObject(NetworkUtils.getJSONFromAPI(loadingMoreURL));
                            final List<Leaderboards> newLeaderboards = gson.fromJson(jsonObject.getString("entries"), new TypeToken<List<Leaderboards>>() {
                            }.getType());
                            final int lastPosition = mLeaderboards.size();
                            mLeaderboards.remove(mLeaderboards.size() - 1);
                            for (Leaderboards newItem : newLeaderboards)
                                mLeaderboards.add(newItem);
                            mLeaderboards.add(new Leaderboards());
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mAdapter.notifyItemRangeChanged(lastPosition, newLeaderboards.size());
                                }
                            });


                        } catch (final Exception e) {

                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };
    }

    private void updateLeaderboards() {
        mSwipeRefreshLayout.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            .create();
                    Preferences preferences = new Preferences(getActivity());
                    String urlLeaderboardsFinal = mUrlLeaderboards + preferences.getLeague() + "?";
                    urlLeaderboardsFinal += preferences.isLabyrinth() ? "type=labyrinth&difficulty=" + preferences.getDifficulty() : "";
                    urlLeaderboardsFinal += preferences.isByAccountName() ? "accountName=" + preferences.getAccount() : "";
                    if (preferences.isLabyrinth() || preferences.isByAccountName())
                        urlLeaderboardsFinal += "&";
                    mLastFinalLaddersURL = urlLeaderboardsFinal;
                    urlLeaderboardsFinal += "offset=0&limit=" + QUANTITY_ITEMS_PER_SEARCH;
                    urlLeaderboardsFinal = urlLeaderboardsFinal.replace(" ", "%20");
                    JSONObject jsonObject = new JSONObject(NetworkUtils.getJSONFromAPI(urlLeaderboardsFinal));
                    mLeaderboards = gson.fromJson(jsonObject.getString("entries"), new TypeToken<List<Leaderboards>>() {
                    }.getType());
                    if (urlLeaderboardsFinal.contains("accountName") && mLeaderboards.size() > 0) {
                        List<Leaderboards> Leaderboards = new ArrayList<Leaderboards>();
                        for (Leaderboards item: mLeaderboards) {
                            List<Leaderboards> tempLeaderboards;
                            urlLeaderboardsFinal = mUrlLeaderboards + preferences.getLeague() + "?offset=" + (item.getRank() - 1) + "&limit=1";
                            urlLeaderboardsFinal = urlLeaderboardsFinal.replace(" ", "%20");
                            jsonObject = new JSONObject(NetworkUtils.getJSONFromAPI(urlLeaderboardsFinal));
                            tempLeaderboards = gson.fromJson(jsonObject.getString("entries"), new TypeToken<List<Leaderboards>>() {
                            }.getType());
                            for (Leaderboards tempItem:tempLeaderboards)
                                Leaderboards.add(tempItem);
                        }
                        mLeaderboards = Leaderboards;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mAdapter = new RecyclerAdapterLeaderboards(getActivity(), mLeaderboards, onLoadingMore);
                            mRecyclerView.setAdapter(mAdapter);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });


                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void updateLeagues(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UpdatingLeagues = true;
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            .create();
                    Preferences preferences = new Preferences(context);
                    String UrlLeague = mUrlLeague;
                    if (preferences.isShowFinishedEvents())
                        UrlLeague = UrlLeague.replace("main", "all") + "&compact=1";
                    JSONObject jsonObject = new JSONObject("{\"entries\":" + NetworkUtils.getJSONFromAPI(UrlLeague) + "}");
                    Leagues = gson.fromJson(jsonObject.getString("entries"), new TypeToken<List<Leagues>>() {
                    }.getType());
                    UpdatingLeagues = false;


                } catch (final Exception e) {
                    UpdatingLeagues = false;
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_leaderboards, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            DialogFilter.ShowFilters(this.getActivity(), new DialogFilter.OnSelectedFilter() {
                @Override
                public void onSelectedFilter() {
                    updateLeaderboards();
                    updateFilterText();
                }
            });

        }
        return super.onOptionsItemSelected(item);
    }

}