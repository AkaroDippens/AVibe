package com.example.avibe.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.avibe.ui.theme.NeonPurple

// Media add dialog (MP3 or MP4)
@Composable
fun AddMediaDialog(
    onMp3: () -> Unit,
    onMp4: () -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean
) {
    // Сhoosing a color depending on the theme
    val surfaceColor = if (isDarkTheme) Color(0xFF1A1F3A) else MaterialTheme.colorScheme.surface
    val textColor = if (isDarkTheme) Color(0xFFF0F0F0) else MaterialTheme.colorScheme.onBackground
    val textSecondary = if (isDarkTheme) Color(0xFFB0B0B0) else MaterialTheme.colorScheme.onSurface
    val fabColor = if (isDarkTheme) Color(0xFF00D9FF) else MaterialTheme.colorScheme.primary

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
                "Добавить файл",
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Выберите тип файла",
                color = textSecondary,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(24.dp))

            NeonButton(
                text = "♪ MP3 (Аудио)",
                onClick = onMp3,
                neonColor = fabColor,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            NeonButton(
                text = "▶ MP4 (Видео)",
                onClick = onMp4,
                neonColor = NeonPurple,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = textSecondary
                )
            ) {
                Text("Отмена", fontSize = 14.sp)
            }
        }
    }
}

// Neon button for media add dialog
@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    neonColor: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = neonColor.copy(alpha = 0.2f),
            contentColor = neonColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}
