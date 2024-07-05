package com.example.cheesechase;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import com.google.gson.JsonObject;


public interface ChaseDeuxApi {

    @GET("obstacleLimit")
    Call<JsonObject> getObstacleLimit();
    @GET("/image")
    Call<ResponseBody> getImage(@Query("character") String character);
    @POST("randomWord")
    Call<JsonObject> getRandomWord(@Body RequestBody requestBody);
    @POST("theme")
    Call<JsonObject> getTheme(@Body RequestBody requestBody);
}