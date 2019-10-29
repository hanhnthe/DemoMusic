package com.example.hanh29_10;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import com.example.hanh29_10.controller.LayoutController;
import com.example.hanh29_10.controller.OneFragmentController;
import com.example.hanh29_10.controller.TowFragmentController;
import com.example.hanh29_10.fragment.AllSongsFragment;
import com.example.hanh29_10.fragment.FavoriteSongsFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.IBinder;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class ActivityMusic extends AppCompatActivity implements OnSongClickListener {
    private ActionBar mActionBar;
    private LayoutController mLayoutController;
    private SongGetter mSongGetter;

    //khai bao cac doi tuong service
    private MediaPlaybackService mMediaService;
    private Intent mPlayIntent;
    private boolean mMusicBound = false;
    private AppCompatActivity compatActivity;
    public int mOrientation;

    private DrawerLayout mDrawerLayout;

    private List<SongModel> mList;

    public SongGetter getSongGetter(int i) {
        if (i == 1) {
            mMediaService.setList(mSongGetter.getLitSong());
            return mSongGetter;
        } else {
            SongGetter song1 = new SongGetter(this, 2, mSongGetter.getmListAll());
            mMediaService.setList(song1.getLitSong());
            return song1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigationdrawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
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
                    Toast.makeText(getApplicationContext(), "all", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.favoriteSongNavi) {
                    mLayoutController.onCreate(2);
                    Toast.makeText(getApplicationContext(), "favorite", Toast.LENGTH_SHORT).show();
                }
                mDrawerLayout.closeDrawers();
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(musicConnection);
    }

    @Override
    protected void onDestroy() {
        stopService(mPlayIntent);
        super.onDestroy();

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
                // if(isPortrait){
                mLayoutController = new OneFragmentController(compatActivity);
            } else {
                mLayoutController = new TowFragmentController(compatActivity);
            }
            mLayoutController.onCreate(1);
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
        mMediaService.setSong(item.getNumber() - 1);
        mMediaService.playSong();
    }

    public MediaPlaybackService getmMediaService() {
        return mMediaService;
    }

    public void setmMediaService(MediaPlaybackService mMediaService) {
        this.mMediaService = mMediaService;
    }

}
