package com.wave.wavecurrency.utils

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefsDataSource(private val sharedPreferences: SharedPreferences) {
    companion object {
        const val CURRENCY_PREFS = "CURRENCY"
        const val CURRENCY_LIST = "currency_list"
    }

    fun saveCurrencyList(currencyList: Map<String, String>) {
        val json = Gson().toJson(currencyList)  // Serialize the list of string pairs to JSON
        sharedPreferences.edit().putString(CURRENCY_LIST, json).apply()
    }

    fun getCurrencyList(): Map<String, String> {
        val json = sharedPreferences.getString(CURRENCY_LIST, null)  // Get the JSON string
        return if (json != null) {
            val type = object :
                TypeToken<Map<String, String>>() {}.type  // Define the correct type for deserialization
            val stringPairs: Map<String, String> =
                Gson().fromJson(json, type)  // Deserialize JSON into list of string map
            return stringPairs
        } else {
            mutableMapOf()
        }
    }
}