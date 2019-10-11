package com.example.hanh10_10;

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
    private int mPos = -1;


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
        holder.textNumber.setText(Integer.toString(songModel.getNumber()));
        if (mPos == position) {
            holder.textName.setTypeface(null, Typeface.BOLD);
        } else {
            holder.textName.setTypeface(null, Typeface.NORMAL);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSongGetter.setCurrentItemIndex(position);
                SongModel song = mSongGetter.getCurrentItem();
                mPos = holder.getLayoutPosition();
                if (mOnSongClickListener != null) {
                    mOnSongClickListener.onClickItem(song);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongGetter.getCount();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

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
