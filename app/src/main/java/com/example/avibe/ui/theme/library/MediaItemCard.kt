package com.example.avibe.ui.theme.library

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.avibe.data.model.MediaItem
import com.example.avibe.data.model.MediaType
import com.example.avibe.ui.theme.NeonPink
import com.example.avibe.ui.theme.NeonPurple
import com.example.avibe.ui.theme.TextSecondary

@Composable
fun MediaItemCard(
    item: MediaItem,
    onClick: () -> Unit,
    onDelete: () -> Unit = {},
    onRename: (String) -> Unit = {},
    isDarkTheme: Boolean
) {
    // Сhoosing a color depending on the theme
    val surfaceColor = if (isDarkTheme) Color(0xFF1A1F3A) else MaterialTheme.colorScheme.surface
    val textColor = if (isDarkTheme) Color(0xFFF0F0F0) else MaterialTheme.colorScheme.onBackground
    val fabColor = if (isDarkTheme) Color(0xFF00D9FF) else MaterialTheme.colorScheme.primary

    var showMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(item.name) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with color depending on the media type
            Icon(
                imageVector = if (item.type == MediaType.MP4) Icons.Default.Movie else Icons.Default.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (item.type == MediaType.MP4) NeonPurple else fabColor
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = textColor,
                    maxLines = 1
                )
                Text(
                    text = item.type.name,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Меню",
                    tint = TextSecondary
                )
            }

            Log.d("MediaItemCard", "showMenu: $showMenu")
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(surfaceColor)
            ) {
                DropdownMenuItem(
                    text = { Text("Переименовать", color = textColor) },
                    onClick = {
                        showMenu = false
                        newName = item.name
                        showRenameDialog = true
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = fabColor)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Удалить", color = NeonPink) },
                    onClick = {
                        showMenu = false
                        onDelete()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = NeonPink)
                    }
                )
            }
        }
    }

    if (showRenameDialog) {
        RenameDialog(
            currentName = newName,
            onNameChange = { newName = it },
            onConfirm = {
                if (newName.isNotBlank()) {
                    onRename(newName)
                    showRenameDialog = false
                }
            },
            onDismiss = { showRenameDialog = false },
            isDarkTheme = isDarkTheme
        )
    }
}

// Dialog for renaming
@Composable
fun RenameDialog(
    currentName: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean
) {
    val fabColor = if (isDarkTheme) Color(0xFF00D9FF) else MaterialTheme.colorScheme.primary
    val surfaceColor = if (isDarkTheme) Color(0xFF1A1F3A) else MaterialTheme.colorScheme.surface
    val textColor = if (isDarkTheme) Color(0xFFF0F0F0) else MaterialTheme.colorScheme.onBackground

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(
                    color = surfaceColor,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Переименовать",
                color = textColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = currentName,
                onValueChange = onNameChange,
                label = { Text("Название", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = fabColor,
                    unfocusedBorderColor = TextSecondary,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    cursorColor = fabColor,
                    focusedLabelColor = fabColor,
                    unfocusedLabelColor = TextSecondary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text("Отмена", color = TextSecondary)
                }

                TextButton(
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .background(
                            color = fabColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text("Сохранить", color = fabColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
