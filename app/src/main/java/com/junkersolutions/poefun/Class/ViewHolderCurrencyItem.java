package com.junkersolutions.poefun.Class;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.junkersolutions.poefun.Adapters.ExpandableRecyclerAdapterCurrency;
import com.junkersolutions.poefun.Entities.Currency;
import com.junkersolutions.poefun.Fragments.BulkItemExchangeFragment;
import com.junkersolutions.poefun.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ViewHolderCurrencyItem extends ChildViewHolder {

    private int mViewType;
    private CardView mCardView;
    private TextView mTextViewCurrencyDescription;
    private ImageView mImageViewCurrency;
    private ImageView mImageViewSelected;
    private Activity mActivity;
    private ProgressBar mProgressBarCurrency;


    public ViewHolderCurrencyItem(Activity activity, int viewType, View itemView) {
        super(itemView);

        this.mViewType = viewType;
        this.mActivity = activity;
        this.mTextViewCurrencyDescription = itemView.findViewById(R.id.textViewCurrencyDescription);
        this.mImageViewCurrency = itemView.findViewById(R.id.imageViewCurrency);
        this.mImageViewSelected = itemView.findViewById(R.id.imageViewSelected);
        this.mProgressBarCurrency = itemView.findViewById(R.id.progressBarCurrency);
        this.mCardView = itemView.findViewById(R.id.cardView);
    }


    public void bind(final int parentPosition, final int childPosition, @NonNull final Currency currency, final ExpandableRecyclerAdapterCurrency.OnClickItemListener onClickItemListener) {
        mTextViewCurrencyDescription.setText(currency.getDescription());

        if (currency.isSelected())
            mImageViewSelected.setVisibility(View.VISIBLE);
        else
            mImageViewSelected.setVisibility(View.INVISIBLE);
        if (mViewType == ExpandableRecyclerAdapterCurrency.CHILD_WITH_IMAGE) {
            mProgressBarCurrency.setVisibility(View.VISIBLE);
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
            imageLoader.loadImage(currency.getImageURL(), options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mImageViewCurrency.setImageBitmap(loadedImage);
                    mProgressBarCurrency.setVisibility(View.INVISIBLE);
                }
            });
        }

        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(currency.isSelected()) {
                    mImageViewSelected.setVisibility(View.INVISIBLE);
                }
                else{
                    mImageViewSelected.setVisibility(View.VISIBLE);
                }*/

                onClickItemListener.onClickItemListener(parentPosition, childPosition);
            }
        });

    }

}
