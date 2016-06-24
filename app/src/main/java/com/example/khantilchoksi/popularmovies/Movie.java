package com.example.khantilchoksi.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by khantilchoksi on 24/03/16.
 */
public class Movie implements Parcelable{


    private String id;
    String title;
    String releaseDate;
    String posterPath;
    String overview;
    String voteAverage;

    public Movie(String id, String title, String releaseDate, String posterPath, String overview, String voteAverage){
        this.id = id;
        this.title = title;
        this.releaseDate= releaseDate;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
    }

    public Movie(Parcel in){
        id=in.readString();
        title=in.readString();
        releaseDate=in.readString();
        posterPath=in.readString();
        overview= in.readString();
        voteAverage=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(voteAverage);
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

    public String getId() {
        return id;
    }
}
