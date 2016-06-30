package com.example.khantilchoksi.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;

/**
 * Created by Khantil on 28-06-2016.
 */
//AsyncTask for fetching movie trailer
public class FetchTrailersTask extends AsyncTask<Void, Void, ArrayList<Trailer>> {
    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private TextView mNoTrailerTextView;
    private RecyclerView mTrailersRecyclerView;
    private Movie mMovie;
    private Context mContext;

    public FetchTrailersTask(Context context,TextView mNoTrailerTextView, RecyclerView mTrailersRecyclerView, Movie mMovie) {
        this.mContext = context;
        this.mNoTrailerTextView = mNoTrailerTextView;
        this.mTrailersRecyclerView = mTrailersRecyclerView;
        this.mMovie = mMovie;
    }

    @Override
    protected ArrayList<Trailer> doInBackground(Void... params) {
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

    private ArrayList<Trailer> getTrailersDataFromJson(String trailersJsonStr) throws JSONException{
        Log.d(LOG_TAG,"Trailer getTrailerDataFromJson called");
        JSONObject moviesJson = new JSONObject(trailersJsonStr);
        JSONArray trailersArray = moviesJson.getJSONArray("results");

        ArrayList<Trailer> trailersArrayList = new ArrayList<>();
        Log.d(LOG_TAG,"Trailer Size:  " + trailersArray.length());
        for(int i=0; i < trailersArray.length(); i++){

            String id;
            String name;
            String key;

            JSONObject trialerDetailsJSONObject = trailersArray.getJSONObject(i);
            id = trialerDetailsJSONObject.getString("id");
            name = trialerDetailsJSONObject.getString("name");
            key = trialerDetailsJSONObject.getString("key");

            trailersArrayList.add(new Trailer(id,name,key));

        }

        for(Trailer t : trailersArrayList){
            Log.d(LOG_TAG, "Trailer  " + t.toString());
        }

        return trailersArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Trailer> trailers) {
        if(trailers != null) {

/*                for(Trailer t : trailers){
                    Log.d(LOG_TAG, "Trailer added to adapter:  " + t.getTrailerName());
                    mTrailerAdapter.add(t);
                }*/
            if(trailers.size() == 0){
                mNoTrailerTextView.setVisibility(View.VISIBLE);
            }else{

                mTrailersRecyclerView.setAdapter(new TrailersRecyclerViewAdapter(mContext, trailers));

            }

        }
    }

}
