package com.example.khantilchoksi.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khantilchoksi on 24/03/16.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer>{

    private final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Context context, ArrayList<Trailer> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trailer trailer = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_item, parent, false);
        }

        TextView trailerName = (TextView) convertView.findViewById(R.id.trailerName);
        trailerName.setText(trailer.getTrailerName());
        Log.d(LOG_TAG,"Position"+position+"  :  Trailer name: "+trailer.getTrailerName());

        return convertView;
    }
}
