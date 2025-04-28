package com.ben.looneyadventure.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ben.looneyadventure.R
import com.ben.looneyadventure.ui.theme.LooneysBirthdayAdventureTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LooneysBirthdayAdventureTheme {
                GameBoardWithPopup()
            }
        }
    }
}

@Composable
fun GameBoardWithPopup() {
    // 1) Your 16‑icon board
    val icons = listOf(
        R.drawable.ice_cream,     R.drawable.bowling,
        R.drawable.ramen,         R.drawable.locked_gift,
        R.drawable.question_mark, R.drawable.gaming,
        R.drawable.key,           R.drawable.locked_gift,
        R.drawable.puzzle,        R.drawable.question_mark,
        R.drawable.restaurant,    R.drawable.locked_gift,
        R.drawable.basket,        R.drawable.foot,
        R.drawable.mic,           R.drawable.locked_gift
    )

    // 2) Track completion
    val completed = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(icons.size) { add(false) }
        }
    }

    // 3) Popup indices for celebrities
    val iceCreamIndex   = icons.indexOf(R.drawable.ice_cream)
    val bowlingIndex    = icons.indexOf(R.drawable.bowling)
    val ramenIndex      = icons.indexOf(R.drawable.ramen)
    val gamingIndex     = icons.indexOf(R.drawable.gaming)
    val keyIndex        = icons.indexOf(R.drawable.key)
    val basketIndex     = icons.indexOf(R.drawable.basket)
    val restaurantIndex = icons.indexOf(R.drawable.restaurant)
    val micIndex        = icons.indexOf(R.drawable.mic)
    val puzzleIndex     = icons.indexOf(R.drawable.puzzle)
    val footIndex       = icons.indexOf(R.drawable.foot)
    // Find the first question mark (index 4) - different from the second one (index 9)
    val questionMark1Index = 4 // First question mark at row 2, column 1
    // Find the second question mark (index 9)
    val questionMark2Index = 9 // Second question mark at row 3, column 2
    //    First row gift index:
    val firstGiftIndex = 3
    //    Second row gift index:
    val secondGiftIndex = 7
    //    Third row gift index:
    val thirdGiftIndex = 11
    //    Last row gift index:
    val lastGiftIndex = 15
    val popupIndices = setOf(
        iceCreamIndex,
        bowlingIndex,
        ramenIndex,
        gamingIndex,
        keyIndex,
        basketIndex,
        restaurantIndex,
        micIndex,
        puzzleIndex,
        footIndex,
        questionMark1Index,
        questionMark2Index,
        firstGiftIndex,
        secondGiftIndex,
        thirdGiftIndex,
        lastGiftIndex
    )

    // 4) Celebrity maps
    val characterMap = mapOf(
        iceCreamIndex   to R.drawable.ice_cream_celebrity,
        bowlingIndex    to R.drawable.bowling_celebrity,
        ramenIndex      to R.drawable.ramen_celebrity,
        gamingIndex     to R.drawable.gamming_celebrity,
        keyIndex        to R.drawable.key_celebrity,
        basketIndex     to R.drawable.basket_celebrity,
        restaurantIndex to R.drawable.restaurant_celebrity,
        micIndex        to R.drawable.mic_celebrity,
        puzzleIndex     to R.drawable.puzzle_celebrity,
        footIndex       to R.drawable.foot_celebrity,
        questionMark1Index to R.drawable.question_mark_celebrity1,
        questionMark2Index to R.drawable.question_mark_celebrity2,
        firstGiftIndex  to R.drawable.book,
        secondGiftIndex to R.drawable.sloth,
        thirdGiftIndex  to R.drawable.camera_v2,
        lastGiftIndex   to R.drawable.bracelet
    )
    val voiceMap = mapOf(
        iceCreamIndex   to R.raw.ice_cream_voice,
        bowlingIndex    to R.raw.bowling_voice,
        ramenIndex      to R.raw.ramen_voice,
        gamingIndex     to R.raw.gamming_voice,
        keyIndex        to R.raw.key_voice_v2,
        basketIndex     to R.raw.basket_voice_v3,
        restaurantIndex to R.raw.restaurant_voice_v3,
        micIndex        to R.raw.mic_voice_v2,
        puzzleIndex     to R.raw.puzzle_voice,
        footIndex       to R.raw.foot_voice_v2,
        questionMark1Index to R.raw.question_mark_voice1,
        questionMark2Index to R.raw.question_mark_voice2_v2,
        firstGiftIndex  to R.raw.book_voice,
        secondGiftIndex to R.raw.sloth_voice_v2,
        thirdGiftIndex  to R.raw.camera_gift_voice,
        lastGiftIndex   to R.raw.bracelet_voice_v2
    )

    // Description map for each icon
    val descriptionMap = mapOf(
        iceCreamIndex   to "Ice Cream Adventure",
        bowlingIndex    to "Bowling Challenge",
        ramenIndex      to "Ramen Experience",
        gamingIndex     to "Gaming Time",
        keyIndex        to "Secret Location",
        basketIndex     to "Picnic Adventure",
        restaurantIndex to "Romantic Dinner",
        micIndex        to "Stand Up Show",
        puzzleIndex     to "Puzzle Challenge",
        footIndex       to "Walking Challenge",
        questionMark1Index to "Mystery Adventure #1",
        questionMark2Index to "Mystery Adventure #2",
        firstGiftIndex  to "Book Gift",
        secondGiftIndex to "Sloth Puzzle Gift",
        thirdGiftIndex  to "Camera Gift",
        lastGiftIndex   to "Bracelet Gift"
    )

    // 5) New unlocked gift image
    val unlockedGiftRes = R.drawable.unlocked_gift
    //    Book image for gift popup:
    val bookRes = R.drawable.book

    //    Second row gift image (sloth puzzle):
    val slothRes = R.drawable.sloth

    //    Third row gift index and its image:
    val cameraRes = R.drawable.camera_v2

    //    Last row gift index and its image:
    val braceletRes = R.drawable.bracelet

    // popup states
    var showTaskPopup by remember { mutableStateOf(false) }
    var showGiftPopup by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }

    Box(Modifier.fillMaxSize()) {
        // background
        Image(
            painter = painterResource(R.drawable.board_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // grid
        Column(
            Modifier
                .align(Alignment.Center)
                .offset(y = 40.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement  = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0 until 4) {
                val base = row * 4
                // check if first three in this row are done
                val rowComplete = completed[base] &&
                        completed[base + 1] &&
                        completed[base + 2]

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment   = Alignment.CenterVertically
                ) {
                    for (col in 0 until 4) {
                        val idx = base + col
                        // decide which image to show
                        val res = if (col == 3) {
                            if (rowComplete) unlockedGiftRes else R.drawable.locked_gift
                        } else {
                            icons[idx]
                        }

                        // Special case: If this is a gift column and the row is complete,
                        // we want to identify which gift index this is
                        val isUnlockedGift = col == 3 && rowComplete
                        val giftIndex = if (isUnlockedGift) {
                            when (row) {
                                0 -> firstGiftIndex
                                1 -> secondGiftIndex
                                2 -> thirdGiftIndex
                                else -> lastGiftIndex
                            }
                        } else {
                            -1
                        }

                        // enable tap for:
                        // - celeb icons but not when in gift column (col==3)
                        // - normal icons to mark complete, but not when in gift column
                        // - gifts ONLY when their row is complete
                        val enabled = if (col == 3) {
                            // For gift column, only enable if row is complete
                            rowComplete
                        } else {
                            // For non-gift columns, enable if it's in popup indices or for regular marking
                            true
                        }

                        Box(
                            Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clickable(enabled = enabled) {
                                    when {
                                        // Use TaskPopup for any icon that's in popupIndices AND has voice/image in maps
                                        idx in popupIndices && col != 3 -> {
                                            selectedIndex = idx
                                            showTaskPopup = true
                                        }
                                        isUnlockedGift -> {
                                            // All gifts now have voice functionality, use TaskPopup for them
                                            selectedIndex = giftIndex
                                            showTaskPopup = true
                                        }
                                        else -> {
                                            completed[idx] = true
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(res),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (completed[idx]) {
                                Box(
                                    Modifier
                                        .matchParentSize()
                                        .background(Color.Black.copy(alpha = 0.6f))
                                )
                            }
                        }
                    }
                }
            }
        }

        // celebrity popup
        if (showTaskPopup) {
            TaskPopup(
                characterRes = characterMap.getValue(selectedIndex),
                voiceRes     = voiceMap.getValue(selectedIndex),
                description  = descriptionMap.getValue(selectedIndex),
                onDismiss    = { showTaskPopup = false },
                onComplete   = {
                    completed[selectedIndex] = true
                    showTaskPopup = false
                }
            )
        }

        // All gifts now have voice functionality, so this section might not be needed anymore
        // Keeping it as a fallback for any unforeseen cases
        if (showGiftPopup) {
            val imageRes = when (selectedIndex) {
                thirdGiftIndex -> cameraRes   // Third row gift (now uses TaskPopup)
                else -> bookRes               // Default fallback
            }

            // Get the appropriate description for this gift
            val description = descriptionMap.getValue(selectedIndex)

            // Determine which row this gift belongs to so we can mark the correct cells as completed
            val rowForThisGift = when (selectedIndex) {
                firstGiftIndex -> 0
                secondGiftIndex -> 1
                thirdGiftIndex -> 2
                else -> 3
            }
            val baseIndex = rowForThisGift * 4

            Dialog(
                onDismissRequest = {
                    // Mark all cells in this row as completed
                    for (i in 0 until 4) {
                        completed[baseIndex + i] = true
                    }
                    showGiftPopup = false
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                ) {
                    Image(
                        painter = painterResource(imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.9f)
                            .align(Alignment.TopCenter)
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

                    Image(
                        painter = painterResource(R.drawable.btn_complete),
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(128.dp)
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                            .clickable {
                                // Mark all cells in this row as completed
                                for (i in 0 until 4) {
                                    completed[baseIndex + i] = true
                                }
                                showGiftPopup = false
                            }
                    )
                }
            }
        }
    }
}
