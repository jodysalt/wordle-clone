package com.jodysalt.wordclone.game

import kotlin.random.Random

enum class GameStatus { IN_PROGRESS, WON, LOST }

enum class GuessError { TOO_SHORT, NOT_A_WORD }

/** Immutable game state; every action returns a new [Game]. */
data class Game(
    val answer: String,
    val guesses: List<String> = emptyList(),
    val currentInput: String = "",
    val status: GameStatus = GameStatus.IN_PROGRESS,
    val error: GuessError? = null,
) {
    fun typeLetter(c: Char): Game {
        if (status != GameStatus.IN_PROGRESS) return this
        val letter = c.lowercaseChar()
        if (letter !in 'a'..'z' || currentInput.length >= WORD_LENGTH) return this
        return copy(currentInput = currentInput + letter, error = null)
    }

    fun deleteLetter(): Game {
        if (status != GameStatus.IN_PROGRESS) return this
        return copy(currentInput = currentInput.dropLast(1), error = null)
    }

    fun submit(validGuesses: Set<String>): Game {
        if (status != GameStatus.IN_PROGRESS) return this
        if (currentInput.length < WORD_LENGTH) return copy(error = GuessError.TOO_SHORT)
        if (currentInput !in validGuesses) return copy(error = GuessError.NOT_A_WORD)
        val newGuesses = guesses + currentInput
        val newStatus = when {
            currentInput == answer -> GameStatus.WON
            newGuesses.size >= MAX_GUESSES -> GameStatus.LOST
            else -> GameStatus.IN_PROGRESS
        }
        return copy(guesses = newGuesses, currentInput = "", status = newStatus, error = null)
    }

    fun scoredGuesses(): List<List<LetterState>> = guesses.map { Scorer.score(it, answer) }

    companion object {
        const val WORD_LENGTH = 5
        const val MAX_GUESSES = 6

        fun newGame(answers: List<String>, random: Random = Random.Default): Game =
            Game(answer = answers.random(random))
    }
}
