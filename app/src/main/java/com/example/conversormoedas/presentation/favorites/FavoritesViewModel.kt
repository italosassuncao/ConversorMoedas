package com.example.conversormoedas.presentation.favorites

import android.media.metrics.Event
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conversormoedas.data.models.Quotation
import com.example.conversormoedas.data.repository.QuotationRepository
import com.example.conversormoedas.util.Resource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel que gerencia a lista de contaçoes favoritas.
class FavoritesViewModel(
    private val repository: QuotationRepository
) : ViewModel() {

    // Obtém o Flow de cotações favoritas do repositório.
    val state: StateFlow<FavoritesState> = repository.getFavoriteQuotations()
        .map { quotations ->
            FavoritesState(
                favorites = quotations,
                isEmpty = quotations.isEmpty()
            )
        }
        // Converte para StateFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesState()
        )

    // Remove um item dos favoritos
    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.RemoveFavorite -> {
                viewModelScope.launch {
                    repository.removeQuotationFromFavorites(event.quotationId)
                }
            }
        }
    }
}

// Estado da tela de favoritos
data class FavoritesState(
    val favorites: List<Quotation> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = true
)

// Eventos da tela de favoritos
sealed class FavoritesEvent {
    data class RemoveFavorite(val quotationId: String) : FavoritesEvent()
}