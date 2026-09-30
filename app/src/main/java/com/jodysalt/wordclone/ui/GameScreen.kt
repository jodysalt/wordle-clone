package com.jodysalt.wordclone.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jodysalt.wordclone.game.Game
import com.jodysalt.wordclone.game.GameStatus
import com.jodysalt.wordclone.game.GuessError
import com.jodysalt.wordclone.game.keyboardState

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val game = viewModel.game
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Grid(game)
            Text(text = message(game), style = MaterialTheme.typography.titleMedium)
            Button(onClick = viewModel::onNewGame) {
                Text("New game")
            }
            Keyboard(
                letterStates = game.keyboardState(),
                onLetter = viewModel::onLetter,
                onEnter = viewModel::onEnter,
                onBackspace = viewModel::onBackspace,
            )
        }
    }
}

private fun message(game: Game): String = when (game.status) {
    GameStatus.WON -> "You won! The word was ${game.answer.uppercase()}"
    GameStatus.LOST -> "The word was ${game.answer.uppercase()}"
    GameStatus.IN_PROGRESS -> when (game.error) {
        GuessError.TOO_SHORT -> "Not enough letters"
        GuessError.NOT_A_WORD -> "Not in word list"
        null -> ""
    }
}
