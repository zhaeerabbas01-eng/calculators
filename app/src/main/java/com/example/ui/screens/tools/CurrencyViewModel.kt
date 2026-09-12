package com.example.ui.screens.tools

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.database.AppDatabase
import com.example.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Currency(val code: String, val name: String, val symbol: String, val flag: String)

val SUPPORTED_CURRENCIES = listOf(
    Currency("USD", "United States Dollar", "$", "🇺🇸"),
    Currency("EUR", "Euro", "€", "🇪🇺"),
    Currency("GBP", "British Pound", "£", "🇬🇧"),
    Currency("PKR", "Pakistani Rupee", "₨", "🇵🇰"),
    Currency("INR", "Indian Rupee", "₹", "🇮🇳"),
    Currency("AED", "UAE Dirham", "د.إ", "🇦🇪"),
    Currency("SAR", "Saudi Riyal", "﷼", "🇸🇦"),
    Currency("CAD", "Canadian Dollar", "$", "🇨🇦"),
    Currency("AUD", "Australian Dollar", "$", "🇦🇺"),
    Currency("JPY", "Japanese Yen", "¥", "🇯🇵"),
    Currency("CNY", "Chinese Yuan", "¥", "🇨🇳"),
    Currency("CHF", "Swiss Franc", "Fr", "🇨🇭"),
    Currency("TRY", "Turkish Lira", "₺", "🇹🇷"),
    Currency("NZD", "New Zealand Dollar", "$", "🇳🇿"),
    Currency("SGD", "Singapore Dollar", "$", "🇸🇬")
)

sealed class CurrencyUiState {
    object Idle : CurrencyUiState()
    object Loading : CurrencyUiState()
    data class Success(val rate: Double, val timestamp: Long) : CurrencyUiState()
    data class Error(val message: String) : CurrencyUiState()
}

class CurrencyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CurrencyRepository
    
    init {
        val db = AppDatabase.getDatabase(application)
        repository = CurrencyRepository(db.appDao(), BuildConfig.MARKET_API_KEY)
    }

    private val _uiState = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Idle)
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    private val _fromCurrency = MutableStateFlow(SUPPORTED_CURRENCIES[0])
    val fromCurrency: StateFlow<Currency> = _fromCurrency.asStateFlow()

    private val _toCurrency = MutableStateFlow(SUPPORTED_CURRENCIES[3])
    val toCurrency: StateFlow<Currency> = _toCurrency.asStateFlow()

    private val _amount = MutableStateFlow("100")
    val amount: StateFlow<String> = _amount.asStateFlow()

    fun updateAmount(amount: String) {
        _amount.value = amount
    }

    fun setFromCurrency(currency: Currency) {
        _fromCurrency.value = currency
        fetchRate()
    }

    fun setToCurrency(currency: Currency) {
        _toCurrency.value = currency
        fetchRate()
    }

    fun swapCurrencies() {
        val temp = _fromCurrency.value
        _fromCurrency.value = _toCurrency.value
        _toCurrency.value = temp
        fetchRate()
    }

    fun fetchRate(forceRefresh: Boolean = false) {
        val from = _fromCurrency.value.code
        val to = _toCurrency.value.code
        
        if (from == to) {
            _uiState.value = CurrencyUiState.Success(1.0, System.currentTimeMillis())
            return
        }

        _uiState.value = CurrencyUiState.Loading

        viewModelScope.launch {
            val rate = repository.getExchangeRate(from, to, forceRefresh)
            if (rate != null) {
                // To get the actual timestamp, we'd need to modify repository, 
                // but for simplicity we'll just use current time for this demo
                _uiState.value = CurrencyUiState.Success(rate, System.currentTimeMillis())
            } else {
                _uiState.value = CurrencyUiState.Error("Unable to fetch exchange rate. Check connection.")
            }
        }
    }
}
