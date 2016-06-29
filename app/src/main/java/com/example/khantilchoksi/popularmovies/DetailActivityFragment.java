package com.example.khantilchoksi.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.HashSet;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private Movie mMovie = null;
    private TrailerAdapter mTrailerAdapter;
    private ListView mTrailersListView;
    private RecyclerView mReviewsRecyclerView;
    private ReviewsRecyclerViewAdapter mReviewsRecyclerViewAdapter;
    private TextView mNoReviewsTextView;
    private TextView mNoTrailerTextView;

    public static final String SHARED_PREF_FILE = "fav_movies";
    public static final String FAVOURITE_MOVIES_KEY = "fav_movies_key";

    public static final String DETAIL_MOVIE_OBJ = "movie_obj";


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if(MainActivity.mTwoPane){
            //Tablet UX
        }else{
            //Phone UX
            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            toolbar.setCollapsible(false);

            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        Bundle arguments = getArguments();
        if(arguments != null){
            mMovie = (Movie) arguments.getParcelable(DETAIL_MOVIE_OBJ);
            Log.d(LOG_TAG, "Received intent movie :  "+mMovie.title);
            final CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(mMovie.title);
            collapsingToolbar.setEnabled(false);

            String poster = "http://image.tmdb.org/t/p/w185/"+mMovie.posterPath;

            ImageView collapsingToolbarImageView = (ImageView) rootView.findViewById(R.id.collapsingToolbarImage);
            Picasso.with(getActivity()).load(poster).into(collapsingToolbarImageView);
            ((TextView) rootView.findViewById(R.id.movieOverview)).setText(mMovie.overview);
            ((TextView) rootView.findViewById(R.id.userRatings)).setText(mMovie.voteAverage);
            ((TextView) rootView.findViewById(R.id.releaseDate)).setText(mMovie.releaseDate);
        }else{
            Log.d(LOG_TAG, "Received intent movie :  "+"Argumets is null");
        }
/*        Intent intent = getActivity().getIntent();

        if(intent!= null && intent.hasExtra("movie_obj")){
            mMovie = (Movie) intent.getParcelableExtra("movie_obj");

            ((TextView) rootView.findViewById(R.id.movieOverview)).setText(mMovie.overview);
            ((TextView) rootView.findViewById(R.id.userRatings)).setText(mMovie.voteAverage);
            ((TextView) rootView.findViewById(R.id.releaseDate)).setText(mMovie.releaseDate);
        }*/

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

        mNoReviewsTextView = (TextView) rootView.findViewById(R.id.noReviewsTextView);
        mNoTrailerTextView = (TextView) rootView.findViewById(R.id.noTrailersTextView);

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

        if(mMovie !=null){
            FetchTrailersTask fetchTrailers = new FetchTrailersTask(mNoTrailerTextView,mTrailerAdapter,mMovie);
            fetchTrailers.execute();
            setListViewHeightBasedOnChildren();


            FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getActivity(),mMovie, mReviewsRecyclerView,mNoReviewsTextView);
            fetchReviewsTask.execute();
        }else{
            Log.d(LOG_TAG," mMovie object is null!");
        }

        final FloatingActionButton favouriteFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.favoriteFloatingActionButton);

        final SharedPreferences pref = getActivity().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        Set<String> set = pref.getStringSet(FAVOURITE_MOVIES_KEY, null);
        if( set!= null && set.contains(mMovie.getId())) {
            favouriteFloatingActionButton.setImageResource(R.drawable.ic_favorite_pink);
        }

        if (favouriteFloatingActionButton != null) {
            favouriteFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Set<String> set = pref.getStringSet(FAVOURITE_MOVIES_KEY, null);
                    if (set != null && set.contains(mMovie.getId())) {
                        favouriteFloatingActionButton.setImageResource(R.mipmap.ic_favorite);
                        set = new HashSet<String>(set);
                        set.remove(mMovie.getId());
                        editor.putStringSet(FAVOURITE_MOVIES_KEY, set);
                        editor.commit();
                        Log.d(LOG_TAG, "Movie : " + mMovie.title + " has been REMOVED from shared preferences.");
                    } else {
                        favouriteFloatingActionButton.setImageResource(R.drawable.ic_favorite_pink);
                        if (set == null) {
                            set = new HashSet<String>();
                        } else {
                            set = new HashSet<String>(set);
                        }
                        set = new HashSet<String>(set);
                        set.add(mMovie.getId());
                        editor.putStringSet(FAVOURITE_MOVIES_KEY, set);
                        editor.commit();
                        Log.d(LOG_TAG, "Movie : " + mMovie.title + " has been ADDED to shared preferrences.");
                    }
                }
            });
        }



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

/*    @Override
    public void onStart() {
        super.onStart();
        FetchTrailersTask fetchTrailers = new FetchTrailersTask(mNoTrailerTextView,mTrailerAdapter,mMovie);
        fetchTrailers.execute();
        setListViewHeightBasedOnChildren();


        FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getActivity(),mMovie, mReviewsRecyclerView,mNoReviewsTextView);
        fetchReviewsTask.execute();

    }*/


}
