package com.junkersolutions.poefun.CustomComponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.annotation.StyleableRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.junkersolutions.poefun.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


public class ImageLoading extends ConstraintLayout {

    @StyleableRes
    int index0 = 0;
    @StyleableRes
    int index1 = 1;
    @StyleableRes
    int index2 = 2;

    ConstraintLayout mConstraintLayout;
    TextView mImageDescription;
    ImageView mImageView;
    ProgressBar mProgressBar;


    public ImageLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.custom_image_loading, this);

        int[] sets = {R.attr.imageDescription, R.attr.imageDescriptionVisible, R.attr.imageVisible};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);
        CharSequence imageDescription = typedArray.getText(index0);
        boolean imageDescriptionVisibility = typedArray.getBoolean(index1, false);
        boolean imageVisibility = typedArray.getBoolean(index2, true);


        typedArray.recycle();
        initComponents();

        setImageDescription(imageDescription);
        setImageDescriptionVisibility(imageDescriptionVisibility);
        setImageVisibility(imageVisibility);

    }

    private void initComponents() {

        mConstraintLayout = findViewById(R.id.constraintLayout);

        mImageDescription = findViewById(R.id.textViewDescription);

        mImageView = findViewById(R.id.imageView);

        mProgressBar = findViewById(R.id.progressBar);

    }


    public CharSequence getImageDescription() {
        return mImageDescription.getText();
    }

    public void setImageDescription(CharSequence value) {
        mImageDescription.setText(value);
    }


    public void setImageVisibility(boolean value) {
        ConstraintSet set = new ConstraintSet();
        set.clone(mConstraintLayout);
        // The following breaks the connection.
        set.clear(R.id.textViewDescription, ConstraintSet.TOP);
        if (!value) {
            set.connect(R.id.textViewDescription, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        }
        set.applyTo(mConstraintLayout);

        mImageView.setVisibility(value ? VISIBLE : GONE);
        mProgressBar.setVisibility(value ? VISIBLE : GONE);
    }

    public boolean getImageVisibility() {
        return mImageView.getVisibility() == VISIBLE;
    }

    public void setImageDescriptionVisibility(boolean value) {
        ConstraintSet set = new ConstraintSet();
        set.clone(mConstraintLayout);
        // The following breaks the connection.
        set.clear(R.id.progressBar, ConstraintSet.BOTTOM);
        set.clear(R.id.imageView, ConstraintSet.BOTTOM);
        if (value) {
            set.connect(R.id.progressBar, ConstraintSet.BOTTOM, R.id.textViewDescription, ConstraintSet.TOP, 0);
            set.connect(R.id.imageView, ConstraintSet.BOTTOM, R.id.textViewDescription, ConstraintSet.TOP, 0);
        } else {
            set.connect(R.id.progressBar, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            set.connect(R.id.imageView, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        }
        set.applyTo(mConstraintLayout);

        mImageDescription.setVisibility(value ? VISIBLE : GONE);
    }

    public boolean getImageDescriptionVisibility() {
        return mImageDescription.getVisibility() == VISIBLE;
    }


    public ImageView getImageView() {
        return mImageView;
    }

    public boolean getProgressBarVisibility() {
        return mProgressBar.getVisibility() == VISIBLE;
    }

    public void setProgressBarVisibility(boolean value) {
        mProgressBar.setVisibility(value ? VISIBLE : GONE);
    }

    public void loadImage(String url) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        loadImage(url, options);
    }

    public void loadImage(String url, DisplayImageOptions options) {
        setProgressBarVisibility(true);
        ImageLoader imageLoader = ImageLoader.getInstance();

        if (options != null)
            imageLoader.loadImage(url, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mImageView.setImageBitmap(loadedImage);
                    setProgressBarVisibility(false);
                }
            });
        else {
            imageLoader.loadImage(url, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mImageView.setImageBitmap(loadedImage);
                    setProgressBarVisibility(false);
                }
            });
        }
    }

    public void setImageLoadingOnlyText(ImageLoading imageLoading) {
        imageLoading.setImageVisibility(false);
        imageLoading.setImageDescriptionVisibility(true);
        ViewGroup.LayoutParams params = imageLoading.getLayoutParams();
        params.width = params.WRAP_CONTENT;
        params.height = params.WRAP_CONTENT;
        imageLoading.setLayoutParams(params);
    }

}
