package com.wave.wavecurrency.repository

import com.wave.wavecurrency.model.CurrencyListResponse
import com.wave.wavecurrency.model.DataOrException
import com.wave.wavecurrency.model.ExchangeRatesResponse

interface ExchangeRatesRepository {
    suspend fun getExchangeRates(
        currencies: String,
        source: String,
        format: Int
    ): DataOrException<ExchangeRatesResponse?, Boolean, Exception>

    suspend fun getCurrencyList():DataOrException<CurrencyListResponse?, Boolean, Exception>
}