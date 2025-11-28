package com.example.conversormoedas.presentation.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conversormoedas.data.models.Quotation
import com.example.conversormoedas.data.repository.QuotationRepository
import com.example.conversormoedas.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel que gerencia o estado e a lógica de negócios da tela de Exploração.
 */
class ExploreViewModel(
    private val repository: QuotationRepository
) : ViewModel() {

    // Estado da tela
    var state by mutableStateOf(ExploreState())
        private set

    private var searchJob: Job? = null

    init {
        getQuotations()
    }

    // Lida com eventos de UI
    fun onEvent(event: ExploreEvent) {
        when (event) {
            is ExploreEvent.OnSearchQueryChange -> {
                // Atualiza os termos de busca
                state = state.copy(searchQuery = event.query)
                // Cancela o job de busca anterior
                searchJob?.cancel()
                // Inicia um novo job com delay
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getQuotations()
                }
            }
        }
    }

    fun getQuotations() {
        viewModelScope.launch {
            repository.getTrendingQuotations(state.searchQuery).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        state = state.copy(
                            quotations = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        state = state.copy(
                            error = result.message,
                            isLoading = false
                        )
                    }
                    is Resource.Loading -> {
                        state = state.copy(isLoading = true)
                    }
                }
            }
        }
    }
}

data class ExploreState(
    val quotations: List<Quotation> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ExploreEvent {
    data class OnSearchQueryChange(val query: String) : ExploreEvent()
}