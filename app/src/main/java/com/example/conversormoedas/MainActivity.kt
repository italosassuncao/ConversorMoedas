package com.example.conversormoedas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.conversormoedas.ui.theme.ConversorMoedasTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Explore : Screen("explore", "Explorar", Icons.Filled.List)
    object Favorites : Screen("favorites", "Favoritos", Icons.Filled.Favorite)
    object Alerts : Screen("alerts", "Alertas", Icons.Filled.Notifications)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConversorMoedasTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val items = listOf(Screen.Explore, Screen.Favorites, Screen.Alerts)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Explore.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Pesquisa e lista principal
            composable(Screen.Explore.route) { ExploreScreen(NavController) }
            // Tela de favoritos, apenas contações marcadas
            composable(Screen.Favorites.route) { FavoritesScreen() }
            // Configuração e histórico de alertas
            composable(Screen.Alerts.route) { AlertsScreen() }

            composable("detail/{quotationId}") { backStackEntry ->
                val quotationId = backStackEntry.arguments?.getString("quotationId") ?: return@composable
                DetailScreen(quotationId = id)
            }
        }
    }
}

@Composable
fun ExploreScreen(navController: NavController.Companion) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Button(onClick = { navController.navigate("detail/bitcoin") }) {
            Text("Explorar - Clique para Detalhes")
        }
    }
}

@Composable
fun FavoritesScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Text("Favoritos") // Implementar listagem e remoção.
    }
}

@Composable
fun AlertsScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Text("Alertas") // Implementar lógica de notificação e histórico.
    }
}

@Composable
fun DetailScreen(quotationId: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Text("Detalhes de Cotação: $quotationId") // Implementar Gráfico Histórico.
    }
}

// TODO: Criar package ui.theme e arquivo Theme.kt
@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    QuotationAppTheme {
        MainAppScreen()
    }
}