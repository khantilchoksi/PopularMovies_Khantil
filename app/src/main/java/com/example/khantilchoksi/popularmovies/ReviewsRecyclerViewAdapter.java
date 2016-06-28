package com.example.khantilchoksi.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khantil on 26-06-2016.
 */
public class ReviewsRecyclerViewAdapter
        extends RecyclerView.Adapter<ReviewsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ArrayList<String>> mReviews;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;

        public final TextView mAuthorTextView;
        public final TextView mContentTextView;
        public final View mDividerLine;

        public ViewHolder(View view) {
            super(view);

            mAuthorTextView = (TextView) view.findViewById(R.id.reviewerName);
            mContentTextView = (TextView) view.findViewById(R.id.reviewDetails);
            mDividerLine = view.findViewById(R.id.reviewDividerLine);
        }


    }

    public ArrayList<String> getValueAt(int position) {
        return mReviews.get(position);
    }

    public ReviewsRecyclerViewAdapter(Context context, ArrayList<ArrayList<String>> items) {

        mReviews = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mAuthorTextView.setText(mReviews.get(position).get(0));
        holder.mContentTextView.setText(mReviews.get(position).get(1));

        if( ( getItemCount() - 1 ) == position){
            Log.d("Recycler View Adapter:", " getItemCount()  = "+getItemCount());
            //If the item is last in the recycler view
            //then there is no need to show the divider line
            holder.mDividerLine.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }
}
