package com.junkersolutions.poefun.Class;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.junkersolutions.poefun.Entities.CurrencyGroup;
import com.junkersolutions.poefun.R;

import java.util.List;

public class ViewHolderCurrencyGroup extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private TextView textViewCurrencyDescription;
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
        this.imageButtonExpand = itemView.findViewById(R.id.imageButtonExpand);
    }

    public void bind(@NonNull CurrencyGroup currencyGroup) {
        textViewCurrencyDescription.setText(currencyGroup.getGroupName());
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
        } else { activity.getResources().getDimension(R.dimen.image_group_size_normal);
            paramsCard.setMargins(paramsCard.leftMargin, paramsCard.topMargin, paramsCard.rightMargin, defaulMarginBootom);
            imageButtonExpand.animate().rotation(0).start();
        }
    }




}
