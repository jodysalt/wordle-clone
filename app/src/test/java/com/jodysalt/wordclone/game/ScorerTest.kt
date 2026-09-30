package com.jodysalt.wordclone.game

import com.jodysalt.wordclone.game.LetterState.ABSENT
import com.jodysalt.wordclone.game.LetterState.CORRECT
import com.jodysalt.wordclone.game.LetterState.PRESENT
import org.junit.Assert.assertEquals
import org.junit.Test

class ScorerTest {
    @Test
    fun allCorrect() {
        assertEquals(List(5) { CORRECT }, Scorer.score("crane", "crane"))
    }

    @Test
    fun allAbsent() {
        assertEquals(List(5) { ABSENT }, Scorer.score("dumpy", "crane"))
    }

    @Test
    fun mixed() {
        assertEquals(
            listOf(CORRECT, PRESENT, ABSENT, ABSENT, CORRECT),
            Scorer.score("caste", "crane"),
        )
    }

    @Test
    fun repeatedLettersClaimCorrectBeforePresent() {
        assertEquals(
            listOf(PRESENT, PRESENT, CORRECT, CORRECT, ABSENT),
            Scorer.score("babes", "abbey"),
        )
    }

    @Test
    fun extraRepeatedLettersScoreAbsent() {
        assertEquals(
            listOf(ABSENT, ABSENT, PRESENT, ABSENT, CORRECT),
            Scorer.score("eerie", "crane"),
        )
    }
}
