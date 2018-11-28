package com.junkersolutions.poefun.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.junkersolutions.poefun.Adapters.RecyclerAdapterBulkItemExchange;
import com.junkersolutions.poefun.Adapters.RecyclerAdapterSelectedCurrency;
import com.junkersolutions.poefun.Class.Preferences;
import com.junkersolutions.poefun.Dialog.Dialog;
import com.junkersolutions.poefun.Dialog.DialogSelectCurrency;
import com.junkersolutions.poefun.Entities.Currency;
import com.junkersolutions.poefun.Entities.CurrencyGroup;
import com.junkersolutions.poefun.Entities.Leagues;
import com.junkersolutions.poefun.R;
import com.junkersolutions.poefun.WebService.PoEWebService;
import com.junkersolutions.poefun.WebService.WebServiceEntitiesReturn.Result;
import com.junkersolutions.poefun.WebService.WebServiceEntitiesReturn.ResultItems;
import com.junkersolutions.poefun.WebService.WebServiceEntitiesReturn.ResultSearch;

import java.util.ArrayList;
import java.util.List;

public class BulkItemExchangeFragment extends Fragment {

    public final static int QUANTITY_ITEMS_PER_SEARCH = 10;
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
    private RecyclerView mRecyclerViewBulkItemExchange;
    private RecyclerView.LayoutManager mLayoutManagerBulkItemExchange;
    private RecyclerView.Adapter mAdapterBulkItemExchange;
    public static List<CurrencyGroup> mListGroupCurrency;
    private ArrayAdapter<String> mSpinnerArrayAdapter;
    private Spinner mSpinnerLeagues;
    private RecyclerAdapterSelectedCurrency.OnClickItemListener onClickItemListenerWhatYouWant;
    private RecyclerAdapterSelectedCurrency.OnClickItemListener onClickItemListenerWhatYouHave;
    private Button mButtonSearch;
    private List<Result> mBulkItemExchangeList;
    private ResultSearch mResultSearch;
    private RecyclerAdapterBulkItemExchange.OnLoadingMore onLoadingMore;
    private Switch mSwitch;
    private ConstraintLayout mConstraintLayoutFilter;
    private ConstraintLayout mConstraintLayoutArrow;
    private ViewGroup mRoot;
    private ImageView mImageViewArrow;
    private ImageButton mImageButtonChange;


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


        mButtonSearch = mRootView.findViewById(R.id.buttonSearch);

        mSwitch = mRootView.findViewById(R.id.switchOnline);
        mSwitch.setChecked(true);

