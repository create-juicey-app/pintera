package org.juiceydev.pintera.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.juiceydev.pintera.ui.components.CommentsSheetContent
import org.juiceydev.pintera.ui.components.PinItem
import org.juiceydev.pintera.ui.components.PinOptionsSheetContent
import org.juiceydev.pintera.ui.components.SharePinSheetContent
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FeedLayout() {
    // Mock Data using Unsplash Source for better testing visuals
    val pins = remember {
        List(40) { index ->
            val height = Random.nextInt(150, 450)
            Triple(
                height, // Height for aspect ratio calculation
                // Use the same height in the URL to ensure the image aspect ratio matches the item aspect ratio
                "https://picsum.photos/seed/pintera_$index/400/$height",
                if (index % 3 == 0) "Aesthetic Inspiration #$index" else null // Optional title
            )
        }
    }

    var showOptionsSheet by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    var selectedPin by remember { mutableStateOf<Triple<Int, String, String?>?>(null) }
    
    // We need separate sheets or manage the content dynamically
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Handle back press when details are open
    BackHandler(enabled = selectedPin != null) {
        selectedPin = null
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
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp
                    ) {
                        items(pins) { pin ->
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
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showCommentsSheet = false
                        }
                    }
                )
            }
        }
    }
}
