package com.example.hanh5_11;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import com.example.hanh5_11.controller.LayoutController;
import com.example.hanh5_11.controller.OneFragmentController;
import com.example.hanh5_11.controller.TowFragmentController;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import android.os.IBinder;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.List;

public class ActivityMusic extends AppCompatActivity implements OnSongClickListener {
    private ActionBar mActionBar;
    private LayoutController mLayoutController;
    private SongGetter mSongGetter, mSongGetterAll, mSongGetterFavorite;

    //khai bao cac doi tuong service
    private MediaPlaybackService mMediaService;
    private Intent mPlayIntent;
    private boolean mMusicBound = false;
    private AppCompatActivity compatActivity;
    public int mOrientation;

    private DrawerLayout mDrawerLayout;
    private final static String LISTSONGSAVE = "listsongsave";

    private List<SongModel> mList;
    private final static String CHECK = "check";
    private Boolean check = true;

    public SongGetter getSongGetter(int i) {
        if (i == 1) {
            return mSongGetterAll;
        } else {
            boolean check1 = mSongGetterAll.ismCheckdataChange();
            if (check1) {
                mSongGetterFavorite = new SongGetter(this, 2, mSongGetterAll.getmListAll());
            }
            return mSongGetterFavorite;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigationdrawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        //cap quyen truy cap  READ_EXTERNAL_STORAGE
        int permission_storage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission_storage != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        } else {
            mSongGetterAll = new SongGetter(this, 1, null);
            mSongGetterFavorite = new SongGetter(this, 2, null);
            if (savedInstanceState != null) {
                check = savedInstanceState.getBoolean(CHECK);
                if (check) {
                    mSongGetter = mSongGetterAll;
                    check = true;
                    mList = readListPreference();
                } else {
                    mSongGetter = mSongGetterFavorite;
                    check = false;
                    mList = readListPreference();
                }
            } else {
                mSongGetter = mSongGetterAll;
                mList = mSongGetter.getLitSong();
            }
            compatActivity = this;
            mOrientation = getResources().getConfiguration().orientation;

            if (mPlayIntent == null) {
                mPlayIntent = new Intent(this, MediaPlaybackService.class);
                startService(mPlayIntent);
                bindService(mPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
            }
            navigation();
        }
    }

    private void makeRequest() {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        mSongGetter = new SongGetter(this, 1, null);
        mList = mSongGetter.getLitSong();
        compatActivity = this;
        mOrientation = getResources().getConfiguration().orientation;

        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, MediaPlaybackService.class);
            startService(mPlayIntent);
            bindService(mPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
        navigation();
    }

    private void navigation() {
        mDrawerLayout = findViewById(R.id.navigation);

        NavigationView navigationView = findViewById(R.id.navi_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.allSongNavi) {
                    mLayoutController.onCreate(1);
                    check = true;
                    Toast.makeText(getApplicationContext(), "all", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.favoriteSongNavi) {
                    mLayoutController.onCreate(2);
                    check = false;
                    Toast.makeText(getApplicationContext(), "favorite", Toast.LENGTH_SHORT).show();
                }
                mDrawerLayout.closeDrawers();
                return false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CHECK, check);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(musicConnection);
    }

    //ket noi voi service
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) service;
            //get service
            setmMediaService(((MediaPlaybackService.MusicBinder) service).getService());
            //chuyen list
            mMediaService.setList(mList);
            mMusicBound = true;
            if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                mLayoutController = new OneFragmentController(compatActivity);
            } else {
                mLayoutController = new TowFragmentController(compatActivity);
            }
            if (check) {
                mLayoutController.onCreate(1);
            } else {
                mLayoutController.onCreate(2);
            }

            mLayoutController.setmOnclickService((OnSongClickListener) compatActivity);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicBound = false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClickItem(SongModel item) {
        if (mSongGetterFavorite != null && !check) {
            mMediaService.setList(mSongGetterFavorite.getLitSong());
            saveList(mSongGetterFavorite.getLitSong());
            mSongGetterAll.setmCheckdataChange(false);
        }
        if (mSongGetterAll != null && check) {
            mMediaService.setList(mSongGetterAll.getLitSong());
            saveList(mSongGetterAll.getLitSong());
        }
        mMediaService.setNumber(item.getId());
        mMediaService.playSong();
    }

    public MediaPlaybackService getmMediaService() {
        return mMediaService;
    }

    public void setmMediaService(MediaPlaybackService mMediaService) {
        this.mMediaService = mMediaService;
    }

    public void saveList(List<SongModel> list) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(LISTSONGSAVE, json);
        editor.commit();
    }

    public List<SongModel> readListPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString(LISTSONGSAVE, "");
        Type type = new TypeToken<List<SongModel>>() {
        }.getType();
        List<SongModel> list = gson.fromJson(json, type);
        return list;
    }

}
