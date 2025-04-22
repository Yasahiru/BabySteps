package com.cmc.babysteps.data.service

import com.cmc.babysteps.data.model.WeekData
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/api/pregnancy/{lang}/week/{week}")
    suspend fun getWeekData(
        @Path("lang") lang: String,
        @Path("week") week: Int
    ): WeekData
}

