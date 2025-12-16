package com.italo.conversormoedas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.italo.conversormoedas.presentation.detail.DetailScreen
import com.italo.conversormoedas.presentation.explore.ExploreScreen
import com.italo.conversormoedas.presentation.favorites.FavoritesScreen
import com.italo.conversormoedas.ui.theme.QuotationAppTheme

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
            QuotationAppTheme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            composable(Screen.Explore.route) { ExploreScreen(navController) }
            composable(Screen.Favorites.route) { FavoritesScreen() }

            composable(Screen.Alerts.route) { PlaceholderAlertsScreen() }

            composable("detail/{quotationId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("quotationId") ?: return@composable
                DetailScreen(navController = navController, quotationId = id)
            }
        }
    }
}

@Composable
fun PlaceholderAlertsScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Text("Alertas") // Implementar lógica de notificação e histórico.
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    QuotationAppTheme {
        MainAppScreen()
    }
}