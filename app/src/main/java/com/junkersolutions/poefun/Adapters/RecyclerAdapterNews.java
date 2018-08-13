package com.junkersolutions.poefun.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.junkersolutions.poefun.Class.Useful;
import com.junkersolutions.poefun.R;

import org.mcsoxford.rss.RSSItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jever on 26/04/2017.
 */

public class RecyclerAdapterNews extends RecyclerView.Adapter<RecyclerAdapterNews.ViewHolder> {

    private List<RSSItem> list;
    private Activity activity;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardViewNews;
        public TextView mTextViewTitle;
        public TextView mTextViewDescription;
        public TextView mTextViewDate;
        public Button mButtonShowMore;

        public ViewHolder(View v) {
            super(v);
            mCardViewNews =v.findViewById(R.id.cardViewNews);
            mButtonShowMore = v.findViewById(R.id.buttonShowMore);
            mTextViewTitle = v.findViewById(R.id.textViewTitle);
            mTextViewDescription = v.findViewById(R.id.textViewDescription);
            mTextViewDate = v.findViewById(R.id.textViewDate);

        }
    }

    public RecyclerAdapterNews(Activity activity, List<RSSItem> list) {
        this.activity = activity;
        this.list = list;
    }

    @Override
    public RecyclerAdapterNews.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_news, parent, false);

        return new RecyclerAdapterNews.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapterNews.ViewHolder holder, int position) {
        try {
            final RSSItem rssItem = list.get(position);
            holder.mTextViewTitle.setText(rssItem.getTitle());
            holder.mTextViewDescription.setText(Html.fromHtml(rssItem.getDescription()).toString());

            String dateText = "";
            Calendar pubDate = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            pubDate.setTime(rssItem.getPubDate());
            if ((now.get(Calendar.DAY_OF_YEAR) - pubDate.get(Calendar.DAY_OF_YEAR) == 0)) {
                if ((now.get(Calendar.HOUR_OF_DAY) - pubDate.get(Calendar.HOUR_OF_DAY) == 0))
                    dateText = (now.get(Calendar.MINUTE) - pubDate.get(Calendar.MINUTE)) + " " + activity.getString(R.string.minutes_ago);
                else
                    dateText = (now.get(Calendar.HOUR_OF_DAY) - pubDate.get(Calendar.HOUR_OF_DAY)) + " " + activity.getString(R.string.hours_ago);
            } else if ((now.get(Calendar.DAY_OF_YEAR) - pubDate.get(Calendar.DAY_OF_YEAR) == 1)) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH'h'mm");
                dateText = activity.getString(R.string.yesterday_at) + " " + sdf.format(pubDate.getTime());
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH'h'mm");
                dateText = sdf.format(pubDate.getTime());
            }
            holder.mTextViewDate.setText(dateText);

            holder.mButtonShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, rssItem.getLink());
                    activity.startActivity(browserIntent);
                }
            });

            holder.mCardViewNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, rssItem.getLink());
                    activity.startActivity(browserIntent);
                }
            });


        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}