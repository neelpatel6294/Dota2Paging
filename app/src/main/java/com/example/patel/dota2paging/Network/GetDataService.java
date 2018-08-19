package com.example.patel.dota2paging.Network;


import com.example.patel.dota2paging.Model.Heroes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {


    @GET("/api/heroStats")
    Call<List<Heroes>> getAllData(@Query("since") long since, @Query("per_page") int perPage);
}
