package com.ashe.whatfood

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
}