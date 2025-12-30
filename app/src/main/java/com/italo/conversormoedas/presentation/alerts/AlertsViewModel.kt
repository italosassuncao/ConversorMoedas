package com.italo.conversormoedas.presentation.alerts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.italo.conversormoedas.data.local.AlertDao
import com.italo.conversormoedas.data.local.AlertEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * ViewModel para gerir o estado da lista de alertas.
 */
class AlertsViewModel(private val alertDao: AlertDao) : ViewModel() {
    val alerts = alertDao.getAllAlertsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
            emptyList())

    fun addAlert(quotationId: String, symbol: String, price: Double, isAbove: Boolean) {
        viewModelScope.launch {
            alertDao.insertAlert(
                AlertEntity(
                    quotationId = quotationId,
                    symbol = symbol,
                    targetPrice = price,
                    isAbove = isAbove
                )
            )
        }
    }

    fun deleteAlert(id: Int) {
        viewModelScope.launch { alertDao.deleteAlert(id) }
    }
}

/**
 * Ecrã principal de Alertas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(viewModel: AlertsViewModel = koinViewModel()) {
    val alerts by viewModel.alerts.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Meus Alertas", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Novo Alerta")
            }
        }
    ) { padding ->
        if (alerts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Nenhum alerta configurado.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(alerts) { alert ->
                    AlertItem(alert, onDelete = { viewModel.deleteAlert(alert.id) })
                }
            }
        }

        if (showDialog) {
            AddAlertDialog(
                onDismiss = { showDialog = false },
                onConfirm = { id, sym, price, above ->
                    viewModel.addAlert(id, sym, price, above)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AlertItem(alert: AlertEntity, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (alert.isActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                contentDescription = null,
                tint = if (alert.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(alert.symbol, fontWeight = FontWeight.Bold)
                Text(if (alert.isAbove) "Acima de: $${alert.targetPrice}" else "Abaixo de: $${alert.targetPrice}")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddAlertDialog(onDismiss: () -> Unit, onConfirm: (String, String, Double, Boolean) -> Unit) {
    var symbol by remember { mutableStateOf("BTC") }
    var price by remember { mutableStateOf("") }
    var isAbove by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Alerta de Preço") },
        text = {
            Column {
                OutlinedTextField(value = symbol, onValueChange = { symbol = it }, label = { Text("Símbolo (ex: BTC)") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Preço Alvo ($)") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isAbove, onCheckedChange = { isAbove = it })
                    Text("Alerta quando o preço subir acima de")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(symbol.lowercase(), symbol.uppercase(), price.toDoubleOrNull() ?: 0.0, isAbove) }) {
                Text("Criar")
            }
        }
    )
}