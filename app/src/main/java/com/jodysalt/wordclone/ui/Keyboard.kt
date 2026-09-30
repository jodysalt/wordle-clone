package com.jodysalt.wordclone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jodysalt.wordclone.game.LetterState

private val LetterRows = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")

@Composable
fun Keyboard(
    letterStates: Map<Char, LetterState>,
    onLetter: (Char) -> Unit,
    onEnter: () -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        LetterRows.forEachIndexed { index, letters ->
            val isLastRow = index == LetterRows.lastIndex
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            ) {
                if (isLastRow) Key("Enter", null, Modifier.weight(1.5f), onEnter)
                letters.forEach { c ->
                    val fill = letterColor(letterStates[c] ?: LetterState.UNKNOWN)
                    Key(c.uppercase(), fill, Modifier.weight(1f)) { onLetter(c) }
                }
                if (isLastRow) Key("⌫", null, Modifier.weight(1.5f), onBackspace)
            }
        }
    }
}

@Composable
private fun Key(label: String, fill: Color?, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(fill ?: MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = if (label.length > 1) 12.sp else 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (fill != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
