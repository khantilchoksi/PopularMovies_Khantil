package com.example.khantilchoksi.popularmovies;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
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

/**
 * Created by Khantil on 28-06-2016.
 */
//AsyncTask for fetching movie trailer
public class FetchTrailersTask extends AsyncTask<Void, Void, Trailer[]> {
    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private TextView mNoTrailerTextView;
    private TrailerAdapter mTrailerAdapter;
    private Movie mMovie;

    public FetchTrailersTask(TextView mNoTrailerTextView, TrailerAdapter mTrailerAdapter, Movie mMovie) {
        this.mNoTrailerTextView = mNoTrailerTextView;
        this.mTrailerAdapter = mTrailerAdapter;
        this.mMovie = mMovie;
    }

    @Override
    protected Trailer[] doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String trailersJsonStr = null;

        try{

            String baseURL = "https://api.themoviedb.org/3/movie/"+mMovie.getId()+"/videos";
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

            trailersJsonStr = buffer.toString();
            Log.d(LOG_TAG, "Trailers JSON String "+trailersJsonStr);

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
            return getTrailersDataFromJson(trailersJsonStr);
        }catch (JSONException je){
            Log.e(LOG_TAG,"JSON Exception", je);
            je.printStackTrace();
        }
        return null;
    }

    private Trailer[] getTrailersDataFromJson(String trailersJsonStr) throws JSONException{
        Log.d(LOG_TAG,"Trailer getTrailerDataFromJson called");
        JSONObject moviesJson = new JSONObject(trailersJsonStr);
        JSONArray trailersArray = moviesJson.getJSONArray("results");

        Trailer[] trailers = new Trailer[trailersArray.length()];
        Log.d(LOG_TAG,"Trailer Size:  " + trailersArray.length());
        for(int i=0; i < trailersArray.length(); i++){

            String id;
            String name;
            String key;

            JSONObject trialerDetailsJSONObject = trailersArray.getJSONObject(i);
            id = trialerDetailsJSONObject.getString("id");
            name = trialerDetailsJSONObject.getString("name");
            key = trialerDetailsJSONObject.getString("key");

            trailers[i] = new Trailer(id,name,key);

        }

        for(Trailer t : trailers){
            Log.d(LOG_TAG, "Trailer  " + t.toString());
        }

        return trailers;
    }

    @Override
    protected void onPostExecute(Trailer[] trailers) {
        if(trailers != null){
            mTrailerAdapter.clear();
            Log.d(LOG_TAG, "Trailer adapter after clearing:  " + mTrailerAdapter.getCount());

/*                for(Trailer t : trailers){
                    Log.d(LOG_TAG, "Trailer added to adapter:  " + t.getTrailerName());
                    mTrailerAdapter.add(t);
                }*/
            if(trailers.length == 0){
                mNoTrailerTextView.setVisibility(View.VISIBLE);
            }else{
                for(int i=0;i<trailers.length;i++){
                    Log.d(LOG_TAG, "Trailer added to adapter:  " + trailers[i].getTrailerName());
                    mTrailerAdapter.add(trailers[i]);
                }

            }

        }
    }

}
