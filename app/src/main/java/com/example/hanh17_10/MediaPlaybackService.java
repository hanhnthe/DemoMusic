package com.example.hanh17_10;

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

import java.io.Serializable;
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
    private final IBinder mMusicBind = new MusicBinder();

    private NotificationManager mNotifyManager;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int FOREGROUND_ID = 2;

    private boolean shuffle = false;
    private boolean repeat = false;
    private Random rand;

    private Boolean changeData = false;
    public static final String ACTION_PLAY = "notification_action_play";
    public static final String ACTION_NEXT = "notification_action_next";
    public static final String ACTION_PREV = "notification_action_prev";
    private RemoteViews notificationLayout;
    private RemoteViews notificationLayoutBig;

    //oncreat cho service
    @Override
    public void onCreate() {
        super.onCreate();
        mCurrentSong = 0;//khoi tao vi tri =0
        mPlayer = new MediaPlayer();
        initMusicPlayer();
        createNotificationChannel();
        rand = new Random();
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
        mPlayer.reset();
        SongModel playSong = songs.get(mCurrentSong);//get song
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

        SongModel song = songs.get(mCurrentSong);
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
                        .setAutoCancel(true);
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

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //start playback
        mediaPlayer.start();
        startForeground(FOREGROUND_ID, buildForegroundNotification());
        // sendBroadCastObjectService();
        getApplication().registerReceiver(receiverNotification, new IntentFilter(ACTION_PLAY));
        getApplication().registerReceiver(receiverNotification, new IntentFilter(ACTION_NEXT));
        getApplication().registerReceiver(receiverNotification, new IntentFilter(ACTION_PREV));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
        stopForeground(true);
        getApplication().unregisterReceiver(receiverNotification);
    }

    public SongModel getSongCurrent() {
        return songs.get(mCurrentSong);
    }

    //thiet lap bai hat hien tai duoc chon
    public void setSong(int songIndex) {
        mCurrentSong = songIndex;//su dung khi nguoi dung chon 1 bai hat tu danh sach
    }

    public int getmCurrentSong() {
        return mCurrentSong;
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
            sendBroadCast();
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
        } else {
            if (shuffle) {
                int newSong = mCurrentSong;
                while (newSong == mCurrentSong) {
                    newSong = rand.nextInt(songs.size());
                }
                mCurrentSong = newSong;
            } else {
                mCurrentSong--;
                if (mCurrentSong < 0) mCurrentSong = songs.size() - 1;
            }
            playSong();
        }
    }

    //choi bai hat sau, neu la bai cuoi thi choi lai bai dau tien
    public void playNext() {
        if (repeat) {
            mCurrentSong = mCurrentSong;
        } else if (shuffle) {
            int newSong = mCurrentSong;
            while (newSong == mCurrentSong) {
                newSong = rand.nextInt(songs.size());
            }
            mCurrentSong = newSong;
        } else {
            mCurrentSong++;
            if (mCurrentSong >= songs.size()) mCurrentSong = 0;
        }
        playSong();
    }

    //phat ngau nhien shuffle
    public void shuffeSong() {
        if (shuffle) shuffle = false;
        else shuffle = true;
    }

    public void repeatSong() {
        if (repeat) repeat = false;
        else repeat = true;
    }

    public void sendBroadCast() {
        String action = "my_action";
        Intent intent = new Intent(action);
        intent.setAction(action);//thiet lap ten de receiver nhan duoc thi nhan biet do la intent
        intent.putExtra("my_key", changeData);
        sendBroadcast(intent);
    }

    public BroadcastReceiver receiverNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_PLAY)) {
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    notificationLayout.setImageViewResource(R.id.playNoti, R.drawable.ic_play_22);
                    notificationLayoutBig.setImageViewResource(R.id.playNoti, R.drawable.ic_play_22);
                } else {
                    mPlayer.start();
                    notificationLayout.setImageViewResource(R.id.playNoti, R.drawable.ic_pause_22);
                    notificationLayoutBig.setImageViewResource(R.id.playNoti, R.drawable.ic_pause_22);
                }
            } else if (intent.getAction().equals(ACTION_NEXT)) {
                changeData = true;
                sendBroadCast();
                playNext();
                changeData = false;
            } else if (intent.getAction().equals(ACTION_PREV)) {
                changeData = true;
                sendBroadCast();
                playPrev();
                changeData = false;
            }
        }
    };

}
