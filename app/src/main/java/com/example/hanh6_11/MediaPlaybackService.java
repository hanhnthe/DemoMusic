package com.example.hanh6_11;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.hanh6_11.fragment.MediaPlaybackFragment;

import java.io.Serializable;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Random;

public class MediaPlaybackService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, Serializable {

    //them doi tuong
    //media player
    private MediaPlayer mPlayer;
    //song list
    public List<SongModel> songs;
    //current position
    private int mCurrentSong;
    private int mIdCurrentSong;
    private final IBinder mMusicBind = new MusicBinder();

    private NotificationManager mNotifyManager;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    public static final int FOREGROUND_ID = 2;

    private boolean shuffle = false;
    private int repeat = 1;
    private Random rand;
    private int mSavePlay;
    private int isplaying = 1;

    private Boolean changeData = false;
    public static final String ACTION_PLAY = "notification_action_play";
    public static final String ACTION_NEXT = "notification_action_next";
    public static final String ACTION_PREV = "notification_action_prev";
    public static final String ACTION = "my_action";
    public static final String ISPLAYING ="isplaying";
    public static final String MY_KEY = "my_key";
    public static final String SONGSHAREPREFERENCE = "song1";
    public static final String IDSONGCURRENT = "idsongcurrent";
    private Intent mchangeListennerIntent = null;
    private RemoteViews notificationLayout;
    private RemoteViews notificationLayoutBig;

    //oncreat cho service
    @Override
    public void onCreate() {
        super.onCreate();
        mCurrentSong = -1;//khoi tao vi tri =0
        mPlayer = new MediaPlayer();
        initMusicPlayer();
        createNotificationChannel();
        rand = new Random();
        mSavePlay = 0;
    }

    //phuong thuc khoi tao lop mediaplayer
    public void initMusicPlayer() {
        //cau hinh phat nhac bang cach thiet lap thuoc tinh
        mPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // thiet lap onprepare khi doi tuong mediaplayre duoc chuan bi
        mPlayer.setOnPreparedListener(this);
        //thiet lap khi bai hat da phat xong
        mPlayer.setOnCompletionListener(this);
        //thiet lap khi say ra loi
        mPlayer.setOnErrorListener(this);
    }

    //methos truyen danh sach cac bai hat tu activity
    public void setList(List<SongModel> songs) {
        this.songs = songs;
    }

    public NotificationManager getmNotifyManager() {
        return mNotifyManager;
    }

    //them binder de tuong tac voi activity
    public class MusicBinder extends Binder implements Serializable {
        MediaPlaybackService getService() {
            return MediaPlaybackService.this;
        }
    }

    public IBinder onBind(Intent arg0) {
        return mMusicBind;
    }

    //thiet lap de play 1 song
    public void playSong() {
        mSavePlay++;
        mPlayer.reset();
        saveIdSongCurrent(mIdCurrentSong);
        SongModel playSong = findSongFromId();//get song
        long idSong = playSong.getId();//get id
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, idSong);
        //thiet lap uri nay lam nguoi du lieu doi tuong mediaplayer
        try {
            mPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mPlayer.prepareAsync();
        mNotifyManager.notify(FOREGROUND_ID, buildForegroundNotification());

    }

    //tim bai hat theo id trong songs
    public SongModel findSongFromId() {
        SongModel songModel = new SongModel();
        mIdCurrentSong = readIDShare();
        for (int i = 0; i < songs.size(); i++) {
            SongModel song = songs.get(i);
            if (song.getId() == mIdCurrentSong) {
                songModel = song;
                break;
            }
        }
        return songModel;
    }

