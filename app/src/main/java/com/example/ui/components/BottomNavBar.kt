package com.example.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.CropNavTab

@Composable
fun CropBottomNavBar(
    activeTab: CropNavTab,
    onTabSelected: (CropNavTab) -> Unit,
    currentLanguage: AppLanguage
) {
    NavigationBar(
        modifier = Modifier
            .height(72.dp)
            .testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = activeTab == CropNavTab.SCANNER,
            onClick = { onTabSelected(CropNavTab.SCANNER) },
            icon = {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scanner",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("tab_scanner", currentLanguage),
                    fontSize = 12.sp,
                    fontWeight = if (activeTab == CropNavTab.SCANNER) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("nav_tab_scanner")
        )

        NavigationBarItem(
            selected = activeTab == CropNavTab.WEATHER,
            onClick = { onTabSelected(CropNavTab.WEATHER) },
            icon = {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Weather & Risk",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("tab_weather", currentLanguage),
                    fontSize = 12.sp,
                    fontWeight = if (activeTab == CropNavTab.WEATHER) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("nav_tab_weather")
        )

        NavigationBarItem(
            selected = activeTab == CropNavTab.HISTORY,
            onClick = { onTabSelected(CropNavTab.HISTORY) },
            icon = {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("tab_history", currentLanguage),
                    fontSize = 12.sp,
                    fontWeight = if (activeTab == CropNavTab.HISTORY) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("nav_tab_history")
        )

        NavigationBarItem(
            selected = activeTab == CropNavTab.GUIDE,
            onClick = { onTabSelected(CropNavTab.GUIDE) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = "Agronomy Guide",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("tab_guide", currentLanguage),
                    fontSize = 12.sp,
                    fontWeight = if (activeTab == CropNavTab.GUIDE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("nav_tab_guide")
        )
    }
}
