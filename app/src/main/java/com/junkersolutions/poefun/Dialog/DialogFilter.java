package com.junkersolutions.poefun.Dialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import com.junkersolutions.poefun.Class.Preferences;
import com.junkersolutions.poefun.Entities.Leagues;
import com.junkersolutions.poefun.Fragments.LeaderboardsFragment;
import com.junkersolutions.poefun.R;

import java.util.ArrayList;
import java.util.List;

public class DialogFilter extends AppCompatDialog {

    private Spinner mSpinnerLeagues;
    private OnSelectedFilter onSelectedFilter = null;
    private ProgressBar mProgressBar;
    private Switch mSwitchLab;
    private Switch mSwitchAccount;
    private TextInputEditText mTextInputEditText;
    private TextInputLayout mTextImputLayout;
    private RadioGroup mRadioGroupDifficulty;
    private RadioButton mRadioButtonNormal;
    private RadioButton mRadioButtonCruel;
    private RadioButton mRadioButtonMerciless;
    private RadioButton mRadioButtonEternal;
    private Button mButtonOK;
    private Button mButtonCancel;
    private ArrayAdapter<String> mSpinnerArrayAdapter;


    public DialogFilter(Activity activity, OnSelectedFilter selectedFilter) {
        super(activity, R.style.DialogMatchParent);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        onSelectedFilter = selectedFilter;

    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_filter);
        LeaderboardsFragment.updateLeagues(this.getContext());
        mButtonOK = findViewById(R.id.buttonOKFilter);
        mButtonCancel = findViewById(R.id.buttonCancelSort);
        mProgressBar = findViewById(R.id.progressBar);
        mSwitchLab = findViewById(R.id.switchLabyrinth);
        mSwitchAccount = findViewById(R.id.switchAccount);
        mRadioGroupDifficulty = findViewById(R.id.radioGroupDifficulty);
        mRadioButtonNormal = findViewById(R.id.radioButtonNormal);
        mRadioButtonCruel = findViewById(R.id.radioButtonCruel);
        mRadioButtonMerciless = findViewById(R.id.radioButtonMerciless);
        mRadioButtonEternal = findViewById(R.id.radioButtonEternal);

        mSpinnerLeagues = findViewById(R.id.spinnerLeague);

        mTextInputEditText= findViewById(R.id.textImputLayoutEditTextAccount);
        mTextImputLayout = findViewById(R.id.textImputLayoutAccount);

        mButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Preferences preferences = new Preferences(getContext());
                try {
                    preferences.setLeague(mSpinnerLeagues.getSelectedItem().toString());
                    preferences.setLabyrinth(mSwitchLab.isChecked());
                    preferences.setByAccountName(mSwitchAccount.isChecked());
                    preferences.setAccount(mTextInputEditText.getText().toString());
                    if(mRadioButtonNormal.isChecked())
                        preferences.setDifficulty("Normal");
                    if(mRadioButtonCruel.isChecked())
                        preferences.setDifficulty("Cruel");
                    if(mRadioButtonMerciless.isChecked())
                        preferences.setDifficulty("Merciless");
                    if(mRadioButtonEternal.isChecked())
                        preferences.setDifficulty("Eternal");

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (onSelectedFilter != null)
                    onSelectedFilter.onSelectedFilter();
                DialogFilter.this.dismiss();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFilter.this.dismiss();
            }
        });
        mSwitchLab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mRadioButtonNormal.setEnabled(true);
                    mRadioButtonCruel.setEnabled(true);
                    mRadioButtonMerciless.setEnabled(true);
                    mRadioButtonEternal.setEnabled(true);
                    mSwitchAccount.setChecked(false);
                    mTextImputLayout.setEnabled(false);
                }
                else {
                    mRadioButtonNormal.setEnabled(false);
                    mRadioButtonCruel.setEnabled(false);
                    mRadioButtonMerciless.setEnabled(false);
                    mRadioButtonEternal.setEnabled(false);
                }
            }
        });

        mSwitchAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mSwitchLab.setChecked(false);
                    mRadioButtonNormal.setEnabled(false);
                    mRadioButtonCruel.setEnabled(false);
                    mRadioButtonMerciless.setEnabled(false);
                    mRadioButtonEternal.setEnabled(false);
                    mTextImputLayout.setEnabled(true);
                }
                else {
                    mTextImputLayout.setEnabled(false);
                }
            }
        });

        loading(true);
        new LoadingLeagues().execute(true);
    }

    public void loading(boolean loading) {
        if (loading) {
            mButtonOK.setEnabled(false);
            mButtonCancel.setEnabled(false);
            mSwitchLab.setEnabled(false);
            mRadioButtonNormal.setEnabled(false);
            mRadioButtonCruel.setEnabled(false);
            mRadioButtonMerciless.setEnabled(false);
            mRadioButtonEternal.setEnabled(false);
            mSpinnerLeagues.setEnabled(false);
            mTextImputLayout.setEnabled(false);
            mSwitchAccount.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            Preferences preferences = new Preferences(getContext());
            try {
                mSpinnerLeagues.setSelection(mSpinnerArrayAdapter.getPosition(preferences.getLeague()));
                mSwitchLab.setChecked(preferences.isLabyrinth());
                mSwitchAccount.setChecked(preferences.isByAccountName());
                if(preferences.isByAccountName()){
                    mTextImputLayout.setEnabled(true);
                    mTextInputEditText.setText(preferences.getAccount());
                }
                if(preferences.isLabyrinth()) {
                    mRadioButtonNormal.setEnabled(true);
                    mRadioButtonCruel.setEnabled(true);
                    mRadioButtonMerciless.setEnabled(true);
                    mRadioButtonEternal.setEnabled(true);
                }
                else {
                    mRadioButtonNormal.setEnabled(false);
                    mRadioButtonCruel.setEnabled(false);
                    mRadioButtonMerciless.setEnabled(false);
                    mRadioButtonEternal.setEnabled(false);
                }

                switch (preferences.getDifficulty()){
                    case "Normal":
                        mRadioButtonNormal.setChecked(true);
                        break;
                    case "Cruel":
                        mRadioButtonCruel.setChecked(true);
                        break;
                    case "Merciless":
                        mRadioButtonMerciless.setChecked(true);
                        break;
                    case "Eternal":
                        mRadioButtonEternal.setChecked(true);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            mSwitchAccount.setEnabled(true);
            mButtonOK.setEnabled(true);
            mButtonCancel.setEnabled(true);
            mSwitchLab.setEnabled(true);
            mSpinnerLeagues.setEnabled(true);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void loadedLeagues() {
        List<String> listLeagues = new ArrayList<String>();

        for (Leagues leagueItem : LeaderboardsFragment.Leagues)
            listLeagues.add(leagueItem.getId());

        mSpinnerArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, listLeagues);
        mSpinnerLeagues = findViewById(R.id.spinnerLeague);
        mSpinnerLeagues.setAdapter(mSpinnerArrayAdapter);
        loading(false);
    }

    public interface OnSelectedFilter {
        void onSelectedFilter();
    }

    public static void ShowFilters(final Activity context, DialogFilter.OnSelectedFilter selectedFilter) {
        DialogFilter dialogFilter = new DialogFilter(context, selectedFilter);
        dialogFilter.setTitle(context.getString(R.string.filter));
        dialogFilter.show();
    }

    private class LoadingLeagues extends AsyncTask<Boolean, Integer, Long> {
        protected Long doInBackground(Boolean... leagues) {
            long done = 0;

            while (true) {
                //if (LeaderboardsFragment.Leagues == null || LeaderboardsFragment.UpdatingLeagues == true) {
                if (LeaderboardsFragment.Leagues == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else
                    break;
            }

            return done;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {
            loadedLeagues();
        }
    }

}

