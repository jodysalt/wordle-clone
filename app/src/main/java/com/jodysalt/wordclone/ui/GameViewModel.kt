package com.jodysalt.wordclone.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.jodysalt.wordclone.game.Game
import com.jodysalt.wordclone.game.WordList

/** Loads the bundled word lists once and drives the pure [Game]; survives rotation only. */
class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val wordList: WordList = WordList.fromText(
        answersText = readAsset(application, "answers.txt"),
        validText = readAsset(application, "valid_guesses.txt"),
    )

    var game: Game by mutableStateOf(Game.newGame(wordList.answers))
        private set

    fun onLetter(c: Char) {
        game = game.typeLetter(c)
    }

    fun onBackspace() {
        game = game.deleteLetter()
    }

    fun onEnter() {
        game = game.submit(wordList.validGuesses)
    }

    fun onNewGame() {
        game = Game.newGame(wordList.answers)
    }

    private companion object {
        fun readAsset(application: Application, name: String): String =
            application.assets.open(name).bufferedReader().use { it.readText() }
    }
}
