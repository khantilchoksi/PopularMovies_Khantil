package com.example.khantilchoksi.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Khantil on 28-06-2016.
 */
public class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private Context mContext;
    private MovieAdapter mMovieAdapter;

    public FetchMoviesTask(Context mContext, MovieAdapter mMoviewAdapter) {
        this.mContext = mContext;
        this.mMovieAdapter = mMoviewAdapter;
    }

    @Override
    protected Movie[] doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        try{
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
            String sortBy = pref.getString("sort_movies","popular");

            String baseURL = "https://api.themoviedb.org/3/movie/"+sortBy;
            String apiKey = "?api_key=" + BuildConfig.MOVIEDB_API_KEY;

            URL url = new URL(baseURL.concat(apiKey));
            Log.d(LOG_TAG, "URL : " + url.toString());
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
            String overview;
            String voteAverage;

            JSONObject movieDetails = moviesArray.getJSONObject(i);
            id = movieDetails.getString("id");
            title = movieDetails.getString("title");
            releaseDate = movieDetails.getString("release_date");
            posterPath = movieDetails.getString("poster_path");
            overview= movieDetails.getString("overview");
            voteAverage = movieDetails.getString("vote_average");

            movies[i] = new Movie(id,title,releaseDate,posterPath,overview, voteAverage);

        }

        for(Movie m : movies){
            Log.v(LOG_TAG, "\n Movie  "+ m.toString());
        }

        return movies;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        if(movies != null){
            mMovieAdapter.clear();

            for(Movie m : movies){
                mMovieAdapter.add(m);
            }

            if(MainActivity.mTwoPane){
                //Tablet UX
                Log.d(LOG_TAG,"Two pane layout initialized with first movie details");
                Bundle args = new Bundle();
                args.putParcelable(DetailActivityFragment.DETAIL_MOVIE_OBJ, mMovieAdapter.getItem(0));

                DetailActivityFragment detailFragment = new DetailActivityFragment();
                detailFragment.setArguments(args);

                AppCompatActivity appCompatActivity = (AppCompatActivity) mContext;
                appCompatActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, detailFragment, MainActivity.DETAILFRAGMENT_TAG)
                        .commit();
            }

        }
    }
}
