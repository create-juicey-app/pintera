package org.juiceydev.pintera.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.juiceydev.pintera.ui.components.PinItem
import java.util.Locale
import kotlin.random.Random

// Constant radius for consistency
private val PinCornerRadius = 16.dp

// Suppress OPT_IN_USAGE to avoid needing to reference the internal ExperimentalMaterial3ExpressiveApi annotation class
@Suppress("OPT_IN_USAGE")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PostView(
    imageUrl: String,
    title: String?,
    imageAspectRatio: Float,
    onBack: () -> Unit,
    onMoreClick: () -> Unit,
    onShareClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onPinClick: (Triple<Int, String, String?>) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableIntStateOf(1200) }

    // More Pins State
    val morePins = remember { mutableStateListOf<Triple<Int, String, String?>>() }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Load Data Function
    fun loadMoreItems() {
        if (isLoading) return
        isLoading = true
        scope.launch {
            delay(1500) // Simulate network delay
            val startId = morePins.size
            val newPins = List(10) { index ->
                val id = startId + index
                val height = Random.nextInt(150, 450)
                Triple(
                    height,
                    "https://picsum.photos/seed/pintera_more_${id}/400/$height",
                    if (index % 3 == 0) "Related Pin #$id" else null
                )
            }
            morePins.addAll(newPins)
            isLoading = false
        }
    }

    // Initial Load
    LaunchedEffect(Unit) {
        if (morePins.isEmpty()) loadMoreItems()
    }

    val listState = rememberLazyStaggeredGridState()
    
    // Infinite Scroll Detection
    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0) return@derivedStateOf false
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= totalItems - 4
        }
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom) loadMoreItems()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pin Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onMoreClick) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "Options"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                windowInsets = WindowInsets.statusBars
            )
        }
    ) { padding ->
        LazyVerticalStaggeredGrid(
            state = listState,
            columns = StaggeredGridCells.Adaptive(160.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp
        ) {
            // 1. The main Image and Details (Spanning full width)
            item(span = StaggeredGridItemSpan.FullLine) {
                Column {
                    // Image Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        with(sharedTransitionScope) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = title,
                                modifier = Modifier
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState(key = "image-$imageUrl"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                    .graphicsLayer {
                                        shape = RoundedCornerShape(PinCornerRadius)
                                        clip = true
                                    }
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(PinCornerRadius)
                                    )
                                    .fillMaxWidth()
                                    .aspectRatio(imageAspectRatio),
                                contentScale = ContentScale.FillWidth
                            )
                        }
                    }

                    // Quick Actions Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { 
                                isLiked = !isLiked
                                if (isLiked) likesCount++ else likesCount--
                            }) {
                                Icon(
                                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (isLiked) Color.Red else LocalContentColor.current
                                )
                            }
                            Text(
                                text = if (likesCount >= 1000) "${String.format(Locale.getDefault(), "%.1f", likesCount / 1000.0)}k" else likesCount.toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            IconButton(onClick = onCommentsClick) {
                                Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment")
                            }
                            Text(
                                text = "342", 
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.clickable(onClick = onCommentsClick)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = onShareClick) {
                                Icon(Icons.Default.Share, contentDescription = "Share")
                            }
                        }

                        Button(
                            onClick = { /* TODO */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Save", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title and Description
                    Column {
                        if (title != null) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        Text(
                            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // "More like this" Header
                    Text(
                        text = "More like this",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            // 2. The "More Like This" Grid
            items(items = morePins) { pin ->
                val (height, url, title) = pin
                PinItem(
                    height = height,
                    imageUrl = url,
                    title = title,
                    onMoreClick = onMoreClick,
                    onClick = { onPinClick(pin) },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
            
            // 3. Expressive Loading Indicator
            if (isLoading) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        LoadingIndicator()
                    }
                }
            }
        }
    }
}