    public Notification buildForegroundNotification() {
        Intent notificationIntent = new Intent(
                getApplicationContext(), ActivityMusic.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        //xu ly su kien tren notification
        Intent playClick = new Intent(ACTION_PLAY);
        Intent nextClick = new Intent(ACTION_NEXT);
        Intent prevClick = new Intent(ACTION_PREV);
        PendingIntent notiPlay = PendingIntent
                .getBroadcast(getApplicationContext(), 1, playClick, 0);
        PendingIntent notiNext = PendingIntent
                .getBroadcast(getApplicationContext(), 1, nextClick, 0);
        PendingIntent notiPrev = PendingIntent
                .getBroadcast(getApplicationContext(), 1, prevClick, 0);

        notificationLayout = new RemoteViews(
                getPackageName(), R.layout.notification_small);
        notificationLayoutBig = new RemoteViews(
                getPackageName(), R.layout.notification_big);

        SongModel song = findSongFromId();
        notificationLayout.setImageViewBitmap(R.id.notify_image, song.getImageSong());
        notificationLayoutBig.setImageViewBitmap(R.id.notify_image, song.getImageSong());
        notificationLayoutBig.setTextViewText(R.id.notify_name, song.getNameSong());
        notificationLayoutBig.setTextViewText(R.id.notify_author, song.getAuthorSong());
        notificationLayout.setImageViewResource(R.id.playNoti, R.drawable.ic_pause_22);
        notificationLayoutBig.setImageViewResource(R.id.playNoti, R.drawable.ic_pause_22);

        //set su kien:
        notificationLayout.setOnClickPendingIntent(R.id.playNoti, notiPlay);
        notificationLayout.setOnClickPendingIntent(R.id.nextNoti, notiNext);
        notificationLayout.setOnClickPendingIntent(R.id.prevNoti, notiPrev);
        //big
        notificationLayoutBig.setOnClickPendingIntent(R.id.playNoti, notiPlay);
        notificationLayoutBig.setOnClickPendingIntent(R.id.nextNoti, notiNext);
        notificationLayoutBig.setOnClickPendingIntent(R.id.prevNoti, notiPrev);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL_ID)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setCustomContentView(notificationLayout)
                        .setCustomBigContentView(notificationLayoutBig)
                        .setOnlyAlertOnce(true);
        return (notification.build());
    }
    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(PRIMARY_CHANNEL_ID, "Music Notification",
                            NotificationManager.IMPORTANCE_HIGH);
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    public void updatePlayNotification() {
        Intent notificationIntent = new Intent(
                getApplicationContext(), ActivityMusic.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        //xu ly su kien tren notification
        Intent playClick = new Intent(ACTION_PLAY);
        Intent nextClick = new Intent(ACTION_NEXT);
        Intent prevClick = new Intent(ACTION_PREV);
        PendingIntent notiPlay = PendingIntent
                .getBroadcast(getApplicationContext(), 1, playClick, 0);
        PendingIntent notiNext = PendingIntent
                .getBroadcast(getApplicationContext(), 1, nextClick, 0);
        PendingIntent notiPrev = PendingIntent
                .getBroadcast(getApplicationContext(), 1, prevClick, 0);

        notificationLayout = new RemoteViews(
                getPackageName(), R.layout.notification_small);
        notificationLayoutBig = new RemoteViews(
                getPackageName(), R.layout.notification_big);

        SongModel song = findSongFromId();
        notificationLayout.setImageViewBitmap(R.id.notify_image, song.getImageSong());
        notificationLayoutBig.setImageViewBitmap(R.id.notify_image, song.getImageSong());
        notificationLayoutBig.setTextViewText(R.id.notify_name, song.getNameSong());
        notificationLayoutBig.setTextViewText(R.id.notify_author, song.getAuthorSong());
        notificationLayout.setImageViewResource(R.id.playNoti, R.drawable.ic_play_22);
        notificationLayoutBig.setImageViewResource(R.id.playNoti, R.drawable.ic_play_22);

        //set su kien:
        notificationLayout.setOnClickPendingIntent(R.id.playNoti, notiPlay);
        notificationLayout.setOnClickPendingIntent(R.id.nextNoti, notiNext);
        notificationLayout.setOnClickPendingIntent(R.id.prevNoti, notiPrev);
        //big
        notificationLayoutBig.setOnClickPendingIntent(R.id.playNoti, notiPlay);
        notificationLayoutBig.setOnClickPendingIntent(R.id.nextNoti, notiNext);
        notificationLayoutBig.setOnClickPendingIntent(R.id.prevNoti, notiPrev);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL_ID)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setCustomContentView(notificationLayout)
                        .setCustomBigContentView(notificationLayoutBig)
                        .setOnlyAlertOnce(true);
        mNotifyManager.notify(FOREGROUND_ID, notification.build());
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //start playback
        mediaPlayer.start();
        startForeground(FOREGROUND_ID, buildForegroundNotification());
        // sendBroadCastObjectService();
        if (mchangeListennerIntent == null) {
            mchangeListennerIntent = getApplication().registerReceiver(receiverNotification, new IntentFilter(ACTION_PLAY));
            getApplication().registerReceiver(receiverNotification, new IntentFilter(ACTION_NEXT));
            getApplication().registerReceiver(receiverNotification, new IntentFilter(ACTION_PREV));
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
        stopForeground(true);
        if (mchangeListennerIntent != null) {
            getApplication().unregisterReceiver(receiverNotification);
            mchangeListennerIntent = null;
        }
    }

    //get number = id hieen tai
    public int getmCurrentSong() {
        SongModel songModel = findSongFromId();
        return songModel.getNumber();
    }

    public int getmIdCurrentSong() {
        return mIdCurrentSong;
    }

    //set id = number da dc cong
    public void setID(int number) {
        for (int i = 0; i < songs.size(); i++) {
            SongModel song = songs.get(i);
            if (song.getNumber() == number) {
                mIdCurrentSong = song.getId();
                saveIdSongCurrent(mIdCurrentSong);
                break;
            }
        }
    }

    public void setNumber(int id) {
        mIdCurrentSong = id;
        saveIdSongCurrent(id);
        SongModel songModel = findSongFromId();
        setmCurrentSong(songModel.getNumber());

    }

    public void setmCurrentSong(int mCurrentSong) {
        this.mCurrentSong = mCurrentSong;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mPlayer.getCurrentPosition() > mPlayer.getDuration()) {
            changeData = true;
            isplaying=1;
            sendBroadCast(isplaying);
            mediaPlayer.reset();
            playNext();
            changeData = false;
        }
    }
    //tac dong den musiccontroller
    public int getPos() {
        return mPlayer.getCurrentPosition();
    }
    public int getDur() { //tra ve tong thoi gian bai hat
        return mPlayer.getDuration();
    }

