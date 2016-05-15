package com.example.catcha.sync.rest;

import com.example.catcha.sync.model.Connections;
import com.example.catcha.sync.model.Stations;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TransportService {

    @GET("locations")
    Call<Stations> findLocations(@Query("query") String query);

    @GET("connections")
    Call<Connections> findConnections(@Query("from") String from, @Query("to") String to);

}
