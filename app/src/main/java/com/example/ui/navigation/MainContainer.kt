package com.example.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AppointmentsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BookingFlowScreen
import com.example.ui.screens.BoutiqueDashboardScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.GownDetailScreen
import com.example.ui.screens.NotificationCenterScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.viewmodel.GownScoutViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(viewModel: GownScoutViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val lastActionMessage by viewModel.lastActionMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val configuration = LocalConfiguration.current
    val isExpandedWidth = configuration.screenWidthDp >= 600

    LaunchedEffect(lastActionMessage) {
        lastActionMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearLastActionMessage()
        }
    }

    if (currentScreen is Screen.Auth) {
        AuthScreen(viewModel = viewModel)
        return
    }

    val navItems = listOf(
        NavDestination(Screen.Catalog, "Catalog", Icons.Default.Checkroom),
        NavDestination(Screen.Appointments, "Appointments", Icons.Default.CalendarMonth),
        NavDestination(Screen.Dashboard, "Boutique Suite", Icons.Default.Storefront),
        NavDestination(Screen.Analytics, "Analytics", Icons.Default.Analytics),
        NavDestination(Screen.Profile, "Account", Icons.Default.Person)
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "GOWN SCOUT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (currentUser?.role == "BOUTIQUE_OWNER") "Boutique Mode" else "Client Mode",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Notifications) },
                        modifier = Modifier.testTag("notification_icon_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (notifications.isNotEmpty()) {
                                    Badge { Text("${notifications.size}") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (!isExpandedWidth && currentScreen !in listOf(Screen.Detail, Screen.Booking)) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentScreen.route == item.screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(item.screen) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_${item.screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isExpandedWidth && currentScreen !in listOf(Screen.Detail, Screen.Booking)) {
                NavigationRail {
                    navItems.forEach { item ->
                        val isSelected = currentScreen.route == item.screen.route
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(item.screen) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 11.sp) }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                    when (screen) {
                        is Screen.Catalog -> CatalogScreen(viewModel = viewModel)
                        is Screen.Detail -> GownDetailScreen(viewModel = viewModel)
                        is Screen.Booking -> BookingFlowScreen(viewModel = viewModel)
                        is Screen.Appointments -> AppointmentsScreen(viewModel = viewModel)
                        is Screen.Dashboard -> BoutiqueDashboardScreen(viewModel = viewModel)
                        is Screen.Analytics -> AnalyticsScreen(viewModel = viewModel)
                        is Screen.Notifications -> NotificationCenterScreen(viewModel = viewModel)
                        is Screen.Profile -> ProfileScreen(viewModel = viewModel)
                        else -> CatalogScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

private data class NavDestination(
    val screen: Screen,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
