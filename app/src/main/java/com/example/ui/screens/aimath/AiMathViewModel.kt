package com.example.ui.screens.aimath

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AiMathViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AiMathUiState>(AiMathUiState.Idle)
    val uiState: StateFlow<AiMathUiState> = _uiState.asStateFlow()

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        val userMessage = ChatMessage(message, isUser = true)
        _chatHistory.value = _chatHistory.value + userMessage
        _uiState.value = AiMathUiState.Loading

        viewModelScope.launch {
            try {
                val responseText = generateResponse(message)
                val aiMessage = ChatMessage(responseText, isUser = false)
                _chatHistory.value = _chatHistory.value + aiMessage
                _uiState.value = AiMathUiState.Idle
            } catch (e: Exception) {
                _uiState.value = AiMathUiState.Error("Error: ${e.message}")
            }
        }
    }

    private suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = "You are CalcX AI, an advanced math assistant. You help users solve mathematical problems, explain formulas, and understand concepts. Answer clearly and concisely. Do not guess simple arithmetic; you can compute it, but remind users they have a deterministic calculator in the app for basic math. Show step-by-step explanations where helpful."))
            )
        )
        val response = RetrofitClient.service.generateContent(apiKey, request)
        response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I'm sorry, I couldn't generate a response."
    }
}

data class ChatMessage(val text: String, val isUser: Boolean)

sealed class AiMathUiState {
    object Idle : AiMathUiState()
    object Loading : AiMathUiState()
    data class Error(val message: String) : AiMathUiState()
}
