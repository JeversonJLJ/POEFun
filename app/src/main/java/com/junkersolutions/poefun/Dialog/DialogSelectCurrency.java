package com.junkersolutions.poefun.Dialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.junkersolutions.poefun.Adapters.ExpandableRecyclerAdapterCurrency;
import com.junkersolutions.poefun.Adapters.ExpandableRecyclerAdapterSounds;
import com.junkersolutions.poefun.Adapters.RecyclerAdapterSelectedCurrency;
import com.junkersolutions.poefun.Class.Preferences;
import com.junkersolutions.poefun.Entities.Currency;
import com.junkersolutions.poefun.Entities.CurrencyGroup;
import com.junkersolutions.poefun.Entities.Leagues;
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
    private List<CurrencyGroup> currencyGroups;
    private Activity activity;


    public DialogSelectCurrency(Activity activity, final List<CurrencyGroup> currencyGroups, OnSelectedCurrency selectedCurrency) {
        super(activity, R.style.DialogMatchParent);
        this.activity = activity;
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        onSelectedCurrency = selectedCurrency;
        this.currencyGroups = currencyGroups;



    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_currency);
        LeaderboardsFragment.updateLeagues(this.getContext());
        mButtonOK = findViewById(R.id.buttonOK);
        mButtonCancel = findViewById(R.id.buttonCancel);


        mButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectedCurrency != null)
                    onSelectedCurrency.onSelectedCurrency(currencyGroups);
                DialogSelectCurrency.this.dismiss();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelectCurrency.this.dismiss();
            }
        });

        mRecyclerViewCurrencyGroups = findViewById(R.id.recyclerViewCurrency);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity,4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(mAdapter.getItemViewType(position)==ExpandableRecyclerAdapter.TYPE_PARENT){
                    return 4;
                }
                return 1;
            }
        });
        mRecyclerViewCurrencyGroups.setLayoutManager(gridLayoutManager);

        //RecyclerAdapterSelectedCurrency recyclerAdapterSelectedCurrency = new RecyclerAdapterSelectedCurrency(activity,this.currencyGroups.get(0).getChildList());
        //mRecyclerViewCurrencyGroups.setAdapter(recyclerAdapterSelectedCurrency);


        mAdapter = new ExpandableRecyclerAdapterCurrency(activity, this.currencyGroups);
        mRecyclerViewCurrencyGroups.setAdapter(mAdapter);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        mAdapter.setOnClickItemListener(new ExpandableRecyclerAdapterCurrency.OnClickItemListener() {
            @Override
            public void onClickItemListener(int groupPosition, int itemPosition) {

            }
        });
        selectItem();

    }

    private void selectItem(){
        mAdapter.setOnClickItemListener(new ExpandableRecyclerAdapterCurrency.OnClickItemListener() {
            @Override
            public void onClickItemListener(int groupPosition, int itemPosition) {
                if(currencyGroups.get(groupPosition).getCurrencyList().get(itemPosition).isSelected()){
                    currencyGroups.get(groupPosition).getCurrencyList().get(itemPosition).setSelected(false);
                }
                else{
                    currencyGroups.get(groupPosition).getCurrencyList().get(itemPosition).setSelected(true);
                }
                mAdapter.notifyChildChanged(groupPosition,itemPosition);
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
