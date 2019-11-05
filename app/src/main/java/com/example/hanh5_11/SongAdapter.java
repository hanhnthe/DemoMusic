package com.example.hanh5_11;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanh5_11.sqlite.FavoriteSongProvider;
import com.example.hanh5_11.sqlite.SongsFavoriteTable;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    protected SongGetter mSongGetter;
    private OnSongClickListener mOnSongClickListener;
    private Context mContext;
    private int mCheckFavorite;

    //contructor
    public SongAdapter(SongGetter songGetter, int check) {
        mSongGetter = songGetter;
        mCheckFavorite = check;
    }

    @NonNull
    @Override
    public SongAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View item = layoutInflater.inflate(R.layout.song_one_row, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final SongAdapter.MyViewHolder holder, final int position) {
        final SongModel songModel = mSongGetter.getSongAt(position);

        songModel.setNumber(position + 1);
        final TextView text1 = holder.textTime;
        text1.setText(songModel.getTimeSong());
        holder.textName.setText(songModel.getNameSong());
        if (getmPos() == position) {
            holder.textName.setTypeface(null, Typeface.BOLD);
            holder.textNumber.setText("");
            holder.textNumber.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_notification, 0, 0);
        } else {
            holder.textName.setTypeface(null, Typeface.NORMAL);
            holder.textNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.textNumber.setText("" + songModel.getNumber());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSongGetter.setCurrentItemIndex(position);
                SongModel song = mSongGetter.getCurrentItem();
                if (mOnSongClickListener != null) {
                    mOnSongClickListener.onClickItem(song);
                    notifyDataSetChanged();
                }
            }
        });
        if (mCheckFavorite == 1) {
            holder.popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(mContext, v);
                    menu.getMenuInflater().inflate(R.menu.options_menu_all, menu.getMenu());
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.favoriteButtom: {
                                    addFavorite(songModel);
                                    mSongGetter.setmCheckdataChange(true);
                                    break;
                                }
                                case R.id.removebuttom: {
                                    removeFavorite(songModel);
                                    mSongGetter.setmCheckdataChange(true);
                                    break;
                                }
                            }
                            return false;
                        }
                    });
                    menu.show();
                }
            });
        } else if (mCheckFavorite == 2) {
            holder.popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(mContext, v);
                    menu.getMenuInflater().inflate(R.menu.favoritesongmenu, menu.getMenu());
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.removebuttom: {
                                    mSongGetter.getLitSong().remove(position);
                                    notifyItemRemoved(position);
                                    removeFavorite(songModel);
                                    break;
                                }
                            }
                            return false;
                        }
                    });
                    menu.show();
                }
            });
        }
    }

    public Cursor findSongById(int id) {
        return mContext.getContentResolver().query(FavoriteSongProvider.CONTENT_URI, new String[]{SongsFavoriteTable.IS_FAVORITE},
                SongsFavoriteTable.ID_PROVIDER + "=?",
                new String[]{String.valueOf(id)}, null);
    }

    public void removeFavorite(SongModel song) {
        ContentValues values = new ContentValues();
        Cursor cursor = findSongById(song.getId());
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            System.out.println(count);
            if (count == 2) {
                values.put(SongsFavoriteTable.IS_FAVORITE, 1);
                Toast.makeText(mContext, "Da xoa bai hat khoi yeu thich", Toast.LENGTH_SHORT).show();
                mContext.getContentResolver().update(FavoriteSongProvider.CONTENT_URI, values,
                        "id_provider = \"" + song.getId() + "\"", null);
            } else {
                Toast.makeText(mContext, "bai hat chua dc yeu thich", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } else {
            Toast.makeText(mContext, "bai hat chua dc yeu thich", Toast.LENGTH_SHORT).show();
        }
    }

    public void addFavorite(SongModel song) {
        ContentValues values = new ContentValues();
        Cursor cursor = findSongById(song.getId());
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            System.out.println(count);
            if (count == 2) {
                Toast.makeText(mContext.getApplicationContext(), "bài hát đã được yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                values.put(SongsFavoriteTable.IS_FAVORITE, 2);
                Toast.makeText(mContext.getApplicationContext(), "đã thêm bài hát vao yeu thich", Toast.LENGTH_SHORT).show();
                mContext.getContentResolver().update(FavoriteSongProvider.CONTENT_URI, values,
                        "id_provider = \"" + song.getId() + "\"", null);

            }
            cursor.close();
        } else {
            values.put(SongsFavoriteTable.COUNT_OF_PLAY, 3);
            values.put(SongsFavoriteTable.ID_PROVIDER, song.getId());
            values.put(SongsFavoriteTable.IS_FAVORITE, 2);
            mContext.getContentResolver().insert(FavoriteSongProvider.CONTENT_URI, values);
            Toast.makeText(mContext.getApplicationContext(), "đã thêm bài hát vao yeu thich", Toast.LENGTH_SHORT).show();
        }
    }

    public int getmPos() {
        return mSongGetter.getCurrentItemIndex();
    }

    @Override
    public int getItemCount() {
        return mSongGetter.getCount();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textNumber, textName, textTime;
        public ImageButton popup;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.nameSong);
            textNumber = (TextView) itemView.findViewById(R.id.numberSong);
            textTime = (TextView) itemView.findViewById(R.id.timeSong);
            popup = (ImageButton) itemView.findViewById(R.id.popup);
        }
    }

    public void setOnSongclickListener(OnSongClickListener listener) {
        mOnSongClickListener = listener;

    }

}
