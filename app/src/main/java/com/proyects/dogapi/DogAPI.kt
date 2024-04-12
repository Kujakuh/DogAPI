package com.proyects.dogapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface DogAPI {
    @Headers("Accept: application/json")

    @GET("breeds/image/random")
    fun getUrl(): Call<DogMetaData>
}
