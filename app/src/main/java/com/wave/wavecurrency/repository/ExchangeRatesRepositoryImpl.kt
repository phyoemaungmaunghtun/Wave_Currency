package com.wave.wavecurrency.repository

import com.wave.wavecurrency.model.CurrencyListResponse
import com.wave.wavecurrency.model.DataOrException
import com.wave.wavecurrency.model.ExchangeRatesResponse
import com.wave.wavecurrency.network.APIService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesRepositoryImpl @Inject constructor(
    private val apiService: APIService
) : ExchangeRatesRepository {

    private val getExchangeRate = DataOrException<ExchangeRatesResponse?, Boolean, Exception>()
    override suspend fun getExchangeRates(
        currencies: String,
        source: String,
        format: Int
    ): DataOrException<ExchangeRatesResponse?, Boolean, Exception> {
        try {
            getExchangeRate.loading = true
            val resp = apiService.getExchangeRates(currencies, source, format)
            getExchangeRate.data = resp.body()
            if (resp.code() == 200 && getExchangeRate.data?.success == true) {
                getExchangeRate.loading = false
            } else {
                throw Exception(getServerErrorResponses(resp.code()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getExchangeRate.e = e
        }
        return getExchangeRate
    }

    private val currencyList = DataOrException<CurrencyListResponse?, Boolean, Exception>()
    override suspend fun getCurrencyList(): DataOrException<CurrencyListResponse?, Boolean, Exception> {
        try {
            currencyList.loading = true
            val resp = apiService.getCurrencyList()
            currencyList.data = resp.body()
            if (resp.code() == 200 && currencyList.data?.success == true) {
                currencyList.loading = false
            } else {
                throw Exception(getServerErrorResponses(resp.code()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            currencyList.e = e
        }
        return currencyList
    }

    private fun getServerErrorResponses(responseCode: Int): String {
        return when (responseCode) {
            404 -> "Server Response: $responseCode  not found"
            403 -> "Oops! It seems you don't have the necessary permissions to access this feature. Please contact your system administrator for assistance."
            500 -> "Server Response: $responseCode  server broken"
            504 -> "Server Response: $responseCode  gateway time-out"
            else -> "Server Response: $responseCode  unknown error"
        }
    }
}