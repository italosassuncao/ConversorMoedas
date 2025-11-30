package com.example.conversormoedas.presentation.detail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.conversormoedas.data.repository.QuotationRepository

// Constante para a chave do ID
private const val QUOTATION_ID_KEY = "quotatioId"

class DetailViewModel(
    private val repository: QuotationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val quotationId: String = savedStateHandle.get<String>(QUOTATION_ID_KEY) ?: ""

    var state by mutableStateOf(DetailState())
        private set

    init {
        if (quotationId.isNotBlank()) {
            getPriceHistory()

            state = state.copy(
                quotationId = quotationId,
                name = quotationId.replaceFirstChar { it.uppercase() })
        }
    }
}