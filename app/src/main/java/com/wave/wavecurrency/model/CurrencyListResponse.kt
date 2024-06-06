package com.wave.wavecurrency.model

import com.google.gson.annotations.SerializedName

data class CurrencyListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("currencies") val currencies: Map<String, String>
)