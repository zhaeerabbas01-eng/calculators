package com.example.domain.repository

import com.example.data.api.AlphaVantageClient
import com.example.data.database.AppDao
import com.example.data.database.CurrencyRateEntity

class CurrencyRepository(
    private val dao: AppDao,
    private val apiKey: String
) {
    suspend fun getExchangeRate(from: String, to: String, forceRefresh: Boolean = false): Double? {
        val pair = "${from}_${to}"
        
        // 1. Try to fetch from network if forceRefresh is true or no cached data
        if (forceRefresh) {
            val rate = fetchAndCacheRate(from, to, pair)
            if (rate != null) return rate
        }

        // 2. Try cache
        val cachedRate = dao.getCurrencyRate(pair)
        if (cachedRate != null) {
            // Optional: Consider cache expired if older than 24h
            val isExpired = System.currentTimeMillis() - cachedRate.lastUpdated > 24 * 60 * 60 * 1000
            if (!isExpired) {
                return cachedRate.rate
            }
        }

        // 3. Try to fetch from network if we didn't before
        if (!forceRefresh) {
            val rate = fetchAndCacheRate(from, to, pair)
            if (rate != null) return rate
        }
        
        // 4. Return expired cache as fallback
        return cachedRate?.rate
    }

    private suspend fun fetchAndCacheRate(from: String, to: String, pair: String): Double? {
        return try {
            val response = AlphaVantageClient.service.getExchangeRate(from, to, apiKey)
            val rateStr = response.exchangeRate?.exchangeRate
            if (rateStr != null) {
                val rate = rateStr.toDoubleOrNull()
                if (rate != null) {
                    dao.insertCurrencyRate(
                        CurrencyRateEntity(
                            pair = pair,
                            rate = rate,
                            lastUpdated = System.currentTimeMillis()
                        )
                    )
                    rate
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
