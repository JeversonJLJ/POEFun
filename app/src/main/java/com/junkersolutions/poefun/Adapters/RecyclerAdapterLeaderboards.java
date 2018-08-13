package com.junkersolutions.poefun.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.junkersolutions.poefun.Class.NetworkUtils;
import com.junkersolutions.poefun.Class.Preferences;
import com.junkersolutions.poefun.Class.Useful;
import com.junkersolutions.poefun.Dialog.DialogFilter;
import com.junkersolutions.poefun.Entities.Leaderboards;
import com.junkersolutions.poefun.Fragments.LeaderboardsFragment;
import com.junkersolutions.poefun.R;
import com.squareup.okhttp.internal.Util;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by jever on 26/04/2017.
 */

public class RecyclerAdapterLeaderboards extends RecyclerView.Adapter<RecyclerAdapterLeaderboards.ViewHolder> {



    private final static int LAB_VIEW = 0;
    private final static int LEAGUE_VIEW = 1;
    private final static int ACCOUNT_VIEW = 2;
    private final static int LOADING_MORE_VIEW = 3;
    private List<Leaderboards> list;
    private Activity activity;
    private OnLoadingMore onLoadingMore = null;

    public interface OnLoadingMore {
        void onLoadingMore();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextViewRank;
        public TextView mTextViewAccount;
        public TextView mTextViewCharacter;
        public TextView mTextViewLevel;
        public TextView mTextViewClass;
        public TextView mTextViewTime;
        public ImageButton mImageButtonTwitch;
        public ImageButton mImageButtonCharacterDead;
        public ImageButton mImageButtonLinkAccount;
        public ImageButton mImageButtonLinkCharacter;
        public TextView mTextViewLevelChallenges;
        public ImageButton mImageButtonOnline;
        public ProgressBar mProgressBarLoadingMore;
        public ViewGroup mRoot;

        public ViewHolder(View v) {
            super(v);
            mRoot = v.findViewById(R.id.cardViewRank);
            mImageButtonTwitch = v.findViewById(R.id.imageButtonTwitch);
            mImageButtonCharacterDead = v.findViewById(R.id.imageButtonCharacterDead);
            mImageButtonLinkAccount = v.findViewById(R.id.imageButtonLinkAccount);
            mImageButtonLinkCharacter = v.findViewById(R.id.imageButtonLinkCharacter);
            mTextViewRank = v.findViewById(R.id.textViewRank);
            mTextViewAccount = v.findViewById(R.id.textViewAccount);
            mTextViewCharacter = v.findViewById(R.id.textViewCharacter);
            mTextViewLevel = v.findViewById(R.id.textViewLevel);
            mTextViewClass = v.findViewById(R.id.textViewClass);
            mTextViewLevelChallenges = v.findViewById(R.id.textViewChallenges);
            mTextViewTime = v.findViewById(R.id.textViewTime);
            mImageButtonOnline = v.findViewById(R.id.imageButtonOnline);
            mProgressBarLoadingMore = v.findViewById(R.id.progressBarLoadingMore);
        }
    }

    public RecyclerAdapterLeaderboards(Activity activity, List<Leaderboards> list) {
        this.activity = activity;
        this.list = list;
    }

    public RecyclerAdapterLeaderboards(Activity activity, List<Leaderboards> list, OnLoadingMore onLoadingMore) {
        this.activity = activity;
        this.list = list;
        this.list.add(new Leaderboards());
        this.onLoadingMore = onLoadingMore;
    }


    @Override
    public RecyclerAdapterLeaderboards.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case LAB_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_rank_lab, parent, false);
                break;
            case LOADING_MORE_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_rank_loading_more, parent, false);
                break;
            case ACCOUNT_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_rank_account, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_rank_league, parent, false);
                break;
        }

        return new RecyclerAdapterLeaderboards.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapterLeaderboards.ViewHolder holder, int position) {
        try {
            final Leaderboards leaderboards = list.get(position);
            String accountName = "";

            int viewType = getItemViewType(position);
            if (viewType == LOADING_MORE_VIEW) {
                if (onLoadingMore != null) {
                    if (list.size() < LeaderboardsFragment.QUANTITY_ITEMS_PER_SEARCH)
                        holder.mProgressBarLoadingMore.setVisibility(View.GONE);
                    else {
                        onLoadingMore.onLoadingMore();
                    }
                } else {
                    holder.mProgressBarLoadingMore.setVisibility(View.GONE);
                }
            } else {
                holder.mTextViewRank.setText(String.valueOf(leaderboards.getRank()));
                if (leaderboards.getAccount() != null) {
                    accountName = leaderboards.getAccount().getName();
                    holder.mTextViewAccount.setText(leaderboards.getAccount().getName());
                    holder.mTextViewLevelChallenges.setText(String.valueOf(leaderboards.getAccount().getChallenges().getTotal()));
                    if (leaderboards.getAccount().getTwitch() != null)
                        holder.mImageButtonTwitch.setVisibility(View.VISIBLE);
                    else
                        holder.mImageButtonTwitch.setVisibility(View.INVISIBLE);

                } else {
                    Preferences preferences = new Preferences(activity);
                    holder.mTextViewAccount.setText(preferences.getAccount());
                    accountName = preferences.getAccount();
                }

                holder.mTextViewCharacter.setText(leaderboards.getCharacter().getName());

                if (viewType == ACCOUNT_VIEW || viewType == LEAGUE_VIEW) {
                    holder.mTextViewLevel.setText(String.valueOf(leaderboards.getCharacter().getLevel()));
                }
                if (leaderboards.isOnline())
                    holder.mImageButtonOnline.setImageResource(R.drawable.ic_online);
                else
                    holder.mImageButtonOnline.setImageResource(R.drawable.ic_offline);


                if (viewType == LAB_VIEW)
                    holder.mTextViewTime.setText(Useful.convertLabTime(leaderboards.getTime()));

                holder.mTextViewClass.setText(leaderboards.getCharacter().getClas());


                if (viewType != LAB_VIEW) {
                    if (leaderboards.isDead())
                        holder.mImageButtonCharacterDead.setVisibility(View.VISIBLE);
                    else
                        holder.mImageButtonCharacterDead.setVisibility(View.INVISIBLE);
                }


                final String finalAccountName = accountName;
                if (viewType == LAB_VIEW || viewType == LEAGUE_VIEW) {
                    holder.mImageButtonTwitch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://www.twitch.tv/" + finalAccountName));
                                activity.startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }

                holder.mImageButtonLinkCharacter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://www.pathofexile.com/account/view-profile/" + finalAccountName + "/characters?characterName=" + leaderboards.getCharacter().getName()));
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT);
                        }
                    }
                });

                holder.mImageButtonLinkAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://www.pathofexile.com/account/view-profile/" + finalAccountName + "/characters"));
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list != null && list.size() > 0) {
            if (position == list.size() - 1 && list.get(position).getCharacter() == null)
                return LOADING_MORE_VIEW;
            else if (list.get(position).getAccount() == null)
                return ACCOUNT_VIEW;
            else if (list.get(position).getCharacter().getLevel() == 0)
                return LAB_VIEW;
            else
                return LEAGUE_VIEW;
        }
        return LEAGUE_VIEW;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}