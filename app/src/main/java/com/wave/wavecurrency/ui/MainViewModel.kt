package com.wave.wavecurrency.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wave.wavecurrency.model.CurrencyListResponse
import com.wave.wavecurrency.model.DataOrException
import com.wave.wavecurrency.model.ExchangeRatesResponse
import com.wave.wavecurrency.usecase.GetExchangeRatesUseCase
import com.wave.wavecurrency.utils.SharedPrefsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val getExchangeRatesUseCase: GetExchangeRatesUseCase,
    private val localDataSource: SharedPrefsDataSource,
) : AndroidViewModel(application) {
    private val baseCurrency = "USD"
    private var _exchangeRateData =
        MutableStateFlow<DataOrException<ExchangeRatesResponse?, Boolean, Exception>?>(null)
    val exchangeRateData: StateFlow<DataOrException<ExchangeRatesResponse?, Boolean, Exception>?> =
        _exchangeRateData

    private var _currencyList =
        MutableStateFlow<DataOrException<CurrencyListResponse?, Boolean, Exception>?>(null)
    val currencyList: StateFlow<DataOrException<CurrencyListResponse?, Boolean, Exception>?> =
        _currencyList

    init {
        val currencies = localDataSource.getCurrencyList()
        if (currencies.isEmpty()) {
            getCurrencyList()
        } else {
            _currencyList.value = DataOrException(
                CurrencyListResponse(
                    success = true, currencies = currencies
                )
            )
        }
        getExchangeRate()
    }

    private fun shouldRefreshData(): Boolean {
        val lastRefreshTime = localDataSource.getLastRefreshTime()
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastRefreshTime) > (30 * 60 * 1000) // 30 minutes in milliseconds
    }

    fun getExchangeRate() {
        viewModelScope.launch {
            if (shouldRefreshData()) {
                _exchangeRateData.value = DataOrException(loading = true)
                val result: DataOrException<ExchangeRatesResponse?, Boolean, Exception> = try {
                    val response = getExchangeRatesUseCase.invoke(baseCurrency)
                    if (response.data?.quotes != null) {
                        val transformedQuotes = response.data!!.quotes.mapKeys { entry ->
                            entry.key.removePrefix(baseCurrency)
                        }
                        val transformedResponse = response.data!!.copy(quotes = transformedQuotes)
                        DataOrException(data = transformedResponse)
                    } else {
                        response
                    }
                } catch (e: Exception) {
                    DataOrException(e = e)
                }
                _exchangeRateData.value = result
                if (result.data?.quotes?.isNotEmpty() == true) {
                    localDataSource.saveCurrencyRate(result.data!!.quotes)
                    _exchangeRateData.value =
                        _exchangeRateData.value?.copy(e = null) // Reset error after handling
                }
            } else {
                val cachedRates = localDataSource.getCurrencyRate()
                _exchangeRateData.value = DataOrException(
                    ExchangeRatesResponse(
                        success = true, quotes = cachedRates
                    )
                )
            }
        }
    }

    fun getCurrencyList() {
        viewModelScope.launch {
            _currencyList.value = DataOrException(loading = true)
            val result = try {
                getExchangeRatesUseCase.invoke()
            } catch (e: Exception) {
                DataOrException(e = e)
            }
            _currencyList.value = result
            val currencies = _currencyList.value!!.data?.currencies
            if (currencies?.isNotEmpty() == true) {
                localDataSource.saveCurrencyList(currencies)
                _currencyList.value =
                    _currencyList.value?.copy(e = null) // Reset error after handling
            }
        }
    }

    fun calculateExchangeRate(amount: Double, currencyType: String) {
        viewModelScope.launch {
            val exchangeRates = localDataSource.getCurrencyRate()
            if (exchangeRates.isEmpty()) {
                getExchangeRate()
                return@launch
            }
            val convertedRates = mutableMapOf<String, Double>()

            if (currencyType == baseCurrency) {
                for ((key, rate) in exchangeRates) {
                    val targetCurrency = key.removePrefix(baseCurrency)
                    val convertedAmount = amount * rate
                    convertedRates[targetCurrency] = convertedAmount
                }
            } else {
                val baseRate = exchangeRates[currencyType] ?: return@launch
                for ((key, rate) in exchangeRates) {
                    if (key == "$baseCurrency$currencyType") continue
                    val targetCurrency = key.removePrefix(baseCurrency)
                    val convertedAmount = (amount / baseRate) * rate
                    convertedRates[targetCurrency] = convertedAmount
                }
            }

            _exchangeRateData.value = DataOrException(
                ExchangeRatesResponse(
                    success = true, quotes = convertedRates
                )
            )
        }
    }
}