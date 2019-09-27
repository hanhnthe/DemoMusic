package com.example.hanhcopy26_9;

import android.content.res.Configuration;
import android.os.Bundle;

import com.example.hanhcopy26_9.fragment.AllSongsFragment;
import com.example.hanhcopy26_9.fragment.DetailSongFragment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityMusic extends AppCompatActivity implements OnSongClickListener, View.OnClickListener {
    FragmentManager fragmentManager;
    ActionBar actionBar ;
    AllSongsFragment allSongsFragment = new AllSongsFragment();
    DetailSongFragment detailSongFragment = new DetailSongFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        //actionBar.hide();
       if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment,allSongsFragment);
            fragmentTransaction.commit();
            allSongsFragment.setOnClickListener(this);
        }
        else{
            View view1 = findViewById(R.id.linearLayout3);
            view1.setVisibility(View.GONE);
        }




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClickItem(SongModel song) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        actionBar.hide();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment,detailSongFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}
