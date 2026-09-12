package com.example.data.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@Serializable
data class GlobalQuoteResponse(
    @kotlinx.serialization.SerialName("Global Quote") val globalQuote: GlobalQuote? = null
)

@Serializable
data class GlobalQuote(
    @kotlinx.serialization.SerialName("01. symbol") val symbol: String,
    @kotlinx.serialization.SerialName("05. price") val price: String,
    @kotlinx.serialization.SerialName("09. change") val change: String,
    @kotlinx.serialization.SerialName("10. change percent") val changePercent: String
)

@Serializable
data class CurrencyExchangeRateResponse(
    @kotlinx.serialization.SerialName("Realtime Currency Exchange Rate") val exchangeRate: CurrencyExchangeRate? = null
)

@Serializable
data class CurrencyExchangeRate(
    @kotlinx.serialization.SerialName("1. From_Currency Code") val fromCode: String,
    @kotlinx.serialization.SerialName("3. To_Currency Code") val toCode: String,
    @kotlinx.serialization.SerialName("5. Exchange Rate") val exchangeRate: String,
    @kotlinx.serialization.SerialName("6. Last Refreshed") val lastRefreshed: String
)

interface AlphaVantageApiService {
    @GET("query?function=GLOBAL_QUOTE")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): GlobalQuoteResponse

    @GET("query?function=CURRENCY_EXCHANGE_RATE")
    suspend fun getExchangeRate(
        @Query("from_currency") fromCurrency: String,
        @Query("to_currency") toCurrency: String,
        @Query("apikey") apiKey: String
    ): CurrencyExchangeRateResponse
}

object AlphaVantageClient {
    private const val BASE_URL = "https://www.alphavantage.co/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: AlphaVantageApiService by lazy {
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        retrofit.create(AlphaVantageApiService::class.java)
    }
}
