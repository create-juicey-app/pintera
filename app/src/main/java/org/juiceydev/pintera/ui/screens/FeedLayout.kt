package org.juiceydev.pintera.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.*
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.juiceydev.pintera.ui.components.CommentsSheetContent
import org.juiceydev.pintera.ui.components.PinItem
import org.juiceydev.pintera.ui.components.PinOptionsSheetContent
import org.juiceydev.pintera.ui.components.SharePinSheetContent
import kotlin.random.Random

@Suppress("OPT_IN_USAGE")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FeedLayout() {
    // Mutable State List for Pins to support infinite scrolling
    val pins = remember { mutableStateListOf<Triple<Int, String, String?>>() }
    var isLoading by remember { mutableStateOf(false) }
    
    // Initial Load
    LaunchedEffect(Unit) {
        if (pins.isEmpty()) {
            val initialPins = List(40) { index ->
                val height = Random.nextInt(150, 450)
                Triple(
                    height,
                    "https://picsum.photos/seed/pintera_$index/400/$height",
                    if (index % 3 == 0) "Aesthetic Inspiration #$index" else null
                )
            }
            pins.addAll(initialPins)
        }
    }

    var showOptionsSheet by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    var selectedPin by remember { mutableStateOf<Triple<Int, String, String?>?>(null) }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyStaggeredGridState()

    // Handle back press when details are open
    BackHandler(enabled = selectedPin != null) {
        selectedPin = null
    }

    // Load More Logic
    fun loadMorePins() {
        if (isLoading) return
        isLoading = true
        scope.launch {
            delay(1500) // Simulate network delay
            val startId = pins.size
            val newPins = List(20) { index ->
                val id = startId + index
                val height = Random.nextInt(150, 450)
                Triple(
                    height,
                    "https://picsum.photos/seed/pintera_$id/400/$height",
                    if (id % 3 == 0) "Aesthetic Inspiration #$id" else null
                )
            }
            pins.addAll(newPins)
            isLoading = false
        }
    }

    // Infinite Scroll Detection
    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0) return@derivedStateOf false
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= totalItems - 6 // Trigger load slightly before bottom
        }
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom) loadMorePins()
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = selectedPin,
            label = "SharedEntryTransition"
        ) { targetPin ->
            if (targetPin == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(160.dp),
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp
                    ) {
                        items(items = pins) { pin ->
                            val (height, url, title) = pin
                            PinItem(
                                height = height,
                                imageUrl = url,
                                title = title,
                                onMoreClick = { showOptionsSheet = true },
                                onClick = { selectedPin = pin },
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@AnimatedContent
                            )
                        }
                        
                        if (isLoading) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingIndicator()
                                }
                            }
                        }
                    }
                }
            } else {
                val (height, url, title) = targetPin
                PostView(
                    imageUrl = url,
                    title = title,
                    imageAspectRatio = 400f / height.toFloat(),
                    onBack = { selectedPin = null },
                    onMoreClick = { showOptionsSheet = true },
                    onShareClick = { showShareSheet = true },
                    onCommentsClick = { showCommentsSheet = true },
                    onPinClick = { selectedPin = it },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent
                )
            }
        }
        
        // Options Sheet
         if (showOptionsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showOptionsSheet = false },
                sheetState = sheetState,
                tonalElevation = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                // Wrap in navigation bar insets to prevent bottom clipping
                Column(Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                    PinOptionsSheetContent(
                        onOptionSelected = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) showOptionsSheet = false
                            }
                        }
                    )
                }
            }
        }

        // Share Sheet
        if (showShareSheet && selectedPin != null) {
            ModalBottomSheet(
                onDismissRequest = { showShareSheet = false },
                sheetState = sheetState,
                tonalElevation = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                SharePinSheetContent(
                    imageUrl = selectedPin!!.second,
                    onDismiss = {
                         scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showShareSheet = false
                        }
                    }
                )
            }
        }

        // Comments Sheet
        if (showCommentsSheet && selectedPin != null) {
            ModalBottomSheet(
                onDismissRequest = { showCommentsSheet = false },
                sheetState = sheetState,
                tonalElevation = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                CommentsSheetContent(
                )
            }
        }
    }
}
