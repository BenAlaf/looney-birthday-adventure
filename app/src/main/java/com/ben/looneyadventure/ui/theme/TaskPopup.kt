package com.ben.looneyadventure.ui

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ben.looneyadventure.MusicManager
import com.ben.looneyadventure.R                 // ← your app's R

@Composable
fun TaskPopup(
    characterRes: Int,
    voiceRes:     Int,
    description:  String,
    onDismiss:    () -> Unit,
    onComplete:   () -> Unit
) {
    val context = LocalContext.current
    var voicePlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    Dialog(
        onDismissRequest = {
            MusicManager.backgroundPlayer?.setVolume(1f, 1f)
            voicePlayer?.release()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
        ) {
            // per-icon character image
            Image(
                painter = painterResource(id = characterRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Description text at the top with small semi-transparent background
            Box(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = description,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }

            // big buttons
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // voice button
                Image(
                    painter = painterResource(id = R.drawable.btn_voice),
                    contentDescription = "Play Voice",
                    modifier = Modifier
                        .size(128.dp)
                        .clickable {
                            MusicManager.backgroundPlayer?.setVolume(0.2f, 0.2f)
                            voicePlayer?.release()
                            voicePlayer = MediaPlayer.create(context, voiceRes).apply {
                                setOnCompletionListener {
                                    MusicManager.backgroundPlayer?.setVolume(1f, 1f)
                                    release()
                                }
                                start()
                            }
                        }
                )
                // complete button
                Image(
                    painter = painterResource(id = R.drawable.btn_complete),
                    contentDescription = "Mark Complete",
                    modifier = Modifier
                        .size(128.dp)
                        .clickable {
                            MusicManager.backgroundPlayer?.setVolume(1f, 1f)
                            voicePlayer?.release()
                            onComplete()
                        }
                )
            }
        }
    }
}
