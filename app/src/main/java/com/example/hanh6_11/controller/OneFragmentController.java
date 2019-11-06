package com.example.hanh6_11.controller;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanh6_11.OnSongClickListener;
import com.example.hanh6_11.R;
import com.example.hanh6_11.SongModel;
import com.example.hanh6_11.fragment.AllSongsFragment;
import com.example.hanh6_11.fragment.FavoriteSongsFragment;
import com.example.hanh6_11.fragment.MediaPlaybackFragment;

public class OneFragmentController extends LayoutController implements View.OnClickListener {

    private Bundle mBundle;//khai bao luu tru gia tri hien tai

    public OneFragmentController(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void onCreate(int i) {
        if (mActivity.findViewById(R.id.container_fragment) != null) {
            if (i == 1) {
                mBaseSongListFragment = new AllSongsFragment();
            } else if (i == 2) {
                mBaseSongListFragment = new FavoriteSongsFragment();
            }

            mBaseSongListFragment.setOnClickListener(this);
            mBaseSongListFragment.setOnSongClickListener(this);
            mActivity.getSupportFragmentManager().beginTransaction().
                    replace(R.id.container_fragment, mBaseSongListFragment).commit();
        }
    }

    @Override
    public void onClickItem(final SongModel item) {
        View view = mBaseSongListFragment.getView();
        mOnclickService.onClickItem(item);
        mBundle = newBundleFromSong(item);// khoi tao bien bunlde vao item click
        String name, author;
        Bitmap image;
        name = item.getNameSong();
        author = item.getAuthorSong();
        image = item.getImageSong();

        TextView nameSong2 = (TextView) view.findViewById(R.id.nameSong2);
        nameSong2.setText(name);
        TextView authorSong = (TextView) view.findViewById(R.id.author1);
        authorSong.setText(author);
        ImageView imageView = (ImageView) view.findViewById(R.id.image1);
        imageView.setImageBitmap(image);
        ImageButton play = (ImageButton) view.findViewById(R.id.playSong1);
        play.setImageResource(R.drawable.ic_pause_1);

        clickSongFavorite(item);

    }

    @Override
    public void onClick(View view) {
        mActivity.getSupportActionBar().hide();
        MediaPlaybackFragment mediaPlaybackFragment = new MediaPlaybackFragment();
        mActivity.getSupportFragmentManager().beginTransaction().
                replace(R.id.container_fragment, mediaPlaybackFragment).
                addToBackStack(null).commit();
    }

    @Override
    public void setmOnclickService(OnSongClickListener click) {
        mOnclickService = click;
    }


}
