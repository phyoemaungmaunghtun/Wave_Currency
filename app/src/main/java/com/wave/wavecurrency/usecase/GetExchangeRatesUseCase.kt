package com.wave.wavecurrency.usecase

import com.wave.wavecurrency.model.CurrencyListResponse
import com.wave.wavecurrency.model.DataOrException
import com.wave.wavecurrency.model.ExchangeRatesResponse
import com.wave.wavecurrency.repository.ExchangeRatesRepository
import javax.inject.Inject

class GetExchangeRatesUseCase @Inject constructor(
    private val repository: ExchangeRatesRepository
) {
    suspend operator fun invoke(
        source: String,
    ): DataOrException<ExchangeRatesResponse?, Boolean, Exception> {
        return repository.getExchangeRates(source)
    }

    suspend operator fun invoke(
    ): DataOrException<CurrencyListResponse?, Boolean, Exception> {
        return repository.getCurrencyList()
    }
}