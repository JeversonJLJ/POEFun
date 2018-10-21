package com.junkersolutions.poefun.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.junkersolutions.poefun.Class.ViewHolderCurrencyGroup;
import com.junkersolutions.poefun.Class.ViewHolderCurrencyItem;
import com.junkersolutions.poefun.Entities.Currency;
import com.junkersolutions.poefun.Entities.CurrencyGroup;
import com.junkersolutions.poefun.R;

import java.util.List;

/**
 * Created by jever on 30/11/2017.
 */

public class ExpandableRecyclerAdapterCurrency extends ExpandableRecyclerAdapter<CurrencyGroup, Currency, ViewHolderCurrencyGroup, ViewHolderCurrencyItem> {


    public static final int PARENT = 0;
    public static final int CHILD_WITH_IMAGE = 1;
    public static final int CHILD_WITHOUT_IMAGE = 2;


    private List<CurrencyGroup> list;
    private Activity activity;
    private LayoutInflater mInflater;

    private OnClickItemListener onClickItemListener = null;

    public interface OnClickItemListener {
        void onClickItemListener(int groupPosition, int itemPosition);
    }


    public ExpandableRecyclerAdapterCurrency(Activity activity, @NonNull List<CurrencyGroup> list) {
        super(list);
        this.list = list;
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
    }


    @UiThread
    @NonNull
    @Override
    public ViewHolderCurrencyGroup onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View groupView = mInflater.inflate(R.layout.recycler_selection_currency_group, parentViewGroup, false);
        return new ViewHolderCurrencyGroup(activity, groupView);
    }

    @UiThread
    @NonNull
    @Override
    public ViewHolderCurrencyItem onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View currencyItemView;
        if (viewType == CHILD_WITH_IMAGE) {
            currencyItemView = mInflater.inflate(R.layout.recycler_selection_currency_with_image, childViewGroup, false);
        } else {
            currencyItemView = mInflater.inflate(R.layout.recycler_selection_currency_without_image, childViewGroup, false);
        }
        return new ViewHolderCurrencyItem(activity, viewType, currencyItemView);
    }


    @UiThread
    @Override
    public void onBindParentViewHolder(@NonNull ViewHolderCurrencyGroup recipeViewHolder, int parentPosition, @NonNull CurrencyGroup currencyGroup) {
        recipeViewHolder.bind(currencyGroup);
    }

    @UiThread
    @Override
    public void onBindChildViewHolder(@NonNull ViewHolderCurrencyItem childViewHolder, int parentPosition, int childPosition, @NonNull Currency currency) {
        childViewHolder.bind(parentPosition, childPosition, currency, onClickItemListener);
    }

    @Override
    public int getParentViewType(int parentPosition) {
        return PARENT;
    }

    @Override
    public int getChildViewType(int parentPosition, int childPosition) {
        if (list.get(parentPosition).getChildList().get(childPosition).getImageURL() != null && !list.get(parentPosition).getChildList().get(childPosition).getImageURL().isEmpty())
            return CHILD_WITH_IMAGE;
        else
            return CHILD_WITHOUT_IMAGE;

    }

    @Override
    public boolean isParentViewType(int viewType) {
        return viewType == PARENT;
    }

    public OnClickItemListener getOnClickItemListener() {
        return onClickItemListener;
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }
}