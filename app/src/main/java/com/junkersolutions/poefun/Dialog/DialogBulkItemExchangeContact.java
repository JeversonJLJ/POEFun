package com.junkersolutions.poefun.Dialog;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.junkersolutions.poefun.Class.Preferences;
import com.junkersolutions.poefun.CustomComponents.ImageLoading;
import com.junkersolutions.poefun.Entities.Leagues;
import com.junkersolutions.poefun.Fragments.BulkItemExchangeFragment;
import com.junkersolutions.poefun.Fragments.LeaderboardsFragment;
import com.junkersolutions.poefun.R;
import com.junkersolutions.poefun.WebService.WebServiceEntitiesReturn.Listing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class DialogBulkItemExchangeContact extends AppCompatDialog {

    private Listing mListing;
    private TextView mTextViewStock;
    private EditText mEditTextWhisper;
    private SeekBar mSeekBarAmount;
    private ImageLoading mImageLoadingGet;
    private ImageLoading mImageLoadingPay;
    private TextView mTextViewAmountGet;
    private TextView mTextViewAmountPay;
    private Button mButtonClose;
    private Button mButtonShare;
    private Button mButtonCopy;
    private Activity mActivity;


    public DialogBulkItemExchangeContact(Activity activity, Listing listing) {
        super(activity, R.style.DialogMatchParent);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        mActivity = activity;
        mListing = listing;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bulk_item_exchange_contact);
        mTextViewStock = findViewById(R.id.textViewStock);
        mEditTextWhisper = findViewById(R.id.editTextWhisper);
        mSeekBarAmount = findViewById(R.id.seekBarAmount);
        mImageLoadingGet = findViewById(R.id.imageLoadingCurrencyGet);
        mImageLoadingPay = findViewById(R.id.imageLoadingCurrencyPay);
        mTextViewAmountGet = findViewById(R.id.textViewAmountGet);
        mTextViewAmountPay = findViewById(R.id.textViewAmountPay);
        mButtonClose = findViewById(R.id.buttonClose);
        mButtonShare = findViewById(R.id.buttonShare);
        mButtonCopy = findViewById(R.id.buttonCopy);

        mTextViewStock.setText(String.valueOf(mListing.getPrice().getItem().getStock()));
        mListing.setWhisper(mListing.getWhisper().replace("'","&apos"));
        setEditTextWhisper(MessageFormat.format(mListing.getWhisper(), Math.round(mListing.getPrice().getItem().getAmount()), Math.round(mListing.getPrice().getExchange().getAmount())));

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mSeekBarAmount.setMin(1);
            }
            mSeekBarAmount.setMax(51);
            mSeekBarAmount.setProgress(1);
            mSeekBarAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mTextViewAmountGet.setText(String.valueOf(Math.round(mListing.getPrice().getItem().getAmount() * progress)));
                    mTextViewAmountPay.setText(String.valueOf(Math.round(mListing.getPrice().getExchange().getAmount() * progress)));
                    setEditTextWhisper(MessageFormat.format(mListing.getWhisper(), Math.round(mListing.getPrice().getItem().getAmount() * progress),
                            Math.round(mListing.getPrice().getExchange().getAmount() * progress)));
                    //changeEditText(mListing.getPrice().getItem().getAmount() * progress, mListing.getPrice().getExchange().getAmount() * progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTextViewAmountGet.setText(String.valueOf(Math.round(mListing.getPrice().getItem().getAmount())));
        mTextViewAmountPay.setText(String.valueOf(Math.round(mListing.getPrice().getExchange().getAmount())));


        if (!BulkItemExchangeFragment.searchCurrencyImage(mListing.getPrice().getItem().getCurrency()).isEmpty()) {
            mImageLoadingGet.loadImage(BulkItemExchangeFragment.searchCurrencyImage(mListing.getPrice().getItem().getCurrency()));
        } else {
            mImageLoadingGet.setImageDescription(BulkItemExchangeFragment.searchCurrencyDescription(mListing.getPrice().getItem().getCurrency()));
            mImageLoadingGet.setImageLoadingOnlyText(mImageLoadingGet);
        }

        if (!BulkItemExchangeFragment.searchCurrencyImage(mListing.getPrice().getExchange().getCurrency()).isEmpty()) {
            mImageLoadingPay.loadImage(BulkItemExchangeFragment.searchCurrencyImage(mListing.getPrice().getExchange().getCurrency()));
        } else {
            mImageLoadingPay.setImageDescription(BulkItemExchangeFragment.searchCurrencyDescription(mListing.getPrice().getExchange().getCurrency()));
            mImageLoadingPay.setImageLoadingOnlyText(mImageLoadingPay);
        }


        mButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String shareBody = mEditTextWhisper.getText().toString();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    mActivity.startActivity(Intent.createChooser(sharingIntent, "Share"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mButtonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(mActivity.getResources().getString(R.string.copied_successful), mEditTextWhisper.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(), R.string.copied_successful, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setEditTextWhisper(String textWhisper){
        mEditTextWhisper.setText(textWhisper.replace("&apos","'"));
    }

    public static void ShowContact(final Activity context, Listing listing) {
        DialogBulkItemExchangeContact dialogFilter = new DialogBulkItemExchangeContact(context, listing);
        dialogFilter.setTitle(context.getString(R.string.contact));
        dialogFilter.show();
    }


}


