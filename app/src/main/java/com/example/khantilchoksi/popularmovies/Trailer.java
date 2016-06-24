package com.example.khantilchoksi.popularmovies;

/**
 * Created by Khantil on 23-06-2016.
 */
public class Trailer {
    private String id;
    private String trailerName;
    private String trailerKey;

    public Trailer(String id, String trailerName, String trailerKey) {
        this.id = id;
        this.trailerName = trailerName;
        this.trailerKey = trailerKey;
    }

    public String getId() {
        return id;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public String getTrailerKey() {
        return trailerKey;
    }
}
