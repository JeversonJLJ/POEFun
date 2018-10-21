package com.junkersolutions.poefun.Class;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.junkersolutions.poefun.Entities.Sound;
import com.junkersolutions.poefun.Entities.SoundGroup;
import com.junkersolutions.poefun.R;

import java.util.List;

public class ViewHolderSoundsGroup extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private ImageView imageViewGroup;
    private TextView name;
    private ViewGroup root;
    private ViewGroup card;
    private ImageView imageViewExpand;
    private List<Sound> sounds;
    private int defaulMarginBootom = 0;
    private boolean expanded = false;
    private Activity activity;

    public ViewHolderSoundsGroup(Activity activity, View itemView) {
        super(itemView);
        this.activity = activity;
        this.root = activity.findViewById(R.id.fragment_root);
        this.card = itemView.findViewById(R.id.root);
        this.imageViewGroup = itemView.findViewById(R.id.img_group);
        this.name = itemView.findViewById(R.id.tv_name);
        this.imageViewExpand = itemView.findViewById(R.id.img_expand);
        //this.recyclerViewSounds = this.rootRecyclerViewGroup.findViewById(R.id.recyclerViewSoundsItems);
        //this.recyclerViewSounds.setLayoutManager(new LinearLayoutManager(activity));
    }

    public void bind(@NonNull SoundGroup soundGroup) {
        sounds = soundGroup.getSoundItemList();
        name.setText(soundGroup.getGroupName());
        imageViewGroup.setImageURI(null);
        imageViewGroup.setImageURI(soundGroup.getSoundItemList().get(soundGroup.getSoundItemList().size() - 1).getImageUri());

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

        ViewGroup.LayoutParams paramsImageView = imageViewGroup.getLayoutParams();
        ViewGroup.MarginLayoutParams paramsCard = (ViewGroup.MarginLayoutParams) card.getLayoutParams();

        TransitionManager.beginDelayedTransition(root, new AutoTransition());
        if (expanded) {
            paramsImageView.width = (int) activity.getResources().getDimension(R.dimen.image_group_size_small);
            paramsImageView.height = (int) activity.getResources().getDimension(R.dimen.image_group_size_small);
            defaulMarginBootom = paramsCard.bottomMargin;
            paramsCard.setMargins(paramsCard.leftMargin, paramsCard.topMargin, paramsCard.rightMargin, 0);
            imageViewGroup.setLayoutParams(paramsImageView);
            setTextAppearance(activity, name, android.R.style.TextAppearance_Medium);
            imageViewExpand.animate().rotation(180).start();
        } else {
            paramsImageView.width = (int) activity.getResources().getDimension(R.dimen.image_group_size_normal);
            paramsImageView.height = (int) activity.getResources().getDimension(R.dimen.image_group_size_normal);
            paramsCard.setMargins(paramsCard.leftMargin, paramsCard.topMargin, paramsCard.rightMargin, defaulMarginBootom);
            imageViewGroup.setLayoutParams(paramsImageView);
            setTextAppearance(activity, name, android.R.style.TextAppearance_Large);
            imageViewExpand.animate().rotation(0).start();
        }
    }

    private void setTextAppearance(Context context, TextView textView, int resId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            textView.setTextAppearance(context, resId);
        } else {
            textView.setTextAppearance(resId);
        }
    }


}
