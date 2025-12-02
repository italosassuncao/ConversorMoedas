package com.example.conversormoedas.presentation.detail

import android.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.conversormoedas.presentation.detail.components.LineChart
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat

// Componente Composable para a tela de Detalhes de uma Cotação (Gráfico e Dados).

@Composable
fun DetailScreen(
    navController: NavController,
    quotationId: String,
    viewModel: DetailViewModel = koinViewModel()
) {
    // Estado do ViewModel
    val state = viewModel.state
    val priceFormatter = DecimalFormat("#,##0.00")
    val percentFormatter = DecimalFormat("0.00")
    val changeColor = if (state.change24h >= 0) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    Scaffold(
        topBar = {
            topAppBar(
                title = { Text(state.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // Botao favorito
                    IconButton(onClick = { /* viewModel.toggleFavorite() */ }) {
                        Icon(Icons.Filled.FavoriteBorder,
                            contentDescription = "Adicionar aos Favoritos")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            if (state.isLoading && state.historyData.isEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Resumo cotaçao
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                val currentPrice = state.historyData.lastOrNull()?.price ?: 0.0
                Text(
                    text = "$${priceFormatter.format(currentPrice)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Variaçao 24 horas
                val sign = if (state.change24h >= 0) "+" else ""
                Text(
                    text = "$sign${percentFormatter.format(state.change24h)}% (7 dias)",
                    style = MaterialTheme.typography.titleMedium,
                    color = changeColor,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Gráfico de histórico de preços
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Histórico (7 dias)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (state.error != null) {
                            Text(
                                text = "Erro ao carregar o gráfico: ${state.error}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 20.dp)
                            )
                        } else if (state.historyData.isNotEmpty()) {
                            LineChart(
                                data = state.historyData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        } else if (!state.isLoading) {
                            Text(
                                text = "Nenhum dado de histórico encontrado.",
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                    .padding(vertical = 20.dp)
                            )
                        }
                    }
                }

                // Informações Adicionais (Placeholder para expansão futura)
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Detalhes do Ativo", style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ID: $quotationId")
                        Text("Moeda Base: USD")
                        Text("Fonte de Dados: CoinGecko")
                        // Aqui podem vir Market Cap, Volume 24h, etc.
                    }
                }
            }
        }
    }
}