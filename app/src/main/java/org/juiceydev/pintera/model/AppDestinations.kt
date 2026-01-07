package org.juiceydev.pintera.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestinations(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    SEARCH("Search", Icons.Filled.Search, Icons.Outlined.Search),
    CREATE("Create", Icons.Filled.AddBox, Icons.Outlined.AddBox),
    INBOX("Inbox", Icons.Filled.Email, Icons.Outlined.Email),
    PROFILE("Profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle),
}