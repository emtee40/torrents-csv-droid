package com.torrents_csv_android

import androidx.annotation.Keep
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@Keep
data class Torrent(
    var completed: Int,
    var created_unix: Int,
    var infohash: String,
    var leechers: Int,
    var seeders: Int,
    var scraped_date: Int,
    var size_bytes: Long,
    var name: String,
)

const val BASE_URL = "https://torrents-csv.com/service/"

interface APIService {
    @GET("search")
    suspend fun getTorrents(
        @Query("q") search: String,
    ): List<Torrent>

    companion object {
        private var apiService: APIService? = null

        fun getInstance(): APIService {
            if (apiService == null) {
                apiService =
                    Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(APIService::class.java)
            }
            return apiService!!
        }
    }
}
