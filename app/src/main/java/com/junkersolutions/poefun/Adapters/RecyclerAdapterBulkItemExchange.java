package com.junkersolutions.poefun.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.junkersolutions.poefun.CustomComponents.ImageLoading;
import com.junkersolutions.poefun.Dialog.DialogBulkItemExchangeContact;
import com.junkersolutions.poefun.Entities.Currency;
import com.junkersolutions.poefun.Entities.CurrencyGroup;
import com.junkersolutions.poefun.Fragments.BulkItemExchangeFragment;
import com.junkersolutions.poefun.R;
import com.junkersolutions.poefun.WebService.WebServiceEntitiesReturn.Result;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * Created by jever on 26/04/2017.
 */

public class RecyclerAdapterBulkItemExchange extends RecyclerView.Adapter<RecyclerAdapterBulkItemExchange.ViewHolder> {


    private final static int BULK_ITEM_EXCHANGE_VIEW = 0;
    private final static int LOADING_MORE_VIEW = 1;
    private List<Result> list;
    private Activity activity;
    private OnLoadingMore onLoadingMore = null;

    public interface OnLoadingMore {
        void onLoadingMore();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageButton mImageButtonOnline;
        public ImageButton mImageButtonLinkAccount;
        public ImageLoading mImageLoadingPayMin;
        public ImageLoading mImageLoadingGetMin;
        public ImageLoading mImageLoadingWorthPayMin;
        public ImageLoading mImageLoadingWorthGetMin;
        public ImageLoading mImageLoadingGet;
        public ImageLoading mImageLoadingPay;
        public ImageLoading mImageLoadingCountry;
        public TextView mTextViewWorthAmmountGet;
        public TextView mTextViewWorthAmmountPay;
        public TextView mTextViewCurrencyGet;
        public TextView mTextViewCurrencyPay;
        public TextView mTextViewGetAmount;
        public TextView mTextViewPayAmount;
        public TextView mTextViewStockAmount;
        public TextView mTextViewCurrencyStockGet;
        public TextView mTextViewProfileName;
        public Button mButtonContact;


        public ProgressBar mProgressBarLoadingMore;
        public ViewGroup mRoot;

        public ViewHolder(View v) {
            super(v);
            mRoot = v.findViewById(R.id.cardViewRank);

            mImageButtonOnline = v.findViewById(R.id.imageButtonOnline);
            mImageButtonLinkAccount = v.findViewById(R.id.imageButtonLinkAccount);
            mImageLoadingPayMin = v.findViewById(R.id.imageLoadingPayMin);
            mImageLoadingGetMin = v.findViewById(R.id.imageLoadingGetMin);
            mImageLoadingWorthPayMin = v.findViewById(R.id.imageLoadingWorthPayMin);
            mImageLoadingWorthGetMin = v.findViewById(R.id.imageLoadingWorthGetMin);
            mImageLoadingGet = v.findViewById(R.id.imageLoadingGet);
            mImageLoadingPay = v.findViewById(R.id.imageLoadingPay);
            mImageLoadingCountry = v.findViewById(R.id.imageLoadingCountry);

            mTextViewWorthAmmountGet = v.findViewById(R.id.textViewWorthAmmountGet);
            mTextViewWorthAmmountPay = v.findViewById(R.id.textViewWorthAmmountPay);
            mTextViewCurrencyGet = v.findViewById(R.id.textViewCurrencyGet);
            mTextViewCurrencyPay = v.findViewById(R.id.textViewCurrencyPay);
            mTextViewGetAmount = v.findViewById(R.id.textViewGetAmount);
            mTextViewPayAmount = v.findViewById(R.id.textViewPayAmount);
            mTextViewStockAmount = v.findViewById(R.id.textViewStockAmount);
            mTextViewCurrencyStockGet = v.findViewById(R.id.textViewCurrencyStockGet);
            mTextViewProfileName = v.findViewById(R.id.textViewProfileName);

            mButtonContact = v.findViewById(R.id.buttonContact);

            mProgressBarLoadingMore = v.findViewById(R.id.progressBarLoadingMore);
        }
    }


