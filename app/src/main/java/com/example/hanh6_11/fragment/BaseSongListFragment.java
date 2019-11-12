package com.example.hanh6_11.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanh6_11.ActivityMusic;
import com.example.hanh6_11.MediaPlaybackService;
import com.example.hanh6_11.OnSongClickListener;
import com.example.hanh6_11.R;
import com.example.hanh6_11.SongAdapter;
import com.example.hanh6_11.SongGetter;
import com.example.hanh6_11.SongModel;

public class BaseSongListFragment extends Fragment {
    private RecyclerView mRecyclerview;
    private SongAdapter mSongAdapter;
    private RecyclerView.LayoutManager mLayout;
    private SongGetter mSongGetter;

    private View.OnClickListener mListen;
    private OnSongClickListener mOnSongClickListener;

    private TextView nameTxt, auTxt;
    private ImageView ima;
    private ImageButton mPlay;

    private MediaPlaybackService mService;
    protected ActivityMusic mActi;
    private int mSaveInstance = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActi = (ActivityMusic) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_music, container, false);
        mService = mActi.getmMediaService();

        mRecyclerview = (RecyclerView) view.findViewById(R.id.myrecyclerview);
        mLayout = new LinearLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(mLayout);
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());
        setRecyclerview();
        anhxaViewSmallDetail(view);

        bundlerSongSmallDetail();
        play();
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService.isPng()) {
                    mPlay.setImageResource(R.drawable.ic_play_1);
                    mService.pausePlayer();
                    mService.updatePlayNotification();
                } else {
                    mSaveInstance = mService.getmSavePlay();
                    if (mSaveInstance == 0) {
                        mService.playSong();
                        mSaveInstance++;
                        mService.setmSavePlay(mSaveInstance);
                        setRecyclerview();
                    } else {
                        mService.go();
                    }
                    mPlay.setImageResource(R.drawable.ic_pause_1);
                    mService.getmNotifyManager().notify(mService.FOREGROUND_ID, mService.buildForegroundNotification());
                }
            }
        });

        return view;
    }

    public void setRecyclerview() {
        if (mService != null) {
            songGetter().setmCurrentItemIndex(mService.getmIdCurrentSong());
            ((LinearLayoutManager) mLayout).scrollToPositionWithOffset(songGetter().getCurrentItemIndex(), 20);
        }
        mSongAdapter = getSongAdapter(songGetter());
        mSongAdapter.setOnSongclickListener(mOnSongClickListener);
        mRecyclerview.setAdapter(mSongAdapter);
        mSongAdapter.notifyDataSetChanged();
        mRecyclerview.setHasFixedSize(true);
    }

    public SongGetter songGetter() {
        return mSongGetter;
    }

    public SongAdapter getSongAdapter(SongGetter songGetter) {
        return mSongAdapter;
    }

    public void anhxaViewSmallDetail(View view) {
        View view1 = view.findViewById(R.id.linearLayout3);
        nameTxt = (TextView) view1.findViewById(R.id.nameSong2);
        auTxt = (TextView) view1.findViewById(R.id.author1);
        ima = (ImageView) view1.findViewById(R.id.image1);
        mPlay = (ImageButton) view1.findViewById(R.id.playSong1);
        //set click view small detail de chuyen fragment
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListen != null)
                    mListen.onClick(view);
            }
        });
    }

    public void play() {
        if (mService != null) {
            if (mService.isPng()) {
                mPlay.setImageResource(R.drawable.ic_pause_1);
            }
            else {
                mPlay.setImageResource(R.drawable.ic_play_1);
            }
        }

    }

    public void bundlerSongSmallDetail() {
        if (mService != null) {
            setRecyclerview();
            SongModel item1 = mService.findSongFromId();
            String name1, author;
            Bitmap image;
            name1 = item1.getNameSong();
            author = item1.getAuthorSong();
            image = item1.getImageSong();
            nameTxt.setText(name1);
            auTxt.setText(author);
            ima.setImageBitmap(image);
            play();
        }
    }

    public void setOnSongClickListener(OnSongClickListener onSongClickListener) {
        mOnSongClickListener = onSongClickListener;
        if (mSongAdapter != null) {
            mSongAdapter.setOnSongclickListener(onSongClickListener);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.mListen = listener;
    }

    //cap nhat giao dien small detail
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        //code thi hanh khi receiver nhan dc intent
        @Override
        public void onReceive(Context context, Intent intent) {
            //kiem tra intent
            if (intent.getAction().equals(MediaPlaybackService.ACTION)) {
                //doc du lieu tu intent
                Boolean change = intent.getBooleanExtra(MediaPlaybackService.MY_KEY, true);
                int isplaying = intent.getIntExtra(MediaPlaybackService.ISPLAYING,0);
                if (change&& isplaying==0) {
                    bundlerSongSmallDetail();
                }else if( change && isplaying==1){
                    bundlerSongSmallDetail();
                    mPlay.setImageResource(R.drawable.ic_pause_1);
                }
            }
        }
    };

    public BroadcastReceiver receiverCallBackFragment = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MediaPlaybackFragment.CALLBACKALL)){
                boolean change = intent.getBooleanExtra(MediaPlaybackFragment.CHECKCALLBACK,true);
                if(change){
                    setRecyclerview();
                }
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        ((LinearLayoutManager) mLayout).scrollToPositionWithOffset(songGetter().getCurrentItemIndex(), 20);
        getActivity().registerReceiver(receiver, new IntentFilter(MediaPlaybackService.ACTION));
        getActivity().registerReceiver(receiverCallBackFragment,new IntentFilter(MediaPlaybackFragment.CALLBACKALL));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(receiverCallBackFragment);
    }

}
