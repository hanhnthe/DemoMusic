package com.example.hanh4_10;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class SongGetter {
    private List<SongModel> litSong = new ArrayList<>();
    private int mCurrentItemIndex;
    private Context mContext;

    public SongGetter() {
        //getMp3FilesFromMemory();

    }

    //set du lieu
    public SongGetter(Context context) {
        //mSong = allSongsFragment.getMp3FilesFromMemory();
        mContext = context;
        mCurrentItemIndex=0;
        getMp3FilesFromMemory();

    }


    public List<SongModel> getMp3FilesFromMemory() {

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        String[] projetion = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._ID
        };
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projetion,
                selection,
                null,
                null);

//        Cursor cursorAlbums = context.getContentResolver().query(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                new String[] {MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM},
//                MediaStore.Audio.Media.ALBUM_ID + "=?",
//                new String[]{String.valueOf(albumId)},null);
//        long albumId = cursor.getLong(
//                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        SongModel song;
        int i = 0;
        while (cursor.moveToNext()) {
            song = new SongModel();
            i++;
            song.setId((int) cursor.getLong(4));
            song.setNumber(i);
            song.setNameSong(cursor.getString(0));
            song.setAuthorSong(cursor.getString(1));
            // song.setImageSong(cursor.getString(2));
            long duration = cursor.getLong(3);

            song.setTimeSong(convertDuration(duration));
            litSong.add(song);
        }
        cursor.close();
        return litSong;


    }

    public String convertDuration(long duration) {
        String out = null;
        long hours = 0;
        try {
            hours = (duration / 3600000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return out;
        }
        long remaining_minutes = (duration - (hours * 3600000)) / 60000;
        String minutes = String.valueOf(remaining_minutes);
        if (minutes.equals(0)) {
            minutes = "00";
        }
        long remaining_seconds = (duration - (hours * 3600000) - (remaining_minutes * 60000));
        String seconds = String.valueOf(remaining_seconds);
        if (seconds.length() < 2) {
            seconds = "00";
        } else {
            seconds = seconds.substring(0, 2);
        }

        if (hours > 0) {
            out = hours + ":" + minutes + ":" + seconds;
        } else {
            out = minutes + ":" + seconds;
        }

        return out;

    }

    public SongModel getNextSong(){
        mCurrentItemIndex++;
        if (mCurrentItemIndex >= litSong.size()) {
            mCurrentItemIndex = litSong.size() - 1;
        }
        return litSong.get(mCurrentItemIndex);
    }
    public int getCurrentItemIndex(){
        return mCurrentItemIndex;
    }
    public SongModel getCurrentItem(){
        return litSong.get(mCurrentItemIndex);
    }
    public boolean hasNext(){
        return mCurrentItemIndex < litSong.size() - 1;
    }
    public boolean hasPrevious(){
        return mCurrentItemIndex>0;
    }
    public int getCount(){
        return litSong.size();
    }
    public SongModel getSongAt(int pos){
        return litSong.get(pos);
    }
    public void setCurrentItemIndex(int pos){
        if(pos<0) pos=0;
        if(pos>=getCount()) pos=getCount()-1;
        mCurrentItemIndex=pos;
    }
    public void setCurrentSongNumber(int number){
        for (int i = 0; i < litSong.size(); i++) {
            SongModel song = new SongModel();
            if(number == song.getNumber()){
                mCurrentItemIndex=i;
                return;
            }
        }
    }

}
