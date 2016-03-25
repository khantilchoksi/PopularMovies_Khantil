package com.example.khantilchoksi.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * //Use the {@link MainFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private MovieAdapter movieAdapter;
    //private ArrayList<Movie> movieList;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    /*public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

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
                movieDetailIntent.putExtra("movie_obj",movie);
                startActivity(movieDetailIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        FetchMovies fetchMovies = new FetchMovies();
        fetchMovies.execute();
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

    public class FetchMovies extends AsyncTask<Void, Void, Movie[]>{
        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try{
                String baseURL = "https://api.themoviedb.org/3/movie/popular";
                String apiKey = "?api_key=" + BuildConfig.MOVIEDB_API_KEY;

                URL url = new URL(baseURL.concat(apiKey));
                //Create the request to moviedb api
                urlConnection= (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream== null){
                    Log.e(LOG_TAG,"Input Stream is null");
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0){
                    return null;
                }

                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movies JSON String "+moviesJsonStr);

            }catch(IOException e){
                Log.e(LOG_TAG,"Error in Internet Connection", e);
            } finally{
                if(urlConnection!= null){
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try{
                        reader.close();
                    }catch (final IOException e){
                        Log.e(LOG_TAG,"Error closing Buffered Reader", e);
                    }
                }
            }

            try{
                return getMoviesDataFromJson(moviesJsonStr);
            }catch (JSONException je){
                Log.e(LOG_TAG,"JSON Exception", je);
                je.printStackTrace();
            }
            return null;
        }

        private Movie[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException{
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray("results");

            Movie[] movies = new Movie[moviesArray.length()];

            for(int i=0; i < moviesArray.length(); i++){

                String id;
                String title;
                String releaseDate;
                String posterPath;

                JSONObject movieDetails = moviesArray.getJSONObject(i);
                id = movieDetails.getString("id");
                title = movieDetails.getString("title");
                releaseDate = movieDetails.getString("release_date");
                posterPath = movieDetails.getString("poster_path");

                movies[i] = new Movie(id,title,releaseDate,posterPath);

            }

            for(Movie m : movies){
                Log.v(LOG_TAG, "\n Movie  "+ m.toString());
            }

            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if(movies != null){
                movieAdapter.clear();

                for(Movie m : movies){
                    movieAdapter.add(m);
                }
            }
        }
    }
}