package com.example.khantilchoksi.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Khantil on 30-06-2016.
 */
public class TrailersRecyclerViewAdapter
        extends RecyclerView.Adapter<TrailersRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Trailer> mTrailers;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTrailerNameTextView;
        public final View mView;
        public final View mDividerLine;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTrailerNameTextView = (TextView) view.findViewById(R.id.trailerName);
            mDividerLine = view.findViewById(R.id.trailerDividerLine);
        }


    }

    public Trailer getValueAt(int position) {
        return mTrailers.get(position);
    }

    public TrailersRecyclerViewAdapter(Context context, ArrayList<Trailer> items) {
        mTrailers = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTrailerNameTextView.setText(mTrailers.get(position).getTrailerName());

        if( ( getItemCount() - 1 ) == position){
            //If the item is last in the recycler view
            //then there is no need to show the divider line
            holder.mDividerLine.setVisibility(View.GONE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Trailer getTrailer = mTrailers.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + getTrailer.getTrailerKey()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }
}
