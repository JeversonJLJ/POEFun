package com.junkersolutions.poefun.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.junkersolutions.poefun.Adapters.ExpandableRecyclerAdapterCurrency;
import com.junkersolutions.poefun.Adapters.RecyclerAdapterSelectedCurrency;
import com.junkersolutions.poefun.Dialog.DialogSelectCurrency;
import com.junkersolutions.poefun.Entities.Currency;
import com.junkersolutions.poefun.Entities.CurrencyGroup;
import com.junkersolutions.poefun.Entities.Leagues;
import com.junkersolutions.poefun.R;

import java.util.ArrayList;
import java.util.List;

public class BulkItemExchangeFragment extends Fragment {

    private View mRootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager mLayoutManagerWhatYouHave;
    private RecyclerView.LayoutManager mLayoutManagerWhatYouWant;
    private RecyclerView mRecyclerViewWhatYouHave;
    private RecyclerView mRecyclerViewWhatYouWant;
    private RecyclerView.Adapter mAdapterWhatYouHave;
    private RecyclerView.Adapter mAdapterWhatYouWant;
    private List<CurrencyGroup> mListGroupCurrencyWhatYouWant;
    private List<CurrencyGroup> mListGroupCurrencyWhatYouHave;
    public static List<CurrencyGroup> mListGroupCurrency;
    private ArrayAdapter<String> mSpinnerArrayAdapter;
    private Spinner mSpinnerLeagues;
    private RecyclerAdapterSelectedCurrency.OnClickItemListener onClickItemListenerWhatYouWant;
    private RecyclerAdapterSelectedCurrency.OnClickItemListener onClickItemListenerWhatYouHave;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mRootView = inflater.inflate(R.layout.fragment_bulk_item_exchange, container, false);
        mRootView.setVisibility(View.VISIBLE);

        mSwipeRefreshLayout = mRootView.findViewById(R.id.tab_bulk_item_exchange_swipe_refresh_layout);
        mSpinnerLeagues = mRootView.findViewById(R.id.spinner_league);

        mLayoutManagerWhatYouHave = new LinearLayoutManager(mRootView.getContext());
        ((LinearLayoutManager) mLayoutManagerWhatYouHave).setOrientation(LinearLayoutManager.HORIZONTAL);
        mLayoutManagerWhatYouWant = new LinearLayoutManager(mRootView.getContext());
        ((LinearLayoutManager) mLayoutManagerWhatYouWant).setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewWhatYouHave = mRootView.findViewById(R.id.recycler_view_what_you_have);
        mRecyclerViewWhatYouHave.setLayoutManager(mLayoutManagerWhatYouHave);
        mRecyclerViewWhatYouWant = mRootView.findViewById(R.id.recycler_view_what_you_want);
        mRecyclerViewWhatYouWant.setLayoutManager(mLayoutManagerWhatYouWant);

        List<Currency> selectCurrencyWhatYouHave = new ArrayList<Currency>();
        List<Currency> selectCurrencyWhatYouWant = new ArrayList<Currency>();
        selectCurrencyWhatYouHave.add(new Currency(getString(R.string.click_to_select_what_you_have), "", "", ""));
        selectCurrencyWhatYouWant.add(new Currency(getString(R.string.click_to_select_what_you_want), "", "", ""));

        mListGroupCurrencyWhatYouHave = new ArrayList<CurrencyGroup>();
        mListGroupCurrencyWhatYouWant = new ArrayList<CurrencyGroup>();


