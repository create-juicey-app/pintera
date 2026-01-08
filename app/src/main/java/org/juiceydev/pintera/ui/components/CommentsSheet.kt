package org.juiceydev.pintera.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

data class Comment(
    val id: String,
    val author: String,
    val text: String,
    val replies: SnapshotStateList<Comment> = mutableStateListOf(),
    val likes: Int = 0,
    val isLiked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsSheetContent() {
    var commentText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<Comment?>(null) }
    val focusRequester = remember { FocusRequester() }
    
    // Mock Data
    val comments = remember {
        mutableStateListOf(
            Comment("1", "Alice", "Great shot! 📸", 
                replies = mutableStateListOf(
                    Comment("1-1", "Bob", "Agreed! The lighting is perfect.")
                ),
                likes = 12
            ),
            Comment("2", "Charlie", "Where was this taken?", likes = 5),
            Comment("3", "Dave", "Amazing!", likes = 2),
            Comment("4", "Eve", "Saved to my board.")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize() 
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = "Comments",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 24.dp)
        )
        
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            items(items = comments, key = { comment -> comment.id }) { comment ->
                CommentItem(
                    comment = comment,
                    onReplyClick = { targetComment ->
                        replyingTo = targetComment
                        focusRequester.requestFocus()
                    }
                )
            }
            
            // "Show more" button footer
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = { /* TODO: Load more comments */ }) {
                        Text("Show more comments")
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        // Replying Indicator
        if (replyingTo != null) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Replying to ${replyingTo?.author}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(
                        onClick = { replyingTo = null },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Close, "Cancel reply")
                    }
                }
            }
        }

        // Input Area
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = commentText,
                    onValueChange = { newText -> commentText = newText },
                    placeholder = { 
                        Text(if (replyingTo != null) "Reply to ${replyingTo?.author}..." else "Add a comment") 
                    },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    shape = RoundedCornerShape(28.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                FilledIconButton(
                    onClick = { 
                        if (commentText.isNotBlank()) {
                            val newComment = Comment(
                                id = System.currentTimeMillis().toString(),
                                author = "Me",
                                text = commentText
                            )
                            
                            if (replyingTo != null) {
                                replyingTo?.replies?.add(newComment)
                                replyingTo = null
                            } else {
                                comments.add(0, newComment)
                            }
                            commentText = ""
                        }
                    },
                    enabled = commentText.isNotBlank(),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    onReplyClick: (Comment) -> Unit,
    isReply: Boolean = false
) {
    var isLiked by remember { mutableStateOf(comment.isLiked) }
    var likesCount by remember { mutableIntStateOf(comment.likes) }
    var showMenu by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Avatar
        Surface(
            modifier = Modifier.size(if (isReply) 32.dp else 48.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = comment.author.first().toString(),
                    style = if (isReply) MaterialTheme.typography.titleSmall else MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            // Header: Name + Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.author,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Vertical Menu Icon (MoreVert)
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        offset = DpOffset(0.dp, 8.dp),
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Report user") },
                            onClick = { showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Flag, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Block user") },
                            onClick = { showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Block, null) }
                        )
                    }
                }
            }
            
            // Comment Text - Closer to Author
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onReplyClick(comment) }
                    .padding(start = 0.dp, end = 4.dp, bottom = 4.dp, top = 0.dp)
                    .fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Actions Row with Larger Hitboxes
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reply Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onReplyClick(comment) }
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = "Reply",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Like Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { 
                            isLiked = !isLiked
                            if (isLiked) likesCount++ else likesCount--
                        }
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        modifier = Modifier.size(18.dp),
                        tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (likesCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = likesCount.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Render Threaded Replies
            if (comment.replies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        comment.replies.forEach { reply ->
                            CommentItem(
                                comment = reply,
                                onReplyClick = onReplyClick,
                                isReply = true
                            )
                        }
                    }
                }
            }
        }
    }
}
