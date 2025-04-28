package com.ben.looneyadventure.ui

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ben.looneyadventure.MusicManager
import com.ben.looneyadventure.R
import com.ben.looneyadventure.ui.theme.LooneysBirthdayAdventureTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Start looping intro music
        mediaPlayer = MediaPlayer.create(this, R.raw.intro_music).apply {
            isLooping = true
            start()
        }
        // Expose globally so popups can duck/restore volume
        MusicManager.backgroundPlayer = mediaPlayer

        setContent {
            LooneysBirthdayAdventureTheme {
                StartScreen {
                    // leave music playing, just navigate
                    startActivity(Intent(this@MainActivity, GameActivity::class.java))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // clear global ref & release once app closes
        MusicManager.backgroundPlayer = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

@Composable
fun StartScreen(onStartClick: () -> Unit) {
    val backgroundImages = listOf(
        R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3,
        R.drawable.bg_4, R.drawable.bg_5, R.drawable.bg_6,
        R.drawable.bg_7_v2, R.drawable.bg_8, R.drawable.bg_9
    )
    var currentIndex by remember { mutableStateOf(0) }
    // State for showing tutorial popup
    var showTutorial by remember { mutableStateOf(false) }
    // MediaPlayer for tutorial voice
    val context = androidx.compose.ui.platform.LocalContext.current
    var tutorialPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentIndex = (currentIndex + 1) % backgroundImages.size
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(
            targetState   = currentIndex,
            modifier      = Modifier.fillMaxSize(),
            animationSpec = tween(durationMillis = 1000) // 1s fade
        ) { idx ->
            Image(
                painter        = painterResource(id = backgroundImages[idx]),
                contentDescription = null,
                contentScale   = ContentScale.Crop,
                alignment      = Alignment.Center,
                modifier       = Modifier.fillMaxSize()
            )
        }

        Image(
            painter = painterResource(id = R.drawable.title),
            contentDescription = "Title",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp)
                .width(180.dp)
        )

        // Tutorial button placed above start button
        Image(
            painter = painterResource(id = R.drawable.tutorial_button),
            contentDescription = "Tutorial Button",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp) // Positioned above start button
                .clickable {
                    showTutorial = true
                    // Reduce background music volume when tutorial starts
                    MusicManager.backgroundPlayer?.setVolume(0.2f, 0.2f)
                }
                .width(280.dp)
                .height(100.dp),
            contentScale = ContentScale.FillBounds // Ensure image fills the bounds
        )

        Image(
            painter = painterResource(id = R.drawable.start_button),
            contentDescription = "Start Button",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .clickable { onStartClick() }
                .width(320.dp)
                .height(160.dp),
            contentScale = ContentScale.FillBounds // Ensure image fills the bounds
        )

        // Tutorial popup with Michael Scofield
        if (showTutorial) {
            Dialog(
                onDismissRequest = {
                    showTutorial = false
                    // Restore background music volume when tutorial closes
                    MusicManager.backgroundPlayer?.setVolume(1f, 1f)
                    // Release the media player
                    tutorialPlayer?.release()
                    tutorialPlayer = null
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tutorial_image),
                        contentDescription = "Tutorial",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Close button at bottom
                    Image(
                        painter = painterResource(id = R.drawable.btn_complete),
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(128.dp)
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                            .clickable {
                                showTutorial = false
                                // Restore background music volume
                                MusicManager.backgroundPlayer?.setVolume(1f, 1f)
                                // Release the media player
                                tutorialPlayer?.release()
                                tutorialPlayer = null
                            }
                    )
                }

                // Play tutorial voice when popup appears
                LaunchedEffect(showTutorial) {
                    tutorialPlayer?.release()
                    tutorialPlayer = MediaPlayer.create(context, R.raw.tutorial_voice_v2).apply {
                        setOnCompletionListener {
                            // Restore background music volume when voice ends
                            MusicManager.backgroundPlayer?.setVolume(1f, 1f)
                            release()
                            tutorialPlayer = null
                        }
                        start()
                    }
                }
            }
        }
    }
}
