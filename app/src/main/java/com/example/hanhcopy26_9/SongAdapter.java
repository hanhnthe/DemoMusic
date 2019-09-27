package com.example.hanhcopy26_9;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
        final SongModel songModel = mSongGetter.getSongAt(position);

        //set du lieu vao trong dong
        final TextView text1 = holder.textTime;
        text1.setText(songModel.getTimeSong());
        holder.textNumber.setText("" + songModel.getNumber());
        holder.textName.setText(songModel.getNameSong());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSongGetter.setCurrentItemIndex(position);
                SongModel song = mSongGetter.getCurrentItem();

                if (mListener != null) {
                    mListener.onClickItem(song);
                    TextView view1 = view.findViewById(R.id.nameSong);
                    view1.setTypeface(null,Typeface.BOLD);
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

    private OnSongClickListener mListener;

    public void setOnclickListener(OnSongClickListener listener) {
        mListener = listener;

    }

}
