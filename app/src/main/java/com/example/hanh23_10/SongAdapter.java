package com.example.hanh23_10;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    protected SongGetter mSongGetter;
    private OnSongClickListener mOnSongClickListener;

    //contructor
    public SongAdapter(SongGetter songGetter) {
        mSongGetter = songGetter;
    }

    @NonNull
    @Override
    public SongAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View item = layoutInflater.inflate(R.layout.song_one_row, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final SongAdapter.MyViewHolder holder, final int position) {
        SongModel songModel = mSongGetter.getSongAt(position);

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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.nameSong);
            textNumber = (TextView) itemView.findViewById(R.id.numberSong);
            textTime = (TextView) itemView.findViewById(R.id.timeSong);
        }
    }

    public void setOnSongclickListener(OnSongClickListener listener) {
        mOnSongClickListener = listener;

    }

}
