package com.example.ui.screens.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.AlphaVantageClient
import com.example.data.api.GlobalQuote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class MarketUiState {
    object Idle : MarketUiState()
    object Loading : MarketUiState()
    data class Success(val quote: GlobalQuote) : MarketUiState()
    data class Error(val message: String) : MarketUiState()
}

class MarketViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<MarketUiState>(MarketUiState.Idle)
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query.uppercase()
    }

    fun fetchQuote() {
        val symbol = _searchQuery.value.trim()
        if (symbol.isEmpty()) return

        _uiState.value = MarketUiState.Loading

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.MARKET_API_KEY
                val response = withContext(Dispatchers.IO) {
                    AlphaVantageClient.service.getQuote(symbol, apiKey)
                }
                
                val quote = response.globalQuote
                if (quote != null && quote.symbol.isNotEmpty()) {
                    _uiState.value = MarketUiState.Success(quote)
                } else {
                    _uiState.value = MarketUiState.Error("Symbol not found or API limit reached.")
                }
            } catch (e: Exception) {
                _uiState.value = MarketUiState.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }
}