        mConstraintLayoutFilter = mRootView.findViewById(R.id.constraintLayoutFilter);
        mConstraintLayoutArrow = mRootView.findViewById(R.id.constraintLayoutArrow);
        mConstraintLayoutArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConstraintLayoutFilter.getVisibility() == View.VISIBLE)
                    showSearch(false);
                else
                    showSearch(true);
            }
        });
        mRoot = mRootView.findViewById(R.id.constraintLayoutRoot);


        mImageViewArrow = mRootView.findViewById(R.id.imageView);

        mImageViewArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConstraintLayoutFilter.getVisibility() == View.VISIBLE)
                    showSearch(false);
                else
                    showSearch(true);
            }
        });

        mImageButtonChange  = mRootView.findViewById(R.id.imageButtonChange);
        mImageButtonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CurrencyGroup> listGroupCurrencyWhatYouHaveTemp = new ArrayList<CurrencyGroup>();
                for (CurrencyGroup currencyGroup:mListGroupCurrencyWhatYouHave) {
                    listGroupCurrencyWhatYouHaveTemp.add(currencyGroup.getClone());
                }
                mListGroupCurrencyWhatYouHave.clear();
                for (CurrencyGroup currencyGroup:mListGroupCurrencyWhatYouWant) {
                    mListGroupCurrencyWhatYouHave.add(currencyGroup.getClone());
                }
                mListGroupCurrencyWhatYouWant = listGroupCurrencyWhatYouHaveTemp;

                setAdapterWhatYouHave(mListGroupCurrencyWhatYouHave);
                setAdapterWhatYouWant(mListGroupCurrencyWhatYouWant);
                mBulkItemExchangeList.clear();
                mAdapterBulkItemExchange.notifyDataSetChanged();
            }
        });


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
                        setAdapterWhatYouHave(currencyGroupsList);
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
                        setAdapterWhatYouWant(currencyGroupsList);
                    }
                });
            }
        };


        mAdapterWhatYouHave = new RecyclerAdapterSelectedCurrency(getActivity(), selectCurrencyWhatYouHave, onClickItemListenerWhatYouHave);
        mAdapterWhatYouWant = new RecyclerAdapterSelectedCurrency(getActivity(), selectCurrencyWhatYouWant, onClickItemListenerWhatYouWant);

        mRecyclerViewWhatYouHave.setAdapter(mAdapterWhatYouHave);
        mRecyclerViewWhatYouWant.setAdapter(mAdapterWhatYouWant);


        mBulkItemExchangeList = new ArrayList<Result>();
        mRecyclerViewBulkItemExchange = mRootView.findViewById(R.id.recycler_view_bulk_item_exchange);
        mLayoutManagerBulkItemExchange = new LinearLayoutManager(mRootView.getContext());
        mRecyclerViewBulkItemExchange.setLayoutManager(mLayoutManagerBulkItemExchange);
        mAdapterBulkItemExchange = new RecyclerAdapterBulkItemExchange(getActivity(), mBulkItemExchangeList);
        mRecyclerViewBulkItemExchange.setAdapter(mAdapterBulkItemExchange);

        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences preferences = new Preferences(getContext());
                try {
                    preferences.setBulkItemExchangeLeague(mSpinnerLeagues.getSelectedItem().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                search();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search();
            }
        });

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

        loadingMore();

        return mRootView;
    }

    private void setAdapterWhatYouHave(List<CurrencyGroup> currencyGroupsList){
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

    private void setAdapterWhatYouWant(List<CurrencyGroup> currencyGroupsList){
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

    private void loadingMore() {
        onLoadingMore = new RecyclerAdapterBulkItemExchange.OnLoadingMore() {
            @Override
            public void onLoadingMore() {
                searchItems();
            }
        };
    }

    private void setLastOptionLeague() {
        Preferences preferences = new Preferences(getContext());
        try {
            mSpinnerLeagues.setSelection(mSpinnerArrayAdapter.getPosition(preferences.getBulkItemExchangeLeague()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void search() {
        showSearch(false);
        loading(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mBulkItemExchangeList.clear();
                    mBulkItemExchangeList.add(new Result());
                    List<String> have, want;
                    have = new ArrayList<String>();
                    want = new ArrayList<String>();

                    for (CurrencyGroup currencyGroup : mListGroupCurrencyWhatYouHave) {
                        for (Currency currency : currencyGroup.getCurrencyList()) {
                            if (currency.isSelected())
                                have.add(currency.getId());
                        }

                    }

                    for (CurrencyGroup currencyGroup : mListGroupCurrencyWhatYouWant) {
                        for (Currency currency : currencyGroup.getCurrencyList()) {
                            if (currency.isSelected())
                                want.add(currency.getId());
                        }

                    }


                    new PoEWebService().getBulkExchangeSearch(getContext(), mSpinnerLeagues.getSelectedItem().toString(), have, want, mSwitch.isChecked() ? "online" : "any", new PoEWebService.OnGetBulkExchangeSearch() {
                        @Override
                        public void onGetBulkExchangeSearch(ResultSearch resultSearch) {
                            mResultSearch = resultSearch;
                            if (mResultSearch.getResult().size() == 0) {
                                Dialog.showDialogMessage(getActivity(), getString(R.string.no_items_found));
                                loading(false);
                                return;
                            }
                            searchItems();
                        }

                        @Override
                        public void onGetBulkExchangeSearchError(Exception e) {
                            loading(false);
                            Dialog.showDialogMessage(getActivity(), e.getMessage());
                        }
                    });
                } catch (final Exception e) {
                    loading(false);
                    Dialog.showDialogMessage(getActivity(), e.getMessage());
                }
            }
        }).start();
    }

    private void searchItems() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> items = new ArrayList<String>();
                    if (mResultSearch.getResult().size() < QUANTITY_ITEMS_PER_SEARCH) {
                        for (int i = 0; i < mResultSearch.getResult().size(); i++)
                            items.add(mResultSearch.getResult().get(i));

                        for (int i = 0; i < mResultSearch.getResult().size(); i++)
                            mResultSearch.getResult().remove(0);

                    } else {
                        for (int i = 0; i < QUANTITY_ITEMS_PER_SEARCH; i++)
                            items.add(mResultSearch.getResult().get(i));

                        for (int i = 0; i < QUANTITY_ITEMS_PER_SEARCH; i++)
                            mResultSearch.getResult().remove(0);

                    }

                    new PoEWebService().getBulkExchangeItem(getContext(), items, mResultSearch.getId(), new PoEWebService.OnGetBulkExchangeItem() {
                        @Override
                        public void onGetBulkExchangeItem(final ResultItems resultItems) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mBulkItemExchangeList.size() == 1 && mBulkItemExchangeList.get(0).getListing() == null) {
                                        mBulkItemExchangeList = resultItems.getResult();
                                        mAdapterBulkItemExchange = new RecyclerAdapterBulkItemExchange(getActivity(), mBulkItemExchangeList, onLoadingMore);
                                        mRecyclerViewBulkItemExchange.setAdapter(mAdapterBulkItemExchange);
                                    } else {
                                        final int lastPosition = mBulkItemExchangeList.size();
                                        mBulkItemExchangeList.remove(mBulkItemExchangeList.size() - 1);
                                        mBulkItemExchangeList.addAll(resultItems.getResult());
                                        if (mBulkItemExchangeList.size() < mResultSearch.getTotal())
                                            mBulkItemExchangeList.add(new Result());
                                        mAdapterBulkItemExchange.notifyItemRangeChanged(lastPosition, resultItems.getResult().size());
                                    }
                                    loading(false);
                                }
                            });

                        }

                        @Override
                        public void onGetBulkExchangeItemError(Exception e) {
                            loading(false);
                            Dialog.showDialogMessage(getActivity(), e.getMessage());
                        }
                    });

                } catch (final Exception e) {
                    loading(false);
                    Dialog.showDialogMessage(getActivity(), e.getMessage());
                }
            }
        }).start();
    }


    private void loading(final boolean loading) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mButtonSearch.setEnabled(!loading);
                mSwipeRefreshLayout.setRefreshing(loading);
            }
        });
        // mRootView.setEnabled(loading);

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
        setLastOptionLeague();
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
                            }

                            for (int i = 0; i < mListGroupCurrency.size(); i++) {
                                if (mListGroupCurrency.get(i).getGroupName().equalsIgnoreCase("Currency")) {
                                    CurrencyGroup currencyBKP = mListGroupCurrency.get(i).getClone();
                                    mListGroupCurrency.set(i, mListGroupCurrency.get(0).getClone());
                                    mListGroupCurrency.set(0, currencyBKP.getClone());
                                    break;
                                }
                            }


                            for (CurrencyGroup currencyGroup : mListGroupCurrency) {
                                mListGroupCurrencyWhatYouHave.add(currencyGroup.getClone());
                                mListGroupCurrencyWhatYouWant.add(currencyGroup.getClone());

                            }


                            dataBase.keepSynced(true);

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

    private void showSearch(final boolean show) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TransitionManager.beginDelayedTransition(mRoot, new AutoTransition());
                if (show) {
                    mConstraintLayoutFilter.setVisibility(View.VISIBLE);
                    mImageViewArrow.setImageDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.arrow_up_float));
                } else {
                    mConstraintLayoutFilter.setVisibility(View.GONE);
                    mImageViewArrow.setImageDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.arrow_down_float));
                }
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

    public static String searchCurrencyImage(String currencyId) {
        for (CurrencyGroup currencyGroup : BulkItemExchangeFragment.mListGroupCurrency) {
            for (Currency currency : currencyGroup.getCurrencyList()) {
                if (currency.getId().equalsIgnoreCase(currencyId))
                    return currency.getImageURL();
            }
        }
        return "";
    }

    public static String searchCurrencyDescription(String currencyId) {
        for (CurrencyGroup currencyGroup : BulkItemExchangeFragment.mListGroupCurrency) {
            for (Currency currency : currencyGroup.getCurrencyList()) {
                if (currency.getId().equalsIgnoreCase(currencyId))
                    return currency.getDescription();
            }
        }
        return "";
    }

}
