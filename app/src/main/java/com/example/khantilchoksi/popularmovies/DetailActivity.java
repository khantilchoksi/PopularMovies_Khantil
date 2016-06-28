package com.example.khantilchoksi.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    public static final String SHARED_PREF_FILE = "fav_movies";
    public static final String FAVOURITE_MOVIES_KEY = "fav_movies_key";
    private Movie mMovie = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if(intent!= null && intent.hasExtra("movie_obj")){
            mMovie = (Movie) intent.getParcelableExtra("movie_obj");

            final CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(mMovie.title);


            String poster = "http://image.tmdb.org/t/p/w185/"+mMovie.posterPath;

            ImageView collapsingToolbarImageView = (ImageView) findViewById(R.id.collapsingToolbarImage);
            Picasso.with(this).load(poster).into(collapsingToolbarImageView);

        }

        final FloatingActionButton favouriteFloatingActionButton = (FloatingActionButton) findViewById(R.id.favoriteFloatingActionButton);

        final SharedPreferences pref = getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        Set<String> set = pref.getStringSet(FAVOURITE_MOVIES_KEY, null);
        if( set!= null && set.contains(mMovie.getId())) {
            favouriteFloatingActionButton.setImageResource(R.drawable.ic_favorite_pink);
        }

        if (favouriteFloatingActionButton != null) {
            favouriteFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Set<String> set = pref.getStringSet(FAVOURITE_MOVIES_KEY, null);
                    if(set!= null && set.contains(mMovie.getId())){
                        favouriteFloatingActionButton.setImageResource(R.mipmap.ic_favorite);
                        set = new HashSet<String>(set);
                        set.remove(mMovie.getId());
                        editor.putStringSet(FAVOURITE_MOVIES_KEY, set);
                        editor.commit();
                        Log.d(LOG_TAG, "Movie : " + mMovie.title + " has been REMOVED from shared preferences.");
                    }else{
                        favouriteFloatingActionButton.setImageResource(R.drawable.ic_favorite_pink);
                        if(set == null){
                            set = new HashSet<String>();
                        }else{
                            set = new HashSet<String>(set);
                        }
                        set = new HashSet<String>(set);
                        set.add(mMovie.getId());
                        editor.putStringSet(FAVOURITE_MOVIES_KEY, set);
                        editor.commit();
                        Log.d(LOG_TAG, "Movie : " + mMovie.title + " has been ADDED to shared preferrences.");
                    }
                }
            });
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
