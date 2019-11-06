package com.example.hanh6_11;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.example.hanh6_11.sqlite.FavoriteSongProvider;
import com.example.hanh6_11.sqlite.SongsFavoriteTable;

import java.io.FileDescriptor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SongGetter {
    private ArrayList<SongModel> mListSong = new ArrayList<>();
    private ArrayList<SongModel> mListAll = new ArrayList<>();
    ;
    private int mCurrentItemIndex;
    private Context mContext;
    private boolean mCheckdataChange = false;

    //set du lieu
    public SongGetter(Context context, int i, ArrayList<SongModel> songs) {
        mContext = context;
        getSongFromDevice(i, songs);
    }

    public List<SongModel> getLitSong() {
        return mListSong;
    }

    public ArrayList<SongModel> getmListAll() {
        return mListAll;
    }

    public ArrayList<SongModel> getSongFromDevice(int choose, ArrayList<SongModel> songs) {
        if (choose == 1) {
            mListSong = getAllSong();
        } else if (choose == 2 && songs != null) {
            mListSong = getFavoriteSong(songs);
        }
        return mListSong;
    }


    public ArrayList<SongModel> getAllSong() {
        mListAll = new ArrayList<>();
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String[] projetion = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._ID};
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projetion,
                selection,
                null,
                null);
        SongModel song;
        while (cursor.moveToNext()) {
            song = new SongModel();

            song.setId((int) cursor.getLong(4));
            //song.setNumber(i);
            song.setNameSong(cursor.getString(0));
            song.setAuthorSong(cursor.getString(1));
            long image = cursor.getLong(2);
            Bitmap img = getAlbumart(image);
            if (img != null) {
                song.setImageSong(img);
            } else {
                Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.mac_dinh);
                song.setImageSong(icon);
            }

            long duration = cursor.getLong(3);
            SimpleDateFormat dinhdang = new SimpleDateFormat("mm:ss");
            song.setTimeSong(dinhdang.format(duration));
            mListAll.add(song);
        }
        cursor.close();
        return mListAll;
    }

    public ArrayList<SongModel> getFavoriteSong(ArrayList<SongModel> list) {
        ArrayList<SongModel> songFavorite = new ArrayList<>();
        String selecfavorite = SongsFavoriteTable.IS_FAVORITE + "=2";
        String[] proje = {
                SongsFavoriteTable.ID_PROVIDER,
                SongsFavoriteTable.IS_FAVORITE,
                SongsFavoriteTable.COUNT_OF_PLAY};
        Cursor favorite = mContext.getContentResolver().query(FavoriteSongProvider.CONTENT_URI,
                proje, selecfavorite, null, null);
        if (favorite != null) {
            while (favorite.moveToNext())
                for (int j = 0; j < list.size(); j++) {
                    SongModel song1 = list.get(j);
                    if (song1.getId() == favorite.getInt(0)) {
                        songFavorite.add(song1);
                    }
                }
        }
        favorite.close();
        return songFavorite;
    }

    public Bitmap getAlbumart(long album_id) {
        Bitmap bm = null;
        try {
            final Uri artWorkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(artWorkUri, album_id);
            ParcelFileDescriptor pfd = mContext.getContentResolver()
                    .openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

    public int getCurrentItemIndex() {
        return mCurrentItemIndex;
    }

    public void setmCurrentItemIndex(int id) {
        mCurrentItemIndex = -1;
        for (int i = 0; i < mListSong.size(); i++) {
            SongModel song = mListSong.get(i);
            if (song.getId() == id) {
                mCurrentItemIndex = i;
                break;
            }
        }
    }

    public SongModel getCurrentItem() {
        return mListSong.get(mCurrentItemIndex);
    }

    public int getCount() {
        return mListSong.size();
    }

    public SongModel getSongAt(int pos) {
        return mListSong.get(pos);
    }

    public void setCurrentItemIndex(int pos) {
        if (pos < 0) {
            pos = -1;
        }
        if (pos >= getCount()) {
            pos = getCount() - 1;
        }
        mCurrentItemIndex = pos;
    }

    public void setmCheckdataChange(boolean mCheckdataChange) {
        this.mCheckdataChange = mCheckdataChange;
    }

    public boolean ismCheckdataChange() {
        return mCheckdataChange;
    }

    public void setmListSong(ArrayList<SongModel> mListSong) {
        this.mListSong = mListSong;
    }

    public int getIDSong() {
        int id = -1;
        for (int i = 0; i < mListSong.size(); i++) {
            SongModel songModel = mListSong.get(i);
            if (songModel.getNumber() == mCurrentItemIndex + 1) {
                id = songModel.getId();
                break;
            }
        }
        return id;
    }
}
