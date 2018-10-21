package com.junkersolutions.poefun.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.junkersolutions.poefun.Entities.Currency;
import com.junkersolutions.poefun.Fragments.LeaderboardsFragment;
import com.junkersolutions.poefun.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by jever on 26/04/2017.
 */

public class RecyclerAdapterSelectedCurrency extends RecyclerView.Adapter<RecyclerAdapterSelectedCurrency.ViewHolder> {

    private final static int WITH_IMAGE = 0;
    private final static int WITHOUT_IMAGE = 1;
    private List<Currency> list;
    private Activity activity;

    private OnClickItemListener onClickItemListener = null;

    public interface OnClickItemListener {
        void onClickItemListener();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public ImageView mImageViewCurrency;
        public ProgressBar mProgressBarCurrency;
        public TextView mTextViewCurrencyDescription;

        public ViewHolder(View v) {
            super(v);
            mCardView = v.findViewById(R.id.cardView);
            mImageViewCurrency = v.findViewById(R.id.imageViewCurrency);
            mProgressBarCurrency = v.findViewById(R.id.progressBarCurrency);
            mTextViewCurrencyDescription = v.findViewById(R.id.textViewCurrencyDescription);
        }
    }



    public RecyclerAdapterSelectedCurrency(Activity activity, List<Currency> list, OnClickItemListener onClickItemListener) {
        this.activity = activity;
        this.list = list;
        this.onClickItemListener = onClickItemListener;
    }

    @Override
    public RecyclerAdapterSelectedCurrency.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case WITH_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_selected_currency_with_image, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_selected_currency_without_image, parent, false);
                break;
        }

        return new RecyclerAdapterSelectedCurrency.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapterSelectedCurrency.ViewHolder holder, int position) {
        try {
            int viewType = getItemViewType(position);
            if (viewType == WITH_IMAGE) {
                ImageLoader imageLoader = ImageLoader.getInstance();
                holder.mImageViewCurrency.setImageURI(Uri.parse(""));
                holder.mProgressBarCurrency.setVisibility(View.VISIBLE);
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .build();
                imageLoader.loadImage(list.get(position).getImageURL(), options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.mImageViewCurrency.setImageBitmap(loadedImage);
                        holder.mProgressBarCurrency.setVisibility(View.INVISIBLE);
                    }
                });

            } else {
                holder.mTextViewCurrencyDescription.setText(list.get(position).getDescription());
            }

            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickItemListener != null) {
                        onClickItemListener.onClickItemListener();
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list != null && list.size() > 0) {
            if (list.get(position).getImageURL() != null && !list.get(position).getImageURL().isEmpty()) {
                return WITH_IMAGE;
            }
        }
        return WITHOUT_IMAGE;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}