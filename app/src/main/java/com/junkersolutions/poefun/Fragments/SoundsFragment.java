package com.junkersolutions.poefun.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.junkersolutions.poefun.Adapters.ExpandableRecyclerAdapterSounds;
import com.junkersolutions.poefun.Entities.SoundGroup;
import com.junkersolutions.poefun.R;
import com.junkersolutions.poefun.Entities.Sound;
import com.junkersolutions.poefun.Class.Useful;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.junkersolutions.poefun.Class.Useful.APP_STORAGE_PATCH;

/**
 * Created by jever on 20/09/2017.
 */

public class SoundsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpandableRecyclerAdapterSounds adapter;
    private ProgressBar mProgressBar;
    private ProgressBar mStatusBarProgressBar;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private Query mDataBase;

    private List<SoundGroup> mlistGroup;
    private List<SoundGroup> mlistGroupSearch;
    private View rootView;
    private SearchView mSearchView;
    private View mRoot;
    private Snackbar mSnackBar;
    private TextView mStatusUpdate;
    private RelativeLayout mStatusBar;
    private int mSoundCount = 0;
    private Animation animShow, animHide;


    public SoundsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_sounds, container, false);
        rootView.setVisibility(View.VISIBLE);
        mRoot = getActivity().findViewById(android.R.id.content);

        mSnackBar = Snackbar.make(mRoot, "", Snackbar.LENGTH_LONG);

        mStatusUpdate = (TextView) rootView.findViewById(R.id.status_text);
        mStatusBar = (RelativeLayout) rootView.findViewById(R.id.status_bar);

        mStatusBar.setVisibility(View.GONE);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mStatusBarProgressBar = (ProgressBar) rootView.findViewById(R.id.status_progressBar);

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://poefun-73c8e.appspot.com/");
        initAnimation();

        loading(true);


        if (Useful.checkStorageWritePermission(SoundsFragment.this.getActivity()) && Useful.checkStorageReadPermission(SoundsFragment.this.getActivity())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        prepareListData();
                    } catch (Exception e) {
                        Toast.makeText(SoundsFragment.this.getActivity(), e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }).start();
        }

        mlistGroup = new ArrayList<SoundGroup>();

        recyclerView = rootView.findViewById(R.id.recyclerViewSoundsGroup);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        adapter = new ExpandableRecyclerAdapterSounds(this.getActivity(), mlistGroup);
        recyclerView.setAdapter(adapter);

        return rootView;
    }


    private void initAnimation() {
        animShow = AnimationUtils.loadAnimation(this.getContext(), R.anim.popup_show_top_bottom);
        animHide = AnimationUtils.loadAnimation(this.getContext(), R.anim.popup_hide_bottom_top);
    }

    private void search(String imput) {
        try {
            mlistGroupSearch = new ArrayList<SoundGroup>();
            for (SoundGroup soundGroup : mlistGroup)
                mlistGroupSearch.add(soundGroup.getClone());

            for (SoundGroup soundGroup : mlistGroupSearch) {
                Iterator<Sound> i = soundGroup.getSoundItemList().iterator();
                while (i.hasNext()) {
                    Sound sound = i.next();
                    if (!sound.getSoundTitle().toLowerCase().contains(imput.toLowerCase()) && !soundGroup.getGroupName().toLowerCase().contains(imput.toLowerCase()))
                        i.remove();
                }
            }

            Iterator<SoundGroup> i = mlistGroupSearch.iterator();
            while (i.hasNext()) {
                SoundGroup soundGroup = i.next();
                if (soundGroup.getSoundItemList().size() == 0)
                    i.remove();
            }


            changeAdapter(mlistGroupSearch);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_sounds, menu);
        super.onCreateOptionsMenu(menu, inflater);

        mSearchView = (SearchView) menu.findItem(R.id.action_srv).getActionView();
        //mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryHint(getString(R.string.hint_seach_view_sounds));


        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        changeAdapter(mlistGroup);
                    }
                }).start();
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String imput) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        search(imput);
                    }
                }).start();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty())
                    changeAdapter(mlistGroup);
                return false;
            }
        });
    }

    private void changeAdapter(final List<SoundGroup> soundGroups) {
        SoundsFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    adapter = new ExpandableRecyclerAdapterSounds(SoundsFragment.this.getActivity(), soundGroups);
                    recyclerView.setAdapter(adapter);
                    mProgressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateAdapter() {

        SoundsFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    adapter.notifyParentDataSetChanged(true);
                    mProgressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private List<Sound> searchSoundsInGroup(String groupDescription) {
        for (SoundGroup itemGroup : mlistGroup) {
            if (itemGroup.getGroupName().equals(groupDescription))
                return itemGroup.getSoundItemList();

        }
        return null;
    }

    private void setSoundsInGroup(String groupDescription, List<Sound> sounds) {
        for (SoundGroup itemGroup : mlistGroup) {
            if (itemGroup.getGroupName().equals(groupDescription)) {
                itemGroup.setSoundItemList(sounds);
                return;
            }
        }

    }


    private void removeSound(DataSnapshot dataSnapshot) {

        List<Sound> sounds = searchSoundsInGroup(dataSnapshot.child("GroupDescription").getValue(String.class));
        if (sounds != null) {
            for (Sound sound : sounds) {
                if (sound.getId() == Long.parseLong(dataSnapshot.child("id").getValue(String.class))) {
                    sounds.remove(sound);
                    break;
                }
            }
        }
        for (SoundGroup groupName : mlistGroup) {
            sounds = searchSoundsInGroup(groupName.getGroupName());
            if (sounds != null) {
                if (sounds.size() == 0) {
                    mlistGroup.remove(groupName);
                    break;
                }
            }
        }
        changeAdapter(mlistGroup);
    }

    private void addSound(DataSnapshot dataSnapshot) {
        try {
            boolean existsGroup = false;

            for (SoundGroup groupName : mlistGroup) {
                if (groupName.getGroupName().equalsIgnoreCase(dataSnapshot.child("GroupDescription").getValue(String.class))) {
                    existsGroup = true;
                    if (Boolean.parseBoolean(dataSnapshot.child("newGroupSound").getValue(String.class)))
                        groupName.setNewGroupSound(true);
                    break;
                }
            }
            if (!existsGroup)
                mlistGroup.add(new SoundGroup(dataSnapshot.child("GroupDescription").getValue(String.class), Boolean.parseBoolean(dataSnapshot.child("newGroupSound").getValue(String.class))));
            //  mlistDataHeader.add(new SoundGroup(dataSnapshot.child("GroupDescription").getValue(String.class), Boolean.parseBoolean(dataSnapshot.child("newGroupSound").getValue(String.class))));


            List<Sound> sounds = searchSoundsInGroup(dataSnapshot.child("GroupDescription").getValue(String.class));
            if (sounds == null)
                sounds = new ArrayList<Sound>();
            final String imageName = dataSnapshot.child("ImageName").getValue(String.class);
            final String soundName = dataSnapshot.child("SoundName").getValue(String.class);
            String imagePath = APP_STORAGE_PATCH + "/" + imageName;
            String soundPath = APP_STORAGE_PATCH + "/" + soundName;

            //Image download
            if (!Useful.fileExists(APP_STORAGE_PATCH + "/" + imageName)) {
                mStorageRef = mStorage.getReferenceFromUrl(dataSnapshot.child("ImageURL").getValue(String.class));
                new File(APP_STORAGE_PATCH).mkdirs();
                File imageFile = new File(APP_STORAGE_PATCH, imageName);
                Useful.firebaseDownloadFile(this.getActivity(), mStorageRef, imageFile);
            }


            Sound newSound = new Sound(dataSnapshot.child("SoundDescription").getValue(String.class), soundName,
                    Uri.parse(imagePath),
                    Uri.parse(soundPath),
                    Long.parseLong(dataSnapshot.child("id").getValue(String.class)),
                    mStorage.getReferenceFromUrl(dataSnapshot.child("SoundURL").getValue(String.class)));
            boolean existsSound = false;
            for (Sound item : sounds) {
                if (item.getSoundTitle().equals(newSound.getSoundTitle()))
                    existsSound = true;
            }
            if (!existsSound) {
                sounds.add(newSound);
                mSoundCount++;

            }

            setSoundsInGroup(dataSnapshot.child("GroupDescription").getValue(String.class), sounds);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void loading(final boolean progressVisible) {
        try {
            SoundsFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressVisible)
                        mProgressBar.setVisibility(View.VISIBLE);
                    else
                        mProgressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Toast.makeText(SoundsFragment.this.getActivity(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void prepareListData() {

        mDataBase = FirebaseDatabase.getInstance().getReference().child("Sounds").limitToLast(50);
        mDataBase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                loading(true);
                addSound(dataSnapshot);
                updateAdapter();
                loading(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                loading(true);
                removeSound(dataSnapshot);
                addSound(dataSnapshot);
                loading(false);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                loading(true);
                removeSound(dataSnapshot);
                loading(false);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        showHideStatusBar(true);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final Query dataBase = FirebaseDatabase.getInstance().getReference().child("Sounds");
        dataBase.keepSynced(false);
        dataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                addSound(child);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mStatusUpdate.setText("Loading sounds, Groups:" + mlistGroup.size() + " Sounds:" + mSoundCount);
                                    }
                                });
                            }
                            dataBase.keepSynced(true);
                            Thread.sleep(1000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        updateAdapter();
                                        showHideStatusBar(false);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } catch (Exception e) {
                            Toast.makeText(SoundsFragment.this.getActivity(), e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showHideStatusBar(final boolean show) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    mStatusBar.startAnimation(animShow);
                    animShow.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            mStatusBar.setVisibility(View.VISIBLE);
                            mStatusUpdate.setText("Waiting for server response...");
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    mStatusBar.startAnimation(animHide);
                    animHide.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mStatusUpdate.setText("");
                            mStatusBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }
        });
    }

}