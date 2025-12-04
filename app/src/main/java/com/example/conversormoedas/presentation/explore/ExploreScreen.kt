package com.example.conversormoedas.presentation.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.conversormoedas.presentation.explore.components.QuotationItem
import org.koin.androidx.compose.koinViewModel

// Componente Composable para a tela principal de Exploração.
// Usa o ExploreViewModel injetado pelo Koin.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavController,
    viewModel: ExploreViewModel = koinViewModel()
) {

    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explorar Mercados") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Campo de Pesquisa
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = {
                    viewModel.onEvent(ExploreEvent.OnSearchQueryChange(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Pesquisar por nome ou simbolo") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Pesquisar") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Indicador de Carregamento
            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Exibição de Erro
            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Lista de Cotações
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(state.quotations, key = {it.id}) { quotation ->
                    QuotationItem(
                        quotation = quotation,
                        onItemClick = {
                            navController.navigate("detail/${it.id}")
                        }
                    )
                }
            }

            // Se a lista estiver vazia e não estiver carregando
            if (!state.isLoading && state.quotations.isEmpty() && state.searchQuery.isNotBlank()) {
                Text(
                    text = "Nenhuma cotação encontrada para '${state.searchQuery}'.",
                    modifier = Modifier.padding(24.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}