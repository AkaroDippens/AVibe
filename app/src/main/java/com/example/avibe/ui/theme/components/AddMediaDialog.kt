package com.example.avibe.ui.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Media dialog for adding MP3 and MP4 files
@Composable
fun AddMediaDialog(
    onMp3: () -> Unit,
    onMp4: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить файл") },
        text = { Text("Выберите тип файла") },
        confirmButton = {
            Column {
                Button(onClick = onMp3, modifier = Modifier.fillMaxWidth()) {
                    Text("MP3 (Аудио)")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onMp4, modifier = Modifier.fillMaxWidth()) {
                    Text("MP4 (Видео)")
                }
            }
        }
    )
}