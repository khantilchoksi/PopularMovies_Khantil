package com.example.khantilchoksi.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private Movie mMovie;
    private TrailerAdapter mTrailerAdapter;
    private ListView mTrailersListView;
    private RecyclerView mReviewsRecyclerView;
    private ReviewsRecyclerViewAdapter mReviewsRecyclerViewAdapter;


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if(intent!= null && intent.hasExtra("movie_obj")){
            mMovie = (Movie) intent.getParcelableExtra("movie_obj");

            ((TextView) rootView.findViewById(R.id.moviewOverview)).setText(mMovie.overview);
            ((TextView) rootView.findViewById(R.id.userRatings)).setText(mMovie.voteAverage);
            ((TextView) rootView.findViewById(R.id.releaseDate)).setText(mMovie.releaseDate);
        }

        mTrailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>(3));

        mTrailersListView = (ListView) rootView.findViewById(R.id.trailerListView);
        mTrailersListView.setAdapter(mTrailerAdapter);
        /*setListViewHeightBasedOnChildren();

        mTrailersListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/

        mTrailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trailer getTrailer = mTrailerAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + getTrailer.getTrailerKey()));
                startActivity(intent);
            }
        });

        mReviewsRecyclerView = (RecyclerView) rootView.findViewById(R.id.reviewsRecyclerView);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(mReviewsRecyclerView.getContext()));
        mReviewsRecyclerViewAdapter = new ReviewsRecyclerViewAdapter(getActivity(),
                new ArrayList<ArrayList<String>>());

/*        mReviewsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });*/

/*        //add ItemDecoration
//        mReviewsRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(1));
        mReviewsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));*/

        return rootView;
    }

    public void setListViewHeightBasedOnChildren() {
        ListAdapter listAdapter = mTrailersListView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mTrailersListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, mTrailersListView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mTrailersListView.getLayoutParams();
        params.height = totalHeight + (mTrailersListView.getDividerHeight() * (listAdapter.getCount() - 1));
        mTrailersListView.setLayoutParams(params);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchTrailers fetchTrailers = new FetchTrailers();
        fetchTrailers.execute();

        FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getActivity(),mMovie, mReviewsRecyclerView);
        fetchReviewsTask.execute();

    }

    //AsyncTask for fetching movie trailer
    public class FetchTrailers extends AsyncTask<Void, Void, Trailer[]> {
        private final String LOG_TAG = FetchTrailers.class.getSimpleName();

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
                for(int i=0;i<trailers.length;i++){
                    Log.d(LOG_TAG, "Trailer added to adapter:  " + trailers[i].getTrailerName());
                    mTrailerAdapter.add(trailers[i]);
                }
                setListViewHeightBasedOnChildren();
            }
        }

    }
}
