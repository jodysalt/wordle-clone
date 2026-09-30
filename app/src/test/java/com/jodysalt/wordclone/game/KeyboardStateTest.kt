package com.jodysalt.wordclone.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class KeyboardStateTest {
    @Test
    fun presentThenCorrectStaysCorrect() {
        // crane: 'a' is PRESENT in arose, then CORRECT in stair
        val game = Game(answer = "crane", guesses = listOf("arose", "stair"))
        assertEquals(LetterState.PRESENT, Scorer.score("arose", "crane")[0])
        assertEquals(LetterState.CORRECT, game.keyboardState()['a'])
    }

    @Test
    fun correctThenAbsentExtraCopyStaysCorrect() {
        // crane: 'e' is CORRECT in arose, then the extra copies in eerie score ABSENT
        val game = Game(answer = "crane", guesses = listOf("arose", "eerie"))
        assertEquals(LetterState.ABSENT, Scorer.score("eerie", "crane")[0])
        assertEquals(LetterState.CORRECT, game.keyboardState()['e'])
    }

    @Test
    fun presentThenAbsentStaysPresent() {
        // crane: 'e' is PRESENT in eaten, then PRESENT then ABSENT (extra copy) in sheep
        val game = Game(answer = "crane", guesses = listOf("eaten", "sheep"))
        assertEquals(LetterState.ABSENT, Scorer.score("sheep", "crane")[3])
        val keyboard = game.keyboardState()
        assertEquals(LetterState.PRESENT, keyboard['e'])
        assertEquals(LetterState.ABSENT, keyboard['s'])
        assertNull(keyboard['z'])
    }
}
