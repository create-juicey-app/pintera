package org.juiceydev.pintera.ui.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PinOptionsSheetContent(onOptionSelected: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            // Add navigation bar padding + extra breathing room
            .padding(bottom = 48.dp)
    ) {
        // Optional: Header Title matching M3 guidelines for Action Sheets
        Text(
            text = "Options",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        // Group 1: Primary Actions
        // Using Outlined icons is often preferred for actions in M3
        OptionItem("Save", Icons.Outlined.BookmarkBorder) {
            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
            onOptionSelected()
        }
        OptionItem("Share", Icons.Outlined.Share) { onOptionSelected() }
        OptionItem("Download image", Icons.Outlined.Download) { onOptionSelected() }

        // Divider with subtle spacing
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Group 2: Secondary / Negative Actions
        OptionItem("See more like this", Icons.Outlined.ZoomIn) { onOptionSelected() }
        OptionItem("Hide", Icons.Outlined.VisibilityOff) { onOptionSelected() }
        OptionItem("Report Pin", Icons.Outlined.Report) { onOptionSelected() }
    }
}

@Composable
fun OptionItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    // ListItem is the canonical M3 component for lists in sheets
    ListItem(
        headlineContent = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                // M3 Spec: Icons in lists usually use the 'OnSurfaceVariant' color
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Standard ripple effect
        // M3 Specs often imply full width, but explicit padding for content is handled by ListItem
    )
}