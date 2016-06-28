package com.example.khantilchoksi.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;


public class MainFragment extends Fragment {


    // TODO: Rename and change types of parameters
    private MovieAdapter movieAdapter;
    private TextView favoriteEmptyTextView;


    public MainFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        GridView gridView = (GridView) rootView.findViewById(R.id.moviesGrid);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movieAdapter.getItem(position);

                Intent movieDetailIntent = new Intent(getActivity(), DetailActivity.class);
                movieDetailIntent.putExtra("movie_obj", movie);
                startActivity(movieDetailIntent);
            }
        });

        favoriteEmptyTextView = (TextView) rootView.findViewById(R.id.favoriteEmpty);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = pref.getString("sort_movies","popular");

        if(sortBy.equals("favorite")){
            movieAdapter.clear();
            Log.d("Pref","Favorite is selected");
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(DetailActivity.SHARED_PREF_FILE, Context.MODE_PRIVATE);
            Set<String> favoriteMoviesSet = sharedPreferences.getStringSet(DetailActivity.FAVOURITE_MOVIES_KEY, null);

            if(favoriteMoviesSet != null){
//                String[] favoriteMoviesList = (String[]) favoriteMoviesSet.toArray();
                ArrayList<String> favoriteMovieList = new ArrayList<String>(favoriteMoviesSet);

                for(String movieId : favoriteMovieList){
                    Log.d(" Favourite Movie: ", movieId);
                    FetchFavoriteMovieTask fetchFavoriteMovieTask = new FetchFavoriteMovieTask(getActivity(), movieAdapter, movieId);
                    fetchFavoriteMovieTask.execute();
                }
            }else{
                favoriteEmptyTextView.setVisibility(View.VISIBLE);
            }

        }else{
            FetchMoviesTask fetchMovies = new FetchMoviesTask(getActivity(),movieAdapter);
            fetchMovies.execute();
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    //@Override
    /*public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
