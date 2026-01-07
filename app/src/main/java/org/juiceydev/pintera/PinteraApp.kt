package org.juiceydev.pintera

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.juiceydev.pintera.model.AppDestinations
import org.juiceydev.pintera.ui.screens.FeedLayout

import org.juiceydev.pintera.ui.screens.PlaceholderScreen

@Composable
fun PinteraApp() {
    val destinations = AppDestinations.entries
    val pagerState = rememberPagerState(pageCount = { destinations.size })
    val scope = rememberCoroutineScope()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            destinations.forEachIndexed { index, destination ->
                val isSelected = pagerState.currentPage == index

                item(
                    selected = isSelected,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
                    icon = {
                        Crossfade(targetState = isSelected, label = "IconFade") { selected ->
                            Icon(
                                imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.label
                            )
                        }
                    },
                    label = { Text(destination.label, fontSize = 10.sp) },
                    alwaysShowLabel = true
                )
            }
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1,
            userScrollEnabled = true
        ) { page ->
            val currentTarget = destinations[page]

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (currentTarget) {
                        AppDestinations.HOME -> FeedLayout()
                        else -> PlaceholderScreen(currentTarget.label)
                    }
                }
            }
        }
    }
}