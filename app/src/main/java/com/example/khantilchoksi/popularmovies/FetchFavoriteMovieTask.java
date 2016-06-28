package com.example.khantilchoksi.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
public class FetchFavoriteMovieTask extends AsyncTask<Void,Void,Movie> {
    private final String LOG_TAG = FetchFavoriteMovieTask.class.getSimpleName();
    private Context mContext;
    private MovieAdapter mMoviewAdapter;
    private String movieId;

    public FetchFavoriteMovieTask(Context mContext, MovieAdapter mMoviewAdapter,String movieId) {
        this.mContext = mContext;
        this.mMoviewAdapter = mMoviewAdapter;
        this.movieId = movieId;
    }

    @Override
    protected Movie doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;

        try{

            String baseURL = "https://api.themoviedb.org/3/movie/"+movieId;
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

            movieJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Movie JSON String "+movieJsonStr);

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
            return getMovieDataFromJson(movieJsonStr);
        }catch (JSONException je){
            Log.e(LOG_TAG,"JSON Exception", je);
            je.printStackTrace();
        }
        return null;
    }

    private Movie getMovieDataFromJson(String moviesJsonStr) throws JSONException{
        JSONObject movieJson = new JSONObject(moviesJsonStr);

            String id;
            String title;
            String releaseDate;
            String posterPath;
            String overview;
            String voteAverage;

            id = movieJson.getString("id");
            title = movieJson.getString("title");
            releaseDate = movieJson.getString("release_date");
            posterPath = movieJson.getString("poster_path");
            overview= movieJson.getString("overview");
            voteAverage = movieJson.getString("vote_average");

            Movie movie = new Movie(id,title,releaseDate,posterPath,overview, voteAverage);

            Log.d(LOG_TAG, "\n Movie  "+ movie.toString());


        return movie;
    }

    @Override
    protected void onPostExecute(Movie movie) {
        if(movie != null){
                mMoviewAdapter.add(movie);
        }
    }
}