    public Boolean isPng() {
        return mPlayer.isPlaying();
    }
    public void pausePlayer() {
        mPlayer.pause();
    }

    public void seek(int pos) {
        mPlayer.seekTo(pos);
    }

    public void go() {
        mPlayer.start();
    }


    //chuyen tiep den bai hat truoc do neu dang phat <3s
    //chuyen choi lai bai hat neu dang choi >3s
    public void playPrev() {
        if (mPlayer.getCurrentPosition() > 3000) {
            playSong();
            changeData = false;
        } else {
            mCurrentSong = getmCurrentSong();
            if(repeat==3){
               playSong();
            }else if (shuffle) {
                int newSong = mCurrentSong;
                while (newSong == mCurrentSong) {
                    newSong = rand.nextInt(songs.size());
                }
                mCurrentSong = newSong;
                setID(mCurrentSong);
                playSong();
            } else {
                mCurrentSong--;
                if (mCurrentSong < 1) {
                    mCurrentSong = songs.size();
                }
                setID(mCurrentSong);
                playSong();
            }
        }
    }

    //choi bai hat sau, neu la bai cuoi thi choi lai bai dau tien
    public void playNext() {
        if (repeat==3) {
            mIdCurrentSong = mIdCurrentSong;
        } else if (shuffle) {
            mCurrentSong = getmCurrentSong();
            int newSong = mCurrentSong;
            while (newSong == mCurrentSong) {
                newSong = rand.nextInt(songs.size());
            }
            mCurrentSong = newSong;
            setID(mCurrentSong);
        } else {
            mCurrentSong = getmCurrentSong();
            mCurrentSong++;
            if (mCurrentSong > songs.size()) mCurrentSong = 1;
            setID(mCurrentSong);
        }
        playSong();
    }

    //phat ngau nhien shuffle
    public void shuffle() {
        shuffle = readShuffle();
        if (shuffle){
            shuffle = true;
        }
        else {
            shuffle = false;
        }
    }

    public void repeat() {
        repeat = readRepeat();
        if (repeat==1) {
            repeat =1;
        }
        else if(repeat==2){
            repeat=2;
        }else if(repeat==3){
            repeat = 3;
        }
    }
    public void sendBroadCast(int i) {
        Intent intent = new Intent();
        intent.setAction(ACTION);//thiet lap ten de receiver nhan duoc thi nhan biet do la intent
        intent.putExtra(MY_KEY, changeData);
        intent.putExtra(ISPLAYING,i);
        sendBroadcast(intent);
    }

    public BroadcastReceiver receiverNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_PLAY)) {
                if (mPlayer.isPlaying()) {
                    changeData = true;
                    isplaying = 0;
                    sendBroadCast(isplaying);
                    mPlayer.pause();
                    updatePlayNotification();
                    stopForeground(false);
                    changeData = false;
                } else {
                    changeData = true;
                    isplaying=0;
                    sendBroadCast(isplaying);
                    mPlayer.start();
                    startForeground(FOREGROUND_ID, buildForegroundNotification());
                    mNotifyManager.notify(FOREGROUND_ID, buildForegroundNotification());
                    changeData = false;
                }
            } else if (intent.getAction().equals(ACTION_NEXT)) {
                changeData = true;
                playNext();
                isplaying=1;
                sendBroadCast(isplaying);
                changeData = false;
            } else if (intent.getAction().equals(ACTION_PREV)) {
                changeData = true;
                playPrev();
                isplaying=1;
                sendBroadCast(isplaying);
                changeData = false;
            }
        }
    };

    public void saveIdSongCurrent(int id) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SONGSHAREPREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(IDSONGCURRENT, id);
        editor.apply();
    }

    public int readIDShare() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SONGSHAREPREFERENCE, Context.MODE_PRIVATE);
        int i = 0;
        if (sharedPreferences != null) {
            i = sharedPreferences.getInt(IDSONGCURRENT, 0);
        }
        return i;
    }
    public boolean readShuffle(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SONGSHAREPREFERENCE, Context.MODE_PRIVATE);
        boolean check = true;
        if(sharedPreferences!=null){
            check=sharedPreferences.getBoolean(MediaPlaybackFragment.SHUFFLE,true);
        }
        return check;
    }
    public int readRepeat(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SONGSHAREPREFERENCE, Context.MODE_PRIVATE);
        int checkrepeat = 1;
        if(sharedPreferences!= null){
            checkrepeat = sharedPreferences.getInt(MediaPlaybackFragment.REPEAT,1);
        }
        return checkrepeat;
    }
    public int getmSavePlay() {
        return mSavePlay;
    }
    public void setmSavePlay(int mSavePlay) {
        this.mSavePlay = mSavePlay;
    }

}
