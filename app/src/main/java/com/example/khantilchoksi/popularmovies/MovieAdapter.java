package com.example.khantilchoksi.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by khantilchoksi on 24/03/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, List<Movie> movieList){
        super(context, 0, movieList);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Movie movie = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_poster, parent, false);
        }

        String poster = "http://image.tmdb.org/t/p/w185/"+movie.posterPath;

        ImageView moviePoster = (ImageView) convertView.findViewById(R.id.movieImage);
            Picasso.with(getContext()).load(poster).into(moviePoster);

        if(MainActivity.mTwoPane && position == 0){
            convertView.performClick();
        }


        return convertView;
    }

}
