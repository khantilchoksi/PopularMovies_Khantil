package com.example.khantilchoksi.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if(intent!= null && intent.hasExtra("movie_obj")){
            Movie detailMovie = (Movie) intent.getParcelableExtra("movie_obj");

            ((TextView) rootView.findViewById(R.id.movieName)).setText(detailMovie.title);
        }
        return null;
    }
}