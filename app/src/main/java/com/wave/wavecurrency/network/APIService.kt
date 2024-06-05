package com.wave.wavecurrency.network

import com.wave.wavecurrency.model.CurrencyListResponse
import com.wave.wavecurrency.model.ExchangeRatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface APIService {
    @GET("api/live")
    suspend fun getExchangeRates(
        @Query("source") source: String,
    ): Response<ExchangeRatesResponse>

    @GET("list")
    suspend fun getCurrencyList(
    ): Response<CurrencyListResponse>
}
