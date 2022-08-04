package id.my.okisulton.newsapplicationmandiri.network.api

import id.my.okisulton.newsapplicationmandiri.network.model.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * Created by Oki Sulton on 03/08/2022.
 */
interface ApiEndpoint {
    @GET("v2/top-headlines")
    fun getNews(
        @QueryMap parameters : HashMap<String, String>
    ): Call<NewsResponse>
}