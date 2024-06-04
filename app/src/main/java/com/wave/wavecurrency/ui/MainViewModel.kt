package com.wave.wavecurrency.ui

import android.app.Application
import android.util.Log
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
    private val dataSource: SharedPrefsDataSource,
) : AndroidViewModel(application) {
    private var _exchangeRateData =
        MutableStateFlow<DataOrException<ExchangeRatesResponse?, Boolean, Exception>?>(null)
    val exchangeRateData: StateFlow<DataOrException<ExchangeRatesResponse?, Boolean, Exception>?> =
        _exchangeRateData

    private var _currencyList =
        MutableStateFlow<DataOrException<CurrencyListResponse?, Boolean, Exception>?>(null)
    val currencyList: StateFlow<DataOrException<CurrencyListResponse?, Boolean, Exception>?> =
        _currencyList

    init {
        val currencies = dataSource.getCurrencyList()
        if(currencies.isEmpty()){
            getCurrencyList()
        }else{
            _currencyList.value = DataOrException<CurrencyListResponse?, Boolean, Exception>(CurrencyListResponse(success = true, terms = "", privacy = "", currencies = currencies))
        }
        getExchangeRate()
    }

    fun getExchangeRate(
        currencies: String = "EUR,GBP,CAD,PLN,MMK",
        source: String = "USD",
        format: Int = 1
    ) {
        viewModelScope.launch {
            _exchangeRateData.value = getExchangeRatesUseCase.invoke(currencies, source, format)
            if (_exchangeRateData.value!!.data?.quotes?.isNotEmpty() == true) {
                Log.d("##Response", "${_exchangeRateData.value!!.data?.quotes}")
            }
        }
    }

    fun getCurrencyList() {
        viewModelScope.launch {
             _currencyList.value = getExchangeRatesUseCase.invoke()
            val currencies = _currencyList.value!!.data?.currencies
            if(currencies?.isNotEmpty() == true){
                Log.d("##Data", "${_currencyList.value?.data?.currencies}")
                dataSource.saveCurrencyList(currencies)
            }
        }
    }

}