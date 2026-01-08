package org.juiceydev.pintera.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun SharePinSheetContent(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Helper to share URL via Android Intent
    fun shareUrl(url: String, packageName: String? = null) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, "Check out this pin: $url")
            type = "text/plain"
            if (packageName != null) {
                setPackage(packageName)
            }
        }
        
        try {
            if (packageName != null) {
                 context.startActivity(sendIntent)
            } else {
                val shareIntent = Intent.createChooser(sendIntent, "Share Pin via")
                context.startActivity(shareIntent)
            }
            onDismiss()
        } catch (e: Exception) {
            // Fallback if specific package not found
             if (packageName != null) {
                 Toast.makeText(context, "App not installed, opening options...", Toast.LENGTH_SHORT).show()
                 shareUrl(url, null)
             } else {
                 Toast.makeText(context, "Could not share", Toast.LENGTH_SHORT).show()
             }
        }
    }

    // Helper to copy to clipboard
    fun copyToClipboard(url: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Pin URL", url)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
        onDismiss()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Text(
            text = "Share to",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        // Preview of what we are sharing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Sending this Pin",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = imageUrl,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Quick Send (Mock Contacts)
        Text(
            text = "Send to",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        val contacts = listOf("Alice", "Bob", "Charlie", "Dave", "Eve")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(contacts) { name ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { 
                                Toast.makeText(context, "Sent to $name!", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.first().toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = name, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Apps
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ShareAppItem("Copy link", Icons.Outlined.ContentCopy, onClick = { copyToClipboard(imageUrl) })
            }
            item {
                 // Try WhatsApp package name usually "com.whatsapp"
                 ShareAppItem("WhatsApp", Icons.Default.Call, onClick = { shareUrl(imageUrl, "com.whatsapp") })
            }
            item {
                 // General share for Messages/Other
                 ShareAppItem("Messages", Icons.Default.Email, onClick = { shareUrl(imageUrl) })
            }
            item {
                 ShareAppItem("More", Icons.Default.MoreHoriz, onClick = { shareUrl(imageUrl) })
            }
        }
    }
}

@Composable
fun ShareAppItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)
                .padding(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
