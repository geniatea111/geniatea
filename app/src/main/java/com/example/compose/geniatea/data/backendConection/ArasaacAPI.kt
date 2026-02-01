package com.example.compose.geniatea.data.backendConection

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ArasaacAPI {
    @GET("pictograms/{language}/search/{searchText}")
    suspend fun searchPictograms(
        @Path("language") language: String,
        @Path("searchText") searchText: String
    ): Response<List<PictogramResponse>>

    data class PictogramResponse(
        val _id: Long,
        val keywords: List<Keyword>,
        val created: String,
        val lastUpdated: String,
        val downloads: Int,
        val categories: List<String>,
        val synsets: List<String>,
        val tags: List<String>,
        val desc: String?
    )

    data class Keyword(
        val idKeyword: Long,
        val keyword: String,
        val plural: String?,
        val idLocution: String?,
        val meaning: String?,
        val type: Int,
        val lse: Int
    )
}
