package com.torrents_csv_android

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@Parcelize
data class Torrent(
  var completed: Int,
  var created_unix: Int,
  var infohash: String,
  var leechers: Int,
  var seeders: Int,
  var scraped_date: Int,
  var size_bytes: Long,
  var name: String,
): Parcelable

const val BASE_URL = "https://torrents-csv.ml/service/"

interface APIService {

  @GET("search")
  suspend fun getTorrents(@Query("q") search: String): Response<List<Torrent>>

  companion object {

    private var apiService: APIService? = null
    fun getInstance(): APIService {
      if (apiService == null) {
        apiService = Retrofit.Builder()
          .baseUrl(BASE_URL)
          .addConverterFactory(GsonConverterFactory.create())
          .build().create(APIService::class.java)
      }
      return apiService!!
    }
  }
}
