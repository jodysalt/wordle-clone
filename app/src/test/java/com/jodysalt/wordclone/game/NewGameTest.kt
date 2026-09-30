package com.jodysalt.wordclone.game

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NewGameTest {
    private val answers = listOf("crane", "tiger", "caste", "dumpy")
    private val valid = answers.toSet()

    private fun Game.guess(word: String): Game =
        word.fold(this) { g, c -> g.typeLetter(c) }.submit(valid)

    @Test
    fun newGameResetsAFinishedGame() {
        val finished = Game(answer = "crane").guess("crane")
        assertEquals(GameStatus.WON, finished.status)

        val fresh = Game.newGame(answers, Random(42))
        assertTrue(fresh.guesses.isEmpty())
        assertEquals("", fresh.currentInput)
        assertEquals(GameStatus.IN_PROGRESS, fresh.status)
        assertNull(fresh.error)
        assertTrue(fresh.answer in answers)
    }

    @Test
    fun sameSeedGivesSameAnswer() {
        assertEquals(
            Game.newGame(answers, Random(42)).answer,
            Game.newGame(answers, Random(42)).answer,
        )
    }
}
