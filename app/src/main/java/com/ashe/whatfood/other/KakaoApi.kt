package com.ashe.whatfood.other

import com.ashe.whatfood.dto.ResultSearchKeyword
import com.ashe.whatfood.dto.ResultThumbnailImage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoApi {
    @GET("v2/local/search/keyword.json")
    fun getSearchKeyword(
        @Header("Authorization") key: String,
        @Query("query") query: String
    ): Call<ResultSearchKeyword>

    @GET("v2/search/image")
    fun getThumbnailImage(
        @Header("Authorization") key: String,
        @Query("query") query: String,
    ): Call<ResultThumbnailImage>

    @GET("/v2/local/search/category.json")
    fun getNearbyRestaurants(
        @Header("Authorization") key: String,
        @Query("category_group_code") code: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") radius:Int,
        @Query("page") page:Int
    ): Call<ResultSearchKeyword>
}