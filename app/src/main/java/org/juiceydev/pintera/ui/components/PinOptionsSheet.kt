package org.juiceydev.pintera.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun PinOptionsSheetContent(onOptionSelected: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp) // Padding for nav bar
    ) {
        Text(
            text = "Options",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )

        HorizontalDivider()

        OptionItem("Save", Icons.Default.BookmarkBorder) {
            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
            onOptionSelected()
        }
        OptionItem("Share", Icons.Default.Share) { onOptionSelected() }
        OptionItem("Download image", Icons.Default.Download) { onOptionSelected() }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        OptionItem("See more like this", Icons.Default.ZoomIn) { onOptionSelected() }
        OptionItem("See less like this", Icons.Default.VisibilityOff) { onOptionSelected() }
        OptionItem("Report Pin", Icons.Default.Report) { onOptionSelected() }
    }
}

@Composable
fun OptionItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(text) },
        icon = { Icon(icon, contentDescription = null) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp),
        shape = RoundedCornerShape(12.dp)
    )
}