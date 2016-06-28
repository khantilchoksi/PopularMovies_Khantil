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
import java.util.List;

/**
 * Created by Khantil on 26-06-2016.
 */
public class FetchReviewsTask extends AsyncTask<Void,Void,ArrayList<ArrayList<String>>>{

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private Context mContext;
    private Movie mMovie;
    private RecyclerView mReviewsRecyclerView;
    private TextView mNoReviewsTextView;

    public FetchReviewsTask(Context mContext, Movie mMovie,RecyclerView mReviewsRecyclerView, TextView mNoReviewsTextView) {
        this.mContext = mContext;
        this.mMovie = mMovie;
        this.mReviewsRecyclerView = mReviewsRecyclerView;
        this.mNoReviewsTextView = mNoReviewsTextView;
    }

    @Override
    protected ArrayList<ArrayList<String>> doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String reviewsJsonStr = null;

        try{

            String baseURL = "https://api.themoviedb.org/3/movie/"+mMovie.getId()+"/reviews";
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

            reviewsJsonStr = buffer.toString();
            Log.d(LOG_TAG, "Reviews JSON String "+reviewsJsonStr);

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
            return getReviewsDataFromJson(reviewsJsonStr);
        }catch (JSONException je){
            Log.e(LOG_TAG,"JSON Exception", je);
            je.printStackTrace();
        }
        return null;
    }

    private ArrayList<ArrayList<String>> getReviewsDataFromJson(String trailersJsonStr) throws JSONException{

        JSONObject reviewsJsonObject = new JSONObject(trailersJsonStr);
        JSONArray reviewsArray = reviewsJsonObject.getJSONArray("results");

        //Two dimensional string array, one column for author and second column for content
        ArrayList<ArrayList<String>> reviewsArrayList = new ArrayList<ArrayList<String>>();

        for(int i=0; i < reviewsArray.length(); i++){

            String author;
            String content;

            JSONObject trialerDetailsJSONObject = reviewsArray.getJSONObject(i);
            author = trialerDetailsJSONObject.getString("author");
            content = trialerDetailsJSONObject.getString("content");

            ArrayList<String> review = new ArrayList<String>();
            review.add(author);
            review.add(content);

            reviewsArrayList.add(review);
        }

        for(ArrayList<String> review: reviewsArrayList){
            Log.d(LOG_TAG, "Review: Author:  " + review.toString());
        }

        return reviewsArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList<String>> reviews) {
        if(reviews != null) {
//            Log.d(LOG_TAG, "Trailer adapter after clearing:  " + mTrailerAdapter.getCount());

            mReviewsRecyclerView.setAdapter(new ReviewsRecyclerViewAdapter(mContext, reviews));
        }

        if(reviews.size() == 0){
            mNoReviewsTextView.setVisibility(View.VISIBLE);
        }
    }

}