    public RecyclerAdapterBulkItemExchange(Activity activity, List<Result> list) {
        this.activity = activity;
        this.list = list;
    }

    public RecyclerAdapterBulkItemExchange(Activity activity, List<Result> list, OnLoadingMore onLoadingMore) {
        this.activity = activity;
        this.list = list;
        this.list.add(new Result());
        this.onLoadingMore = onLoadingMore;
    }


    @Override
    public RecyclerAdapterBulkItemExchange.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case LOADING_MORE_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_loading_more, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_bulk_item_exchange, parent, false);
                break;
        }

        return new RecyclerAdapterBulkItemExchange.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapterBulkItemExchange.ViewHolder holder, int position) {
        try {
            final Result result = list.get(position);

            int viewType = getItemViewType(position);
            if (viewType == LOADING_MORE_VIEW) {
                if (onLoadingMore != null) {
                    if (list.size() < BulkItemExchangeFragment.QUANTITY_ITEMS_PER_SEARCH)
                        holder.mProgressBarLoadingMore.setVisibility(View.GONE);
                    else {
                        onLoadingMore.onLoadingMore();
                    }
                } else {
                    holder.mProgressBarLoadingMore.setVisibility(View.GONE);
                }
            } else {

                holder.mButtonContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogBulkItemExchangeContact.ShowContact(activity, result.getListing());
                    }
                });

                holder.mImageButtonLinkAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://www.pathofexile.com/account/view-profile/" + result.getListing().getAccount().getName() + "/characters"));
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT);
                        }
                    }
                });

                if (!BulkItemExchangeFragment.searchCurrencyImage(result.getListing().getPrice().getItem().getCurrency()).isEmpty()) {
                    holder.mImageLoadingGet.loadImage(BulkItemExchangeFragment.searchCurrencyImage(result.getListing().getPrice().getItem().getCurrency()));
                    holder.mImageLoadingGetMin.loadImage(BulkItemExchangeFragment.searchCurrencyImage(result.getListing().getPrice().getItem().getCurrency()));
                    holder.mImageLoadingWorthGetMin.loadImage(BulkItemExchangeFragment.searchCurrencyImage(result.getListing().getPrice().getItem().getCurrency()));
                } else {
                    holder.mImageLoadingGet.setImageVisibility(false);
                    holder.mImageLoadingGet.setImageDescriptionVisibility(false);

                    holder.mImageLoadingGetMin.setImageDescription(BulkItemExchangeFragment.searchCurrencyDescription(result.getListing().getPrice().getItem().getCurrency()));
                    holder.mImageLoadingGetMin.setImageLoadingOnlyText(holder.mImageLoadingGetMin);

                    holder.mImageLoadingWorthGetMin.setImageDescription(BulkItemExchangeFragment.searchCurrencyDescription(result.getListing().getPrice().getItem().getCurrency()));
                    holder.mImageLoadingGetMin.setImageLoadingOnlyText(holder.mImageLoadingWorthGetMin);
                }
                if (!BulkItemExchangeFragment.searchCurrencyImage(result.getListing().getPrice().getExchange().getCurrency()).isEmpty()) {
                    holder.mImageLoadingPay.loadImage(BulkItemExchangeFragment.searchCurrencyImage(result.getListing().getPrice().getExchange().getCurrency()));
                    holder.mImageLoadingPayMin.loadImage(BulkItemExchangeFragment.searchCurrencyImage(result.getListing().getPrice().getExchange().getCurrency()));
                    holder.mImageLoadingWorthPayMin.loadImage(BulkItemExchangeFragment.searchCurrencyImage(result.getListing().getPrice().getExchange().getCurrency()));

                } else {
                    holder.mImageLoadingPay.setImageVisibility(false);
                    holder.mImageLoadingPay.setImageDescriptionVisibility(false);

                    holder.mImageLoadingPayMin.setImageDescription(BulkItemExchangeFragment.searchCurrencyDescription(result.getListing().getPrice().getExchange().getCurrency()));
                    holder.mImageLoadingGetMin.setImageLoadingOnlyText(holder.mImageLoadingPayMin);

                    holder.mImageLoadingWorthPayMin.setImageDescription(BulkItemExchangeFragment.searchCurrencyDescription(result.getListing().getPrice().getExchange().getCurrency()));
                    holder.mImageLoadingGetMin.setImageLoadingOnlyText(holder.mImageLoadingWorthPayMin);
                }

                holder.mImageLoadingCountry.loadImage(getLanguageFlag(result.getListing().getAccount().getLanguage()));

                double get = result.getListing().getPrice().getItem().getAmount();
                double pay = result.getListing().getPrice().getExchange().getAmount();

                String getDescription = BulkItemExchangeFragment.searchCurrencyDescription(result.getListing().getPrice().getItem().getCurrency());
                String payDescription = BulkItemExchangeFragment.searchCurrencyDescription(result.getListing().getPrice().getExchange().getCurrency());
                int stock = result.getListing().getPrice().getItem().getStock();
                String profileName = result.getListing().getAccount().getName();
                holder.mTextViewWorthAmmountGet.setText(round(get / pay));
                holder.mTextViewWorthAmmountPay.setText(round(pay / get));
                holder.mTextViewCurrencyGet.setText(getDescription);
                holder.mTextViewCurrencyPay.setText(payDescription);
                holder.mTextViewGetAmount.setText(String.valueOf(doubleToString(get)));
                holder.mTextViewPayAmount.setText(String.valueOf(doubleToString(pay)));
                holder.mTextViewStockAmount.setText(String.valueOf(doubleToString(stock)));
                holder.mTextViewCurrencyStockGet.setText(getDescription);
                holder.mTextViewProfileName.setText(profileName);

                if (result.getListing().getAccount().getOnline() != null)
                    holder.mImageButtonOnline.setImageResource(R.drawable.ic_online);
                else
                    holder.mImageButtonOnline.setImageResource(R.drawable.ic_offline);
            }
        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (list != null && list.size() > 0) {
            if (position == list.size() - 1 && list.get(position).getListing() == null)
                return LOADING_MORE_VIEW;
            else
                return BULK_ITEM_EXCHANGE_VIEW;
        }
        return BULK_ITEM_EXCHANGE_VIEW;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    

    private String getLanguageFlag(String language) {
        String flag = language.split("_")[1];
        switch (flag) {
            case "BR":
                return "https://web.poecdn.com/image/lang/BR.png";
            case "RU":
                return "https://web.poecdn.com/image/lang/RU.png";
            case "TH":
                return "https://web.poecdn.com/image/lang/TH.png";
            case "DE":
                return "https://web.poecdn.com/image/lang/DE.png";
            case "FR":
                return "https://web.poecdn.com/image/lang/FR.png";
            case "ES":
                return "https://web.poecdn.com/image/lang/ES.png";
            default:
                return "https://web.poecdn.com/image/lang/GB.png";
        }
    }

    private String round(double value) {
        if (value == 0 || value >= 1)
            return String.valueOf(Math.round(value));

        return String.format("%.4f", value);
    }

    private String doubleToString(double value) {
        String stingValue = String.valueOf(value);
        return stingValue.indexOf(".") < 0 ? stingValue : stingValue.replaceAll("0*$", "").replaceAll("\\.$", "");
    }


    /*
    private String round(String value) {
        if (Double.parseDouble(value.replace(",",".")) == 0 || Double.parseDouble(value.replace(",",".")) > 1)
            return value;

        for (int i = value.length()-1; i >= 0; i++) {

        }

        //System.out.printf ("%f%n", media);
        int rightNumbers = 0;
        String result = "";
        for (int i = 0; i < value.length(); i++) {
            result += value.charAt(i);
            if (value.charAt(i) == '.' || value.charAt(i) == ',') {
                for (int j = i + 1; j < value.length(); j++) {
                    result += value.charAt(j);
                    rightNumbers++;
                    if (value.charAt(j) != '0') {
                        if (rightNumbers >= 4)
                            break;
                    }
                }
                break;
            }
        }


        return result;
    }

    /* final double integerPart = Math.floor(value);
    double base = 1.0;

    value -= integerPart;
        while (Math.abs(value) < 1) {
        value *= 10.0;
        base *= 10.0;
    }


    double result = integerPart + Math.round(value) / base;
    */


}