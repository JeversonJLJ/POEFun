package com.junkersolutions.poefun.Dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.junkersolutions.poefun.Adapters.ExpandableRecyclerAdapterCurrency;
import com.junkersolutions.poefun.Class.ViewHolderCurrencyGroup;
import com.junkersolutions.poefun.Entities.Currency;
import com.junkersolutions.poefun.Entities.CurrencyGroup;
import com.junkersolutions.poefun.Fragments.LeaderboardsFragment;
import com.junkersolutions.poefun.R;

import java.util.ArrayList;
import java.util.List;

public class DialogSelectCurrency extends AppCompatDialog {

    private ExpandableRecyclerAdapterCurrency mAdapter;
    private RecyclerView mRecyclerViewCurrencyGroups;
    private OnSelectedCurrency onSelectedCurrency = null;
    private Button mButtonOK;
    private Button mButtonCancel;
    private Button mButtonClear;
    private List<CurrencyGroup> mCurrencyGroups;

    private Activity activity;


    public DialogSelectCurrency(Activity activity, final List<CurrencyGroup> currencyGroups, OnSelectedCurrency selectedCurrency) {
        super(activity, R.style.DialogMatchParent);
        this.activity = activity;
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        onSelectedCurrency = selectedCurrency;
        mCurrencyGroups = new ArrayList<CurrencyGroup>();
        for (CurrencyGroup currencyGroup : currencyGroups) {
            mCurrencyGroups.add(currencyGroup.getClone());
        }


    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bulk_item_exchange_select_currency);
        LeaderboardsFragment.updateLeagues(this.getContext());
        mButtonOK = findViewById(R.id.buttonCopy);
        mButtonCancel = findViewById(R.id.buttonClose);
        mButtonClear = findViewById(R.id.buttonClear);


        mButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectedCurrency != null)
                    onSelectedCurrency.onSelectedCurrency(mCurrencyGroups);
                DialogSelectCurrency.this.dismiss();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelectCurrency.this.dismiss();
            }
        });

        mButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CurrencyGroup currencyGroup : mCurrencyGroups) {
                    for (Currency currency : currencyGroup.getCurrencyList()) {
                        currency.setSelected(false);
                    }
                }
                mAdapter.notifyParentDataSetChanged(true);
                Toast.makeText(getContext(), R.string.cleared, Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerViewCurrencyGroups = findViewById(R.id.recyclerViewCurrency);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.getItemViewType(position) == ExpandableRecyclerAdapter.TYPE_PARENT) {
                    return 4;
                }
                return 1;
            }
        });
        mRecyclerViewCurrencyGroups.setLayoutManager(gridLayoutManager);

        //RecyclerAdapterSelectedCurrency recyclerAdapterSelectedCurrency = new RecyclerAdapterSelectedCurrency(activity,this.currencyGroups.get(0).getChildList());
        //mRecyclerViewCurrencyGroups.setAdapter(recyclerAdapterSelectedCurrency);


        mAdapter = new ExpandableRecyclerAdapterCurrency(activity, this.mCurrencyGroups);
        mRecyclerViewCurrencyGroups.setAdapter(mAdapter);

        boolean haveSelected = false;
        for (CurrencyGroup currencyGroup : mCurrencyGroups) {
            boolean selected = false;
            for (Currency currency : currencyGroup.getCurrencyList()) {
                if (currency.isSelected()) {
                    selected = true;
                    break;
                }
            }
            if (selected) {
                haveSelected = true;
                mAdapter.expandParent(currencyGroup);
            }
        }

        if (!haveSelected) {
            for (CurrencyGroup currencyGroup : mCurrencyGroups) {
                if (currencyGroup.getGroupName().equalsIgnoreCase("Currency")) {
                    mAdapter.expandParent(currencyGroup);
                    break;
                }
            }
        }

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        selectItem();

    }

    private void selectItem() {
        mAdapter.setOnClickItemListener(new ExpandableRecyclerAdapterCurrency.OnClickItemListener() {
            @Override
            public void onClickItemListener(int groupPosition, int itemPosition) {
                if (mCurrencyGroups.get(groupPosition).getCurrencyList().get(itemPosition).isSelected()) {
                    mCurrencyGroups.get(groupPosition).getCurrencyList().get(itemPosition).setSelected(false);
                } else {
                    mCurrencyGroups.get(groupPosition).getCurrencyList().get(itemPosition).setSelected(true);
                }
                ViewHolderCurrencyGroup.setSelected(mCurrencyGroups.get(groupPosition), activity);
                mAdapter.notifyChildChanged(groupPosition, itemPosition);

            }
        });
    }

    public interface OnSelectedCurrency {
        void onSelectedCurrency(List<CurrencyGroup> currencyGroupsList);
    }

    public static void ShowCurrency(final Activity context, String title, List<CurrencyGroup> currencyGroups, DialogSelectCurrency.OnSelectedCurrency selectedCurrency) {
        DialogSelectCurrency dialogCurrency = new DialogSelectCurrency(context, currencyGroups, selectedCurrency);
        dialogCurrency.setTitle(title);
        dialogCurrency.show();


    }


}

