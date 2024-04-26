package com.example.innotest

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface PexelsService {
    @GET("collections/featured")
    fun getFeaturedCollections(
        @Header("Authorization") apiKey: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<CollectionsResponse>

    @GET("curated")
    fun getCuratedPhotos(
        @Header("Authorization") apiKey: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Call<PhotosResponse>
}
    data class PhotosResponse(
        val photos: List<Photo>
    )

    data class CollectionsResponse(
        val collections: List<Collection>
    )

    data class Collection(
        val id: String ,
        val title: String,
        val description: String,
        val private: Boolean,
        val mediaCount: Int,
        val photosCount: Int,
        val videosCount: Int,
        val curated: Boolean,
        val coverPhoto: CoverPhoto
    )

    data class CoverPhoto(
        val avg_color: String,
        val height: Int,
        val id: Int,
        val liked: Boolean,
        val photographer: String,
        val photographer_id: Int,
        val photographer_url: String,
        val src: Src,
        val url: String,
        val width: Int
    )
    data class Src(
        val landscape: String,
        val large: String,
        val large2x: String,
        val medium: String,
        val original: String,
        val portrait: String,
        val small: String,
        val tiny: String
)
data class Photo(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographer_url: String,
    val photographer_id: Int,
    val avg_color: String,
    val src: Src,
    val alt: String

)



    object PexelsApi {
        private const val BASE_URL = "https://api.pexels.com/v1/"
        const val API_KEY = "PmxjA47BhjHLAH2p8kyAnKeilRRuSVLZhvbvVNxvGTguGVViBrRTq6Oi"

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: PexelsService = retrofit.create(PexelsService::class.java)
    }
