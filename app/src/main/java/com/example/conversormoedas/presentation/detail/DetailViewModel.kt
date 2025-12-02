package com.example.conversormoedas.presentation.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conversormoedas.data.models.PriceHistory
import com.example.conversormoedas.data.repository.QuotationRepository
import com.example.conversormoedas.util.Resource
import kotlinx.coroutines.launch

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
        // Se o ID for válido, busca o histórico ao iniciar
        if (quotationId.isNotBlank()) {
            getPriceHistory()
            // Simulação: Busca dados básicos
            state = state.copy(
                quotationId = quotationId,
                name = quotationId.replaceFirstChar { it.uppercase() })
        }
    }

    // Busca o histórico de preço da cotação.
    fun getPriceHistory() {
        if (quotationId.isBlank()) return

        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            when (val result = repository.getPriceHistory(quotationId)) {
                is Resource.Success -> {
                    state = state.copy(
                        historyData = result.data ?: emptyList(),
                        change24h = calculatePriceChange(result.data)
                    )
                }

                is Resource.Error -> {
                    state = state.copy(
                        error = result.message,
                        isLoading = false
                    )
                }

                is Resource.Loading -> {
                    // Estado de loading já definido no initState
                }
            }
        }
    }

    // Calcula a mudança percentual de preço com base nos dados históricos.
    private fun calculatePriceChange(history: List<PriceHistory>?): Double {
        if (history.isNullOrEmpty() || history.size <2) return 0.0

        val startPrice = history.first().price
        val endPrice = history.last().price

        return if (startPrice != 0.0) {
            ((endPrice - startPrice) / startPrice) * 100
        } else {
            0.0
        }
    }

    // --- Futuras Funções ---
}

data class DetailState(
    val quotatioId: String = "",
    val name: String = "Carregando...",
    val historyData: List<PriceHistory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val change24h: Double = 0.0 // Mudança percentual calculada.
)