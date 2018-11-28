package com.junkersolutions.poefun.Class;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.junkersolutions.poefun.Entities.Currency;
import com.junkersolutions.poefun.Entities.CurrencyGroup;
import com.junkersolutions.poefun.R;

import java.util.List;

public class ViewHolderCurrencyGroup extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private TextView textViewCurrencyDescription;
    private TextView textViewSelected;
    private ViewGroup cardView;
    private ImageButton imageButtonExpand;
    private int defaulMarginBootom = 0;
    private boolean expanded = false;
    private Activity activity;

    public ViewHolderCurrencyGroup(Activity activity, View itemView) {
        super(itemView);
        this.activity = activity;
        this.cardView = activity.findViewById(R.id.cardView);
        this.textViewCurrencyDescription = itemView.findViewById(R.id.textViewCurrencyDescription);
        this.textViewSelected = itemView.findViewById(R.id.textViewSelected);
        this.imageButtonExpand = itemView.findViewById(R.id.imageButtonExpand);
    }

    public void bind(@NonNull CurrencyGroup currencyGroup) {
        currencyGroup.setTextViewSelected(textViewSelected);
        textViewCurrencyDescription.setText(currencyGroup.getGroupName());
        setSelected(currencyGroup,activity);
    }


    public static void setSelected(@NonNull CurrencyGroup currencyGroup, Activity activity){
        currencyGroup.setSelected(0);
        for (Currency currency : currencyGroup.getCurrencyList()) {
            if (currency.isSelected())
                currencyGroup.setSelected(currencyGroup.getSelected() + 1);
        }
        if (currencyGroup.getSelected() == 0)
            currencyGroup.getTextViewSelected().setText("");
        else
            currencyGroup.getTextViewSelected().setText("(" + currencyGroup.getSelected() + " "+ activity.getString(R.string.selected) + ")");
    }

    @SuppressLint("NewApi")
    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        this.expanded = expanded;
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        changeScene();
    }


    public void changeScene() {
        ViewGroup.MarginLayoutParams paramsCard = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
        if (expanded) {
            defaulMarginBootom = paramsCard.bottomMargin;
            paramsCard.setMargins(paramsCard.leftMargin, paramsCard.topMargin, paramsCard.rightMargin, 0);
            imageButtonExpand.animate().rotation(180).start();
        } else {
            activity.getResources().getDimension(R.dimen.image_group_size_normal);
            paramsCard.setMargins(paramsCard.leftMargin, paramsCard.topMargin, paramsCard.rightMargin, defaulMarginBootom);
            imageButtonExpand.animate().rotation(0).start();
        }
    }


}