        onClickItemListenerWhatYouHave = new RecyclerAdapterSelectedCurrency.OnClickItemListener() {
            @Override
            public void onClickItemListener() {
                DialogSelectCurrency.ShowCurrency(getActivity(), getString(R.string.what_you_have), mListGroupCurrencyWhatYouHave, new DialogSelectCurrency.OnSelectedCurrency() {
                    @Override
                    public void onSelectedCurrency(List<CurrencyGroup> currencyGroupsList) {
                        List<Currency> currencyList = new ArrayList<Currency>();
                        for (CurrencyGroup currencyGroup : currencyGroupsList)
                            for (Currency currencyItem : currencyGroup.getCurrencyList())
                                if (currencyItem.isSelected())
                                    currencyList.add(currencyItem.getClone());

                        if (currencyList.size() <= 0) {
                            currencyList.add(new Currency(getString(R.string.click_to_select_what_you_have), "", "", ""));
                        }

                        mAdapterWhatYouHave = new RecyclerAdapterSelectedCurrency(getActivity(), currencyList, onClickItemListenerWhatYouHave);
                        mRecyclerViewWhatYouHave.setAdapter(mAdapterWhatYouHave);
                    }
                });
            }
        };


        onClickItemListenerWhatYouWant = new RecyclerAdapterSelectedCurrency.OnClickItemListener() {
            @Override
            public void onClickItemListener() {
                DialogSelectCurrency.ShowCurrency(getActivity(), getString(R.string.what_you_want), mListGroupCurrencyWhatYouWant, new DialogSelectCurrency.OnSelectedCurrency() {
                    @Override
                    public void onSelectedCurrency(List<CurrencyGroup> currencyGroupsList) {
                        List<Currency> currencyList = new ArrayList<Currency>();
                        for (CurrencyGroup currencyGroup : currencyGroupsList)
                            for (Currency currencyItem : currencyGroup.getCurrencyList())
                                if (currencyItem.isSelected())
                                    currencyList.add(currencyItem.getClone());

                        if (currencyList.size() <= 0) {
                            currencyList.add(new Currency(getString(R.string.click_to_select_what_you_want), "", "", ""));
                        }

                        mAdapterWhatYouWant = new RecyclerAdapterSelectedCurrency(getActivity(), currencyList, onClickItemListenerWhatYouWant);
                        mRecyclerViewWhatYouWant.setAdapter(mAdapterWhatYouWant);
                    }
                });
            }
        };


        mAdapterWhatYouHave = new RecyclerAdapterSelectedCurrency(getActivity(), selectCurrencyWhatYouHave, onClickItemListenerWhatYouHave);
        mAdapterWhatYouWant = new RecyclerAdapterSelectedCurrency(getActivity(), selectCurrencyWhatYouWant, onClickItemListenerWhatYouWant);

        mRecyclerViewWhatYouHave.setAdapter(mAdapterWhatYouHave);
        mRecyclerViewWhatYouWant.setAdapter(mAdapterWhatYouWant);


