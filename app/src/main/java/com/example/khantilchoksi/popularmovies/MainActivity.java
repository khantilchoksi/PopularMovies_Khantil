package com.example.khantilchoksi.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback{

    public static boolean mTwoPane;
    public static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



/*        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mainContainer,new MainFragment())
                    .commit();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(findViewById(R.id.movie_detail_container)!= null){
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            Log.d(LOG_TAG, "Two Pane Layout is there!");
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
/*            if(savedInstanceState==null){
                Log.d(LOG_TAG, "Creating Details Fragment Container!");
                *//*getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container,new DetailActivityFragment(),DETAILFRAGMENT_TAG)
                        .commit();*//*
            }*/

        }else{
            Log.d(LOG_TAG, "Two Pane Layout is not there!");
            mTwoPane = false;
            //not to show the shadow below the action bar when it is not the two pane mode
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id== R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movie) {
        if(mTwoPane){
            //That is table two pane view is there
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_MOVIE_OBJ, movie);

            DetailActivityFragment detailFragment = new DetailActivityFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();

        }else{
            //phone view
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivityFragment.DETAIL_MOVIE_OBJ, movie);
            startActivity(intent);
        }
    }
    /*@Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
    }*/
}
