package com.jodysalt.wordclone.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GameTest {
    private val valid = setOf("crane", "dumpy", "caste", "tiger")

    private fun Game.type(word: String): Game = word.fold(this) { g, c -> g.typeLetter(c) }

    private fun Game.guess(word: String): Game = type(word).submit(valid)

    @Test
    fun tooShortGuessIsRejected() {
        val game = Game(answer = "crane").type("cra").submit(valid)
        assertEquals(GuessError.TOO_SHORT, game.error)
        assertEquals(0, game.guesses.size)
        assertEquals("cra", game.currentInput)
    }

    @Test
    fun unknownWordIsRejected() {
        val game = Game(answer = "crane").type("zzzzz").submit(valid)
        assertEquals(GuessError.NOT_A_WORD, game.error)
        assertEquals(0, game.guesses.size)
    }

    @Test
    fun typingClearsErrorAndCapsInputAtFiveLetters() {
        val game = Game(answer = "crane").type("cr").submit(valid).type("ANExy")
        assertNull(game.error)
        assertEquals("crane", game.currentInput)
    }

    @Test
    fun correctGuessWinsAndFreezesGame() {
        val won = Game(answer = "crane").guess("dumpy").guess("crane")
        assertEquals(GameStatus.WON, won.status)
        assertEquals(listOf("dumpy", "crane"), won.guesses)
        assertEquals(won, won.typeLetter('t'))
        assertEquals(won, won.type("tiger").submit(valid))
        assertEquals(won, won.deleteLetter())
    }

    @Test
    fun sixWrongGuessesLose() {
        var game = Game(answer = "crane")
        repeat(Game.MAX_GUESSES) { game = game.guess("dumpy") }
        assertEquals(GameStatus.LOST, game.status)
        assertEquals(Game.MAX_GUESSES, game.guesses.size)
        assertEquals("crane", game.answer)
        assertEquals(game, game.guess("crane"))
    }

    @Test
    fun scoredGuessesUseScorer() {
        val game = Game(answer = "crane").guess("caste")
        assertEquals(listOf(Scorer.score("caste", "crane")), game.scoredGuesses())
    }
}