       /* ConstraintLayout constraintLayout = mRootView.findViewById(R.id.filter);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelectCurrency.ShowCurrency(getActivity(), getString(R.string.what_you_have), mListGroupCurrencyWhatYouWant, new DialogSelectCurrency.OnSelectedCurrency() {
                    @Override
                    public void onSelectedCurrency(List<Currency> currencyList) {
                        mAdapterWhatYouHave = new RecyclerAdapterSelectedCurrency(getActivity(), currencyList);
                        mRecyclerViewWhatYouHave.setAdapter(mAdapterWhatYouHave);
                    }
                });
            }
        });
        */


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadCurrencyData();
                } catch (Exception e) {
                    Toast.makeText(BulkItemExchangeFragment.this.getActivity(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

        new LoadingData().execute(true);
        loading(true);

        return mRootView;
    }


    private void loading(boolean loading) {
        mRootView.setEnabled(loading);
        mSwipeRefreshLayout.setRefreshing(loading);
    }

    private List<Currency> searchCurrencyInGroup(String groupDescription) {
        for (CurrencyGroup itemGroup : mListGroupCurrency) {
            if (itemGroup.getGroupName().equals(groupDescription))
                return itemGroup.getCurrencyList();

        }
        return null;
    }

    private void setCurrencyInGroup(String groupDescription, List<Currency> currency) {
        for (CurrencyGroup itemGroup : mListGroupCurrency) {
            if (itemGroup.getGroupName().equals(groupDescription)) {
                itemGroup.setCurrencyList(currency);
                return;
            }
        }

    }

    public void addCurrency(DataSnapshot dataSnapshot) {
        try {

            boolean existsGroup = false;

            for (CurrencyGroup groupName : mListGroupCurrency) {
                if (groupName.getGroupName().equalsIgnoreCase(dataSnapshot.child("GroupDescription").getValue(String.class))) {
                    existsGroup = true;
                    break;
                }
            }
            if (!existsGroup)
                mListGroupCurrency.add(new CurrencyGroup(dataSnapshot.child("GroupDescription").getValue(String.class)));


            List<Currency> currency = searchCurrencyInGroup(dataSnapshot.child("GroupDescription").getValue(String.class));
            if (currency == null)
                currency = new ArrayList<Currency>();


            Currency newCurrency = new Currency(dataSnapshot.child("CurrencyDescription").getValue(String.class),
                    dataSnapshot.child("GroupDescription").getValue(String.class),
                    dataSnapshot.child("id").getValue(String.class),
                    dataSnapshot.child("ImageURL").getValue(String.class));


            boolean existsSound = false;
            for (Currency item : currency) {
                if (item.getId().equals(newCurrency.getId()))
                    existsSound = true;
            }
            if (!existsSound) {
                currency.add(newCurrency);
            }

            setCurrencyInGroup(dataSnapshot.child("GroupDescription").getValue(String.class), currency);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadLeagues() {
        List<String> listLeagues = new ArrayList<String>();

        for (Leagues leagueItem : LeaderboardsFragment.Leagues)
            listLeagues.add(leagueItem.getId());

        mSpinnerArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, listLeagues);
        mSpinnerLeagues = mRootView.findViewById(R.id.spinner_league);
        mSpinnerLeagues.setAdapter(mSpinnerArrayAdapter);
        loading(false);
    }

    private void loadCurrencyData() {
        final Query dataBase = FirebaseDatabase.getInstance().getReference().child("CurrencyExchange");
        dataBase.keepSynced(false);
        dataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            mListGroupCurrency = new ArrayList<CurrencyGroup>();
                            for (DataSnapshot child : snapshot.getChildren()) {
                                addCurrency(child);
                                /*runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mStatusUpdate.setText("Loading sounds, Groups:" + mlistGroup.size() + " Sounds:" + mSoundCount);
                                    }
                                });
                                */
                            }

                            for (CurrencyGroup currencyGroup : mListGroupCurrency) {
                                mListGroupCurrencyWhatYouHave.add(currencyGroup.getClone());
                                mListGroupCurrencyWhatYouWant.add(currencyGroup.getClone());
                            }


                            dataBase.keepSynced(true);
                            Thread.sleep(1000);
                            /*runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        //mAdapterWhatYouHave = new RecyclerAdapterSelectedCurrency(BulkItemExchangeFragment.this.getActivity(), mListCurrency);
                                        //mAdapterWhatYouWant = new RecyclerAdapterSelectedCurrency(BulkItemExchangeFragment.this.getActivity(), mListCurrency);
                                        //mRecyclerViewWhatYouHave.setAdapter(mAdapterWhatYouHave);
                                        //mRecyclerViewWhatYouWant.setAdapter(mAdapterWhatYouWant);
                                        //mRecyclerViewWhatYouHave.notifyAll();
                                        //mRecyclerViewWhatYouWant.notifyAll();
                                        // updateAdapter();
                                        //showHideStatusBar(false);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            */
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class LoadingData extends AsyncTask<Boolean, Integer, Long> {
        protected Long doInBackground(Boolean... leagues) {
            long done = 0;

            while (true) {
                if (LeaderboardsFragment.Leagues == null ||
                        BulkItemExchangeFragment.mListGroupCurrency == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else
                    break;
            }

            return done;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {
            loadLeagues();
        }
    }

}
