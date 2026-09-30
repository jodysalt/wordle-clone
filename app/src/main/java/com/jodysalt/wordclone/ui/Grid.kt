package com.jodysalt.wordclone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jodysalt.wordclone.game.Game
import com.jodysalt.wordclone.game.LetterState

private val Green = Color(0xFF6AAA64)
private val Yellow = Color(0xFFC9B458)
private val Grey = Color(0xFF787C7E)

/** Tile and key fill for a scored letter; null means "use the default surface". */
fun letterColor(state: LetterState): Color? = when (state) {
    LetterState.CORRECT -> Green
    LetterState.PRESENT -> Yellow
    LetterState.ABSENT -> Grey
    LetterState.UNKNOWN -> null
}

@Composable
fun Grid(game: Game, modifier: Modifier = Modifier) {
    val scored = game.scoredGuesses()
    Column(
        modifier = modifier.widthIn(max = 340.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        for (row in 0 until Game.MAX_GUESSES) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (col in 0 until Game.WORD_LENGTH) {
                    val tileModifier = Modifier.weight(1f)
                    when {
                        row < game.guesses.size ->
                            Tile(game.guesses[row][col], letterColor(scored[row][col]), tileModifier)
                        row == game.guesses.size ->
                            Tile(game.currentInput.getOrNull(col), null, tileModifier)
                        else -> Tile(null, null, tileModifier)
                    }
                }
            }
        }
    }
}

@Composable
private fun Tile(letter: Char?, fill: Color?, modifier: Modifier) {
    val outline = if (letter != null) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val base = modifier.aspectRatio(1f)
    val styled = if (fill != null) base.background(fill) else base.border(2.dp, outline)
    Box(modifier = styled, contentAlignment = Alignment.Center) {
        if (letter != null) {
            Text(
                text = letter.uppercase(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (fill != null) Color.White else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
