package com.example.khantilchoksi.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by khantilchoksi on 24/03/16.
 */
public class Movie implements Parcelable{
    String id;
    String title;
    String releaseDate;
    String posterPath;

    public Movie(String id, String title, String releaseDate, String posterPath){
        this.id = id;
        this.title = title;
        this.releaseDate= releaseDate;
        this.posterPath = posterPath;
    }

    public Movie(Parcel in){
        id=in.readString();
        title=in.readString();
        releaseDate=in.readString();
        posterPath=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel){
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i){
            return new Movie[i];
        }
    };

    @Override
    public String toString(){
        return "\n Tite : "+this.title +"  ID : "+this.id+ "  Release Date : "+this.releaseDate+ " Poster Path : "+this.posterPath;
    }
}